//package Dragon.models.boss.list_boss.Broly;
//
//import Dragon.models.boss.Boss;
//import Dragon.models.boss.BossID;
//import Dragon.models.boss.BossStatus;
//import Dragon.models.boss.BossesData;
//import Dragon.models.map.ItemMap;
//import Dragon.models.player.Player;
//import Dragon.services.EffectSkillService;
//import Dragon.services.Service;
//import Dragon.utils.Util;
//import java.util.Random;
//
//
////public class BrolyClone extends Boss {
//
// //   public BrolyClone() throws Exception {
//  //      super(BossID.BROLY, BossesData.BROLY_CLONE);
//   // }
//    
//    @Override
//    public void active() {
//        super.active();
//        if(Util.canDoWithTime(st,300000)){
//            this.changeStatus(BossStatus.LEAVE_MAP);
//        }
//    }
//    
//    @Override
//    public void joinMap() {
//        super.joinMap();
//        st= System.currentTimeMillis();
//    }
//    private long st;
//    
//        @Override
//    public void moveTo(int x, int y) {
//        if(this.currentLevel == 1){
//            return;
//        }
//        super.moveTo(x, y);
//    }
//    
//    @Override
//    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
//        if (!this.isDie()) {
//            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
//                this.chat("Xí hụt");
//                return 0;
//            }
//            damage = this.nPoint.subDameInjureWithDeff(damage/2);
//            if (!piercing && effectSkill.isShielding) {
//                if (damage > nPoint.hpMax) {
//                    EffectSkillService.gI().breakShield(this);
//                }
//                damage = damage/2;
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
//}
