package Dragon.models.boss.dhvt;

import Dragon.models.boss.BossData;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossesData;
import Dragon.models.player.Player;

/**
 * @author BTH sieu cap vippr0
 */
public class ChanXu extends BossDHVT {

    public ChanXu(Player player) throws Exception {
        super(BossID.CHAN_XU, BossesData.CHAN_XU);
        this.playerAtt = player;
    }
}
