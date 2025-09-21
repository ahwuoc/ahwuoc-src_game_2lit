package Dragon.models.boss;

public class BossData {

    public static final int DEFAULT_APPEAR = 0;
    public static final int APPEAR_WITH_ANOTHER = 1;
    public static final int ANOTHER_LEVEL = 2;

    private String name;

    private byte gender;

    private short[] outfit;

    private double dame;

    private double[] hp;

    private int[] mapJoin;

    private int[][] skillTemp;

    private String[] textS;

    private String[] textM;

    private String[] textE;

    private int secondsRest;

    private TypeAppear typeAppear;

    private int[] bossesAppearTogether;

    private BossData(String name, byte gender, short[] outfit, long dame, long[] hp,
            int[] mapJoin, int[][] skillTemp, String[] textS, String[] textM,
            String[] textE) {
        this.name = name;
        this.gender = gender;
        this.outfit = outfit;
        this.dame = (double) dame; // Convert long sang double
        this.hp = new double[hp.length];
        for (int i = 0; i < hp.length; i++) {
            this.hp[i] = (double) hp[i]; // Convert long[] sang double[]
        }
        this.mapJoin = mapJoin;
        this.skillTemp = skillTemp;
        this.textS = textS;
        this.textM = textM;
        this.textE = textE;
        this.secondsRest = 0;
        this.typeAppear = TypeAppear.DEFAULT_APPEAR;
    }

    private BossData(String name, byte gender, short[] outfit, double dame, double[] hp,
            int[] mapJoin, int[][] skillTemp, String[] textS, String[] textM,
            String[] textE) {
        this.name = name;
        this.gender = gender;
        this.outfit = outfit;
        this.dame = dame; // Không cần cast vì cùng kiểu double
        this.hp = hp; // Không cần cast vì cùng kiểu double[]
        this.mapJoin = mapJoin;
        this.skillTemp = skillTemp;
        this.textS = textS;
        this.textM = textM;
        this.textE = textE;
        this.secondsRest = 0;
        this.typeAppear = TypeAppear.DEFAULT_APPEAR;
    }

    // Constructor mixed: double dame, long[] hp (tương thích)
    private BossData(String name, byte gender, short[] outfit, double dame, long[] hp,
            int[] mapJoin, int[][] skillTemp, String[] textS, String[] textM,
            String[] textE) {
        this.name = name;
        this.gender = gender;
        this.outfit = outfit;
        this.dame = dame; // dame đã là double
        this.hp = new double[hp.length];
        for (int i = 0; i < hp.length; i++) {
            this.hp[i] = (double) hp[i]; // Convert long[] sang double[]
        }
        this.mapJoin = mapJoin;
        this.skillTemp = skillTemp;
        this.textS = textS;
        this.textM = textM;
        this.textE = textE;
        this.secondsRest = 0;
        this.typeAppear = TypeAppear.DEFAULT_APPEAR;
    }

    public BossData(String name, byte gender, short[] outfit, long dame, long[] hp,
            int[] mapJoin, int[][] skillTemp, String[] textS, String[] textM,
            String[] textE, int secondsRest) {
        this(name, gender, outfit, dame, hp, mapJoin, skillTemp, textS, textM, textE);
        this.secondsRest = secondsRest;
    }

    public BossData(String name, byte gender, short[] outfit, double dame, long[] hp,
            int[] mapJoin, int[][] skillTemp, String[] textS, String[] textM,
            String[] textE, int secondsRest) {
        this(name, gender, outfit, dame, hp, mapJoin, skillTemp, textS, textM, textE);
        this.secondsRest = secondsRest;
    }

    public BossData(String name, byte gender, short[] outfit, double dame, long[] hp,
            int[] mapJoin, int[][] skillTemp, String[] textS, String[] textM,
            String[] textE, int secondsRest, int[] bossesAppearTogether) {
        this(name, gender, outfit, dame, hp, mapJoin, skillTemp, textS, textM, textE, secondsRest);
        this.bossesAppearTogether = bossesAppearTogether;
    }

    public BossData(String name, byte gender, short[] outfit, double dame, double[] hp,
            int[] mapJoin, int[][] skillTemp, String[] textS, String[] textM,
            String[] textE, int secondsRest) {
        this.name = name;
        this.gender = gender;
        this.outfit = outfit;
        this.dame = dame; // Không cần cast vì cùng kiểu double
        this.hp = hp; // Không cần cast vì cùng kiểu double[]
        this.mapJoin = mapJoin;
        this.skillTemp = skillTemp;
        this.textS = textS;
        this.textM = textM;
        this.textE = textE;
        this.secondsRest = secondsRest;
        this.typeAppear = TypeAppear.DEFAULT_APPEAR;
    }

    public BossData(String name, byte gender, short[] outfit, long dame, long[] hp,
            int[] mapJoin, int[][] skillTemp, String[] textS, String[] textM,
            String[] textE, int secondsRest, int[] bossesAppearTogether) {
        this(name, gender, outfit, dame, hp, mapJoin, skillTemp, textS, textM, textE, secondsRest);
        this.bossesAppearTogether = bossesAppearTogether;
    }

    public BossData(String name, byte gender, short[] outfit, long dame, long[] hp,
            int[] mapJoin, int[][] skillTemp, String[] textS, String[] textM,
            String[] textE, TypeAppear typeAppear) {
        this(name, gender, outfit, dame, hp, mapJoin, skillTemp, textS, textM, textE);
        this.typeAppear = typeAppear;
    }

    public BossData(String name, byte gender, short[] outfit, long dame, long[] hp,
            int[] mapJoin, int[][] skillTemp, String[] textS, String[] textM,
            String[] textE, int secondsRest, TypeAppear typeAppear) {
        this(name, gender, outfit, dame, hp, mapJoin, skillTemp, textS, textM, textE, secondsRest);
        this.typeAppear = typeAppear;
    }

    public BossData(String name, byte gender, long dame, long[] hp,
            short[] outfit, int[] mapJoin, int[][] skillTemp,
            int secondsRest, String[] textS, String[] textM,
            String[] textE) {
        this.name = name;
        this.gender = gender;
        this.dame = (double) dame; // Convert long sang double
        this.hp = new double[hp.length];
        for (int i = 0; i < hp.length; i++) {
            this.hp[i] = (double) hp[i]; // Convert long[] sang double[]
        }
        this.outfit = outfit;
        this.mapJoin = mapJoin;
        this.skillTemp = skillTemp;
        this.secondsRest = secondsRest;
        this.textS = new String[]{};
        this.textM = new String[]{};
        this.textE = new String[]{};
    }

    // Manual Builder to replace Lombok @Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;
        private byte gender;
        private long dame;
        private long[] hp;
        private short[] outfit;
        private int[] mapJoin;
        private int[][] skillTemp;
        private int secondsRest;
        private String[] textS;
        private String[] textM;
        private String[] textE;

        public Builder name(String v) {
            this.name = v;
            return this;
        }

        public Builder gender(byte v) {
            this.gender = v;
            return this;
        }

        public Builder dame(long v) {
            this.dame = v;
            return this;
        }

        public Builder hp(long[] v) {
            this.hp = v;
            return this;
        }

        public Builder outfit(short[] v) {
            this.outfit = v;
            return this;
        }

        public Builder mapJoin(int[] v) {
            this.mapJoin = v;
            return this;
        }

        public Builder skillTemp(int[][] v) {
            this.skillTemp = v;
            return this;
        }

        public Builder secondsRest(int v) {
            this.secondsRest = v;
            return this;
        }

        public Builder textS(String[] v) {
            this.textS = v;
            return this;
        }

        public Builder textM(String[] v) {
            this.textM = v;
            return this;
        }

        public Builder textE(String[] v) {
            this.textE = v;
            return this;
        }

        public BossData build() {
            // Use the existing constructor to keep conversion logic
            BossData data = new BossData(name, gender, dame,
                    hp != null ? hp : new long[]{},
                    outfit, mapJoin, skillTemp,
                    secondsRest,
                    textS != null ? textS : new String[]{},
                    textM != null ? textM : new String[]{},
                    textE != null ? textE : new String[]{});
            return data;
        }
    }

    // ========== HELPER METHODS FOR COMPATIBILITY ==========
    /**
     * Getter tương thích với long (cho code cũ)
     */
    public long getDameLong() {
        return (long) this.dame;
    }

    /**
     * Getter tương thích với double (cho code mới)
     */
    public double getDameDouble() {
        return this.dame;
    }

    /**
     * Setter tương thích với long (cho code cũ)
     */
    public void setDame(long dame) {
        this.dame = (double) dame;
    }

    /**
     * Setter tương thích với double (cho code mới)
     */
    public void setDame(double dame) {
        this.dame = dame;
    }

    /**
     * Getter HP tương thích với long[] (cho code cũ)
     */
    public long[] getHpLong() {
        long[] result = new long[hp.length];
        for (int i = 0; i < hp.length; i++) {
            result[i] = (long) hp[i];
        }
        return result;
    }

    /**
     * Getter HP tương thích với double[] (cho code mới)
     */
    public double[] getHpDouble() {
        return this.hp;
    }

    /**
     * Setter HP tương thích với long[] (cho code cũ)
     */
    public void setHp(long[] hp) {
        this.hp = new double[hp.length];
        for (int i = 0; i < hp.length; i++) {
            this.hp[i] = (double) hp[i];
        }
    }

    /**
     * Setter HP tương thích với double[] (cho code mới)
     */
    public void setHp(double[] hp) {
        this.hp = hp;
    }

    // ========== STANDARD GETTERS/SETTERS ==========
    public String getName() {
        return name;
    }

    // Getter mặc định cho dame và hp (tương thích với code hiện tại)
    public double getDame() {
        return dame;
    }

    public double[] getHp() {
        return hp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getGender() {
        return gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public short[] getOutfit() {
        return outfit;
    }

    public void setOutfit(short[] outfit) {
        this.outfit = outfit;
    }

    public int[] getMapJoin() {
        return mapJoin;
    }

    public void setMapJoin(int[] mapJoin) {
        this.mapJoin = mapJoin;
    }

    public int[][] getSkillTemp() {
        return skillTemp;
    }

    public void setSkillTemp(int[][] skillTemp) {
        this.skillTemp = skillTemp;
    }

    public String[] getTextS() {
        return textS;
    }

    public void setTextS(String[] textS) {
        this.textS = textS;
    }

    public String[] getTextM() {
        return textM;
    }

    public void setTextM(String[] textM) {
        this.textM = textM;
    }

    public String[] getTextE() {
        return textE;
    }

    public void setTextE(String[] textE) {
        this.textE = textE;
    }

    public int getSecondsRest() {
        return secondsRest;
    }

    public void setSecondsRest(int secondsRest) {
        this.secondsRest = secondsRest;
    }

    public TypeAppear getTypeAppear() {
        return typeAppear;
    }

    public void setTypeAppear(TypeAppear typeAppear) {
        this.typeAppear = typeAppear;
    }

    public int[] getBossesAppearTogether() {
        return bossesAppearTogether;
    }

    public void setBossesAppearTogether(int[] bossesAppearTogether) {
        this.bossesAppearTogether = bossesAppearTogether;
    }

    // Thêm field và method getId() để tương thích với database
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

/**
 * Vui lòng không sao chép mã nguồn này dưới mọi hình thức. Hãy tôn trọng tác
 * giả của mã nguồn này. Xin cảm ơn! - GirlBeo
 */
