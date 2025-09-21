package Dragon.models.boss.list_boss;

import Dragon.models.boss.Boss;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossStatus;
import Dragon.models.boss.BossesData;
import Dragon.models.map.ItemMap;
import Dragon.models.player.Player;
import Dragon.services.EffectSkillService;
import Dragon.services.InventoryServiceNew;
import Dragon.services.Service;
import Dragon.utils.Util;
import java.util.Random;

/**
 *
 * @author ADMIN
 */
public class AnTrom extends Boss {

    private long antrom;
    private long time;

    public AnTrom() throws Exception {
        super(BossID.ANTROM, BossesData.ANTROM);
    }

    private void antrom() {
        // Kiểm tra thời gian cho phép ăn trộm
        if (!Util.canDoWithTime(this.time, this.antrom)) {
            return;
        }
        // Lấy một người chơi ngẫu nhiên trong khu vực đang hoạt động của Boss
        Player pl = this.zone.getRandomPlayerInMap();
        if (pl == null || pl.isDie() || !pl.getSession().actived) {
            return;
        }
        // Kiểm tra số vàng và hoạt động của người chơi
        if (pl.inventory.gold <= 1000000) {
            this.chat("Không Đủ Vàng Để Ăn Trộm!");
            return;
        }
        // ăn trộm vàng của người chơi
        int stolenGold = Util.nextInt(1000, 5000);
        pl.inventory.gold -= stolenGold;
        this.inventory.gold += stolenGold;
        // Thông báo ăn trộm trên kênh chat
        this.chat("Haha, Tôi Đã Ăn Trộm Được " + stolenGold + " Vàng Rồi!!");
        // Cập nhật thời gian ăn trộm lần cuối và thời gian cho phép ăn trộm tiếp theo
        this.time = System.currentTimeMillis();
        this.antrom = 4000;
        // Gửi thông tin vàng mới của người chơi và cập nhật trên máy chủ
        Service.gI().sendMoney(pl);
        InventoryServiceNew.gI().sendItemBags(pl);
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
        if (Util.isTrue(97, 100)) {
            int goldReward = (int) (this.inventory.gold * Util.nextInt(30, 50) / 100);
            Service.getInstance().dropItemMap(
                    this.zone,
                    Util.manhTS(zone, 76, goldReward, this.location.x, this.location.y, plKill.id)
            );
            Service.gI().sendThongBaoAllPlayer(plKill.name + " Vừa Tiêu Diệt Ăn Trộm Và Nhận Được " + goldReward + " Vàng");
            plKill.inventory.event += 1;
        }
    }
}
