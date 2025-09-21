package Dragon.models.boss.vodai;

import Dragon.models.boss.BossData;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossesData;
import Dragon.models.player.Player;

/**
 * @author BTH sieu cap vippr0
 */
public class Satan extends BossVD {

    public Satan(Player player) throws Exception {
        super(BossID.SATAN, BossesData.SATAN);
        this.playerAtt = player;
    }
}
