package Dragon.models.boss.list_boss.cell;

import Dragon.consts.ConstPlayer;
import Dragon.models.boss.*;
import Dragon.models.boss.list_boss.BDKB.TrungUyXanhLo;
import Dragon.models.map.ItemMap;
import Dragon.models.player.Player;
import Dragon.models.skill.Skill;
import Dragon.server.Manager;
import Dragon.services.EffectSkillService;
import Dragon.services.Service;
import Dragon.services.TaskService;
import Dragon.utils.Util;
import Dragon.services.PlayerService;
import Dragon.services.SkillService;
import Dragon.utils.SkillUtil;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SieuBoHung extends Boss {

    private long lastTimeHapThu;
    private int timeHapThu;
    private int initSuper = 0;

    public SieuBoHung() throws Exception {
        super(BossID.SIEU_BO_HUNG, BossesData.SIEU_BO_HUNG_1, BossesData.SIEU_BO_HUNG_2, BossesData.SIEU_BO_HUNG_3);
    }

    @Override
    public void reward(Player plKill) {
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
        rewardFutureBoss(plKill);

    }

    @Override
    public void joinMap() {
        super.joinMap();
        st = System.currentTimeMillis();
    }

    private long st;

    @Override
    public void active() {
        if (this.currentLevel == 1 && this.zone.getBosses().stream().filter(b -> b.name.equals("Xên con")).findAny().orElse(null) != null) {
            this.changeToTypeNonPK();
        } else {
            if (this.typePk != ConstPlayer.PK_ALL) {
                this.changeToTypePK();
            }
            this.hapThu();
            this.attack();
            if (Util.canDoWithTime(st, 900000)) {
                this.changeStatus(BossStatus.LEAVE_MAP);
            }
        }
    }

    boolean newBoss = false;

    @Override
    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            if (!newBoss) {
                if (this.currentLevel == 1 && this.nPoint.hp <= 450_000_000) {
                    this.playerSkill.skills.add(new Skill(SkillUtil.createSkill(Skill.TAI_TAO_NANG_LUONG, 7)));
                    this.playerSkill.skillSelect = this.playerSkill.getSkillbyId(Skill.TAI_TAO_NANG_LUONG);
                    SkillService.gI().useSkill(this, null, null, null);
                    try {
                        for (int i = 0; i < 7; i++) {
                            Boss b = new Xencon();
                            b.id = Util.nextInt(100000, 1000000);
                            b.zone = this.zone;
                        }
                        newBoss = !newBoss;
                    } catch (Exception e) {
                    }
                }
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

    private void hapThu() {
        if (!Util.canDoWithTime(this.lastTimeHapThu, this.timeHapThu)) {
            return;
        }

        Player pl = this.zone.getRandomPlayerInMap();
        if (pl == null || pl.isDie()) {
            return;
        }
        double HP = this.nPoint.hp + pl.nPoint.hp;
        if (HP > 2000000000) {
            HP = 2000000000;
        }
        if (this.nPoint.hpg < HP) {
            this.nPoint.hpg = (int) HP;
        }
        this.nPoint.hp = (int) HP;
        this.nPoint.critg++;
        PlayerService.gI().hoiPhuc(this, pl.nPoint.hp, 0);
        //pl.injured(null, pl.nPoint.hpMax, true, false);
        Service.gI().sendThongBao(pl, "Bạn vừa bị " + this.name + " hấp thu!");
        this.chat(2, "Ui cha cha, kinh dị quá. " + pl.name + " vừa bị tên " + this.name + " nuốt chửng kìa!!!");
        this.chat("Haha, ngọt lắm đấy " + pl.name + "..");
        this.lastTimeHapThu = System.currentTimeMillis();
        this.timeHapThu = Util.nextInt(15000, 20000);
    }
}
