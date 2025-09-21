package Dragon.models.boss.list_boss.nappa;

import Dragon.models.boss.Boss;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossStatus;
import Dragon.models.boss.BossesData;
import Dragon.models.map.ItemMap;
import Dragon.models.player.Player;
import Dragon.models.skill.Skill;
import Dragon.services.PetService;
import Dragon.services.Service;
import Dragon.services.TaskService;
import Dragon.utils.Util;

public class Rambo extends Boss {

    public Rambo() throws Exception {
        super(BossID.RAMBO, BossesData.RAMBO);
    }

    @Override
    public void active() {
        super.active();
    }

    @Override
    public void joinMap() {
        super.joinMap();
    }
}

/**
 * Vui lòng không sao chép mã nguồn này dưới mọi hình thức. Hãy tôn trọng tác
 * giả của mã nguồn này. Xin cảm ơn! - GirlBeo
 */
