package Dragon.models.map.gas;

import Dragon.models.boss.BossID;
//import Dragon.models.boss.bdkb.TrungUyXanhLo;
import Dragon.models.boss.list_boss.gas.DrLyChee;
import Dragon.models.boss.list_boss.gas.HaChiJack;
import Dragon.models.item.Item;
//import static Dragon.models.map.bando.BanDoKhoBau.TIME_BAN_DO_KHO_BAU;
import static Dragon.models.map.gas.Gas.TIME_KHI_GAS;
import Dragon.models.mob.Mob;
import Dragon.models.player.Player;
import Dragon.services.InventoryServiceNew;
import Dragon.services.MapService;
import Dragon.services.Service;
import Dragon.services.func.ChangeMapService;
import Dragon.utils.Logger;
import Dragon.utils.Util;
import java.util.List;

/**
 *
 * @author Trum
 *
 */
public class GasService {

    public static GasService i;

    public GasService() {

    }

    public static GasService gI() {
        if (i == null) {
            i = new GasService();
        }
        return i;
    }

    private int MobinMap(Player pl) {
        int mob = 0;
        for (Mob m : pl.zone.mobs) {
            if (m.status != 0 && m.status != 1) {
                mob += 1;
            }
        }
        return mob;
    }

    public void update(Player player) {

        if (player.isPl() == true && player.clan.khiGas != null && player.clan.timeOpenKhiGas != 0) {
            if (player.zone.map.mapId == 148 && MobinMap(player) == 0 && !player.clan.khiGas.isInitBoss) {
                player.clan.khiGas.InitBoss(player);
                player.clan.khiGas.isInitBoss = true;
            }
            if (Util.canDoWithTime(player.clan.timeOpenKhiGas, TIME_KHI_GAS)) {
                ketthucGas(player);
                player.clan.khiGas = null;
            }
        }
    }

    private void kickOutOfGas(Player player) {
        if (MapService.gI().isMapKhiGas(player.zone.map.mapId)) {
            Service.gI().sendThongBao(player, "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
        }
    }

    private void ketthucGas(Player player) {
        List<Player> playersMap = player.zone.getPlayers();
        for (int i = playersMap.size() - 1; i >= 0; i--) {
            Player pl = playersMap.get(i);
            kickOutOfGas(pl);
        }
    }

    public void openGas(Player player, int level) {
        if (level >= 1 && level <= 100) {
            if (player.clan != null && player.clan.khiGas == null) {
                if (player.clan.SoLanDiKhiGas < 3) {
                    Gas gas = null;
                    for (Gas gasz : Gas.KHI_GAS) {
                        if (!gasz.isOpened) {
                            gas = gasz;
                            break;
                        }
                    }
                    if (gas != null) {
                        player.clan.SoLanDiKhiGas += 1;
                        gas.openGas(player, player.clan, level);
                    } else {
                        Service.getInstance().sendThongBao(player, "Khí gas hủy diệt hiện tại đang quá đông, vui lòng quay lại sau");
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Tuần này Bang hội của bạn đã đi 3 lần rồi , hãy đi vào tuần sau");
                }
            } else {
                Service.getInstance().sendThongBao(player, "Cấp độ cao nhất là 100");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Cấp độ cao nhất là 100");
        }
    }
}
