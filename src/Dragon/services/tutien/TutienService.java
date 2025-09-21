package Dragon.services.tutien;

import Dragon.consts.ConstNpc;
import Dragon.models.npc.Npc;
import Dragon.models.player.Player;

/**
 * Service xử lý UI và tương tác cho hệ thống Tu Tiên
 */
public class TutienService {

    private static TutienService instance;

    public static TutienService gI() {
        if (instance == null) {
            instance = new TutienService();
        }
        return instance;
    }

    /**
     * Lấy hiển thị cấp bậc Tu Tiên rõ ràng
     */
    private String getTutienLevelDisplay(Player player) {
        // Hiển thị cấp tu tiên và EXP
        return "Cấp " + player.Captutien + " (EXP: " + player.Exptutien + ")";
    }

    /**
     * Hiển thị menu chính
     */
    public void showMainMenu(Npc npc, Player player) {
        String levelDisplay = getTutienLevelDisplay(player);
        String perLevelBonusInfo = "Mỗi cấp: Dame +1,000 | HP +20,000 | Ki +20,000";

        // Menu với thông tin tu tiên và đột phá cấp bậc
        npc.createOtherMenu(player, ConstNpc.BASE_MENU,
                "|7|Xin Chào Cư Dân \n" + "Cấp Tu Tiên Của Bạn Đang Là : " + levelDisplay + "\n"
                + "\n|3|" + perLevelBonusInfo + "\n",
                "Xem thông tin\ntu tiên", "Đột phá cấp bậc", "Đóng");
    }

    public void showTuTienInfoMenu(Npc npc, Player player) {
        long expNeededForNextLevel = TutienCalculator.getExpNeededForNextLevel(player.Captutien);
        double successRate = Math.min(player.Exptutien / (double) expNeededForNextLevel, 1.0) * 100.0;

        String currentRankName = TutienRealm.getFullRankName(player.Captutien);

        String currentBonusInfo = TutienCalculator.getShortBonusInfo(player.Captutien);
        String perLevelBonusInfo = "Mỗi cấp: Dame +1,000 | HP +20,000 | Ki +20,000";

        String info = "|7|Thông tin Tu Tiên\n"
                + "Cấp bậc hiện tại: " + currentRankName + " (Cấp " + player.Captutien + ")\n"
                + "EXP tu tiên: " + player.Exptutien + "\n"
                + "EXP cần để lên cấp: " + expNeededForNextLevel + "\n"
                + "Tỷ lệ thành công: " + String.format("%.1f", successRate) + "%\n"
                + "\n|2|Bonus hiện tại:\n" + currentBonusInfo + "\n"
                + "\n|3|" + perLevelBonusInfo + "\n"
                + "\n|2|Đan có thể sử dụng:\n" + getUsableItemsInfo(player);
        showSubMenu(npc, player, info);
    }

    /**
     * Hiển thị menu phụ (sub menu)
     */
    private void showSubMenu(Npc npc, Player player, String content) {
        // Menu với thông tin tu tiên và đột phá cấp bậc
        npc.createOtherMenu(player, ConstNpc.BASE_MENU, content,
                "Xem thông tin\ntu tiên", "Đột phá cấp bậc", "Đóng");
    }

    /**
     * Xử lý đột phá cấp bậc
     */
    public void handleDotPhaCapBac(Player player) {
        TutienResult result = TutienManager.gI().dotPhaCapBac(player);
        Dragon.services.Service.gI().sendThongBao(player, result.getMessage());
    }

    /**
     * Xử lý xem thông tin Tu Tiên
     */
    public void handleViewTuTienInfo(Npc npc, Player player) {
        showTuTienInfoMenu(npc, player);
    }

    /**
     * Lấy thông tin đan có thể sử dụng ở cấp hiện tại
     */
    private String getUsableItemsInfo(Player player) {
        StringBuilder info = new StringBuilder();
        int currentLevel = player.Captutien;

        // Danh sách tất cả đan và cấp yêu cầu
        int[][] danInfo = {
            {TutienConstants.ItemIds.DO_KHI_DAN, 0, 9, 1000},
            {TutienConstants.ItemIds.DO_GIA_DAN, 10, 19, 2000},
            {TutienConstants.ItemIds.DO_SU_DAN, 20, 29, 3000},
            {TutienConstants.ItemIds.DAI_DO_SU_DAN, 30, 39, 4000},
            {TutienConstants.ItemIds.DO_LINH_DAN, 40, 49, 5000},
            {TutienConstants.ItemIds.DO_VUONG_DAN, 50, 59, 6000},
            {TutienConstants.ItemIds.DO_HOANG_DAN, 60, 69, 7000},
            {TutienConstants.ItemIds.DO_TONG_DAN, 70, 79, 8000},
            {TutienConstants.ItemIds.DO_TON_DAN, 80, 89, 9000},
            {TutienConstants.ItemIds.DO_THANH_DAN, 90, 95, 10000},
            {TutienConstants.ItemIds.DO_DE_DAN, 96, 96, 15000}
        };

        boolean hasUsableItem = false;

        for (int i = 0; i < danInfo.length; i++) {
            int itemId = danInfo[i][0];
            int minLevel = danInfo[i][1];
            int maxLevel = danInfo[i][2];
            int fixedExp = danInfo[i][3];

            if (currentLevel >= minLevel && currentLevel <= maxLevel) {
                if (hasUsableItem) {
                    info.append("\n");
                }

                // Lấy tên từ item template
                String itemName = TutienCalculator.getItemNameById(itemId);

                info.append("|2|").append(itemName)
                        .append(" (Cấp ").append(minLevel).append("-").append(maxLevel).append(")\n")
                        .append("|7|EXP: ").append(fixedExp);
                hasUsableItem = true;
            }
        }

        if (!hasUsableItem) {
            info.append("|1|Chưa có đan nào phù hợp với cấp hiện tại");
        }

        return info.toString();
    }

    /**
     * Xử lý menu selection
     */
    public void handleMenuSelection(Npc npc, Player player, int select) {
        // Menu: [Xem thông tin tu tiên] [Đột phá cấp bậc] [Đóng]
        switch (select) {
            case 0:
                handleViewTuTienInfo(npc, player);
                break;
            case 1:
                handleDotPhaCapBac(player);
                break;
            case 2:
                // Đóng menu
                break;
        }
    }
}
