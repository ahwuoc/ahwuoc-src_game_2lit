/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dragon.models.boss.list_boss.ginyu;

import Dragon.consts.ConstPlayer;
import Dragon.models.boss.Boss;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossManager;
import Dragon.models.boss.BossStatus;
import Dragon.models.boss.BossesData;
import Dragon.models.map.ItemMap;
import Dragon.models.player.Player;
import Dragon.services.Service;
import Dragon.services.TaskService;
import Dragon.utils.Util;

/**
 *
 * @author Administrator
 */
public class So1 extends Boss {

    public So1() throws Exception {
        super(BossID.SO_1, BossesData.SO_1);
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(15, 100)) {
            ItemMap it = new ItemMap(this.zone, 17, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.gI().dropItemMap(this.zone, it);
        }
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }

    @Override
    protected void notifyJoinMap() {
        if (this.currentLevel == 1000000000) {
            return;
        }
        super.notifyJoinMap();
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    private long st;

    @Override
    public void active() {
        if (BossManager.gI().getBossByName("Số 2").zone != null) {
            this.changeToTypeNonPK();
        } else {
            if (this.typePk == ConstPlayer.NON_PK) {
                this.changeToTypePK();
            }
            this.attack();
        }
    }

}
