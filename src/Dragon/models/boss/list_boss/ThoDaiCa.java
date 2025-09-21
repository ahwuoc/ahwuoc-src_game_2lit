/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dragon.models.boss.list_boss;

import Dragon.models.boss.Boss;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossStatus;
import Dragon.models.boss.BossesData;
import Dragon.models.map.ItemMap;
import Dragon.models.player.Player;
import Dragon.services.EffSkinService;
import Dragon.services.EffectSkillService;
import Dragon.services.ItemTimeService;
import Dragon.services.Service;
import Dragon.utils.Util;

/**
 *
 * @author Administrator
 */
public class ThoDaiCa extends Boss {

    public ThoDaiCa() throws Exception {
        super(Util.randomBossId(), BossesData.THO_DAI_CA);
    }

    @Override
    public void reward(Player plKill) {
        ItemMap itemMap;
        for (int i = 0; i < 100; i += 10) {
            itemMap = new ItemMap(zone, 462, 1, this.location.x + i, zone.map.yPhysicInTop(this.location.x, this.location.y), -1);
            Service.gI().dropItemMap(this.zone, itemMap);
            itemMap = new ItemMap(zone, 462, 1, this.location.x - i, zone.map.yPhysicInTop(this.location.x, this.location.y), -1);
            Service.gI().dropItemMap(this.zone, itemMap);
        }
    }

    @Override
    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (plAtt.nPoint.wearingThodaica = true) {
                Service.getInstance().chat(this, "Aaaaaaaaa các ngươi sẽ bị biến thành carot");
                Service.getInstance().chat(plAtt, "Con Mẹ Nó");
                EffSkinService.gI().setHoaDa(plAtt, System.currentTimeMillis(), 300000);
                Service.getInstance().Send_Caitrang(plAtt);
                ItemTimeService.gI().sendItemTime(plAtt, 4076, 300000 / 1000);
            }
            damage = Util.nextInt(10000, 100000);
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = Util.nextInt(1000, 10000);
            }
            if (damage > 1 && plAtt != null) {
                if (Util.isTrue(Util.nextInt(3, 8), 100)) {
                    Service.gI().dropItemMap(this.zone, new ItemMap(zone, 462, 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y), -1));
                }
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
