package Dragon.services.tutien;

import Dragon.models.player.Player;
import Dragon.utils.Util;
import Dragon.services.Service;

/**
 * Manager quản lý hệ thống Tu Tiên
 */
public class TutienManager {

    private static TutienManager instance;

    public static TutienManager gI() {
        if (instance == null) {
            instance = new TutienManager();
        }
        return instance;
    }

    /**
     * Kiểm tra có thể mở thiên phú không
     */
    public boolean canOpenThienPhu(Player player) {
        if (player.isAdmin()) {
            return true; // Admin luôn có thể mở
        }

        return player.nPoint.power >= 100000000000L;
        // 100 tỷ sức mạnh
    }

    public boolean canDotPhaCapBac(Player player) {
        // Chỉ cần có EXP tu tiên để đột phá, không cần cấp cụ thể
        // Kiểm tra cả EXP > 0 và chưa đạt cấp tối đa
        return player.Exptutien > 0 && player.Captutien < TutienConstants.MAX_LEVEL;
    }

    /**
     * Lấy thông báo chi tiết về điều kiện đột phá
     */
    public String getDotPhaCapBacConditionMessage(Player player) {
        if (player.Exptutien <= 0) {
            return "Cần có EXP tu tiên để đột phá cấp bậc";
        }

        if (player.Captutien >= TutienConstants.MAX_LEVEL) {
            return "Đã đạt cấp tu tiên tối đa (" + TutienConstants.MAX_LEVEL + ")";
        }

        return "Đủ điều kiện đột phá cấp bậc";
    }

    /**
     * Mở thiên phú
     */
    public TutienResult openThienPhu(Player player) {
        if (!canOpenThienPhu(player)) {
            return TutienResult.failure("Không đủ điều kiện mở thiên phú\nCần đạt 100 tỷ sức mạnh");
        }

        boolean isAdmin = player.isAdmin();
        // Random thiên phú
        int tp = Util.nextInt(1, Util.nextInt(1, Util.nextInt(1, 8)));
        player.TUTIEN[2] = tp;

        String message = isAdmin
                ? "|2|Admin mở thiên phú thành công:\n" + tp + " sao (miễn phí)"
                : "Chúc mừng con con mở đc thiên phú:\n" + tp + " sao\n(Điều kiện: 100 tỷ sức mạnh)";

        return TutienResult.success(message);
    }

    /**
     * Đột phá cấp bậc tu tiên
     */
    public TutienResult dotPhaCapBac(Player player) {
        if (!canDotPhaCapBac(player)) {
            return TutienResult
                    .failure("Không đủ điều kiện đột phá cấp bậc\n" + getDotPhaCapBacConditionMessage(player));
        }

        // Kiểm tra đã đạt cấp tối đa chưa
        if (player.Captutien >= TutienConstants.MAX_LEVEL) {
            return TutienResult
                    .failure("Đã đạt cấp tu tiên tối đa (" + TutienConstants.MAX_LEVEL + ")");
        }

        long expNeededForNextLevel = TutienCalculator.getExpNeededForNextLevel(player.Captutien);
        double expRatio = Math.min(player.Exptutien / (double) expNeededForNextLevel, 1.0);
        double successRate = expRatio * 100.0; // Từ 0% đến 100%

        if (Util.isTrue((float) successRate, 100)) {
            // Thực hiện đột phá
            player.Exptutien = 0;
            player.Captutien++;

            // Cập nhật thông tin player sau khi đột phá
            Service.gI().point(player);
            Service.gI().Send_Caitrang(player);

            // Thông báo thành công
            String message = "Chúc mừng con đã đột phá cấp bậc thành công\ntừ cấp "
                    + (player.Captutien - 1) + " lên cấp "
                    + player.Captutien + ".\nTỷ lệ thành công: " + String.format("%.1f", successRate) + "%";

            // Gửi thông báo thành công
            Service.gI().sendThongBao(player, message);

            return TutienResult.success(message);
        } else {
            return TutienResult.failure(
                    "Xin lỗi nhưng ta đã cố hết sức.\nTỷ lệ thành công: " + String.format("%.1f", successRate) + "%");
        }
    }

}
