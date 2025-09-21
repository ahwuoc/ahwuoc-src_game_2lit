/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dragon.De2;

import Dragon.consts.ConstPlayer;
import Dragon.models.mob.Mob;
import Dragon.models.player.Player;
import Dragon.models.skill.Skill;
import Dragon.server.ServerNotify;
import Dragon.services.MapService;
import Dragon.services.PlayerService;
import Dragon.services.Service;
import Dragon.services.SkillService;
import Dragon.services.func.ChangeMapService;
import Dragon.utils.Util;

/**
 *
 * @author Administrator
 */
public class Thu_TrieuHoi extends Player {

    public Player masterr;
    public static final byte FOLLOW = 0;
    public static final byte ATTACK_PLAYER = 1;
    public static final byte ATTACK_MOB = 2;
    public static final byte GOHOME = 3;
    public byte status = 0;
//    public static int idb;

    public Thu_TrieuHoi(Player masterr) {
        this.masterr = masterr;
        this.isTrieuhoipet = true;
        this.id = -masterr.id * 2;
    }

    @Override
    public short getHead() {
        return 104;

    }

    @Override
    public short getBody() {
        return 674;
    }

    @Override
    public short getLeg() {
        return 675;
    }

    @Override
    public byte getAura() {
        return 22;
    }

    public byte getStatus() {
        return this.status;
    }

    private boolean goingHome;
    private long lastTimeTargetPlayer;
    private int timeTargetPlayer;

    private static final short ARANGE_CAN_ATTACK = 1500;

    public void changeStatus(byte status) {
        if (goingHome || this.isDie()) {
            Service.gI().sendThongBao(masterr, "Không thể thực hiện");
            return;
        }
        if (status == GOHOME) {
            goHome();
        }
        this.status = status;
    }

    public void goHome() {
        if (this.status == GOHOME) {
            return;
        }
        goingHome = true;
        ChangeMapService.gI().goToMap(this, MapService.gI().getMapCanJoin(this, masterr.gender + 21, -1));
        this.zone.load_Me_To_Another(this);
        Thu_TrieuHoi.this.status = Thu_TrieuHoi.GOHOME;
        goingHome = false;
    }

    private Mob findMobAttack() {
        int dis = ARANGE_CAN_ATTACK;
        Mob mobAtt = null;
        for (Mob mob : zone.mobs) {
            if (mob.isDie()) {
                continue;
            }
            int d = Util.getDistance(this, mob);
            if (d <= dis) {
                dis = d;
                mobAtt = mob;
            }
        }
        return mobAtt;
    }

    public Player getPlayerAttack() {
        if (masterr.TrieuHoiPlayerAttack != null) {
            if (masterr.TrieuHoiPlayerAttack != null
                    && (masterr.TrieuHoiPlayerAttack.isDie() || !this.zone.equals(masterr.TrieuHoiPlayerAttack.zone))) {
                masterr.TrieuHoiPlayerAttack = null;
                if (masterr.TrieuHoiPlayerAttack == null) {
                    if (this.playerAttack != null
                            && (this.playerAttack.isDie() || !this.zone.equals(this.playerAttack.zone)
                            || (this.playerAttack.pvp == null || this.pvp == null)
                            || (this.playerAttack.typePk != ConstPlayer.PK_ALL
                            || this.typePk != ConstPlayer.PK_ALL)
                            || (!this.pvp.isInPVP(this.playerAttack) || !this.playerAttack.pvp.isInPVP(this))
                            || ((this.playerAttack.cFlag == 0 && this.cFlag == 0)
                            && (this.playerAttack.cFlag != 8 || this.cFlag == this.playerAttack.cFlag)))
                            || this.playerAttack == this || this.playerAttack == masterr) {
                        this.playerAttack = null;
                    }
                    if (this.zone != null
                            && (this.playerAttack == null || this.playerAttack == this || this.playerAttack == masterr)
                            || Util.canDoWithTime(this.lastTimeTargetPlayer, this.timeTargetPlayer)) {
                        this.playerAttack = this.zone.PlayerPKinmap();
                        this.lastTimeTargetPlayer = System.currentTimeMillis();
                        this.timeTargetPlayer = Util.nextInt(40000, 45000);
                    }
                    return this.playerAttack;
                }
            }
            return masterr.TrieuHoiPlayerAttack;
        } else {
            if (this.playerAttack != null && (this.playerAttack.isDie() || !this.zone.equals(this.playerAttack.zone)
                    || (this.playerAttack.pvp == null || this.pvp == null)
                    || (this.playerAttack.typePk != ConstPlayer.PK_ALL || this.typePk != ConstPlayer.PK_ALL)
                    || (!this.pvp.isInPVP(this.playerAttack) || !this.playerAttack.pvp.isInPVP(this))
                    || ((this.playerAttack.cFlag == 0 && this.cFlag == 0)
                    && (this.playerAttack.cFlag != 8 || this.cFlag == this.playerAttack.cFlag)))
                    || this.playerAttack == this || this.playerAttack == masterr) {
                this.playerAttack = null;
            }
            if (this.zone != null
                    && (this.playerAttack == null || this.playerAttack == this || this.playerAttack == masterr)
                    || Util.canDoWithTime(this.lastTimeTargetPlayer, this.timeTargetPlayer)) {
                this.playerAttack = this.zone.PlayerPKinmap();
                this.lastTimeTargetPlayer = System.currentTimeMillis();
                this.timeTargetPlayer = Util.nextInt(40000, 45000);
            }
            return this.playerAttack;
        }
    }

    public void joinMapMaster() {
        if (status != GOHOME && !isDie()) {
            this.location.x = masterr.location.x + Util.nextInt(-10, 10);
            this.location.y = masterr.location.y;
            ChangeMapService.gI().goToMap(this, masterr.zone);
            this.zone.load_Me_To_Another(this);
        }
    }

    private long lastTimeMoveAtHome;
    private byte directAtHome = -1;
    private long lastTimeMoveIdle;
    private int timeMoveIdle;
    public boolean idle;

    private void moveIdle() {
        if (status == GOHOME) {
            return;
        }
        if (idle && Util.canDoWithTime(lastTimeMoveIdle, timeMoveIdle)) {
            int dir = this.location.x - masterr.location.x <= 0 ? -1 : 1;
            PlayerService.gI().playerMove(this, masterr.location.x
                    + Util.nextInt(dir == -1 ? 30 : -50, dir == -1 ? 50 : 30), masterr.location.y);
            lastTimeMoveIdle = System.currentTimeMillis();
            timeMoveIdle = Util.nextInt(5000, 8000);
        }
    }

    public long LasttimeHs;

    private Mob mobAttack;
    private Player playerAttack;

    @Override
    public void update() {
        super.update();
        if (isDie()) {
            if (System.currentTimeMillis() - LasttimeHs > 30000) {
                Service.gI().hsChar(this, nPoint.hpMax, nPoint.mpMax);
            } else {
                return;
            }
        }
        if (justRevived1 && this.zone == masterr.zone) {
            Service.getInstance().chatJustForMe(masterr, this, "Ta Đã Sống Dậy Rồi Đây !!");
            justRevived1 = false;
        }
        if (masterr != null && (this.zone == null || this.zone != masterr.zone)) {
            joinMapMaster();
        }
        if (masterr != null && masterr.isDie() || effectSkill.isHaveEffectSkill()) {
            return;
        }
        if (Util.canDoWithTime(masterr.Autothucan, 900000) && masterr.trangthai == true) {
            if (masterr.inventory.gold < 10000) {
                Service.gI().sendThongBao(masterr,
                        "|7|Cần 10K Vàng");
                return;
            }
            masterr.inventory.gold -= 10000;
            masterr.ThucAnThan++;
            masterr.Autothucan = System.currentTimeMillis();
//            if (masterr.ThucAnThan > 200) {
//                masterr.CapBacThan = -1;
//                Service.gI().sendThongBao(masterr,
//                    "|7|Vì cho Đạo Lữ ăn quá no nên Đạo Lữ đã bạo thể mà chết.");
//                } else {
//                    Service.gI().sendThongBao(masterr,
//                        "|2|Thức ăn Đạo Lữ: " + masterr.ThucAnThan
//                        + "%\n|1|Bạn đã cho Đạo Lữ ăn\nLưu ý: khi cho quá 200% Đạo Lữ sẽ no quá mà chết");
//                }
        }
        if (Util.canDoWithTime(masterr.ThanLastTimeThucan, 600000)) {
            masterr.ThanLastTimeThucan = System.currentTimeMillis();
            masterr.ThucAnThan--;
            masterr.DameThan += Util.nextInt(50, 100);
            if (masterr.ThucAnThan <= 0) {
                masterr.CapBacThan = -1;
                Service.gI().sendThongBao(masterr, "|7|Thần Đã Chết Vì Bị Bỏ Đói");
            } else if (masterr.ThucAnThan > 0 && masterr.ThucAnThan <= 20) {
                Service.gI().sendThongBao(masterr, "|7|Thức Ăn Thần Dưới 20%");
            }
            ServerNotify.gI().sendNotifyTab(masterr);
        }
        moveIdle();
        switch (status) {
            case FOLLOW:
                followMaster(60);
                break;
            case ATTACK_PLAYER:
                Player plPr = getPlayerAttack();
                if (plPr != null && plPr != this && plPr != masterr && plPr.location != null) {
                    this.playerSkill.skillSelect = getSkill(1);
                    if (SkillService.gI().canUseSkillWithCooldown(this)) {
                        PlayerService.gI().playerMove(this, plPr.location.x + Util.nextInt(-60, 60),
                                plPr.location.y);
                        SkillService.gI().useSkill(this, plPr, null, null);
                    }
                } else {
                    idle = true;
                }
                break;
            case ATTACK_MOB:
                mobAttack = findMobAttack();
                if (mobAttack != null) {
                    this.playerSkill.skillSelect = getSkill(1);
                    if (SkillService.gI().canUseSkillWithCooldown(this)) {
                        PlayerService.gI().playerMove(this, mobAttack.location.x + Util.nextInt(-60, 60),
                                mobAttack.location.y);
                        SkillService.gI().useSkill(this, null, mobAttack, null);
                    }
                } else {
                    idle = true;
                }
                break;
            // thú triệu hồi về nhà phước
            case GOHOME:
                if (this.zone != null
                        && (this.zone.map.mapId == 2)) {
                    if (System.currentTimeMillis() - lastTimeMoveAtHome <= 5000) {
                        return;
                    } else {
                        if (this.zone.map.mapId == 2) {
                            if (directAtHome == -1) {

                                PlayerService.gI().playerMove(this, 208, 360);
                                directAtHome = 1;
                            } else {
                                PlayerService.gI().playerMove(this, 208, 360);
                                directAtHome = -1;
                            }
                        }
                        lastTimeMoveAtHome = System.currentTimeMillis();
                    }
                }
                break;
        }
    }

    private Skill getSkill(int indexSkill) {
        return this.playerSkill.skills.get(indexSkill - 1);
    }

    public void followMaster() {
        if (this.isDie() || effectSkill.isHaveEffectSkill()) {
            return;
        }
        switch (this.status) {
            case ATTACK_MOB:
                if (mobAttack != null && Util.getDistance(this, masterr) <= 1500) {
                    break;
                }
            case ATTACK_PLAYER:
                if (getPlayerAttack() != null && Util.getDistance(this, masterr) <= 1500) {
                    break;
                }
            case FOLLOW:
                followMaster(500);
                break;
        }
    }

    private void followMaster(int dis) {
        int mX = masterr.location.x;
        int mY = masterr.location.y;
        int disX = this.location.x - mX;
        if (Math.sqrt(Math.pow(mX - this.location.x, 2) + Math.pow(mY - this.location.y, 2)) >= dis) {
            if (disX < 0) {
                this.location.x = mX - Util.nextInt(0, dis);
            } else {
                this.location.x = mX + Util.nextInt(0, dis);
            }
            this.location.y = mY;
            PlayerService.gI().playerMove(this, this.location.x, this.location.y);
        }
    }

    @Override
    public void dispose() {
        if (zone != null) {
            ChangeMapService.gI().exitMap(this);
        }
        this.mobAttack = null;
        this.masterr = null;
        super.dispose();
    }
}
