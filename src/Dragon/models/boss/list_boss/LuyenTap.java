package Dragon.models.boss.list_boss;

import Dragon.consts.ConstPlayer;
import Dragon.models.boss.Boss;
import Dragon.models.boss.BossData;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossesData;
import Dragon.models.map.Zone;
import Dragon.models.player.Player;
import Dragon.services.EffectSkillService;
import Dragon.services.PlayerService;
import Dragon.services.func.ChangeMapService;
import Dragon.utils.Util;

public class LuyenTap extends Boss {

    public LuyenTap(int bossID, BossData bossData, Zone zone) throws Exception {
        super(bossID, bossData);
        this.zone = zone;
    }

    @Override
    public void joinMap() {
        ChangeMapService.gI().changeMapYardrat(this, this.zone, 330, 576);
        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }
        this.moveTo(330, 576);
    }

    @Override
    public void attack() {
    }

    @Override
    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            this.nPoint.subHP(damage);
            if (this.nPoint.hp <= 0) {
                this.nPoint.hp = this.nPoint.hpMax;
            }
            return damage;
        } else {
            return 0;
        }
    }
}
