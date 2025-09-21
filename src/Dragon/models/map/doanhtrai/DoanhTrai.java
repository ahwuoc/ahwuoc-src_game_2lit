package Dragon.models.map.doanhtrai;

import Dragon.models.clan.Clan;
import Dragon.models.map.Zone;
import Dragon.models.mob.Mob;
import Dragon.models.player.Player;
import Dragon.services.ItemTimeService;
import Dragon.services.func.ChangeMapService;
import Dragon.utils.Util;
import java.util.ArrayList;
import java.util.List;

public class DoanhTrai {

    //bang hội đủ số người mới đc mở
    public static final List<DoanhTrai> DOANH_TRAI;
    public static final int N_PLAYER_CLAN = 0;
    //số người đứng cùng khu
    public static final int N_PLAYER_MAP = 0;
    public static final int AVAILABLE = 150;
    public static final int TIME_DOANH_TRAI = 18000;

    static {
        DOANH_TRAI = new ArrayList<>();
        for (int i = 0; i < AVAILABLE; i++) {
            DOANH_TRAI.add(new DoanhTrai(i));
        }
    }

    private int id;
    private List<Zone> zones;
    private Clan clan;
    public boolean isOpened;

    private long lastTimeOpen;

    public DoanhTrai(int id) {
        this.id = id;
        this.zones = new ArrayList<>();
    }

    public void addZone(Zone zone) {
        this.zones.add(zone);
    }

    public Zone getMapById(int mapId) {
        for (Zone zone : this.zones) {
            if (zone.map.mapId == mapId) {
                return zone;
            }
        }
        return null;
    }

    public void openDoanhTrai(Player player) {
        this.lastTimeOpen = System.currentTimeMillis();
        this.clan = player.clan;
        player.clan.doanhTrai = this;
        player.clan.playerOpenDoanhTrai = player.name;
        player.clan.lastTimeOpenDoanhTrai = this.lastTimeOpen;
        player.clan.timeOpenDoanhTrai = this.lastTimeOpen;
        //Khởi tạo quái, boss
        this.init();
        //Đưa thành viên vào doanh trại
        for (Player pl : player.clan.membersInGame) {
            if (pl == null || pl.zone == null || !player.zone.equals(pl.zone)) {
                continue;
            }
            ChangeMapService.gI().changeMapInYard(pl, 999, -1, 60);//Phước Map Doanh Trại
            ItemTimeService.gI().sendTextDoanhTrai(pl);
        }
    }

    private void init() {
        long totalDame = 0;
        long totalHp = 0;
        for (Player pl : this.clan.membersInGame) {
            totalDame += pl.nPoint.dame;
            totalHp += pl.nPoint.hpMax;
        }

        //Hồi sinh quái
        for (Zone zone : this.zones) {
            for (Mob mob : zone.mobs) {
                mob.point.dame = Util.trum(totalHp / 20);
                mob.point.maxHp = Util.trum(totalDame * 20);
                mob.hoiSinh();
            }
        }
    }

    private void sendTextDoanhTrai() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().sendTextDoanhTrai(pl);
        }
    }

    // Getters/Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public long getLastTimeOpen() {
        return lastTimeOpen;
    }

    public void setLastTimeOpen(long lastTimeOpen) {
        this.lastTimeOpen = lastTimeOpen;
    }
}
