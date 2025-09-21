package Dragon.models.matches;

public class TOP {

    private String name;
    private byte gender;
    private short head;
    private short body;
    private short leg;
    private long power;
    private long ki;
    private long hp;
    private long sd;
    private byte nv;
    private byte subnv;
    private int sk;
    private int pvp;
    private int id_player;
    private String info1;
    private String info2;
    public int rank;

    public TOP() {
    }

    // Getters/Setters
    public String getName() {
        return name;
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

    public short getHead() {
        return head;
    }

    public void setHead(short head) {
        this.head = head;
    }

    public short getBody() {
        return body;
    }

    public void setBody(short body) {
        this.body = body;
    }

    public short getLeg() {
        return leg;
    }

    public void setLeg(short leg) {
        this.leg = leg;
    }

    public long getPower() {
        return power;
    }

    public void setPower(long power) {
        this.power = power;
    }

    public long getKi() {
        return ki;
    }

    public void setKi(long ki) {
        this.ki = ki;
    }

    public long getHp() {
        return hp;
    }

    public void setHp(long hp) {
        this.hp = hp;
    }

    public long getSd() {
        return sd;
    }

    public void setSd(long sd) {
        this.sd = sd;
    }

    public byte getNv() {
        return nv;
    }

    public void setNv(byte nv) {
        this.nv = nv;
    }

    public byte getSubnv() {
        return subnv;
    }

    public void setSubnv(byte subnv) {
        this.subnv = subnv;
    }

    public int getSk() {
        return sk;
    }

    public void setSk(int sk) {
        this.sk = sk;
    }

    public int getPvp() {
        return pvp;
    }

    public void setPvp(int pvp) {
        this.pvp = pvp;
    }

    public int getId_player() {
        return id_player;
    }

    public void setId_player(int id_player) {
        this.id_player = id_player;
    }

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    // Manual Builder to keep API compatibility with Lombok usage in Manager
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final TOP inst = new TOP();

        public Builder name(String v) {
            inst.setName(v);
            return this;
        }

        public Builder gender(byte v) {
            inst.setGender(v);
            return this;
        }

        public Builder head(short v) {
            inst.setHead(v);
            return this;
        }

        public Builder body(short v) {
            inst.setBody(v);
            return this;
        }

        public Builder leg(short v) {
            inst.setLeg(v);
            return this;
        }

        public Builder power(long v) {
            inst.setPower(v);
            return this;
        }

        public Builder ki(long v) {
            inst.setKi(v);
            return this;
        }

        public Builder hp(long v) {
            inst.setHp(v);
            return this;
        }

        public Builder sd(long v) {
            inst.setSd(v);
            return this;
        }

        public Builder nv(byte v) {
            inst.setNv(v);
            return this;
        }

        public Builder subnv(byte v) {
            inst.setSubnv(v);
            return this;
        }

        public Builder sk(int v) {
            inst.setSk(v);
            return this;
        }

        public Builder pvp(int v) {
            inst.setPvp(v);
            return this;
        }

        public Builder id_player(int v) {
            inst.setId_player(v);
            return this;
        }

        public Builder info1(String v) {
            inst.setInfo1(v);
            return this;
        }

        public Builder info2(String v) {
            inst.setInfo2(v);
            return this;
        }

        public TOP build() {
            return inst;
        }
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
