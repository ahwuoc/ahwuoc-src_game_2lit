package Dragon.models.boss.vodai;

import Dragon.models.boss.BossData;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossesData;
import Dragon.models.player.Player;

/**
 * @author BTH sieu cap vippr0
 */
public class Nguoivohinh extends BossVD {

    public Nguoivohinh(Player player) throws Exception {
        super(BossID.NGUOIVOHINH, BossesData.NGUOIVOHINH);
        this.playerAtt = player;
    }
}
