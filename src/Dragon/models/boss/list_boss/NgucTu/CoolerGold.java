/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dragon.models.boss.list_boss.NgucTu;

import Dragon.models.boss.Boss;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossManager;
import Dragon.models.boss.BossStatus;
import Dragon.models.boss.BossesData;
import Dragon.models.map.ItemMap;
import Dragon.models.player.Player;
import Dragon.models.skill.Skill;
import Dragon.services.EffectSkillService;
import Dragon.services.PetService;
import Dragon.services.Service;
import Dragon.utils.Util;

import java.util.Random;

public class CoolerGold extends Boss {

    public CoolerGold() throws Exception {
        super(BossID.COOLER_GOLD, BossesData.COOLER_GOLD);
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    private long st;

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 900000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(5, 100)) { // 5% cơ hội rơi item
            int[] itemIds = {1744, 1745, 1746}; // Danh sách 3 item có thể rơi
            int randomIndex = Util.nextInt(0, itemIds.length - 1); // Chọn ngẫu nhiên 1 item trong 3
            int itemId = itemIds[randomIndex]; // Lấy ID item ngẫu nhiên

            Service.gI().dropItemMap(this.zone, new ItemMap(zone, itemId, 2, this.location.x, this.location.y, plKill.id));
        } else {
            // Nếu không rơi 1 trong 3 item trên, thực hiện rơi item trong else
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, Util.nextInt(16, 20), 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        super.dispose();
    }
}
