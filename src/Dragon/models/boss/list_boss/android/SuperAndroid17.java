package Dragon.models.boss.list_boss.android;

import Dragon.consts.ConstMob;
import Dragon.models.boss.Boss;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossManager;
import Dragon.models.boss.BossStatus;
import Dragon.models.boss.BossesData;
import Dragon.models.map.ItemMap;
import Dragon.models.mob.Mob;
import Dragon.models.player.Player;
import Dragon.services.EffectSkillService;
import Dragon.services.PetService;
import Dragon.services.Service;
import Dragon.services.TaskService;
import Dragon.utils.Util;

public class SuperAndroid17 extends Boss {

    public SuperAndroid17() throws Exception {
        super(BossID.SUPER_ANDROID_17, BossesData.SUPER_ANDROID_17);
        this.nPoint.defg = (short) (this.nPoint.hpg / 1000);
        if (this.nPoint.defg < 0) {
            this.nPoint.defg = (short) -this.nPoint.defg;
        }
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(15, 100)) {
            ItemMap it = new ItemMap(this.zone, 1142, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.gI().dropItemMap(this.zone, it);
        }

    }

    @Override
    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage / 2;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    private long st;

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 900000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        this.dispose();
    }
}
