package Dragon.models.boss.list_boss.TrainOffline;

import Dragon.models.boss.*;
import Dragon.models.map.Zone;
import Dragon.models.player.Player;

/**
 * @Stole By MITCHIKEN ZALO 0358689793
 */
public class ToSuKaio extends TrainBoss {

    public ToSuKaio(byte bossID, BossData bossData, Zone zone, int x, int y) throws Exception {
        super(BossID.TS_KAIO, BossesData.TO_SU_KAIO, zone, x, y);
    }

    @Override
    public void reward(Player plKill) {
        //vật phẩm rơi khi diệt boss nhân bản

        plKill.rsfight();
        this.chat("Hôm nay ta không được khỏe");
        this.playerkill = plKill;

    }

    @Override
    public void attack() {
        if (this.playerTarger == null || this.playerTarger.isDie()) {
            this.leaveMap();
        }
    }

    @Override
    public void active() {

    }

}
