package Dragon.models.boss.list_boss.TrainOffline;

import Dragon.models.boss.*;
import Dragon.models.map.Zone;
import Dragon.models.player.Player;
import Dragon.services.TaskService;

/**
 * @Stole By MITCHIKEN ZALO 0358689793
 */
public class ThanMeo extends TrainBoss {

    public ThanMeo(byte bossID, BossData bossData, Zone zone, int x, int y) throws Exception {
        super(BossID.MEO_THAN, BossesData.THAN_MEO, zone, x, y);
    }

    @Override
    public void reward(Player plKill) {
        //vật phẩm rơi khi diệt boss nhân bản
        if (plKill.isfight1) {
            plKill.typetrain++;
            TaskService.gI().checkDoneTaskKillBoss(plKill, this);
        }
        plKill.rsfight();
        this.chat("Hôm nay ta không được khỏe");
        this.playerkill = plKill;

    }

}
