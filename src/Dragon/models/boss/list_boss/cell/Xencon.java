package Dragon.models.boss.list_boss.cell;

import Dragon.consts.ConstPlayer;
import Dragon.models.boss.Boss;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossManager;
import Dragon.models.boss.BossesData;
import Dragon.models.map.ItemMap;
import Dragon.models.player.Player;
import Dragon.models.skill.Skill;
import Dragon.server.Manager;
import Dragon.services.EffectSkillService;
import Dragon.services.Service;
import Dragon.services.TaskService;
import Dragon.utils.Util;
import Dragon.services.PlayerService;
import Dragon.services.func.ChangeMapService;
import java.util.Random;

public class Xencon extends Boss {

    private long lastTimeHapThu;
    private int timeHapThu;

    public Xencon() throws Exception {
        super(BossID.XEN_CON_1, BossesData.XEN_CON);
    }

    @Override
    public void active() {
        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }
        this.hapThu();
        this.attack();
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

    private void hapThu() {
        if (!Util.canDoWithTime(this.lastTimeHapThu, this.timeHapThu) || !Util.isTrue(1, 100)) {
            return;
        }

        Player pl = this.zone.getRandomPlayerInMap();
        if (pl == null || pl.isDie()) {
            return;
        }
        double HP = this.nPoint.hp + (this.nPoint.hpg * 0.2);
        if (HP > 2000000000) {
            HP = 2000000000;
        }
        if (this.nPoint.hpg < HP) {
            this.nPoint.hpg = (int) HP;
        }
        this.nPoint.hp = (int) HP;
        this.nPoint.critg++;
        PlayerService.gI().hoiPhuc(this, pl.nPoint.hp, 0);
        pl.injured(null, (long) pl.nPoint.hpMax, true, false);
        Service.gI().sendThongBao(pl, "Bạn vừa bị " + this.name + " hấp thu!");
        this.chat(2, "Ui cha cha, kinh dị quá. " + pl.name + " vừa bị tên " + this.name + " nuốt chửng kìa!!!");
        this.chat("Haha, ngọt lắm đấy " + pl.name + "..");
        this.lastTimeHapThu = System.currentTimeMillis();
        this.timeHapThu = Util.nextInt(15000, 20000);
    }

}
