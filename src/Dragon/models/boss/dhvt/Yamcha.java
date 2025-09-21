package Dragon.models.boss.dhvt;

import Dragon.models.boss.BossID;
import Dragon.models.boss.BossesData;
import Dragon.models.boss.dhvt.BossDHVT;
import Dragon.models.player.Player;

/**
 *
 * @author BTH fix
 */
public class Yamcha extends BossDHVT {

    public Yamcha(Player player) throws Exception {
        super(BossID.YAMCHA, BossesData.YAMCHA);
        this.playerAtt = player;
    }
}
