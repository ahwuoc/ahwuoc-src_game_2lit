//Phước Chuyển Sinh VIP
package Dragon.services;

import Dragon.models.item.Item;
import Dragon.models.player.NPoint;
import Dragon.models.player.Pet;
import Dragon.models.player.Player;
import Dragon.server.Client;
import java.awt.Point;

public class OpenPowerService {

    public static final long COST_SPEED_OPEN_LIMIT_POWER = 1000000000L;

    private static OpenPowerService i;

    private OpenPowerService() {

    }

    public static OpenPowerService gI() {
        if (i == null) {
            i = new OpenPowerService();
        }
        return i;
    }

    public boolean openPowerBasic(Player player) {
        byte curLimit = player.nPoint.limitPower;
        if (curLimit < NPoint.MAX_LIMIT) {
            if (!player.itemTime.isOpenPower && player.nPoint.canOpenPower()) {
                player.itemTime.isOpenPower = true;
                player.itemTime.lastTimeOpenPower = System.currentTimeMillis();
                ItemTimeService.gI().sendAllItemTime(player);
                return true;
            } else {
                Service.gI().sendThongBao(player, "Sức Mạnh Của Bạn Không Đủ Để Thực Hiện");
                return false;
            }
        } else {
            Service.gI().sendThongBao(player, "Sức Mạnh Của Bạn Đã Đạt Tới Mức Tối Đa");
            return false;
        }
    }

    public boolean chuyenSinh(Player player) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 0) {
            Service.gI().sendThongBao(player, "Hành Trang Không Đủ Chỗ Trống");
            return false;
        }

        if (player.nPoint.power < 200L) {
            if (!player.isPet) {
                Service.gI().sendThongBao(player, "Để Chuyển Sinh Cần Sức Mạnh 200");
            } else {
                Service.gI().sendThongBao(((Pet) player).master, "Bạn Không Đủ Điều Kiện Để Chuyển Sinh");
            }
            return false;
        }

        // Kiểm tra vàng
        if (player.inventory.gold < 5000) {
            Service.gI().sendThongBao(player, "Cần 5.000 vàng để Chuyển Sinh");
            return false;
        }

        // Trừ vàng
        player.inventory.gold -= 5000;
        Service.gI().sendMoney(player);

        // Reset sức mạnh và tăng chỉ số
        player.nPoint.power = 1000;
        player.ChuyenSinh++;
        player.nPoint.hpg += 100;
        player.nPoint.dameg += 10;
        player.nPoint.mpg += 100;
        Service.getInstance().point(player);

        Client.gI().kickSession(player.getSession());

        if (!player.isPet) {
            Service.gI().sendThongBao(player, "Bạn Đã Được Chuyển Sinh");
        } else {
            Service.gI().sendThongBao(((Pet) player).master, "Giới hạn sức mạnh của đệ tử đã được reset");
        }

        return true;
    }

    public boolean openPowerSpeed(Player player) {
        if (player.nPoint.limitPower < NPoint.MAX_LIMIT) {
//            if (player.nPoint.power >= 17900000000L) {
            player.nPoint.limitPower++;
            if (player.nPoint.limitPower > NPoint.MAX_LIMIT) {
                player.nPoint.limitPower = NPoint.MAX_LIMIT;
            }
            if (!player.isPet) {
                Service.gI().sendThongBao(player, "Giới Hạn Sức Mạnh Của Bạn Đã Được Tăng Lên 1 Bậc");
            } else {
                Service.gI().sendThongBao(((Pet) player).master, "Giới Hạn Sức Mạnh Của Đệ Tử Đã Được Tăng Lên 1 Bậc");
            }
            return true;
//            } else {
//                if (!player.isPet) {
//                    Service.gI().sendThongBao(player, "Sức mạnh của bạn không đủ để thực hiện");
//                } else {
//                    Service.gI().sendThongBao(((Pet) player).master, "Sức mạnh của đệ tử không đủ để thực hiện");
//                }
//                return false;
//            }
        } else {
            if (!player.isPet) {
                Service.gI().sendThongBao(player, "Sức Mạnh Của Bạn Đã Đạt Tới Mức Tối Đa");
            } else {
                Service.gI().sendThongBao(((Pet) player).master, "Sức Mạnh Của Đệ Tử Đã Đạt Tới Mức Tối Đa");
            }
            return false;
        }
    }

}
