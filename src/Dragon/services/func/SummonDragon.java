package Dragon.services.func;

import java.util.HashMap;
import java.util.Map;
import Dragon.models.item.Item;
import Dragon.consts.ConstNpc;
import Dragon.consts.ConstPlayer;
import Dragon.jdbc.daos.GodGK;
import Dragon.jdbc.daos.PlayerDAO;
import Dragon.models.item.Item.ItemOption;
import Dragon.models.map.Zone;
import Dragon.services.NpcService;
import Dragon.models.player.Inventory;
import Dragon.models.player.Player;
import Dragon.services.Service;
import Dragon.utils.Util;
import com.girlkun.network.io.Message;
import Dragon.server.Client;
import Dragon.services.ItemService;
import Dragon.services.PlayerService;
import Dragon.services.InventoryServiceNew;
import Dragon.utils.Logger;
import java.util.List;

public class SummonDragon {

    public static final byte WISHED = 0;
    public static final byte TIME_UP = 1;

    public static final byte DRAGON_SHENRON = 0;
    public static final byte DRAGON_PORUNGA = 1;

    public static final short NGOC_RONG_1_SAO = 14;
    public static final short NGOC_RONG_2_SAO = 15;
    public static final short NGOC_RONG_3_SAO = 16;
    public static final short NGOC_RONG_4_SAO = 17;
    public static final short NGOC_RONG_5_SAO = 18;
    public static final short NGOC_RONG_6_SAO = 19;
    public static final short NGOC_RONG_7_SAO = 20;

    public static final String SUMMON_SHENRON_TUTORIAL
            = "Có 3 Cách Gọi Rồng Thần. Gọi Từ Ngọc 1 Sao, Gọi Từ Ngọc 2 Sao, Hoặc Gọi Từ Ngọc 3 Sao\n"
            + "Các Ngọc 4 Sao Đến 7 Sao Không Thể Gọi Rồng Thần Được\n"
            + "Để Gọi Rồng 1 Sao Cần Ngọc Từ 1 Sao Đến 7 Sao\n"
            + "Để Gọi Rồng 2 Sao Cần Ngọc Từ 2 Sao Đến 7 Sao\n"
            + "Để Gọi Rồng 3 Sao Cần Ngọc Từ 3 Sao Đến 7 Sao\n"
            + "Quá 5 Phút Nếu Không Ước Rồng Thần Sẽ Bay Mất";

    public static final String SHENRON_SAY
            = "Ta Sẽ Ban Cho Người 1 Điều Ước, Ngươi Có 5 Phút, Hãy Suy Nghĩ Thật Kỹ Trước Khi Quyết Định";

    public static final String[] SHENRON_1_STAR_WISHES_1
            = new String[]{"Vàng", "Găng Tay\nĐang Mang\nLên 1 Cấp", "Chí Mạng\nGốc +2%",
                "Thay\nChiêu 2-3\nĐệ Tử", "Điều Ước\nKhác"};

    public static final String[] SHENRON_1_STAR_WISHES_2
            = new String[]{"Đẹp Trai\nNhất\nVũ Trụ", "Ngọc Hồng",
                "Găng Tay Đệ\nĐang Mang\nLên 1 Cấp",
                "Điều Ước\nKhác"};

    public static final String[] SHENRON_2_STARS_WHISHES
            = new String[]{"Ngọc Hồng", "Sức Mạnh\nTiềm Năng", "Vàng"};

    public static final String[] SHENRON_3_STARS_WHISHES
            = new String[]{"Ngọc Hồng", "+Sức Mạnh\nTiềm Năng", "Vàng"};

    //--------------------------------------------------------------------------
    private static SummonDragon instance;
    private final Map pl_dragonStar;
    private long lastTimeShenronAppeared;
    private long lastTimeShenronWait;
    private final int timeResummonShenron = 180000;

    private boolean isShenronAppear;
    private final int timeShenronWait = 180000;

    private final Thread update;
    private boolean active;

    public boolean isPlayerDisconnect;
    public Player playerSummonShenron;
    private int playerSummonShenronId;
    private Zone mapShenronAppear;
    private byte shenronStar;
    private int menuShenron;
    private byte select;

    private SummonDragon() {
        this.pl_dragonStar = new HashMap<>();
        this.update = new Thread(() -> {
            while (active) {
                try {
                    if (isShenronAppear) {
                        if (isPlayerDisconnect) {

                            List<Player> players = mapShenronAppear.getPlayers();
                            for (Player plMap : players) {
                                if (plMap.id == playerSummonShenronId) {
                                    playerSummonShenron = plMap;
                                    reSummonShenron();
                                    isPlayerDisconnect = false;
                                    break;
                                }
                            }

                        }
                        if (Util.canDoWithTime(lastTimeShenronWait, timeShenronWait)) {
                            shenronLeave(playerSummonShenron, TIME_UP);
                        }
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Logger.logException(SummonDragon.class, e);
                }
            }
        });
        this.active();
    }

    private void active() {
        if (!active) {
            active = true;
            this.update.start();
        }
    }

    public void summonNamec(Player pl) {
        if (pl.zone.map.mapId == 7) {
            playerSummonShenron = pl;
            playerSummonShenronId = (int) pl.id;
            mapShenronAppear = pl.zone;
            sendNotifyShenronAppear();
            activeShenron(pl, true, SummonDragon.DRAGON_PORUNGA);
            sendWhishesNamec(pl);
        } else {
            Service.gI().sendThongBao(pl, "Không Thể Thực Hiện");
        }
    }

    public static SummonDragon gI() {
        if (instance == null) {
            instance = new SummonDragon();
        }
        return instance;
    }

    public void openMenuSummonShenron(Player pl, byte dragonBallStar) {
        this.pl_dragonStar.put(pl, dragonBallStar);
        NpcService.gI().createMenuConMeo(pl, ConstNpc.SUMMON_SHENRON, -1, "Bạn Muốn Gọi Rồng Thần ?",
                "Hướng\ndẫn thêm\n(mới)", "Gọi\nRồng Thần\n" + dragonBallStar + " Sao");
    }

    public void summonShenron(Player pl) {
        if (pl != null && pl.zone != null && pl.zone.map != null) {
            if (pl.zone.map.mapId == 3) {
                if (checkShenronBall(pl)) {
                    playerSummonShenron = pl;
                    playerSummonShenronId = (int) pl.id;
                    mapShenronAppear = pl.zone;
                    byte dragonStar = (byte) pl_dragonStar.get(playerSummonShenron);
                    int begin = NGOC_RONG_1_SAO;
                    switch (dragonStar) {
                        case 2:
                            begin = NGOC_RONG_2_SAO;
                            break;
                        case 3:
                            begin = NGOC_RONG_3_SAO;
                            break;
                    }
                    for (int i = begin; i <= NGOC_RONG_7_SAO; i++) {
                        try {
                            InventoryServiceNew.gI().subQuantityItemsBag(pl, InventoryServiceNew.gI().findItemBag(pl, i), 1);
                        } catch (Exception ex) {

                        }
                    }
                    InventoryServiceNew.gI().sendItemBags(pl);
                    sendNotifyShenronAppear();
                    activeShenron(pl, true, SummonDragon.DRAGON_SHENRON);
                    sendWhishesShenron(pl);

                }
            } else {
                Service.gI().sendThongBao(pl, "Chỉ Được Gọi Rồng Thần Ở Ngôi Làng Nhỏ!");
            }
        }
    }

    private void reSummonShenron() {
        activeShenron(playerSummonShenron, true, SummonDragon.DRAGON_SHENRON);
        sendWhishesShenron(playerSummonShenron);
    }

    private void sendWhishesShenron(Player pl) {
        byte dragonStar;
        try {
            dragonStar = (byte) pl_dragonStar.get(pl);
            this.shenronStar = dragonStar;
        } catch (Exception e) {
            dragonStar = this.shenronStar;

        }
        switch (dragonStar) {
            case 1:
                NpcService.gI().createMenuRongThieng(pl, ConstNpc.SHENRON_1_1, SHENRON_SAY, SHENRON_1_STAR_WISHES_1);
                break;
            case 2:
                NpcService.gI().createMenuRongThieng(pl, ConstNpc.SHENRON_2, SHENRON_SAY, SHENRON_2_STARS_WHISHES);
                break;
            case 3:
                NpcService.gI().createMenuRongThieng(pl, ConstNpc.SHENRON_3, SHENRON_SAY, SHENRON_3_STARS_WHISHES);
                break;
        }
    }

    private void sendWhishesNamec(Player pl) {
        NpcService.gI().createMenuRongThieng(pl, ConstNpc.NAMEC_1, "Ta Sẽ Ban Cho Cả Bang Ngươi 1 Điều Ước, Ngươi Có 5 Phút, Hãy Suy Nghĩ Thật Kỹ Trước Khi Quyết Định", "x99 Ngọc Rồng 3 Sao");
    }

    private void activeShenron(Player pl, boolean appear, byte type) {
        Message msg;
        try {
            msg = new Message(-83);
            msg.writer().writeByte(appear ? 0 : (byte) 1);
            if (appear) {
                msg.writer().writeShort(pl.zone.map.mapId);
                msg.writer().writeShort(pl.zone.map.bgId);
                msg.writer().writeByte(pl.zone.zoneId);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeUTF("");
                msg.writer().writeShort(pl.location.x);
                msg.writer().writeShort(pl.location.y);
                msg.writer().writeByte(type);
                lastTimeShenronWait = System.currentTimeMillis();
                isShenronAppear = true;
            }
            pl.sendMessage(msg);
        } catch (Exception e) {

        }
    }

    public void activeNight(Player pl) {
        Message msg;
        try {
            msg = new Message(-83);
            msg.writer().writeByte(0);

            msg.writer().writeShort(180);
            msg.writer().writeShort(11);
            msg.writer().writeByte(0);
            msg.writer().writeInt((int) -1);
            msg.writer().writeUTF("");
            msg.writer().writeShort(-1);
            msg.writer().writeShort(-1);
            msg.writer().writeByte(-1);
            //   lastTimeShenronWait = System.currentTimeMillis();
            //   isShenronAppear = true;

            Service.gI().sendMessAllPlayerInMap(pl, msg);
        } catch (Exception e) {
        }
    }

    public void activeDay(Player pl) {
        Message msg;
        try {
            msg = new Message(-83);
            msg.writer().writeByte(1);

            msg.writer().writeShort(180);
            msg.writer().writeShort(pl.zone.map.bgId);
            msg.writer().writeByte(0);
            msg.writer().writeInt((int) -1);
            msg.writer().writeUTF("");
            msg.writer().writeShort(-1);
            msg.writer().writeShort(-1);
            msg.writer().writeByte(-1);
            //   lastTimeShenronWait = System.currentTimeMillis();
            //   isShenronAppear = true;

            Service.gI().sendMessAllPlayerInMap(pl, msg);
        } catch (Exception e) {
        }
    }

    public void changeToNight(Player pl, boolean appear, byte type) {
        Message msg;
        try {
            msg = new Message(-83);
            msg.writer().writeByte(0);
            if (appear) {
                msg.writer().writeShort(175);
                msg.writer().writeShort(pl.zone.map.bgId);
                msg.writer().writeByte(0);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeUTF("");
                msg.writer().writeShort(pl.location.x);
                msg.writer().writeShort(pl.location.y);
                msg.writer().writeByte(type);

            }
            Service.getInstance().sendMessAllPlayer(msg);
        } catch (Exception e) {
        }
    }

    private boolean checkShenronBall(Player pl) {
        byte dragonStar = (byte) this.pl_dragonStar.get(pl);
        Item s2 = InventoryServiceNew.gI().findItemBag(pl, NGOC_RONG_2_SAO);
        Item s3 = InventoryServiceNew.gI().findItemBag(pl, NGOC_RONG_3_SAO);
        Item s4 = InventoryServiceNew.gI().findItemBag(pl, NGOC_RONG_4_SAO);
        Item s5 = InventoryServiceNew.gI().findItemBag(pl, NGOC_RONG_5_SAO);
        Item s6 = InventoryServiceNew.gI().findItemBag(pl, NGOC_RONG_6_SAO);
        Item s7 = InventoryServiceNew.gI().findItemBag(pl, NGOC_RONG_7_SAO);
        if (dragonStar == 1) {
            if (s2 == null || s2.quantity < 1) {
                Service.gI().sendThongBao(pl, "Bạn Còn Thiếu 1 Viên Ngọc Rồng 2 Sao");
                return false;
            }
            if (s3 == null || s3.quantity < 1) {
                Service.gI().sendThongBao(pl, "Bạn Còn Thiếu 1 Viên Ngọc Rồng 3 Sao");
                return false;
            }
        } else if (dragonStar == 2) {
            if (s3 == null || s3.quantity < 1) {
                Service.gI().sendThongBao(pl, "Bạn Còn Thiếu 1 Viên Ngọc Rồng 3 Sao");
                return false;
            }
        }
        if (s4 == null || s4.quantity < 1) {
            Service.gI().sendThongBao(pl, "Bạn Còn Thiếu 1 Viên Ngọc Rồng 4 Sao");
            return false;
        }
        if (s5 == null || s5.quantity < 1) {
            Service.gI().sendThongBao(pl, "Bạn Còn Thiếu 1 Viên Ngọc Rồng 5 Sao");
            return false;
        }
        if (s6 == null || s6.quantity < 1) {
            Service.gI().sendThongBao(pl, "Bạn Còn Thiếu 1 Viên Ngọc Rồng 6 Sao");
            return false;
        }
        if (s7 == null || s7.quantity < 1) {
            Service.gI().sendThongBao(pl, "Bạn Còn Thiếu 1 Viên Ngọc Rồng 7 Sao");
            return false;
        }
        return true;
    }

    private void sendNotifyShenronAppear() {

    }

    public void confirmWish() {
        if (this.playerSummonShenron != null) {
            switch (this.menuShenron) {
                case ConstNpc.SHENRON_1_1:
                    switch (this.select) {
                        case 0: //200 tr vàng
                            this.playerSummonShenron.inventory.gold += 150000;
                            PlayerService.gI().sendInfoHpMpMoney(this.playerSummonShenron);
                            break;
                        case 1: //găng tay đang đeo lên 1 cấp
                            Item item = this.playerSummonShenron.inventory.itemsBody.get(2);
                            if (item.isNotNullItem()) {
                                int level = 0;
                                for (ItemOption io : item.itemOptions) {
                                    if (io.optionTemplate.id == 72) {
                                        level = io.param;
                                        if (level < 7) {
                                            io.param++;
                                        }
                                        break;
                                    }
                                }
                                if (level < 7) {
                                    if (level == 0) {
                                        item.itemOptions.add(new ItemOption(72, 1));
                                    }
                                    for (ItemOption io : item.itemOptions) {
                                        if (io.optionTemplate.id == 0) {
                                            io.param += (io.param * 10 / 100);
                                            break;
                                        }
                                    }
                                    InventoryServiceNew.gI().sendItemBody(playerSummonShenron);
                                } else {
                                    Service.gI().sendThongBao(playerSummonShenron, "Găng Tay Của Ngươi Đã Đạt Cấp Tối Đa");
                                    reOpenShenronWishes(playerSummonShenron);
                                    return;
                                }
                            } else {
                                Service.gI().sendThongBao(playerSummonShenron, "Ngươi Hiện Tại Có Đeo Găng Đâu");
                                reOpenShenronWishes(playerSummonShenron);
                                return;
                            }
                            break;
                        case 2: //chí mạng +2%
                            if (this.playerSummonShenron.nPoint.critg < 9) {
                                this.playerSummonShenron.nPoint.critg += 2;
                            } else {
                                Service.gI().sendThongBao(playerSummonShenron, "Điều Ước Này Đã Quá Sức Với Ta, Ta Sẽ Cho Ngươi Chọn Lại");
                                reOpenShenronWishes(playerSummonShenron);
                                return;
                            }
                            break;
                        case 3: //thay chiêu 2-3 đệ tử
                            if (playerSummonShenron.pet != null) {
                                if (playerSummonShenron.pet.playerSkill.skills.get(1).skillId != -1) {
                                    playerSummonShenron.pet.openSkill2();
                                    if (playerSummonShenron.pet.playerSkill.skills.get(2).skillId != -1) {
                                        playerSummonShenron.pet.openSkill3();
                                    }
                                } else {
                                    Service.gI().sendThongBao(playerSummonShenron, "Ít Nhất Đệ Tử Ngươi Phải Có Chiêu 2 Chứ!");
                                    reOpenShenronWishes(playerSummonShenron);
                                    return;
                                }
                            } else {
                                Service.gI().sendThongBao(playerSummonShenron, "Ngươi Làm Gì Có Đệ Tử?");
                                reOpenShenronWishes(playerSummonShenron);
                                return;
                            }
                            break;
                    }
                    break;
                case ConstNpc.SHENRON_1_2:
                    switch (this.select) {
                        case 0: //đẹp trai nhất vũ trụ
                            if (InventoryServiceNew.gI().getCountEmptyBag(playerSummonShenron) > 0) {
                                byte gender = this.playerSummonShenron.gender;
                                Item avtVip = ItemService.gI().createNewItem((short) (gender == ConstPlayer.TRAI_DAT ? 227
                                        : gender == ConstPlayer.NAMEC ? 228 : 229));
                                avtVip.itemOptions.add(new ItemOption(97, Util.nextInt(5, 10)));
                                avtVip.itemOptions.add(new ItemOption(77, Util.nextInt(10, 20)));
                                InventoryServiceNew.gI().addItemBag(playerSummonShenron, avtVip);
                                InventoryServiceNew.gI().sendItemBags(playerSummonShenron);
                            } else {
                                Service.gI().sendThongBao(playerSummonShenron, "Hành Trang Đã Đầy");
                                reOpenShenronWishes(playerSummonShenron);
                                return;
                            }
                            break;
                        case 1: //+3k ruby
                            this.playerSummonShenron.inventory.ruby += 3000;
                            PlayerService.gI().sendInfoHpMpMoney(this.playerSummonShenron);
                            break;

                        case 2: //găng tay đệ lên 1 cấp
                            if (this.playerSummonShenron.pet != null) {
                                Item item = this.playerSummonShenron.pet.inventory.itemsBody.get(2);
                                if (item.isNotNullItem()) {
                                    int level = 0;
                                    for (ItemOption io : item.itemOptions) {
                                        if (io.optionTemplate.id == 72) {
                                            level = io.param;
                                            if (level < 7) {
                                                io.param++;
                                            }
                                            break;
                                        }
                                    }
                                    if (level < 7) {
                                        if (level == 0) {
                                            item.itemOptions.add(new ItemOption(72, 1));
                                        }
                                        for (ItemOption io : item.itemOptions) {
                                            if (io.optionTemplate.id == 0) {
                                                io.param += (io.param * 10 / 100);
                                                break;
                                            }
                                        }
                                        Service.gI().point(playerSummonShenron);
                                    } else {
                                        Service.gI().sendThongBao(playerSummonShenron, "Găng Tay Của Đệ Ngươi Đã Đạt Cấp Tối Đa");
                                        reOpenShenronWishes(playerSummonShenron);
                                        return;
                                    }
                                } else {
                                    Service.gI().sendThongBao(playerSummonShenron, "Đệ Ngươi Hiện Tại Có Đeo Găng Đâu");
                                    reOpenShenronWishes(playerSummonShenron);
                                    return;
                                }
                            } else {
                                Service.gI().sendThongBao(playerSummonShenron, "Ngươi Đâu Có Đệ Tử");
                                reOpenShenronWishes(playerSummonShenron);
                                return;
                            }
                            break;
                    }
                    break;
                case ConstNpc.SHENRON_2:
                    switch (this.select) {
                        case 0: //+150 ngọc
                            this.playerSummonShenron.inventory.ruby += 20;
                            PlayerService.gI().sendInfoHpMpMoney(this.playerSummonShenron);
                            break;
                        case 1: //+20 tr smtn
                            Service.gI().addSMTN(this.playerSummonShenron, (byte) 2, 150000, false);
                            break;
                        case 2: //2 tr vàng
                            if (this.playerSummonShenron.inventory.gold > Inventory.LIMIT_GOLD - 100000) {
                                this.playerSummonShenron.inventory.gold = Inventory.LIMIT_GOLD;
                            } else {
                                this.playerSummonShenron.inventory.gold += 100000;
                            }
                            PlayerService.gI().sendInfoHpMpMoney(this.playerSummonShenron);
                            break;
                    }
                    break;
                case ConstNpc.SHENRON_3:
                    switch (this.select) {
                        case 0: //+15 ngọc
                            this.playerSummonShenron.inventory.ruby += 10;
                            PlayerService.gI().sendInfoHpMpMoney(this.playerSummonShenron);
                            break;
                        case 1: //+2 tr smtn
                            Service.gI().addSMTN(this.playerSummonShenron, (byte) 2, 100000, false);
                            break;
                        case 2: //200k vàng
                            if (this.playerSummonShenron.inventory.gold > (Inventory.LIMIT_GOLD - 50000)) {
                                this.playerSummonShenron.inventory.gold = Inventory.LIMIT_GOLD;
                            } else {
                                this.playerSummonShenron.inventory.gold += 50000;
                            }
                            PlayerService.gI().sendInfoHpMpMoney(this.playerSummonShenron);
                            break;
                    }
                    break;
                case ConstNpc.NAMEC_1:
                    if (select == 0) {
                        if (playerSummonShenron.clan != null) {
                            playerSummonShenron.clan.members.forEach(m -> {
                                if (Client.gI().getPlayer(m.id) != null) {
                                    Player p = Client.gI().getPlayer(m.id);
                                    Item it = ItemService.gI().createNewItem((short) 19);
                                    it.quantity = 99;
                                    InventoryServiceNew.gI().addItemBag(p, it);
                                    InventoryServiceNew.gI().sendItemBags(p);
                                } else {
                                    Player p = GodGK.loadById(m.id);
                                    if (p != null) {
                                        Item it = ItemService.gI().createNewItem((short) 19);
                                        it.quantity = 99;
                                        InventoryServiceNew.gI().addItemBag(p, it);
                                        PlayerDAO.updatePlayer(p);
                                    }
                                }
                            });
                        } else {
                            Item it = ItemService.gI().createNewItem((short) 19);
                            it.quantity = 99;
                            InventoryServiceNew.gI().addItemBag(playerSummonShenron, it);
                            InventoryServiceNew.gI().sendItemBags(playerSummonShenron);
                        }
                    }
                    break;
            }
            shenronLeave(this.playerSummonShenron, WISHED);
        }
    }

    public void showConfirmShenron(Player pl, int menu, byte select) {
        this.menuShenron = menu;
        this.select = select;
        String wish = null;
        switch (menu) {
            case ConstNpc.SHENRON_1_1:
                wish = SHENRON_1_STAR_WISHES_1[select];
                break;
            case ConstNpc.SHENRON_1_2:
                wish = SHENRON_1_STAR_WISHES_2[select];
                break;
            case ConstNpc.SHENRON_2:
                wish = SHENRON_2_STARS_WHISHES[select];
                break;
            case ConstNpc.SHENRON_3:
                wish = SHENRON_3_STARS_WHISHES[select];
                break;
            case ConstNpc.NAMEC_1:
                wish = "x99 Ngọc Rồng 3 Sao";
                break;
        }
        NpcService.gI().createMenuRongThieng(pl, ConstNpc.SHENRON_CONFIRM, "Ngươi Có Chắc Muốn Ước?", wish, "Từ Chối");
    }

    public void reOpenShenronWishes(Player pl) {
        switch (menuShenron) {
            case ConstNpc.SHENRON_1_1:
                NpcService.gI().createMenuRongThieng(pl, ConstNpc.SHENRON_1_1, SHENRON_SAY, SHENRON_1_STAR_WISHES_1);
                break;
            case ConstNpc.SHENRON_1_2:
                NpcService.gI().createMenuRongThieng(pl, ConstNpc.SHENRON_1_2, SHENRON_SAY, SHENRON_1_STAR_WISHES_2);
                break;
            case ConstNpc.SHENRON_2:
                NpcService.gI().createMenuRongThieng(pl, ConstNpc.SHENRON_2, SHENRON_SAY, SHENRON_2_STARS_WHISHES);
                break;
            case ConstNpc.SHENRON_3:
                NpcService.gI().createMenuRongThieng(pl, ConstNpc.SHENRON_3, SHENRON_SAY, SHENRON_3_STARS_WHISHES);
                break;
        }
    }

    public void shenronLeave(Player pl, byte type) {
        if (type == WISHED) {
            NpcService.gI().createTutorial(pl, -1, "Điều Uớc Của Cư Dân Đã Trở Thành Sự Thật\nHẹn Gặp Cư Dân Lần Sau, Ta Đi Ngủ Đây!");
        } else {
            NpcService.gI().createMenuRongThieng(pl, ConstNpc.IGNORE_MENU, "Ta Buồn Ngủ Quá Rồi\nHẹn Gặp Cư Dân Lần Sau, Ta Đi Đây!");
        }
        activeShenron(pl, false, SummonDragon.DRAGON_SHENRON);
        this.isShenronAppear = false;
        this.menuShenron = -1;
        this.select = -1;
        this.playerSummonShenron = null;
        this.playerSummonShenronId = -1;
        this.shenronStar = -1;
        this.mapShenronAppear = null;
        this.active = false;
        lastTimeShenronAppeared = System.currentTimeMillis();
    }
}
