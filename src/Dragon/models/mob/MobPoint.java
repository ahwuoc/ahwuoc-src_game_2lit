package Dragon.models.mob;

import Dragon.utils.Util;

public class MobPoint {

    public final Mob mob;
    public double hp;
    public double maxHp;
    public double dame;

    public MobPoint(Mob mob) {
        this.mob = mob;
    }

    public double getHpFull() {
        return maxHp;
    }

    public void setHpFull(double hp) {
        maxHp = hp;
    }

    public void setHpFull(long hp) {
        maxHp = hp;
    }

    public double gethp() {
        return hp;
    }

    public void sethp(double hp) {
        if (this.hp < 0) {
            this.hp = 0;
        } else {
            this.hp = hp;
        }
    }

    public void sethp(long hp) {
        if (this.hp < 0) {
            this.hp = 0;
        } else {
            this.hp = hp;
        }
    }

    public double getDameAttack() {
        return this.dame != 0 ? this.dame : this.getHpFull() * Util.nextInt(mob.pDame - 1, mob.pDame + 1) / 100
                + Util.nextInt(-(mob.level * 10), mob.level * 10);
    }
}
