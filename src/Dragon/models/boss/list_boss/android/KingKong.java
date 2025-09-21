package Dragon.models.boss.list_boss.android;

import Dragon.models.boss.Boss;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossStatus;
import Dragon.models.boss.BossesData;
import Dragon.models.map.ItemMap;
import Dragon.models.player.Player;
import Dragon.services.Service;
import Dragon.services.TaskService;
import Dragon.utils.Util;

public class KingKong extends Boss {

    public KingKong() throws Exception {
        super(BossID.KING_KONG, BossesData.KING_KONG);
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(50, 100)) {
            ItemMap it = new ItemMap(this.zone, 16, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.gI().dropItemMap(this.zone, it);
        } else {
            ItemMap it = new ItemMap(this.zone, 15, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.gI().dropItemMap(this.zone, it);
        }
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    private long st;

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 900000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

//    @Override
//    public void doneChatS() {
//        if (this.bossAppearTogether == null || this.bossAppearTogether[this.currentLevel] == null) {
//            return;
//        }
//        for (Boss boss : this.bossAppearTogether[this.currentLevel]) {
//            if(boss.id == BossID.POC && !boss.isDie()){
//                boss.changeToTypePK();
//                break;
//            }
//        }
//    }
}

/**
 * Vui lòng không sao chép mã nguồn này dưới mọi hình thức. Hãy tôn trọng tác
 * giả của mã nguồn này. Xin cảm ơn! - GirlBeo
 */
