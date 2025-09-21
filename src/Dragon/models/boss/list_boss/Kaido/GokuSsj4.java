package Dragon.models.boss.list_boss.Kaido;

import Dragon.models.boss.list_boss.Cooler.*;
import Dragon.models.boss.Boss;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossStatus;
import Dragon.models.boss.BossesData;
import Dragon.models.map.ItemMap;
import Dragon.models.player.Player;
import Dragon.server.Manager;
import Dragon.services.EffectSkillService;
import Dragon.services.Service;
import Dragon.utils.Util;

import java.util.Random;

public class GokuSsj4 extends Boss {

    public GokuSsj4() throws Exception {
        super(BossID.GOKU_SSJ4, BossesData.GOKU_SSJ4);
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(30, 100)) {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, 1279, Util.nextInt(1, 3), this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
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
        if (Util.canDoWithTime(st, 90000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
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
                damage = 1;
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

}
