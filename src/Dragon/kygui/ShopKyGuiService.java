package Dragon.kygui;

import Dragon.models.player.Player;
import Dragon.services.Service;

public class ShopKyGuiService {

    private static ShopKyGuiService instance;

    public static ShopKyGuiService gI() {
        if (instance == null) {
            instance = new ShopKyGuiService();
        }
        return instance;
    }

    public void KiGui(Player player, short idItem, int ruby, byte rubyType, int quantity) {
        Service.gI().sendThongBao(player, "Tính năng ký gửi đang bảo trì");
    }

    public void claimOrDel(Player player, byte action, short idItem) {
        Service.gI().sendThongBao(player, "Tính năng ký gửi đang bảo trì");
    }

    public void buyItem(Player player, short idItem) {
        Service.gI().sendThongBao(player, "Tính năng ký gửi đang bảo trì");
    }

    public void openShopKyGui(Player player, byte rubyType, int ruby) {
        Service.gI().sendThongBao(player, "Tính năng ký gửi đang bảo trì");
    }

    public void upItemToTop(Player player, short idItem) {
        Service.gI().sendThongBao(player, "Tính năng ký gửi đang bảo trì");
    }

    public byte getTabKiGui(Object item) {
        return 0; // Default tab
    }
}
