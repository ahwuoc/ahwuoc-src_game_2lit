package Dragon.services.tutien;

/**
 * Enum quản lý các đại cảnh giới Tu Tiên
 */
public enum TutienRealm {
    DO_KHI("Đấu khí", 0, 9),
    DO_GIA("Đấu giả", 10, 19),
    DO_SU("Đấu sư", 20, 29),
    DAI_DO_SU("Đại đấu sư", 30, 39),
    DO_LINH("Đấu linh", 40, 49),
    DO_VUONG("Đấu vương", 50, 59),
    DO_HOANG("Đấu hoàng", 60, 69),
    DO_TONG("Đấu tông", 70, 79),
    DO_TON("Đấu tôn", 80, 89),
    DO_THANH("Đấu thánh", 90, 95),
    DO_DE("Đấu đế", 96, 96);

    private final String name;
    private final int minLevel;
    private final int maxLevel;

    TutienRealm(String name, int minLevel, int maxLevel) {
        this.name = name;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    public String getName() {
        return name;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Lấy đại cảnh giới từ level
     */
    public static TutienRealm getRealmByLevel(int level) {
        for (TutienRealm realm : values()) {
            if (level >= realm.minLevel && level <= realm.maxLevel) {
                return realm;
            }
        }
        return null;
    }

    /**
     * Kiểm tra level có hợp lệ không
     */
    public static boolean isValidLevel(int level) {
        return level >= 0 && level <= 96;
    }

    /**
     * Lấy tên cấp bậc đầy đủ
     */
    public static String getFullRankName(int level) {
        if (!isValidLevel(level)) {
            return "Phế vật";
        }

        TutienRealm realm = getRealmByLevel(level);
        if (realm == null) {
            return "Phế vật";
        }

        // Xử lý các trường hợp đặc biệt
        if (level == 96) {
            return "Đấu đế";
        }
        if (level >= 90 && level <= 95) {
            return "Đấu thánh";
        }

        // Tính tầng trong đại cảnh giới
        int tier = level % 10;
        if (tier == 9) {
            return realm.getName() + " đỉnh phong";
        } else {
            return realm.getName() + " tầng " + (tier + 1);
        }
    }
}
