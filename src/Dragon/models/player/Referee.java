package Dragon.models.player;

import Dragon.models.shop.ShopServiceNew;
import Dragon.services.MapService;
import Dragon.consts.ConstMap;
import Dragon.models.map.Map;
import Dragon.models.map.Zone;
import Dragon.server.Manager;
import Dragon.services.MapService;
import Dragon.services.PlayerService;
import Dragon.services.Service;
import Dragon.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class Referee extends Player {

    private long lastTimeChat;
    private Player playerTarget;

    private long lastTimeTargetPlayer;
    private long timeTargetPlayer = 10000;
    private long lastZoneSwitchTime;
    private long zoneSwitchInterval;
    private List<Zone> availableZones;

    public void initReferee() {
        init();
    }

    @Override
    public short getHead() {
        return 1294;
    }

    @Override
    public short getBody() {
        return 1295;
    }

    @Override
    public short getLeg() {
        return 1296;
    }

    public void joinMap(Zone z, Player player) {
        MapService.gI().goToMap(player, z);
        z.load_Me_To_Another(player);
    }

    @Override
    public void update() {
        if (Util.canDoWithTime(lastTimeChat, 5000)) {
            Service.getInstance().chat(this, "Đại Hội Võ Thuật lần thứ 23 đã chính thức khai mạc");
            Service.getInstance().chat(this, "Còn chờ gì nữa mà không đăng kí tham gia để nhận nhiều phẩn quà hấp dẫn");
            lastTimeChat = System.currentTimeMillis();
        }
    }

    private void init() {
        int id = -1000000;
        for (Map m : Manager.MAPS) {
            if (m.mapId == 52) {
                for (Zone z : m.zones) {
                    Referee pl = new Referee();
                    pl.name = "Trọng Tài";
                    pl.gender = 0;
                    pl.id = id++;
                    pl.nPoint.hpMax = 200000000000L;
                    pl.nPoint.hpg = 200000000000L;
                    pl.nPoint.hp = 200000000000L;
                    pl.nPoint.setFullHpMpDame();
                    pl.location.x = 387;
                    pl.location.y = 336;
                    joinMap(z, pl);
                    pl.typePk = 5;
                    z.setReferee(pl);
                    z.update();
                }
            } else if (m.mapId == 129) {
                for (Zone z : m.zones) {
                    Referee pl = new Referee();
                    pl.name = "Trọng Tài";
                    pl.gender = 0;
                    pl.id = id++;
                    pl.nPoint.hpMax = 69;
                    pl.nPoint.hpg = 69;
                    pl.nPoint.hp = 69;
                    pl.nPoint.setFullHpMpDame();
                    pl.location.x = 385;
                    pl.location.y = 264;
                    joinMap(z, pl);
                    z.setReferee(pl);
                }
            }
        }
    }
}
