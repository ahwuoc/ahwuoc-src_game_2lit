package Dragon.models.map.nguhanhson;

import Dragon.models.clan.Clan;
import Dragon.models.map.Zone;
import Dragon.models.mob.Mob;
import Dragon.models.player.Player;
import Dragon.services.MapService;
import Dragon.services.Service;
import Dragon.services.func.ChangeMapService;
import Dragon.utils.TimeUtil;
import Dragon.utils.Util;
import java.util.ArrayList;
import java.util.List;

public class nguhs {

    public static final byte HOUR_OPEN_MAP_NHS = 0;
    public static final byte MIN_OPEN_MAP_NHS = 0;
    public static final byte SECOND_OPEN_MAP_NHS = 0;

    public static final byte HOUR_CLOSE_MAP_NHS = 23;
    public static final byte MIN_CLOSE_MAP_NHS = 59;
    public static final byte SECOND_CLOSE_MAP_NHS = 59;

    public static final int AVAILABLE = 7;

    private static nguhs i;

    public static long TIME_OPEN_NHS;
    public static long TIME_CLOSE_NHS;

    private int day = -1;

    public static nguhs gI() {
        if (i == null) {
            i = new nguhs();
        }
        i.setTimeJoinnguhs();
        return i;
    }

    public void setTimeJoinnguhs() {
        if (i.day == -1 || i.day != TimeUtil.getCurrDay()) {
            i.day = TimeUtil.getCurrDay();
            try {
                TIME_OPEN_NHS = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_OPEN_MAP_NHS + ":" + MIN_OPEN_MAP_NHS + ":" + SECOND_OPEN_MAP_NHS, "dd/MM/yyyy HH:mm:ss");
                TIME_CLOSE_NHS = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_CLOSE_MAP_NHS + ":" + MIN_CLOSE_MAP_NHS + ":" + SECOND_CLOSE_MAP_NHS, "dd/MM/yyyy HH:mm:ss");
            } catch (Exception ignored) {
            }
        }
    }

    private void kickOutOfnguhs(Player player) {
        if (MapService.gI().isnguhs(player.zone.map.mapId)) {
            Service.getInstance().sendThongBao(player, "Hết thời gian rồi, tàu vận chuyển sẽ đưa bạn về nhà");
            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
        }
    }

    private void ketthucnguhs(Player player) {
        player.zone.finishnguhs = true;
        List<Player> playersMap = player.zone.getPlayers();
        for (int i = playersMap.size() - 1; i >= 0; i--) {
            Player pl = playersMap.get(i);
            kickOutOfnguhs(pl);
        }
    }

    public void joinnguhs(Player player) {
        boolean changed = false;
        if (player.clan != null) {
            List<Player> players = player.zone.getPlayers();
            for (Player pl : players) {
                if (pl.clan != null && !player.equals(pl) && player.clan.equals(pl.clan) && !player.isBoss) {
                    Service.getInstance().changeFlag(player, 8);
                    changed = true;
                    break;
                }
            }
        }
        if (!changed && !player.isBoss) {
            Service.getInstance().changeFlag(player, 8);
        }
    }

    public void update(Player player) {
        try {
            long now = System.currentTimeMillis();
            if (!(now > TIME_OPEN_NHS && now < TIME_CLOSE_NHS) && MapService.gI().isnguhs(player.zone.map.mapId)) {

                ketthucnguhs(player);

            }
        } catch (Exception ex) {
        }
    }

}
