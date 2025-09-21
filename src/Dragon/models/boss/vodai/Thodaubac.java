package Dragon.models.boss.vodai;

import Dragon.models.boss.BossData;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossesData;
import Dragon.models.player.Player;

/**
 * @author BTH sieu cap vippr0
 */
public class Thodaubac extends BossVD {

    public Thodaubac(Player player) throws Exception {
        super(BossID.THODAUBAC, BossesData.THODAUBAC);
        this.playerAtt = player;
    }
}
