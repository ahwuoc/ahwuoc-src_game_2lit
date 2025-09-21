package Dragon.services.tutien;

/**
 * Các hằng số cho hệ thống Tu Tiên
 */
public class TutienConstants {

    // Cấp độ tối đa
    public static final int MAX_LEVEL = 96;

    // Thiên phú
    public static final int MIN_THIEN_PHU = 1;
    public static final int MAX_THIEN_PHU = 50;

    // Chi phí mở thiên phú
    public static final long OPEN_THIEN_PHU_REQUIRED_POWER = 100_000_000_000L; // 100 tỷ sức mạnh

    public static class ItemIds {

        // Đấu Khí Đan (cấp 0-9)
        public static final int DO_KHI_DAN = 1806;

        // Đấu Giả Đan (cấp 10-19)
        public static final int DO_GIA_DAN = 1807;

        // Đấu Sư Đan (cấp 20-29)
        public static final int DO_SU_DAN = 1808;

        // Đại Đấu Sư Đan (cấp 30-39)
        public static final int DAI_DO_SU_DAN = 1809;

        // Đấu Linh Đan (cấp 40-49)
        public static final int DO_LINH_DAN = 1810;

        // Đấu Vương Đan (cấp 50-59)
        public static final int DO_VUONG_DAN = 1811;

        // Đấu Hoàng Đan (cấp 60-69)
        public static final int DO_HOANG_DAN = 1812;

        // Đấu Tông Đan (cấp 70-79)
        public static final int DO_TONG_DAN = 1813;

        // Đấu Tôn Đan (cấp 80-89)
        public static final int DO_TON_DAN = 1814;

        // Đấu Thánh Đan (cấp 90-95)
        public static final int DO_THANH_DAN = 1815;

        // Đấu Đế Đan (cấp 96)
        public static final int DO_DE_DAN = 1816;
    }

    // Exp range cho từng loại đan (mỗi đan tương ứng với 1 đại cảnh giới)
    public static class ExpRanges {

        // Đấu Khí Đan (cấp 0-9)
        public static final int[] DO_KHI_EXP = {50, 150};

        // Đấu Giả Đan (cấp 10-19)
        public static final int[] DO_GIA_EXP = {100, 250};

        // Đấu Sư Đan (cấp 20-29)
        public static final int[] DO_SU_EXP = {150, 350};

        // Đại Đấu Sư Đan (cấp 30-39)
        public static final int[] DAI_DO_SU_EXP = {200, 450};

        // Đấu Linh Đan (cấp 40-49)
        public static final int[] DO_LINH_EXP = {250, 550};

        // Đấu Vương Đan (cấp 50-59)
        public static final int[] DO_VUONG_EXP = {300, 650};

        // Đấu Hoàng Đan (cấp 60-69)
        public static final int[] DO_HOANG_EXP = {350, 750};

        // Đấu Tông Đan (cấp 70-79)
        public static final int[] DO_TONG_EXP = {400, 850};

        // Đấu Tôn Đan (cấp 80-89)
        public static final int[] DO_TON_EXP = {450, 950};

        // Đấu Thánh Đan (cấp 90-95)
        public static final int[] DO_THANH_EXP = {500, 1000};

        // Đấu Đế Đan (cấp 96)
        public static final int[] DO_DE_EXP = {1000, 2000};
    }

    // Bonus từ cấp Tiên Kiếm
    public static class TienKiemBonus {

        public static final int DO_LINH_REQUIRED_LEVEL = 200;
        public static final int DO_LINH_BONUS_DIVISOR = 15;

        public static final int DO_VUONG_REQUIRED_LEVEL = 250;
        public static final int DO_VUONG_BONUS_DIVISOR = 12;

        public static final int DO_THANH_REQUIRED_LEVEL = 300;
        public static final int DO_THANH_BONUS_DIVISOR = 10;
    }

    // Bonus từ thiên phú
    public static final int THIEN_PHU_BONUS_PERCENT_PER_STAR = 15;

    // Bonus từ Đầu Là Đại Lực
    public static final int DAU_LA_DAI_LUC_MAX_BONUS_PERCENT = 20;
    public static final int DAU_LA_DAI_LUC_DIVISOR = 5;

    // Bonus theo cấp tiểu cảnh giới (từng tầng)
    public static class RealmBonus {

        public static final int DAMAGE_PER_LEVEL = 1_000; // 1k sức đánh mỗi cấp
        public static final int HP_PER_LEVEL = 20_000; // 20k HP mỗi cấp
        public static final int KI_PER_LEVEL = 20_000; // 20k Ki mỗi cấp
    }

    // Cấu hình hệ thống Tu Tiên
    public static class Config {

        // Tắt hoàn toàn hệ thống thiên phú
        public static final boolean DISABLE_THIEN_PHU = true;

        // Tắt hiệu ứng Tu Tiên (chỉ giữ chỉ số gốc)
        public static final boolean DISABLE_TUTIEN_EFFECTS = false;

        // Vẫn cho phép sử dụng đan và tăng cấp
        public static final boolean ALLOW_TUTIEN_LEVELING = true;

        // Vẫn cho phép mở/đột phá thiên phú
        public static final boolean ALLOW_THIEN_PHU = false;
    }
}
