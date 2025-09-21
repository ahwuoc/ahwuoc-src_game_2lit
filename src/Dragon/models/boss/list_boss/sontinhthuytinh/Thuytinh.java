///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package Dragon.models.boss.list_boss.sontinhthuytinh;
//
//import Dragon.consts.ConstPlayer;
//import Dragon.models.boss.Boss;
//import Dragon.models.boss.BossData;
//import Dragon.models.boss.BossID;
//import Dragon.models.boss.BossManager;
//import Dragon.models.boss.BossStatus;
//import Dragon.models.boss.BossesData;
//import Dragon.models.item.Item;
//import Dragon.models.map.ItemMap;
//import Dragon.models.map.Zone;
//import Dragon.models.player.Player;
//import Dragon.models.skill.Skill;
//import Dragon.services.EffectSkillService;
//import Dragon.services.InventoryServiceNew;
//import Dragon.services.PlayerService;
//import Dragon.services.Service;
//import Dragon.services.SkillService;
//import Dragon.services.TaskService;
//import Dragon.utils.Logger;
//import Dragon.utils.SkillUtil;
//import Dragon.utils.Util;
//import java.util.Random;
//
///**
// *
// * @author Khánh Đẹp Zoai
// */
//public class Thuytinh extends Boss {
//
//    public Thuytinh() throws Exception {
//        super(BossID.THUY_TINH, BossesData.THUY_TINH);
//        this.cFlag = 10;
//    }
//
//    @Override
//    public void reward(Player plKill) {
//        int[] itemDos = new int[]{421, 422};
//        int randomnro = new Random().nextInt(itemDos.length);
//        if (Util.isTrue(50, 100)) {
//            Service.gI().dropItemMap(this.zone, Util.sukienhungvuong(zone, itemDos[randomnro], 1, this.location.x, this.location.y, plKill.id));
//        }
//        plKill.point_hungvuong += 10;
//        Service.gI().sendThongBao(plKill, "Bạn Vừa Nhận Được Một Điểm Hùng Vương");
//        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
//    }
//
//    @Override
//    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
//        if (plAtt.cFlag != 9) {// tỉ lệ hụt của thiên sứ
//            this.chat("|7|Không có kiếm mà đòi đánh ta hả");
//            damage = 0;
//
//        }
//        if (!this.isDie()) {
//            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
//                this.chat("Xí hụt");
//                return 0;
//            }
//            damage = this.nPoint.subDameInjureWithDeff(damage);
//            if (!piercing && effectSkill.isShielding) {
//                if (damage > nPoint.hpMax) {
//                    EffectSkillService.gI().breakShield(this);
//                }
//                damage = damage / 2;
//            }
//            this.nPoint.subHP(damage);
//            if (isDie()) {
//                this.setDie(plAtt);
//                die(plAtt);
//            }
//            return damage;
//        } else {
//            return 0;
//        }
//    }
//
//    @Override
//    public void active() {
//        super.active();
//        this.attack();
//        this.changeToTypeNonPK();
//        // Service.gI().changeFlag(this, 10);
//        if (Util.canDoWithTime(st, 1800000)) {
//            this.changeStatus(BossStatus.LEAVE_MAP);
//        }
//        if (BossManager.gI().getBossById(BossID.SON_TINH) == null) {
//            this.leaveMap();
//        }
//        if (System.currentTimeMillis() - lastTimeBlame > 10000) {
//            this.chat("|7|Hãy về phe của ta nếu không ngươi sẽ phải chịu hậu quả");
//            lastTimeBlame = System.currentTimeMillis();
//        }
//    }
//
//    @Override
//    public void attack() {
//        if (Util.canDoWithTime(this.lastTimeAttack, 100)) {
//            this.lastTimeAttack = System.currentTimeMillis();
//            try {
//                Player pl = getPlayerAttack();
//                if (pl == null || pl.isDie()) {
//                    return;
//                }
//                this.playerSkill.skillSelect = this.playerSkill.skills
//                        .get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
//                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
//                    if (Util.isTrue(5, 20)) {
//                        if (SkillUtil.isUseSkillChuong(this)) {
//                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
//                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70));
//                        } else {
//                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
//                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50));
//                        }
//                    }
//                    SkillService.gI().useSkill(this, pl, null, null);
//                    checkPlayerDie(pl);
//                } else {
//                    if (Util.isTrue(1, 2)) {
//                        this.moveToPlayer(pl);
//                    }
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void joinMap() {
//        super.joinMap(); // To change body of generated methods, choose Tools | Templates.
//        st = System.currentTimeMillis();
//    }
//
//    private long st;
//    private long lastTimeBlame;
//}
