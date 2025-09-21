package Dragon.services.VoiceChat;

import Dragon.models.player.Player;
import Dragon.models.map.Zone;
import Dragon.utils.Logger;
import com.girlkun.network.io.Message;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class VoiceChatService {

    private static VoiceChatService i;

    // Testing flag: when true, the sender also receives their own voice packet
    private boolean echoToSender = false; // test

    private static final int MAX_BYTES_PER_SECOND = 120_000; // generous cap for PCM16 mono ~88 KB/s
    private static final ConcurrentHashMap<Long, RateState> RATE = new ConcurrentHashMap<>();
    // Per-sender sequence for v2 packets
    private static final ConcurrentHashMap<Long, Integer> SEQ = new ConcurrentHashMap<>();

    private static class RateState {

        long windowStartMs;
        int bytes;
    }

    public static VoiceChatService gI() {
        if (i == null) {
            i = new VoiceChatService();
        }
        return i;
    }

    /**
     * Handle incoming voice chat message from client
     */
    public void handleVoiceMessage(Player player, Message msg) {
        try {
            if (player == null || player.zone == null) {
                return;
            }

            // Read voice data from message (client sends sbyte[] which we need to convert
            // to byte[])
            // Read and discard client-sent identifiers; we will derive authoritative values
            // from server state
            int playerId = msg.reader().readInt(); // ignored
            int mapId = msg.reader().readInt(); // ignored
            int zoneId = msg.reader().readInt(); // ignored
            int dataLength = msg.reader().readInt();

            // Validate dataLength early
            if (dataLength <= 0 || dataLength > 4096) {
                Logger.error("Invalid voice data length: " + dataLength);
                return;
            }

            // Read raw bytes from client; keep as-is to avoid extra allocations
            byte[] payload = new byte[dataLength];
            msg.reader().read(payload);

            // Rate limiting per player id
            long now = System.currentTimeMillis();
            long pid = player.id;
            RateState st = RATE.computeIfAbsent(pid, k -> new RateState());
            synchronized (st) {
                if (now - st.windowStartMs >= 1000) {
                    st.windowStartMs = now;
                    st.bytes = 0;
                }
                if (st.bytes + dataLength > MAX_BYTES_PER_SECOND) {
                    // Drop this packet silently to protect server bandwidth
                    if ((now / 2000) % 5 == 0) {
                        Logger.log(Logger.YELLOW,
                                "[VOICE] Throttling player " + player.name + ": exceeded per-second limit\n");
                    }
                    return;
                }
                st.bytes += dataLength;
            }
            Logger.log(Logger.CYAN, "Voice from " + player.name + " in zone " + player.zone.zoneId
                    + " (data length: " + dataLength + " bytes)\n");

            broadcastVoiceToZone(player, payload);

        } catch (IOException e) {
            Logger.logException(VoiceChatService.class, e);
        }
    }

    /**
     * Handle incoming voice chat v2 message from client (with seq & timestamp)
     */
    public void handleVoiceMessageV2(Player player, Message msg) {
        try {
            if (player == null || player.zone == null) {
                return;
            }

            // v2 payload: playerId, mapId, zoneId, seq, timestampMs, dataLength, payload
            msg.reader().readInt(); // client-sent playerId (ignored)
            msg.reader().readInt(); // mapId (ignored)
            msg.reader().readInt(); // zoneId (ignored)
            int seq = msg.reader().readInt();
            int ts = msg.reader().readInt();
            int dataLength = msg.reader().readInt();

            if (dataLength <= 0 || dataLength > 4096) {
                Logger.error("Invalid voice v2 data length: " + dataLength);
                return;
            }

            byte[] payload = new byte[dataLength];
            msg.reader().read(payload);

            // Rate limit same as v1
            long now = System.currentTimeMillis();
            long pid = player.id;
            RateState st = RATE.computeIfAbsent(pid, k -> new RateState());
            synchronized (st) {
                if (now - st.windowStartMs >= 1000) {
                    st.windowStartMs = now;
                    st.bytes = 0;
                }
                if (st.bytes + dataLength > MAX_BYTES_PER_SECOND) {
                    if ((now / 2000) % 5 == 0) {
                        Logger.log(Logger.YELLOW,
                                "[VOICE v2] Throttling player " + player.name + ": exceeded per-second limit\n");
                    }
                    return;
                }
                st.bytes += dataLength;
            }

            Logger.log(Logger.CYAN, "Voice v2 from " + player.name + " in zone " + player.zone.zoneId
                    + " (seq=" + seq + ", ts=" + ts + ", len=" + dataLength + ")\n");

            // Broadcast as usual (method will generate both v1 & v2 out messages)
            broadcastVoiceToZone(player, payload);
        } catch (IOException e) {
            Logger.logException(VoiceChatService.class, e);
        }
    }

    /**
     * Broadcast voice data to all players in the same zone
     */
    private void broadcastVoiceToZone(Player sender, byte[] payload) {
        try {
            Zone zone = sender.zone;
            if (zone == null) {
                return;
            }

            Message voiceMsg = new Message(72);
            voiceMsg.writer().writeInt((int) sender.id);
            voiceMsg.writer().writeInt(zone.map.mapId);
            voiceMsg.writer().writeInt(zone.zoneId);
            voiceMsg.writer().writeInt(payload.length);
            voiceMsg.writer().write(payload);

            int seq = SEQ.merge(sender.id, 1, (oldV, one) -> oldV + 1);
            long ts = System.currentTimeMillis();
            Message voiceMsgV2 = new Message(174);
            voiceMsgV2.writer().writeInt((int) sender.id);
            voiceMsgV2.writer().writeInt(zone.map.mapId);
            voiceMsgV2.writer().writeInt(zone.zoneId);
            voiceMsgV2.writer().writeInt(seq);
            voiceMsgV2.writer().writeInt((int) (ts & 0x7fffffff));
            voiceMsgV2.writer().writeInt(payload.length);
            voiceMsgV2.writer().write(payload);

            List<Player> playersInZone = zone.getPlayers();
            for (Player targetPlayer : playersInZone) {
                if (targetPlayer != null && targetPlayer.id != sender.id) {
                    try {
                        targetPlayer.sendMessage(voiceMsg);
                        targetPlayer.sendMessage(voiceMsgV2);
                    } catch (Exception e) {
                        Logger.error("Error sending voice to player " + targetPlayer.name + ": " + e.getMessage());
                    }
                }
            }

            if (echoToSender) {
                try {
                    sender.sendMessage(voiceMsg);
                    sender.sendMessage(voiceMsgV2);
                } catch (Exception e) {
                    Logger.error("Error echoing voice back to sender " + sender.name + ": " + e.getMessage());
                }
            }

            voiceMsg.cleanup();
            voiceMsgV2.cleanup();

        } catch (Exception e) {
            Logger.logException(VoiceChatService.class, e);
        }
    }

    public void sendVoiceZoneInfo(Player player, Zone zone) {
        try {
            Message msg = new Message(173);
            msg.writer().writeInt(zone.zoneId);
            msg.writer().writeInt(zone.getPlayers().size());
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(VoiceChatService.class, e);
        }
    }

    public void onPlayerChangeZone(Player player, Zone oldZone, Zone newZone) {
        if (player != null) {
            Logger.log(Logger.CYAN, "Player " + player.name + " voice zone changed from "
                    + (oldZone != null ? oldZone.zoneId : "null") + " to "
                    + (newZone != null ? newZone.zoneId : "null") + "\n");
        }
    }

    public String getVoiceStats() {
        return "Voice Chat Service Active";
    }
}
