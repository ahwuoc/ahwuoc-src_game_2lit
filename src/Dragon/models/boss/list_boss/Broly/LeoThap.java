package Dragon.models.boss.list_boss.Broly;

import Dragon.consts.ConstPlayer;
import Dragon.jdbc.daos.PlayerDAO;
import java.util.Random;
import Dragon.models.boss.Boss;
import Dragon.models.boss.BossData;
import Dragon.models.boss.BossManager;
import Dragon.models.map.Zone;
import Dragon.models.player.Player;
import Dragon.models.skill.Skill;
import Dragon.services.EffectSkillService;
import Dragon.services.Service;
import Dragon.services.SkillService;
import Dragon.utils.SkillUtil;
import Dragon.utils.Util;

/**
 *
 * @author Tamkjll
 */
public class LeoThap extends Boss {

    String nameString;
    private int lvboss;
    Player plattack;

    public LeoThap(int bossID, BossData bossData, Zone zone, String name, int lvdame, Player pl) throws Exception {
        super(bossID, bossData);
        this.zone = zone;
        nameString = name;
        this.nPoint.khangTDHS = true;
        this.nPoint.khangTM = true;
        this.plattack = pl;
    }

    @Override
    public void reward(Player plKill) {

        plKill.capboss++;
        Service.gI().sendMoney(plKill);
        PlayerDAO.updatePlayer(plKill);
//                        if (plKill.TamkjllCapPb < 500) {
//                                plKill.TamkjllCapPb++;
//                        }
//                        if (plKill.CapTamkjll < 100) {
//                                if (Util.isTrue(10f, 100)) {
//                                        plKill.CapTamkjll++;
//                                        Service.gI().sendThongBao(plKill, "Bạn nhận đc 1 cấp tamkjll");
//                                }
//                        }
//                        int[] itemCt = new int[] { 457, 1099, 1100, 1101, 1102, 1103 };
//                        int randomDo = new Random().nextInt(itemCt.length);
//                        if (Util.isTrue(50f, 100)) {
//                                Service.gI().dropItemMap(this.zone,
//                                                Util.useItem(zone, itemCt[randomDo], Util.nextInt(4),
//                                                                this.location.x, this.location.y, plKill.id));
//                        }
//                        if (Util.isTrue(30f, 100)) {
//                                Service.gI().dropItemMap(this.zone,
//                                                Util.useItem(zone, 1144, 1, this.location.x, this.location.y,
//                                                                plKill.id));
//                                return;
//                        }

    }

    @Override
    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 100)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage / 4);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage / 4;
            }
            damage = damage * 4 / 5;
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return (damage);
        } else {
            return 0;
        }
    }

    private long Timegayst;

    @Override
    public void active() {
        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }
        this.nPoint.khangTDHS = true;
        this.nPoint.khangTM = true;
        this.nPoint.critg = 110;
        this.nPoint.tlNeDon = 80;
        this.nPoint.calPoint();
        Player pl = this.plattack;
        if (pl == null || pl.isDie()
                //                        || !pl.name.equals(nameString) 
                //                        || pl.xuatsu != lvboss
                || this.zone.getNumOfPlayers() >= 2) {
            this.leaveMap();
        }
//                if (this.zone.PetinMap()) {
//                        this.leaveMap();
//                }
        super.active();
        this.attack();
        if (Util.canDoWithTime(Timegayst, 60000)) {
            this.leaveMap();
        }
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = this.plattack;
                if (pl == null || pl.isDie()) {
                    return;
                }
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70));
                        } else {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50));
                        }
                    }
                    SkillService.gI().useSkill(this, pl, null, null);
                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
//                Logger.logException(Boss.class, ex);
            }
        }
        if (this.plattack == null || this.plattack.isDie()) {
            this.leaveMap();
        }
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        this.dispose();
    }

    @Override
    public void joinMap() {
        super.joinMap(); // To change body of generated methods, choose Tools | Templates.
        Timegayst = System.currentTimeMillis();
    }
}
