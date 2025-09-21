package Dragon.models.boss.dhvt;

import Dragon.models.boss.BossData;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossesData;
import Dragon.models.player.Player;

/**
 * @author BTH sieu cap vippr0
 */
public class ChaPa extends BossDHVT {

    public ChaPa(Player player) throws Exception {
        super(BossID.CHA_PA, BossesData.CHA_PA);
        this.playerAtt = player;
    }
}
