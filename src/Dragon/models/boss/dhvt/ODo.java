package Dragon.models.boss.dhvt;

import Dragon.models.boss.BossData;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossesData;
import Dragon.models.player.Player;

/**
 * @author BTH sieu cap vippr0
 */
public class ODo extends BossDHVT {

    public ODo(Player player) throws Exception {
        super(BossID.O_DO, BossesData.O_DO);
        this.playerAtt = player;
    }
}
