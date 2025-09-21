package Dragon.models.sieuhang;

import Dragon.models.boss.BossData;
import Dragon.models.boss.dhvt.BossDHVT;
import Dragon.models.player.Player;
import Dragon.utils.Util;

public class ClonePlayer extends BossDHVT {

    public ClonePlayer(Player player, BossData data, int id) throws Exception {
        super(Util.randomBossId(), data, 5000);
        this.playerAtt = player;
        this.idPlayer = id;
    }
}
