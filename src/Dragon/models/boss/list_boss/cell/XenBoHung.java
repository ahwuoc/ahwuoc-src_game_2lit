package Dragon.models.boss.list_boss.cell;

import Dragon.consts.ConstPlayer;
import Dragon.models.boss.Boss;
import Dragon.models.boss.BossesData;
import Dragon.models.boss.BossID;
import Dragon.models.map.ItemMap;
import Dragon.models.player.Player;
import Dragon.services.EffectSkillService;
import Dragon.services.PlayerService;
import Dragon.services.Service;
import Dragon.services.TaskService;
import Dragon.utils.Util;

import java.util.Random;

public class XenBoHung extends Boss {

    private long lastTimeHapThu;
    private int timeHapThu;

    public XenBoHung() throws Exception {
        super(BossID.XEN_BO_HUNG, BossesData.XEN_BO_HUNG_1, BossesData.XEN_BO_HUNG_2, BossesData.XEN_BO_HUNG_3);
    }

    @Override
    public void reward(Player plKill) {

        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
        rewardFutureBoss(plKill);
    }

    @Override
    public void active() {
        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }
        this.hapThu();
        this.attack();
    }

    private void hapThu() {
        if (!Util.canDoWithTime(this.lastTimeHapThu, this.timeHapThu) || !Util.isTrue(1, 100)) {
            return;
        }

        Player pl = this.zone.getRandomPlayerInMap();
        if (pl == null || pl.isDie()) {
            return;
        }
        double HP = this.nPoint.hp + (this.nPoint.hpg * 0.2);
        if (HP > 2000000000) {
            HP = 2000000000;
        }
        if (this.nPoint.hpg < HP) {
            this.nPoint.hpg = (int) HP;
        }
        this.nPoint.hp = (int) HP;
        this.nPoint.critg++;
        PlayerService.gI().hoiPhuc(this, pl.nPoint.hp, 0);
        pl.injured(null, (long) pl.nPoint.hpMax, true, false);
        Service.gI().sendThongBao(pl, "Bạn vừa bị " + this.name + " hấp thu!");
        this.chat(2, "Ui cha cha, kinh dị quá. " + pl.name + " vừa bị tên " + this.name + " nuốt chửng kìa!!!");
        this.chat("Haha, ngọt lắm đấy " + pl.name + "..");
        this.lastTimeHapThu = System.currentTimeMillis();
        this.timeHapThu = Util.nextInt(15000, 20000);
    }

}

/**
 * Vui lòng không sao chép mã nguồn này dưới mọi hình thức. Hãy tôn trọng tác
 * giả của mã nguồn này. Xin cảm ơn! - GirlBeo
 */
