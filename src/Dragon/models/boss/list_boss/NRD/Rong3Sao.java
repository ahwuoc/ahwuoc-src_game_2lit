package Dragon.models.boss.list_boss.NRD;

import Dragon.models.player.Player;
import Dragon.models.boss.Boss;
import Dragon.models.boss.BossesData;
import Dragon.models.map.ItemMap;
import Dragon.services.EffectSkillService;
import Dragon.services.Service;
import Dragon.utils.Util;

public class Rong3Sao extends Boss {

    public Rong3Sao() throws Exception {
        super(Util.randomBossId(), BossesData.Rong_3Sao);
    }

    @Override
    public void reward(Player plKill) {
        ItemMap it = new ItemMap(this.zone, 374, 1, this.location.x, this.location.y, -1);
        Service.getInstance().dropItemMap(this.zone, it);
    }

    @Override
    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage / 7);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage / 4;
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
