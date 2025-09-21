package Dragon.services.func;

import Dragon.card.Card;
import Dragon.card.RadarCard;
import Dragon.card.RadarService;
import Dragon.consts.ConstMap;
import Dragon.models.item.Item;
import Dragon.consts.ConstNpc;
import Dragon.consts.ConstPlayer;
import Dragon.models.boss.BossManager;
import Dragon.models.item.Item.ItemOption;
import Dragon.models.map.Zone;
import Dragon.models.npc.specialnpc.MabuEgg;
import Dragon.models.player.Inventory;
import Dragon.services.NpcService;
import Dragon.models.player.Player;
import Dragon.models.skill.Skill;
import com.girlkun.network.io.Message;
import Dragon.server.Manager;
import Dragon.utils.SkillUtil;
import Dragon.services.Service;
import Dragon.utils.Util;
import Dragon.server.io.MySession;
import Dragon.services.EffectSkillService;
import Dragon.services.ItemService;
import Dragon.services.ItemTimeService;
import Dragon.services.PetService;
import Dragon.services.PlayerService;
import Dragon.services.TaskService;
import Dragon.services.InventoryServiceNew;
import Dragon.services.ItemMapService;
import Dragon.services.MapService;
//import Dragon.services.NgocRongNamecService;
import Dragon.services.RewardService;
import Dragon.services.SkillService;
import Dragon.utils.Logger;
import Dragon.utils.TimeUtil;
import java.util.Date;
import java.util.Random;

public class UseItem {

    private static final int ITEM_BOX_TO_BODY_OR_BAG = 0;
    private static final int ITEM_BAG_TO_BOX = 1;
    private static final int ITEM_BODY_TO_BOX = 3;
    private static final int ITEM_BAG_TO_BODY = 4;
    private static final int ITEM_BODY_TO_BAG = 5;
    private static final int ITEM_BAG_TO_PET_BODY = 6;
    private static final int ITEM_BODY_PET_TO_BAG = 7;

    private static final byte DO_USE_ITEM = 0;
    private static final byte DO_THROW_ITEM = 1;
    private static final byte ACCEPT_THROW_ITEM = 2;
    private static final byte ACCEPT_USE_ITEM = 3;

    private static UseItem instance;

    private UseItem() {

    }

    public static UseItem gI() {
        if (instance == null) {
            instance = new UseItem();
        }
        return instance;
    }

    public void getItem(MySession session, Message msg) {
        Player player = session.player;
        TransactionService.gI().cancelTrade(player);
        try {
            int type = msg.reader().readByte();
            int index = msg.reader().readByte();
            if (index == -1) {
                return;
            }
            switch (type) {
                case ITEM_BOX_TO_BODY_OR_BAG:
                    InventoryServiceNew.gI().itemBoxToBodyOrBag(player, index);
                    TaskService.gI().checkDoneTaskGetItemBox(player);
                    break;
                case ITEM_BAG_TO_BOX:
                    InventoryServiceNew.gI().itemBagToBox(player, index);
                    break;
                case ITEM_BODY_TO_BOX:
                    InventoryServiceNew.gI().itemBodyToBox(player, index);
                    break;
                case ITEM_BAG_TO_BODY:
                    InventoryServiceNew.gI().itemBagToBody(player, index);
                    break;
                case ITEM_BODY_TO_BAG:
                    InventoryServiceNew.gI().itemBodyToBag(player, index);
                    break;
                case ITEM_BAG_TO_PET_BODY:
                    InventoryServiceNew.gI().itemBagToPetBody(player, index);
                    break;
                case ITEM_BODY_PET_TO_BAG:
                    InventoryServiceNew.gI().itemPetBodyToBag(player, index);
                    break;
            }
            player.setClothes.setup();
            if (player.pet != null) {
                player.pet.setClothes.setup();
            }
            player.setClanMember();
            Service.gI().point(player);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void testItem(Player player, Message _msg) {
        TransactionService.gI().cancelTrade(player);
        Message msg;
        try {
            byte type = _msg.reader().readByte();
            int where = _msg.reader().readByte();
            int index = _msg.reader().readByte();
            System.out.println("type: " + type);
            System.out.println("where: " + where);
            System.out.println("index: " + index);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void doItem(Player player, Message _msg) {

        TransactionService.gI().cancelTrade(player);
        Message msg;
        byte type;
        try {
            type = _msg.reader().readByte();
            int where = _msg.reader().readByte();
            int index = _msg.reader().readByte();
            switch (type) {
                case DO_USE_ITEM:
                    if (player != null && player.inventory != null) {
                        if (index >= 0) {
                            Item item = player.inventory.itemsBag.get(index);
                            if (item.isNotNullItem()) {
                                if (item.template.type == 7) {
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc chắn học "
                                            + player.inventory.itemsBag.get(index).template.name + "?");
                                    player.sendMessage(msg);
                                } else {

                                    UseItem.gI().useItem(player, item, index);
                                    try {
                                        Dragon.services.TaskServiceNew.getInstance()
                                                .checkDoneTaskUseItem(player, item.template.id);
                                    } catch (Exception e) {
                                        Logger.logException(UseItem.class, e);
                                    }
                                }
                            }
                        } else {
                            this.eatPea(player);
                        }
                    }
                    break;
                case DO_THROW_ITEM:
                    if (!(player.zone.map.mapId == 99 || player.zone.map.mapId == 99 || player.zone.map.mapId == 99)) {
                        Item item = null;
                        if (where == 0) {
                            item = player.inventory.itemsBody.get(index);
                        } else {
                            item = player.inventory.itemsBag.get(index);
                        }
                        if (item != null && item.template != null) {
                            msg = new Message(-43);
                            msg.writer().writeByte(type);
                            msg.writer().writeByte(where);
                            msg.writer().writeByte(index);
                            msg.writer().writeUTF("Bạn chắc chắn muốn vứt " + item.template.name + "?");
                            player.sendMessage(msg);
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                    }
                    break;
                case ACCEPT_THROW_ITEM:
                    InventoryServiceNew.gI().throwItem(player, where, index);
                    Service.gI().point(player);
                    InventoryServiceNew.gI().sendItemBags(player);
                    break;
                case ACCEPT_USE_ITEM:
                    UseItem.gI().useItem(player, player.inventory.itemsBag.get(index), index);
                    try {
                        Item used = player.inventory.itemsBag.get(index);
                        if (used != null && used.isNotNullItem()) {
                            Dragon.services.TaskServiceNew.getInstance()
                                    .checkDoneTaskUseItem(player, used.template.id);
                        }
                    } catch (Exception e) {
                        Logger.logException(UseItem.class, e);
                    }
                    break;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void useItem(Player pl, Item item, int indexBag) {
        if (item.template.strRequire <= pl.nPoint.power) {
            switch (item.template.type) {
                case 21:
                    if (pl.newpet != null) {
                        ChangeMapService.gI().exitMap(pl.newpet);
                        pl.newpet.dispose();
                        pl.newpet = null;
                    }
                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                    PetService.Pet2(pl, item.template.head, item.template.body, item.template.leg);
                    Service.getInstance().point(pl);
                    break;
                case 7: // sách học, nâng skill
                    learnSkill(pl, item);
                    break;
                case 33:
                    UseCard(pl, item);
                    break;
                case 6: // đậu thần
                    this.eatPea(pl);
                    break;
                case 12: // ngọc rồng các loại
                    controllerCallRongThan(pl, item);
                    break;
                case 23: // thú cưỡi mới
                case 24: // thú cưỡi cũ
                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                    break;
                case 11:
                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                    Service.gI().sendFlagBag(pl);
                    break;
                case 77:
                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                    break;
                case 74:
                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                    Service.gI().sendFoot(pl, item.template.id);
                    break;
                case 72: {
                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                    Service.gI().sendPetFollow(pl, (short) (item.template.iconID - 1));
                    break;
                }
                case 39:
                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                    Service.gI().removeTitle(pl);
                    Service.gI().point(pl);
                    break;
                default:
                    switch (item.template.id) {
                        case 1110:
                            if (InventoryServiceNew.gI().findItem(pl.inventory.itemsBag, 1110).isNotNullItem()) {
                                pl.tickxanh = true;
                                Service.gI().point(pl);
                                Service.gI().sendMoney(pl);
                            }
                            break;

                        case 992:
                            pl.type = 1;
                            pl.maxTime = 5;
                            Service.gI().Transport(pl);
                            break;
                        case 1444:
                            pl.type = 3;
                            pl.maxTime = 5;
                            Service.gI().Transport(pl);
                            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                            break;
                        case 361:
                            maydoboss(pl);
                            break;
                        case 1536: // cskb
                            Input.gI().TAOPET(pl);
                            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                            break;
                        case 1762: // cskb
                            showthanthu(pl);
                            break;
                        case 293:
                            openGoiDau1(pl, item);
                            break;
                        case 294:
                            openGoiDau2(pl, item);
                            break;
                        case 295:
                            openGoiDau3(pl, item);
                            break;
                        case 296:
                            openGoiDau4(pl, item);
                            break;
                        case 297:
                            openGoiDau5(pl, item);
                            break;
                        case 298:
                            openGoiDau6(pl, item);
                            break;
                        case 299:
                            openGoiDau7(pl, item);
                            break;
                        case 596:
                            openGoiDau8(pl, item);
                            break;
                        case 597:
                            openGoiDau9(pl, item);
                            break;
                        case 211: // nho tím
                        case 212: // nho xanh
                            eatGrapes(pl, item);
                            break;
                        case 1105:// hop qua skh, item 2002 xd
                            UseItem.gI().Hopts(pl, item);
                            break;
                        case 1545:
                            if (pl.pet != null) {
                                NpcService.gI().createMenuConMeo(pl, ConstNpc.menu_detu, 21587,
                                        "|7|Boom Boom Boommmmmmmmmmmmmmm...\n"
                                        + "|5|Ngươi muốn nở trứng sao?\n"
                                        + "Sau khi ấp trứng thành công và nở trứng ngươi sẽ nhận được ngẫu nhiên 1 loại\n"
                                        + "đệ tử có chỉ số sức mạnh khác nhau:\n"
                                        + " + Berus: 10% chỉ số\n"
                                        + " + Broly: 13% chỉ số\n"
                                        + "+ Ubb: 15% chỉ số\n"
                                        + " + Xên Con: 18% chỉ số\n"
                                        + "Ngươi có thể mở ra loại nào tùy vào nhân phẩm của ngươi!!!\n"
                                        + "Chúc ngươi may mắn!!!",
                                        "Trái Đất", "Namec", "Xayda", "Từ chổi");
                                InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                            } else {
                                Service.gI().sendThongBao(pl, "Ngươi làm gì có đệ tử");
                            }
                            break;
                        case 1761:// phước danh sách boss
                            // NpcService.gI().createMenuConMeo(pl, ConstNpc.phuocdanhsachboss, 21587,
                            // "|7|Danh Sách Boss\n"
                            // + "|5|- BOSS Kẻ Ngoại Tộc [Map Thành Phố Đen] 15 Phút\n"
                            // + "|5|- BOSS OG73.1 [Map Đồi Cao] 30 Phút\n"
                            // + "|5|- BOSS Bill Con [Map Vực Băng] 1 Tiếng\n"
                            // + "|5|- BOSS Black Evil Goku [Map Area 51] 2 Tiếng\n", "Đóng");
                            maydoboss(pl);
                            break;
                        case 1980:// hop qua skh, item 2002 xd
                            UseItem.gI().Hophdt(pl, item);
                            break;
                        // case 1979://hop qua skh, item 2002 xd
                        // pl.cauca.StartCauCa();
                        // break;
                        case 1987:// hop qua skh, item 2002 xd
                            UseItem.gI().Hophd(pl, item);
                            break;
                        case 1986:// hop qua skh, item 2002 xd
                            UseItem.gI().Hoptl(pl, item);
                            break;
                        case 1985:// hop qua skh, item 2002 xd
                            UseItem.gI().Hoptst(pl, item);
                            break;

                        case 342:
                        case 343:
                        case 344:
                        case 345:
                            if (pl.zone.items.stream().filter(it -> it != null && it.itemTemplate.type == 22)
                                    .count() < 5) {
                                Service.gI().DropVeTinh(pl, item, pl.zone, pl.location.x, pl.location.y);
                                InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                            } else {
                                Service.gI().sendThongBao(pl, "Đặt ít vệ tinh thôi");
                            }
                            break;
                        case 457:
                            openThoiVangMAX(pl, item);
                            break;
                        case 569:
                            openduahau(pl, item);
                            break;
                        case 568: // quả trứng
                            if (pl.mabuEgg == null) {
                                MabuEgg.createMabuEgg(pl);
                                InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                                if (pl.zone.map.mapId == (3)) {// Phước load map 3
                                    if (pl.mabuEgg != null) {
                                        pl.mabuEgg.sendMabuEgg();
                                    }
                                }
                            } else {
                                Service.gI().sendThongBao(pl, "Bạn đã có quả trứng nên không thể sử dụng");
                            }
                            break;
                        case 1394:
                            if (pl.lastTimeTitle1 == 0) {
                                pl.lastTimeTitle1 += System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 3);
                            } else {
                                pl.lastTimeTitle1 += (1000 * 60 * 60 * 24 * 3);
                            }
                            pl.isTitleUse = true;
                            Service.gI().point(pl);
                            Service.gI().sendTitle(pl, 888);
                            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                            Service.gI().sendThongBao(pl, "Bạn nhận được 3 ngày danh hiệu !");
                            break;
                        case 380: // cskb
                            openCSKB(pl, item);
                            break;
                        case 574: // cskb
                            // openCSKBDB(pl, item);
                            break;
                        case 1453: // Hop qua
                            Hoprandom(pl, item);
                            break;
                        case 1501: // Hop qua
                            opencaitrang(pl, item);
                            break;
                        case 1502: // Hop qua
                            hopquatanthu(pl, item);
                            break;
                        case 1503: // Hop qua
                            hopquatanthu1(pl, item);
                            break;
                        case 628:
                            openPhieuCaiTrangHaiTac(pl, item);
                        case 381: // cuồng nộ
                        case 382: // bổ huyết
                        case 383: // bổ khí
                        case 384: // giáp xên
                        case 385: // ẩn danh
                        case 379: // máy dò capsule
                        case 2037: // máy dò cosmos
                        case 663: // bánh pudding
                        case 664: // xúc xíc
                        case 665: // kem dâu
                        case 666: // mì ly
                        case 667: // sushi
                        case 1099:
                        case 1100:
                        case 1101:
                        case 1102:
                        case 1103:
                        case 1978:
                        case 1233:
                        case 1234:
                        case 1235:
                        case 1560:
                        case 1642:
                        case 1643:
                        case 1644:
                        case 465:// 1 trung
                        case 466:// 2 trung
                        case 890:// gaquay
                        case 891:// thap cam
                        case 1549:
                            useItemTime(pl, item);
                            break;
                        case 570:
                            openWoodChest(pl, item);
                            break;
                        case 1611:
                            if (pl != null && pl.zone.map.mapId >= 174 && pl.zone.map.mapId <= 176) {
                                int time = Util.nextInt(10000, 20000);
                                EffectSkillService.gI().setBlindDCTT(pl, System.currentTimeMillis(), time);
                                EffectSkillService.gI().sendEffectPlayer(pl, pl, EffectSkillService.TURN_ON_EFFECT,
                                        EffectSkillService.CANCAUCAOCAP_EFFECT);
                                ItemTimeService.gI().sendItemTime(pl, 21341, time / 1000);
                                InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                                try {
                                    Thread.sleep(time);
                                } catch (Exception e) {
                                }
                                ItemService.gI().OpenItem1611(pl, item);
                            } else {
                                Service.gI().sendThongBao(pl, "Đây Không Phải Map Bắt Bọ");
                            }
                            break;
                        // Tất cả item Tu Tiên (1806-1816)
                        case 1806:
                        case 1807:
                        case 1808:
                        case 1809:
                        case 1810:
                        case 1811:
                        case 1812:
                        case 1813:
                        case 1814:
                        case 1815:
                        case 1816:
                            Dragon.services.tutien.TutienItemManager.gI().useTutienItem(pl, item);
                            break;
                        case 1774:
                            useCanCau(pl, item);
                            break;
                        case 1775:
                            useCanCau1(pl, item);
                            break;
                        case 1776:
                            useCanCau2(pl, item);
                            break;
                        case 1777:
                            useCanCau3(pl, item);
                            break;
                        case 1639:
                            kem(pl, item);
                            break;
                        case 1641:
                            quekem(pl, item);
                            break;
                        case 1653:
                            if (pl.pet != null) {
                                if (pl.pet.playerSkill.skills.get(1).skillId != -1) {
                                    pl.pet.openskillKAME();
                                    InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                                } else {
                                    Service.gI().sendThongBao(pl, "Ít nhất đệ tử ngươi phải có chiêu 2 chứ!");

                                    return;
                                }
                            } else {
                                Service.gI().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
                                return;
                            }
                            break;
                        case 1654:
                            if (pl.pet != null) {
                                if (pl.pet.playerSkill.skills.get(2).skillId != -1) {
                                    pl.pet.openskillTDHS();
                                    InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                                } else {
                                    Service.gI().sendThongBao(pl, "Ít nhất đệ tử ngươi phải có chiêu 3 chứ!");

                                    return;
                                }
                            } else {
                                Service.gI().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
                                return;
                            }
                            break;
                        case 1655:
                            if (pl.pet != null) {
                                if (pl.pet.playerSkill.skills.get(3).skillId != -1) {
                                    pl.pet.openskillKhi();
                                    InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                                } else {
                                    Service.gI().sendThongBao(pl, "Ít nhất đệ tử ngươi phải có chiêu 4 chứ!");

                                    return;
                                }
                            } else {
                                Service.gI().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
                                return;
                            }
                            break;
                        case 1006:
                            xocavang(pl, item);
                            break;
                        case 1005:
                            xocaxanh(pl, item);
                            break;
                        case 1649:
                            tanthu(pl, item);
                            break;
                        case 1512:
                            hopTrungThu(pl, item);
                            break;
                        case 1278:
                            RuongItemCap2(pl, item);
                            break;
                        case 521: // tdlt
                            if (pl.zone.map.mapId != 36) {
                                useTDLT(pl, item);
                            } else {
                                Service.gI().sendThongBao(pl, "Bạn Không Thể Dùng Tự Động Luyện Tập Ở Map Này!"); // đá
                                // bảo
                                // vệ
                            }

                            break;
                        case 454: // bông tai
                            UseItem.gI().usePorata(pl);
                            break;
                        case 193: // gói 10 viên capsule
                            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                        case 194: // capsule đặc biệt
                            // NpcService.gI().createMenuConMeo(pl, ConstNpc.phuoccapsule, 21587,
                            // "|7|Chọn Nơi Muốn Đi!",
                            // "Về Nhà",
                            // "Vùng\n Đồi Hoang",
                            // "Đồi Cát",
                            // "Thành Phố\nĐen",
                            // "Thành Phố\nBăng",
                            // "Núi Tuyết",
                            // "Vực Cấm",
                            // "Siêu Thị");
                            openCapsuleUI(pl);
                            break;
                        case 401: // đổi đệ tử
                            changePet(pl, item);
                            break;
                        case 1108: // đổi đệ tử
                            changeBerusPet(pl, item);
                            break;
                        case 722: // đổi đệ tử
                            changePetPic(pl, item);
                            break;
                        case 402: // sách nâng chiêu 1 đệ tử
                        case 403: // sách nâng chiêu 2 đệ tử
                        case 404: // sách nâng chiêu 3 đệ tử
                        case 759: // sách nâng chiêu 4 đệ tử
                            upSkillPet(pl, item);
                            break;
                        case 921: // bông tai c2
                            UseItem.gI().usePorata2(pl);
                            break;
                        case 1155:
                            UseItem.gI().usePorata3(pl);
                            break;
                        case 1156:
                            UseItem.gI().usePorata4(pl);
                            break;

                        case 2000:// hop qua skh, item 2000 td
                        case 2001:// hop qua skh, item 2001 nm
                        case 2002:// hop qua skh, item 2002 xd
                            UseItem.gI().ItemSKH(pl, item);
                            break;
                        // case 1105://hop qua skh, item 2002 xd
                        // UseItem.gI().Hopts(pl, item);
                        // break;
                        case 1997:// hop qua skh, item 2002 xd
                            Openhopct(pl, item);
                            break;
                        case 1998:// hop qua skh, item 2002 xd
                            Openhopflagbag(pl, item);
                            break;
                        case 1999:// hop qua skh, item 2002 xd
                            Openhoppet(pl, item);
                            break;
                        case 1457:// hop qua skh, item 2002 xd
                            Openhoppet(pl, item);
                            break;
                        case 1458:// hop qua skh, item 2002 xd
                            Openhoppet(pl, item);
                            break;
                        case 2003:// hop qua skh, item 2003 td
                        case 2004:// hop qua skh, item 2004 nm
                        case 2005:// hop qua skh, item 2005 xd
                            UseItem.gI().ItemDHD(pl, item);
                            break;
                        case 736:
                            ItemService.gI().OpenItem736(pl, item);
                            break;
                        case 987:
                            Service.gI().sendThongBao(pl, "Bảo vệ trang bị không bị rớt cấp"); // đá bảo vệ
                            break;
                        case 1098:
                            useItemHopQuaTanThu(pl, item);
                            break;
                        case 2078:
                            UseItem.gI().hopquagiangsinh(pl);
                            break;
                        case 1128:
                            openDaBaoVe(pl, item);
                            break;
                        case 1129:
                            openSPL(pl, item);
                            break;
                        case 1130:
                            openDaNangCap(pl, item);
                            break;
                        case 1131:
                            if (pl.pet != null) {
                                if (pl.pet.playerSkill.skills.get(1).skillId != -1) {
                                    pl.pet.openSkill2();
                                    if (pl.pet.playerSkill.skills.get(2).skillId != -1) {
                                        pl.pet.openSkill3();
                                    }
                                    InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                                } else {
                                    Service.gI().sendThongBao(pl, "Ít nhất đệ tử ngươi phải có chiêu 2 chứ!");

                                    return;
                                }
                            } else {
                                Service.gI().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
                                return;
                            }
                            break;
                        case 1132:

                            SkillService.gI().learSkillSpecial(pl, Skill.SUPER_KAME);
                            break;
                        case 1133:
                            SkillService.gI().learSkillSpecial(pl, Skill.MA_PHONG_BA);
                            break;
                        case 1134:
                            SkillService.gI().learSkillSpecial(pl, Skill.LIEN_HOAN_CHUONG);
                            break;
                        case 2006:
                            Input.gI().createFormChangeNameByItem(pl);
                            break;
                        case 999:
                            if (pl.pet == null) {
                                Service.gI().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
                                break;
                            }

                            if (pl.pet.playerSkill.skills.get(1).skillId != -1
                                    && pl.pet.playerSkill.skills.get(2).skillId != -1) {
                                pl.pet.openSkill2();
                                pl.pet.openSkill3();
                                InventoryServiceNew.gI().subQuantityItem(pl.inventory.itemsBag, item, 1);
                                InventoryServiceNew.gI().sendItemBags(pl);
                                Service.gI().sendThongBao(pl, "Đã đổi thành công chiêu 2 3 đệ tử");
                            } else {
                                Service.gI().sendThongBao(pl, "Ít nhất đệ tử ngươi phải có chiêu 2 chứ!");
                            }
                            break;

                        case 2027:
                        case 2028: {
                            if (InventoryServiceNew.gI().getCountEmptyBag(pl) == 0) {
                                Service.gI().sendThongBao(pl, "Hành trang không đủ chỗ trống");
                            } else {
                                InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                                Item linhThu = ItemService.gI().createNewItem((short) Util.nextInt(2019, 2026));
                                linhThu.itemOptions.add(new Item.ItemOption(50, Util.nextInt(2, 10)));
                                linhThu.itemOptions.add(new Item.ItemOption(77, Util.nextInt(2, 5)));
                                linhThu.itemOptions.add(new Item.ItemOption(103, Util.nextInt(2, 5)));
                                linhThu.itemOptions.add(new Item.ItemOption(95, Util.nextInt(1, 3)));
                                linhThu.itemOptions.add(new Item.ItemOption(96, Util.nextInt(1, 3)));
                                InventoryServiceNew.gI().addItemBag(pl, linhThu);
                                InventoryServiceNew.gI().sendItemBags(pl);
                                Service.gI().sendThongBao(pl,
                                        "Chúc mừng bạn nhận được Linh thú " + linhThu.template.name);
                            }
                            break;

                        }
                    }
                    break;
            }
            InventoryServiceNew.gI().sendItemBags(pl);
        } else {
            Service.gI().sendThongBaoOK(pl, "Sức mạnh không đủ yêu cầu");
        }
    }

    private void fixPetBTH(Player pl) {
        if (pl.newpet != null) {
            ChangeMapService.gI().exitMap(pl.newpet);
            pl.newpet.dispose();
            pl.newpet = null;
        }
    }

    private void Openhopct(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            boolean vinhvien = Manager.TotalCaiTrang >= 500;
            int[] rdct = new int[]{1290, 1291, 1281, 1302, 1282, 1296, 1297, 1298, 1295, 1301, 1300, 1307, 1306, 1308,
                1309, 1310};
            int[] rdop = new int[]{5, 14, 94, 108, 97};
            int randomct = new Random().nextInt(rdct.length);
            int randomop = new Random().nextInt(rdop.length);
            Item ct = ItemService.gI().createNewItem((short) rdct[randomct]);
            Item vt = ItemService.gI().createNewItem((short) Util.nextInt(16, 16));
            if (!vinhvien) {
                ct.itemOptions.add(new Item.ItemOption(50, Util.nextInt(20, 27)));
                ct.itemOptions.add(new Item.ItemOption(77, Util.nextInt(20, 27)));
                ct.itemOptions.add(new Item.ItemOption(103, Util.nextInt(20, 27)));
                ct.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 5)));
                Manager.TotalCaiTrang += 1;
            } else {
                ct.itemOptions.add(new Item.ItemOption(50, Util.nextInt(25, 27)));
                ct.itemOptions.add(new Item.ItemOption(77, Util.nextInt(25, 30)));
                ct.itemOptions.add(new Item.ItemOption(103, Util.nextInt(25, 30)));
                Manager.TotalCaiTrang = 0;
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().addItemBag(pl, ct);
            InventoryServiceNew.gI().addItemBag(pl, vt);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + ct.template.name + " và " + vt.template.name);
        } else {
            Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 1 ô trống trong hành trang.");
        }
    }

    private void Openhopflagbag(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            boolean vinhvien = Manager.TotalFlag >= 500;
            int[] rdfl = new int[]{1157, 1203, 1204, 1205, 1206, 1207, 954, 955, 1220, 1221, 966, 1222, 1226, 1228,
                1229, 467, 468, 469, 470, 982, 471, 983, 994, 995, 740, 996, 741, 997, 998, 999, 1000, 745,
                1001, 1007, 2035, 1013, 1021, 766, 1022, 767, 1023};
            int[] rdop = new int[]{50, 77, 103};
            int[] daysrandom = new int[]{3, 7, 15, 30};
            int randomfl = new Random().nextInt(rdfl.length);
            int randomop = new Random().nextInt(rdop.length);
            Item fl = ItemService.gI().createNewItem((short) rdfl[randomfl]);
            Item vt = ItemService.gI().createNewItem((short) Util.nextInt(16, 16));
            if (!vinhvien) {
                fl.itemOptions.add(new Item.ItemOption(rdop[randomop], Util.nextInt(5, 10)));
                fl.itemOptions.add(new Item.ItemOption(93, daysrandom[Util.nextInt(daysrandom.length)]));
                Manager.TotalFlag += 1;
            } else {
                fl.itemOptions.add(new Item.ItemOption(rdop[randomop], Util.nextInt(5, 10)));
                Manager.TotalFlag = 0;
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().addItemBag(pl, fl);
            InventoryServiceNew.gI().addItemBag(pl, vt);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + fl.template.name + " và " + vt.template.name);
        } else {
            Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 1 ô trống trong hành trang.");
        }
    }

    private void Openhoppet(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            int[] rdpet = new int[]{1311, 1312, 1313};
            int[] rdop = new int[]{50, 77, 103};
            int[] daysrandom = new int[]{3, 7};
            boolean vinhvien = Manager.TotalPet >= 500;
            int randompet = new Random().nextInt(rdpet.length);
            int randomop = new Random().nextInt(rdop.length);
            Item pet = ItemService.gI().createNewItem((short) rdpet[randompet]);
            Item vt = ItemService.gI().createNewItem((short) Util.nextInt(16, 16));
            if (!vinhvien) {
                pet.itemOptions.add(new Item.ItemOption(50, Util.nextInt(5, 10)));
                pet.itemOptions.add(new Item.ItemOption(77, Util.nextInt(5, 10)));
                pet.itemOptions.add(new Item.ItemOption(103, Util.nextInt(5, 10)));
                pet.itemOptions.add(new Item.ItemOption(93, daysrandom[Util.nextInt(daysrandom.length)]));
                Manager.TotalPet += 1;
            } else {
                pet.itemOptions.add(new Item.ItemOption(50, Util.nextInt(8, 13)));
                pet.itemOptions.add(new Item.ItemOption(77, Util.nextInt(8, 12)));
                pet.itemOptions.add(new Item.ItemOption(103, Util.nextInt(10, 14)));
                Manager.TotalPet = 0;
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().addItemBag(pl, pet);
            InventoryServiceNew.gI().addItemBag(pl, vt);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + pet.template.name + " và " + vt.template.name);
        } else {
            Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 1 ô trống trong hành trang.");
        }
    }

    private void hopthuong(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            short[] rac = {860, 421, 422, 1311, 1312, 1313};
            byte index2 = (byte) Util.nextInt(0, rac.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item it = ItemService.gI().createNewItem(rac[index2]);
            if (it.template.id == 739) {
                it.itemOptions.add(new ItemOption(50, Util.nextInt(10, 35)));
                it.itemOptions.add(new ItemOption(77, Util.nextInt(10, 35)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(10, 35)));
                it.itemOptions.add(new ItemOption(14, Util.nextInt(10, 35)));
            } else {
                it.itemOptions.add(new ItemOption(50, Util.nextInt(10, 35)));
                it.itemOptions.add(new ItemOption(77, Util.nextInt(10, 35)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(10, 35)));
                it.itemOptions.add(new ItemOption(14, Util.nextInt(10, 35)));
            }
            if (Util.isTrue(99, 100)) {
                it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 5)));
            }
            it.itemOptions.add(new ItemOption(30, 0));
            InventoryServiceNew.gI().addItemBag(pl, it);
            icon[1] = it.template.iconID;
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);
            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    public void UseCard(Player pl, Item item) {
        RadarCard radarTemplate = RadarService.gI().RADAR_TEMPLATE.stream().filter(c -> c.Id == item.template.id)
                .findFirst().orElse(null);
        if (radarTemplate == null) {
            return;
        }
        if (radarTemplate.Require != -1) {
            RadarCard radarRequireTemplate = RadarService.gI().RADAR_TEMPLATE.stream()
                    .filter(r -> r.Id == radarTemplate.Require).findFirst().orElse(null);
            if (radarRequireTemplate == null) {
                return;
            }
            Card cardRequire = pl.Cards.stream().filter(r -> r.Id == radarRequireTemplate.Id).findFirst().orElse(null);
            if (cardRequire == null || cardRequire.Level < radarTemplate.RequireLevel) {
                Service.gI().sendThongBao(pl, "Bạn cần sưu tầm " + radarRequireTemplate.Name + " ở cấp độ "
                        + radarTemplate.RequireLevel + " mới có thể sử dụng thẻ này");
                return;
            }
        }
        Card card = pl.Cards.stream().filter(r -> r.Id == item.template.id).findFirst().orElse(null);
        if (card == null) {
            Card newCard = new Card(item.template.id, (byte) 1, radarTemplate.Max, (byte) -1, radarTemplate.Options);
            if (pl.Cards.add(newCard)) {
                RadarService.gI().RadarSetAmount(pl, newCard.Id, newCard.Amount, newCard.MaxAmount);
                RadarService.gI().RadarSetLevel(pl, newCard.Id, newCard.Level);
                InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                InventoryServiceNew.gI().sendItemBags(pl);
            }
        } else {
            if (card.Level >= 2) {
                Service.gI().sendThongBao(pl, "Thẻ này đã đạt cấp tối đa");
                return;
            }
            card.Amount++;
            if (card.Amount >= card.MaxAmount) {
                card.Amount = 0;
                if (card.Level == -1) {
                    card.Level = 1;
                } else {
                    card.Level++;
                }
                Service.gI().point(pl);
            }
            RadarService.gI().RadarSetAmount(pl, card.Id, card.Amount, card.MaxAmount);
            RadarService.gI().RadarSetLevel(pl, card.Id, card.Level);
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);
        }
    }

    private void useItemChangeFlagBag(Player player, Item item) {
        switch (item.template.id) {
            case 994: // vỏ ốc
                break;
            case 995: // cây kem
                break;
            case 996: // cá heo
                break;
            case 997: // con diều
                break;
            case 998: // diều rồng
                break;
            case 999: // mèo mun
                if (!player.effectFlagBag.useMeoMun) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useMeoMun = !player.effectFlagBag.useMeoMun;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 1000: // xiên cá
                if (!player.effectFlagBag.useXienCa) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useXienCa = !player.effectFlagBag.useXienCa;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 1001: // phóng heo
                if (!player.effectFlagBag.usePhongHeo) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.usePhongHeo = !player.effectFlagBag.usePhongHeo;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 1202: // Hào quang
                if (!player.effectFlagBag.useHaoQuang) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useHaoQuang = !player.effectFlagBag.useHaoQuang;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
        }
        Service.gI().point(player);
        Service.gI().sendFlagBag(player);
    }

    // Phước Câu Cá VIP PRO 1
    // ---------------------------------------------------------------------------Cần
    // Câu 1
    private void useCanCau(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            if (pl.zone.map.mapId != 5 && pl.zone.map.mapId != 6) {
                Service.gI().sendThongBao(pl, "Cần Câu Này Không Được Dùng Câu Ở Đây!");
                return;
            }
            // Kiểm tra nếu một trong các điều kiện là true
            if (pl.itemTime.isCauCa || pl.itemTime.isCauCa1 || pl.itemTime.isCauCa2 || pl.itemTime.isCauCa3) {
                Service.gI().sendThongBao(pl, "Hãy Kiên Nhẫn Chờ Đợi Thêm Một Chút!");
                return;
            }

            // Tìm mồi câu
            Item moicau1824 = InventoryServiceNew.gI().findItemBag(pl, 1824);
            Item moicau1825 = InventoryServiceNew.gI().findItemBag(pl, 1825);
            Item moicau1826 = InventoryServiceNew.gI().findItemBag(pl, 1826);
            Item moicau1827 = InventoryServiceNew.gI().findItemBag(pl, 1827);
            Item moicau1828 = InventoryServiceNew.gI().findItemBag(pl, 1828);
            Item moicau1829 = InventoryServiceNew.gI().findItemBag(pl, 1829);
            Item moicau = null;
            int rateModifier = 0;

            // Xác định mồi và hệ số tỷ lệ
            if (moicau1824 != null) {
                moicau = moicau1824;
                rateModifier = 6;
            } else if (moicau1825 != null) {
                moicau = moicau1825;
                rateModifier = 8;
            } else if (moicau1826 != null) {
                moicau = moicau1826;
                rateModifier = 12;
            } else if (moicau1827 != null) {
                moicau = moicau1827;
                rateModifier = 4;
            } else if (moicau1828 != null) {
                moicau = moicau1828;
                rateModifier = 4;
            } else if (moicau1829 != null) {
                moicau = moicau1829;
                rateModifier = 4;
            }

            // Nếu không có mồi, vẫn cho phép câu với rateModifier = 0
            if (moicau == null) {
                Service.gI().sendThongBao(pl, "Đang Câu Với Không Mồi! Tỉ Lệ Giảm 80%");
                rateModifier = -50; // Giảm tỷ lệ khi không có mồi
            }

            pl.rateModifier = rateModifier; // Lưu lại hệ số tỷ lệ vào player

            int durCancau = 0;
            int lvlCanCau = 0;

            for (ItemOption io : item.itemOptions) {
                switch (io.optionTemplate.id) {
                    case 224:
                        durCancau = io.param;
                        break;
                    case 72:
                    // lvlCanCau = io.param;
                    // break;
                }
            }

            int time = Util.nextInt(15000, 15000);
            pl.lastTimeCauCa = System.currentTimeMillis();
            pl.itemTime.lastTimeCauCa = System.currentTimeMillis();
            pl.itemTime.isCauCa = true;
            ItemTimeService.gI().sendItemTime(pl, 21884, time / 1000);
            Service.getInstance().point(pl);

            durCancau++;
            item.itemOptions.clear();
            // item.itemOptions.add(new ItemOption(72, lvlCanCau));
            item.itemOptions.add(new ItemOption(224, durCancau));

            // Trừ mồi nếu có
            if (moicau != null) {
                InventoryServiceNew.gI().subQuantityItemsBag(pl, moicau, 1);
            }
            InventoryServiceNew.gI().sendItemBags(pl);

        } else {
            Service.gI().sendThongBao(pl, "Hàng Trang Đã Đầy");
        }
    }

    // ---------------------------------------------------------------------------Cần
    // Câu 2
    private void useCanCau1(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            if (pl.zone.map.mapId != 5 && pl.zone.map.mapId != 6) {
                Service.gI().sendThongBao(pl, "Cần Câu Này Không Được Dùng Câu Ở Đây!");
                return;
            }
            if (pl.itemTime.isCauCa || pl.itemTime.isCauCa1 || pl.itemTime.isCauCa2 || pl.itemTime.isCauCa3) {
                Service.gI().sendThongBao(pl, "Hãy Kiên Nhẫn Chờ Đợi Thêm Một Chút!");
                return;
            }

            // Tìm mồi câu
            Item moicau1824 = InventoryServiceNew.gI().findItemBag(pl, 1824);
            Item moicau1825 = InventoryServiceNew.gI().findItemBag(pl, 1825);
            Item moicau1826 = InventoryServiceNew.gI().findItemBag(pl, 1826);
            Item moicau1827 = InventoryServiceNew.gI().findItemBag(pl, 1827);
            Item moicau1828 = InventoryServiceNew.gI().findItemBag(pl, 1828);
            Item moicau1829 = InventoryServiceNew.gI().findItemBag(pl, 1829);
            Item moicau = null;
            int rateModifier = 0;

            // Xác định mồi và hệ số tỷ lệ
            if (moicau1824 != null) {
                moicau = moicau1824;
                rateModifier = 6;
            } else if (moicau1825 != null) {
                moicau = moicau1825;
                rateModifier = 8;
            } else if (moicau1826 != null) {
                moicau = moicau1826;
                rateModifier = 12;
            } else if (moicau1827 != null) {
                moicau = moicau1827;
                rateModifier = 4;
            } else if (moicau1828 != null) {
                moicau = moicau1828;
                rateModifier = 4;
            } else if (moicau1829 != null) {
                moicau = moicau1829;
                rateModifier = 4;
            }

            // Nếu không có mồi, vẫn cho phép câu với rateModifier = 0
            if (moicau == null) {
                Service.gI().sendThongBao(pl, "Đang Câu Với Không Mồi! Tỉ Lệ Giảm 80%");
                rateModifier = -50; // Giảm tỷ lệ khi không có mồi
            }

            pl.rateModifier = rateModifier; // Lưu lại hệ số tỷ lệ vào player

            int durCancau = 0;
            int lvlCanCau = 0;

            for (ItemOption io : item.itemOptions) {
                switch (io.optionTemplate.id) {
                    case 224:
                        durCancau = io.param;
                        break;
                    case 72:
                    // lvlCanCau = io.param;
                    // break;
                }
            }

            int time = Util.nextInt(14000, 14000);
            pl.lastTimeCauCa = System.currentTimeMillis();
            pl.itemTime.lastTimeCauCa = System.currentTimeMillis();
            pl.itemTime.isCauCa1 = true;
            ItemTimeService.gI().sendItemTime(pl, 32276, time / 1000);
            Service.getInstance().point(pl);

            durCancau++;
            item.itemOptions.clear();
            // item.itemOptions.add(new ItemOption(72, lvlCanCau));
            item.itemOptions.add(new ItemOption(224, durCancau));

            // Trừ mồi nếu có
            if (moicau != null) {
                InventoryServiceNew.gI().subQuantityItemsBag(pl, moicau, 1);
            }
            InventoryServiceNew.gI().sendItemBags(pl);

        } else {
            Service.gI().sendThongBao(pl, "Hàng Trang Đã Đầy");
        }
    }

    // ---------------------------------------------------------------------------Cần
    // Câu 3
    private void useCanCau2(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            if (pl.zone.map.mapId != 5 && pl.zone.map.mapId != 6) {
                Service.gI().sendThongBao(pl, "Cần Câu Này Không Được Dùng Câu Ở Đây!");
                return;
            }
            if (pl.itemTime.isCauCa || pl.itemTime.isCauCa1 || pl.itemTime.isCauCa2 || pl.itemTime.isCauCa3) {
                Service.gI().sendThongBao(pl, "Hãy Kiên Nhẫn Chờ Đợi Thêm Một Chút!");
                return;
            }

            // Tìm mồi câu
            Item moicau1824 = InventoryServiceNew.gI().findItemBag(pl, 1824);
            Item moicau1825 = InventoryServiceNew.gI().findItemBag(pl, 1825);
            Item moicau1826 = InventoryServiceNew.gI().findItemBag(pl, 1826);
            Item moicau1827 = InventoryServiceNew.gI().findItemBag(pl, 1827);
            Item moicau1828 = InventoryServiceNew.gI().findItemBag(pl, 1828);
            Item moicau1829 = InventoryServiceNew.gI().findItemBag(pl, 1829);
            Item moicau = null;
            int rateModifier = 0;

            // Xác định mồi và hệ số tỷ lệ
            if (moicau1824 != null) {
                moicau = moicau1824;
                rateModifier = 6;
            } else if (moicau1825 != null) {
                moicau = moicau1825;
                rateModifier = 8;
            } else if (moicau1826 != null) {
                moicau = moicau1826;
                rateModifier = 12;
            } else if (moicau1827 != null) {
                moicau = moicau1827;
                rateModifier = 4;
            } else if (moicau1828 != null) {
                moicau = moicau1828;
                rateModifier = 4;
            } else if (moicau1829 != null) {
                moicau = moicau1829;
                rateModifier = 4;
            }

            // Nếu không có mồi, vẫn cho phép câu với rateModifier = 0
            if (moicau == null) {
                Service.gI().sendThongBao(pl, "Đang Câu Với Không Mồi! Tỉ Lệ Giảm 80%");
                rateModifier = -50; // Giảm tỷ lệ khi không có mồi
            }

            pl.rateModifier = rateModifier; // Lưu lại hệ số tỷ lệ vào player

            int durCancau = 0;
            int lvlCanCau = 0;

            for (ItemOption io : item.itemOptions) {
                switch (io.optionTemplate.id) {
                    case 224:
                        durCancau = io.param;
                        break;
                    case 72:
                    // lvlCanCau = io.param;
                    // break;
                }
            }

            int time = Util.nextInt(13000, 13000);
            pl.lastTimeCauCa = System.currentTimeMillis();
            pl.itemTime.lastTimeCauCa = System.currentTimeMillis();
            pl.itemTime.isCauCa2 = true;
            ItemTimeService.gI().sendItemTime(pl, 32277, time / 1000);
            Service.getInstance().point(pl);

            durCancau++;
            item.itemOptions.clear();
            // item.itemOptions.add(new ItemOption(72, lvlCanCau));
            item.itemOptions.add(new ItemOption(224, durCancau));

            // Trừ mồi nếu có
            if (moicau != null) {
                InventoryServiceNew.gI().subQuantityItemsBag(pl, moicau, 1);
            }
            InventoryServiceNew.gI().sendItemBags(pl);

        } else {
            Service.gI().sendThongBao(pl, "Hàng Trang Đã Đầy");
        }
    }

    // ---------------------------------------------------------------------------Cần
    // Câu 4
    private void useCanCau3(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            if (pl.zone.map.mapId != 5 && pl.zone.map.mapId != 6) {
                Service.gI().sendThongBao(pl, "Cần Câu Này Không Được Dùng Câu Ở Đây!");
                return;
            }
            if (pl.itemTime.isCauCa || pl.itemTime.isCauCa1 || pl.itemTime.isCauCa2 || pl.itemTime.isCauCa3) {
                Service.gI().sendThongBao(pl, "Hãy Kiên Nhẫn Chờ Đợi Thêm Một Chút!");
                return;
            }

            // Tìm mồi câu
            Item moicau1824 = InventoryServiceNew.gI().findItemBag(pl, 1824);
            Item moicau1825 = InventoryServiceNew.gI().findItemBag(pl, 1825);
            Item moicau1826 = InventoryServiceNew.gI().findItemBag(pl, 1826);
            Item moicau1827 = InventoryServiceNew.gI().findItemBag(pl, 1827);
            Item moicau1828 = InventoryServiceNew.gI().findItemBag(pl, 1828);
            Item moicau1829 = InventoryServiceNew.gI().findItemBag(pl, 1829);
            Item moicau = null;
            int rateModifier = 0;

            // Xác định mồi và hệ số tỷ lệ
            if (moicau1824 != null) {
                moicau = moicau1824;
                rateModifier = 6;
            } else if (moicau1825 != null) {
                moicau = moicau1825;
                rateModifier = 8;
            } else if (moicau1826 != null) {
                moicau = moicau1826;
                rateModifier = 12;
            } else if (moicau1827 != null) {
                moicau = moicau1827;
                rateModifier = 4;
            } else if (moicau1828 != null) {
                moicau = moicau1828;
                rateModifier = 4;
            } else if (moicau1829 != null) {
                moicau = moicau1829;
                rateModifier = 4;
            }

            // Nếu không có mồi, vẫn cho phép câu với rateModifier = 0
            if (moicau == null) {
                Service.gI().sendThongBao(pl, "Không Có Mồi! Tỉ Lệ Giảm 80%");
                rateModifier = -50; // Giảm tỷ lệ khi không có mồi
            }

            pl.rateModifier = rateModifier; // Lưu lại hệ số tỷ lệ vào player

            int durCancau = 0;
            int lvlCanCau = 0;

            for (ItemOption io : item.itemOptions) {
                switch (io.optionTemplate.id) {
                    case 224:
                        durCancau = io.param;
                        break;
                    case 72:
                    // lvlCanCau = io.param;
                    // break;
                }
            }

            int time = Util.nextInt(9000, 9000);
            pl.lastTimeCauCa = System.currentTimeMillis();
            pl.itemTime.lastTimeCauCa = System.currentTimeMillis();
            pl.itemTime.isCauCa3 = true;
            ItemTimeService.gI().sendItemTime(pl, 32278, time / 1000);
            Service.getInstance().point(pl);

            durCancau++;
            item.itemOptions.clear();
            // item.itemOptions.add(new ItemOption(72, lvlCanCau));
            item.itemOptions.add(new ItemOption(224, durCancau));

            // Trừ mồi nếu có
            if (moicau != null) {
                InventoryServiceNew.gI().subQuantityItemsBag(pl, moicau, 1);
            }
            InventoryServiceNew.gI().sendItemBags(pl);

        } else {
            Service.gI().sendThongBao(pl, "Hàng Trang Đã Đầy");
        }
    }

    private void changePet(Player player, Item item) {
        if (player.pet != null) {
            int gender = player.pet.gender + 1;
            if (gender > 2) {
                gender = 0;
            }
            PetService.gI().changeNormalPet(player, gender);
            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện cho đệ tử !");
        }
    }

    private void changeBerusPet(Player player, Item item) {
        if (player.pet != null) {
            int gender = player.pet.gender;
            PetService.gI().changeBerusPet(player, gender);
            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
    }

    private void changePetPic(Player player, Item item) {
        if (player.pet != null) {
            int gender = player.pet.gender;
            PetService.gI().changePicPet(player, gender);
            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
    }

    private void openDaBaoVe(Player player, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            short[] possibleItems = {987, 987};
            byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
            newItem.itemOptions.add(new ItemOption(73, 0));
            newItem.quantity = (short) Util.nextInt(1, 10);
            InventoryServiceNew.gI().addItemBag(player, newItem);
            icon[1] = newItem.template.iconID;

            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
            InventoryServiceNew.gI().sendItemBags(player);

            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy");
        }
    }

    private void openduahau(Player player, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item hongngoc = ItemService.gI().createNewItem((short) 861);
            hongngoc.quantity += Util.nextInt(10, 100);
            Service.gI().sendThongBao(player, "Bạn Đã Nhận Được " + hongngoc.template.name + " " + hongngoc.quantity);
            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
            InventoryServiceNew.gI().addItemBag(player, hongngoc);
            icon[1] = hongngoc.template.iconID;

            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
            InventoryServiceNew.gI().sendItemBags(player);

            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy");
        }
    }

    private void openSPL(Player player, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            short[] possibleItems = {441, 442, 443, 444, 445, 446, 447};
            byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
            newItem.itemOptions.add(new ItemOption(73, 0));
            newItem.quantity = (short) Util.nextInt(1, 10);
            InventoryServiceNew.gI().addItemBag(player, newItem);
            icon[1] = newItem.template.iconID;

            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
            InventoryServiceNew.gI().sendItemBags(player);

            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy");
        }
    }

    private void openDaNangCap(Player player, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            short[] possibleItems = {220, 221, 222, 223, 224};
            byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
            newItem.itemOptions.add(new ItemOption(73, 0));
            newItem.quantity = (short) Util.nextInt(1, 10);
            InventoryServiceNew.gI().addItemBag(player, newItem);
            icon[1] = newItem.template.iconID;

            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
            InventoryServiceNew.gI().sendItemBags(player);

            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy");
        }
    }

    private void openManhTS(Player player, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            short[] possibleItems = {1066, 1067, 1068, 1069, 1070};
            byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
            newItem.itemOptions.add(new ItemOption(73, 0));
            newItem.quantity = (short) Util.nextInt(1, 99);
            InventoryServiceNew.gI().addItemBag(player, newItem);
            icon[1] = newItem.template.iconID;
            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
            InventoryServiceNew.gI().sendItemBags(player);
            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy");
        }
    }

    private void openGoiDau1(Player player, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            short[] possibleItems = {13, 13};
            byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
            newItem.quantity = (short) Util.nextInt(99, 99);
            InventoryServiceNew.gI().addItemBag(player, newItem);
            icon[1] = newItem.template.iconID;

            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
            InventoryServiceNew.gI().sendItemBags(player);

            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy");
        }
    }

    private void openGoiDau2(Player player, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            short[] possibleItems = {60, 60};
            byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
            newItem.quantity = (short) Util.nextInt(99, 99);
            InventoryServiceNew.gI().addItemBag(player, newItem);
            icon[1] = newItem.template.iconID;

            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
            InventoryServiceNew.gI().sendItemBags(player);

            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy");
        }
    }

    private void openGoiDau3(Player player, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            short[] possibleItems = {61, 61};
            byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
            newItem.quantity = (short) Util.nextInt(99, 99);
            InventoryServiceNew.gI().addItemBag(player, newItem);
            icon[1] = newItem.template.iconID;

            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
            InventoryServiceNew.gI().sendItemBags(player);

            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy");
        }
    }

    private void openGoiDau4(Player player, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            short[] possibleItems = {62, 62};
            byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
            newItem.quantity = (short) Util.nextInt(99, 99);
            InventoryServiceNew.gI().addItemBag(player, newItem);
            icon[1] = newItem.template.iconID;

            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
            InventoryServiceNew.gI().sendItemBags(player);

            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy");
        }
    }

    private void openGoiDau5(Player player, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            short[] possibleItems = {63, 63};
            byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
            newItem.quantity = (short) Util.nextInt(99, 99);
            InventoryServiceNew.gI().addItemBag(player, newItem);
            icon[1] = newItem.template.iconID;

            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
            InventoryServiceNew.gI().sendItemBags(player);

            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy");
        }
    }

    private void openGoiDau6(Player player, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            short[] possibleItems = {64, 64};
            byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
            newItem.quantity = (short) Util.nextInt(99, 99);
            InventoryServiceNew.gI().addItemBag(player, newItem);
            icon[1] = newItem.template.iconID;

            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
            InventoryServiceNew.gI().sendItemBags(player);

            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy");
        }
    }

    private void openGoiDau7(Player player, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            short[] possibleItems = {65, 65};
            byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
            newItem.quantity = (short) Util.nextInt(99, 99);
            InventoryServiceNew.gI().addItemBag(player, newItem);
            icon[1] = newItem.template.iconID;

            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
            InventoryServiceNew.gI().sendItemBags(player);

            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy");
        }
    }

    private void openGoiDau8(Player player, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            short[] possibleItems = {352, 352};
            byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
            newItem.quantity = (short) Util.nextInt(99, 99);
            InventoryServiceNew.gI().addItemBag(player, newItem);
            icon[1] = newItem.template.iconID;

            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
            InventoryServiceNew.gI().sendItemBags(player);

            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy");
        }
    }

    private void openGoiDau9(Player player, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            short[] possibleItems = {523, 523};
            byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
            newItem.quantity = (short) Util.nextInt(99, 99);
            InventoryServiceNew.gI().addItemBag(player, newItem);
            icon[1] = newItem.template.iconID;

            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
            InventoryServiceNew.gI().sendItemBags(player);

            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy");
        }
    }

    private void openPhieuCaiTrangHaiTac(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            Item ct = ItemService.gI().createNewItem((short) Util.nextInt(618, 626));
            ct.itemOptions.add(new ItemOption(147, 3));
            ct.itemOptions.add(new ItemOption(77, 3));
            ct.itemOptions.add(new ItemOption(103, 3));
            ct.itemOptions.add(new ItemOption(149, 0));
            if (item.template.id == 2006) {
                ct.itemOptions.add(new ItemOption(93, Util.nextInt(1, 7)));
            } else if (item.template.id == 2007) {
                ct.itemOptions.add(new ItemOption(93, Util.nextInt(7, 30)));
            }
            InventoryServiceNew.gI().addItemBag(pl, ct);
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);
            CombineServiceNew.gI().sendEffectOpenItem(pl, item.template.iconID, ct.template.iconID);
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void eatGrapes(Player pl, Item item) {
        int percentCurrentStatima = pl.nPoint.stamina * 100 / pl.nPoint.maxStamina;
        if (percentCurrentStatima > 50) {
            Service.gI().sendThongBao(pl, "Thể lực vẫn còn trên 50%");
            return;
        } else if (item.template.id == 211) {
            pl.nPoint.stamina = pl.nPoint.maxStamina;
            Service.gI().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 100%");
        } else if (item.template.id == 212) {
            pl.nPoint.stamina += (pl.nPoint.maxStamina * 20 / 100);
            Service.gI().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 20%");
        }
        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
        InventoryServiceNew.gI().sendItemBags(pl);
        PlayerService.gI().sendCurrentStamina(pl);
    }

    private void randomDB(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = {861};
            int[][] gold = {{15000, 50000}};
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (index <= 3) {
                pl.inventory.ruby += Util.nextInt(gold[0][0], gold[0][1]);
                if (pl.inventory.ruby > Inventory.LIMIT_GOLD) {
                    pl.inventory.ruby = (int) Inventory.LIMIT_GOLD;
                }
                PlayerService.gI().sendInfoHpMpMoney(pl);
                icon[1] = 7743;
            } else {
                Item it = ItemService.gI().createNewItem(temp[index]);
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryServiceNew.gI().addItemBag(pl, it);
                icon[1] = it.template.iconID;
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);

            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void openCSKB(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = {76, 188, 189, 190, 381, 382, 383, 384, 385};
            int[][] gold = {{5000, 20000}};
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (index <= 3) {
                pl.inventory.gold += Util.nextInt(gold[0][0], gold[0][1]);
                if (pl.inventory.gold > Inventory.LIMIT_GOLD) {
                    pl.inventory.gold = Inventory.LIMIT_GOLD;
                }
                PlayerService.gI().sendInfoHpMpMoney(pl);
                icon[1] = 930;
            } else {
                Item it = ItemService.gI().createNewItem(temp[index]);
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryServiceNew.gI().addItemBag(pl, it);
                icon[1] = it.template.iconID;
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);

            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    public void hopquatanthu(Player player, Item item) {
        switch (player.gender) {
            case 0: {
                Item itemReward = ItemService.gI().createNewItem((short) 0);
                Item itemReward1 = ItemService.gI().createNewItem((short) 6);
                Item itemReward2 = ItemService.gI().createNewItem((short) 12);
                Item itemReward3 = ItemService.gI().createNewItem((short) 21);
                Item itemReward4 = ItemService.gI().createNewItem((short) 27);
                itemReward.quantity = 1;
                itemReward1.quantity = 1;
                itemReward2.quantity = 1;
                itemReward3.quantity = 1;
                itemReward4.quantity = 1;
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
                    itemReward.itemOptions.add(new ItemOption(47, 5));
                    itemReward1.itemOptions.add(new ItemOption(7, 30));
                    itemReward2.itemOptions.add(new ItemOption(14, 1));
                    itemReward3.itemOptions.add(new ItemOption(0, 5));
                    itemReward4.itemOptions.add(new ItemOption(6, 30));

                    itemReward.itemOptions.add(new ItemOption(107, 5));
                    itemReward1.itemOptions.add(new ItemOption(107, 5));
                    itemReward2.itemOptions.add(new ItemOption(107, 5));
                    itemReward3.itemOptions.add(new ItemOption(107, 5));
                    itemReward4.itemOptions.add(new ItemOption(107, 5));

                    itemReward.itemOptions.add(new ItemOption(30, 1));
                    itemReward1.itemOptions.add(new ItemOption(30, 1));
                    itemReward2.itemOptions.add(new ItemOption(30, 1));
                    itemReward3.itemOptions.add(new ItemOption(30, 1));
                    itemReward4.itemOptions.add(new ItemOption(30, 1));

                    InventoryServiceNew.gI().addItemBag(player, itemReward);
                    InventoryServiceNew.gI().addItemBag(player, itemReward1);
                    InventoryServiceNew.gI().addItemBag(player, itemReward2);
                    InventoryServiceNew.gI().addItemBag(player, itemReward3);
                    InventoryServiceNew.gI().addItemBag(player, itemReward4);

                    Service.getInstance().sendThongBao(player, "Bạn đã nhận được set đồ 5 sao !");
                    InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                    InventoryServiceNew.gI().sendItemBags(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
                }
            }
            break;
            case 1: {
                Item itemReward = ItemService.gI().createNewItem((short) 1);
                Item itemReward1 = ItemService.gI().createNewItem((short) 7);
                Item itemReward2 = ItemService.gI().createNewItem((short) 12);
                Item itemReward3 = ItemService.gI().createNewItem((short) 22);
                Item itemReward4 = ItemService.gI().createNewItem((short) 28);
                itemReward.quantity = 1;
                itemReward1.quantity = 1;
                itemReward2.quantity = 1;
                itemReward3.quantity = 1;
                itemReward4.quantity = 1;
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {

                    itemReward.itemOptions.add(new ItemOption(47, 5));
                    itemReward1.itemOptions.add(new ItemOption(7, 30));
                    itemReward2.itemOptions.add(new ItemOption(14, 1));
                    itemReward3.itemOptions.add(new ItemOption(0, 5));
                    itemReward4.itemOptions.add(new ItemOption(6, 30));

                    itemReward.itemOptions.add(new ItemOption(107, 5));
                    itemReward1.itemOptions.add(new ItemOption(107, 5));
                    itemReward2.itemOptions.add(new ItemOption(107, 5));
                    itemReward3.itemOptions.add(new ItemOption(107, 5));
                    itemReward4.itemOptions.add(new ItemOption(107, 5));

                    itemReward.itemOptions.add(new ItemOption(30, 1));
                    itemReward1.itemOptions.add(new ItemOption(30, 1));
                    itemReward2.itemOptions.add(new ItemOption(30, 1));
                    itemReward3.itemOptions.add(new ItemOption(30, 1));
                    itemReward4.itemOptions.add(new ItemOption(30, 1));

                    InventoryServiceNew.gI().addItemBag(player, itemReward);
                    InventoryServiceNew.gI().addItemBag(player, itemReward1);
                    InventoryServiceNew.gI().addItemBag(player, itemReward2);
                    InventoryServiceNew.gI().addItemBag(player, itemReward3);
                    InventoryServiceNew.gI().addItemBag(player, itemReward4);

                    Service.getInstance().sendThongBao(player, "Bạn đã nhận được set đồ 5 sao !");
                    InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                    InventoryServiceNew.gI().sendItemBags(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
                }
            }
            break;
            case 2: {
                Item itemReward = ItemService.gI().createNewItem((short) 2);
                Item itemReward1 = ItemService.gI().createNewItem((short) 8);
                Item itemReward2 = ItemService.gI().createNewItem((short) 12);
                Item itemReward3 = ItemService.gI().createNewItem((short) 23);
                Item itemReward4 = ItemService.gI().createNewItem((short) 29);
                itemReward.quantity = 1;
                itemReward1.quantity = 1;
                itemReward2.quantity = 1;
                itemReward3.quantity = 1;
                itemReward4.quantity = 1;
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
                    itemReward.itemOptions.add(new ItemOption(47, 5));
                    itemReward1.itemOptions.add(new ItemOption(7, 30));
                    itemReward2.itemOptions.add(new ItemOption(14, 1));
                    itemReward3.itemOptions.add(new ItemOption(0, 5));
                    itemReward4.itemOptions.add(new ItemOption(6, 30));

                    itemReward.itemOptions.add(new ItemOption(107, 5));
                    itemReward1.itemOptions.add(new ItemOption(107, 5));
                    itemReward2.itemOptions.add(new ItemOption(107, 5));
                    itemReward3.itemOptions.add(new ItemOption(107, 5));
                    itemReward4.itemOptions.add(new ItemOption(107, 5));

                    itemReward.itemOptions.add(new ItemOption(30, 1));
                    itemReward1.itemOptions.add(new ItemOption(30, 1));
                    itemReward2.itemOptions.add(new ItemOption(30, 1));
                    itemReward3.itemOptions.add(new ItemOption(30, 1));
                    itemReward4.itemOptions.add(new ItemOption(30, 1));

                    InventoryServiceNew.gI().addItemBag(player, itemReward);
                    InventoryServiceNew.gI().addItemBag(player, itemReward1);
                    InventoryServiceNew.gI().addItemBag(player, itemReward2);
                    InventoryServiceNew.gI().addItemBag(player, itemReward3);
                    InventoryServiceNew.gI().addItemBag(player, itemReward4);

                    Service.getInstance().sendThongBao(player, "Bạn đã nhận được set đồ 5 sao !");
                    InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                    InventoryServiceNew.gI().sendItemBags(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
                }
            }
            break;
        }
    }

    private void opencaitrang(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 1) {
            int id = Util.nextInt(0, 100);
            int[] rdct = new int[]{1340};
            int[] rdop = new int[]{5, 14, 94, 108, 97, 106, 107};
            int randomct = new Random().nextInt(rdct.length);
            int randomop = new Random().nextInt(rdop.length);
            Item ct = ItemService.gI().createNewItem((short) rdct[randomct]);

            if (id <= 90) {
                ct.itemOptions.add(new Item.ItemOption(50, Util.nextInt(30, 30)));
                ct.itemOptions.add(new Item.ItemOption(77, Util.nextInt(30, 30)));
                ct.itemOptions.add(new Item.ItemOption(103, Util.nextInt(30, 30)));
                ct.itemOptions.add(new Item.ItemOption(5, Util.nextInt(10, 10)));

                ct.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 3)));
            } else {
                ct.itemOptions.add(new Item.ItemOption(50, Util.nextInt(30, 30)));
                ct.itemOptions.add(new Item.ItemOption(77, Util.nextInt(30, 30)));
                ct.itemOptions.add(new Item.ItemOption(103, Util.nextInt(30, 30)));
                ct.itemOptions.add(new Item.ItemOption(5, Util.nextInt(10, 10)));
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().addItemBag(pl, ct);

            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + ct.template.name);
        } else {
            Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 2 ô trống trong hành trang.");
        }
    }

    private void kem(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            short[] rac = {1641};
            byte index2 = (byte) Util.nextInt(0, rac.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item it = ItemService.gI().createNewItem(rac[index2]);
            if (it.template.id == 1641) {
                it.itemOptions.add(new ItemOption(174, 2024));
            }
            InventoryServiceNew.gI().addItemBag(pl, it);
            icon[1] = it.template.iconID;
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void quekem(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            short[] rac = {1630, 1074, 1075, 1076, 1077, 1079, 1080, 1081, 1082};
            byte index2 = (byte) Util.nextInt(0, rac.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (Util.isTrue(30, 100)) {
                Item it = ItemService.gI().createNewItem(rac[index2]);
                if (it.template.id == 1630) {
                    it.itemOptions.add(new ItemOption(50, Util.nextInt(10, 20)));
                    it.itemOptions.add(new ItemOption(77, Util.nextInt(10, 20)));
                    it.itemOptions.add(new ItemOption(103, Util.nextInt(10, 20)));
                    it.itemOptions.add(new ItemOption(14, Util.nextInt(10, 20)));
                    it.itemOptions.add(new ItemOption(174, 2024));
                } else {
                    it.itemOptions.add(new ItemOption(174, 2024));
                }
                if (Util.isTrue(95, 100)) {
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 5)));
                }

                it.itemOptions.add(new ItemOption(30, 0));
                InventoryServiceNew.gI().addItemBag(pl, it);
                pl.inventory.gold += Util.nextInt(30000, 100000);
                pl.kemtraicay += 1;
                icon[1] = it.template.iconID;
                Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name + " Và...Vàng");
            } else {
                Service.gI().sendThongBao(pl, "Chúc Bạn May Mắn Lần Sau");
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void xocavang(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            short[] rac = {1296, 1023, 994, 996, 997, 998, 1648};
            byte index2 = (byte) Util.nextInt(0, rac.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item it = ItemService.gI().createNewItem(rac[index2]);
            if (it.template.id == 1296) {
                it.itemOptions.add(new ItemOption(50, Util.nextInt(20, 35)));
                it.itemOptions.add(new ItemOption(77, Util.nextInt(20, 35)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(20, 35)));
                it.itemOptions.add(new ItemOption(14, Util.nextInt(20, 35)));
                it.itemOptions.add(new ItemOption(174, 2024));
            } else {
                it.itemOptions.add(new ItemOption(50, Util.nextInt(10, 20)));
                it.itemOptions.add(new ItemOption(77, Util.nextInt(10, 20)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(10, 20)));
                it.itemOptions.add(new ItemOption(14, Util.nextInt(10, 20)));
                it.itemOptions.add(new ItemOption(174, 2024));
            }
            if (Util.isTrue(95, 100)) {
                it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 5)));
            }

            it.itemOptions.add(new ItemOption(30, 0));
            InventoryServiceNew.gI().addItemBag(pl, it);
            pl.inventory.gold += Util.nextInt(30000, 100000);
            pl.point_vnd += 1;
            icon[1] = it.template.iconID;
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name + " Và...Vàng");
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void hopTrungThu(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            short[] rac = {1440, 1505, 1666, 1667, 1668, 1669, 1670, 1671};
            byte index2 = (byte) Util.nextInt(0, rac.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item it = ItemService.gI().createNewItem(rac[index2]);
            if (it.template.id == 1671) {
                it.itemOptions.add(new ItemOption(50, Util.nextInt(20, 35)));
                it.itemOptions.add(new ItemOption(77, Util.nextInt(20, 35)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(20, 35)));
                it.itemOptions.add(new ItemOption(14, Util.nextInt(20, 35)));
                it.itemOptions.add(new ItemOption(174, 2024));
            } else {
                it.itemOptions.add(new ItemOption(50, Util.nextInt(10, 20)));
                it.itemOptions.add(new ItemOption(77, Util.nextInt(10, 20)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(10, 20)));
                it.itemOptions.add(new ItemOption(14, Util.nextInt(10, 20)));
                it.itemOptions.add(new ItemOption(174, 2024));
            }
            if (Util.isTrue(95, 100)) {
                it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 5)));
            }
            it.itemOptions.add(new ItemOption(30, 0));
            InventoryServiceNew.gI().addItemBag(pl, it);
            pl.point_vnd += 1;
            icon[1] = it.template.iconID;
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name + " Và...Vàng");
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void tanthu(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 6) {
            Item Frieren = ItemService.gI().createNewItem((short) 1630);
            short baseAo = 1;
            short baseQuan = 7;
            short baseGang = 22;
            short baseGiay = 28;
            short genderAo = (pl.gender == 0) ? -1 : ((pl.gender == 2) ? 1 : (short) 0);
            short genderQuan = (pl.gender == 0) ? -1 : ((pl.gender == 2) ? 1 : (short) 0);
            short genderGang = (pl.gender == 0) ? -1 : ((pl.gender == 2) ? 1 : (short) 0);
            short genderGiay = (pl.gender == 0) ? -1 : ((pl.gender == 2) ? 1 : (short) 0);
            Item ao = ItemService.gI().createNewItem((short) (baseAo + genderAo));
            Item quan = ItemService.gI().createNewItem((short) (baseQuan + genderQuan));
            Item gang = ItemService.gI().createNewItem((short) (baseGang + genderGang));
            Item giay = ItemService.gI().createNewItem((short) (baseGiay + genderGiay));
            Item nhan = ItemService.gI().createNewItem((short) 12);
            Frieren.itemOptions.add(new ItemOption(50, 30));
            Frieren.itemOptions.add(new ItemOption(77, 30));
            Frieren.itemOptions.add(new ItemOption(103, 30));
            Frieren.itemOptions.add(new ItemOption(101, 50));
            Frieren.itemOptions.add(new ItemOption(93, 5));
            ao.itemOptions.add(new ItemOption(47, 2));
            quan.itemOptions.add(new ItemOption(6, 30));
            gang.itemOptions.add(new ItemOption(0, 4));
            giay.itemOptions.add(new ItemOption(7, 10));
            nhan.itemOptions.add(new ItemOption(14, 1));
            ao.itemOptions.add(new ItemOption(107, 4));
            quan.itemOptions.add(new ItemOption(107, 4));
            gang.itemOptions.add(new ItemOption(107, 4));
            giay.itemOptions.add(new ItemOption(107, 4));
            nhan.itemOptions.add(new ItemOption(107, 4));
            InventoryServiceNew.gI().addItemBag(pl, Frieren);
            InventoryServiceNew.gI().addItemBag(pl, ao);
            InventoryServiceNew.gI().addItemBag(pl, quan);
            InventoryServiceNew.gI().addItemBag(pl, gang);
            InventoryServiceNew.gI().addItemBag(pl, giay);
            InventoryServiceNew.gI().addItemBag(pl, nhan);
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void xocaxanh(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            short[] rac = {1631, 1023, 994, 996, 997, 998, 1648, 1000, 1001};
            byte index2 = (byte) Util.nextInt(0, rac.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item it = ItemService.gI().createNewItem(rac[index2]);
            if (it.template.id == 1631) {
                it.itemOptions.add(new ItemOption(50, Util.nextInt(20, 40)));
                it.itemOptions.add(new ItemOption(77, Util.nextInt(20, 40)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(20, 40)));
                it.itemOptions.add(new ItemOption(14, Util.nextInt(20, 40)));
                it.itemOptions.add(new ItemOption(174, 2024));
            } else {
                it.itemOptions.add(new ItemOption(50, Util.nextInt(10, 25)));
                it.itemOptions.add(new ItemOption(77, Util.nextInt(10, 25)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(10, 25)));
                it.itemOptions.add(new ItemOption(14, Util.nextInt(10, 25)));
                it.itemOptions.add(new ItemOption(174, 2024));
            }
            if (Util.isTrue(95, 100)) {
                it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 5)));
            }

            it.itemOptions.add(new ItemOption(30, 0));
            InventoryServiceNew.gI().addItemBag(pl, it);
            pl.inventory.gold += Util.nextInt(30000, 100000);
            pl.point_vnd += 1;
            icon[1] = it.template.iconID;
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name + " Và...Vàng");
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void hopquatanthu1(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 1) {
            int id = Util.nextInt(0, 100);
            int[] rdct = new int[]{1339};

            int[] rdop = new int[]{5, 14, 94, 108, 97, 106, 107};
            int randomct = new Random().nextInt(rdct.length);
            int randomct1 = new Random().nextInt(rdct.length);

            Item ct = ItemService.gI().createNewItem((short) rdct[randomct]);

            if (id <= 90) {
                ct.itemOptions.add(new Item.ItemOption(0, Util.nextInt(3000, 3000)));
                ct.itemOptions.add(new Item.ItemOption(101, Util.nextInt(30, 30)));
                ct.itemOptions.add(new Item.ItemOption(19, Util.nextInt(25, 25)));

            } else {
                ct.itemOptions.add(new Item.ItemOption(0, Util.nextInt(3000, 3000)));
                ct.itemOptions.add(new Item.ItemOption(101, Util.nextInt(30, 30)));
                ct.itemOptions.add(new Item.ItemOption(19, Util.nextInt(25, 25)));
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().addItemBag(pl, ct);

            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + ct.template.name);
        } else {
            Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 2 ô trống trong hành trang.");
        }
    }

    private void Hoprandom(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            short[] rac = {1311, 1312, 1313, 1438};
            byte index2 = (byte) Util.nextInt(0, rac.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item it = ItemService.gI().createNewItem(rac[index2]);
            if (it.template.id == 739) {
                it.itemOptions.add(new ItemOption(50, Util.nextInt(10, 15)));
                it.itemOptions.add(new ItemOption(77, Util.nextInt(10, 15)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(10, 15)));
                it.itemOptions.add(new ItemOption(14, Util.nextInt(10, 15)));
            } else {
                it.itemOptions.add(new ItemOption(50, Util.nextInt(10, 15)));
                it.itemOptions.add(new ItemOption(77, Util.nextInt(10, 15)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(10, 15)));
                it.itemOptions.add(new ItemOption(14, Util.nextInt(10, 15)));
            }
            if (Util.isTrue(95, 100)) {
                it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 5)));
            }
            it.itemOptions.add(new ItemOption(30, 0));
            InventoryServiceNew.gI().addItemBag(pl, it);
            icon[1] = it.template.iconID;
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name);
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    public boolean maydoboss(Player pl) {
        try {
            BossManager.gI().dobossmember(pl);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    private void hopquagiangsinh(Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 2) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 2 ô trống hành trang");
                return;
            }
            Item hopquagiangsinh = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 2078) {
                    hopquagiangsinh = item;
                    break;
                }
            }
            if (hopquagiangsinh != null) {
                Item gaudau = ItemService.gI().createNewItem((short) 2077);
                gaudau.itemOptions.add(new ItemOption(147, Util.nextInt(5, 30)));
                gaudau.itemOptions.add(new Item.ItemOption(77, Util.nextInt(5, 30)));
                gaudau.itemOptions.add(new Item.ItemOption(103, Util.nextInt(5, 30)));
                gaudau.itemOptions.add(new Item.ItemOption(101, Util.nextInt(2, 25)));
                gaudau.itemOptions.add(new Item.ItemOption(211, 0));
                gaudau.itemOptions.add(new Item.ItemOption(30, 0));
                InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquagiangsinh, 1);
                InventoryServiceNew.gI().addItemBag(pl, gaudau);
                InventoryServiceNew.gI().sendItemBags(pl);
                Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + gaudau.template.name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void useItemHopQuaTanThu(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = {17, 18, 19, 20};
            int[][] gold = {{10000, 50000}};
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (index <= 3) {
                pl.inventory.gold += Util.nextInt(gold[0][0], gold[0][1]);
                if (pl.inventory.gold > Inventory.LIMIT_GOLD) {
                    pl.inventory.gold = Inventory.LIMIT_GOLD;
                }
                PlayerService.gI().sendInfoHpMpMoney(pl);
                icon[1] = 930;
            } else {
                Item it = ItemService.gI().createNewItem(temp[index]);
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryServiceNew.gI().addItemBag(pl, it);
                icon[1] = it.template.iconID;
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);

            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void useItemTime(Player pl, Item item) {
        switch (item.template.id) {
            case 1549:
                pl.itemTime.lastAnhTrang = System.currentTimeMillis();
                pl.itemTime.isAnhTrang = true;
                break;
            case 465: // 1trung
                pl.itemTime.last1Trung = System.currentTimeMillis();
                pl.itemTime.is1Trung = true;
                break;
            case 466: // 2trung
                pl.itemTime.last2Trung = System.currentTimeMillis();
                pl.itemTime.is2Trung = true;
                break;
            case 890:// ga quay
                pl.itemTime.lastgaQuay = System.currentTimeMillis();
                pl.itemTime.isgaQuay = true;
                break;
            case 891:// thap cam
                pl.itemTime.lastthapCam = System.currentTimeMillis();
                pl.itemTime.isthapCam = true;
                break;
            case 1642: // x3EXP
                pl.itemTime.lastnuocmiakhonglo = System.currentTimeMillis();
                pl.itemTime.isnuocmiakhonglo = true;
                break;
            case 1643: // x3EXP
                pl.itemTime.lastnuocmiathom = System.currentTimeMillis();
                pl.itemTime.isnuocmiathom = true;
                break;
            case 1644: // x3EXP
                pl.itemTime.lastnuocmiasaurieng = System.currentTimeMillis();
                pl.itemTime.isnuocmiasaurieng = true;
                break;

            case 1233: // x3EXP
                pl.itemTime.lastX3EXP = System.currentTimeMillis();
                pl.itemTime.isX3EXP = true;
                break;
            case 1234: // x5EXP
                pl.itemTime.lastX5EXP = System.currentTimeMillis();
                pl.itemTime.isX5EXP = true;
                break;
            case 1235: // x7EXP
                pl.itemTime.lastX7EXP = System.currentTimeMillis();
                pl.itemTime.isX7EXP = true;
                break;
            case 1978: // x2EXP
                pl.itemTime.lastX2EXP = System.currentTimeMillis();
                pl.itemTime.isX2EXP = true;
                break;
            case 382: // bổ huyết
                if (pl.itemTime.isUseBoHuyet2) {
                    Service.getInstance().sendThongBao(pl, "Hốc vừa thôi bội thực chết cụ mày giờ");
                    return;
                }
                pl.itemTime.lastTimeBoHuyet = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet = true;
                break;
            case 383: // bổ khí
                if (pl.itemTime.isUseBoKhi2) {
                    Service.getInstance().sendThongBao(pl, "Hốc vừa thôi bội thực chết cụ mày giờ");
                    return;
                }
                pl.itemTime.lastTimeBoKhi = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi = true;
                break;
            case 384: // giáp xên
                if (pl.itemTime.isUseGiapXen2) {
                    Service.getInstance().sendThongBao(pl, "Hốc vừa thôi bội thực chết cụ mày giờ");
                    return;
                }
                pl.itemTime.lastTimeGiapXen = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen = true;
                break;
            case 381: // cuồng nộ
                if (pl.itemTime.isUseCuongNo2) {
                    Service.getInstance().sendThongBao(pl, "Hốc vừa thôi bội thực chết cụ mày giờ");
                    return;
                }
                pl.itemTime.lastTimeCuongNo = System.currentTimeMillis();
                pl.itemTime.isUseCuongNo = true;
                Service.gI().point(pl);
                break;
            case 385: // ẩn danh
                if (pl.itemTime.isUseAnDanh2) {
                    Service.getInstance().sendThongBao(pl, "Hốc vừa thôi bội thực chết cụ mày giờ");
                    return;
                }
                pl.itemTime.lastTimeAnDanh = System.currentTimeMillis();
                pl.itemTime.isUseAnDanh = true;
                break;
            case 379: // máy dò capsule
                if (pl.itemTime.isUseMayDo2) {
                    Service.getInstance().sendThongBao(pl, "Hốc vừa thôi bội thực chết cụ mày giờ");
                    return;
                }
                pl.itemTime.lastTimeUseMayDo = System.currentTimeMillis();
                pl.itemTime.isUseMayDo = true;
                break;
            case 1099:// cn
                if (pl.itemTime.isUseCuongNo) {
                    Service.getInstance().sendThongBao(pl, "Hốc vừa thôi bội thực chết cụ mày giờ");
                    return;
                }
                pl.itemTime.lastTimeCuongNo2 = System.currentTimeMillis();
                pl.itemTime.isUseCuongNo2 = true;
                Service.gI().point(pl);

                break;
            case 1100:// bo huyet
                if (pl.itemTime.isUseBoHuyet) {
                    Service.getInstance().sendThongBao(pl, "Hốc vừa thôi bội thực chết cụ mày giờ");
                    return;
                }
                pl.itemTime.lastTimeBoHuyet2 = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet2 = true;
                break;
            case 1101:// bo khi
                if (pl.itemTime.isUseBoKhi) {
                    Service.getInstance().sendThongBao(pl, "Hốc vừa thôi bội thực chết cụ mày giờ");
                    return;
                }
                pl.itemTime.lastTimeBoKhi2 = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi2 = true;
                break;
            case 1102:// xbh
                if (pl.itemTime.isUseGiapXen) {
                    Service.getInstance().sendThongBao(pl, "Hốc vừa thôi bội thực chết cụ mày giờ");
                    return;
                }
                pl.itemTime.lastTimeGiapXen2 = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen2 = true;
                break;
            case 1103:// an danh
                if (pl.itemTime.isUseAnDanh) {
                    Service.getInstance().sendThongBao(pl, "Hốc vừa thôi bội thực chết cụ mày giờ");
                    return;
                }
                pl.itemTime.lastTimeAnDanh2 = System.currentTimeMillis();
                pl.itemTime.isUseAnDanh2 = true;
                break;
            case 638:// Bình chứa comeson
                pl.itemTime.lastbkt = System.currentTimeMillis();
                pl.itemTime.isbkt = true;
                break;
            case 663: // bánh pudding
            case 664: // xúc xíc
            case 665: // kem dâu
            case 666: // mì ly
            case 667: // sushi
                pl.itemTime.lastTimeEatMeal = System.currentTimeMillis();
                pl.itemTime.isEatMeal = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconMeal);
                pl.itemTime.iconMeal = item.template.iconID;
                break;
            case 2037: // máy dò đồ
                pl.itemTime.lastTimeUseMayDo2 = System.currentTimeMillis();
                pl.itemTime.isUseMayDo2 = true;
                break;
            case 1560:
                pl.itemTime.lastTimedkhi = System.currentTimeMillis();
                pl.itemTime.isdkhi = true;
                Service.gI().Send_Caitrang(pl);
                Service.gI().point(pl);
                ItemTimeService.gI().removeItemTime(pl, pl.itemTime.icondkhi);
                pl.itemTime.iconMeal = item.template.iconID;
                break;
        }
        Service.gI().point(pl);
        ItemTimeService.gI().sendAllItemTime(pl);
        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
        InventoryServiceNew.gI().sendItemBags(pl);
    }

    private void controllerCallRongThan(Player pl, Item item) {
        int tempId = item.template.id;
        if (tempId >= SummonDragon.NGOC_RONG_1_SAO && tempId <= SummonDragon.NGOC_RONG_7_SAO) {
            switch (tempId) {
                case SummonDragon.NGOC_RONG_1_SAO:
                case SummonDragon.NGOC_RONG_2_SAO:
                case SummonDragon.NGOC_RONG_3_SAO:
                    SummonDragon.gI().openMenuSummonShenron(pl, (byte) (tempId - 13));
                    break;
                default:
                    NpcService.gI().createMenuConMeo(pl, ConstNpc.TUTORIAL_SUMMON_DRAGON,
                            -1, "Bạn chỉ có thể gọi rồng từ ngọc 3 sao, 2 sao, 1 sao", "Hướng\ndẫn thêm\n(mới)", "OK");
                    break;
            }
        }
    }

    private void learnSkill(Player pl, Item item) {
        Message msg;
        try {
            if (item.template.gender == pl.gender || item.template.gender == 3) {
                String[] subName = item.template.name.split("");
                byte level = Byte.parseByte(subName[subName.length - 1]);
                Skill curSkill = SkillUtil.getSkillByItemID(pl, item.template.id);
                if (curSkill.point == 7) {
                    Service.gI().sendThongBao(pl, "Kỹ năng đã đạt tối đa!");
                } else {
                    if (curSkill.point == 0) {
                        if (level == 1) {
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id),
                                    level);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.gI().messageSubCommand((byte) 23);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                        } else {
                            Skill skillNeed = SkillUtil
                                    .createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                            Service.gI().sendThongBao(pl,
                                    "Vui lòng học " + skillNeed.template.name + " cấp " + skillNeed.point + " trước!");
                        }
                    } else {
                        if (curSkill.point + 1 == level) {
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id),
                                    level);
                            // System.out.println(curSkill.template.name + " - " + curSkill.point);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.gI().messageSubCommand((byte) 62);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                        } else {
                            Service.gI().sendThongBao(pl, "Vui lòng học " + curSkill.template.name + " cấp "
                                    + (curSkill.point + 1) + " trước!");
                        }
                    }
                    InventoryServiceNew.gI().sendItemBags(pl);
                }
            } else {
                Service.gI().sendThongBao(pl, "Không thể thực hiện");
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void useTDLT(Player pl, Item item) {
        if (pl.itemTime.isUseTDLT) {
            ItemTimeService.gI().turnOffTDLT(pl, item);
        } else {
            ItemTimeService.gI().turnOnTDLT(pl, item);
        }
    }

    private void openThoiVangMAX(Player pl, Item item) {
        NpcService.gI().createMenuConMeo(pl, ConstNpc.TVMAX, -1,
                "1 Thỏi Vàng Trị Giá 500Tr Vàng\n\nHiện Tại Có:" + item.quantity
                + " Thỏi Vàng\n\nNgươi Muốn Sài Bao Nhiêu Thỏi?",
                "1 Thỏi", "5 Thỏi", "10 Thỏi", "25 Thỏi", "50 Thỏi", "100 Thỏi");
        return;
    }

    private void usePorata(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
            return;
        }
        Item get5 = InventoryServiceNew.gI().findItem(pl.inventory.itemsBody, 1440);
        Item get6 = InventoryServiceNew.gI().findItem(pl.inventory.itemsBody, 1505);
        Item pet5 = InventoryServiceNew.gI().findItem(pl.pet.inventory.itemsBody, 1440);
        Item pet6 = InventoryServiceNew.gI().findItem(pl.pet.inventory.itemsBody, 1505);
        if (pet6 != null && pl.fusion.typeFusion == ConstPlayer.NON_FUSION && get5 != null
                || pet5 != null && get6 != null && pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
            pl.pet.ggtv4(true);
        } else if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION && get5 == null
                || pl.fusion.typeFusion == ConstPlayer.NON_FUSION && get6 == null) {
            pl.pet.fusion(true);
        } else {
            pl.pet.unFusion();
        }
    }

    private void usePorata2(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
            return;
        }
        Item get5 = InventoryServiceNew.gI().findItem(pl.inventory.itemsBody, 1440);
        Item get6 = InventoryServiceNew.gI().findItem(pl.inventory.itemsBody, 1505);
        Item pet5 = InventoryServiceNew.gI().findItem(pl.pet.inventory.itemsBody, 1440);
        Item pet6 = InventoryServiceNew.gI().findItem(pl.pet.inventory.itemsBody, 1505);
        if (pet6 != null && pl.fusion.typeFusion == ConstPlayer.NON_FUSION && get5 != null
                || pet5 != null && get6 != null && pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
            pl.pet.ggtv4(true);
        } else if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION && get5 == null
                || pl.fusion.typeFusion == ConstPlayer.NON_FUSION && get6 == null) {
            pl.pet.fusion2(true);
        } else {
            pl.pet.unFusion();
        }
    }

    private void usePorata3(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
            return;
        }
        Item get5 = InventoryServiceNew.gI().findItem(pl.inventory.itemsBody, 1440);
        Item get6 = InventoryServiceNew.gI().findItem(pl.inventory.itemsBody, 1505);
        Item pet5 = InventoryServiceNew.gI().findItem(pl.pet.inventory.itemsBody, 1440);
        Item pet6 = InventoryServiceNew.gI().findItem(pl.pet.inventory.itemsBody, 1505);
        if (pet6 != null && pl.fusion.typeFusion == ConstPlayer.NON_FUSION && get5 != null
                || pet5 != null && get6 != null && pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
            pl.pet.ggtv4(true);
        } else if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION && get5 == null
                || pl.fusion.typeFusion == ConstPlayer.NON_FUSION && get6 == null) {
            pl.pet.fusion3(true);
        } else {
            pl.pet.unFusion();
        }
    }

    private void usePorata4(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
            return;
        }
        Item get5 = InventoryServiceNew.gI().findItem(pl.inventory.itemsBody, 1440);
        Item get6 = InventoryServiceNew.gI().findItem(pl.inventory.itemsBody, 1505);
        Item pet5 = InventoryServiceNew.gI().findItem(pl.pet.inventory.itemsBody, 1440);
        Item pet6 = InventoryServiceNew.gI().findItem(pl.pet.inventory.itemsBody, 1505);
        if (pet6 != null && pl.fusion.typeFusion == ConstPlayer.NON_FUSION && get5 != null
                || pet5 != null && get6 != null && pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
            pl.pet.ggtv4(true);
        } else if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION && get5 == null
                || pl.fusion.typeFusion == ConstPlayer.NON_FUSION && get6 == null) {
            pl.pet.fusion4(true);
        } else {
            pl.pet.unFusion();
        }
    }

    private void openCapsuleUI(Player pl) {
        pl.iDMark.setTypeChangeMap(ConstMap.CHANGE_CAPSULE);
        ChangeMapService.gI().openChangeMapTab(pl);
    }

    public void choseMapCapsule(Player pl, int index) {
        int zoneId = -1;
        if (index >= 0 && index <= pl.mapCapsule.size()) {
            // if (index >= pl.mapCapsule.size() - 1) {
            // Service.gI().sendThongBao(pl, "Có lỗi xãy ra!");
            // return;
            // }
            Zone zoneChose = pl.mapCapsule.get(index);
            // Kiểm tra số lượng người trong khu

            if (zoneChose.getNumOfPlayers() > 25
                    || MapService.gI().isMapDoanhTrai(zoneChose.map.mapId)
                    || MapService.gI().isMapBanDoKhoBau(zoneChose.map.mapId)
                    || MapService.gI().isMapMaBu(zoneChose.map.mapId)
                    || MapService.gI().isMapHuyDiet(zoneChose.map.mapId)) {
                Service.gI().sendThongBao(pl, "Hiện Tại Không Thể Vào Được Khu!");
                return;
            }
            if (index != 0 || zoneChose.map.mapId == 2
                    || zoneChose.map.mapId == 2
                    || zoneChose.map.mapId == 2) {
                pl.mapBeforeCapsule = pl.zone;
            } else {
                zoneId = pl.mapBeforeCapsule != null ? pl.mapBeforeCapsule.zoneId : -1;
                pl.mapBeforeCapsule = null;
            }
            ChangeMapService.gI().changeMapBySpaceShip(pl, pl.mapCapsule.get(index).map.mapId, zoneId, -1);
        }
    }

    public void eatPea(Player player) {
        Item pea = null;
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.type == 6) {
                pea = item;
                break;
            }
        }
        if (pea != null) {
            int hpKiHoiPhuc = 0;
            int lvPea = Integer.parseInt(pea.template.name.substring(13));
            for (Item.ItemOption io : pea.itemOptions) {
                if (io.optionTemplate.id == 2) {
                    hpKiHoiPhuc = io.param * 10000;
                    break;
                }
                if (io.optionTemplate.id == 48) {
                    hpKiHoiPhuc = io.param;
                    break;
                }
            }
            player.nPoint.setHp((long) player.nPoint.hp + hpKiHoiPhuc);
            player.nPoint.setMp((long) player.nPoint.mp + hpKiHoiPhuc);
            PlayerService.gI().sendInfoHpMp(player);
            Service.gI().sendInfoPlayerEatPea(player);
            if (player.pet != null && player.zone.equals(player.pet.zone) && !player.pet.isDie()) {
                int statima = 100 * lvPea;
                player.pet.nPoint.stamina += statima;
                if (player.pet.nPoint.stamina > player.pet.nPoint.maxStamina) {
                    player.pet.nPoint.stamina = player.pet.nPoint.maxStamina;
                }
                player.pet.nPoint.setHp((long) (player.pet.nPoint.hp + hpKiHoiPhuc));
                player.pet.nPoint.setMp((long) (player.pet.nPoint.mp + hpKiHoiPhuc));
                Service.gI().sendInfoPlayerEatPea(player.pet);
                Service.gI().chatJustForMe(player, player.pet, "Cảm ơn sư phụ đã cho con đậu thần");
            }

            InventoryServiceNew.gI().subQuantityItemsBag(player, pea, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        }
    }

    private void upSkillPet(Player pl, Item item) {
        if (pl.pet == null) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        try {
            switch (item.template.id) {
                case 402: // skill 1
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 0)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 403: // skill 2
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 1)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 404: // skill 3
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 2)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 759: // skill 4
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 3)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;

            }

        } catch (Exception e) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            e.printStackTrace();
        }
    }

    private void ItemSKH(Player pl, Item item) {// hop qua skh
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Hãy chọn một món quà", "Áo", "Quần", "Găng", "Giày",
                "Rada", "Từ Chối");
    }

    private void ItemDHD(Player pl, Item item) {// hop qua do huy diet
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Hãy chọn một món quà", "Áo", "Quần", "Găng", "Giày",
                "Rada", "Từ Chối");
    }

    private void Hopts(Player pl, Item item) {// hop qua do thien su
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Chọn hành tinh của mày đi", "Set trái đất",
                "Set namec", "Set xayda", "Từ chổi");
    }

    private void Hoptst(Player pl, Item item) {// hop qua do thien su thuong
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Chọn hành tinh của mày đi", "Set trái đất",
                "Set namec", "Set xayda", "Từ chổi");
    }

    private void Hophdt(Player pl, Item item) {// hop qua do huy diet top
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Chọn hành tinh của mày đi", "Set trái đất",
                "Set namec", "Set xayda", "Từ chổi");
    }

    private void Hophd(Player pl, Item item) {// hop qua do huy diet top
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Chọn hành tinh của mày đi", "Set trái đất",
                "Set namec", "Set xayda", "Từ chổi");
    }

    private void Hoptl(Player pl, Item item) {// hop qua do tl top
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Chọn hành tinh của mày đi", "Set trái đất",
                "Set namec", "Set xayda", "Từ chổi");
    }

    private void GetRubyFormWoodChest(Player pl, Item item) {
        int level = 0;
        for (ItemOption op : item.itemOptions) {
            if (op.optionTemplate.id == 72) {
                level = op.param;
                break;
            }
        }
        int HongNgoc = 0;
        switch (level) {
            case 1:
            case 2:
            case 3:
            case 4:
                HongNgoc = Util.nextInt(500, 1000);
                break;
            case 5:
            case 6:
                HongNgoc = Util.nextInt(1, 15);
                break;
            case 7:
                HongNgoc = Util.nextInt(1, 15);
                break;
            case 8:
                HongNgoc = Util.nextInt(1, 15);
                break;
            case 9:
                HongNgoc = Util.nextInt(1, 15);
                break;
            case 10:
                HongNgoc = Util.nextInt(1, 15);
                break;
            case 11:
                HongNgoc = Util.nextInt(1, 15);
                break;
        }
        pl.inventory.ruby += HongNgoc;
        Service.gI().sendMoney(pl);
        Service.getInstance().sendThongBao(pl, "Bạn nhận được " + HongNgoc + " hồng ngọc");
    }

    private void openWoodChest(Player pl, Item item) {
        int time = (int) TimeUtil.diffDate(new Date(), new Date(item.createTime), TimeUtil.DAY);
        if (time != 0) {
            GetRubyFormWoodChest(pl, item);
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);
        } else {
            Service.getInstance().sendThongBao(pl, "Vui lòng đợi 24h");
        }
    }

    private void RuongItemCap2(Player pl, Item item) {
        NpcService.gI().createMenuConMeo(pl, 1278, -1, "Bạn muốn chọn gì ?", "Cuồng nộ", "Bổ khí", "Bổ huyết",
                "Giáp Xên bọ hung", "Ẩn danh");
    }

    public void SendItemCap2(Player pl, int type, int SoLuong) {
        short tempId = 0;
        switch (type) {
            case 4:
                tempId = 1099;
                break;
            case 5:
                tempId = 1101;
                break;
            case 6:
                tempId = 1100;
                break;
            case 7:
                tempId = 1102;
                break;
            case 8:
                tempId = 1103;
                break;
        }
        Item item = InventoryServiceNew.gI().findItem(pl.inventory.itemsBag, 1278);
        if (item.quantity >= SoLuong) {
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, SoLuong);
            Item itemsend = ItemService.gI().createNewItem(tempId, SoLuong);
            InventoryServiceNew.gI().addItemBag(pl, itemsend);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.gI().sendThongBao(pl, "Bạn nhận được x" + SoLuong + " " + itemsend.template.name);
        } else {
            Service.gI().sendThongBao(pl, "Số lượng rương không đủ");
        }
    }

    public void showthanthu(Player player) {
        if (player.CapBacThan != -1) {
            NpcService.gI().createMenuConMeo(player, ConstNpc.NpcThanThu, 21587,
                    "|7|Menu By Bkt\n"
                    + "|1|Name: " + player.TenThan
                    + "\n|2|Level: " + player.ThanLevel + " ("
                    + (player.ExpThan * 100 / (3000000L + player.ThanLevel * 1500000L)) + "%)"
                    + "\n|2|Kinh nghiệm: " + Util.format(player.ExpThan)
                    + "\nCấp bậc: " + player.NameThanthu(player.CapBacThan)
                    + "\n|5|Thức ăn: " + player.ThucAnThan + "%"
                    + "\nSức Đánh: " + Util.getFormatNumber(player.DameThan)
                    + "\nMáu: " + Util.getFormatNumber(player.MauThan)
                    + "\nKĩ năng: " + player.TrieuHoiKiNang(player.CapBacThan),
                    "Load Chiến Thần", "Cho ăn\n200 Hồng ngọc", "Đi theo", "Tấn công người", "Tấn công Quái",
                    "Về nhà", "Auto cho ăn sau 15p", "Đột phá\nChiến Thần");
        } else {
            Service.gI().sendThongBaoOK(player, "Nhận Thần Tại NPC Bardock Đồi Cát Để Sử Dụng");
        }
    }

}
