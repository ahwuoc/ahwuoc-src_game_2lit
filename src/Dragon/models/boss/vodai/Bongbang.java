package Dragon.models.boss.vodai;

import Dragon.models.boss.BossData;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossesData;
import Dragon.models.player.Player;

/**
 * @author BTH sieu cap vippr0
 */
public class Bongbang extends BossVD {

    public Bongbang(Player player) throws Exception {
        super(BossID.BONGBANG, BossesData.BONGBANG);
        this.playerAtt = player;
    }
}
