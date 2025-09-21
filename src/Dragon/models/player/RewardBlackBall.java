package Dragon.models.player;

import Dragon.models.item.Item;
import Dragon.models.map.blackball.BlackBallWar;
import Dragon.services.InventoryServiceNew;
import Dragon.services.ItemService;
import Dragon.services.Service;
import Dragon.utils.TimeUtil;
import Dragon.utils.Util;

import java.util.Date;

public class RewardBlackBall {

    private static final int TIME_REWARD = 79200000;

    public static final int R1S_1 = 20;
    public static final int R1S_2 = 15;
    public static final int R2S_1 = 15;
    public static final int R2S_2 = 20;
    public static final int R3S_1 = 20;
    public static final int R3S_2 = 10;
    public static final int R4S_1 = 10;
    public static final int R4S_2 = 20;
    public static final int R5S_1 = 20;
    public static final int R5S_2 = 20;
    public static final int R5S_3 = 20;
    public static final int R6S_1 = 50;
    public static final int R6S_2 = 20;
    public static final int R7S_1 = 10;
    public static final int R7S_2 = 15;

    public static final int TIME_WAIT = 3600000;
    public static long time8h;
    private Player player;

    public long[] timeOutOfDateReward;
    public int[] quantilyBlackBall;
    public long[] lastTimeGetReward;

    public RewardBlackBall(Player player) {
        this.player = player;
        this.timeOutOfDateReward = new long[7];
        this.lastTimeGetReward = new long[7];
        this.quantilyBlackBall = new int[7];
        time8h = BlackBallWar.TIME_OPEN;
    }

    public void reward(byte star) {
        if (this.timeOutOfDateReward[star - 1] > time8h) {
            quantilyBlackBall[star - 1]++;
        }
        this.timeOutOfDateReward[star - 1] = System.currentTimeMillis() + TIME_REWARD;
        Service.gI().point(player);
    }

    public void getRewardSelect(byte select) {
        int index = 0;
        for (int i = 0; i < timeOutOfDateReward.length; i++) {
            if (timeOutOfDateReward[i] > System.currentTimeMillis()) {
                index++;
                if (index == select + 1) {
                    getReward(i + 1);
                    break;
                }
            }
        }
    }

    private void getReward(int star) {
        if (timeOutOfDateReward[star - 1] > System.currentTimeMillis()
                && Util.canDoWithTime(lastTimeGetReward[star - 1], TIME_WAIT)) {
            switch (star) {
                case 1:
                    if (player.blackballdata < 1) {
                        Service.gI().sendThongBao(player, "Bạn Đã Nhận Rồi");
                        return;
                    }
                    Item sao1den = ItemService.gI().createNewItem((short) (14));
                    sao1den.quantity += 0;
                    player.blackballdata -= 1;
                    InventoryServiceNew.gI().addItemBag(player, sao1den);
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.gI().sendThongBao(player, "Bạn Nhận Được" + sao1den.template.name);
                    break;
                case 2:
                    if (player.blackballdata < 1) {
                        Service.gI().sendThongBao(player, "Bạn Đã Nhận Rồi");
                        return;
                    }
                    Item sao2den = ItemService.gI().createNewItem((short) (15));
                    sao2den.quantity += 0;
                    player.blackballdata -= 1;
                    InventoryServiceNew.gI().addItemBag(player, sao2den);
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.gI().sendThongBao(player, "Bạn Nhận Được" + sao2den.template.name);
                    break;
                case 3:
                    if (player.blackballdata < 1) {
                        Service.gI().sendThongBao(player, "Bạn Đã Nhận Rồi");
                        return;
                    }
                    Item sao3den = ItemService.gI().createNewItem((short) (16));
                    sao3den.quantity += 0;
                    player.blackballdata -= 1;
                    InventoryServiceNew.gI().addItemBag(player, sao3den);
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.gI().sendThongBao(player, "Bạn Nhận Được" + sao3den.template.name);
                    break;
                case 4:
                    if (player.blackballdata < 1) {
                        Service.gI().sendThongBao(player, "Bạn Đã Nhận Rồi");
                        return;
                    }
                    Item sao4den = ItemService.gI().createNewItem((short) (17));
                    sao4den.quantity += 0;
                    player.blackballdata -= 1;
                    InventoryServiceNew.gI().addItemBag(player, sao4den);
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.gI().sendThongBao(player, "Bạn Nhận Được" + sao4den.template.name);
                    break;
                case 5:
                    if (player.blackballdata < 1) {
                        Service.gI().sendThongBao(player, "Bạn Đã Nhận Rồi");
                        return;
                    }
                    Item sao5den = ItemService.gI().createNewItem((short) (18));
                    sao5den.quantity += 1;
                    player.blackballdata -= 1;
                    InventoryServiceNew.gI().addItemBag(player, sao5den);
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.gI().sendThongBao(player, "Bạn Nhận Được" + sao5den.template.name);
                    break;
                case 6:
                    if (player.blackballdata < 1) {
                        Service.gI().sendThongBao(player, "Bạn Đã Nhận Rồi");
                        return;
                    }
                    Item sao6den = ItemService.gI().createNewItem((short) (19));
                    sao6den.quantity += 2;
                    player.blackballdata -= 1;
                    InventoryServiceNew.gI().addItemBag(player, sao6den);
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.gI().sendThongBao(player, "Bạn Nhận Được" + sao6den.template.name);
                    break;
                case 7:
                    if (player.blackballdata < 1) {
                        Service.gI().sendThongBao(player, "Bạn Đã Nhận Rồi");
                        return;
                    }
                    Item sao7den = ItemService.gI().createNewItem((short) (20));
                    sao7den.quantity += 3;
                    player.blackballdata -= 1;
                    InventoryServiceNew.gI().addItemBag(player, sao7den);
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.gI().sendThongBao(player, "Bạn Nhận Được" + sao7den.template.name);
                    break;

            }
        } else {
            Service.gI().sendThongBao(player, "Chưa Thể Nhận Phần Quà Ngay Lúc Này, Vui Lòng Đợi "
                    + TimeUtil.diffDate(new Date(lastTimeGetReward[star - 1]), new Date(lastTimeGetReward[star - 1] + TIME_WAIT),
                            TimeUtil.MINUTE) + " Phút Nữa");
        }
    }

    public void dispose() {
        this.player = null;
    }
}
