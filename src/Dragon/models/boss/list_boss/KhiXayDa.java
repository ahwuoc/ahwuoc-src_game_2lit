/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dragon.models.boss.list_boss;

import Dragon.models.Template;
import Dragon.models.boss.Boss;
import Dragon.models.boss.BossStatus;
import Dragon.models.boss.BossesData;
import Dragon.models.map.ItemMap;
import Dragon.models.player.Player;
import Dragon.models.skill.Skill;
import Dragon.server.Manager;
import Dragon.services.EffectSkillService;
import Dragon.services.Service;
import Dragon.services.SkillService;
import Dragon.utils.SkillUtil;
import Dragon.utils.Util;
import java.util.Random;

/**
 *
 * @author Administrator
 */
public class KhiXayDa extends Boss {

    public KhiXayDa() throws Exception {
        super(Util.randomBossId(), BossesData.KHIXAYDA);
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(1, 1)) {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, 579, 1, this.location.x, this.location.y, plKill.id));
            if (plKill.gender == 0) {
                plKill.itemTime.lastX3EXP = System.currentTimeMillis();
                plKill.itemTime.isX3EXP = true;
            } else if (plKill.gender == 1) {
                plKill.itemTime.lastX3EXP = System.currentTimeMillis();
                plKill.itemTime.isX3EXP = true;
            } else if (plKill.gender == 2) {
                plKill.itemTime.lastX3EXP = System.currentTimeMillis();
                plKill.itemTime.isX3EXP = true;
            }
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
    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            if (SkillUtil.isUseSkillDacBiet(plAtt)) {
                this.chat("Ta miễn nhiễm với những chiêu thức như này!!");
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
