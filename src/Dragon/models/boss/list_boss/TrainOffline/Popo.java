package Dragon.models.boss.list_boss.TrainOffline;

import Dragon.models.boss.*;
import Dragon.models.map.Zone;
import Dragon.models.player.Player;
import Dragon.services.EffectSkillService;
import Dragon.services.Service;
import Dragon.services.SkillService;
import Dragon.services.func.ChangeMapService;
import Dragon.utils.SkillUtil;
import Dragon.utils.Util;

/**
 * @Stole By MITCHIKEN ZALO 0358689793
 */
public class Popo extends Boss {

    public Popo() throws Exception {
        super(BossID.MR_POPO, BossesData.POPO);
    }

    @Override
    public void reward(Player plKill) {
        //vật phẩm rơi khi diệt boss nhân bản
        if (plKill.isfight) {
            plKill.typetrain++;
        }
        plKill.rsfight();
        this.chat("Hôm nay ta không được khỏe");
        this.playerkill = plKill;

    }

    @Override
    public void active() {

        attack();
    }

    @Override
    public void checkPlayerDie(Player player) {
        if (player.isDie()) {
            this.chat("Quá gà");
            ChangeMapService.gI().changeMapYardrat(this, this.zone, 322, 408);
            this.changeToTypeNonPK();
            player.rsfight();
        }

    }

    @Override
    public void joinMapByZone(Zone zone) {
        if (zone != null) {
            this.zone = zone;
            ChangeMapService.gI().changeMapYardrat(this, this.zone, 302, 408);
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.zone.getNumOfPlayers() < 1) {
            this.changeToTypeNonPK();
            ChangeMapService.gI().changeMapYardrat(this, this.zone, 302, 408);
            nPoint.setFullHpMp();
        }
    }

    @Override
    public void joinMap() {
        this.location.x = 302;
        this.location.y = 408;
        if (zoneFinal != null) {
            joinMapByZone(zoneFinal);
            return;
        }
        if (this.zone == null) {
            if (this.parentBoss != null) {
                this.zone = parentBoss.zone;
            } else if (this.lastZone == null) {
                this.zone = getMapJoin();
            } else {
                this.zone = this.lastZone;
            }
        }
        if (this.zone != null) {
            if (this.currentLevel == 0) {

                ChangeMapService.gI().changeMapYardrat(this, this.zone, 322, 408);
            }
            Service.getInstance().sendFlagBag(this);
//            this.notifyJoinMap();
        }
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100)) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl.isDie() || pl.isfake) {
                    if (pl == null) {
                        this.changeToTypeNonPK();
                    }
                    return;
                }
                if (pl.istry || pl.isfight) {
                    this.changeToTypePK();
                    if (this.isDie()) {
                        Service.gI().hsChar(this, this.nPoint.hpMax, this.nPoint.mpMax);
                    }
                }
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(10, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(200, 400)),
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

    @Override
    public Player getPlayerAttack() {
        if (this.playerTarger != null && (this.playerTarger.isDie() || !this.zone.equals(this.playerTarger.zone))) {
            this.playerTarger = null;
        }
        if (this.playerTarger == null || Util.canDoWithTime(this.lastTimeTargetPlayer, this.timeTargetPlayer)) {
            this.playerTarger = this.zone.getplayertrain();
            this.lastTimeTargetPlayer = System.currentTimeMillis();
        }
        return this.playerTarger;
    }

    @Override
    public void die(Player plKill) {
        if (plKill != null) {
            reward(plKill);
        }
        this.playerkill.rsfight();
    }

    @Override
    public void leaveMap() {
    }
}
