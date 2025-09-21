package Dragon.services.tutien;

import Dragon.models.player.Player;
import Dragon.utils.Util;

/**
 * Class tính toán các chỉ số Tu Tiên
 */
public class TutienCalculator {

    /**
     * Tính Exp Tu Tiên từ item
     */
    public static long calculateExpFromItem(Player player, int itemId, int quantity) {
        int fixedExp = getFixedExpByItemId(itemId);
        if (fixedExp == 0) {
            return 0;
        }

        long baseExp = (long) quantity * fixedExp;

        if (player.DauLaDaiLuc[11] == 1) {
            int bonusPercent = Math.min(
                    (int) (player.DauLaDaiLuc[12] / TutienConstants.DAU_LA_DAI_LUC_DIVISOR),
                    TutienConstants.DAU_LA_DAI_LUC_MAX_BONUS_PERCENT);
            baseExp += baseExp * bonusPercent / 100;
        }
        baseExp += getTienKiemBonus(player, baseExp);

        // Tự động làm tròn EXP để không vượt quá giới hạn cảnh giới
        long finalExp = capExpToRealmLimit(player, baseExp);

        return finalExp;
    }

    private static long capExpToRealmLimit(Player player, long expToAdd) {
        return expToAdd;
    }

    public static long getExpNeededForNextLevel(int currentLevel) {
        if (currentLevel >= 96) {
            return Long.MAX_VALUE; // Đã max level
        }
        return 1000;
    }

    private static int getFixedExpByItemId(int itemId) {
        switch (itemId) {
            case TutienConstants.ItemIds.DO_KHI_DAN: // Đấu Khí Đan (cấp 0-9)
                return 1000;
            case TutienConstants.ItemIds.DO_GIA_DAN: // Đấu Giả Đan (cấp 10-19)
                return 1000;
            case TutienConstants.ItemIds.DO_SU_DAN: // Đấu Sư Đan (cấp 20-29)
                return 1000;
            case TutienConstants.ItemIds.DAI_DO_SU_DAN: // Đại Đấu Sư Đan (cấp 30-39)
                return 1000;
            case TutienConstants.ItemIds.DO_LINH_DAN: // Đấu Linh Đan (cấp 40-49)
                return 1000;
            case TutienConstants.ItemIds.DO_VUONG_DAN: // Đấu Vương Đan (cấp 50-59)
                return 1000;
            case TutienConstants.ItemIds.DO_HOANG_DAN: // Đấu Hoàng Đan (cấp 60-69)
                return 1000;
            case TutienConstants.ItemIds.DO_TONG_DAN: // Đấu Tông Đan (cấp 70-79)
                return 1000;
            case TutienConstants.ItemIds.DO_TON_DAN: // Đấu Tôn Đan (cấp 80-89)
                return 1000;
            case TutienConstants.ItemIds.DO_THANH_DAN: // Đấu Thánh Đan (cấp 90-95)
                return 1000;
            case TutienConstants.ItemIds.DO_DE_DAN: // Đấu Đế Đan (cấp 96)
                return 15000;
            default:
                return 0;
        }
    }

    /**
     * Lấy range Exp theo Item ID (hệ thống mới - mỗi đan tương ứng với 1 đại
     * cảnh giới)
     */
    private static int[] getExpRangeByItemId(int itemId) {
        switch (itemId) {
            case TutienConstants.ItemIds.DO_KHI_DAN:
                return TutienConstants.ExpRanges.DO_KHI_EXP;
            case TutienConstants.ItemIds.DO_GIA_DAN:
                return TutienConstants.ExpRanges.DO_GIA_EXP;
            case TutienConstants.ItemIds.DO_SU_DAN:
                return TutienConstants.ExpRanges.DO_SU_EXP;
            case TutienConstants.ItemIds.DAI_DO_SU_DAN:
                return TutienConstants.ExpRanges.DAI_DO_SU_EXP;
            case TutienConstants.ItemIds.DO_LINH_DAN:
                return TutienConstants.ExpRanges.DO_LINH_EXP;
            case TutienConstants.ItemIds.DO_VUONG_DAN:
                return TutienConstants.ExpRanges.DO_VUONG_EXP;
            case TutienConstants.ItemIds.DO_HOANG_DAN:
                return TutienConstants.ExpRanges.DO_HOANG_EXP;
            case TutienConstants.ItemIds.DO_TONG_DAN:
                return TutienConstants.ExpRanges.DO_TONG_EXP;
            case TutienConstants.ItemIds.DO_TON_DAN:
                return TutienConstants.ExpRanges.DO_TON_EXP;
            case TutienConstants.ItemIds.DO_THANH_DAN:
                return TutienConstants.ExpRanges.DO_THANH_EXP;
            case TutienConstants.ItemIds.DO_DE_DAN:
                return TutienConstants.ExpRanges.DO_DE_EXP;
            default:
                return null;
        }
    }

    /**
     * Tính bonus từ cấp Tiên Kiếm
     */
    private static long getTienKiemBonus(Player player, long baseExp) {
        if (player.Captutien >= TutienConstants.TienKiemBonus.DO_THANH_REQUIRED_LEVEL) {
            return baseExp / TutienConstants.TienKiemBonus.DO_THANH_BONUS_DIVISOR;
        } else if (player.Captutien >= TutienConstants.TienKiemBonus.DO_VUONG_REQUIRED_LEVEL) {
            return baseExp / TutienConstants.TienKiemBonus.DO_VUONG_BONUS_DIVISOR;
        } else if (player.Captutien >= TutienConstants.TienKiemBonus.DO_LINH_REQUIRED_LEVEL) {
            return baseExp / TutienConstants.TienKiemBonus.DO_LINH_BONUS_DIVISOR;
        }
        return 0;
    }

    /**
     * Kiểm tra item có thể sử dụng ở cấp hiện tại không
     */
    public static boolean canUseItemAtLevel(int itemId, int currentLevel) {
        TutienRealm requiredRealm = getRequiredRealmByItemId(itemId);
        if (requiredRealm == null) {
            return false;
        }

        return currentLevel >= requiredRealm.getMinLevel()
                && currentLevel <= requiredRealm.getMaxLevel();
    }

    /**
     * Lấy đại cảnh giới yêu cầu theo Item ID (hệ thống mới)
     */
    private static TutienRealm getRequiredRealmByItemId(int itemId) {
        switch (itemId) {
            case TutienConstants.ItemIds.DO_KHI_DAN:
                return TutienRealm.DO_KHI;
            case TutienConstants.ItemIds.DO_GIA_DAN:
                return TutienRealm.DO_GIA;
            case TutienConstants.ItemIds.DO_SU_DAN:
                return TutienRealm.DO_SU;
            case TutienConstants.ItemIds.DAI_DO_SU_DAN:
                return TutienRealm.DAI_DO_SU;
            case TutienConstants.ItemIds.DO_LINH_DAN:
                return TutienRealm.DO_LINH;
            case TutienConstants.ItemIds.DO_VUONG_DAN:
                return TutienRealm.DO_VUONG;
            case TutienConstants.ItemIds.DO_HOANG_DAN:
                return TutienRealm.DO_HOANG;
            case TutienConstants.ItemIds.DO_TONG_DAN:
                return TutienRealm.DO_TONG;
            case TutienConstants.ItemIds.DO_TON_DAN:
                return TutienRealm.DO_TON;
            case TutienConstants.ItemIds.DO_THANH_DAN:
                return TutienRealm.DO_THANH;
            case TutienConstants.ItemIds.DO_DE_DAN:
                return TutienRealm.DO_DE;
            default:
                return null;
        }
    }

    /**
     * Lấy tên item theo ID từ item template
     */
    public static String getItemNameById(int itemId) {
        // Kiểm tra xem có phải item tu tiên không
        if (itemId >= TutienConstants.ItemIds.DO_KHI_DAN && itemId <= TutienConstants.ItemIds.DO_DE_DAN) {
            // Lấy từ item template
            var template = Dragon.server.Manager.ITEM_TEMPLATES.get(itemId);
            if (template != null) {
                return template.name;
            }
        }
        return "Unknown Item";
    }

    /**
     * Tính bonus sức đánh theo cấp tiểu cảnh giới
     */
    public static long calculateRealmDamageBonus(int level) {
        if (level < 0 || level > TutienConstants.MAX_LEVEL) {
            return 0;
        }

        // Tính bonus theo từng cấp
        return (long) level * TutienConstants.RealmBonus.DAMAGE_PER_LEVEL;
    }

    /**
     * Tính bonus HP theo cấp tiểu cảnh giới
     */
    public static long calculateRealmHpBonus(int level) {
        if (level < 0 || level > TutienConstants.MAX_LEVEL) {
            return 0;
        }

        // Tính bonus theo từng cấp
        return (long) level * TutienConstants.RealmBonus.HP_PER_LEVEL;
    }

    /**
     * Tính bonus Ki theo cấp tiểu cảnh giới
     */
    public static long calculateRealmKiBonus(int level) {
        if (level < 0 || level > TutienConstants.MAX_LEVEL) {
            return 0;
        }

        // Tính bonus theo từng cấp
        return (long) level * TutienConstants.RealmBonus.KI_PER_LEVEL;
    }

    /**
     * Lấy thông tin bonus cấp tiểu cảnh giới (hiển thị ngang)
     */
    public static String getRealmBonusInfo(int level) {
        if (level < 0 || level > TutienConstants.MAX_LEVEL) {
            return "Không có bonus";
        }

        long damageBonus = calculateRealmDamageBonus(level);
        long hpBonus = calculateRealmHpBonus(level);
        long kiBonus = calculateRealmKiBonus(level);

        return String.format("Bonus cấp tiểu cảnh giới: Dame +%s | HP +%s | Ki +%s",
                formatNumber(damageBonus),
                formatNumber(hpBonus),
                formatNumber(kiBonus));
    }

    /**
     * Lấy thông tin bonus ngắn gọn (1 dòng)
     */
    public static String getShortBonusInfo(int level) {
        if (level < 0 || level > TutienConstants.MAX_LEVEL) {
            return "Không có bonus";
        }

        long damageBonus = calculateRealmDamageBonus(level);
        long hpBonus = calculateRealmHpBonus(level);
        long kiBonus = calculateRealmKiBonus(level);

        return String.format("Dame +%s | HP +%s | Ki +%s",
                formatNumber(damageBonus),
                formatNumber(hpBonus),
                formatNumber(kiBonus));
    }

    /**
     * Format số với dấu phẩy
     */
    private static String formatNumber(long number) {
        return String.format("%,d", number);
    }
}
