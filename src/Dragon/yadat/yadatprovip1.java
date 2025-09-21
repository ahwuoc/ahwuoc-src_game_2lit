package Dragon.yadat;

import Dragon.yadat.*;
//import Dragon.models.boss.list_boss.cell.*;
import Dragon.consts.ConstPlayer;
import Dragon.models.boss.*;
import static Dragon.models.boss.BossStatus.ACTIVE;
import static Dragon.models.boss.BossStatus.JOIN_MAP;
import static Dragon.models.boss.BossStatus.RESPAWN;
import Dragon.models.boss.list_boss.cell.SieuBoHung;
import Dragon.models.map.ItemMap;
import Dragon.models.map.Zone;
import Dragon.models.map.challenge.MartialCongressService;
import Dragon.models.player.Player;
import Dragon.models.skill.Skill;
import Dragon.services.EffectSkillService;
import Dragon.services.PlayerService;
import Dragon.services.Service;
import Dragon.utils.Util;
import java.util.logging.Level;
import java.util.logging.Logger;
import Dragon.models.boss.BossManager;
import Dragon.models.boss.BossesData;
import Dragon.models.boss.Boss;
import Dragon.models.boss.BossID;
import Dragon.models.map.ItemMap;
import Dragon.server.Manager;
import Dragon.consts.ConstPlayer;
import Dragon.models.boss.Boss;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossManager;
import Dragon.models.boss.BossesData;
import Dragon.models.map.ItemMap;
import Dragon.models.player.Player;
import Dragon.models.skill.Skill;
import Dragon.server.Manager;
import Dragon.services.EffectSkillService;
import Dragon.services.Service;
import Dragon.services.TaskService;
import Dragon.services.PlayerService;
import Dragon.services.func.ChangeMapService;
import java.util.Random;

public class yadatprovip1 extends Boss {

    private static final int[][] FULL_LIENHOAN = new int[][]{{Skill.LIEN_HOAN, 1}, {Skill.LIEN_HOAN, 2}, {Skill.LIEN_HOAN, 3}, {Skill.LIEN_HOAN, 4}, {Skill.LIEN_HOAN, 5}, {Skill.LIEN_HOAN, 6}, {Skill.LIEN_HOAN, 7}};
    //   private static final int[][] FULL_TAI_TAO_NANG_LUONG = new int[][]{{Skill.TAI_TAO_NANG_LUONG, 1}, {Skill.TAI_TAO_NANG_LUONG, 2}, {Skill.TAI_TAO_NANG_LUONG, 3}, {Skill.TAI_TAO_NANG_LUONG, 4}, {Skill.TAI_TAO_NANG_LUONG, 5}, {Skill.TAI_TAO_NANG_LUONG, 6}, {Skill.TAI_TAO_NANG_LUONG, 7}};
    private long lastTimeHapThu;
    private int timeHapThu;
    private int initSuper = 0;
    protected Player playerAtt;
    private int timeLive = 10;
    private boolean calledNinja;

    public yadatprovip1() throws Exception {

        super(BossID.BOSS_YADAT1, BossesData.BOSS_YADAT1);
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(60, 100)) {
            ItemMap it = new ItemMap(this.zone, 590, 10, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, it);
        }
    }

    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 1800000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        if (Util.canDoWithTime(st, 1800000)) {
            BossManager.gI().removeBoss(this);
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }
    private long st;

    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = (long) this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage / 2;
            }
            this.nPoint.subHP(damage);
            if (this.nPoint.hp <= 150000000 && !this.calledNinja) {
                try {
                    new tapsuyadat(this.zone, 2, Util.nextInt(1000, 10000), BossID.Yadat1);
                    new tapsuyadat(this.zone, 2, Util.nextInt(1000, 10000), BossID.Yadat2);
                    new tapsuyadat(this.zone, 2, Util.nextInt(1000, 10000), BossID.Yadat3);
                    new tapsuyadat(this.zone, 2, Util.nextInt(1000, 10000), BossID.Yadat4);
                    new tapsuyadat(this.zone, 2, Util.nextInt(1000, 10000), BossID.Yadat5);
                    new tapsuyadat(this.zone, 2, Util.nextInt(1000, 10000), BossID.Yadat6);
                    new tapsuyadat(this.zone, 2, Util.nextInt(1000, 10000), BossID.Yadat7);
                    new tapsuyadat(this.zone, 2, Util.nextInt(1000, 10000), BossID.Yadat8);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                this.calledNinja = true;
            }
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
