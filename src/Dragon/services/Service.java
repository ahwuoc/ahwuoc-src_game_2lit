package Dragon.services;

import Dragon.Bot.*;
import com.girlkun.database.GirlkunDB;
import Dragon.consts.ConstNpc;
import Dragon.consts.ConstPlayer;
import Dragon.jdbc.daos.PlayerDAO;
import Dragon.models.boss.BossID;
import com.girlkun.network.server.GirlkunSessionManager;
import Dragon.utils.FileIO;
import Dragon.data.DataGame;
import Dragon.jdbc.daos.GodGK;
import Dragon.models.boss.Boss;
import Dragon.models.boss.BossManager;
import Dragon.models.boss.list_boss.doanh_trai.TrungUyTrang;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.List;

import Dragon.models.item.Item;
import Dragon.models.map.ItemMap;
import Dragon.models.mob.Mob;
import Dragon.models.npc.specialnpc.MabuEgg;
import Dragon.models.player.Pet;
import Dragon.models.item.Item.ItemOption;
import Dragon.models.map.Zone;
import Dragon.models.matches.PVP;
import Dragon.models.matches.PVPManager;
import Dragon.models.matches.TOP;
import Dragon.models.npc.specialnpc.BillEgg;
import Dragon.models.player.Player;
import Dragon.models.shop.ItemShop;
import Dragon.models.shop.Shop;
import Dragon.server.io.MySession;
import Dragon.models.skill.Skill;
import com.girlkun.network.io.Message;
import com.girlkun.network.session.ISession;
import com.girlkun.network.session.Session;
import com.girlkun.result.GirlkunResultSet;
import Dragon.server.Client;
import Dragon.server.Controller;
import Dragon.server.Maintenance;
import Dragon.server.Manager;
import Dragon.server.ServerManager;
import static Dragon.services.PetService.Thu_TrieuHoi;
import Dragon.services.func.ChangeMapService;
import Dragon.services.func.Input;
import Dragon.thuongnhanthanbi.DungeonInstance;

import static Dragon.services.func.SummonDragon.DRAGON_SHENRON;
import Dragon.utils.Logger;
import Dragon.utils.TimeUtil;
import Dragon.utils.Util;
import Dragon.De2.Thu_TrieuHoi;

import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

public class Service {

    private static Service instance;
    public long lasttimechatbanv = 0;
    public long lasttimechatmuav = 0;

    public static Service gI() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }

    public static Service getInstance() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }

    public void managePlayer(Player player, Message _msg) {
        if (!player.getSession().isAdmin) {
            Service.gI().sendThongBao(player, "Ch? d�nh cho Admin");
            return;
        }
        if (_msg != null) {
            try {
                String name = _msg.readUTF();
                System.out.println("Check Player : " + name);
                Player pl = Client.gI().getPlayer(name);
                if (pl != null) {
                    int sl = InventoryServiceNew.gI().findItemBag(pl, (short) 457) == null ? 0
                            : InventoryServiceNew.gI().findItemBag(pl, (short) 457).quantity;
                    NpcService.gI().createMenuConMeo(player, ConstNpc.QUANLYTK, 21587, "|7|[ MANAGER ACCOUNT ]"
                            + "\n|7|Player : " + pl.name + (pl.vip > 0 && pl.vip < 4 ? " [VIP" + pl.vip + "] "
                                    : pl.vip == 4 ? " [VIP 4]"
                                            : pl.vip == 5 ? " [VIP 5]"
                                                    : "")
                            + "\nAccount ID : " + pl.id + " | " + "IP Connect : " + pl.getSession().ipAddress + " | "
                            + "Version Mod : " + pl.getSession().version
                            + "\nActive : " + (pl.getSession().actived == true ? "On" : "Off")
                            + "\nTh??i V�ng : " + Util.format(sl)
                            + "\nH?ng Ng??c : " + Util.format(pl.inventory.ruby)
                            + "\nT?ng N?p : " + Util.format(pl.getSession().tongnap)
                            + "\nVN?? : " + Util.format(pl.getSession().vnd)
                            + "\n|7|[ DRAGONBALL Kamui ]",
                            new String[] { "???I T�N", "BAN", "KICK", "ACTIVE", "??? T?", "DANH HI?U", "NHI?M V?",
                                    "GIAM GI?", "MAKE ADMIN", "THU ITEM" },
                            pl);
                } else {
                    Service.gI().sendThongBao(player, "Ng???i ch?i kh�ng t?n t?i ho?c ?ang offline");
                }
            } catch (IOException e) {
                System.out.println("L?i Manager Player");
            }
        } else {
            System.out.println("Manager Player msg null");
        }
    }

    public void removeEff(Player pl, int... id) {
        try {
            Message msg = new Message(-128);
            if (id.length > 0) {
                msg.writer().writeByte(1);
            } else {
                msg.writer().writeByte(2);
            }
            msg.writer().writeInt((int) pl.id);
            if (id.length > 0) {
                msg.writer().writeShort(id[0]);
            }
            sendMessAllPlayerInMap(pl.zone, msg);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void SendImgSkill9(short SkillId, int IdAnhSKill) {
        Message msg = new Message(62);
        try {
            msg.writeShort(SkillId);
            msg.writeByte(1);
            msg.writeByte(IdAnhSKill);
            Service.getInstance().sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void addEffectChar(Player pl, int id, int layer, int loop, int loopcount, int stand) {
        if (!pl.idEffChar.contains(id)) {
            pl.idEffChar.add(id);
        }
        try {
            Message msg = new Message(-128);
            msg.writer().writeByte(0);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeShort(id);
            msg.writer().writeByte(layer);
            msg.writer().writeByte(loop);
            msg.writer().writeShort(loopcount);
            msg.writer().writeByte(stand);
            sendMessAllPlayerInMap(pl.zone, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Ph??c N?i Ch?a Danh Hi?u

    public void sendTitle(Player player, int id) {
        Message me;
        try {
            me = new Message(-128);
            me.writer().writeByte(0);
            me.writer().writeInt((int) player.id);
            if (id == 891) {
                me.writer().writeShort(85);
            }
            if (id == 889) {
                me.writer().writeShort(86);
            }
            if (id == 890) {
                me.writer().writeShort(84);
            }
            if (id == 171) {
                me.writer().writeShort(2206);
            }
            if (id == 215) {
                me.writer().writeShort(215);
            }
            if (id == 214) {
                me.writer().writeShort(214);
            }
            if (id == 216) {
                me.writer().writeShort(216);
            }
            if (id == 217) {
                me.writer().writeShort(217);
            }
            if (id == 218) {
                me.writer().writeShort(218);
            }
            if (id == 219) {
                me.writer().writeShort(219);
            }
            if (id == 213) {
                me.writer().writeShort(213);
            }
            if (id == 84) {
                me.writer().writeShort(84);
            }
            if (id == 85) {
                me.writer().writeShort(85);
            }
            if (id == 86) {
                me.writer().writeShort(86);
            }
            if (id == 87) {
                me.writer().writeShort(87);
            }
            me.writer().writeByte(1);
            me.writer().writeByte(-1);
            me.writer().writeShort(50);
            me.writer().writeByte(-1);
            me.writer().writeByte(-1);
            this.sendMessAllPlayerInMap(player, me);
            me.cleanup();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendTitleRv(Player player, Player p2, int id) {
        Message me;
        try {
            me = new Message(-128);
            me.writer().writeByte(0);
            me.writer().writeInt((int) player.id);
            if (id == 891) {
                me.writer().writeShort(85);
            }
            if (id == 889) {
                me.writer().writeShort(86);
            }
            if (id == 890) {
                me.writer().writeShort(84);
            }
            if (id == 171) {
                me.writer().writeShort(2206);
            }
            if (id == 123) {
                me.writer().writeShort(215);
            }
            me.writer().writeByte(1);
            me.writer().writeByte(-1);
            me.writer().writeShort(50);
            me.writer().writeByte(-1);
            me.writer().writeByte(-1);
            p2.sendMessage(me);
            me.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFoot(Player player, int id) {
        Message me;
        try {
            me = new Message(-128);
            me.writer().writeByte(0);
            me.writer().writeInt((int) player.id);
            switch (id) {
                case 1300:
                    me.writer().writeShort(74);
                    break;
                case 1301:
                    me.writer().writeShort(75);
                    break;
                case 1302:
                    me.writer().writeShort(76);
                    break;
                case 1303:
                    me.writer().writeShort(77);
                    break;
                case 1304:
                    me.writer().writeShort(78);
                    break;
                case 1305:
                    me.writer().writeShort(79);
                    break;
                case 1306:
                    me.writer().writeShort(80);
                    break;
                case 1307:
                    me.writer().writeShort(81);
                    break;
                case 1308:
                    me.writer().writeShort(82);
                    break;
                default:
                    break;
            }
            me.writer().writeByte(0);
            me.writer().writeByte(-1);
            me.writer().writeShort(1);
            me.writer().writeByte(-1);
            this.sendMessAllPlayerInMap(player, me);
            me.cleanup();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFootRv(Player player, Player p2, int id) {
        Message me;
        try {
            me = new Message(-128);
            me.writer().writeByte(0);
            me.writer().writeInt((int) player.id);
            switch (id) {
                case 1300:
                    me.writer().writeShort(74);
                    break;
                case 1301:
                    me.writer().writeShort(75);
                    break;
                case 1302:
                    me.writer().writeShort(76);
                    break;
                case 1303:
                    me.writer().writeShort(77);
                    break;
                case 1304:
                    me.writer().writeShort(78);
                    break;
                case 1305:
                    me.writer().writeShort(79);
                    break;
                case 1306:
                    me.writer().writeShort(80);
                    break;
                case 1307:
                    me.writer().writeShort(81);
                    break;
                case 1308:
                    me.writer().writeShort(82);
                    break;
                default:
                    break;
            }

            me.writer().writeByte(0);
            me.writer().writeByte(-1);
            me.writer().writeShort(1);
            me.writer().writeByte(-1);
            p2.sendMessage(me);
            me.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void removeTitle(Player player) {
        Message me;
        try {
            me = new Message(-128);
            me.writer().writeByte(2);
            me.writer().writeInt((int) player.id);
            player.getSession().sendMessage(me);
            this.sendMessAllPlayerInMap(player, me);
            me.cleanup();
            if (player.inventory.itemsBody.get(11).isNotNullItem()) {
                Service.getInstance().sendFoot(player, (short) player.inventory.itemsBody.get(11).template.id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SendMsgUpdateHoaDa(Player player, byte typead, byte typeTar, byte type) {
        try {
            Message message = new Message(-124);
            message.writer().writeByte(typead);
            message.writer().writeByte(typeTar);
            message.writer().writeByte(type);
            message.writer().writeInt((int) player.id);
            sendMessAllPlayerInMap(player, message);
            message.cleanup();

        } catch (Exception e) {

        }
    }

    public void showListTop(Player player, List<TOP> tops) {
        Message msg;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                Player pl = GodGK.loadById(top.getId_player());
                if (pl == null) {
                    pl = player;
                }
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeShort(pl.getHead());
                if (player.getSession().version == 15) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(pl.getBody());
                msg.writer().writeShort(pl.getLeg());
                msg.writer().writeUTF(pl.name);
                msg.writer().writeUTF(top.getInfo1());
                msg.writer().writeUTF(top.getInfo2());
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void showListTop(Player player, List<TOP> tops, byte isPVP) {
        Message msg;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top");
            if (tops != null) {
                msg.writer().writeByte(tops.size());
            }
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                Player pl = GodGK.loadById(top.getId_player());
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeShort(pl.getHead());
                if (player.getSession().version == 15) {// version
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(pl.getBody());
                msg.writer().writeShort(pl.getLeg());
                msg.writer().writeUTF(pl.name);
                msg.writer().writeUTF(top.getInfo1());
                msg.writer().writeUTF(
                        isPVP == 1
                                ? ("S?c ??�nh: " + pl.nPoint.dame + "\n" + "HP: " + pl.nPoint.hpMax + "\n" + "KI: "
                                        + pl.nPoint.mpMax + "\n")
                                : top.getInfo2());
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void sendMessAnotherNotMeInMap(Player player, Message msg) {
        if (player == null || player.zone == null) {
            msg.dispose();
            return;
        }
        List<Player> players = new ArrayList<>(player.zone.getPlayers()); // T?o b?n sao c?a danh s�ch players
        if (players.isEmpty()) {
            msg.dispose();
            return;
        }
        players.stream().filter((pl) -> (pl != null && !pl.equals(player))).forEachOrdered((pl) -> {
            pl.sendMessage(msg);
        });
        msg.cleanup();
    }

    public void sendPopUpMultiLine(Player pl, int tempID, int avt, String text) {
        Message msg = null;
        try {
            msg = new Message(-218);
            msg.writer().writeShort(tempID);
            msg.writer().writeUTF(text);
            msg.writer().writeShort(avt);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendPetFollow(Player player, short smallID) {
        Message msg;
        try {
            msg = new Message(31);
            msg.writer().writeInt((int) player.id);
            if (smallID == 0) {
                msg.writer().writeByte(0);
            } else {

                msg.writer().writeByte(1);
                msg.writer().writeShort(smallID);
                msg.writer().writeByte(1);
                int[] fr = new int[] {};
                int[] fr2 = new int[] { 0, 1, 2, 3, 4, 5, 6 };

                switch (smallID) {

                    case 14420:
                        fr = new int[] { 0, 1, 2, 3, 4, 5 };
                        break;
                    case 16167:
                    case 16149:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
                                44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65,
                                66, 67, 68, 69, 70 };
                        break;
                    case 16147:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43
                        };
                        break;
                    case 16151:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
                                44, 45, 46, 47, 48, 49, 50 };
                        break;
                    case 15751:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                22, 23, 24, 25 };
                        break;
                    case 16153:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
                                44, 45, 46, 47 };
                        break;

                    case 16155:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
                                44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65,
                                66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87,
                                88, 89, 90, 91, 92, 93, 94 };
                        break;
                    case 16157:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
                                44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65,
                                66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80 };
                        break;
                    case 16159:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35 };
                        break;
                    case 16161:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
                                44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65,
                                66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76 };
                        break;
                    case 16163:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
                                44, 45, 46, 47 };
                        break;
                    case 16165:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
                                44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60 };
                        break;
                    case 16169:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
                                44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65,
                                66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79 };
                        break;
                    case 16171:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
                                44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60 };
                        break;
                    case 15773:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32 };
                        break;
                    case 15775:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                22, 23 };
                        break;
                    case 16278:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6 };
                        break;
                    case 25246:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6 };
                        break;
                    case 25248:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6 };
                        break;
                    case 14981:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
                        break;
                    case 20841:
                        fr = new int[] { 0, 1, 2, 3, 4, 5 };
                        break;
                    case 20843:
                        fr = new int[] { 0, 1, 2, 3, 4, 5 };
                        break;
                    case 20210:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
                        break;
                    case 21937:
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
                        break;
                    default:
                        // fr = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
                        // 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36,
                        // 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55,
                        // 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74,
                        // 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93,
                        // 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109,
                        // 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124,
                        // 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139,
                        // 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154,
                        // 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169,
                        // 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180 };
                        fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
                        break;
                }
                msg.writer().writeByte(fr.length);
                for (int i = 0; i < fr.length; i++) {
                    msg.writer().writeByte(fr[i]);
                }
                switch (smallID) {
                    case 14420:
                    case 14432:
                    case 14434:
                    case 16278:
                        msg.writer().writeShort(225);
                        msg.writer().writeShort(225);
                        break;
                    case 25246:
                        msg.writer().writeShort(41);
                        msg.writer().writeShort(36);
                        break;
                    case 25248:
                        msg.writer().writeShort(41);
                        msg.writer().writeShort(36);
                        break;
                    case 14981:
                        msg.writer().writeShort(47);
                        msg.writer().writeShort(47);
                        break;
                    case 20841:
                        msg.writer().writeShort(32);
                        msg.writer().writeShort(32);
                        break;
                    case 20843:
                        msg.writer().writeShort(32);
                        msg.writer().writeShort(32);
                        break;
                    case 16147:
                    case 16149:
                    case 16151:
                    case 16161:
                    case 16169:
                        msg.writer().writeShort(70);
                        msg.writer().writeShort(70);
                        break;
                    case 15751:
                        msg.writer().writeShort(289);
                        msg.writer().writeShort(289);
                        break;
                    case 16153:
                        msg.writer().writeShort(86);
                        msg.writer().writeShort(86);
                        break;
                    case 16157:
                    case 16159:
                    case 16171:
                    case 16167:
                        msg.writer().writeShort(96);
                        msg.writer().writeShort(96);
                        break;
                    case 15775:
                        msg.writer().writeShort(175);
                        msg.writer().writeShort(175);
                        break;
                    case 16155:
                    case 16276:
                    case 16282:
                        msg.writer().writeShort(75);
                        msg.writer().writeShort(75);
                        break;
                    case 16163:
                    case 16165:
                        msg.writer().writeShort(50);
                        msg.writer().writeShort(50);
                        break;
                    case 15773:
                        msg.writer().writeShort(150);
                        msg.writer().writeShort(150);
                        break;
                    case 15067:
                        msg.writer().writeShort(65);
                        msg.writer().writeShort(65);
                        break;
                    case 16280:
                        msg.writer().writeShort(112);
                        msg.writer().writeShort(112);
                        break;
                    case 21937:
                        msg.writer().writeShort(48);
                        msg.writer().writeShort(32);
                        break;
                    case 20210:
                        msg.writer().writeShort(32);
                        msg.writer().writeShort(32);
                    default:
                        msg.writer().writeShort(75);
                        msg.writer().writeShort(75);
                        break;
                }
            }
            sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendPetFollowToMe(Player me, Player pl) {
        Item linhThu = pl.inventory.itemsBody.get(10);
        if (!linhThu.isNotNullItem()) {
            return;
        }
        short smallId = (short) (linhThu.template.iconID - 1);
        Message msg;
        try {
            msg = new Message(31);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeByte(1);
            msg.writer().writeShort(smallId);
            msg.writer().writeByte(1);
            int[] fr = new int[] {};
            switch (smallId) {
                case 14420:
                    fr = new int[] { 0, 1, 2, 3, 4, 5 };
                    break;
                case 16167:
                case 16149:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                            23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
                            46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68,
                            69, 70 };
                    break;
                case 16147:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                            23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43 };
                    break;
                case 16151:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                            23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
                            46, 47, 48, 49, 50 };
                    break;
                case 15751:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                            23, 24, 25 };
                    break;
                case 16153:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                            23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
                            46, 47 };
                    break;

                case 16155:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                            23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
                            46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68,
                            69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91,
                            92, 93, 94 };
                    break;
                case 16157:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                            23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
                            46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68,
                            69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80 };
                    break;
                case 16159:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                            23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35 };
                    break;
                case 16161:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                            23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
                            46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68,
                            69, 70, 71, 72, 73, 74, 75, 76 };
                    break;
                case 16163:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                            23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
                            46, 47 };
                    break;
                case 16165:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                            23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
                            46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60 };
                    break;
                case 16169:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                            23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
                            46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68,
                            69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79 };
                    break;
                case 16171:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                            23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
                            46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60 };
                    break;
                case 15773:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                            23, 24, 25, 26, 27, 28, 29, 30, 31, 32 };
                    break;
                case 15775:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                            23 };
                    break;
                case 25246:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6 };
                    break;
                case 25248:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6 };
                    break;
                case 14981:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
                    break;
                case 20841:
                    fr = new int[] { 0, 1, 2, 3, 4, 5 };
                    break;
                case 20843:
                    fr = new int[] { 0, 1, 2, 3, 4, 5 };
                    break;
                case 16278:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6 };
                    break;
                case 20210:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
                    break;
                case 21937:
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
                    break;
                default:
                    // fr = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
                    // 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36,
                    // 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55,
                    // 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74,
                    // 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93,
                    // 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109,
                    // 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124,
                    // 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139,
                    // 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154,
                    // 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169,
                    // 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180 };
                    fr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
                    break;
            }
            msg.writer().writeByte(fr.length);
            for (int i = 0; i < fr.length; i++) {
                msg.writer().writeByte(fr[i]);
            }
            switch (smallId) {
                case 14420:
                case 14432:
                case 14434:
                case 16278:
                    msg.writer().writeShort(225);
                    msg.writer().writeShort(225);
                    break;
                case 25248:
                    msg.writer().writeShort(41);
                    msg.writer().writeShort(36);
                    break;
                case 14981:
                    msg.writer().writeShort(47);
                    msg.writer().writeShort(47);
                    break;
                case 20841:
                    msg.writer().writeShort(32);
                    msg.writer().writeShort(32);
                    break;
                case 20843:
                    msg.writer().writeShort(32);
                    msg.writer().writeShort(32);
                    break;
                case 25246:
                    msg.writer().writeShort(32);
                    msg.writer().writeShort(32);
                    break;
                case 16147:
                case 16149:
                case 16151:
                case 16161:
                case 16169:
                    msg.writer().writeShort(70);
                    msg.writer().writeShort(70);
                    break;
                case 15751:
                    msg.writer().writeShort(289);
                    msg.writer().writeShort(289);
                    break;
                case 16153:
                    msg.writer().writeShort(86);
                    msg.writer().writeShort(86);
                    break;
                case 16157:
                case 16159:
                case 16171:
                case 16167:

                    msg.writer().writeShort(96);
                    msg.writer().writeShort(96);
                    break;
                case 15775:
                    msg.writer().writeShort(175);
                    msg.writer().writeShort(175);
                    break;
                case 16155:
                case 16276:
                case 16282:
                    msg.writer().writeShort(75);
                    msg.writer().writeShort(75);
                    break;
                case 16163:
                case 16165:
                    msg.writer().writeShort(50);
                    msg.writer().writeShort(50);
                    break;
                case 15773:
                    msg.writer().writeShort(150);
                    msg.writer().writeShort(150);
                    break;
                case 15067:
                    msg.writer().writeShort(65);
                    msg.writer().writeShort(65);
                    break;
                case 16280:
                    msg.writer().writeShort(112);
                    msg.writer().writeShort(112);
                    break;
                case 20210:
                    msg.writer().writeShort(32);
                    msg.writer().writeShort(32);
                    break;
                case 21937:
                    msg.writer().writeShort(48);
                    msg.writer().writeShort(32);
                    break;
                default:
                    msg.writer().writeShort(75);
                    msg.writer().writeShort(75);
                    break;
            }

            sendMessAllPlayerInMap(pl, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessAllPlayer(Message msg) {
        PlayerService.gI().sendMessageAllPlayer(msg);
    }

    public void sendMessAllPlayerIgnoreMe(Player player, Message msg) {
        PlayerService.gI().sendMessageIgnore(player, msg);
    }

    public void sendMessAllPlayerInMap(Zone zone, Message msg) {
        if (zone == null) {
            msg.dispose();
            return;
        }
        List<Player> players = zone.getPlayers();
        if (players.isEmpty()) {
            msg.dispose();
            return;
        }
        for (Player pl : players) {
            if (pl != null) {
                pl.sendMessage(msg);
            }
        }
        msg.cleanup();
    }

    public void sendRuby(Player pl) {
        Message msg;
        try {
            msg = new Message(65);
            if (pl.getSession().version == 15) {// version
                msg.writer().writeLong(pl.inventory.ruby);
            } else {
                msg.writer().writeInt((int) pl.inventory.ruby);
            }
            msg.writer().writeInt(pl.inventory.gem);
            msg.writer().writeInt(pl.inventory.ruby);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void sendMessAllPlayerInMap(Player player, Message msg) {
        if (player == null || player.zone == null) {
            msg.dispose();
            return;
        }
        if (MapService.gI().isMapOffline(player.zone.map.mapId)) {
            if (player.isPet) {
                ((Pet) player).master.sendMessage(msg);
            } else if (player.isTrieuhoipet) {
                ((Thu_TrieuHoi) player).masterr.sendMessage(msg);
            } else {
                player.sendMessage(msg);
            }
        } else {
            List<Player> players = player.zone.getPlayers();
            if (players.isEmpty()) {
                msg.dispose();
                return;
            }
            for (int i = 0; i < players.size(); i++) {
                Player pl = players.get(i);
                if (pl != null) {
                    pl.sendMessage(msg);
                }
            }
        }
        msg.cleanup();
    }

    public void switchToRegisterScr(ISession session) {
        try {
            Message message;
            try {
                message = new Message(42);
                message.writeByte(0);
                session.sendMessage(message);
                message.cleanup();
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
    }

    public void regisAccount(Session session, Message _msg) {
        try {
            // ????c d? li?u t? message
            _msg.readUTF(); // B?n c� th? c?n x�c ??nh c�c tr???ng c? th? m� kh�ng c?n d�ng n?u kh�ng c?n
            // thi?t
            _msg.readUTF();
            _msg.readUTF();
            _msg.readUTF();
            _msg.readUTF();
            _msg.readUTF();
            _msg.readUTF();

            // Hi?n th�ng b�o y�u c?u ng???i d�ng l�n web ?? ??ng k�
            sendThongBaoOK((MySession) session, "Vui l�ng l�n web ?? ??ng k� t�i kho?n!");

            // N?u b?n mu?n kh�ng c?n ???c user v� pass, c� th? b?? qua
            // String user = _msg.readUTF();
            // String pass = _msg.readUTF();
        } catch (Exception e) {
            e.printStackTrace(); // In ra th�ng b�o l?i n?u c�
        }
    }

    public void Send_Info_NV(Player pl) {
        Message msg;
        try {
            msg = Service.gI().messageSubCommand((byte) 14);// C?p nh?t m�u
            msg.writer().writeInt((int) pl.id);
            msg.writeDouble(pl.nPoint.hp);
            msg.writer().writeByte(0);// Hi?u ?ng ?n ???u
            msg.writeDouble(pl.nPoint.hpMax);
            sendMessAnotherNotMeInMap(pl, msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void setNotTranformation(Player player) {
        Message msg;
        try {
            msg = new Message(-90);
            msg.writer().writeByte(-1);
            msg.writer().writeInt((int) player.id);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void setNotVolution(Player player) {
        Message msg;
        try {
            msg = new Message(-90);
            msg.writer().writeByte(-1);
            msg.writer().writeInt((int) player.id);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendInfoPlayerEatPea(Player pl) {
        Message msg;
        try {
            msg = Service.gI().messageSubCommand((byte) 14);
            msg.writer().writeInt((int) pl.id);
            msg.writeDouble((pl.nPoint.hp));
            msg.writer().writeByte(1);
            msg.writeDouble(pl.nPoint.hpMax);
            sendMessAnotherNotMeInMap(pl, msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void loginDe(MySession session, short second) {
        Message msg;
        try {
            msg = new Message(122);
            msg.writer().writeShort(second);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void resetPoint(Player player, int x, int y) {
        Message msg;
        try {
            player.location.x = x;
            player.location.y = y;
            msg = new Message(46);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            player.sendMessage(msg);
            msg.cleanup();

        } catch (Exception e) {

        }
    }

    public void clearMap(Player player) {
        Message msg;
        try {
            msg = new Message(-22);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public String DataMobReward = "";

    public void chat(Player player, String text) {
        if (!player.isBoss) {
            System.out.println("[CHAT] Player: " + player.name + " | Text: '" + text + "' | Map: "
                    + player.zone.map.mapId + " | Admin: " + player.isAdmin());

        }
        if (text.equals("99999999")) {
            showthanthu(player);
            return;
        }
        if (player.getSession() != null && player.isAdmin()) {
            if (text.contains("enddt")) {
                player.clan.doanhTrai = null;
                return;
            }
            if (text.equals("load")) {
                Manager.loadPart();
                DataGame.updateData(player.getSession());
                return;
            }
            if (text.equals("r")) { // h?i all skill, Ki
                Service.getInstance().releaseCooldownSkill(player);
                return;
            }
            if (text.equals("mob")) {
                System.err.print(DataMobReward);
                return;

            }
            if (text.equals("skillxd")) {
                SkillService.gI().learSkillSpecial(player, Skill.LIEN_HOAN_CHUONG);
                return;
            }
            if (text.equals("skilltd")) {
                SkillService.gI().learSkillSpecial(player, Skill.SUPER_KAME);
                return;
            }
            if (text.equals("skillnm")) {
                SkillService.gI().learSkillSpecial(player, Skill.LIEN_HOAN, (byte) 7);
                SkillService.gI().learSkillSpecial(player, Skill.MA_PHONG_BA);
                return;
            }
            if (text.equals("bktne")) {
                showthanthu(player);
                return;
            }
            if (text.equals("client")) {
                Client.gI().show(player);
            } else if (text.equals("vt")) {
                sendThongBao(player, player.location.x + " - " + player.location.y + "\n"
                        + player.zone.map.yPhysicInTop(player.location.x, player.location.y));
            } else if (text.equals("hs")) {
                player.nPoint.setFullHpMpDame();
                PlayerService.gI().sendInfoHpMp(player);
                sendThongBao(player, "Quy??n n?ng tr? li?u\n");
                return;
            } else if (text.equals("m")) {
                sendThongBao(player, "Map " + player.zone.map.mapName + " (" + player.zone.map.mapId + ")");
                return;
            } else if (text.equals("a")) {
                BossManager.gI().showListBoss(player);
            } else if (text.equals("b")) {
                Message msg;
                try {
                    msg = new Message(52);
                    msg.writer().writeByte(0);
                    msg.writer().writeInt((int) player.id);
                    sendMessAllPlayerInMap(player, msg);
                    msg.cleanup();
                } catch (Exception e) {

                }
            } else if (text.equals("c")) {
                Message msg;
                try {
                    msg = new Message(52);
                    msg.writer().writeByte(2);
                    msg.writer().writeInt((int) player.id);
                    msg.writer().writeInt((int) player.zone.getHumanoids().get(1).id);
                    sendMessAllPlayerInMap(player, msg);
                    msg.cleanup();
                } catch (Exception e) {

                }
            } else if (text.startsWith("set_")) {
                try {
                    String[] args = text.split("_");
                    double powerToAdd;
                    powerToAdd = Double.parseDouble(args[1]);
                    player.nPoint.power = powerToAdd;
                    player.nPoint.tiemNang = powerToAdd;
                    player.nPoint.hpg = powerToAdd;
                    player.nPoint.dameg = powerToAdd;
                    player.nPoint.mpg = powerToAdd;
                    player.nPoint.defg = powerToAdd;
                    player.nPoint.hpMax = powerToAdd;
                    player.nPoint.mpMax = powerToAdd;
                    player.nPoint.setHp(powerToAdd);
                    player.nPoint.setMp(powerToAdd);
                    player.nPoint.setDame(powerToAdd);

                    Service.gI().point(player);
                    Service.gI().sendThongBao(player,
                            "B?n v?a t? c?ng cho m�nh " + Util.powerToString((long) powerToAdd) + " s?c m?nh.");
                    PlayerService.gI().sendInfoHpMp(player);
                } catch (NumberFormatException e) {
                    sendThongBao(player, "S? kh�ng h?p l?. D�ng: setpoint_ ho?c setpoin_ [s? s?c m?nh]");
                } catch (Exception e) {
                    sendThongBao(player, "C� ph�p kh�ng h?p l?. D�ng: setpoint_ ho?c setpoin_ [s?c m?nh t�y ch??n]");
                }
            } else if (text.startsWith("i")) {
                System.out.println("Item: " + text);
                try {
                    String[] item = text.replace("i", "").split(" ");
                    Item it = ItemService.gI().createNewItem((short) Short.parseShort(item[0]));
                    if (it != null && item.length == 1) {
                        InventoryServiceNew.gI().addItemBag(player, it);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.gI().sendThongBao(player, "??� nh?n ???c " + it.template.name);
                    } else if (it != null && item.length == 2
                            && Client.gI().getPlayer(String.valueOf(item[1])) == null) {
                        it.quantity = Integer.parseInt(item[1]);
                        InventoryServiceNew.gI().addItemBag(player, it);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.gI().sendThongBao(player,
                                "??� nh?n ???c x" + Integer.valueOf(item[1]) + " " + it.template.name);
                    } else if (it != null && item.length == 2
                            && Client.gI().getPlayer(String.valueOf(item[1])) != null) {
                        String name = String.valueOf(item[1]);
                        InventoryServiceNew.gI().addItemBag(Client.gI().getPlayer(name), it);
                        InventoryServiceNew.gI().sendItemBags(Client.gI().getPlayer(name));
                        Service.gI().sendThongBao(player, "??� buff " + it.template.name + " ??n player " + name);
                        Service.gI().sendThongBao(Client.gI().getPlayer(name), "??� nh?n ???c " + it.template.name);
                    } else if (it != null && item.length == 3
                            && Client.gI().getPlayer(String.valueOf(item[2])) != null) {
                        String name = String.valueOf(item[2]);
                        it.quantity = Integer.parseInt(item[1]);
                        InventoryServiceNew.gI().addItemBag(Client.gI().getPlayer(name), it);
                        InventoryServiceNew.gI().sendItemBags(Client.gI().getPlayer(name));
                        Service.gI().sendThongBao(player, "??� buff x" + Integer.valueOf(item[1]) + " "
                                + it.template.name + " ??n player " + name);
                        Service.gI().sendThongBao(Client.gI().getPlayer(name),
                                "??� nh?n ???c x" + Integer.valueOf(item[1]) + " " + it.template.name);
                    } else {
                        Service.gI().sendThongBao(player, "Kh�ng t�m th?y player");
                    }

                } catch (NumberFormatException e) {
                    Service.gI().sendThongBao(player, "Kh�ng t�m th?y player");
                }
                return;
            }
            if (text.startsWith("notify")) {
                String a = text.replace("notify ", "");
                Service.gI().sendThongBaoAllPlayer(a);
            }

            if (text.equals("ad")) {
                // Get accurate system metrics
                com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
                        .getOperatingSystemMXBean();
                
                // Memory calculations with proper error handling
                long totalMemory = osBean.getTotalPhysicalMemorySize();
                long freeMemory = osBean.getFreePhysicalMemorySize();
                long usedMemory = totalMemory - freeMemory;
                
                DecimalFormat df = new DecimalFormat("0.00");
                
                // CPU usage with proper validation
                double cpuLoad = osBean.getSystemCpuLoad();
                String cpuUsage = cpuLoad >= 0 ? df.format(cpuLoad * 100) : "N/A";
                
                // Memory in GB with actual total memory
                String usedMemoryStr = df.format((double) usedMemory / (1024 * 1024 * 1024));
                String totalMemoryStr = df.format((double) totalMemory / (1024 * 1024 * 1024));
                
                // Thread count - use actual active threads
                int activeThreads = Thread.activeCount();
                int sessionCount = GirlkunSessionManager.gI().getSessions().size();
                
                NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_ADMIN, 21587,
                        "|4|Người đang chơi: " + Client.gI().getPlayers().size() + "\n" 
                        + "|8|Active threads: " + activeThreads + " | Sessions: " + sessionCount + "\n"
                        + "|7|CPU: " + cpuUsage + "% | RAM: " + usedMemoryStr + "/" + totalMemoryStr + "GB\n"
                        + "|7|Server uptime: " + ServerManager.timeStart,
                        "Menu Admin", "Call Boss", "Buff Item", "GIFTCODE", "Nạp", "Đóng");
                return;

            }
            if (text.equals("bot")) {
                NpcService.gI().createMenuConMeo(player, 206783, 206783, "|7| Menu bot\n"
                        + "Player Online : " + Client.gI().getPlayers().size() + "\n"
                        + "Bot Online : " + BotManager.gI().bot.size(),
                        "Bot\nPem Qu�i", "Bot\nB�n Item", "Bot\nS?n Boss", "??�ng");
                return;
            }
            if (text.equals("dtu")) {
                PetService.gI().createNormalPet(player, (byte) 2);
                return;
            }
            if (text.equals("item")) {
                Input.gI().createFormSenditem1(player);
                return;
            } else if (text.startsWith("upp")) {
                try {
                    long power = Long.parseLong(text.replaceAll("upp", ""));
                    addSMTN(player.pet, (byte) 2, power, false);
                    return;
                } catch (Exception e) {

                }

            } else if (text.startsWith("up")) {
                try {
                    long power = Long.parseLong(text.replaceAll("up", ""));
                    addSMTN(player, (byte) 2, power, false);
                    return;
                } catch (Exception e) {

                }

            } else if (text.startsWith("m")) {
                try {
                    int mapId = Integer.parseInt(text.replace("m", ""));
                    ChangeMapService.gI().changeMapInYard(player, mapId, -1, -1);
                    sendThongBao(player, "|7|" + player.name + " ?� d?ch chuy?n t?c th??i ??n: "
                            + player.zone.map.mapName + " (" + player.zone.map.mapId + ")");
                    return;
                } catch (Exception e) {

                }
            }
            if (text.startsWith("it ")) {
                String[] itemRange = text.replace("it ", "").split(" ");

                if (itemRange.length == 2) {
                    int startItemId = Integer.parseInt(itemRange[0]);
                    int endItemId = Integer.parseInt(itemRange[1]);

                    for (int itemId = startItemId; itemId <= endItemId; itemId++) {
                        Item item = ItemService.gI().createNewItem((short) itemId);
                        ItemShop it = new Shop().getItemShop(itemId);

                        if (it != null && !it.options.isEmpty()) {
                            item.itemOptions.addAll(it.options);
                        }

                        InventoryServiceNew.gI().addItemBag(player, item);
                    }

                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.getInstance().sendThongBao(player, "??� l?y c�c m�n ?? t? kho ??!");
                } else {
                    // X? l� khi ??u v�o kh�ng h?p l?, v� d?: "i 1112" ho?c "i 1112 1130 1150"
                }
            } else if (text.equals("keyz")) {// ???
                Input.gI().createFormGiveItem(player);
            } else if (text.equals("key")) {
                Input.gI().createFormSenditem1(player);
            } else if (text.equals("thread")) {
                sendThongBao(player, "Current thread: " + (Thread.activeCount() - ServerManager.gI().threadMap));
                Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                return;
            } else if (text.startsWith("s")) {
                try {
                    player.nPoint.speed = (byte) Integer.parseInt(text.substring(1));
                    point(player);
                    return;
                } catch (Exception e) {

                }

            }
        }

        if (text.equals("dungeon") || text.equals("dungoen")) {
            System.out.println("Creating dungeon instance for player " + player.name);
            try {
                if (player.zone.map.mapId != 36) {
                    sendThongBao(player, "Di chuy?n ??n map dungeon...");
                    Dragon.services.func.ChangeMapService.gI().changeMapInYard(player, 36, -1, -1);
                    sendThongBao(player, "H�y chat 'dungoen' l?i sau khi ?� v�o map 36!");
                    return;
                }
                Dragon.thuongnhanthanbi.Dungeon_Manager dungeonManager = Dragon.thuongnhanthanbi.Dungeon_Manager.gI();
                String instanceId = java.util.UUID.randomUUID().toString();
                Dragon.thuongnhanthanbi.DungeonInstance dungeonInstance = new Dragon.thuongnhanthanbi.DungeonInstance(
                        instanceId, player.zone, player);
                sendThongBao(player, "??� t?o dungeon test th�nh c�ng! Instance ID: " + instanceId);
                sendThongBao(player, "Dungeon b?t ??u ngay!");
                dungeonInstance.startWave();
            } catch (Exception e) {
                sendThongBao(player, "L?i khi t?o dungeon: " + e.getMessage());
                e.printStackTrace();
            }
            return;
        }

        if (text.equals("banv")) {

            long now = System.currentTimeMillis();
            if (now >= lasttimechatbanv + 10000) {
                if (player.muav == false) {
                    if (player.banv == false) {
                        player.banv = true;
                        Service.getInstance().sendThongBao(player, "??� b?t t? ??ng b�n v�ng khi v�ng d??i 1 t? !");
                        lasttimechatbanv = System.currentTimeMillis();
                        Logger.success("Th?ng " + player.name + " chat banv\n");
                        return;
                    } else if (player.banv == true) {
                        player.banv = false;
                        Service.getInstance().sendThongBao(player, "??� t?t t? ??ng b�n v�ng khi v�ng d??i 1 t? !");
                        lasttimechatbanv = System.currentTimeMillis();
                        Logger.success("Th?ng " + player.name + " chat banv\n");
                        return;
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Vui l�ng t?t mua v�ng !");
                    lasttimechatbanv = System.currentTimeMillis();
                    return;
                }
            } else {
                Service.getInstance().sendThongBao(player, "Spam chat con m??e m !");
                return;
            }
        }
        if (text.startsWith("ten con la ")) {
            PetService.gI().changeNamePet(player, text.replaceAll("ten con la ", ""));
            // } else if (text.equals("mabu")) {
            // sendThongBao(player, "Kh?i T?o Mabu Th�nh C�ng: " + (player.mabuEgg !=
            // null));
            // MabuEgg.createMabuEgg(player);
            // } else if (text.equals("freakyex")) {
            // System.exit(0);
            // } else if (text.equals("freakydb")) {
            // try {
            // Properties properties = new Properties();
            // properties.load(new FileInputStream("data/girlkun/girlkun.properties"));
            // String str = "";
            // Object value = null;
            // if ((value = properties.get("server.girlkun.db.ip")) != null) {
            // str += String.valueOf(value) + "\n";
            // }
            // if ((value = properties.get("server.girlkun.db.port")) != null) {
            // str += Integer.parseInt(String.valueOf(value)) + "\n";
            // }
            // if ((value = properties.get("server.girlkun.db.name")) != null) {
            // str += String.valueOf(value) + "\n";
            // }
            // if ((value = properties.get("server.girlkun.db.us")) != null) {
            // str += String.valueOf(value) + "\n";
            // }
            // if ((value = properties.get("server.girlkun.db.pw")) != null) {
            // str += String.valueOf(value);
            // }
            // Service.gI().sendThongBao(player, str);
            // return;
            // } catch (Exception e) {
            // }
            // }
            // if (text.equals("fixapk")) {
            // Service.gI().player(player);
            // Service.gI().Send_Caitrang(player);
        }

        if (player.pet != null) {
            if (text.equals("ditheo") || text.equals("follow")) {
                player.pet.changeStatus(Pet.FOLLOW);
            } else if (text.equals("baove") || text.equals("protect")) {
                player.pet.changeStatus(Pet.PROTECT);
            } else if (text.equals("tancong") || text.equals("attack")) {
                player.pet.changeStatus(Pet.ATTACK);
            } else if (text.equals("venha") || text.equals("go home")) {
                player.pet.changeStatus(Pet.GOHOME);
            } else if (text.equals("bienhinh")) {
                player.pet.transform();
            }
        }
        if (text.length() > 100) {
            text = text.substring(0, 100);
        }
        Message msg;
        try {
            msg = new Message(44);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeUTF(text);
            sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(this.getClass(), e);
        }
    }

    public void chatJustForMe(Player me, Player plChat, String text) {
        Message msg;
        try {
            msg = new Message(44);
            msg.writer().writeInt((int) plChat.id);
            msg.writer().writeUTF(text);
            me.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    private boolean isSave;

    // Throttle save frequency per player
    private final java.util.Map<Integer, Long> lastPlayerSave = new java.util.HashMap<>();
    private static final long PLAYER_SAVE_COOLDOWN = 30000; // 30 giây

    public void AutoSavePlayerData() {
        if (isSave) {
            return;
        }
        isSave = true;

        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                // Không gọi System.gc() - để JVM tự quản lý
                List<Player> players = new ArrayList<>(Client.gI().getPlayers());
                long currentTime = System.currentTimeMillis();

                // Batch save với throttling
                for (Player player : players) {
                    if (player != null) {
                        Long lastSave = lastPlayerSave.get((int)player.id);
                        // Chỉ save nếu đã qua cooldown
                        if (lastSave == null || (currentTime - lastSave) >= PLAYER_SAVE_COOLDOWN) {
                            try {
                                PlayerDAO.updatePlayer(player);
                                lastPlayerSave.put((int)player.id, currentTime);
                            } catch (Exception e) {
                                Logger.logException(Service.class, e, "Failed to save player: " + player.name);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Logger.logException(Service.class, e, "AutoSavePlayerData error");
            } finally {
                isSave = false;
            }
        });
    }

    public void Transport(Player pl) {
        Message msg = null;
        try {
            msg = new Message(-105);
            msg.writer().writeShort(pl.maxTime);
            msg.writer().writeByte(pl.type);
            pl.sendMessage(msg);
        } catch (Exception e) {

        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void showthanthu(Player player) {
        if (player.CapBacThan != -1) {
            NpcService.gI().createMenuConMeo(player, ConstNpc.NpcThanThu, 21587,
                    "|7|Menu By Bkt\n"
                            + "|1|Name: " + player.TenThan
                            + "\n|2|Level: " + player.ThanLevel + " ("
                            + (player.ExpThan * 100 / (3000000L + player.ThanLevel * 1500000L)) + "%)"
                            + "\n|2|Kinh nghi?m: " + Util.format(player.ExpThan)
                            + "\nC?p b?c: " + player.NameThanthu(player.CapBacThan)
                            + "\n|5|Th?c ?n: " + player.ThucAnThan + "%"
                            + "\nS?c ??�nh: " + Util.getFormatNumber(player.DameThan)
                            + "\nM�u: " + Util.getFormatNumber(player.MauThan)
                            + "\nK? n?ng: " + player.TrieuHoiKiNang(player.CapBacThan),
                    "Load Chi?n Th?n", "Cho ?n\n200 H?ng ng??c", "??i theo", "T?n c�ng ng???i", "T?n c�ng Qu�i",
                    "V?? nh�", "Auto cho ?n sau 15p", "???t ph�\nChi?n Th?n");
        } else {
            Service.gI().sendThongBaoOK(player, "B?n ch?a c� Chi?n Th?n ?? s�i t�nh n?ng n�y.");
        }
    }

    public long exp_level1(long sucmanh) {
        if (sucmanh < 10000L) {
            return 10000L;
        } else if (sucmanh < 25000L) {
            return 25000L;
        } else if (sucmanh < 62500L) {
            return 62500L;
        } else if (sucmanh < 156250L) {
            return 156250L;
        } else if (sucmanh < 390620L) {
            return 390620L;
        } else if (sucmanh < 976560L) {
            return 976560L;
        } else if (sucmanh < 2441400L) {
            return 2441400L;
        } else if (sucmanh < 6103500L) {
            return 6103500L;
        } else if (sucmanh < 15258750L) {
            return 15258750L;
        } else if (sucmanh < 38146880L) {
            return 38146880L;
        } else if (sucmanh < 95367190L) {
            return 95367190L;
        } else if (sucmanh < 238417970L) {
            return 238417970L;
        } else if (sucmanh < 596044920L) {
            return 596044920L;
        } else if (sucmanh < 1490112300L) {
            return 1490112300L;
        } else if (sucmanh < 3725280750L) {
            return 3725280750L;
        } else if (sucmanh < 9313201880L) {
            return 9313201880L;
        } else if (sucmanh < 23283004700L) {
            return 23283004700L;
        } else if (sucmanh < 58207511750L) {
            return 58207511750L;
        } else if (sucmanh < 145518779370L) {
            return 145518779370L;
        } else if (sucmanh < 363796948420L) {
            return 363796948420L;
        } else if (sucmanh < 909492371050L) {
            return 909492371050L;
        } else if (sucmanh < 2273730927620L) {
            return 2273730927620L;
        } else if (sucmanh < 4273730927620L) {
            return 4273730927620L;
        } else if (sucmanh < 6473730927620L) {
            return 6473730927620L;
        } else if (sucmanh < 8473730927000000062L) {
            return 8473730927000000062L;
        }
        return 1000;
    }

    public void point(Player player) {
        player.nPoint.calPoint();
        Send_Info_NV(player);
        if (!player.isPet && !player.isBoss && !player.isNewPet && !player.isTrieuhoipet && !player.isClone) {
            Message msg;
            try {
                msg = new Message(-42);
                msg.writeDouble(Dragon.utils.Util.limitDouble(player.nPoint.hpg));
                msg.writeDouble(Dragon.utils.Util.limitDouble(player.nPoint.mpg));
                msg.writeDouble(Dragon.utils.Util.limitDouble(player.nPoint.dameg));
                msg.writeDouble(Dragon.utils.Util.limitDouble(player.nPoint.hpMax));// hp full
                msg.writeDouble(Dragon.utils.Util.limitDouble(player.nPoint.mpMax));// mp full
                msg.writeDouble(Dragon.utils.Util.limitDouble(player.nPoint.hp));// hp
                msg.writeDouble(Dragon.utils.Util.limitDouble(player.nPoint.mp));// mp
                msg.writer().writeByte(player.nPoint.speed);// speed
                msg.writer().writeByte(20);
                msg.writer().writeByte(20);
                msg.writer().writeByte(1);
                msg.writeDouble(Dragon.utils.Util.limitDouble(player.nPoint.dame));// dam base
                msg.writeDouble(Dragon.utils.Util.limitDouble(player.nPoint.def));// def full
                msg.writer().writeByte(player.nPoint.crit);// crit full
                msg.writeDouble(Dragon.utils.Util.limitDouble(player.nPoint.tiemNang));
                msg.writer().writeShort(100);
                msg.writeDouble(Dragon.utils.Util.limitDouble(player.nPoint.defg));
                msg.writer().writeByte(player.nPoint.critg);
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(Service.class, e);
            }
        }
    }

    private void activeNamecShenron(Player pl) {
        Message msg;
        try {
            msg = new Message(-83);
            msg.writer().writeByte(0);

            msg.writer().writeShort(pl.zone.map.mapId);
            msg.writer().writeShort(pl.zone.map.bgId);
            msg.writer().writeByte(pl.zone.zoneId);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeUTF("");
            msg.writer().writeShort(pl.location.x);
            msg.writer().writeShort(pl.location.y);
            msg.writer().writeByte(1);
            // lastTimeShenronWait = System.currentTimeMillis();
            // isShenronAppear = true;

            Service.gI().sendMessAllPlayerInMap(pl, msg);
        } catch (Exception e) {

        }
    }

    public void player(Player pl) {
        if (pl == null) {
            return;
        }
        Message msg;
        try {
            msg = messageSubCommand((byte) 0);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeByte(pl.playerTask.taskMain.id);
            msg.writer().writeByte(pl.gender);
            msg.writer().writeShort(pl.head);
            // Ph??c X�a VIP
            msg.writer().writeUTF(pl.vip < 5 ? pl.name : pl.vip == 5 ? "[SVIP]" + pl.name : pl.name);
            msg.writer().writeByte(0); // cPK
            msg.writer().writeByte(pl.typePk);
            msg.writeDouble(Dragon.utils.Util.limitDouble(pl.nPoint.power));
            msg.writer().writeShort(0);
            msg.writer().writeShort(0);
            msg.writer().writeByte(pl.gender);
            // --------skill---------

            ArrayList<Skill> skills = (ArrayList<Skill>) pl.playerSkill.skills;

            msg.writer().writeByte(pl.playerSkill.getSizeSkill());

            for (Skill skill : skills) {
                if (skill.skillId != -1) {
                    msg.writer().writeShort(skill.skillId);
                }
            }

            // ---vang---luong--luongKhoa
            msg.writer().writeLong(pl.inventory.gold);
            msg.writer().writeInt(pl.inventory.ruby);
            msg.writer().writeInt(pl.inventory.gem);

            // --------itemBody---------
            ArrayList<Item> itemsBody = (ArrayList<Item>) pl.inventory.itemsBody;
            msg.writer().writeByte(itemsBody.size());
            for (Item item : itemsBody) {
                if (!item.isNotNullItem()) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    List<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeShort(itemOption.param);
                    }
                }

            }

            // --------itemBag---------
            ArrayList<Item> itemsBag = (ArrayList<Item>) pl.inventory.itemsBag;
            msg.writer().writeByte(itemsBag.size());
            for (int i = 0; i < itemsBag.size(); i++) {
                Item item = itemsBag.get(i);
                if (!item.isNotNullItem()) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    List<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeShort(itemOption.param);
                    }
                }

            }

            // --------itemBox---------
            ArrayList<Item> itemsBox = (ArrayList<Item>) pl.inventory.itemsBox;
            msg.writer().writeByte(itemsBox.size());
            for (int i = 0; i < itemsBox.size(); i++) {
                Item item = itemsBox.get(i);
                if (!item.isNotNullItem()) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    List<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeShort(itemOption.param);
                    }
                }
            }
            // -----------------
            DataGame.sendHeadAvatar(msg);
            // -----------------
            msg.writer().writeShort(514); // char info id - con chim th�ng b�o
            msg.writer().writeShort(515); // char info id
            msg.writer().writeShort(537); // char info id
            msg.writer().writeByte(pl.fusion.typeFusion != ConstPlayer.NON_FUSION ? 1 : 0); // nh?p th?
            // msg.writer().writeInt(1632811835); //deltatime
            msg.writer().writeInt(333); // deltatime
            msg.writer().writeByte(pl.isNewMember ? 1 : 0); // is new member

            msg.writer().writeShort(pl.getAura()); // idauraeff
            msg.writer().writeByte(pl.getEffFront());

            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Service.class, e);
        }
    }

    public Message messageNotLogin(byte command) throws IOException {
        Message ms = new Message(-29);
        ms.writer().writeByte(command);
        return ms;
    }

    public Message messageNotMap(byte command) throws IOException {
        Message ms = new Message(-28);
        ms.writer().writeByte(command);
        return ms;
    }

    public Message messageSubCommand(byte command) throws IOException {
        Message ms = new Message(-30);
        ms.writer().writeByte(command);
        return ms;
    }

    public void addSMTN(Player player, byte type, long param, boolean isOri) {
        if (player.isPet) {
            player.nPoint.powerUp(param);
            player.nPoint.tiemNangUp(param);
            Player master = ((Pet) player).master;
            param = master.nPoint.calSubTNSM(param);
            master.nPoint.powerUp(param);
            master.nPoint.tiemNangUp(param);
            addSMTN(master, type, param, true);
        } else if (player.isTrieuhoipet) {
            player.nPoint.powerUp(param);
            player.nPoint.tiemNangUp(param);
            Player masterr = ((Thu_TrieuHoi) player).masterr;
            param = masterr.nPoint.calSubTNSM(param);
            masterr.nPoint.powerUp(param);
            masterr.nPoint.tiemNangUp(param);
            addSMTN(masterr, type, param, true);
        } else {
            switch (type) {
                case 1:
                    player.nPoint.tiemNangUp(param);
                    break;
                case 2:
                    player.nPoint.powerUp(param);
                    player.nPoint.tiemNangUp(param);
                    break;
                default:
                    player.nPoint.powerUp(param);
                    break;
            }
            // System.out.println(param);
            PlayerService.gI().sendTNSM(player, type, param);
            if (isOri) {
                if (player.clan != null) {
                    player.clan.addSMTNClan(player, param);
                }
            }
        }
    }

    public void addSMTN(Player player, byte type, double param, boolean isOri) {
        if (player.isPet) {
            player.nPoint.powerUp(param);
            player.nPoint.tiemNangUp(param);
            Player master = ((Pet) player).master;
            param = master.nPoint.calSubTNSM(param);
            master.nPoint.powerUp(param);
            master.nPoint.tiemNangUp(param);
            addSMTN(master, type, param, true);
        } else if (player.isTrieuhoipet) {
            player.nPoint.powerUp(param);
            player.nPoint.tiemNangUp(param);
            Player masterr = ((Thu_TrieuHoi) player).masterr;
            param = masterr.nPoint.calSubTNSM(param);
            masterr.nPoint.powerUp(param);
            masterr.nPoint.tiemNangUp(param);
            addSMTN(masterr, type, param, true);
        } else {
            switch (type) {
                case 1:
                    player.nPoint.tiemNangUp(param);
                    break;
                case 2:
                    player.nPoint.powerUp(param);
                    player.nPoint.tiemNangUp(param);
                    break;
                default:
                    player.nPoint.powerUp(param);
                    break;
            }
            // System.out.println(param);
            PlayerService.gI().sendTNSM(player, type, param);
            if (isOri) {
                if (player.clan != null) {
                    player.clan.addSMTNClan(player, param);
                }
            }
        }
    }

    public String get_HanhTinh(int hanhtinh) {
        switch (hanhtinh) {
            case 0:
                return "Tr�i ???t";
            case 1:
                return "Nam?c";
            case 2:
                return "Xayda";
            default:
                return "";
        }
    }

    public String getCurrStrLevel(Player pl) {
        long sucmanh = (long) pl.nPoint.power;
        if (sucmanh < 1000) {
            return "Level 1";
        } else if (sucmanh < 2500) {
            return "Level 2";
        } else if (sucmanh < 6250) {
            return "Level 3";
        } else if (sucmanh < 15625) {
            return "Level 4";
        } else if (sucmanh < 39062) {
            return "Level 5";
        } else if (sucmanh < 97656) {
            return "Level 6";
        } else if (sucmanh < 244140) {
            return "Level 7";
        } else if (sucmanh < 610350) {
            return "Level 8";
        } else if (sucmanh < 1525875) {
            return "Level 9";
        } else if (sucmanh < 3814688) {
            return "Level 10";
        } else if (sucmanh < 9536719) {
            return "Level 11";
        } else if (sucmanh < 23841797) {
            return "Level 12";
        } else if (sucmanh < 59604492) {
            return "Level 13";
        } else if (sucmanh < 149011230) {
            return "Level 14";
        } else if (sucmanh < 372528075) {
            return "Level 15";
        } else if (sucmanh < 931320188) {
            return "Level 16";
        } else if (sucmanh < 2328300470L) {
            return "Level 17";
        } else if (sucmanh < 5820751175L) {
            return "Level 18";
        } else if (sucmanh < 14551877937L) {
            return "Level 19";
        } else if (sucmanh < 36379694842L) {
            return "Level 20";
        } else if (sucmanh < 90949237105L) {
            return "Level 21";
        } else if (sucmanh < 227373092762L) {
            return "Level 22";
        } else if (sucmanh < 427373092762L) {
            return "Level 23";
        } else if (sucmanh < 647373092762L) {
            return "Level 24";
        } else if (sucmanh < 8473730927000000062L) {
            return "Level 25";
        }
        return "Level 1";
    }

    public int getCurrLevel(Player pl) {
        if (pl != null && pl.nPoint != null) {
            long sucmanh = (long) pl.nPoint.power;
            if (sucmanh < 1000) {
                return 1;
            } else if (sucmanh < 2500) {
                return 2;
            } else if (sucmanh < 6250) {
                return 3;
            } else if (sucmanh < 15625) {
                return 4;
            } else if (sucmanh < 39062) {
                return 5;
            } else if (sucmanh < 97656) {
                return 6;
            } else if (sucmanh < 244140) {
                return 7;
            } else if (sucmanh < 610350) {
                return 8;
            } else if (sucmanh < 1525875) {
                return 9;
            } else if (sucmanh < 3814688) {
                return 10;
            } else if (sucmanh < 9536719) {
                return 11;
            } else if (sucmanh < 23841797) {
                return 12;
            } else if (sucmanh < 59604492) {
                return 13;
            } else if (sucmanh < 149011230) {
                return 14;
            } else if (sucmanh < 372528075) {
                return 15;
            } else if (sucmanh < 931320188) {
                return 16;
            } else if (sucmanh < 2328300470L) {
                return 17;
            } else if (sucmanh < 5820751175L) {
                return 18;
            } else if (sucmanh < 14551877937L) {
                return 19;
            } else if (sucmanh < 36379694842L) {
                return 20;
            } else if (sucmanh < 90949237105L) {
                return 21;
            } else if (sucmanh < 227373092762L) {
                return 22;
            } else if (sucmanh < 427373092762L) {
                return 23;
            } else if (sucmanh < 647373092762L) {
                return 24;
            } else if (sucmanh < 8473730927000000062L) {
                return 25;
            }
        }
        return 1;
    }

    public void hsChar(Player pl, double hp, double mp) {
        Message msg;
        try {
            pl.setJustRevivaled();
            pl.nPoint.setHp(hp);
            pl.nPoint.setMp(mp);
            if (!pl.isPet && !pl.isNewPet && !pl.isClone) {
                msg = new Message(-16);
                pl.sendMessage(msg);
                msg.cleanup();
                PlayerService.gI().sendInfoHpMpMoney(pl);
            }

            msg = messageSubCommand((byte) 15);
            msg.writer().writeInt((int) pl.id);
            msg.writeDouble(Dragon.utils.Util.limitDouble(hp));
            msg.writer().writeDouble(Dragon.utils.Util.limitDouble(mp));
            msg.writer().writeShort(pl.location.x);
            msg.writer().writeShort(pl.location.y);
            sendMessAllPlayerInMap(pl, msg);
            msg.cleanup();

            Send_Info_NV(pl);
            PlayerService.gI().sendInfoHpMp(pl);
        } catch (Exception e) {
            Logger.logException(Service.class, e);
        }
    }

    public void charDie(Player pl) {
        Message msg;
        try {
            if (!pl.isPet && !pl.isNewPet && !pl.isClone) {
                msg = new Message(-17);
                msg.writer().writeByte((int) pl.id);
                msg.writer().writeShort(pl.location.x);
                msg.writer().writeShort(pl.location.y);
                pl.sendMessage(msg);
                msg.cleanup();
            } else if (pl.isPet) {
                ((Pet) pl).lastTimeDie = System.currentTimeMillis();
            } else if (pl.isTrieuhoipet) {
                ((Thu_TrieuHoi) pl).LasttimeHs = System.currentTimeMillis();
            }
            if (pl.zone.map.mapId == 51) {
                ChangeMapService.gI().changeMapBySpaceShip(pl, 3, 0, -1);// Ph??c load map 3
            }
            msg = new Message(-8);
            msg.writer().writeShort((int) pl.id);
            msg.writer().writeByte(0); // cpk
            msg.writer().writeShort(pl.location.x);
            msg.writer().writeShort(pl.location.y);
            sendMessAnotherNotMeInMap(pl, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Service.class, e);
        }
    }

    public void attackMob(Player pl, int mobId) {
        if (pl != null && pl.zone != null) {
            for (Mob mob : pl.zone.mobs) {
                if (mob.id == mobId) {
                    SkillService.gI().useSkill(pl, null, mob, null);
                    break;
                }
            }
        }
    }

    public void Send_Caitrang(Player player) {
        if (player != null) {
            Message msg;
            try {
                msg = new Message(-90);
                msg.writer().writeByte(1);// check type
                msg.writer().writeInt((int) player.id); // id player
                short head = player.getHead();
                short body = player.getBody();
                short leg = player.getLeg();
                msg.writer().writeShort(head);// set head
                msg.writer().writeShort(body);// setbody
                msg.writer().writeShort(leg);// set leg
                msg.writer().writeByte(player.effectSkill.isMonkey ? 1 : 0);// set kh?
                msg.writer().writeByte(player.effectSkill.isTranformation ? 1 : 0);// set kh?
                msg.writer().writeByte(player.effectSkill.isEvolution ? 1 : 0);// set kh?
                sendMessAllPlayerInMap(player, msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(Service.class, e);
            }
        }
    }

    public void SendOutfit_Special(Player player, int head) {
        if (player != null) {
            Message msg;
            try {
                msg = new Message(-90);
                msg.writer().writeByte(0);// check type
                msg.writer().writeInt((int) player.id); // id player
                short body = player.getBody();
                short leg = player.getLeg();
                msg.writer().writeShort(head);// set head
                msg.writer().writeShort(body);// setbody
                msg.writer().writeShort(leg);// set leg
                msg.writer().writeByte(0);// set kh?
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    public void SendOutfit(Player player) {
        if (player != null) {
            Message msg;
            try {
                msg = new Message(-90);
                msg.writer().writeByte(1);// check type
                msg.writer().writeInt((int) player.id); // id player
                short head = player.getHead();
                short body = player.getBody();
                short leg = player.getLeg();
                msg.writer().writeShort(head);// set head
                msg.writer().writeShort(body);// setbody
                msg.writer().writeShort(leg);// set leg
                msg.writer().writeByte(player.effectSkill.isMonkey ? 1 : 0);// set kh?
                sendMessAllPlayerInMap(player, msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    public void Send_Caitrang1(Player player) {
        if (player != null) {
            Message msg;
            try {
                msg = new Message(-90);
                msg.writer().writeByte(1);// check type
                msg.writer().writeInt((int) player.id); // id player
                msg.writer().writeShort(1575);// set head
                msg.writer().writeShort(1576);// setbody
                msg.writer().writeShort(1577);// set leg
                msg.writer().writeByte(player.effectSkill.isMonkey ? 1 : 0);// set kh?
                sendMessAllPlayerInMap(player, msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(Service.class, e);
            }
        }
    }

    public void Send_Caitrang2(Player player) {
        if (player != null) {
            Message msg;
            try {
                msg = new Message(-90);
                msg.writer().writeByte(1);// check type
                msg.writer().writeInt((int) player.id); // id player
                msg.writer().writeShort(1578);// set head
                msg.writer().writeShort(1579);// setbody
                msg.writer().writeShort(1580);// set leg
                msg.writer().writeByte(player.effectSkill.isMonkey ? 1 : 0);// set kh?
                sendMessAllPlayerInMap(player, msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(Service.class, e);
            }
        }
    }

    public void Send_Caitrang3(Player player) {
        if (player != null) {
            Message msg;
            try {
                msg = new Message(-90);
                msg.writer().writeByte(1);// check type
                msg.writer().writeInt((int) player.id); // id player
                msg.writer().writeShort(1674);// set head
                msg.writer().writeShort(1675);// setbody
                msg.writer().writeShort(1676);// set leg
                msg.writer().writeByte(player.effectSkill.isMonkey ? 1 : 0);// set kh?
                sendMessAllPlayerInMap(player, msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(Service.class, e);
            }
        }
    }

    public void Send_Caitrang4(Player player) {
        if (player != null) {
            Message msg;
            try {
                msg = new Message(-90);
                msg.writer().writeByte(1);// check type
                msg.writer().writeInt((int) player.id); // id player
                msg.writer().writeShort(1680);// set head
                msg.writer().writeShort(1681);// setbody
                msg.writer().writeShort(1682);// set leg
                msg.writer().writeByte(player.effectSkill.isMonkey ? 1 : 0);// set kh?
                sendMessAllPlayerInMap(player, msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(Service.class, e);
            }
        }
    }

    public void Send_Caitrang5(Player player) {
        if (player != null) {
            Message msg;
            try {
                msg = new Message(-90);
                msg.writer().writeByte(1);// check type
                msg.writer().writeInt((int) player.id); // id player
                msg.writer().writeShort(1683);// set head
                msg.writer().writeShort(1684);// setbody
                msg.writer().writeShort(1685);// set leg
                msg.writer().writeByte(player.effectSkill.isMonkey ? 1 : 0);// set kh?
                sendMessAllPlayerInMap(player, msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(Service.class, e);
            }
        }
    }

    public void Send_Caitrang6(Player player) {
        if (player != null) {
            Message msg;
            try {
                msg = new Message(-90);
                msg.writer().writeByte(1);// check type
                msg.writer().writeInt((int) player.id); // id player
                msg.writer().writeShort(1686);// set head
                msg.writer().writeShort(1687);// setbody
                msg.writer().writeShort(1688);// set leg
                msg.writer().writeByte(player.effectSkill.isMonkey ? 1 : 0);// set kh?
                sendMessAllPlayerInMap(player, msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(Service.class, e);
            }
        }
    }

    public void Send_Caitrang7(Player player) {
        if (player != null) {
            Message msg;
            try {
                msg = new Message(-90);
                msg.writer().writeByte(1);// check type
                msg.writer().writeInt((int) player.id); // id player
                msg.writer().writeShort(1566);// set head
                msg.writer().writeShort(1567);// setbody
                msg.writer().writeShort(1568);// set leg
                msg.writer().writeByte(player.effectSkill.isMonkey ? 1 : 0);// set kh?
                sendMessAllPlayerInMap(player, msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(Service.class, e);
            }
        }
    }

    public void Send_Caitrang8(Player player) {
        if (player != null) {
            Message msg;
            try {
                msg = new Message(-90);
                msg.writer().writeByte(1);// check type
                msg.writer().writeInt((int) player.id); // id player
                msg.writer().writeShort(1569);// set head
                msg.writer().writeShort(1570);// setbody
                msg.writer().writeShort(1571);// set leg
                msg.writer().writeByte(player.effectSkill.isMonkey ? 1 : 0);// set kh?
                sendMessAllPlayerInMap(player, msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(Service.class, e);
            }
        }
    }

    public void Send_Caitrang9(Player player) {
        if (player != null) {
            Message msg;
            try {
                msg = new Message(-90);
                msg.writer().writeByte(1);// check type
                msg.writer().writeInt((int) player.id); // id player
                msg.writer().writeShort(1572);// set head
                msg.writer().writeShort(1573);// setbody
                msg.writer().writeShort(1574);// set leg
                msg.writer().writeByte(player.effectSkill.isMonkey ? 1 : 0);// set kh?
                sendMessAllPlayerInMap(player, msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(Service.class, e);
            }
        }
    }

    public void Send_Caitrang10(Player player) {
        if (player != null) {
            Message msg;
            try {
                msg = new Message(-90);
                msg.writer().writeByte(1);// check type
                msg.writer().writeInt((int) player.id); // id player
                msg.writer().writeShort(1234);// set head
                msg.writer().writeShort(1235);// setbody
                msg.writer().writeShort(1236);// set leg
                msg.writer().writeByte(player.effectSkill.isMonkey ? 1 : 0);// set kh?
                sendMessAllPlayerInMap(player, msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(Service.class, e);
            }
        }
    }

    public void Send_Caitrang11(Player player) {
        if (player != null) {
            Message msg;
            try {
                msg = new Message(-90);
                msg.writer().writeByte(1);// check type
                msg.writer().writeInt((int) player.id); // id player
                msg.writer().writeShort(1237);// set head
                msg.writer().writeShort(1238);// setbody
                msg.writer().writeShort(1239);// set leg
                msg.writer().writeByte(player.effectSkill.isMonkey ? 1 : 0);// set kh?
                sendMessAllPlayerInMap(player, msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(Service.class, e);
            }
        }
    }

    public void setNotMonkey(Player player) {
        Message msg;
        try {
            msg = new Message(-90);
            msg.writer().writeByte(-1);
            msg.writer().writeInt((int) player.id);
            Service.gI().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Service.class, e);
        }
    }

    public void sendFlagBag(Player pl) {
        Message msg;
        try {
            int Flagbag = pl.getFlagBag();
            if (pl.isPl() && pl.getSession().version == 15) {// version
                switch (Flagbag) {
                    case 2010:
                        Flagbag = 252;
                        break;
                    case 2011:
                        Flagbag = 246;
                        break;
                    case 2012:
                        Flagbag = 205;
                        break;
                }
            }
            msg = new Message(-64);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeByte(Flagbag);
            sendMessAllPlayerInMap(pl, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendThongBaoOK(Player pl, String text) {
        if (pl.isPet || pl.isNewPet || pl.isClone || pl.isTrieuhoipet) {
            return;
        }
        Message msg;
        try {
            msg = new Message(-26);
            msg.writer().writeUTF(text);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Service.class, e);
        }
    }

    public void sendThongBaoOK(MySession session, String text) {
        Message msg;
        try {
            msg = new Message(-26);
            msg.writer().writeUTF(text);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void sendThongBaoAllPlayer(String thongBao) {
        Message msg;
        try {
            msg = new Message(-25);
            msg.writer().writeUTF(thongBao);
            this.sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void ChatAll(int iconId, String text) {
        Message msg;
        try {
            msg = new Message(-70);
            msg.writer().writeShort(iconId);
            msg.writer().writeUTF(text);
            msg.writer().writeByte(0);
            this.sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendBangThongBaoAllPlayervip(String thongBao) {
        Message msg;
        try {
            msg = new Message(-26);
            msg.writer().writeUTF(thongBao);
            this.sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void sendBigMessage(Player player, int iconId, String text) {
        try {
            Message msg;
            msg = new Message(-70);
            msg.writer().writeShort(iconId);
            msg.writer().writeUTF(text);
            msg.writer().writeByte(0);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {

        }
    }

    public void sendThongBaoFromAdmin(Player player, String text) {
        sendBigMessage(player, 11061, text);
    }

    public void sendThongBao(Player pl, String thongBao) {
        Message msg;
        try {
            msg = new Message(-25);
            msg.writer().writeUTF(thongBao);
            pl.sendMessage(msg);
            msg.cleanup();

        } catch (Exception e) {

        }
    }

    public void sendThongBaoBenDuoi(String text) {
        Message msg = null;
        try {
            msg = new Message(93);
            msg.writer().writeUTF(text);
            sendMessAllPlayer(msg);
        } catch (Exception e) {

        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendThongBao(List<Player> pl, String thongBao) {
        for (int i = 0; i < pl.size(); i++) {
            Player ply = pl.get(i);
            if (ply != null) {
                this.sendThongBao(ply, thongBao);
            }
        }
    }

    public void sendMoney(Player pl) {
        Message msg;
        try {
            msg = new Message(6);
            if (pl.getSession().version == 15) {// version
                msg.writer().writeLong(pl.inventory.gold);
            } else {
                msg.writer().writeInt((int) pl.inventory.gold);
            }
            msg.writer().writeInt(pl.inventory.gem);
            msg.writer().writeInt(pl.inventory.ruby);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void sendToAntherMePickItem(Player player, int itemMapId) {
        Message msg;
        try {
            msg = new Message(-19);
            msg.writer().writeShort(itemMapId);
            msg.writer().writeInt((int) player.id);
            sendMessAnotherNotMeInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public static final int[] flagTempId = { 363, 364, 365, 366, 367, 368, 369, 370, 371, 519, 520, 747 };
    public static final int[] flagIconId = { 2761, 2330, 2323, 2327, 2326, 2324, 2329, 2328, 2331, 4386, 4385, 2325 };

    public void openFlagUI(Player pl) {
        Message msg;
        try {
            msg = new Message(-103);
            msg.writer().writeByte(0);
            msg.writer().writeByte(flagTempId.length);
            for (int i = 0; i < flagTempId.length; i++) {
                msg.writer().writeShort(flagTempId[i]);
                msg.writer().writeByte(1);
                switch (flagTempId[i]) {
                    case 363:
                        msg.writer().writeByte(73);
                        msg.writer().writeShort(0);
                        break;
                    case 371:
                        msg.writer().writeByte(88);
                        msg.writer().writeShort(10);
                        break;
                    default:
                        msg.writer().writeByte(88);
                        msg.writer().writeShort(5);
                        break;
                }
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void changeFlag(Player pl, int index) {
        Message msg;
        try {
            pl.cFlag = (byte) index;
            msg = new Message(-103);
            msg.writer().writeByte(1);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeByte(index);
            Service.gI().sendMessAllPlayerInMap(pl, msg);
            msg.cleanup();

            msg = new Message(-103);
            msg.writer().writeByte(2);
            msg.writer().writeByte(index);
            msg.writer().writeShort(flagIconId[index]);
            Service.gI().sendMessAllPlayerInMap(pl, msg);
            msg.cleanup();

            if (pl.pet != null) {
                pl.pet.cFlag = (byte) index;
                msg = new Message(-103);
                msg.writer().writeByte(1);
                msg.writer().writeInt((int) pl.pet.id);
                msg.writer().writeByte(index);
                Service.gI().sendMessAllPlayerInMap(pl.pet, msg);
                msg.cleanup();

                msg = new Message(-103);
                msg.writer().writeByte(2);
                msg.writer().writeByte(index);
                msg.writer().writeShort(flagIconId[index]);
                Service.gI().sendMessAllPlayerInMap(pl.pet, msg);
                msg.cleanup();
            }
            pl.iDMark.setLastTimeChangeFlag(System.currentTimeMillis());
        } catch (Exception e) {
            Logger.logException(Service.class, e);
        }
    }

    public void sendFlagPlayerToMe(Player me, Player pl) {
        Message msg;
        try {
            msg = new Message(-103);
            msg.writer().writeByte(2);
            msg.writer().writeByte(pl.cFlag);
            msg.writer().writeShort(flagIconId[pl.cFlag]);
            me.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void chooseFlag(Player pl, int index) {
        if (MapService.gI().isMapBlackBallWar(pl.zone.map.mapId) || MapService.gI().isMapMaBu(pl.zone.map.mapId)
                || MapService.gI().isMapPVP(pl.zone.map.mapId)) {
            sendThongBao(pl, "Kh�ng Th? ???i C?? L�c N�y!");
            return;
        }
        if (Util.canDoWithTime(pl.iDMark.getLastTimeChangeFlag(), 60000)) {
            changeFlag(pl, index);
        } else {
            sendThongBao(pl, "Kh�ng th? ??i c?? l�c n�y! Vui l�ng ??i "
                    + TimeUtil.getTimeLeft(pl.iDMark.getLastTimeChangeFlag(), 60) + " n?a!");
        }
    }

    public void attackPlayer(Player pl, int idPlAnPem) {
        if (pl.zone != null) {
            SkillService.gI().useSkill(pl, pl.zone.getPlayerInMap(idPlAnPem), null, null);
        }
    }

    public void releaseCooldownSkill(Player pl) {
        Message msg;
        try {
            msg = new Message(-94);
            for (Skill skill : pl.playerSkill.skills) {
                skill.coolDown = 0;
                msg.writer().writeShort(skill.skillId);
                int leftTime = (int) (skill.lastTimeUseThisSkill + skill.coolDown - System.currentTimeMillis());
                if (leftTime < 0) {
                    leftTime = 0;
                }
                msg.writer().writeInt(leftTime);
            }
            pl.sendMessage(msg);
            pl.nPoint.setHp((long) pl.nPoint.hpMax);
            pl.nPoint.setMp((long) pl.nPoint.mpMax);
            PlayerService.gI().sendInfoHpMpMoney(pl);
            msg.cleanup();

        } catch (Exception e) {

        }
    }

    public void sendTimeSkill(Player pl) {
        Message msg;
        try {
            msg = new Message(-94);
            for (Skill skill : pl.playerSkill.skills) {
                msg.writer().writeShort(skill.skillId);
                int timeLeft = (int) (skill.lastTimeUseThisSkill + skill.coolDown - System.currentTimeMillis());
                if (timeLeft < 0) {
                    timeLeft = 0;
                }
                msg.writer().writeInt(timeLeft);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void dropItemMap(Zone zone, ItemMap item) {
        Message msg;
        try {
            msg = new Message(68);
            msg.writer().writeShort(item.itemMapId);
            msg.writer().writeShort(item.itemTemplate.id);
            msg.writer().writeShort(item.x);
            msg.writer().writeShort(item.y);
            msg.writer().writeInt(3);//
            sendMessAllPlayerInMap(zone, msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void dropItemMapForMe(Player player, ItemMap item) {
        Message msg;
        try {
            msg = new Message(68);
            msg.writer().writeShort(item.itemMapId);
            msg.writer().writeShort(item.itemTemplate.id);
            msg.writer().writeShort(item.x);
            msg.writer().writeShort(item.y);
            msg.writer().writeInt(3);//
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Service.class, e);
        }
    }

    public void showInfoPet(Player pl) {
        if (pl != null && pl.pet != null) {
            Message msg;
            try {
                msg = new Message(-107);
                msg.writer().writeByte(2);
                msg.writer().writeShort(pl.pet.getHead());
                msg.writer().writeByte(pl.pet.inventory.itemsBody.size());

                for (Item item : pl.pet.inventory.itemsBody) {
                    if (!item.isNotNullItem()) {
                        msg.writer().writeShort(-1);
                    } else {
                        msg.writer().writeShort(item.template.id);
                        msg.writer().writeInt(item.quantity);
                        msg.writer().writeUTF(item.getInfo());
                        msg.writer().writeUTF(item.getContent());

                        int countOption = item.itemOptions.size();
                        msg.writer().writeByte(countOption);
                        for (ItemOption iop : item.itemOptions) {
                            msg.writer().writeByte(iop.optionTemplate.id);
                            msg.writer().writeShort(iop.param);
                        }
                    }
                }

                msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.hp)); // hp
                msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.hpMax)); // hpfull
                msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.mp)); // mp
                msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.mpMax)); // mpfull
                msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.dame)); // damefull
                msg.writer().writeUTF(pl.pet.name); // name
                msg.writer().writeUTF(getCurrStrLevel(pl.pet)); // curr level
                msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.power)); // power
                msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.tiemNang)); // ti??m n?ng
                msg.writer().writeByte(pl.pet.getStatus()); // status
                msg.writer().writeShort(pl.pet.nPoint.stamina); // stamina
                msg.writer().writeShort(pl.pet.nPoint.maxStamina); // stamina full
                msg.writer().writeByte(pl.pet.nPoint.crit); // crit
                msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.def)); // def
                msg.writer().writeByte(4); // counnt pet skill
                for (int i = 0; i < pl.pet.playerSkill.skills.size(); i++) {
                    if (pl.pet.playerSkill.skills.get(i).skillId != -1) {
                        msg.writer().writeShort(pl.pet.playerSkill.skills.get(i).skillId);
                    } else {
                        switch (i) {
                            case 1:
                                msg.writer().writeShort(-1);
                                msg.writer().writeUTF("C?n ??t s?c m?nh 150tr ?? m?");
                                break;
                            case 2:
                                msg.writer().writeShort(-1);
                                msg.writer().writeUTF("C?n ??t s?c m?nh 1t?5 ?? m?");
                                break;
                            case 3:
                                msg.writer().writeShort(-1);
                                msg.writer().writeUTF("C?n ??t s?c m?nh 20t?\n?? m?");
                                break;
                            default:
                                msg.writer().writeShort(-1);
                                msg.writer().writeUTF("C?n ??t s?c m?nh 60t?\n?? m?");
                                break;
                        }
                    }
                }
                pl.sendMessage(msg);
                msg.cleanup();

            } catch (Exception e) {
                Logger.logException(Service.class, e);
            }
        }
    }

    public void showInfoPet1(Player pl) {
        if (pl != null && pl.pet != null) {
            Message msg;
            try {
                msg = new Message(-107);
                msg.writer().writeByte(2);
                msg.writer().writeShort(pl.pet.getHead());
                msg.writer().writeShort(pl.pet.getBody());
                msg.writer().writeShort(pl.pet.getLeg());
                msg.writer().writeByte(pl.pet.inventory.itemsBody.size());

                for (Item item : pl.pet.inventory.itemsBody) {
                    if (!item.isNotNullItem()) {
                        msg.writer().writeShort(-1);
                    } else {
                        msg.writer().writeShort(item.template.id);
                        msg.writer().writeInt(item.quantity);
                        msg.writer().writeUTF(item.getInfo());
                        msg.writer().writeUTF(item.getContent());

                        int countOption = item.itemOptions.size();
                        msg.writer().writeByte(countOption);
                        for (ItemOption iop : item.itemOptions) {
                            msg.writer().writeByte(iop.optionTemplate.id);
                            msg.writer().writeShort(iop.param);
                        }
                    }
                }

                // if (pl.getSession() != null && pl.getSession().version == 15) {// version
                // msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.hpg));
                // msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.mpg));
                // msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.dameg));
                // msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.defg));
                // msg.writer().writeInt(pl.pet.nPoint.critg);
                // }
                msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.hp)); // hp
                msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.hpMax)); // hpfull
                msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.mp)); // mp
                msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.mpMax)); // mpfull
                msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.dame)); // damefull
                msg.writer().writeUTF(pl.pet.name); // name
                msg.writer().writeUTF(getCurrStrLevel(pl.pet)); // curr level
                msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.power)); // power
                msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.tiemNang)); // ti??m n?ng
                msg.writer().writeByte(pl.pet.getStatus()); // status
                msg.writer().writeShort(pl.pet.nPoint.stamina); // stamina
                msg.writer().writeShort(pl.pet.nPoint.maxStamina); // stamina full
                msg.writer().writeByte(pl.pet.nPoint.crit); // crit
                msg.writeDouble(Dragon.utils.Util.limitDouble(pl.pet.nPoint.def)); // def
                int sizeSkill = pl.pet.playerSkill.skills.size();
                msg.writer().writeByte(5); // counnt pet skill
                for (int i = 0; i < pl.pet.playerSkill.skills.size(); i++) {
                    if (pl.pet.playerSkill.skills.get(i).skillId != -1) {
                        msg.writer().writeShort(pl.pet.playerSkill.skills.get(i).skillId);
                    } else {
                        switch (i) {
                            case 1:
                                msg.writer().writeShort(-1);
                                msg.writer().writeUTF("C?n ??t s?c m?nh 150tr ?? m?");
                                break;
                            case 2:
                                msg.writer().writeShort(-1);
                                msg.writer().writeUTF("C?n ??t s?c m?nh 1t?5 ?? m?");
                                break;
                            case 3:
                                msg.writer().writeShort(-1);
                                msg.writer().writeUTF("C?n ??t s?c m?nh 20t?\n?? m?");
                                break;
                            default:
                                msg.writer().writeShort(-1);
                                msg.writer().writeUTF("C?n ??t s?c m?nh 60t?\n?? m?");
                                break;
                        }
                    }
                }

                pl.sendMessage(msg);
                msg.cleanup();

            } catch (Exception e) {
                Logger.logException(Service.class, e);
            }
        }
    }

    public void sendSpeedPlayer(Player pl, int speed) {
        Message msg;
        try {
            msg = Service.gI().messageSubCommand((byte) 8);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeByte(speed != -1 ? speed : pl.nPoint.speed);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Service.class, e);
        }
    }

    public void setPos(Player player, int x, int y) {
        player.location.x = x;
        player.location.y = y;
        Message msg;
        try {
            msg = new Message(123);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            msg.writer().writeByte(1);
            sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void getPlayerMenu(Player player, int playerId) {
        Message msg;
        try {
            msg = new Message(-79);
            Player pl = player.zone.getPlayerInMap(playerId);
            if (pl != null) {
                msg.writer().writeInt(playerId);
                msg.writeDouble(Util.limitDouble(pl.nPoint.power));
                msg.writer().writeUTF(Service.gI().getCurrStrLevel(pl));
                player.sendMessage(msg);
            }
            msg.cleanup();
            if (player.isAdmin()) {
                SubMenuService.gI().showMenuForAdmin(player);
            }
        } catch (Exception e) {
            Logger.logException(Service.class, e);
        }
    }

    public void hideWaitDialog(Player pl) {
        Message msg;
        try {
            msg = new Message(-99);
            msg.writer().writeByte(-1);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void chatPrivate(Player plChat, Player plReceive, String text) {
        Message msg;
        try {
            msg = new Message(92);
            msg.writer().writeUTF(plChat.name);
            msg.writer().writeUTF("|7|" + text);
            msg.writer().writeInt((int) plChat.id);
            msg.writer().writeShort(plChat.getHead());
            msg.writer().writeShort(-1);
            msg.writer().writeShort(plChat.getBody());
            msg.writer().writeShort(plChat.getFlagBag()); // bag
            msg.writer().writeShort(plChat.getLeg());
            msg.writer().writeByte(1);
            plChat.sendMessage(msg);
            plReceive.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void changePassword(Player player, String oldPass, String newPass, String rePass) {
        if (player.getSession().pp.equals(oldPass)) {
            if (newPass.length() >= 5) {
                if (newPass.equals(rePass)) {
                    player.getSession().pp = newPass;
                    try {
                        GirlkunDB.executeUpdate("update account set password = ? where id = ? and username = ?",
                                rePass, player.getSession().userId, player.getSession().uu);
                        Service.gI().sendThongBao(player, "???i M?t Kh?u Th�nh C�ng!");
                    } catch (Exception ex) {
                        Service.gI().sendThongBao(player, "???i M?t Kh?u Th?t B?i!");
                        Logger.logException(Service.class, ex);
                    }
                } else {
                    Service.gI().sendThongBao(player, "M?t Kh?u Nh?p L?i Kh�ng ??�ng!");
                }
            } else {
                Service.gI().sendThongBao(player, "M?t Kh?u It Nh?t 5 K� T?!");
            }
        } else {
            Service.gI().sendThongBao(player, "M?t Kh?u C? Kh�ng ??�ng!");
        }
    }

    public void switchToCreateChar(MySession session) {
        Message msg;
        try {
            msg = new Message(2);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void sendCaption(MySession session, byte gender) {
        Message msg;
        try {
            msg = new Message(-41);
            msg.writer().writeByte(Manager.CAPTIONS.size());
            for (String caption : Manager.CAPTIONS) {
                msg.writer().writeUTF(caption.replaceAll("%1", gender == ConstPlayer.TRAI_DAT ? "Tr�i ??t"
                        : (gender == ConstPlayer.NAMEC ? "Nam?c" : "Xayda")));
            }
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void sendHavePet(Player player) {
        Message msg;
        try {
            msg = new Message(-107);
            msg.writer().writeByte(player.pet == null ? 0 : 1);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void sendWaitToLogin(MySession session, int secondsWait) {
        Message msg;
        try {
            msg = new Message(122);
            msg.writer().writeShort(secondsWait);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Service.class, e);
        }
    }

    public void sendMessage(MySession session, int cmd, String path) {
        Message msg;
        try {
            msg = new Message(cmd);
            msg.writer().write(FileIO.readFile(path));
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void createItemMap(Player player, int tempId) {
        ItemMap itemMap = new ItemMap(player.zone, tempId, 1, player.location.x, player.location.y, player.id);
        dropItemMap(player.zone, itemMap);
    }

    public void sendNangDong(Player player) {
        Message msg;
        try {
            msg = new Message(-97);
            msg.writer().writeInt(100);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void setClientType(MySession session, Message msg) {
        try {
            session.typeClient = (msg.reader().readByte());// client_type
            session.zoomLevel = msg.reader().readByte();// zoom_level
            msg.reader().readBoolean();// is_gprs
            msg.reader().readInt();// width
            msg.reader().readInt();// height
            msg.reader().readBoolean();// is_qwerty
            msg.reader().readBoolean();// is_touch
            String platform = msg.reader().readUTF();
            String[] arrPlatform = platform.split("\\|");
            session.version = Integer.parseInt(arrPlatform[1].replaceAll("\\.", ""));
            System.out.println("version: " + session.version);
            // System.out.println(platform);
        } catch (Exception e) {
        } finally {
            msg.cleanup();
        }
        DataGame.sendLinkIP(session);
    }

    public void DropVeTinh(Player pl, Item item, Zone map, int x, int y) {
        ItemMap itemMap = new ItemMap(map, item.template, item.quantity, x, y, pl.id);
        itemMap.options = item.itemOptions;
        map.addItem(itemMap);
        Message msg = null;
        try {
            msg = new Message(68);
            msg.writer().writeShort(itemMap.itemMapId);
            msg.writer().writeShort(itemMap.itemTemplate.id);
            msg.writer().writeShort(itemMap.x);
            msg.writer().writeShort(itemMap.y);
            msg.writer().writeInt(-2);
            msg.writer().writeShort(200);
            sendMessAllPlayerInMap(map, msg);
        } catch (Exception e) {

        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void stealMoney(Player pl, int stealMoney) {// danh cho boss an trom
        Message msg;
        try {
            msg = new Message(95);
            msg.writer().writeInt(stealMoney);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    /**
     * Send voice chat zone information to player
     */
    public void sendVoiceChatZoneInfo(Player player, int zoneId, int playerCount) {
        Message msg;
        try {
            msg = new Message(200); // Custom message ID for voice chat zone info
            msg.writer().writeInt(zoneId);
            msg.writer().writeInt(playerCount);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Service.class, e);
        }
    }

}
