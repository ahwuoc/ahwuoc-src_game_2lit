/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dragon.models.player;

import Dragon.models.mob.Mob;
import Dragon.models.skill.Skill;
import Dragon.services.MapService;
import Dragon.services.PlayerService;
import Dragon.services.SkillService;
import Dragon.services.func.ChangeMapService;
import Dragon.utils.SkillUtil;
import Dragon.utils.Util;

/**
 *
 * @author Administrator
 */
public class Clone extends Player {

    public Player mainPl;
    public long lastTimeRevival;

    public Clone(Player plMaster) {
        this.mainPl = plMaster;
        this.isClone = true;
        this.id = -plMaster.id * 2 + Util.nextInt(1, 100000);
        this.lastTimeRevival = System.currentTimeMillis()
                + (1000 * 60 * 5 * plMaster.playerSkill.getSkillbyId(Skill.PHAN_THAN).point);
    }

    @Override
    public byte getAura() {
        return mainPl.getAura();
    }

    @Override
    public byte getEffFront() {
        return mainPl.getEffFront();
    }

    @Override
    public short getHead() {
        return mainPl.getHead();
    }

    @Override
    public short getBody() {
        return mainPl.getBody();
    }

    @Override
    public short getLeg() {
        return mainPl.getLeg();
    }

    @Override
    public short getFlagBag() {
        return mainPl.getFlagBag();
    }

    @Override
    public short getMount() {
        return mainPl.getMount();
    }

    public void joinMapMaster() {
        if (mainPl == null) {
            return;
        }
        this.location.x = mainPl.location.x + Util.nextInt(-10, 10);
        this.location.y = mainPl.location.y;
        ChangeMapService.gI().goToMap(this, mainPl.zone);
        this.zone.load_Me_To_Another(this);
    }

    @Override
    public void update() {
        super.update();
        // System.out.println("update");
        try {
            if (mainPl != null && mainPl.zone == null) {
                ChangeMapService.gI().exitMap(this);
                // Remove from clones list
                mainPl.clones.remove(this);
                // Keep backward compatibility
                if (mainPl.clone == this) {
                    mainPl.clone.dispose();
                    mainPl.clone = null;
                }
                // System.out.println("set null");
            }
            if (this.isDie()) {
                ChangeMapService.gI().exitMap(this);
            }
            if (mainPl != null && (this.zone == null || this.zone != mainPl.zone)) {
                // System.out.println("joinmap");
                joinMapMaster();
            }
            if (mainPl != null && mainPl.isDie() || effectSkill.isHaveEffectSkill()) {
                return;
            }
            if (Util.canDoWithTime(this.lastTimeRevival, 1000)) {
                ChangeMapService.gI().exitMap(this);
                this.dispose();
                // System.out.println("check time");
            }
            if (mainPl != null) {
                followMaster(60);
            }
        } catch (Exception e) {
        }
    }

    public void attackWithMaster(Player plAtt, Mob mAtt) {
        if (plAtt != null) {
            if (SkillUtil.isUseSkillDam(this)) {
                PlayerService.gI().playerMove(this, plAtt.location.x + Util.nextInt(-60, 60), plAtt.location.y);
            }
            SkillService.gI().useSkillAttack(this, plAtt, null);
        } else if (mAtt != null) {
            if (SkillUtil.isUseSkillDam(this)) {
                PlayerService.gI().playerMove(this, mAtt.location.x + Util.nextInt(-60, 60), mAtt.location.y);
            }
            SkillService.gI().useSkillAttack(this, null, mAtt);
        }
    }

    public void followMaster() {
        if (mainPl == null || this.isDie() || effectSkill.isHaveEffectSkill()) {
            return;
        }
        followMaster(60);
    }

    private void followMaster(int dis) {
        int mX = mainPl.location.x;
        int mY = mainPl.location.y;
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

    public static void callPoint(Player me) {
        me.clones.removeIf(clone -> clone == null || clone.zone == null);
        Clone clone = new Clone(me);
        clone.name = " Phân Thân " + me.name + " #" + (me.clones.size() + 1);
        clone.gender = me.gender;
        clone.nPoint.hpg = me.nPoint.hpg;
        clone.nPoint.mpg = me.nPoint.mpg;
        clone.nPoint.hp = me.nPoint.hp;
        clone.nPoint.mp = me.nPoint.mp;
        clone.nPoint.dameg = me.nPoint.dame / 5;
        clone.nPoint.defg = me.nPoint.def;
        clone.nPoint.critg = me.nPoint.critg;
        clone.nPoint.crit = me.nPoint.crit;
        clone.nPoint.stamina = me.nPoint.stamina;
        clone.nPoint.maxStamina = me.nPoint.maxStamina;
        clone.inventory = me.inventory;
        clone.playerSkill.skills = me.playerSkill.skills;
        clone.nPoint.setBasePoint();
        clone.nPoint.setFullHpMp();
        clone.nPoint.calPoint();
        clone.joinMapMaster();

        // Add to clones list
        me.clones.add(clone);

        // Keep backward compatibility
        me.clone = clone;
    }

    @Override
    public void dispose() {
        if (zone != null) {
            ChangeMapService.gI().exitMap(this);
        }
        this.mainPl = null;
    }
}
