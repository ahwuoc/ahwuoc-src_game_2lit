package Dragon.services.tutien;

import Dragon.models.player.Player;
import Dragon.models.item.Item;
import Dragon.services.Service;
import Dragon.services.InventoryServiceNew;

/**
 * Manager quản lý việc sử dụng item Tu Tiên
 */
public class TutienItemManager {

    private static TutienItemManager instance;

    public static TutienItemManager gI() {
        if (instance == null) {
            instance = new TutienItemManager();
        }
        return instance;
    }

    /**
     * Xử lý sử dụng item Tu Tiên
     */
    public void useTutienItem(Player player, Item item) {
        if (!TutienConstants.Config.ALLOW_TUTIEN_LEVELING) {
            Service.gI().sendThongBao(player, "Hệ thống tu tiên đã được tắt!");
            return;
        }

        if (item == null) {
            Service.gI().sendThongBao(player, "Item không tồn tại!");
            return;
        }

        if (item.template == null) {
            Service.gI().sendThongBao(player, "Item không có template!");
            return;
        }

        if (!isTutienItem(item.template.id)) {
            Service.gI().sendThongBao(player, "Đây không phải là đan tu tiên!");
            return;
        }
        if (!TutienCalculator.canUseItemAtLevel(item.template.id, player.Captutien)) {
            String itemName = TutienCalculator.getItemNameById(item.template.id);
            String requiredLevels = getRequiredLevelsForItem(item.template.id);
            Service.gI().sendThongBao(player,
                    itemName + " chỉ dành cho cấp " + requiredLevels + "!\nCấp hiện tại: " + player.Captutien);
            return;
        }

        long expGained = TutienCalculator.calculateExpFromItem(player, item.template.id, 1);
        player.Exptutien += expGained;
        // Thông báo
        String itemName = TutienCalculator.getItemNameById(item.template.id);
        Service.gI().sendThongBao(player,
                "Bạn nhận được: " + expGained + " Exp Tu Tiên từ " + itemName + ".");
        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        InventoryServiceNew.gI().sendItemBags(player);

    }

    /**
     * Lấy cấp yêu cầu cho item
     */
    private String getRequiredLevelsForItem(int itemId) {
        switch (itemId) {
            case TutienConstants.ItemIds.DO_KHI_DAN:
                return "0-9";
            case TutienConstants.ItemIds.DO_GIA_DAN:
                return "10-19";
            case TutienConstants.ItemIds.DO_SU_DAN:
                return "20-29";
            case TutienConstants.ItemIds.DAI_DO_SU_DAN:
                return "30-39";
            case TutienConstants.ItemIds.DO_LINH_DAN:
                return "40-49";
            case TutienConstants.ItemIds.DO_VUONG_DAN:
                return "50-59";
            case TutienConstants.ItemIds.DO_HOANG_DAN:
                return "60-69";
            case TutienConstants.ItemIds.DO_TONG_DAN:
                return "70-79";
            case TutienConstants.ItemIds.DO_TON_DAN:
                return "80-89";
            case TutienConstants.ItemIds.DO_THANH_DAN:
                return "90-95";
            case TutienConstants.ItemIds.DO_DE_DAN:
                return "96";
            default:
                return "Unknown";
        }
    }

    public boolean isTutienItem(int itemId) {
        return itemId >= TutienConstants.ItemIds.DO_KHI_DAN
                && itemId <= TutienConstants.ItemIds.DO_DE_DAN;
    }
}
