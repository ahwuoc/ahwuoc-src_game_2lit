package Dragon.services.func;

import Dragon.Bot.*;
import com.girlkun.database.GirlkunDB;
import Dragon.consts.ConstNpc;
import Dragon.jdbc.daos.PlayerDAO;
import Dragon.models.item.Item;
import Dragon.models.map.BDKB.BanDoKhoBauService;
import Dragon.models.map.Zone;
import Dragon.models.npc.DuaHau;
import Dragon.models.npc.Npc;
import Dragon.models.npc.NpcManager;
import Dragon.models.player.Inventory;
import Dragon.models.player.Player;
import com.girlkun.network.io.Message;
import com.girlkun.network.session.ISession;
import com.girlkun.result.GirlkunResultSet;
import Dragon.server.Client;
import Dragon.services.Service;
import Dragon.services.GiftService;
import Dragon.services.InventoryServiceNew;
import Dragon.services.ItemService;
import Dragon.services.NpcService;
import Dragon.utils.Logger;
import Dragon.utils.Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Input {

    public static String LOAI_THE;
    public static String MENH_GIA;
    private static final Map<Integer, Object> PLAYER_ID_OBJECT = new HashMap<Integer, Object>();

    public static final int BOTQUAI = 206783;

    public static final int BOTITEM = 206762;

    public static final int BOTBOSS = 2067683;

    public static final int CHANGE_PASSWORD = 500;
    public static final int GIFT_CODE = 501;
    public static final int FIND_PLAYER = 502;
    public static final int CHANGE_NAME = 503;
    public static final int CHOOSE_LEVEL_BDKB = 504;
    public static final int NAP_THE = 505;
    public static final int CHANGE_NAME_BY_ITEM = 506;
    public static final int CHOOSE_LEVEL_GAS = 555;
    public static final int GIVE_IT = 507;
    public static final int DONATE_CS = 523;
    public static final int QUY_DOI_COIN = 508;
    public static final int SEND_ITEM = 512;
    public static final int SEND_ITEM_OP = 513;
    public static final int SEND_ITEM_SKH = 514;
    public static final int SEND_ITEM_OP_VIP = 515;
    public static final int NAP_COIN = 520;
    public static final int QUY_DOI_NGOC_XANH = 509;
    public static final int TAI = 510;
    public static final int XIU = 511;
    public static final int QUY_DOI_HONG_NGOC = 509;
    public static final byte NUMERIC = 0;
    public static final byte ANY = 1;
    public static final byte PASSWORD = 2;
    public static final int UseGold = 3;
    public static final int changeCN = 4;
    public static final int changeBK = 5;
    public static final int changeBH = 6;
    public static final int changeGX = 7;
    public static final int changeAD = 8;
    private static Input intance;
    public static final int XIU_taixiu = 5164;
    public static final int TAI_taixiu = 5165;
    public static final int CHATALL = 521;
    public static final int TRONG_DUA = 374;
    public static final int TAO_PET = 5220;
    public static final int QUANLY = 519;

    private Input() {

    }

    public static Input gI() {
        if (intance == null) {
            intance = new Input();
        }
        return intance;
    }

    public void doInput(Player player, Message msg) {
        try {
            String[] text = new String[msg.reader().readByte()];
            for (int i = 0; i < text.length; i++) {
                text[i] = msg.reader().readUTF();
            }
            switch (player.iDMark.getTypeInput()) {

                case QUANLY:
                    Player ql = Client.gI().getPlayer(text[0]);
                    if (ql != null) {
                        int sl = InventoryServiceNew.gI().findItemBag(ql, (short) 457) == null ? 0
                                : InventoryServiceNew.gI().findItemBag(ql, (short) 457).quantity;
                        NpcService.gI().createMenuConMeo(player, ConstNpc.QUANLYTK, 21587,
                                "|7|[ QUẢN LÝ ACCOUNT BẬC 2 ]\n"
                                + "|1|Player Name : " + ql.name + "\n"
                                + "Account ID : " + ql.id + " | " + "IP Connected : "
                                + ql.getSession().ipAddress + " | " + "Version : " + ql.getSession().version
                                + "\nHồng Ngọc Inventory : " + ql.inventory.ruby
                                + "\nCoin Vnđ Inventory : " + ql.getSession().vnd
                                + "\nThỏi Vàng Inventory : " + sl
                                + "\nActive Status : "
                                + (ql.getSession().actived == true ? "isActived" : "isNonActive")
                                + "\nAccount Status : " + (ql.isAdmin() ? "Key Controller" : "PlayerOnline ")
                                + "\n"
                                + "|7|[Dev By Evils]",
                                new String[]{"CONTROLLER\nADMIN"},
                                ql);
                    } else {
                        Service.gI().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;
                case CHANGE_NAME: {
                    Player plChanged = (Player) PLAYER_ID_OBJECT.get((int) player.id);
                    if (plChanged != null) {
                        if (GirlkunDB.executeQuery("select * from player where name = ?", text[0]).next()) {
                            Service.gI().sendThongBao(player, "Tên nhân vật đã tồn tại");
                        } else {
                            plChanged.name = text[0];
                            GirlkunDB.executeUpdate("update player set name = ? where id = ?", plChanged.name,
                                    plChanged.id);
                            Service.gI().player(plChanged);
                            Service.gI().Send_Caitrang(plChanged);
                            Service.gI().sendFlagBag(plChanged);
                            Zone zone = plChanged.zone;
                            ChangeMapService.gI().changeMap(plChanged, zone, plChanged.location.x,
                                    plChanged.location.y);
                            Service.gI().sendThongBao(plChanged,
                                    "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            Service.gI().sendThongBao(player, "Đổi tên người chơi thành công");
                        }
                    }
                }
                break;
                case BOTITEM:
                    int slot = Integer.parseInt(text[0]);
                    int idBan = Integer.parseInt(text[1]);
                    int idTraoDoi = Integer.parseInt(text[2]);
                    int slot_TraoDoi = Integer.parseInt(text[3]);
                    ShopBot bs = new ShopBot(idBan, idTraoDoi, slot_TraoDoi);
                    new Thread(() -> {
                        NewBot.gI().runBot(1, bs, slot);
                    }).start();
                    break;
                case BOTBOSS:
                    slot = Integer.parseInt(text[0]);
                    new Thread(() -> {
                        NewBot.gI().runBot(2, null, slot);
                    }).start();
                    break;
                case BOTQUAI:
                    slot = Integer.parseInt(text[0]);
                    new Thread(() -> {
                        NewBot.gI().runBot(0, null, slot);
                    }).start();
                    break;
                case CHANGE_NAME_BY_ITEM: {
                    if (player != null) {
                        if (GirlkunDB.executeQuery("select * from player where name = ?", text[0]).next()) {
                            Service.gI().sendThongBao(player, "Tên nhân vật đã tồn tại");
                            createFormChangeNameByItem(player);
                        } else {
                            Item theDoiTen = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2006);
                            if (theDoiTen == null) {
                                Service.gI().sendThongBao(player, "Không tìm thấy thẻ đổi tên");
                            } else {
                                InventoryServiceNew.gI().subQuantityItemsBag(player, theDoiTen, 1);
                                player.name = text[0];
                                GirlkunDB.executeUpdate("update player set name = ? where id = ?", player.name,
                                        player.id);
                                Service.gI().player(player);
                                Service.gI().Send_Caitrang(player);
                                Service.gI().sendFlagBag(player);
                                Zone zone = player.zone;
                                ChangeMapService.gI().changeMap(player, zone, player.location.x, player.location.y);
                                Service.gI().sendThongBao(player,
                                        "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            }
                        }
                    }
                }
                break;
                case TAO_PET: {
                    String NamePet = text[0];
                    if (NamePet.length() < 3 || NamePet.length() > 8) {
                        Service.gI().sendThongBao(player,
                                "Không Ngắn Hơn 3 Và Dài Hơn 8 Kí Tự, Và Cho Phép Kí Tự Đặt Biệt.");
                        break;
                    }
                    player.CapBacThan = -1;
                    if (player.TrieuHoipet != null) {
                        ChangeMapService.gI().exitMap(player.TrieuHoipet);
                        player.TrieuHoipet.dispose();
                        player.TrieuHoipet = null;
                    }
                    player.CreatePet(NamePet);
                    Service.gI().sendThongBao(player, "Bạn Đã Nhận Thiên Binh : " + NamePet);
                    break;
                }
                case TRONG_DUA:
                    trongDua(player, text[0]);
                    break;
                case CHATALL:
                    String chat = text[0];
                    Service.gI().ChatAll(21587, "[Thông Báo]" + "\n"
                            + "|7|" + (player.isAdmin() ? "" : "") + chat + "\n");
                    break;
                case GIVE_IT:
                    if (player.isAdmin()) {
                        int idItemBuff = Integer.parseInt(text[1]);
                        int quantityItemBuff = Integer.parseInt(text[2]);
                        Player pBuffItem = Client.gI().getPlayer(text[0]);
                        if (pBuffItem != null) {
                            String txtBuff = "Buff to player: " + pBuffItem.name + "\b";
                            if (idItemBuff == -1) {
                                pBuffItem.inventory.gold = Math.min(pBuffItem.inventory.gold + (long) quantityItemBuff,
                                        Inventory.LIMIT_GOLD);
                                txtBuff += quantityItemBuff + " vàng\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -2) {
                                pBuffItem.inventory.gem = Math.min(pBuffItem.inventory.gem + quantityItemBuff,
                                        2000000000);
                                txtBuff += quantityItemBuff + " ngọc\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -3) {
                                pBuffItem.inventory.ruby = Math.min(pBuffItem.inventory.ruby + quantityItemBuff,
                                        2000000000);
                                txtBuff += quantityItemBuff + " ngọc khóa\b";
                                Service.getInstance().sendMoney(player);
                            } else {
                                Item itemBuffTemplate = ItemService.gI().createNewItem((short) idItemBuff);
                                itemBuffTemplate.quantity = quantityItemBuff;
                                InventoryServiceNew.gI().addItemBag(pBuffItem, itemBuffTemplate);
                                InventoryServiceNew.gI().sendItemBags(pBuffItem);
                                txtBuff += "x" + quantityItemBuff + " " + itemBuffTemplate.template.name + "\b";
                            }
                            NpcService.gI().createTutorial(player, 24, txtBuff);
                            if (player.id != pBuffItem.id) {
                                NpcService.gI().createTutorial(pBuffItem, 24, txtBuff);
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Player không online");
                        }
                        break;
                    }
                    break;
                case GIFT_CODE: {
                    String textLevel = text[0];
                    GiftService.gI().giftCode(player, textLevel);
                    break;
                }
                case SEND_ITEM:
                    String names = text[0];
                    int id = Integer.valueOf(text[1]);
                    int q = Integer.valueOf(text[2]);
                    if (Client.gI().getPlayer(names) != null) {
                        Item item = ItemService.gI().createNewItem(((short) id));
                        item.quantity = q;
                        InventoryServiceNew.gI().addItemBag(Client.gI().getPlayer(names), item);
                        InventoryServiceNew.gI().sendItemBags(Client.gI().getPlayer(names));
                        Service.gI().sendThongBao(Client.gI().getPlayer(names),
                                "Nhận " + item.template.name + " từ " + player.name);
                        Logger.log(Logger.PURPLE,
                                player.name + " đã gửi x" + q + " " + item.template.name + " tới " + names + "\n");

                    } else {
                        Service.gI().sendThongBao(player, "Không online");
                    }
                    break;
                case SEND_ITEM_OP:
                    if (player.isAdmin()) {
                        int idItemBuff = Integer.parseInt(text[1]);
                        int idOptionBuff = Integer.parseInt(text[2]);
                        int slOptionBuff = Integer.parseInt(text[3]);
                        int slItemBuff = Integer.parseInt(text[4]);
                        Player pBuffItem = Client.gI().getPlayer(text[0]);
                        if (pBuffItem != null) {
                            String txtBuff = "Buff to player: " + pBuffItem.name + "\b";
                            if (idItemBuff == -1) {
                                pBuffItem.inventory.gold = Math.min(pBuffItem.inventory.gold + (long) slItemBuff,
                                        Inventory.LIMIT_GOLD);
                                txtBuff += slItemBuff + " vàng\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -2) {
                                pBuffItem.inventory.gem = Math.min(pBuffItem.inventory.gem + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -3) {
                                pBuffItem.inventory.ruby = Math.min(pBuffItem.inventory.ruby + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc khóa\b";
                                Service.getInstance().sendMoney(player);
                            } else {
                                Item itemBuffTemplate = ItemService.gI().createNewItem((short) idItemBuff);
                                itemBuffTemplate.itemOptions.add(new Item.ItemOption(idOptionBuff, slOptionBuff));
                                itemBuffTemplate.quantity = slItemBuff;
                                txtBuff += "x" + slItemBuff + " " + itemBuffTemplate.template.name + "\b";
                                InventoryServiceNew.gI().addItemBag(pBuffItem, itemBuffTemplate);
                                InventoryServiceNew.gI().sendItemBags(pBuffItem);
                            }
                            NpcService.gI().createTutorial(player, 24, txtBuff);
                            if (player.id != pBuffItem.id) {
                                NpcService.gI().createTutorial(player, 24, txtBuff);
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Player không online");
                        }
                        break;
                    }
                    break;
                case SEND_ITEM_SKH:
                    if (player.isAdmin()) {
                        int idItemBuff = Integer.parseInt(text[1]);
                        int idOptionSKH = Integer.parseInt(text[2]);
                        int idOptionBuff = Integer.parseInt(text[3]);
                        int slOptionBuff = Integer.parseInt(text[4]);
                        int slItemBuff = Integer.parseInt(text[5]);
                        Player pBuffItem = Client.gI().getPlayer(text[0]);
                        if (pBuffItem != null) {
                            String txtBuff = "Buff to player: " + pBuffItem.name + "\b";
                            if (idItemBuff == -1) {
                                pBuffItem.inventory.gold = Math.min(pBuffItem.inventory.gold + (long) slItemBuff,
                                        Inventory.LIMIT_GOLD);
                                txtBuff += slItemBuff + " vàng\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -2) {
                                pBuffItem.inventory.gem = Math.min(pBuffItem.inventory.gem + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -3) {
                                pBuffItem.inventory.ruby = Math.min(pBuffItem.inventory.ruby + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc khóa\b";
                                Service.getInstance().sendMoney(player);
                            } else {
                                Item itemBuffTemplate = ItemService.gI().createNewItem((short) idItemBuff);
                                itemBuffTemplate.itemOptions.add(new Item.ItemOption(idOptionSKH, 0));
                                if (idOptionSKH == 127) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(139, 0));
                                } else if (idOptionSKH == 128) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(140, 0));
                                } else if (idOptionSKH == 129) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(141, 0));
                                } else if (idOptionSKH == 130) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(142, 0));
                                } else if (idOptionSKH == 131) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(143, 0));
                                } else if (idOptionSKH == 132) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(144, 0));
                                } else if (idOptionSKH == 133) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(136, 0));
                                } else if (idOptionSKH == 134) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(137, 0));
                                } else if (idOptionSKH == 135) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(138, 0));
                                }
                                itemBuffTemplate.itemOptions.add(new Item.ItemOption(30, 0));
                                itemBuffTemplate.itemOptions.add(new Item.ItemOption(idOptionBuff, slOptionBuff));
                                itemBuffTemplate.quantity = slItemBuff;
                                txtBuff += "x" + slItemBuff + " " + itemBuffTemplate.template.name + "\b";
                                InventoryServiceNew.gI().addItemBag(pBuffItem, itemBuffTemplate);
                                InventoryServiceNew.gI().sendItemBags(pBuffItem);
                            }
                            NpcService.gI().createTutorial(player, 24, txtBuff);
                            if (player.id != pBuffItem.id) {
                                NpcService.gI().createTutorial(player, 24, txtBuff);
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Player không online");
                        }
                        break;

                    }
                    break;
                case SEND_ITEM_OP_VIP:
                    if (player.isAdmin()) {
                        Player pBuffItem = Client.gI().getPlayer(text[0]);
                        int idItemBuff = Integer.parseInt(text[1]);
                        String idOptionBuff = text[2].trim();

                        int slItemBuff = Integer.parseInt(text[3]);

                        try {
                            if (pBuffItem != null) {
                                String txtBuff = "Buff to player: " + pBuffItem.name + "\b";

                                Item itemBuffTemplate = ItemService.gI().createNewItem((short) idItemBuff, slItemBuff);
                                if (!idOptionBuff.isEmpty()) {
                                    String arr[] = idOptionBuff.split("v");
                                    for (int i = 0; i < arr.length; i++) {
                                        String arr2[] = arr[i].split("-");
                                        int idoption = Integer.parseInt(arr2[0].trim());
                                        int param = Integer.parseInt(arr2[1].trim());
                                        itemBuffTemplate.itemOptions.add(new Item.ItemOption(idoption, param));
                                    }

                                }
                                txtBuff += "x" + slItemBuff + " " + itemBuffTemplate.template.name + "\b";
                                InventoryServiceNew.gI().addItemBag(pBuffItem, itemBuffTemplate);
                                InventoryServiceNew.gI().sendItemBags(pBuffItem);
                                NpcService.gI().createTutorial(player, 24, txtBuff);
                                if (player.id != pBuffItem.id) {
                                    NpcService.gI().createTutorial(pBuffItem, 24, txtBuff);
                                }
                            } else {
                                Service.getInstance().sendThongBao(player, "Player không online");
                            }
                        } catch (Exception e) {
                            Service.getInstance().sendThongBao(player, "Đã có lỗi xảy ra vui lòng thử lại");
                        }

                    }
                    break;
                case NAP_COIN: {
                    String name = text[0];
                    int vnd = Integer.valueOf(text[1]);
                    Player pl = Client.gI().getPlayer(name);
                    if (pl != null) {
                        pl.getSession().vnd += vnd;
                        PreparedStatement ps = null;
                        try (Connection con = GirlkunDB.getConnection();) {
                            ps = con.prepareStatement(
                                    "update account set vnd = (vnd + ?) ,tongnap = (tongnap + ?) where id = ?");
                            ps.setInt(1, vnd);
                            ps.setInt(2, vnd);
                            ps.setInt(3, pl.getSession().userId);
                            ps.executeUpdate();
                        } catch (Exception e) {
                            Logger.logException(PlayerDAO.class, e, "Lỗi update coin " + pl.name);
                        } finally {
                            try {
                                ps.close();
                            } catch (SQLException ex) {
                                System.out.println("Lỗi khi update tongnap");
                            }
                        }
                        Service.getInstance().sendThongBao(player, "Đã nạp " + vnd + " Vnd cho " + pl.name);
                    } else {
                        Service.getInstance().sendThongBao(player, "Người chơi không online");
                    }
                    break;
                }
                case CHANGE_PASSWORD:
                    Service.gI().changePassword(player, text[0], text[1], text[2]);
                    break;
                case FIND_PLAYER:
                    Player pl = Client.gI().getPlayer(text[0]);
                    if (pl != null) {
                        int sl = InventoryServiceNew.gI().findItemBag(pl, (short) 457) == null ? 0
                                : InventoryServiceNew.gI().findItemBag(pl, (short) 457).quantity;
                        NpcService
                                .gI().createMenuConMeo(
                                        player, ConstNpc.MENU_FIND_PLAYER, 21587, "|7|[ QUẢN LÝ PLAYER ]\n"
                                        + "|7|Player Name : " + pl.name + "\n"
                                        + "Account ID : " + pl.id + " | " + "IP Connected : "
                                        + pl.getSession().ipAddress + " | " + "Version : "
                                        + pl.getSession().version
                                        + "\nHồng Ngọc Inventory : " + pl.inventory.ruby
                                        + "\nCoin Vnđ Inventory : " + pl.getSession().vnd
                                        + "\nThỏi Vàng Inventory : " + sl
                                        + "\nActive Status : "
                                        + (pl.getSession().actived == true ? "Đã Mở Thành Viên"
                                        : "Chưa Mở Thành Viên")
                                        + "\nAccount Status : " + (pl.isAdmin() ? "ADMIN" : "")
                                        + "\n|7|[Dev Evils]",
                                        new String[]{"Đi tới\n" + pl.name, "Gọi " + pl.name + "\ntới đây", "Đổi tên",
                                            "Ban", "Kick"},
                                        pl);
                    } else {
                        Service.gI().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;
                // case UseGold:
                // int Gold = Integer.parseInt(text[0]);
                // Item thoivangchange = null;
                // for (Item item : player.inventory.itemsBag) {
                // if (item.isNotNullItem() && item.template.id == 457) {
                // thoivangchange = item;
                // break;
                // }
                // }
                // if (thoivangchange.quantity >= Gold) {
                // long goldsum = (long) (500000000L * (long) Gold);
                // if (player.inventory.gold + goldsum > Inventory.LIMIT_GOLD) {
                // Service.gI().sendThongBao(player, "Số vàng quy đổi vượt quá giới hạn 100
                // tỉ");
                // } else {
                // player.inventory.gold += (long) (500000000L * (long) Gold);
                // InventoryServiceNew.gI().subQuantityItemsBag(player, thoivangchange, Gold);
                // InventoryServiceNew.gI().sendItemBags(player);
                // Service.gI().sendMoney(player);
                // Service.gI().sendThongBao(player, "Đổi Thành Công");
                // }
                // } else {
                // Service.gI().sendThongBao(player, "Số lượng không đủ");
                // }
                // break;
                case UseGold:
                    String goldInput = text[0];
                    // Xóa các dấu trừ nếu có
                    goldInput = goldInput.replaceAll("-", "");
                    int gold;

                    try {
                        gold = Integer.parseInt(goldInput);
                    } catch (NumberFormatException e) {
                        Service.gI().sendThongBao(player, "Bug cái con cặc, bố m ban acc tự động , cố lên kakakaak !");
                        return; // Thoát khỏi hàm khi không thể chuyển đổi thành số nguyên
                    }

                    Item thoivangchange = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 457) {
                            thoivangchange = item;
                            break;
                        }
                    }

                    if (thoivangchange.quantity >= gold) {
                        long goldsum = (long) (500000000L * (long) gold);
                        if (player.inventory.gold + goldsum > Inventory.LIMIT_GOLD) {
                            Service.gI().sendThongBao(player, "Số vàng quy đổi vượt quá giới hạn 100 tỉ");
                        } else {
                            player.inventory.gold += (long) (500000000L * (long) gold);
                            InventoryServiceNew.gI().subQuantityItemsBag(player, thoivangchange, gold);
                            InventoryServiceNew.gI().sendItemBags(player);
                            Service.gI().sendMoney(player);
                            Service.gI().sendThongBao(player, "Đổi Thành Công");
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Số lượng không đủ");
                    }
                    break;
                case QUY_DOI_COIN: // hồng ngọc
                    int ratioGold = 2; // tỉ lệ đổi hồng ngọc
                    int coinGold = 1; // là cái loz
                    int goldTrade = Integer.parseInt(text[0]);
                    if (goldTrade <= 0 || goldTrade >= 50000000) {
                        Service.gI().sendThongBao(player, "giới hạn");
                    } else if (player.getSession().vnd >= goldTrade * coinGold) {
                        PlayerDAO.subvnd(player, goldTrade * coinGold);
                        Item thoiVang = ItemService.gI().createNewItem((short) 861, goldTrade * 2);// x3
                        InventoryServiceNew.gI().addItemBag(player, thoiVang);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.gI().sendThongBao(player, "bạn nhận được " + goldTrade * ratioGold
                                + " " + thoiVang.template.name);
                    } else {
                        Service.gI().sendThongBao(player,
                                "Số tiền của bạn là " + player.getSession().vnd + " không đủ để quy "
                                + " đổi " + goldTrade + " Hồng Ngọc " + " " + "bạn cần thêm"
                                + (player.getSession().vnd - goldTrade));
                    }
                    break;
                case QUY_DOI_HONG_NGOC:
                    int ratioGem = 4; // tỉ lệ đổi tv
                    int coinGem = 1000; // là cái loz
                    int gemTrade = Integer.parseInt(text[0]);
                    if (gemTrade <= 0 || gemTrade >= 50000000) {
                        Service.gI().sendThongBao(player, "giới hạn");
                    } else if (player.getSession().vnd >= gemTrade * coinGem) {
                        PlayerDAO.subvnd(player, gemTrade * coinGem);
                        Item thoiVang = ItemService.gI().createNewItem((short) 457, gemTrade * 4);// x4
                        InventoryServiceNew.gI().addItemBag(player, thoiVang);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.gI().sendThongBao(player, "bạn nhận được " + gemTrade * ratioGem
                                + " " + thoiVang.template.name);
                    } else {
                        Service.gI().sendThongBao(player,
                                "Số tiền của bạn là " + player.getSession().vnd + " không đủ để quy "
                                + " đổi " + gemTrade + " Thỏi Vàng" + " " + "bạn cần thêm"
                                + (player.getSession().vnd - gemTrade));
                    }
                    break;
                case CHOOSE_LEVEL_GAS:
                    int level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 100) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.MR_POPO, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, ConstNpc.MENU_ACCPET_GO_TO_GAS,
                                    "Con có chắc chắn muốn tới Khí gas huỷ diệt cấp độ " + level + "?",
                                    new String[]{"Đồng ý, Let's Go", "Từ chối"}, level);
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    }
                    break;
                case CHOOSE_LEVEL_BDKB:
                    int levele = Integer.parseInt(text[0]);
                    if (levele >= 1 && levele <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.QUY_LAO_KAME, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, ConstNpc.MENU_ACCEPT_GO_TO_BDKB,
                                    "Con có chắc chắn muốn tới bản đồ kho báu cấp độ " + levele + "?",
                                    new String[]{"Đồng ý", "Từ chối"}, levele);
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                    }
                    break;
                case TAI:
                    if (player != null) {
                        int sohntai = Integer.valueOf(text[0]);
                        if (sohntai > 500000) {
                            Service.getInstance().sendThongBao(player, "Tối đa 500000 VNĐ!!");
                            return;
                        }
                        if (sohntai <= 0) {
                            Service.getInstance().sendThongBao(player, "Xanh Chín đi, Đừng bug bẩn !!");
                            return;
                        }
                        if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 1) {
                            Service.getInstance().sendThongBao(player, "Ít nhất 2 ô trống trong hành trang!!");
                            return;
                        }
                        // Item tv1 = null;
                        // for (Item item : player.inventory.itemsBag) {
                        // if (item.isNotNullItem() && item.template.id == 457) {
                        // tv1 = item;
                        // break;
                        // }
                        // }
                        try {
                            if (player.inventory.ruby >= sohntai) {
                                // InventoryServiceNew.gI().subQuantityItemsBag(player, tv1, sotvtai);
                                player.inventory.ruby -= sohntai;
                                Service.gI().sendMoney(player);
                                int TimeSeconds = 10;
                                Service.getInstance().sendThongBao(player, "Chờ 10 giây để biết kết quả");
                                while (TimeSeconds > 0) {
                                    TimeSeconds--;
                                    Thread.sleep(1000);
                                }
                                int x = Util.nextInt(1, 6);
                                int y = Util.nextInt(1, 6);
                                int z = Util.nextInt(1, 6);
                                int tong = (x + y + z);
                                if (4 <= (x + y + z) && (x + y + z) <= 10) {
                                    if (player != null) {
                                        Service.getInstance().sendThongBaoOK(player,
                                                "Kết quả" + "\nSố hệ thống quay ra là :"
                                                + " " + x + " " + y + " " + z + "\nTổng là : " + tong
                                                + "\nBạn đã cược : "
                                                + sohntai + " VNĐ vào Tài" + "\nKết quả : Xỉu" + "\nBạn Thua.");
                                        return;
                                    }
                                } else if (x == y && x == z) {
                                    if (player != null) {
                                        Service.getInstance().sendThongBaoOK(player,
                                                "Kết quả" + "Số hệ thống quay ra : " + x + " " + y + " " + z
                                                + "\nTổng là : " + tong + "\nBạn đã cược : " + sohntai
                                                + " VNĐ vào Xỉu" + "\nKết quả : Tam hoa" + "\nBạn Thua.");
                                        return;
                                    }
                                } else if ((x + y + z) > 10) {

                                    if (player != null) {
                                        // Item tvthang = ItemService.gI().createNewItem((short) 457);
                                        // tvthang.quantity = (int) Math.round(sotvtai * 1.8);
                                        // InventoryServiceNew.gI().addItemBag(player, tvthang);
                                        player.inventory.ruby += sohntai * 1.8;
                                        Service.gI().sendMoney(player);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.getInstance().sendThongBaoOK(player, "Kết quả"
                                                + "\nSố hệ thống quay ra : " + x + " "
                                                + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : " + sohntai
                                                + " VNĐ vào Tài" + "\nKết quả : Tài" + "\n\nBạn dành chiến thắng !");
                                        return;
                                    }
                                }
                            } else {
                                Service.getInstance().sendThongBao(player, "Bạn không đủ Hồng Ngọc để chơi.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Service.getInstance().sendThongBao(player, "Lỗi.");
                        }
                    }
                case XIU:
                    if (player != null) {
                        int sohnxiu = Integer.valueOf(text[0]);
                        if (sohnxiu > 500000) {
                            Service.getInstance().sendThongBao(player, "Tối đa 500.000 VNĐ!!");
                            return;
                        }
                        if (sohnxiu <= 0) {
                            Service.getInstance().sendThongBao(player, "Xanh Chín đi, Đừng Bug bạn ơi !!");
                            return;
                        }
                        if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 1) {
                            Service.getInstance().sendThongBao(player, "Ít nhất 2 ô trống trong hành trang!!");
                            return;
                        }
                        // Item tv2 = null;
                        // for (Item item : player.inventory.itemsBag) {
                        // if (item.isNotNullItem() && item.template.id == 457) {
                        // tv2 = item;
                        // break;
                        // }
                        // }
                        try {
                            if (player.inventory.ruby >= sohnxiu) {
                                // InventoryServiceNew.gI().subQuantityItemsBag(player, tv2, sotvxiu);
                                player.inventory.ruby -= sohnxiu;
                                Service.gI().sendMoney(player);
                                int TimeSeconds = 10;
                                Service.getInstance().sendThongBao(player, "Chờ 10 giây để biết kết quả.");
                                while (TimeSeconds > 0) {
                                    TimeSeconds--;
                                    Thread.sleep(1000);
                                }
                                int x = Util.nextInt(1, 6);
                                int y = Util.nextInt(1, 6);
                                int z = Util.nextInt(1, 6);
                                int tong = (x + y + z);
                                if (4 <= (x + y + z) && (x + y + z) <= 10) {
                                    if (player != null) {
                                        // Item tvthang = ItemService.gI().createNewItem((short) 457);
                                        // tvthang.quantity = (int) Math.round(sotvxiu * 1.8);
                                        // InventoryServiceNew.gI().addItemBag(player, tvthang);
                                        player.inventory.ruby += sohnxiu * 2.3;
                                        Service.gI().sendMoney(player);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.getInstance().sendThongBaoOK(player, "Kết quả"
                                                + "\nSố hệ thống quay ra : " + x + " "
                                                + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : " + sohnxiu
                                                + " VNĐ vào Xỉu" + "\nKết quả : Xỉu" + "\n\nBạn dành chiến thắng");
                                        return;
                                    }
                                } else if (x == y && x == z) {
                                    if (player != null) {
                                        Service.getInstance().sendThongBaoOK(player,
                                                "Kết quả" + "Số hệ thống quay ra : " + x + " " + y + " " + z
                                                + "\nTổng là : " + tong + "\nBạn đã cược : " + sohnxiu
                                                + " VNĐ vào Xỉu" + "\nKết quả : Tam hoa" + "\nBạn thua.");
                                        return;
                                    }
                                } else if ((x + y + z) > 10) {
                                    if (player != null) {
                                        Service.getInstance().sendThongBaoOK(player,
                                                "Kết quả" + "\nSố hệ thống quay ra là :"
                                                + " " + x + " " + y + " " + z + "\nTổng là : " + tong
                                                + "\nBạn đã cược : "
                                                + sohnxiu + " Hồng Ngọc vào Xỉu" + "\nKết quả : Tài"
                                                + "\nBạn đã thua.");
                                        return;
                                    }
                                }
                            } else {
                                Service.getInstance().sendThongBao(player, "Bạn không đủ tiền để chơi.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Service.getInstance().sendThongBao(player, "Không thể thực hiện.");
                        }
                    }
                case DONATE_CS:
                    int csbang = Integer.parseInt(text[0]);
                    Item cscanhan = InventoryServiceNew.gI().findItemBag(player, 1382);
                    if (cscanhan == null && player.clanMember.memberPoint < 1) {
                        Service.gI().sendThongBao(player, "Số điểm capsule bản thân không đủ để thực hiện");
                        break;
                    }
                    InventoryServiceNew.gI().subQuantityItemsBag(player, cscanhan, csbang);
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.gI().sendMoney(player);
                    player.clanMember.memberPoint -= csbang;
                    player.clan.capsuleClan += csbang;
                    player.clanMember.clanPoint += csbang;
                    Service.gI().sendThongBao(player, "bạn đã quyên góp " + csbang + " điểm bang");
                    break;
                case TAI_taixiu:
                    int sotvxiu1 = Integer.valueOf(text[0]);
                    Item thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
                    try {
                        if (sotvxiu1 >= 1 && sotvxiu1 <= 1000) {
                            if (thoivang != null && thoivang.quantity >= sotvxiu1) {
                                InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, sotvxiu1);
                                player.goldTai += sotvxiu1;
                                TaiXiu.gI().goldTai += sotvxiu1;
                                Service.gI().sendThongBao(player,
                                        "Bạn Đã Đặt " + Util.format(sotvxiu1) + " Thỏi Vàng Vào TÀI");
                                TaiXiu.gI().addPlayerTai(player);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.getInstance().sendMoney(player);
                                PlayerDAO.updatePlayer(player);
                            } else {
                                Service.gI().sendThongBao(player, "Bạn Không Đủ Thỏi Vàng Để Chơi.");
                            }
                        } else {
                            Service.gI().sendThongBao(player, "Cược Ít Nhất 1 - Nhiều Nhất 1.000 Thỏi Vàng");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Service.gI().sendThongBao(player, "Lỗi.");
                    }
                    break;
                case XIU_taixiu:
                    int sotvxiu2 = Integer.valueOf(text[0]);
                    Item thoivang1 = InventoryServiceNew.gI().findItemBag(player, 457);
                    try {
                        if (sotvxiu2 >= 1 && sotvxiu2 <= 1000) {
                            if (thoivang1 != null && thoivang1.quantity >= sotvxiu2) {
                                InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang1, sotvxiu2);
                                player.goldXiu += sotvxiu2;
                                TaiXiu.gI().goldXiu += sotvxiu2;
                                Service.gI().sendThongBao(player,
                                        "Bạn đã đặt " + Util.format(sotvxiu2) + " Thỏi Vàng Vào XỈU");
                                TaiXiu.gI().addPlayerXiu(player);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.getInstance().sendMoney(player);
                                PlayerDAO.updatePlayer(player);
                            } else {
                                Service.gI().sendThongBao(player, "Bạn Không Đủ Thỏi Vàng Để Chơi.");
                            }
                        } else {
                            Service.gI().sendThongBao(player, "Cược Ít Nhất 1 - Nhiều Nhất 1.000 Thỏi Vàng");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Service.gI().sendThongBao(player, "Lỗi.");
                        System.out.println("nnnnn2  ");
                    }
                    break;

                case changeCN:
                case changeBK:
                case changeBH:
                case changeGX:
                case changeAD:
                    int SoLuong = Integer.parseInt(text[0]);
                    UseItem.gI().SendItemCap2(player, player.iDMark.getTypeInput(), SoLuong);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void trongDua(Player player, String s) {
        try {
            int slDuaHau = Math.abs(Integer.parseInt(s));
            Item HatGiong = InventoryServiceNew.gI().findItemBag(player, 1456);
            Item PhanBon = InventoryServiceNew.gI().findItemBag(player, 1454);
            Item BinhNuoc = InventoryServiceNew.gI().findItemBag(player, 1455);
            if (HatGiong == null) {
                Service.gI().sendThongBao(player, "Thiếu hạt giống");
                return;
            }

            if (PhanBon == null) {
                Service.gI().sendThongBao(player, "Thiếu phân bón");
                return;
            }

            if (BinhNuoc == null) {
                Service.gI().sendThongBao(player, "Thiếu bình nước");
                return;
            }

            if (HatGiong.quantity < (1 * slDuaHau)) {
                Service.gI().sendThongBao(player, "Không đủ hạt giống");
                return;
            }

            if (PhanBon.quantity < (1 * slDuaHau)) {
                Service.gI().sendThongBao(player, "Không đủ phân bón");
                return;
            }

            if (BinhNuoc.quantity < (1 * slDuaHau)) {
                Service.gI().sendThongBao(player, "Không đủ bình nước");
                return;
            }

            InventoryServiceNew.gI().subQuantityItemsBag(player, HatGiong, (1 * slDuaHau));
            InventoryServiceNew.gI().subQuantityItemsBag(player, PhanBon, (1 * slDuaHau));
            InventoryServiceNew.gI().subQuantityItemsBag(player, BinhNuoc, (1 * slDuaHau));

            DuaHau.gI().plDuaHau++;
            DuaHau.gI().addListPlDuaHau(player);
            Service.gI().sendThongBao(player, "Đã Trồng Dưa Hấu");
            return;

        } catch (NumberFormatException e) {
            Service.gI().sendThongBao(player, "Số lượng nhập không hợp lệ");
        }
    }

    public void createForm(Player pl, int typeInput, String title, SubInput... subInputs) {
        pl.iDMark.setTypeInput(typeInput);
        Message msg;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createForm(ISession session, int typeInput, String title, SubInput... subInputs) {
        Message msg;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void TAOPET(Player pl) {
        createForm(pl, TAO_PET, "Đặt Tên Cho Vị Thần Của Bạn", new SubInput("Tên", ANY));
    }

    public void createFormTrongDua(Player player) {
        createForm(player, TRONG_DUA, "Nấu bánh chưng", new SubInput("Nhập số lượng bánh chưng cần nấu", NUMERIC));
    }

    public void createFormChangePassword(Player pl) {
        createForm(pl, CHANGE_PASSWORD, "Quên Mật Khẩu", new SubInput("Mật Khẩu Cũ", PASSWORD),
                new SubInput("Mật Khẩu Mới", PASSWORD),
                new SubInput("Nhập Lại Mật Khẩu Mới", PASSWORD));
    }

    public void createFormGiveItem(Player pl) {
        createForm(pl, GIVE_IT, "Tặng vật phẩm", new SubInput("Tên", ANY), new SubInput("Id Item", ANY),
                new SubInput("Số lượng", ANY));
    }

    public void createFormGiftCode(Player pl) {
        createForm(pl, GIFT_CODE, "GIFTCODE", new SubInput("GIFT-CODE", ANY));
    }

    public void createFormFindPlayer(Player pl) {
        createForm(pl, FIND_PLAYER, "Tìm kiếm người chơi", new SubInput("Tên người chơi", ANY));
    }

    public void TAI(Player pl) {
        createForm(pl, TAI, "Chọn số thỏi vàng đặt Xỉu", new SubInput("Số thỏi vàng", ANY));// ????
    }

    public void XIU(Player pl) {
        createForm(pl, XIU, "Chọn số thỏi vàng đặt Tài", new SubInput("Số thỏi vàng", ANY));
    }

    public void DonateCsbang(Player pl) {
        createForm(pl, DONATE_CS, "Donate (Điểm Capsule Cá Nhân của bạn sẽ donate vào bang)",
                new SubInput("Nhập số lượng capsule muốn quyên góp", NUMERIC));
    }

    public void TAI_taixiu(Player pl) {
        createForm(pl, TAI_taixiu, "Chọn số thỏi vàng đặt Tài", new SubInput("Số Thỏi vàng cược", ANY));// ????
    }

    public void XIU_taixiu(Player pl) {
        createForm(pl, XIU_taixiu, "Chọn số thỏi vàng đặt Xỉu", new SubInput("Số Thỏi vàng cược", ANY));// ????
    }

    public void createFormNapThe(Player pl, String loaiThe, String menhGia) {
        LOAI_THE = loaiThe;
        MENH_GIA = menhGia;
        createForm(pl, NAP_THE, "Nạp thẻ", new SubInput("Số Seri", ANY), new SubInput("Mã thẻ", ANY));
    }

    public void ChatAll(Player pl) {
        createForm(pl, CHATALL, "CHAT ALL PLAYER", new SubInput("Chat All", ANY));
    }

    public void createFormNapCoin(Player pl) {
        createForm(pl, NAP_COIN, "Nạp VNĐ", new SubInput("Tên Nhân Vật", ANY), new SubInput("Số Lượng", ANY));
    }

    public void createFormSenditem1(Player pl) {
        createForm(pl, SEND_ITEM_OP, "SEND Vật Phẩm Option",
                new SubInput("Tên người chơi", ANY),
                new SubInput("ID Trang Bị", NUMERIC),
                new SubInput("ID Option", NUMERIC),
                new SubInput("Param", NUMERIC),
                new SubInput("Số lượng", NUMERIC));
    }

    public void QuanLyTK(Player pl) {
        createForm(pl, QUANLY, "Quản Lý Account", new SubInput("Tên người chơi", ANY));
    }

    public void createFormSenditem(Player pl) {
        createForm(pl, SEND_ITEM, "Buff Item Người Chơi",
                new SubInput("Name", ANY),
                new SubInput("ID Item", NUMERIC),
                new SubInput("Quantity", NUMERIC));
    }

    public void createFormSenditemskh(Player pl) {
        createForm(pl, SEND_ITEM_SKH, "Buff SKH Option V2",
                new SubInput("Tên người chơi", ANY),
                new SubInput("ID Trang Bị", NUMERIC),
                new SubInput("ID Option SKH 127 > 135", NUMERIC),
                new SubInput("ID Option Bonus", NUMERIC),
                new SubInput("Param", NUMERIC),
                new SubInput("Số lượng", NUMERIC));
    }

    public void createFormSenditem2(Player pl) {
        createForm(pl, SEND_ITEM_OP_VIP, "BUFF VIP", new SubInput("Tên người chơi", ANY), new SubInput("Id Item", ANY),
                new SubInput("Chuỗi option vd : 50-20v30-1", ANY), new SubInput("Số lượng", ANY));
    }

    public void createFormQDTV(Player pl) {

        createForm(pl, QUY_DOI_COIN, "Quy đổi Hồng Ngọc tỉ lệ x2"
                + "\n10.000 coin = 20.000 Hồng ngọc "
                + "\nNạp tiền Tại: https://nrokuroko.online/ "
                + "\nĐăng Nhập và Chọn Nạp Coin "
                + "\nLưu Ý : Nạp đúng nội dung nhé ! Nạp Sai là Mất ^•^ ",
                new SubInput("Nhập số lượng muốn đổi", NUMERIC));
    }

    public void createFormQDHN(Player pl) {

        createForm(pl, QUY_DOI_HONG_NGOC, "Quy đổi Thỏi Vàng"
                + "\nNhập 10 Có nghĩa là  10.000đ"
                + "\nTỉ Lệ Quy Đổi 10.000đ = 40 Thỏi Vàng"
                + "\nNạp tiền Tại: https://nrokuroko.online/ "
                + "\nĐăng Nhập và Chọn Nạp Coin "
                + "\nLưu Ý : Nạp đúng nội dung nhé ! Nạp Sai là Mất ^•^ ",
                new SubInput("Nhập số lượng muốn đổi", NUMERIC));
    }

    public void createFormChangeName(Player pl, Player plChanged) {
        PLAYER_ID_OBJECT.put((int) pl.id, plChanged);
        createForm(pl, CHANGE_NAME, "Đổi tên " + plChanged.name, new SubInput("Tên mới", ANY));
    }

    public void createFormChangeNameByItem(Player pl) {
        createForm(pl, CHANGE_NAME_BY_ITEM, "Đổi tên " + pl.name, new SubInput("Tên mới", ANY));
    }

    public void createFormChooseLevelGas(Player pl) {
        createForm(pl, CHOOSE_LEVEL_GAS, "Chọn cấp độ", new SubInput("Cấp độ (1-100)", NUMERIC));
    }

    public void createFormChooseLevelBDKB(Player pl) {
        createForm(pl, CHOOSE_LEVEL_BDKB, "Chọn cấp độ", new SubInput("Cấp độ (1-110)", NUMERIC));
    }

    public void createFormUseGold(Player pl) {
        createForm(pl, UseGold, "Nhập số lượng cần dùng", new SubInput("1 thỏi vàng dùng sẽ được 500tr vàng", NUMERIC));
    }

    public void createFormBotQuai(Player pl) {
        createForm(pl, BOTQUAI, "Buff Bot Quái",
                new SubInput("Số Lượng Bot", NUMERIC));
    }

    public void createFormBotBoss(Player pl) {
        createForm(pl, BOTBOSS, "Buff Bot Boss",
                new SubInput("Số Lượng Bot", NUMERIC));
    }

    public void createFormBotItem(Player pl) {
        createForm(pl, BOTITEM, "Buff Bot Item",
                new SubInput("Số Lượng Bot", NUMERIC),
                new SubInput("Id Item Cần Bán", NUMERIC),
                new SubInput("Id 457 Thỏi Vàng", NUMERIC),
                new SubInput("Số Lượng Thỏi Vàng Trao Đổi", NUMERIC));
    }
    //

    public void createFormItemC2(Player pl, int select) {
        if (select == 0) {
            createForm(pl, changeCN, "Nhập số lượng cần dùng", new SubInput("Dùng để nhận Cuồng Nộ cấp 2", NUMERIC));
        }
        if (select == 1) {
            createForm(pl, changeBK, "Nhập số lượng cần dùng", new SubInput("Dùng để nhận Bổ khí cấp 2", NUMERIC));
        }
        if (select == 2) {
            createForm(pl, changeBH, "Nhập số lượng cần dùng", new SubInput("Dùng để nhận Bổ huyết cấp 2", NUMERIC));
        }
        if (select == 3) {
            createForm(pl, changeGX, "Nhập số lượng cần dùng",
                    new SubInput("Dùng để nhận Giáp xên bọ hung cấp 2", NUMERIC));
        }
        if (select == 4) {
            createForm(pl, changeAD, "Nhập số lượng cần dùng", new SubInput("Dùng để nhận Ẩn Danh cấp 2", NUMERIC));
        }

    }

    public static class SubInput {

        private String name;
        private byte typeInput;

        public SubInput(String name, byte typeInput) {
            this.name = name;
            this.typeInput = typeInput;
        }
    }

    public void addItemGiftCodeToPlayer(Player p, final String giftcode) {
        try {
            final GirlkunResultSet red = GirlkunDB.executeQuery(
                    "SELECT * FROM `giftcode` WHERE `code` LIKE '" + Util.strSQL(giftcode) + "' LIMIT 1;");
            if (red.first()) {
                String text = "Mã quà tặng" + ": " + giftcode + "\b- " + "Phần quà của bạn là:" + "\b";
                final byte type = red.getByte("type");
                int limit = red.getInt("limit");
                final boolean isDelete = red.getBoolean("Delete");
                final boolean isCheckbag = red.getBoolean("bagCount");
                final JSONArray listUser = (JSONArray) JSONValue.parseWithException(red.getString("listUser"));
                final JSONArray listItem = (JSONArray) JSONValue.parseWithException(red.getString("listItem"));
                final JSONArray option = (JSONArray) JSONValue.parseWithException(red.getString("itemoption"));
                boolean active = red.getBoolean("active");
                if (limit == 0) {
                    NpcService.gI().createTutorial(p, 24, "Số lượng mã quà tặng này đã hết.");
                } else if (active && !p.getSession().actived) {
                    Service.getInstance().sendThongBao(p, "Cần kích hoạt tài khoản để nhận mã quà tặng này");
                    return;
                } else {
                    if (type == 1) {
                        for (int i = 0; i < listUser.size(); ++i) {
                            final int playerId = Integer.parseInt(listUser.get(i).toString());
                            if (playerId == p.id) {
                                // NpcService.gI().createTutorial(p,24, "Mỗi tài khoản chỉ được phép sử dụng mã
                                // quà tặng này 1 lần duy nhất.");
                                Service.gI().sendThongBaoOK(p,
                                        "Mỗi tài khoản chỉ được phép sử dụng mã quà tặng này 1 lần duy nhất.");
                                return;
                            }
                        }
                    }
                    if (isCheckbag && listItem.size() > InventoryServiceNew.gI().getCountEmptyBag(p)) {
                        NpcService.gI().createTutorial(p, 24,
                                "Hành trang cần phải có ít nhất " + listItem.size() + " ô trống để nhận vật phẩm");
                    } else {
                        for (int i = 0; i < listItem.size(); ++i) {
                            final JSONObject item = (JSONObject) listItem.get(i);
                            final int idItem = Integer.parseInt(item.get("id").toString());
                            final int quantity = Integer.parseInt(item.get("quantity").toString());

                            if (idItem == -1) {
                                p.inventory.gold = Math.min(p.inventory.gold + (long) quantity, Inventory.LIMIT_GOLD);
                                text += quantity + " vàng\b";
                            } else if (idItem == -2) {
                                p.inventory.gem = Math.min(p.inventory.gem + quantity, 2000000000);
                                text += quantity + " ngọc\b";
                            } else if (idItem == -3) {
                                p.inventory.ruby = Math.min(p.inventory.ruby + quantity, 2000000000);
                                text += quantity + " ngọc khóa\b";
                            } else {
                                Item itemGiftTemplate = ItemService.gI().createNewItem((short) idItem);

                                itemGiftTemplate.quantity = quantity;
                                if (option != null) {
                                    for (int u = 0; u < option.size(); u++) {
                                        JSONObject jsonobject = (JSONObject) option.get(u);
                                        itemGiftTemplate.itemOptions.add(
                                                new Item.ItemOption(Integer.parseInt(jsonobject.get("id").toString()),
                                                        Integer.parseInt(jsonobject.get("param").toString())));
                                    }

                                }
                                text += "x" + quantity + " " + itemGiftTemplate.template.name + "\b";
                                InventoryServiceNew.gI().addItemBag(p, itemGiftTemplate);
                                InventoryServiceNew.gI().sendItemBags(p);
                            }

                            if (i < listItem.size() - 1) {
                                text += "";
                            }
                        }
                        if (limit != -1) {
                            --limit;
                        }
                        listUser.add(p.id);
                        GirlkunDB.executeUpdate("UPDATE `giftcode` SET `limit` = " + limit + ", `listUser` = '"
                                + listUser.toJSONString() + "' WHERE `code` LIKE '" + Util.strSQL(giftcode) + "';");
                        // NpcService.gI().createTutorial(p,24, text);
                        Service.gI().sendThongBaoOK(p, text);
                    }
                }
            } else {
                NpcService.gI().createTutorial(p, 24, "Mã quà tặng không tồn tại hoặc đã được sử dụng");
            }
        } catch (Exception e) {
            NpcService.gI().createTutorial(p, 24, "Có lỗi sảy ra  hãy báo ngay cho QTV để khắc phục.");
            e.printStackTrace();
        }
    }

}
