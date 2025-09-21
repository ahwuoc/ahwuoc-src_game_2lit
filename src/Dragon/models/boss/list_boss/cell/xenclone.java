/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dragon.models.boss.list_boss.cell;

import Dragon.consts.ConstPlayer;
import Dragon.models.boss.*;
import static Dragon.models.boss.BossStatus.ACTIVE;
import static Dragon.models.boss.BossStatus.JOIN_MAP;
import static Dragon.models.boss.BossStatus.RESPAWN;
import Dragon.models.map.ItemMap;
import Dragon.models.map.Zone;
import Dragon.models.map.challenge.MartialCongressService;
import Dragon.models.player.Player;
import Dragon.models.skill.Skill;
import Dragon.services.EffectSkillService;
import Dragon.services.PlayerService;
import Dragon.services.Service;
import Dragon.services.SkillService;
import Dragon.services.func.ChangeMapService;
import Dragon.utils.SkillUtil;
import Dragon.utils.Util;

public class xenclone extends Boss {

    private long lastUpdate = System.currentTimeMillis();
    private long timeJoinMap;
    protected Player playerAtt;
    private int timeLive = 200000000;

    public xenclone(Zone zone, long dame, long hp, int id) throws Exception {
        super(id, new BossData(
                "Cell con hộ tống", //name 264	265	266
                ConstPlayer.TRAI_DAT, //gender
                new short[]{264, 265, 266, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
                ((10000)), //dame
                new long[]{((100000))}, //hp
                new int[]{143}, //map join
                new int[][]{
                    {Skill.DEMON, 3, 1}, {Skill.DEMON, 6, 2}, {Skill.DRAGON, 7, 3}, {Skill.DRAGON, 1, 4}, {Skill.GALICK, 5, 5},
                    {Skill.KAMEJOKO, 7, 6}, {Skill.KAMEJOKO, 6, 7}, {Skill.KAMEJOKO, 5, 8}, {Skill.KAMEJOKO, 4, 9}, {Skill.KAMEJOKO, 3, 10}, {Skill.KAMEJOKO, 2, 11}, {Skill.KAMEJOKO, 1, 12},
                    {Skill.ANTOMIC, 1, 13}, {Skill.ANTOMIC, 2, 14}, {Skill.ANTOMIC, 3, 15}, {Skill.ANTOMIC, 4, 16}, {Skill.ANTOMIC, 5, 17}, {Skill.ANTOMIC, 6, 19}, {Skill.ANTOMIC, 7, 20},
                    {Skill.MASENKO, 1, 21}, {Skill.MASENKO, 5, 22}, {Skill.MASENKO, 6, 23},},
                new String[]{}, //text chat 1
                new String[]{"|-1|HuHu Hu Hu, Ta bắt nạt các ngươi, HuHu HuHu"}, //text chat 2
                new String[]{}, //text chat 3
                60
        ));
        this.zone = zone;
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(100, 100)) {
            Service.gI().dropItemMap(this.zone, Util.ratiItem(zone, 861, 10, this.location.x, this.location.y, plKill.id));
            ItemMap it1 = new ItemMap(this.zone, 861, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            ItemMap it2 = new ItemMap(this.zone, 861, 1, this.location.x + 10, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 23), plKill.id);
            ItemMap it3 = new ItemMap(this.zone, 861, 1, this.location.x + 10, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 22), plKill.id);
            ItemMap it4 = new ItemMap(this.zone, 861, 1, this.location.x + 10, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 21), plKill.id);
            ItemMap it5 = new ItemMap(this.zone, 861, 1, this.location.x + 10, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 20), plKill.id);
            ItemMap it6 = new ItemMap(this.zone, 861, 1, this.location.x + 10, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 25), plKill.id);
            ItemMap it7 = new ItemMap(this.zone, 861, 1, this.location.x + 10, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 26), plKill.id);
            ItemMap it8 = new ItemMap(this.zone, 861, 1, this.location.x + 10, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 27), plKill.id);
            ItemMap it9 = new ItemMap(this.zone, 861, 1, this.location.x + 10, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 28), plKill.id);
            ItemMap it10 = new ItemMap(this.zone, 861, 1, this.location.x + 10, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 28), plKill.id);
            Service.getInstance().dropItemMap(this.zone, it1);
            Service.getInstance().dropItemMap(this.zone, it2);
            Service.getInstance().dropItemMap(this.zone, it3);
            Service.getInstance().dropItemMap(this.zone, it4);
            Service.getInstance().dropItemMap(this.zone, it5);
            Service.getInstance().dropItemMap(this.zone, it6);
            Service.getInstance().dropItemMap(this.zone, it7);
            Service.getInstance().dropItemMap(this.zone, it8);
            Service.getInstance().dropItemMap(this.zone, it9);
            Service.getInstance().dropItemMap(this.zone, it10);
        }
    }

    @Override
    public void active() {
        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }
        try {
            switch (this.bossStatus) {
                case RESPAWN:
                    this.respawn();
                    this.changeStatus(BossStatus.JOIN_MAP);
                case JOIN_MAP:
                    joinMap();
                    if (this.zone != null) {
                        changeStatus(BossStatus.ACTIVE);
                        timeJoinMap = System.currentTimeMillis();
                        this.typePk = 3;
                        MartialCongressService.gI().sendTypePK(playerAtt, this);
                        PlayerService.gI().changeAndSendTypePK(playerAtt, ConstPlayer.PK_PVP);
                        this.changeStatus(BossStatus.ACTIVE);
                    }
                    break;
                case ACTIVE:
                    if (this.playerSkill.prepareTuSat || this.playerSkill.prepareLaze || this.playerSkill.prepareQCKK) {
                        break;
                    } else {
                        this.attack();
                    }
                    break;
            }
            if (Util.canDoWithTime(lastUpdate, 1000)) {
                lastUpdate = System.currentTimeMillis();
                if (timeLive > 0) {
                    timeLive--;
                } else {
                    super.leaveMap();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 1000;
            }
            damage = this.nPoint.subDameInjureWithDeff(1000);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1000;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return 1000;
        } else {
            return 0;
        }

    }
}
