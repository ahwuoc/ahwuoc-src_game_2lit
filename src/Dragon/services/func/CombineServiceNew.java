package Dragon.services.func;

import Dragon.consts.ConstNpc;
import Dragon.models.item.Item;
import Dragon.models.item.Item.ItemOption;
import Dragon.models.map.ItemMap;
import Dragon.models.npc.Npc;
import Dragon.models.npc.NpcManager;
import Dragon.models.player.Player;
import Dragon.server.Manager;
import Dragon.server.ServerNotify;
import com.girlkun.network.io.Message;
import Dragon.services.*;
import Dragon.utils.Logger;
import Dragon.utils.Util;

import java.util.*;
import java.util.stream.Collectors;

public class CombineServiceNew {

    private static final int COST_DOI_VE_DOI_DO_HUY_DIET = 500000000;
    private static final int RUBY_DAP_DO_KICH_HOAT = 10;
    private static final int COST_DOI_MANH_KICH_HOAT = 500000000;

    private static final int COST = 500000000;

    private static final int TIME_COMBINE = 1;

    private static final byte MAX_STAR_ITEM = 8;
    private static final byte MAX_LEVEL_ITEM = 8;

    private static final byte OPEN_TAB_COMBINE = 0;
    private static final byte REOPEN_TAB_COMBINE = 1;
    private static final byte COMBINE_SUCCESS = 2;
    private static final byte COMBINE_FAIL = 3;
    private static final byte COMBINE_CHANGE_OPTION = 4;
    private static final byte COMBINE_DRAGON_BALL = 5;
    public static final byte OPEN_ITEM = 6;

    public static final int EP_SAO_TRANG_BI = 500;
    public static final int PHA_LE_HOA_TRANG_BI = 501;
    public static final int CHUYEN_HOA_TRANG_BI = 502;

    public static final int NANG_CAP_VAT_PHAM = 510;
    public static final int NANG_CAP_BONG_TAI = 511;
    public static final int MO_CHI_SO_BONG_TAI = 519;
    private static final int RUBY_BONG_TAI = 5000;

    public static final int MO_CHI_SO_Chien_Linh = 520;
    public static final int NANG_CAP_KHI = 521;
    public static final int NANG_CAP_MEO = 2521;
    public static final int NANG_CAP_LUFFY = 5291;
    public static final int Nang_Chien_Linh = 522;
    public static final int CHE_TAO_TRANG_BI_TS = 523;
    public static final int NHAP_NGOC_RONG = 513;
    public static final int CHE_TAO_PHUOC = 3232;
    public static final int PHAN_RA_DO_THAN_LINH = 514;
    public static final int NANG_CAP_DO_TS = 515;
    public static final int NANG_CAP_SKH_VIP = 516;
    public static final int DOI_DIEM = 595;
    public static final int NANG_CAP_CHAN_MENH = 5380;
    public static final int NANG_CAP_SARINGAN = 5381;

    private static final int GOLD_MOCS_BONG_TAI = 500_000_000;
    private static final int Gem_MOCS_BONG_TAI = 500;
    private static final int RUBY_MOCS_BONG_TAI = 500;
    private static final int GOLD_BONG_TAI2 = 500_000_000;
    private static final int RUBY_BONG_TAI2 = 1_000;
    private static final int GEM_BONG_TAI2 = 1_000;

    private static final int GOLD_LINHTHU = 500_000_000;
    private static final int GEM_LINHTHU = 5_000;

    private static final int RATIO_NANG_CAP_ChienLinh = 50;
    private static final int GOLD_Nang_Chien_Linh = 1_000_000_000;
    private static final int RUBY_Nang_Chien_Linh = 5000;
    // private static final int RATIO_NANG_CAP = 100;
    private static final int GOLD_MOCS_Chien_Linh = 500_000_000;
    private static final int RUBY_MOCS_Chien_Linh = 1000;

    private static final int GOLD_NANG_KHI = 500_000_000;
    private static final int RUBY_NANG_KHI = 5000;
    private static final int GOLD_NANG_LUFFY = 500_000_000;
    private static final int RUBY_NANG_LUFFY = 100;
    public static final int REN_KIEM_Z = 517;
    public static final int CTZENO = 518;
    public static final int SKH1 = 521;

    private static final int GOLD_BONG_TAI = 200_000_000;
    private static final int GOLD_KIEM_Z = 200_000_000;
    private static final int GEM_BONG_TAI = 1_000;
    private static final int GEM_KIEM_Z = 1_000;
    private static final int RATIO_BONG_TAI = 50;
    private static final int RATIO_NANG_CAP = 50;
    private static final int RATIO_KIEM_Z2 = 40;
    private static final int RATIO_CTZENO = 50;
    private static final int GEM_CTZENO = 1_000;
    private static final int GOLD_CTZENO = 200_000_000;
    private static final int RATIO_SKH1 = 50;
    private static final int GEM_SKH1 = 1_000;
    private static final int GOLD_SKH1 = 200_000_000;

    // --------Sách Tuyệt Kỹ
    public static final int GIAM_DINH_SACH = 1233;
    public static final int TAY_SACH = 1234;
    public static final int NANG_CAP_SACH_TUYET_KY = 1235;
    public static final int PHUC_HOI_SACH = 1236;
    public static final int PHAN_RA_SACH = 1237;

    public static final int NANG_CAP_DO_KICH_HOAT = 550;
    public static final int NANG_CAP_DO_KICH_HOAT_THUONG = 800;
    public static final int COMBINE_TAN_DAN_FRAGMENT = 6000; // Ghép mảnh tàn đan (99 mảnh → 1 đan)
    public static final int UPGRADE_TUTIEN_DAN = 6001; // Nâng cấp đan (9 đan + công thức)

    // Menu nâng cấp đan Tu Tiên theo cấp
    public static final int UPGRADE_TUTIEN_DAN_LEVEL_1 = 6002; // Nâng cấp lên cấp 1
    public static final int UPGRADE_TUTIEN_DAN_LEVEL_2 = 6003; // Nâng cấp lên cấp 2
    public static final int UPGRADE_TUTIEN_DAN_LEVEL_3 = 6004; // Nâng cấp lên cấp 3
    public static final int UPGRADE_TUTIEN_DAN_LEVEL_4 = 6005; // Nâng cấp lên cấp 4
    public static final int UPGRADE_TUTIEN_DAN_LEVEL_5 = 6006; // Nâng cấp lên cấp 5
    public static final int UPGRADE_TUTIEN_DAN_LEVEL_6 = 6007; // Nâng cấp lên cấp 6
    public static final int UPGRADE_TUTIEN_DAN_LEVEL_7 = 6008; // Nâng cấp lên cấp 7
    public static final int UPGRADE_TUTIEN_DAN_LEVEL_8 = 6009; // Nâng cấp lên cấp 8
    public static final int UPGRADE_TUTIEN_DAN_LEVEL_9 = 6010; // Nâng cấp lên cấp 9
    public static final int UPGRADE_TUTIEN_DAN_LEVEL_10 = 6011; // Nâng cấp lên cấp 10
    private final Npc baHatMit;
    private final Npc itachi;
    // private final Npc tosukaio;
    private final Npc whis;
    private final Npc npsthiensu64;
    private final Npc dodo;
    private final Npc kaido;
    private final Npc chomeoan;
    private final Npc trunglinhthu;
    private static CombineServiceNew i;

    public CombineServiceNew() {
        this.baHatMit = NpcManager.getNpc(ConstNpc.BA_HAT_MIT);
        this.npsthiensu64 = NpcManager.getNpc(ConstNpc.NPC_64);
        this.dodo = NpcManager.getNpc(ConstNpc.DO_DO_DO);
        this.kaido = NpcManager.getNpc(ConstNpc.LUYENDUOCSU);
        this.trunglinhthu = NpcManager.getNpc(ConstNpc.TRUNG_LINH_THU);
        this.whis = NpcManager.getNpc(ConstNpc.WHIS);
        this.chomeoan = NpcManager.getNpc(ConstNpc.CHO_MEO_AN);
        this.itachi = NpcManager.getNpc(ConstNpc.ITACHI);
    }

    public static CombineServiceNew gI() {
        if (i == null) {
            i = new CombineServiceNew();
        }
        return i;
    }

    /**
     * Mở tab đập đồ
     *
     * @param player
     * @param type kiểu đập đồ
     */
    public void openTabCombine(Player player, int type) {
        openTabCombine(player, type, null);
    }

    /**
     * Mở tab đập đồ với NPC cụ thể
     *
     * @param player
     * @param type kiểu đập đồ
     * @param npc NPC hiện tại
     */
    public void openTabCombine(Player player, int type, Dragon.models.npc.Npc npc) {
        player.combineNew.setTypeCombine(type);
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(OPEN_TAB_COMBINE);
            msg.writer().writeUTF(getTextInfoTabCombine(type));
            msg.writer().writeUTF(getTextTopTabCombine(type));
            // Sử dụng NPC hiện tại nếu có, nếu không thì dùng NPC mặc định
            Npc npcForCombine = npc != null ? npc : getNpcByType(type);
            if (npcForCombine != null) {
                player.iDMark.setNpcChose(npcForCombine);
                msg.writer().writeShort(npcForCombine.tempId);
            } else if (player.iDMark.getNpcChose() != null) {
                msg.writer().writeShort(player.iDMark.getNpcChose().tempId);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hiển thị thông tin đập đồ
     *
     * @param player
     */
    public void showInfoCombine(Player player, int[] index) {
        if (player != null && player.combineNew != null && player.combineNew.itemsCombine != null) {
            player.combineNew.clearItemCombine();
        }
        if (index.length > 0) {
            for (int i = 0; i < index.length; i++) {
                player.combineNew.itemsCombine.add(player.inventory.itemsBag.get(index[i]));
            }
        }
        switch (player.combineNew.typeCombine) {
            case NANG_CAP_CHAN_MENH:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item bongTai = null;
                    Item manhVo = null;
                    int star = 0;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.template.id == 1279 && item.isNotNullItem()) {
                            manhVo = item;
                        } else if (item.template.id >= 1300 && item.template.id <= 1308) {
                            bongTai = item;
                            star = item.template.id - 1300;
                        }
                    }
                    if (bongTai != null && bongTai.template.id == 1308) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Chân Mệnh đã đạt cấp tối đa", "Đóng");
                        return;
                    }
                    player.combineNew.DiemNangcap = getDiemNangcapChanmenh(star);
                    player.combineNew.DaNangcap = getDaNangcapChanmenh(star);
                    player.combineNew.TileNangcap = getTiLeNangcapChanmenh(star);
                    if (bongTai != null && manhVo != null
                            && (bongTai.template.id >= 1300 && bongTai.template.id < 1308)) {
                        String npcSay = bongTai.template.name + "\n|2|";
                        for (Item.ItemOption io : bongTai.itemOptions) {
                            npcSay += io.getOptionString() + "\n";
                        }
                        npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.TileNangcap + "%" + "\n";
                        if (player.combineNew.DiemNangcap <= player.PointBoss) {
                            npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.DiemNangcap) + " Điểm Săn Boss";
                            baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                    "Nâng cấp\ncần " + player.combineNew.DaNangcap + " Đá Hoàng Kim");
                        } else {
                            npcSay += "Còn thiếu "
                                    + Util.numberToMoney(player.combineNew.DiemNangcap - player.PointBoss)
                                    + " Điểm Săn Boss";
                            baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Chân Mệnh và Đá Hoàng Kim", "Đóng");
                    }
                } else if (player.combineNew.itemsCombine.size() == 3) {
                    Item bongTai = null;
                    Item manhVo = null;
                    Item tv = null;
                    int star = 0;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.template.id == 1279) {
                            manhVo = item;
                        } else if (item.template.id == 457) {
                            tv = item;
                        } else if (item.template.id >= 1300 && item.template.id <= 1308) {
                            bongTai = item;
                            star = item.template.id - 1300;
                        }
                    }
                    if (bongTai != null && bongTai.template.id == 1308) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Chân Mệnh đã đạt cấp tối đa", "Đóng");
                        return;
                    }
                    player.combineNew.DiemNangcap = getDiemNangcapChanmenh(star) - 5;
                    player.combineNew.DaNangcap = getDaNangcapChanmenh(star) - 5;
                    player.combineNew.TileNangcap = getTiLeNangcapChanmenh(star) + 5;
                    if (tv != null && tv.quantity >= 1 && bongTai != null && manhVo != null
                            && manhVo.quantity >= player.combineNew.DaNangcap - 5
                            && (bongTai.template.id >= 1300 && bongTai.template.id < 1308)) {
                        String npcSay = bongTai.template.name + "\n|2|";
                        for (Item.ItemOption io : bongTai.itemOptions) {
                            npcSay += io.getOptionString() + "\n";
                        }
                        npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.TileNangcap + "%" + "\n";
                        if (player.combineNew.DiemNangcap <= player.PointBoss - 5) {
                            npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.DiemNangcap) + " Điểm Săn Boss";
                            baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                    "Nâng cấp\ncần " + player.combineNew.DaNangcap + " Đá Hoàng Kim");
                        } else {
                            npcSay += "Còn thiếu "
                                    + Util.numberToMoney(player.combineNew.DiemNangcap - player.PointBoss)
                                    + " Điểm Săn Boss";
                            baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Chân Mệnh và Đá Hoàng Kim", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 Chân Mệnh và Đá Hoàng Kim", "Đóng");
                }
                break;
            case NANG_CAP_SARINGAN:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item bongTai = null;
                    Item manhVo = null;
                    int star = 0;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.template.id == 1525 && item.isNotNullItem()) {
                            manhVo = item;
                        } else if (item.template.id >= 1513 && item.template.id <= 1520) {
                            bongTai = item;
                            star = item.template.id - 1513;
                        }
                    }
                    if (bongTai != null && bongTai.template.id == 1520) {
                        this.itachi.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Saringan đã đạt cấp tối đa", "Đóng");
                        return;
                    }
                    player.combineNew.DiemNangcap = getDiemNangcapChanmenh(star);
                    player.combineNew.DaNangcap = getDaNangcapChanmenh(star);
                    player.combineNew.TileNangcap = getTiLeNangcapChanmenh(star);
                    if (bongTai != null && manhVo != null
                            && (bongTai.template.id >= 1513 && bongTai.template.id < 1520)) {
                        String npcSay = bongTai.template.name + "\n|2|";
                        for (Item.ItemOption io : bongTai.itemOptions) {
                            npcSay += io.getOptionString() + "\n";
                        }
                        npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.TileNangcap + "%" + "\n";
                        if (player.combineNew.DiemNangcap <= player.ChuyenSinh) {
                            npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.DiemNangcap) + " Điểm Săn Boss";
                            itachi.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                    "Nâng cấp\ncần " + player.combineNew.DaNangcap + " Bí Thuật");
                        } else {
                            npcSay += "Còn thiếu "
                                    + Util.numberToMoney(player.combineNew.DiemNangcap - player.ChuyenSinh)
                                    + " Điểm Săn Boss";
                            itachi.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                        }
                    } else {
                        this.itachi.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Saringan và Bí Thuật", "Đóng");
                    }
                } else if (player.combineNew.itemsCombine.size() == 3) {
                    Item bongTai = null;
                    Item manhVo = null;
                    Item tv = null;
                    int star = 0;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.template.id == 1525) {
                            manhVo = item;
                        } else if (item.template.id == 457) {
                            tv = item;
                        } else if (item.template.id >= 1513 && item.template.id <= 1520) {
                            bongTai = item;
                            star = item.template.id - 1513;
                        }
                    }
                    if (bongTai != null && bongTai.template.id == 1520) {
                        this.itachi.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Saringan đã đạt cấp tối đa", "Đóng");
                        return;
                    }
                    player.combineNew.DiemNangcap = getDiemNangcapChanmenh(star) - 5;
                    player.combineNew.DaNangcap = getDaNangcapChanmenh(star) - 5;
                    player.combineNew.TileNangcap = getTiLeNangcapChanmenh(star) + 5;
                    if (tv != null && tv.quantity >= 1 && bongTai != null && manhVo != null
                            && manhVo.quantity >= player.combineNew.DaNangcap - 5
                            && (bongTai.template.id >= 1300 && bongTai.template.id < 1308)) {
                        String npcSay = bongTai.template.name + "\n|2|";
                        for (Item.ItemOption io : bongTai.itemOptions) {
                            npcSay += io.getOptionString() + "\n";
                        }
                        npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.TileNangcap + "%" + "\n";
                        if (player.combineNew.DiemNangcap <= player.ChuyenSinh - 5) {
                            npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.DiemNangcap) + " Điểm Săn Boss";
                            itachi.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                    "Nâng cấp\ncần " + player.combineNew.DaNangcap + " Bí Thuật");
                        } else {
                            npcSay += "Còn thiếu "
                                    + Util.numberToMoney(player.combineNew.DiemNangcap - player.ChuyenSinh)
                                    + " Điểm Săn Boss";
                            itachi.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                        }
                    } else {
                        this.itachi.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Saringan và Bí Thuật", "Đóng");
                    }
                } else {
                    this.itachi.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 Saringan và Bí Thuật", "Đóng");
                }
                break;
            case DOI_DIEM:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.npsthiensu64.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Con hãy đưa cho ta thức ăn",
                            "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 1) {
                    List<Integer> itemdov2 = new ArrayList<>(Arrays.asList(663, 664, 665, 666, 667));
                    int couponAdd = 0;
                    Item item = player.combineNew.itemsCombine.get(0);
                    if (item.isNotNullItem()) {
                        if (item.template.id >= 663 && item.template.id <= 667) {
                            couponAdd = itemdov2.stream().anyMatch(t -> t == item.template.id) ? 1
                                    : item.template.id <= 667 ? 1 : 1;
                        }
                    }
                    if (couponAdd == 0) {
                        this.npsthiensu64.createOtherMenu(player, ConstNpc.IGNORE_MENU, "THỨC ĂN!!!!!!!!", "Đóng");
                        return;
                    }
                    String npcSay = "|2|Sau khi phân rã vật phẩm\n|7|"
                            + "Bạn sẽ nhận được : " + couponAdd + " điểm\n"
                            + (500000000 > player.inventory.gold ? "|7|" : "|1|")
                            + "Cần " + Util.numberToMoney(500000000) + " vàng";

                    if (player.inventory.gold < 500000000) {
                        this.npsthiensu64.npcChat(player, "Hết tiền rồi\nẢo ít thôi con");
                        return;
                    }
                    this.npsthiensu64.createOtherMenu(player, ConstNpc.MENU_PHAN_RA_DO_THAN_LINH,
                            npcSay, "Thức Ăn", "Từ chối");
                } else {
                    this.npsthiensu64.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cái Đầu Buồi", "Đóng");
                }
                break;
            // ------Sách Tuyệt Kỹ
            case GIAM_DINH_SACH:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item sachTuyetKy = null;
                    Item buaGiamDinh = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (issachTuyetKy(item)) {
                            sachTuyetKy = item;
                        } else if (item.template.id == 1391) {
                            buaGiamDinh = item;
                        }
                    }
                    if (sachTuyetKy != null && buaGiamDinh != null) {

                        String npcSay = "|1|" + sachTuyetKy.getName() + "\n";
                        npcSay += "|2|" + buaGiamDinh.getName() + " " + buaGiamDinh.quantity + "/1";
                        baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                "Giám định", "Từ chối");
                    } else {
                        Service.getInstance().sendThongBaoOK(player, "Cần Sách Tuyệt Kỹ và bùa giám định");
                        return;
                    }
                } else {
                    Service.getInstance().sendThongBaoOK(player, "Cần Sách Tuyệt Kỹ và bùa giám định");
                    return;
                }
                break;
            case TAY_SACH:
                if (player.combineNew.itemsCombine.size() == 1) {
                    Item sachTuyetKy = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (issachTuyetKy(item)) {
                            sachTuyetKy = item;
                        }
                    }
                    if (sachTuyetKy != null) {
                        String npcSay = "|2|Tẩy Sách Tuyệt Kỹ";
                        baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                "Đồng ý", "Từ chối");
                    } else {
                        Service.getInstance().sendThongBaoOK(player, "Cần Sách Tuyệt Kỹ để tẩy");
                        return;
                    }
                } else {
                    Service.getInstance().sendThongBaoOK(player, "Cần Sách Tuyệt Kỹ để tẩy");
                    return;
                }
                break;

            case NANG_CAP_SACH_TUYET_KY:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item sachTuyetKy = null;
                    Item kimBamGiay = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (issachTuyetKy(item)
                                && (item.template.id == 1383 || item.template.id == 1385 || item.template.id == 1387)) {
                            sachTuyetKy = item;
                        } else if (item.template.id == 1390) {
                            kimBamGiay = item;
                        }
                    }
                    if (sachTuyetKy != null && kimBamGiay != null) {
                        String npcSay = "|2|Nâng cấp sách tuyệt kỹ\n";
                        npcSay += "Cần 10 Kìm bấm giấy\n"
                                + "Tỉ lệ thành công: 10%\n"
                                + "Nâng cấp thất bại sẽ mất 10 Kìm bấm giấy";
                        baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                "Nâng cấp", "Từ chối");
                    } else {
                        Service.getInstance().sendThongBaoOK(player, "Cần Sách Tuyệt Kỹ 1 và 10 Kìm bấm giấy.");
                        return;
                    }
                } else {
                    Service.getInstance().sendThongBaoOK(player, "Cần Sách Tuyệt Kỹ 1 và 10 Kìm bấm giấy.");
                    return;
                }
                break;
            case PHUC_HOI_SACH:
                if (player.combineNew.itemsCombine.size() == 1) {
                    Item sachTuyetKy = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (issachTuyetKy(item)) {
                            sachTuyetKy = item;
                        }
                    }
                    if (sachTuyetKy != null) {
                        String npcSay = "|2|Phục hồi " + sachTuyetKy.getName() + "\n"
                                + "Cần 10 cuốn sách cũ\n"
                                + "Phí phục hồi 10 triệu vàng";
                        baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                "Đồng ý", "Từ chối");
                    } else {
                        Service.getInstance().sendThongBaoOK(player, "Không tìm thấy vật phẩm");
                        return;
                    }
                } else {
                    Service.getInstance().sendThongBaoOK(player, "Không tìm thấy vật phẩm");
                    return;
                }
                break;
            case PHAN_RA_SACH:
                if (player.combineNew.itemsCombine.size() == 1) {
                    Item sachTuyetKy = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (issachTuyetKy(item)) {
                            sachTuyetKy = item;
                        }
                    }
                    if (sachTuyetKy != null) {
                        String npcSay = "|2|Phân rã sách\n"
                                + "Nhận lại 5 cuốn sách cũ\n"
                                + "Phí rã 10 triệu vàng";
                        baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                "Đồng ý", "Từ chối");
                    } else {
                        Service.getInstance().sendThongBaoOK(player, "Không tìm thấy vật phẩm");
                        return;
                    }
                } else {
                    Service.getInstance().sendThongBaoOK(player, "Không tìm thấy vật phẩm");
                    return;
                }
                break;
            case NANG_CAP_DO_KICH_HOAT:
                if (player.combineNew.itemsCombine.size() == 3) {
                    Item thiensu = null;
                    Item skh1 = null;
                    Item skh2 = null;
                    if (player.combineNew.itemsCombine.get(0).isDTS()) {
                        thiensu = player.combineNew.itemsCombine.get(0);
                    }
                    if (player.combineNew.itemsCombine.get(1).isSKH()) {
                        skh1 = player.combineNew.itemsCombine.get(1);
                    }
                    if (player.combineNew.itemsCombine.get(2).isSKH()) {
                        skh2 = player.combineNew.itemsCombine.get(2);
                    }
                    if (thiensu != null && skh1 != null && skh2 != null) {
                        player.combineNew.goldCombine = 500_000_000;
                        player.combineNew.ratioCombine = 100;
                        String npcSay = "\n|2| " + thiensu.template.name;
                        npcSay += "\n|2| " + skh1.template.name;
                        npcSay += "\n|2| " + skh2.template.name + "\n";
                        npcSay += "\n|7|Ta sẽ phù phép trang bị ngươi cho ta thành 1 trang bị thiên sứ kích hoạt có chỉ số ngẫu nhiên";
                        npcSay += "\n|7|Tỉ lệ thành công: " + player.combineNew.ratioCombine + "%" + "\n";
                        if (player.combineNew.goldCombine <= player.inventory.gold) {
                            npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.goldCombine) + " vàng";
                            baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                    "Nâng cấp\ncần " + player.combineNew.goldCombine + " vàng");
                        } else {
                            npcSay += "Còn thiếu "
                                    + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold)
                                    + " vàng";
                            baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                        }
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy đưa cho ta 1 trang bị Thiên sứ"
                            + "\n và 2 trang bị kích hoạt", "Đóng");
                }

                break;
            case NANG_CAP_DO_KICH_HOAT_THUONG:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Hãy đưa ta 1 món huỷ diệt, ta sẽ cho 1 món huỷ diệt tương ứng", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 2) {
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDTL())
                            .count() != 2) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ huỷ diệt rồi", "Đóng");
                        return;
                    }
                    String npcSay = "|7|Ngoc Rong Meta\n" + "|7|NÂNG CẤP TRANG BỊ KÍCH HOẠT\n"
                            + "|2|Đã đạt đủ số lượng nguyên liệu, bạn sẽ nhận được : \n"
                            + "("
                            + player.combineNew.itemsCombine.stream().filter(Item::isDTL).findFirst().get().typeName()
                            + " kích hoạt)\n" + "[ THƯỜNG ]\n"
                            + "|7|Nâng Cấp Ngay?\n"
                            + "|1|Cần " + Util.numberToMoney(500000000) + " vàng";

                    if (player.inventory.gold < 500000000) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hết tiền rồi\nẢo ít thôi con",
                                "Đóng");
                        return;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                            npcSay, "Nâng cấp\n" + Util.numberToMoney(500000000) + " vàng", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 2) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Nguyên liệu không phù hợp",
                                "Đóng");
                        return;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Còn thiếu nguyên liệu để nâng cấp hãy quay lại sau", "Đóng");
                }
                break;
            case Nang_Chien_Linh:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item linhthu = null;
                    Item ttt = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.template.type == 72) {
                            linhthu = item;
                        } else if (item.template.id == 2031) {
                            ttt = item;
                        }
                    }

                    if (linhthu != null && ttt != null) {

                        player.combineNew.goldCombine = GOLD_Nang_Chien_Linh;
                        player.combineNew.rubyCombine = RUBY_Nang_Chien_Linh;
                        player.combineNew.ratioCombine = RATIO_NANG_CAP_ChienLinh;

                        String npcSay = "Pet: " + linhthu.template.name + " \n|2|";
                        for (Item.ItemOption io : linhthu.itemOptions) {
                            npcSay += io.getOptionString() + "\n";
                        }
                        npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.ratioCombine + "%" + "\n";
                        if (ttt.quantity >= 10) {
                            if (player.combineNew.goldCombine <= player.inventory.gold) {
                                if (player.combineNew.rubyCombine <= player.inventory.ruby) {
                                    npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.goldCombine) + " vàng";
                                    trunglinhthu.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                            "Nâng cấp\ncần " + player.combineNew.rubyCombine + " hồng ngọc");
                                } else {
                                    npcSay += "Còn thiếu "
                                            + Util.numberToMoney(player.combineNew.rubyCombine - player.inventory.ruby)
                                            + " hồng ngọc";
                                    trunglinhthu.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                                }
                            } else {
                                npcSay += "Còn thiếu "
                                        + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold)
                                        + " vàng";
                                trunglinhthu.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                            }
                        } else {
                            npcSay += "Còn thiếu " + Util.numberToMoney(10 - ttt.quantity) + "Thăng tinh thạch";
                            trunglinhthu.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                        }

                    } else {
                        this.trunglinhthu.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Linh Thú và x10 Thăng tinh thạch", "Đóng");
                    }
                } else {
                    this.trunglinhthu.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 Linh Thú và x10 Thăng tinh thạch", "Đóng");
                }
                break;

            case NANG_CAP_LUFFY:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item ctluffy = null;
                    Item dns = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (checkctluffy(item)) {
                            ctluffy = item;
                        } else if (item.template.id == 1335) {
                            dns = item;
                        }
                    }

                    if (ctluffy != null && dns != null) {
                        int lvluffy = lvluffy(ctluffy);
                        int countdns = getcountdnsnangluffy(lvluffy);
                        player.combineNew.goldCombine = getGoldnangluffy(lvluffy);
                        player.combineNew.rubyCombine = getRubydnangluffy(lvluffy);
                        player.combineNew.ratioCombine = getRatioNangluffy(lvluffy);

                        String npcSay = "Cải trang Luffy : " + lvluffy + " \n|2|";
                        for (Item.ItemOption io : ctluffy.itemOptions) {
                            npcSay += io.getOptionString() + "\n";
                        }
                        npcSay += "|7|Tỉ lệ thành công: 50 " + "%" + "\n";
                        if (dns.quantity >= countdns) {
                            if (player.combineNew.goldCombine <= player.inventory.gold) {
                                if (player.combineNew.rubyCombine <= player.inventory.ruby) {
                                    npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.goldCombine) + " vàng";
                                    kaido.createOtherMenu(player, ConstNpc.MENU_NANG_LUFFY, npcSay,
                                            "Nâng cấp\ncần " + player.combineNew.rubyCombine + " hồng ngọc");
                                } else {
                                    npcSay += "Còn thiếu "
                                            + Util.numberToMoney(player.combineNew.rubyCombine - player.inventory.ruby)
                                            + " hồng ngọc";
                                    kaido.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                                }
                            } else {
                                npcSay += "Còn thiếu "
                                        + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold)
                                        + " vàng";
                                kaido.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                            }
                        } else {
                            npcSay += "Còn thiếu " + Util.numberToMoney(countdns - dns.quantity) + " Đá thức tỉnh";
                            kaido.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                        }

                    } else {
                        this.kaido.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Cải trang luffy chưa thức tỉnh Cấp 1-7 và Đá thức tỉnh", "Đóng");
                    }
                } else {
                    this.kaido.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 Cải trang luffy chưa thức tỉnh Cấp 1-7 và Đá thức tỉnh", "Đóng");
                }
                break;
            case NANG_CAP_MEO:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item ctmeo = null;
                    Item dns = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (checkctmeo(item)) {
                            ctmeo = item;
                        } else if (item.template.id == 1004) {
                            dns = item;
                        }
                    }

                    if (ctmeo != null && dns != null) {
                        int lvmeo = lvmeo(ctmeo);
                        int countdns = getcountdnsnangmeo(lvmeo);
                        player.combineNew.goldCombine = getGoldnangmeo(lvmeo);
                        player.combineNew.rubyCombine = getRubydnangmeo(lvmeo);
                        player.combineNew.ratioCombine = getRatioNangmeo(lvmeo);

                        String npcSay = "Thú Cưng : " + lvmeo + " \n|2|";
                        for (Item.ItemOption io : ctmeo.itemOptions) {
                            npcSay += io.getOptionString() + "\n";
                        }
                        npcSay += "|7|Tỉ lệ thành công: 50" + "%" + "\n";
                        if (dns.quantity >= countdns) {
                            if (player.combineNew.goldCombine <= player.inventory.gold) {
                                if (player.combineNew.rubyCombine <= player.inventory.ruby) {
                                    npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.goldCombine) + " vàng";
                                    chomeoan.createOtherMenu(player, ConstNpc.MENU_NANG_MEO, npcSay,
                                            "Nâng cấp\ncần " + player.combineNew.rubyCombine + " hồng ngọc");
                                } else {
                                    npcSay += "Còn thiếu "
                                            + Util.numberToMoney(player.combineNew.rubyCombine - player.inventory.ruby)
                                            + " hồng ngọc";
                                    chomeoan.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                                }
                            } else {
                                npcSay += "Còn thiếu "
                                        + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold)
                                        + " vàng";
                                chomeoan.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                            }
                        } else {
                            npcSay += "Còn thiếu " + Util.numberToMoney(countdns - dns.quantity) + " Thức ăn cho mèo";
                            chomeoan.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                        }

                    } else {
                        this.chomeoan.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 bé mèo và thức ăn cho mèo", "Đóng");
                    }
                } else {
                    this.chomeoan.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 bé mèo và thức ăn cho mèo", "Đóng");
                }
                break;

            case NANG_CAP_BONG_TAI:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item bongtai = null;
                    Item manhvobt = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (checkbongtai(item)) { // gán bông tai c1 hoặc c2
                            bongtai = item;
                        } else if (item.template.id == 933) { // gán mảnh vỡ bt
                            manhvobt = item;
                        }
                    }

                    if (bongtai != null && manhvobt != null) {
                        int level = 0;
                        for (ItemOption io : bongtai.itemOptions) {
                            if (io.optionTemplate.id == 72) {
                                level = io.param;
                                break;
                            }
                        }
                        if (level < 4) {
                            int lvbt = lvbt(bongtai);
                            int countmvbt = getcountmvbtnangbt(lvbt);
                            player.combineNew.goldCombine = getGoldnangbt(lvbt);
                            player.combineNew.gemCombine = getgemdnangbt(lvbt);
                            player.combineNew.ratioCombine = getRationangbt(lvbt);

                            String npcSay = "Bông tai Porata Cấp: " + lvbt + " \n|2|";
                            for (ItemOption io : bongtai.itemOptions) {
                                npcSay += io.getOptionString() + "\n";
                            }
                            npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.ratioCombine + "%" + "\n";
                            if (manhvobt.quantity >= countmvbt) {
                                if (player.combineNew.goldCombine <= player.inventory.gold) {
                                    if (player.combineNew.gemCombine <= player.inventory.gem) {
                                        npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.goldCombine)
                                                + " vàng";
                                        baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                                "Nâng cấp\ncần " + player.combineNew.gemCombine + " ngọc");
                                    } else {
                                        npcSay += "Còn thiếu " + Util.numberToMoney(
                                                player.combineNew.gemCombine - player.inventory.gem) + " ngọc";
                                        baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                                    }
                                } else {
                                    npcSay += "Còn thiếu "
                                            + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold)
                                            + " vàng";
                                    baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                                }
                            } else {
                                npcSay += "Còn thiếu " + Util.numberToMoney(countmvbt - manhvobt.quantity)
                                        + " Mảnh vỡ bông tai";
                                baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Đã đạt cấp tối đa!)))", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Bông tai Porata cấp 1 hoặc cấp 2 và Mảnh vỡ bông tai", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 Bông tai Porata cấp 1 hoặc cấp 2 và Mảnh vỡ bông tai", "Đóng");
                }
                break;
            case MO_CHI_SO_BONG_TAI:
                if (player.combineNew.itemsCombine.size() == 3) {
                    Item bongTai = null;
                    Item manhHon = null;
                    Item daXanhLam = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.template.id == 921 || item.template.id == 1155 || item.template.id == 1156) {
                            bongTai = item;
                        } else if (item.template.id == 934) {
                            manhHon = item;
                        } else if (item.template.id == 935) {
                            daXanhLam = item;
                        }
                    }
                    if (bongTai != null && manhHon != null && daXanhLam != null && manhHon.quantity >= 99
                            && daXanhLam.quantity >= 1) {

                        player.combineNew.goldCombine = GOLD_MOCS_BONG_TAI;
                        player.combineNew.gemCombine = Gem_MOCS_BONG_TAI;
                        player.combineNew.ratioCombine = RATIO_NANG_CAP;

                        String npcSay = "Bông tai Porata cấp "
                                + (bongTai.template.id == 921
                                        ? bongTai.template.id == 1155 ? bongTai.template.id == 1156 ? "2" : "3" : "4"
                                        : "1")
                                + " \n|2|";
                        for (ItemOption io : bongTai.itemOptions) {
                            npcSay += io.getOptionString() + "\n";
                        }
                        npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.ratioCombine + "%" + "\n";
                        if (player.combineNew.goldCombine <= player.inventory.gold) {
                            if (player.combineNew.gemCombine <= player.inventory.gem) {
                                npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.goldCombine) + " vàng";
                                baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                        "Nâng cấp\ncần " + player.combineNew.gemCombine + " ngọc");
                            } else {
                                npcSay += "Còn thiếu "
                                        + Util.numberToMoney(player.combineNew.gemCombine - player.inventory.gem)
                                        + " ngọc";
                                baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                            }
                        } else {
                            npcSay += "Còn thiếu "
                                    + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold)
                                    + " vàng";
                            baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Bông tai Porata cấp 2 hoặc 3, X99 Mảnh hồn bông tai và x1 Đá xanh lam", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 Bông tai Porata cấp 2 hoặc 3, X99 Mảnh hồn bông tai và x1 Đá xanh lam", "Đóng");
                }

                break;
            case MO_CHI_SO_Chien_Linh:
                if (player.combineNew.itemsCombine.size() == 3) {
                    Item ChienLinh = null;
                    Item damathuat = null;
                    Item honthu = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.template.id >= 1149 && item.template.id <= 1151) {
                            ChienLinh = item;
                        } else if (item.template.id == 2030) {
                            damathuat = item;
                        } else if (item.template.id == 2029) {
                            honthu = item;
                        }
                    }
                    if (ChienLinh != null && damathuat != null && damathuat.quantity >= 99 && honthu.quantity >= 99) {

                        player.combineNew.goldCombine = GOLD_MOCS_Chien_Linh;
                        player.combineNew.rubyCombine = RUBY_MOCS_Chien_Linh;
                        player.combineNew.ratioCombine = RATIO_NANG_CAP;

                        String npcSay = "Chiến Linh " + "\n|2|";
                        for (Item.ItemOption io : ChienLinh.itemOptions) {
                            npcSay += io.getOptionString() + "\n";
                        }
                        npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.ratioCombine + "%" + "\n";
                        if (player.combineNew.goldCombine <= player.inventory.gold) {
                            if (player.combineNew.rubyCombine <= player.inventory.ruby) {
                                npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.goldCombine) + " vàng";
                                trunglinhthu.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                        "Nâng cấp\ncần " + player.combineNew.rubyCombine + " hồng ngọc");
                            } else {
                                npcSay += "Còn thiếu "
                                        + Util.numberToMoney(player.combineNew.rubyCombine - player.inventory.ruby)
                                        + " hồng ngọc";
                                trunglinhthu.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                            }
                        } else {
                            npcSay += "Còn thiếu "
                                    + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold)
                                    + " vàng";
                            trunglinhthu.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                        }
                    } else {
                        this.trunglinhthu.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Chiến Linh, X99 Đá ma thuật và X99 Hồn linh thú", "Đóng");
                    }
                } else {
                    this.trunglinhthu.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 Chiến Linh, X99 Đá ma thuật và X99 Hồn linh thú", "Đóng");
                }

                break;

            case EP_SAO_TRANG_BI:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item trangBi = null;
                    Item daPhaLe = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (isTrangBiPhaLeHoa(item)) {
                            trangBi = item;
                        } else if (isDaPhaLe(item)) {
                            daPhaLe = item;
                        }
                    }
                    int star = 0; // sao pha lê đã ép
                    int starEmpty = 0; // lỗ sao pha lê
                    if (trangBi != null && daPhaLe != null) {
                        for (Item.ItemOption io : trangBi.itemOptions) {
                            if (io.optionTemplate.id == 102) {
                                star = io.param;
                            } else if (io.optionTemplate.id == 107) {
                                starEmpty = io.param;
                            }
                        }
                        if (star < starEmpty) {
                            player.combineNew.rubyCombine = getGemEpSao(star);
                            String npcSay = trangBi.template.name + "\n|2|";
                            for (Item.ItemOption io : trangBi.itemOptions) {
                                if (io.optionTemplate.id != 102) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                            }
                            if (daPhaLe.template.type == 30) {
                                for (Item.ItemOption io : daPhaLe.itemOptions) {
                                    npcSay += "|7|" + io.getOptionString() + "\n";
                                }
                            } else {
                                npcSay += "|7|" + ItemService.gI().getItemOptionTemplate(getOptionDaPhaLe(daPhaLe)).name
                                        .replaceAll("#", getParamDaPhaLe(daPhaLe) + "") + "\n";
                            }
                            // npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.rubyCombine) + "
                            // ngọc hồng";
                            baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                    "Nâng Cấp");

                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Cần 1 Trang Bị Có Lỗ Sao Pha Lê Và 1 Loại Đá Pha Lê Để Ép Vào", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Trang Bị Có Lỗ Sao Pha Lê Và 1 Loại Đá Pha Lê Để Ép Vào", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 Trang Bị Có Lỗ Sao Pha Lê Và 1 Loại Đá Pha Lê Để Ép Vào", "Đóng");
                }
                break;
            case REN_KIEM_Z:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item manhKiemZ = null;
                    Item quangKiemZ = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.template.id >= 555 && item.template.id <= 567) {
                            manhKiemZ = item;
                        } else if (item.template.id == 1995) {
                            quangKiemZ = item;
                        }
                    }
                    if (manhKiemZ != null && quangKiemZ != null && quangKiemZ.quantity >= 1) {
                        player.combineNew.goldCombine = GOLD_KIEM_Z;
                        player.combineNew.gemCombine = GEM_KIEM_Z;
                        player.combineNew.ratioCombine = RATIO_KIEM_Z2;
                        String npcSay = "Kiếm Z cấp 1" + "\n|2|";
                        for (Item.ItemOption io : manhKiemZ.itemOptions) {
                            npcSay += io.getOptionString() + "\n";
                        }
                        npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.ratioCombine + "%" + "\n";
                        if (player.combineNew.goldCombine <= player.inventory.gold) {
                            npcSay += "|1|Rèn Kiếm Z " + Util.numberToMoney(player.combineNew.goldCombine) + " vàng";
                            baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                    "Rèn Kiếm Z\ncần " + player.combineNew.gemCombine + " Ngọc xanh");
                        } else {
                            npcSay += "Còn thiếu "
                                    + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold)
                                    + " vàng";
                            baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                        }
                    } else if (manhKiemZ == null || quangKiemZ == null) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Kiếm Z và X99 Quặng Kiếm Z", "Đóng");
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Số lượng quặng Kiếm Z không đủ", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 Kiếm Z và X99 Quặng Kiếm Z", "Đóng");
                }
                break;

            case PHA_LE_HOA_TRANG_BI:
                if (player.combineNew.itemsCombine.size() == 1) {
                    Item item = player.combineNew.itemsCombine.get(0);
                    if (isTrangBiPhaLeHoa(item)) {
                        int star = 0;
                        for (Item.ItemOption io : item.itemOptions) {
                            if (io.optionTemplate.id == 107) {
                                star = io.param;
                                break;
                            }
                        }
                        if (star < MAX_STAR_ITEM) {
                            player.combineNew.goldCombine = getGoldPhaLeHoa(star);
                            player.combineNew.gemCombine = getGemPhaLeHoa(star);
                            player.combineNew.ratioCombine = getRatioPhaLeHoa(star);

                            String npcSay = item.template.name + "\n|2|";
                            for (Item.ItemOption io : item.itemOptions) {
                                if (io.optionTemplate.id != 102) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                            }
                            int gem = player.combineNew.gemCombine;
                            npcSay += "|7|Tỉ Lệ Thành Công: " + player.combineNew.ratioCombine + "%" + "\n";
                            if (player.combineNew.goldCombine <= player.inventory.gold) {
                                npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.goldCombine) + " Vàng";
                                baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                        "Nâng Cấp\nx1", "Nâng Cấp\nx10", "Nâng Cấp\nx100");
                            } else {
                                npcSay += "Còn Thiếu "
                                        + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold)
                                        + " Vàng";
                                baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Vật Phẩm Đã Đạt Tối Đa Sao Pha Lê", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vật Phẩm Này Không Thể Đục Lỗ",
                                "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy Hãy Chọn 1 Vật Phẩm Để Pha Lê Hóa",
                            "Đóng");
                }
                break;

            case CHE_TAO_TRANG_BI_TS:
                if (player.combineNew.itemsCombine.size() == 0) {
                    return;
                }
                if (player.combineNew.itemsCombine.size() >= 2 && player.combineNew.itemsCombine.size() < 5) {
                    if (player.combineNew.itemsCombine.stream()
                            .filter(item -> item.isNotNullItem() && item.isCongThucVip()).count() < 1) {
                        this.whis.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu Công thức Vip", "Đóng");
                        return;
                    }
                    if (player.combineNew.itemsCombine.stream()
                            .filter(item -> item.isNotNullItem() && item.isManhTS() && item.quantity >= 999)
                            .count() < 1) {
                        this.whis.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu Mảnh đồ thiên sứ", "Đóng");
                        return;
                    }
                    Item mTS = null, daNC = null, daMM = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.isNotNullItem()) {
                            if (item.isManhTS()) {
                                mTS = item;
                            } else if (item.isDaNangCap()) {
                                daNC = item;
                            } else if (item.isDaMayMan()) {
                                daMM = item;
                            }
                        }
                    }
                    int tilemacdinh = 35;
                    int tilenew = tilemacdinh;

                    String npcSay = "|1|Chế tạo "
                            + player.combineNew.itemsCombine.stream().filter(Item::isManhTS).findFirst().get()
                                    .typeNameManh()
                            + " Thiên sứ "
                            + player.combineNew.itemsCombine.stream().filter(Item::isCongThucVip).findFirst().get()
                                    .typeHanhTinh()
                            + "\n"
                            + "|1|Mạnh hơn trang bị Hủy Diệt từ 20% đến 35% \n"
                            + "|2|Mảnh ghép " + mTS.quantity + "/999(Thất bại -99 mảnh ghép)";
                    if (daNC != null) {
                        npcSay += "|2|Đá nâng cấp "
                                + player.combineNew.itemsCombine.stream().filter(Item::isDaNangCap).findFirst().get()
                                        .typeDanangcap()
                                + " (+" + (daNC.template.id - 1073) + "0% tỉ lệ thành công)\n";
                    }
                    if (daMM != null) {
                        npcSay += "|2|Đá may mắn "
                                + player.combineNew.itemsCombine.stream().filter(Item::isDaMayMan).findFirst().get()
                                        .typeDaMayman()
                                + " (+" + (daMM.template.id - 1078) + "0% tỉ lệ tối đa các chỉ số)\n";
                    }
                    if (daNC != null) {
                        tilenew += (daNC.template.id - 1073) * 10;
                        npcSay += "|2|Tỉ lệ thành công: " + tilenew + "%\n";
                    } else {
                        npcSay += "|2|Tỉ lệ thành công: " + tilemacdinh + "%\n";
                    }
                    npcSay += "|2|Phí nâng cấp: 2 tỉ vàng";
                    if (player.inventory.gold < 2000000000) {
                        this.whis.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bạn không đủ vàng", "Đóng");
                        return;
                    }
                    this.whis.createOtherMenu(player, ConstNpc.MENU_DAP_DO,
                            npcSay, "Đồng ý", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 4) {
                        this.whis.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Nguyên liệu không phù hợp", "Đóng");
                        return;
                    }
                    this.whis.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Không đủ nguyên liệu", "Đóng");
                }
                break;
            case NHAP_NGOC_RONG:
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                    if (player.combineNew.itemsCombine.size() == 1) {
                        Item item = player.combineNew.itemsCombine.get(0);
                        if (item != null && item.isNotNullItem() && (item.template.id > 14 && item.template.id <= 20)
                                && item.quantity >= 7) {
                            String npcSay = "|2|Con Có Muốn Biến 7 " + item.template.name + " Thành\n"
                                    + "1 Viên " + ItemService.gI().getTemplate((short) (item.template.id - 1)).name
                                    + "\n"
                                    + "|7|Cần 7 " + item.template.name;
                            this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Làm Phép",
                                    "Từ Chối");
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Cần 7 Viên Ngọc Rồng 2 Sao Trở Lên", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 7 Viên Ngọc Rồng 2 Sao Trở Lên", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hành Trang Cần Ít Nhất 1 Chỗ Trống",
                            "Đóng");
                }
                break;
            case NANG_CAP_VAT_PHAM:
                if (player.combineNew.itemsCombine.size() >= 2 && player.combineNew.itemsCombine.size() < 4) {
                    if (player.combineNew.itemsCombine.stream()
                            .filter(item -> item.isNotNullItem() && item.template.type < 5).count() < 1) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Sai Trang Bị Cần Nâng Cấp",
                                "Đóng");
                        break;
                    }
                    if (player.combineNew.itemsCombine.stream()
                            .filter(item -> item.isNotNullItem() && item.template.type == 14).count() < 1) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Thiếu Đá Nâng Cấp, Ruby, Saphia, Titan,...", "Đóng");
                        break;
                    }
                    if (player.combineNew.itemsCombine.size() == 3 && player.combineNew.itemsCombine.stream()
                            .filter(item -> item.isNotNullItem() && item.template.id == 987).count() < 1) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu Đá Bảo Vệ", "Đóng");
                        break;
                    }
                    Item itemDo = null;
                    Item itemDNC = null;
                    Item itemDBV = null;
                    for (int j = 0; j < player.combineNew.itemsCombine.size(); j++) {
                        if (player.combineNew.itemsCombine.get(j).isNotNullItem()) {
                            if (player.combineNew.itemsCombine.size() == 3
                                    && player.combineNew.itemsCombine.get(j).template.id == 987) {
                                itemDBV = player.combineNew.itemsCombine.get(j);
                                continue;
                            }
                            if (player.combineNew.itemsCombine.get(j).template.type < 5) {
                                itemDo = player.combineNew.itemsCombine.get(j);
                            } else {
                                itemDNC = player.combineNew.itemsCombine.get(j);
                            }
                        }
                    }
                    if (isCoupleItemNangCapCheck(itemDo, itemDNC)) {
                        int level = 0;
                        for (Item.ItemOption io : itemDo.itemOptions) {
                            if (io.optionTemplate.id == 72) {
                                level = io.param;
                                break;
                            }
                        }
                        if (level < MAX_LEVEL_ITEM) {
                            player.combineNew.goldCombine = getGoldNangCapDo(level);
                            player.combineNew.ratioCombine = (float) getTileNangCapDo(level);
                            player.combineNew.countDaNangCap = getCountDaNangCapDo(level);
                            player.combineNew.countDaBaoVe = (short) getCountDaBaoVe(level);
                            String npcSay = "|2|Hiện Tại " + itemDo.template.name + " (+" + level + ")\n|0|";
                            for (Item.ItemOption io : itemDo.itemOptions) {
                                if (io.optionTemplate.id != 72) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                            }
                            String option = null;
                            int param = 0;
                            for (Item.ItemOption io : itemDo.itemOptions) {
                                if (io.optionTemplate.id == 47
                                        || io.optionTemplate.id == 6
                                        || io.optionTemplate.id == 0
                                        || io.optionTemplate.id == 7
                                        || io.optionTemplate.id == 14
                                        || io.optionTemplate.id == 22
                                        || io.optionTemplate.id == 23) {
                                    option = io.optionTemplate.name;
                                    param = io.param + (io.param * 10 / 100);
                                    break;
                                }
                            }
                            npcSay += "|2|Sau Khi Nâng Cấp (+" + (level + 1) + ")\n|7|"
                                    + option.replaceAll("#", String.valueOf(param))
                                    + "\n|7|Tỉ Lệ Thành Công: " + player.combineNew.ratioCombine + "%\n"
                                    + (player.combineNew.countDaNangCap > itemDNC.quantity ? "|7|" : "|1|")
                                    + "Cần " + player.combineNew.countDaNangCap + " " + itemDNC.template.name
                                    + "\n" + (player.combineNew.goldCombine > player.inventory.gold ? "|7|" : "|1|")
                                    + "Cần " + Util.numberToMoney(player.combineNew.goldCombine) + " Vàng";

                            String daNPC = player.combineNew.itemsCombine.size() == 3 && itemDBV != null
                                    ? String.format("\nCần Tốn %s Đá Bảo Vệ", player.combineNew.countDaBaoVe)
                                    : "";
                            if ((level == 2 || level == 4 || level == 6)
                                    && !(player.combineNew.itemsCombine.size() == 3 && itemDBV != null)) {
                                npcSay += "\nNếu Thất Bại Sẽ Rớt Xuống (+" + (level - 1) + ")";
                            }
                            if (player.combineNew.countDaNangCap > itemDNC.quantity) {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        npcSay, "Còn Thiếu\n" + (player.combineNew.countDaNangCap - itemDNC.quantity)
                                        + " " + itemDNC.template.name);
                            } else if (player.combineNew.goldCombine > player.inventory.gold) {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        npcSay,
                                        "Còn Thiếu\n"
                                        + Util.numberToMoney(
                                                (player.combineNew.goldCombine - player.inventory.gold))
                                        + " Vàng");
                            } else if (player.combineNew.itemsCombine.size() == 3 && Objects.nonNull(itemDBV)
                                    && itemDBV.quantity < player.combineNew.countDaBaoVe) {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        npcSay, "Còn Thiếu\n" + (player.combineNew.countDaBaoVe - itemDBV.quantity)
                                        + " Đá Bảo Vệ");
                            } else {
                                this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                                        npcSay, "Nâng Cấp\n" + Util.numberToMoney(player.combineNew.goldCombine)
                                        + " Vàng" + daNPC,
                                        "Từ Chối");
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Trang Bị Của Ngươi Đã Đạt Cấp Tối Đa", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Hãy Chọn 1 Trang Bị Và 1 Loại Đá Nâng Cấp", "Đóng");
                    }
                } else {
                    if (player.combineNew.itemsCombine.size() > 3) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cất Đi Con Ta Không Thèm", "Đóng");
                        break;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Hãy Chọn 1 Trang Bị Và 1 Loại Đá Nâng Cấp", "Đóng");
                }
                break;
            case PHAN_RA_DO_THAN_LINH:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Con Hãy đưa ta đồ thần linh để phân rã", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 1) {
                    List<Integer> itemdov2 = new ArrayList<>(Arrays.asList(562, 564, 566));
                    int couponAdd = 0;
                    Item item = player.combineNew.itemsCombine.get(0);
                    if (item.isNotNullItem()) {
                        if (item.template.id >= 555 && item.template.id <= 567) {
                            couponAdd = itemdov2.stream().anyMatch(t -> t == item.template.id) ? 2
                                    : item.template.id == 561 ? 3 : 1;
                        }
                    }
                    if (couponAdd == 0) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Ta chỉ có thể phân rã đồ thần linh thôi", "Đóng");
                        return;
                    }
                    String npcSay = "|2|Sau khi phân rã vật phẩm\n|7|"
                            + "Bạn sẽ nhận được : " + couponAdd + " Đá Ngũ Sắc\n"
                            + (500000000 > player.inventory.gold ? "|7|" : "|1|")
                            + "Cần " + Util.numberToMoney(50000000) + " vàng";

                    if (player.inventory.gold < 50000000) {
                        this.baHatMit.npcChat(player, "Con không đủ 50TR vàng");
                        return;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.MENU_PHAN_RA_DO_THAN_LINH,
                            npcSay, "Phân Rã\n" + Util.numberToMoney(50000000) + " vàng", "Từ chối");
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Ta chỉ có thể phân rã 1 lần 1 món đồ thần linh", "Đóng");
                }
                break;
            case NANG_CAP_DO_TS:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Hãy đưa ta 2 món Hủy Diệt bất kì và 1 món Thần Linh cùng loại", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 4) {
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDTL())
                            .count() < 1) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ thần linh", "Đóng");
                        return;
                    }
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDHD())
                            .count() < 2) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ hủy diệt", "Đóng");
                        return;
                    }
                    if (player.combineNew.itemsCombine.stream()
                            .filter(item -> item.isNotNullItem() && item.isManhTS() && item.quantity >= 5)
                            .count() < 1) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu mảnh thiên sứ", "Đóng");
                        return;
                    }

                    String npcSay = "|2|Con có muốn đổi các món nguyên liệu ?\n|7|"
                            + "Và nhận được "
                            + player.combineNew.itemsCombine.stream().filter(Item::isManhTS).findFirst().get()
                                    .typeNameManh()
                            + " thiên sứ tương ứng\n"
                            + "|1|Cần " + Util.numberToMoney(COST) + " vàng";

                    if (player.inventory.gold < COST) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hết tiền rồi\nẢo ít thôi con",
                                "Đóng");
                        return;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.MENU_NANG_CAP_DO_TS,
                            npcSay, "Nâng cấp\n" + Util.numberToMoney(COST) + " vàng", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 3) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cất đi con ta không thèm", "Đóng");
                        return;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Còn thiếu nguyên liệu để nâng cấp hãy quay lại sau", "Đóng");
                }
                break;
            case NANG_CAP_SKH_VIP:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Hãy đưa ta 1 món Hủy Diệt và 2 món Thần Linh ngẫu nhiên", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 3) {
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDHD())
                            .count() < 1) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ Hủy Diệt", "Đóng");
                        return;
                    }
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDTL())
                            .count() < 2) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ Thần Linh ", "Đóng");
                        return;
                    }

                    String npcSay = "|2|Con có muốn đổi các món nguyên liệu ?\n|7|"
                            + "Và nhận được "
                            + player.combineNew.itemsCombine.stream().filter(Item::isDHD).findFirst().get().typeName()
                            + " kích hoạt VIP tương ứng\n"
                            + "|1|Cần " + Util.numberToMoney(COST) + " vàng";

                    if (player.inventory.gold < COST) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hết tiền rồi\nẢo ít thôi con",
                                "Đóng");
                        return;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.MENU_NANG_DOI_SKH_VIP,
                            npcSay, "Nâng cấp\n" + Util.numberToMoney(COST) + " vàng", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 3) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Nguyên liệu không phù hợp",
                                "Đóng");
                        return;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Còn thiếu nguyên liệu để nâng cấp hãy quay lại sau", "Đóng");
                }
                break;

            // Phước Chế Tạo------------------------------------------------------------
            case CHE_TAO_PHUOC:
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {

                    List<Item> itemsCombine = player.combineNew.itemsCombine;

                    if (itemsCombine.size() == 1) {

                        Item item1 = itemsCombine.get(0);

                        boolean isValid = item1 != null && item1.isNotNullItem();

                        if (isValid && item1.template.id == 1736 && item1.quantity >= 10) {
                            String npcSay = "|2|Ngươi Chắc Chắn Muốn Chế Tạo\nTa Không Trả Lại Đâu Đấy!";
                            this.dodo.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Chế Tạo",
                                    "Từ Chối");
                        } else if (isValid && item1.template.id == 1737 && item1.quantity >= 10) {
                            String npcSay = "|2|Ngươi Chắc Chắn Muốn Chế Tạo\nTa Không Trả Lại Đâu Đấy!";
                            this.dodo.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Chế Tạo",
                                    "Từ Chối");
                        } else {

                            this.dodo.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ngươi Chưa Đủ Nguyên Liệu Ta Cần.",
                                    "Đóng");
                        }
                    } else {

                        this.dodo.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Không Đủ Nguyên Liệu.", "Đóng");
                    }
                } else {

                    this.dodo.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hành Trang Cần Ít Nhất 1 Chỗ Trống",
                            "Đóng");
                }
                break;
            case COMBINE_TAN_DAN_FRAGMENT:
                Dragon.services.tutien.TutienCombineService.gI().showInfoCombineTanDanFragment(player,
                        player.iDMark.getNpcChose());
                break;
            case UPGRADE_TUTIEN_DAN:
                Dragon.services.tutien.TutienCombineService.gI().showInfoUpgradeTutienDan(player,
                        player.iDMark.getNpcChose());
                break;

        }
    }

    private void chetaophuoc(Player player) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {

            List<Item> itemsCombine = player.combineNew.itemsCombine;

            if (itemsCombine.size() == 1) {

                Item item1 = itemsCombine.get(0);

                boolean isValid = item1 != null && item1.isNotNullItem();

                if (isValid && item1.template.id == 1736 && item1.quantity >= 5) {
                    if (player.inventory.gold < 50000) {
                        Service.getInstance().sendThongBao(player, "Cần 10K Vàng");
                        return;
                    }
                    Item kiem1 = ItemService.gI().createNewItem((short) 1732);
                    kiem1.itemOptions.add(
                            new Item.ItemOption(77, Util.isTrue(10, 100) ? Util.nextInt(5, 15) : Util.nextInt(5, 10)));
                    kiem1.itemOptions.add(
                            new Item.ItemOption(103, Util.isTrue(10, 100) ? Util.nextInt(5, 15) : Util.nextInt(5, 10)));
                    kiem1.itemOptions.add(
                            new Item.ItemOption(50, Util.isTrue(10, 100) ? Util.nextInt(5, 15) : Util.nextInt(5, 10)));
                    kiem1.itemOptions.add(
                            new Item.ItemOption(72, Util.isTrue(10, 100) ? Util.nextInt(1, 5) : Util.nextInt(5, 7)));
                    sendEffectSuccessCombine(player);
                    InventoryServiceNew.gI().addItemBag(player, kiem1);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, item1, 5);
                    InventoryServiceNew.gI().sendItemBags(player);
                    player.inventory.gold -= 10000;
                    Service.gI().sendMoney(player);
                    reOpenItemCombine(player);

                }
            } else if (itemsCombine.size() == 1) {

                Item item1 = itemsCombine.get(0);

                boolean isValid = item1 != null && item1.isNotNullItem();

                if (isValid && item1.template.id == 1737 && item1.quantity >= 5) {
                    if (player.inventory.gold < 50000) {
                        Service.getInstance().sendThongBao(player, "Cần 20K Vàng");
                        return;
                    }
                    Item kiem1 = ItemService.gI().createNewItem((short) 1733);
                    kiem1.itemOptions.add(
                            new Item.ItemOption(77, Util.isTrue(10, 100) ? Util.nextInt(5, 15) : Util.nextInt(5, 10)));
                    kiem1.itemOptions.add(
                            new Item.ItemOption(103, Util.isTrue(10, 100) ? Util.nextInt(5, 15) : Util.nextInt(5, 10)));
                    kiem1.itemOptions.add(
                            new Item.ItemOption(50, Util.isTrue(10, 100) ? Util.nextInt(5, 15) : Util.nextInt(5, 10)));
                    kiem1.itemOptions.add(
                            new Item.ItemOption(72, Util.isTrue(10, 100) ? Util.nextInt(1, 5) : Util.nextInt(5, 7)));
                    sendEffectSuccessCombine(player);
                    InventoryServiceNew.gI().addItemBag(player, kiem1);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, item1, 5);
                    InventoryServiceNew.gI().sendItemBags(player);
                    player.inventory.gold -= 20000;
                    Service.gI().sendMoney(player);
                    reOpenItemCombine(player);

                }
            }
        }
    }

    // Phước Chế
    // Tạo---------------------------------------------------------------------------------------
    /**
     * Bắt đầu đập đồ - điều hướng từng loại đập đồ
     *
     * @param player
     */
    public void startCombine(Player player, int type) {
        switch (player.combineNew.typeCombine) {
            case EP_SAO_TRANG_BI:
                epSaoTrangBi(player);
                break;
            case PHA_LE_HOA_TRANG_BI:
                phaLeHoaTrangBi(player, type);
                break;
            case CHUYEN_HOA_TRANG_BI:
                break;
            case NHAP_NGOC_RONG:
                nhapNgocRong(player);
                break;
            case CHE_TAO_PHUOC:
                chetaophuoc(player);
                break;
            case REN_KIEM_Z:
                renKiemZ(player);
                break;
            case PHAN_RA_DO_THAN_LINH:
                phanradothanlinh(player);
                break;
            case NANG_CAP_DO_TS:
                openDTS(player);
                break;
            case NANG_CAP_DO_KICH_HOAT:
                dapDoKichHoat(player);
                break;
            case NANG_CAP_DO_KICH_HOAT_THUONG:
                dapDoKichHoatthuong(player);
                break;
            case NANG_CAP_SKH_VIP:
                openSKHVIP(player);
                break;
            case NANG_CAP_VAT_PHAM:
                nangCapVatPham(player);
                break;
            case NANG_CAP_BONG_TAI:
                nangCapBongTai(player);
                break;
            case MO_CHI_SO_BONG_TAI:
                moChiSoBongTai(player);
                break;
            case NANG_CAP_KHI:
                nangCapKhi(player);
                break;
            case NANG_CAP_MEO:
                nangCapMeo(player);
                break;
            case NANG_CAP_LUFFY:
                nangCapLuffy(player);
                break;
            case MO_CHI_SO_Chien_Linh:
                moChiSoLinhThu(player);
                break;
            case Nang_Chien_Linh:
                nangCapChienLinh(player);
                break;
            case CHE_TAO_TRANG_BI_TS:
                openCreateItemAngel(player);
                break;
            case DOI_DIEM:
                doidiem(player);
                break;
            // Sách Tuyệt Kỹ
            case GIAM_DINH_SACH:
                giamDinhSach(player);
                break;
            case TAY_SACH:
                taySach(player);
                break;
            case NANG_CAP_SACH_TUYET_KY:
                nangCapSachTuyetKy(player);
                break;
            case PHUC_HOI_SACH:
                phucHoiSach(player);
                break;
            case PHAN_RA_SACH:
                phanRaSach(player);
                break;
            case COMBINE_TAN_DAN_FRAGMENT:
                Dragon.services.tutien.TutienCombineService.gI().combineTanDanFragment(player);
                break;
            case UPGRADE_TUTIEN_DAN:
                Dragon.services.tutien.TutienCombineService.gI().upgradeTutienDan(player);
                break;
        }
        player.iDMark.setIndexMenu(ConstNpc.IGNORE_MENU);
        player.combineNew.clearParamCombine();
        player.combineNew.lastTimeCombine = System.currentTimeMillis();
    }

    private void doidiem(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            player.inventory.gold -= 0;
            List<Integer> itemdov2 = new ArrayList<>(Arrays.asList(663, 664, 665, 666, 667));
            Item item = player.combineNew.itemsCombine.get(0);
            sendEffectSuccessCombine(player);
            if (item.quantity < 59) {
                Service.gI().sendThongBaoOK(player, "Đéo Đủ Thức Ăn");
            } else if (item.quantity >= 59) {
                InventoryServiceNew.gI().sendItemBags(player);
                player.inventory.coupon += 1;
                Service.gI().sendThongBaoOK(player, "Bú 1 Điểm");
                InventoryServiceNew.gI().subQuantityItemsBag(player, item, 59);
                player.combineNew.itemsCombine.clear();
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
    }

    private void giamDinhSach(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {

            Item sachTuyetKy = null;
            Item buaGiamDinh = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (issachTuyetKy(item)) {
                    sachTuyetKy = item;
                } else if (item.template.id == 1391) {
                    buaGiamDinh = item;
                }
            }
            if (sachTuyetKy != null && buaGiamDinh != null) {
                Item sachTuyetKy_2 = ItemService.gI().createNewItem((short) sachTuyetKy.template.id);
                if (checkHaveOption(sachTuyetKy, 0, 221)) {
                    int tyle = new Random().nextInt(10);
                    if (tyle >= 0 && tyle <= 33) {
                        sachTuyetKy_2.itemOptions.add(new ItemOption(50, new Util().nextInt(5, 10)));
                    } else if (tyle > 33 && tyle <= 66) {
                        sachTuyetKy_2.itemOptions.add(new ItemOption(77, new Util().nextInt(10, 15)));
                    } else {
                        sachTuyetKy_2.itemOptions.add(new ItemOption(103, new Util().nextInt(10, 15)));
                    }
                    for (int i = 1; i < sachTuyetKy.itemOptions.size(); i++) {
                        sachTuyetKy_2.itemOptions.add(new ItemOption(sachTuyetKy.itemOptions.get(i).optionTemplate.id,
                                sachTuyetKy.itemOptions.get(i).param));
                    }
                    sendEffectSuccessCombine(player);
                    InventoryServiceNew.gI().addItemBag(player, sachTuyetKy_2);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, sachTuyetKy, 1);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, buaGiamDinh, 1);
                    InventoryServiceNew.gI().sendItemBags(player);
                    reOpenItemCombine(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Còn cái nịt mà giám");
                    return;
                }
            }
        }
    }

    private void nangCapSachTuyetKy(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {

            Item sachTuyetKy = null;
            Item kimBamGiay = null;

            for (Item item : player.combineNew.itemsCombine) {
                if (issachTuyetKy(item)) {
                    sachTuyetKy = item;
                } else if (item.template.id == 1390) {
                    kimBamGiay = item;
                }
            }
            Item sachTuyetKy_2 = ItemService.gI().createNewItem((short) ((short) sachTuyetKy.template.id + 1));
            if (sachTuyetKy != null && kimBamGiay != null) {
                if (kimBamGiay.quantity < 10) {
                    Service.getInstance().sendThongBao(player, "Không đủ Kìm bấm giấy mà đòi nâng cấp");
                    return;
                }
                if (checkHaveOption(sachTuyetKy, 0, 221)) {
                    Service.getInstance().sendThongBao(player, "Chưa giám định mà đòi nâng cấp");
                    return;
                }
                for (int i = 0; i < sachTuyetKy.itemOptions.size(); i++) {
                    sachTuyetKy_2.itemOptions.add(new ItemOption(sachTuyetKy.itemOptions.get(i).optionTemplate.id,
                            sachTuyetKy.itemOptions.get(i).param));
                }
                sendEffectSuccessCombine(player);
                InventoryServiceNew.gI().addItemBag(player, sachTuyetKy_2);
                InventoryServiceNew.gI().subQuantityItemsBag(player, sachTuyetKy, 1);
                InventoryServiceNew.gI().subQuantityItemsBag(player, kimBamGiay, 10);
                InventoryServiceNew.gI().sendItemBags(player);
                reOpenItemCombine(player);

            }
        }
    }

    private void phucHoiSach(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            Item cuonSachCu = InventoryServiceNew.gI().findItemBag(player, (short) 1392);
            int goldPhanra = 10_000_000;
            Item sachTuyetKy = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (issachTuyetKy(item)) {
                    sachTuyetKy = item;
                }
            }
            if (sachTuyetKy != null) {
                int doBen = 0;
                ItemOption optionLevel = null;
                for (ItemOption io : sachTuyetKy.itemOptions) {
                    if (io.optionTemplate.id == 219) {
                        doBen = io.param;
                        optionLevel = io;
                        break;
                    }
                }
                if (cuonSachCu == null) {
                    Service.getInstance().sendThongBaoOK(player, "Cần sách tuyệt kỹ và 10 cuốn sách cũ");
                    return;
                }
                if (cuonSachCu.quantity < 10) {
                    Service.getInstance().sendThongBaoOK(player, "Cần sách tuyệt kỹ và 10 cuốn sách cũ");
                    return;
                }
                if (player.inventory.gold < goldPhanra) {
                    Service.getInstance().sendThongBao(player, "Không có tiền mà đòi phục hồi à");
                    return;
                }
                if (doBen != 1000) {
                    for (int i = 0; i < sachTuyetKy.itemOptions.size(); i++) {
                        if (sachTuyetKy.itemOptions.get(i).optionTemplate.id == 219) {
                            sachTuyetKy.itemOptions.get(i).param = 1000;
                            break;
                        }
                    }
                    player.inventory.gold -= 10_000_000;
                    InventoryServiceNew.gI().subQuantityItemsBag(player, cuonSachCu, 10);
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    sendEffectSuccessCombine(player);
                    reOpenItemCombine(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Còn dùng được phục hồi ăn cứt à");
                    return;
                }
            }
        }
    }

    private void phanRaSach(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            Item cuonSachCu = ItemService.gI().createNewItem((short) 1392, 5);
            int goldPhanra = 10_000_000;
            Item sachTuyetKy = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (issachTuyetKy(item)) {
                    sachTuyetKy = item;
                }
            }
            if (sachTuyetKy != null) {
                int luotTay = 0;
                ItemOption optionLevel = null;
                for (ItemOption io : sachTuyetKy.itemOptions) {
                    if (io.optionTemplate.id == 218) {
                        luotTay = io.param;
                        optionLevel = io;
                        break;
                    }
                }
                if (player.inventory.gold < goldPhanra) {
                    Service.getInstance().sendThongBao(player, "Không có tiền mà đòi phân rã à");
                    return;
                }
                if (luotTay == 0) {

                    player.inventory.gold -= goldPhanra;
                    InventoryServiceNew.gI().subQuantityItemsBag(player, sachTuyetKy, 1);
                    InventoryServiceNew.gI().addItemBag(player, cuonSachCu);
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    sendEffectSuccessCombine(player);
                    reOpenItemCombine(player);

                } else {
                    Service.getInstance().sendThongBao(player, "Còn dùng được phân rã ăn cứt à");
                    return;
                }
            }
        }
    }

    private void taySach(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            Item sachTuyetKy = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (issachTuyetKy(item)) {
                    sachTuyetKy = item;
                }
            }
            if (sachTuyetKy != null) {
                int luotTay = 0;
                ItemOption optionLevel = null;
                for (ItemOption io : sachTuyetKy.itemOptions) {
                    if (io.optionTemplate.id == 218) {
                        luotTay = io.param;
                        optionLevel = io;
                        break;
                    }
                }
                if (luotTay == 0) {
                    Service.getInstance().sendThongBao(player, "Còn cái nịt mà tẩy");
                    return;
                }
                Item sachTuyetKy_2 = ItemService.gI().createNewItem((short) sachTuyetKy.template.id);
                if (checkHaveOption(sachTuyetKy, 0, 221)) {
                    Service.getInstance().sendThongBao(player, "Còn cái nịt mà tẩy");
                    return;
                }
                int tyle = new Random().nextInt(10);
                for (int i = 1; i < sachTuyetKy.itemOptions.size(); i++) {
                    if (sachTuyetKy.itemOptions.get(i).optionTemplate.id == 218) {
                        sachTuyetKy.itemOptions.get(i).param -= 1;
                    }
                }
                sachTuyetKy_2.itemOptions.add(new ItemOption(221, 0));
                for (int i = 1; i < sachTuyetKy.itemOptions.size(); i++) {
                    sachTuyetKy_2.itemOptions.add(new ItemOption(sachTuyetKy.itemOptions.get(i).optionTemplate.id,
                            sachTuyetKy.itemOptions.get(i).param));
                }
                sendEffectSuccessCombine(player);
                InventoryServiceNew.gI().addItemBag(player, sachTuyetKy_2);
                InventoryServiceNew.gI().subQuantityItemsBag(player, sachTuyetKy, 1);
                InventoryServiceNew.gI().sendItemBags(player);
                reOpenItemCombine(player);
            }
        }
    }

    public void GetTrangBiKichHoathuydiet(Player player, int id) {
        Item item = ItemService.gI().createNewItem((short) id);
        int[][] optionNormal = {{127, 128}, {130, 132}, {133, 135}};
        int[][] paramNormal = {{139, 140}, {142, 144}, {136, 138}};
        int[][] optionVIP = {{129}, {131}, {134}};
        int[][] paramVIP = {{141}, {143}, {137}};
        int random = Util.nextInt(optionNormal.length);
        int randomSkh = Util.nextInt(100);
        if (item.template.type == 0) {
            item.itemOptions.add(new ItemOption(47, Util.nextInt(1500, 2000)));
        }
        if (item.template.type == 1) {
            item.itemOptions.add(new ItemOption(22, Util.nextInt(100, 150)));
        }
        if (item.template.type == 2) {
            item.itemOptions.add(new ItemOption(0, Util.nextInt(9000, 11000)));
        }
        if (item.template.type == 3) {
            item.itemOptions.add(new ItemOption(23, Util.nextInt(90, 150)));
        }
        if (item.template.type == 4) {
            item.itemOptions.add(new ItemOption(14, Util.nextInt(15, 20)));
        }
        if (randomSkh <= 20) {// tile ra do kich hoat
            if (randomSkh <= 5) { // tile ra option vip
                item.itemOptions.add(new ItemOption(optionVIP[player.gender][0], 0));
                item.itemOptions.add(new ItemOption(paramVIP[player.gender][0], 0));
                item.itemOptions.add(new ItemOption(30, 0));
            } else {//
                item.itemOptions.add(new ItemOption(optionNormal[player.gender][random], 0));
                item.itemOptions.add(new ItemOption(paramNormal[player.gender][random], 0));
                item.itemOptions.add(new ItemOption(30, 0));
            }
        }

        InventoryServiceNew.gI().addItemBag(player, item);
        InventoryServiceNew.gI().sendItemBags(player);
    }

    public void GetTrangBiKichHoatthiensu(Player player, int id) {
        Item item = ItemService.gI().createNewItem((short) id);
        int[][] optionNormal = {{127, 128}, {130, 132}, {133, 135}};
        int[][] paramNormal = {{139, 140}, {142, 144}, {136, 138}};
        int[][] optionVIP = {{129}, {131}, {134}};
        int[][] paramVIP = {{141}, {143}, {137}};
        int random = Util.nextInt(optionNormal.length);
        int randomSkh = Util.nextInt(100);
        if (item.template.type == 0) {
            item.itemOptions.add(new ItemOption(47, Util.nextInt(2000, 2500)));
        }
        if (item.template.type == 1) {
            item.itemOptions.add(new ItemOption(22, Util.nextInt(150, 200)));
        }
        if (item.template.type == 2) {
            item.itemOptions.add(new ItemOption(0, Util.nextInt(18000, 20000)));
        }
        if (item.template.type == 3) {
            item.itemOptions.add(new ItemOption(23, Util.nextInt(150, 200)));
        }
        if (item.template.type == 4) {
            item.itemOptions.add(new ItemOption(14, Util.nextInt(20, 25)));
        }
        if (randomSkh <= 20) {// tile ra do kich hoat
            if (randomSkh <= 5) { // tile ra option vip
                item.itemOptions.add(new ItemOption(optionVIP[player.gender][0], 0));
                item.itemOptions.add(new ItemOption(paramVIP[player.gender][0], 0));
                item.itemOptions.add(new ItemOption(30, 0));
            } else {//
                item.itemOptions.add(new ItemOption(optionNormal[player.gender][random], 0));
                item.itemOptions.add(new ItemOption(paramNormal[player.gender][random], 0));
                item.itemOptions.add(new ItemOption(30, 0));
            }
        }

        InventoryServiceNew.gI().addItemBag(player, item);
        InventoryServiceNew.gI().sendItemBags(player);
    }

    public void laychisoctkhi(Player player, Item ctkhi, int lvkhi) {
        ctkhi.itemOptions.add(new ItemOption(50, 12 + 5 * lvkhi));// sd
        ctkhi.itemOptions.add(new ItemOption(77, 15 + 5 * lvkhi));// hp
        ctkhi.itemOptions.add(new ItemOption(103, 15 + 5 * lvkhi));// ki
        ctkhi.itemOptions.add(new ItemOption(14, 5 + 2 * lvkhi));// cm
        ctkhi.itemOptions.add(new ItemOption(5, 20 + 10 * lvkhi));// sd cm
        ctkhi.itemOptions.add(new ItemOption(156, 10 + 5 * lvkhi));
        ctkhi.itemOptions.add(new ItemOption(181, 5 + 5 * lvkhi));
        ctkhi.itemOptions.add(new ItemOption(106, 0));
        ctkhi.itemOptions.add(new ItemOption(34, 0));
        ctkhi.itemOptions.add(new ItemOption(30, 0));
        InventoryServiceNew.gI().sendItemBags(player);
    }

    public void laychisoctmeo(Player player, Item ctkhi, int lvkhi) {
        ctkhi.itemOptions.add(new ItemOption(50, Util.nextInt(2, 15)));// sd
        ctkhi.itemOptions.add(new ItemOption(77, Util.nextInt(5, 20)));// hp
        ctkhi.itemOptions.add(new ItemOption(103, Util.nextInt(5, 20)));// ki
        ctkhi.itemOptions.add(new ItemOption(162, Util.nextInt(5, 15)));// ki
        ctkhi.itemOptions.add(new ItemOption(114, 100));
        ctkhi.itemOptions.add(new ItemOption(111, 0));
        ctkhi.itemOptions.add(new ItemOption(30, 0));
        InventoryServiceNew.gI().sendItemBags(player);
    }

    public void laychisoctluffy(Player player, Item ctluffy, int lvluffy) {
        ctluffy.itemOptions.add(new ItemOption(50, 10 + 2 * lvluffy));// sd
        ctluffy.itemOptions.add(new ItemOption(95, 2 + 2 * lvluffy));// hp
        ctluffy.itemOptions.add(new ItemOption(96, 2 + 2 * lvluffy));// ki
        ctluffy.itemOptions.add(new ItemOption(14, 2 + 1 * lvluffy));// cm
        ctluffy.itemOptions.add(new ItemOption(5, 2 + 1 * lvluffy));// sd cm
        ctluffy.itemOptions.add(new ItemOption(116, 0));
        ctluffy.itemOptions.add(new ItemOption(106, 0));
        ctluffy.itemOptions.add(new ItemOption(30, 0));
        InventoryServiceNew.gI().sendItemBags(player);
    }

    public void laychiChienLinh(Player player, Item ctkhi) {
        ctkhi.itemOptions.add(new ItemOption(50, Util.nextInt(7, 15)));// sd
        ctkhi.itemOptions.add(new ItemOption(77, Util.nextInt(7, 15)));// hp
        ctkhi.itemOptions.add(new ItemOption(103, Util.nextInt(7, 15)));// ki
        ctkhi.itemOptions.add(new ItemOption(72, 2));
        ctkhi.itemOptions.add(new ItemOption(30, 0));
        InventoryServiceNew.gI().sendItemBags(player);
    }

    private void doiKiemThan(Player player) {
        if (player.combineNew.itemsCombine.size() == 3) {
            Item keo = null, luoiKiem = null, chuoiKiem = null;
            for (Item it : player.combineNew.itemsCombine) {
                if (it.template.id == 2015) {
                    keo = it;
                } else if (it.template.id == 2016) {
                    chuoiKiem = it;
                } else if (it.template.id == 2017) {
                    luoiKiem = it;
                }
            }
            if (keo != null && keo.quantity >= 99 && luoiKiem != null && chuoiKiem != null) {
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                    sendEffectSuccessCombine(player);
                    Item item = ItemService.gI().createNewItem((short) 2018);
                    item.itemOptions.add(new Item.ItemOption(50, Util.nextInt(9, 15)));
                    item.itemOptions.add(new Item.ItemOption(77, Util.nextInt(8, 15)));
                    item.itemOptions.add(new Item.ItemOption(103, Util.nextInt(8, 15)));
                    if (Util.isTrue(80, 100)) {
                        item.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 15)));
                    }
                    InventoryServiceNew.gI().addItemBag(player, item);

                    InventoryServiceNew.gI().subQuantityItemsBag(player, keo, 99);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, luoiKiem, 1);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, chuoiKiem, 1);

                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.gI().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
    }

    private void doiChuoiKiem(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            Item manhNhua = player.combineNew.itemsCombine.get(0);
            if (manhNhua.template.id == 2014 && manhNhua.quantity >= 99) {
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                    sendEffectSuccessCombine(player);
                    Item item = ItemService.gI().createNewItem((short) 2016);
                    InventoryServiceNew.gI().addItemBag(player, item);

                    InventoryServiceNew.gI().subQuantityItemsBag(player, manhNhua, 99);

                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.gI().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
    }

    private void doiLuoiKiem(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            Item manhSat = player.combineNew.itemsCombine.get(0);
            if (manhSat.template.id == 2013 && manhSat.quantity >= 99) {
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                    sendEffectSuccessCombine(player);
                    Item item = ItemService.gI().createNewItem((short) 2017);
                    InventoryServiceNew.gI().addItemBag(player, item);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, manhSat, 99);

                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.gI().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
    }

    private void doiManhKichHoat(Player player) {
        if (player.combineNew.itemsCombine.size() == 2 || player.combineNew.itemsCombine.size() == 3) {
            Item nr1s = null, doThan = null, buaBaoVe = null;
            for (Item it : player.combineNew.itemsCombine) {
                if (it.template.id == 14) {
                    nr1s = it;
                } else if (it.template.id == 2010) {
                    buaBaoVe = it;
                } else if (it.template.id >= 555 && it.template.id <= 567) {
                    doThan = it;
                }
            }

            if (nr1s != null && doThan != null) {
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0
                        && player.inventory.gold >= COST_DOI_MANH_KICH_HOAT) {
                    player.inventory.gold -= COST_DOI_MANH_KICH_HOAT;
                    int tiLe = buaBaoVe != null ? 100 : 50;
                    if (Util.isTrue(tiLe, 100)) {
                        sendEffectSuccessCombine(player);
                        Item item = ItemService.gI().createNewItem((short) 2009);
                        item.itemOptions.add(new Item.ItemOption(30, 0));
                        InventoryServiceNew.gI().addItemBag(player, item);
                    } else {
                        sendEffectFailCombine(player);
                    }
                    InventoryServiceNew.gI().subQuantityItemsBag(player, nr1s, 1);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, doThan, 1);
                    if (buaBaoVe != null) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, buaBaoVe, 1);
                    }
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.gI().sendMoney(player);
                    reOpenItemCombine(player);
                }
            } else {
                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                        "Hãy chọn 1 trang bị thần linh và 1 viên ngọc rồng 1 sao", "Đóng");
            }
        }
    }

    private void phanradothanlinh(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            player.inventory.gold -= 50000000;
            List<Integer> itemdov2 = new ArrayList<>(Arrays.asList(562, 564, 566));
            Item item = player.combineNew.itemsCombine.get(0);
            int couponAdd = itemdov2.stream().anyMatch(t -> t == item.template.id) ? 2
                    : item.template.id == 561 ? 3 : 1;
            sendEffectSuccessCombine(player);
            Item dangusac = ItemService.gI().createNewItem((short) 674);
            Item dangusac1 = ItemService.gI().createNewItem((short) 674);
            Item dangusac2 = ItemService.gI().createNewItem((short) 674);
            InventoryServiceNew.gI().addItemBag(player, dangusac);
            InventoryServiceNew.gI().sendItemBags(player);
            if (item.template.id == 561) {
                InventoryServiceNew.gI().addItemBag(player, dangusac);
                InventoryServiceNew.gI().addItemBag(player, dangusac1);
                InventoryServiceNew.gI().addItemBag(player, dangusac2);
                InventoryServiceNew.gI().sendItemBags(player);
            } else if (item.template.id == 562 || item.template.id == 564 || item.template.id == 566) {
                InventoryServiceNew.gI().addItemBag(player, dangusac);
                InventoryServiceNew.gI().addItemBag(player, dangusac1);
                InventoryServiceNew.gI().sendItemBags(player);
            }
            Service.gI().sendThongBaoOK(player, "Bạn Nhận Được Đá Ngũ Sắc");
            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
            player.combineNew.itemsCombine.clear();
            InventoryServiceNew.gI().sendItemBags(player);
            Service.gI().sendMoney(player);
            reOpenItemCombine(player);
        }
    }

    public void openDTS(Player player) {
        // check sl đồ tl, đồ hd
        // new update 2 mon huy diet + 1 mon than linh(skh theo style) + 5 manh bat ki
        if (player.combineNew.itemsCombine.size() != 4) {
            Service.gI().sendThongBao(player, "Thiếu đồ");
            return;
        }
        if (player.inventory.gold < COST) {
            Service.gI().sendThongBao(player, "Ảo ít thôi con...");
            return;
        }
        if (InventoryServiceNew.gI().getCountEmptyBag(player) < 1) {
            Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            return;
        }
        Item itemTL = player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDTL())
                .findFirst().get();
        List<Item> itemHDs = player.combineNew.itemsCombine.stream()
                .filter(item -> item.isNotNullItem() && item.isDHD()).collect(Collectors.toList());
        Item itemManh = player.combineNew.itemsCombine.stream()
                .filter(item -> item.isNotNullItem() && item.isManhTS() && item.quantity >= 5).findFirst().get();

        player.inventory.gold -= COST;
        sendEffectSuccessCombine(player);
        short[][] itemIds = {{1048, 1051, 1054, 1057, 1060}, {1049, 1052, 1055, 1058, 1061},
        {1050, 1053, 1056, 1059, 1062}}; // thứ tự td - 0,nm - 1, xd - 2

        Item itemTS = ItemService.gI().DoThienSu(
                itemIds[itemTL.template.gender > 2 ? player.gender : itemTL.template.gender][itemManh.typeIdManh()],
                itemTL.template.gender);
        InventoryServiceNew.gI().addItemBag(player, itemTS);

        InventoryServiceNew.gI().subQuantityItemsBag(player, itemTL, 1);
        InventoryServiceNew.gI().subQuantityItemsBag(player, itemManh, 5);
        itemHDs.forEach(item -> InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1));
        InventoryServiceNew.gI().sendItemBags(player);
        Service.gI().sendMoney(player);
        Service.gI().sendThongBao(player, "Bạn đã nhận được " + itemTS.template.name);
        player.combineNew.itemsCombine.clear();
        reOpenItemCombine(player);
    }

    public void openSKHVIP(Player player) {
        // 1 thiên sứ + 2 món kích hoạt -- món đầu kh làm gốc
        if (player.combineNew.itemsCombine.size() != 3) {
            Service.getInstance().sendThongBao(player, "Thiếu nguyên liệu");
            return;
        }
        if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDHD()).count() != 1) {
            Service.getInstance().sendThongBao(player, "Thiếu đồ Hủy Diệt");
            return;
        }
        if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDTL()).count() != 2) {
            Service.getInstance().sendThongBao(player, "Thiếu đồ Thần Linh");
            return;
        }
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            if (player.inventory.gold < 1) {
                Service.getInstance().sendThongBao(player, "Con cần thêm vàng để đổi...");
                return;
            }
            player.inventory.gold -= COST;
            Item itemTS = player.combineNew.itemsCombine.stream().filter(Item::isDHD).findFirst().get();
            List<Item> itemSKH = player.combineNew.itemsCombine.stream()
                    .filter(item -> item.isNotNullItem() && item.isDTL()).collect(Collectors.toList());
            CombineServiceNew.gI().sendEffectOpenItem(player, itemTS.template.iconID, itemTS.template.iconID);
            short itemId;
            if (itemTS.template.gender == 3 || itemTS.template.type == 4) {
                itemId = Manager.radaSKHVip[Util.nextInt(0, 5)];
                if (player.getSession().bdPlayer > 0 && Util.isTrue(1, (int) (100 / player.getSession().bdPlayer))) {
                    itemId = Manager.radaSKHVip[6];
                }
            } else {
                itemId = Manager.doSKHVip[itemTS.template.gender][itemTS.template.type][Util.nextInt(0, 5)];
                if (player.getSession().bdPlayer > 0 && Util.isTrue(1, (int) (100 / player.getSession().bdPlayer))) {
                    itemId = Manager.doSKHVip[itemTS.template.gender][itemTS.template.type][6];
                }
            }
            int skhId = ItemService.gI().randomSKHId(itemTS.template.gender);
            Item item;
            if (new Item(itemId).isDTL()) {
                item = Util.ratiItemTL(itemId);
                item.itemOptions.add(new Item.ItemOption(skhId, 1));
                item.itemOptions.add(new Item.ItemOption(ItemService.gI().optionIdSKH(skhId), 1));
                item.itemOptions.remove(item.itemOptions.stream()
                        .filter(itemOption -> itemOption.optionTemplate.id == 21).findFirst().get());
                item.itemOptions.add(new Item.ItemOption(21, 15));
                item.itemOptions.add(new Item.ItemOption(30, 1));
            } else {
                item = ItemService.gI().itemSKH(itemId, skhId);
            }
            InventoryServiceNew.gI().addItemBag(player, item);
            InventoryServiceNew.gI().subQuantityItemsBag(player, itemTS, 1);
            itemSKH.forEach(i -> InventoryServiceNew.gI().subQuantityItemsBag(player, i, 2));
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
            player.combineNew.itemsCombine.clear();
            reOpenItemCombine(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

    private void dapDoKichHoat(Player player) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hãy chuẩn bị ít nhất 1 ô trống trong hành trang");
            return;
        }
        if (player.combineNew.itemsCombine.size() == 3) {
            Item thiensu = null;
            Item skh1 = null;
            Item skh2 = null;
            if (player.combineNew.itemsCombine.get(0).isDTS()) {
                thiensu = player.combineNew.itemsCombine.get(0);
            }
            if (player.combineNew.itemsCombine.get(1).isSKH()) {
                skh1 = player.combineNew.itemsCombine.get(1);
            }
            if (player.combineNew.itemsCombine.get(2).isSKH()) {
                skh2 = player.combineNew.itemsCombine.get(2);
            }
            if (thiensu != null && skh1 != null && skh2 != null) {
                UpdateItem.createSKHThienSu(player, thiensu.template.gender, thiensu.template.type, thiensu);
                player.inventory.gold -= 500000000;
                Service.gI().sendMoney(player);
                InventoryServiceNew.gI().subQuantityItemsBag(player, thiensu, 1);
                InventoryServiceNew.gI().subQuantityItemsBag(player, skh1, 1);
                InventoryServiceNew.gI().subQuantityItemsBag(player, skh2, 1);
                InventoryServiceNew.gI().sendItemBags(player);
                reOpenItemCombine(player);
            }
        } else {
            return;
        }
    }

    private void dapDoKichHoatthuong(Player player) {
        if (player.combineNew.itemsCombine.size() != 2) {
            Service.gI().sendThongBao(player, "Sai nguyên liệu");
            return;
        }
        if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isDTL()).count() != 2) {
            Service.gI().sendThongBao(player, "Thiếu đồ Thần Linh");
            return;
        }
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            if (player.inventory.gold < 1) {
                Service.gI().sendThongBao(player, "Con cần thêm vàng để đổi...");
                return;
            }
            player.inventory.gold -= COST;
            Item itemTL = player.combineNew.itemsCombine.stream().filter(Item::isDTL).findFirst().get();
            List<Item> itemDiKem = player.combineNew.itemsCombine.stream()
                    .filter(item -> item.isNotNullItem() && item.isDTL()).collect(Collectors.toList());
            CombineServiceNew.gI().sendEffectOpenItem(player, itemTL.template.iconID, itemTL.template.iconID);
            short itemId = Manager.doSKH[player.gender][itemTL.template.type][0];
            Item item = ItemService.gI().itemSKHT(itemId, ItemService.gI().randomSKHTId(player.gender));
            InventoryServiceNew.gI().addItemBag(player, item);
            InventoryServiceNew.gI().subQuantityItemsBag(player, itemTL, 1);
            itemDiKem.forEach(it -> InventoryServiceNew.gI().subQuantityItemsBag(player, it, 1));
            InventoryServiceNew.gI().sendItemBags(player);
            Service.gI().sendMoney(player);
            player.combineNew.itemsCombine.clear();
            reOpenItemCombine(player);
        } else {
            Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

    private void doiVeHuyDiet(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            Item item = player.combineNew.itemsCombine.get(0);
            if (item.isNotNullItem() && item.template.id >= 555 && item.template.id <= 567) {
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0
                        && player.inventory.gold >= COST_DOI_VE_DOI_DO_HUY_DIET) {
                    player.inventory.gold -= COST_DOI_VE_DOI_DO_HUY_DIET;
                    Item ticket = ItemService.gI().createNewItem((short) (2001 + item.template.type));
                    ticket.itemOptions.add(new Item.ItemOption(30, 0));
                    InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                    InventoryServiceNew.gI().addItemBag(player, ticket);
                    sendEffectOpenItem(player, item.template.iconID, ticket.template.iconID);

                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.gI().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
    }

    private void nangCapChienLinh(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {
            int gold = player.combineNew.goldCombine;
            if (player.inventory.gold < gold) {
                Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện");
                return;
            }
            int ruby = player.combineNew.rubyCombine;
            if (player.inventory.ruby < ruby) {
                Service.gI().sendThongBao(player, "Không đủ hồng ngọc để thực hiện");
                return;
            }

            Item linhthu = null;
            Item ttt = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (item.template.type == 72) {
                    linhthu = item;
                } else if (item.template.id == 2031) {
                    ttt = item;
                }
            }
            if (linhthu != null && ttt != null) {

                if (ttt.quantity < 10) {
                    Service.gI().sendThongBao(player, "Thăng tinh thạch");
                    return;
                }
                player.inventory.gold -= gold;
                player.inventory.ruby -= ruby;
                InventoryServiceNew.gI().subQuantityItemsBag(player, ttt, 10);
                if (Util.isTrue(player.combineNew.ratioCombine, 100)) {
                    short[] chienlinh = {2019, 2020, 2021, 2022, 2023, 2024, 2025};
                    linhthu.template = ItemService.gI().getTemplate(chienlinh[Util.nextInt(0, 2)]);
                    linhthu.itemOptions.clear();
                    laychiChienLinh(player, linhthu);
                    sendEffectSuccessCombine(player);
                } else {
                    sendEffectFailCombine(player);
                }
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
    }

    private void nangCapKhi(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {
            int gold = player.combineNew.goldCombine;
            if (player.inventory.gold < gold) {
                Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện");
                return;
            }
            int ruby = player.combineNew.rubyCombine;
            if (player.inventory.ruby < ruby) {
                Service.gI().sendThongBao(player, "Không đủ hồng ngọc để thực hiện");
                return;
            }

            Item ctkhi = null;
            Item dns = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (checkctkhi(item)) {
                    ctkhi = item;
                } else if (item.template.id == 2063) {
                    dns = item;
                }
            }
            if (ctkhi != null && dns != null) {
                int lvkhi = lvkhi(ctkhi);
                int countdns = getcountdnsnangkhi(lvkhi);
                if (countdns > dns.quantity) {
                    Service.gI().sendThongBao(player, "Không đủ Đá little Girl");
                    return;
                }
                player.inventory.gold -= gold;
                player.inventory.ruby -= ruby;
                InventoryServiceNew.gI().subQuantityItemsBag(player, dns, countdns);
                if (Util.isTrue(player.combineNew.ratioCombine, 100)) {
                    short idctkhisaunc = getidctkhisaukhilencap(lvkhi);
                    ctkhi.template = ItemService.gI().getTemplate(idctkhisaunc);
                    ctkhi.itemOptions.clear();
                    ctkhi.itemOptions.add(new Item.ItemOption(72, lvkhi + 1));
                    laychisoctkhi(player, ctkhi, lvkhi);
                    sendEffectSuccessCombine(player);
                } else {
                    sendEffectFailCombine(player);
                }
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
    }

    private void nangCapMeo(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {
            int gold = player.combineNew.goldCombine;
            if (player.inventory.gold < gold) {
                Service.gI().sendThongBao(player, "Không đủ vàng để huấn luyện");
                return;
            }
            int ruby = player.combineNew.rubyCombine;
            if (player.inventory.ruby < ruby) {
                Service.gI().sendThongBao(player, "Không đủ hồng ngọc để huấn luyện");
                return;
            }

            Item ctmeo = null;
            Item dns = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (checkctmeo(item)) {
                    ctmeo = item;
                } else if (item.template.id == 1004) {
                    dns = item;
                }
            }
            if (ctmeo != null && dns != null) {
                int lvmeo = lvmeo(ctmeo);
                int countdns = getcountdnsnangmeo(lvmeo);
                if (countdns > dns.quantity) {
                    Service.gI().sendThongBao(player, "Không đủ Thức ăn");
                    return;
                }
                player.inventory.gold -= gold;
                player.inventory.ruby -= ruby;
                InventoryServiceNew.gI().subQuantityItemsBag(player, dns, countdns);
                if (Util.isTrue(player.combineNew.ratioCombine, 100)) {
                    short idctmeosaunc = getidctmeosaukhilencap(lvmeo);
                    ctmeo.template = ItemService.gI().getTemplate(idctmeosaunc);
                    ctmeo.itemOptions.clear();
                    ctmeo.itemOptions.add(new Item.ItemOption(72, lvmeo + 1));
                    laychisoctmeo(player, ctmeo, lvmeo);
                    sendEffectSuccessCombine(player);
                } else {
                    sendEffectFailCombine(player);
                }
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
    }

    private void nangCapLuffy(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {
            int gold = player.combineNew.goldCombine;
            if (player.inventory.gold < gold) {
                Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện");
                return;
            }
            int ruby = player.combineNew.rubyCombine;
            if (player.inventory.ruby < ruby) {
                Service.gI().sendThongBao(player, "Không đủ hồng ngọc để thực hiện");
                return;
            }

            Item ctluffy = null;
            Item dns = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (checkctluffy(item)) {
                    ctluffy = item;
                } else if (item.template.id == 1335) {
                    dns = item;
                }
            }
            if (ctluffy != null && dns != null) {
                int lvluffy = lvluffy(ctluffy);
                int countdns = getcountdnsnangluffy(lvluffy);
                if (countdns > dns.quantity) {
                    Service.gI().sendThongBao(player, "Không đủ Đá thức tỉnh");
                    return;
                }
                player.inventory.gold -= gold;
                player.inventory.ruby -= ruby;
                InventoryServiceNew.gI().subQuantityItemsBag(player, dns, countdns);
                if (Util.isTrue(player.combineNew.ratioCombine, 100)) {
                    short idctluffysaunc = getidctluffysaukhilencap(lvluffy);
                    ctluffy.template = ItemService.gI().getTemplate(idctluffysaunc);
                    ctluffy.itemOptions.clear();
                    ctluffy.itemOptions.add(new Item.ItemOption(72, lvluffy + 1));
                    laychisoctluffy(player, ctluffy, lvluffy);
                    sendEffectSuccessCombine(player);
                } else {
                    sendEffectFailCombine(player);
                }
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
    }

    private void nangCapBongTai(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {
            int gold = player.combineNew.goldCombine;
            if (player.inventory.gold < gold) {
                Service.getInstance().sendThongBao(player, "Không đủ vàng để thực hiện");
                return;
            }
            int gem = player.combineNew.gemCombine;
            if (player.inventory.gem < gem) {
                Service.getInstance().sendThongBao(player, "Không đủ ngọc để thực hiện");
                return;
            }
            Item bongtai = null;
            Item manhvobt = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (checkbongtai(item)) {// Kiểm tra có bt c1 hoặc c2 không , id 451 và 921
                    bongtai = item;
                } else if (item.template.id == 933) {// check có mảnh vỡ bông tai và gán vào
                    manhvobt = item;
                }
            }
            if (bongtai != null && manhvobt != null) {
                int level = 0;
                for (ItemOption io : bongtai.itemOptions) {
                    if (io.optionTemplate.id == 72) {
                        level = io.param;
                        break;
                    }
                }
                if (level < 4) {
                    int lvbt = lvbt(bongtai);
                    int countmvbt = getcountmvbtnangbt(lvbt);
                    if (countmvbt > manhvobt.quantity) {
                        Service.getInstance().sendThongBao(player, "Không đủ Mảnh vỡ bông tai");
                        return;
                    }
                    player.inventory.gold -= gold;
                    player.inventory.gem -= gem;
                    InventoryServiceNew.gI().subQuantityItemsBag(player, manhvobt, countmvbt);
                    if (Util.isTrue(player.combineNew.ratioCombine, 100)) {
                        bongtai.template = ItemService.gI().getTemplate(getidbtsaukhilencap(lvbt));
                        bongtai.itemOptions.clear();
                        bongtai.itemOptions.add(new ItemOption(72, lvbt + 1));
                        sendEffectSuccessCombine(player);
                    } else {
                        sendEffectFailCombine(player);
                    }
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
    }

    private short getidbtsaukhilencap(int lvbtcu) {
        switch (lvbtcu) {
            case 1:
                return 921;
            case 2:
                return 1155;
            case 3:
                return 1156;

        }
        return 0;
    }

    private void moChiSoBongTai(Player player) {
        if (player.combineNew.itemsCombine.size() == 3) {
            int gold = player.combineNew.goldCombine;
            if (player.inventory.gold < gold) {
                Service.getInstance().sendThongBao(player, "Không đủ vàng để thực hiện");
                return;
            }
            int gem = player.combineNew.gemCombine;
            if (player.inventory.gem < gem) {
                Service.getInstance().sendThongBao(player, "Không đủ ngọc để thực hiện");
                return;
            }
            Item bongTai = null;
            Item manhHon = null;
            Item daXanhLam = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (item.template.id == 921 || item.template.id == 1155 || item.template.id == 1156) {
                    bongTai = item;
                } else if (item.template.id == 934) {
                    manhHon = item;
                } else if (item.template.id == 935) {
                    daXanhLam = item;
                }
            }
            if (bongTai != null && daXanhLam != null && manhHon.quantity >= 99 && daXanhLam.quantity >= 1) {
                player.inventory.gold -= gold;
                player.inventory.gem -= gem;
                InventoryServiceNew.gI().subQuantityItemsBag(player, manhHon, 99);
                InventoryServiceNew.gI().subQuantityItemsBag(player, daXanhLam, 1);
                if (Util.isTrue(player.combineNew.ratioCombine, 100)) {
                    bongTai.itemOptions.clear();
                    if (bongTai.template.id == 921) {
                        bongTai.itemOptions.add(new ItemOption(72, 2));
                        int rdUp = Util.nextInt(0, 8);
                        switch (rdUp) {
                            case 0:
                                bongTai.itemOptions.add(new ItemOption(50, 5));
                                break;
                            case 1:
                                bongTai.itemOptions.add(new ItemOption(77, 5));
                                break;
                            case 2:
                                bongTai.itemOptions.add(new ItemOption(103, 5));
                                break;
                            case 3:
                                bongTai.itemOptions.add(new ItemOption(108, 5));
                                break;
                            case 4:
                                bongTai.itemOptions.add(new ItemOption(94, 5));
                                break;
                            case 5:
                                bongTai.itemOptions.add(new ItemOption(14, 5));
                                break;
                            case 6:
                                bongTai.itemOptions.add(new ItemOption(80, 5));
                                break;
                            case 7:
                                bongTai.itemOptions.add(new ItemOption(81, 5));
                                break;
                            case 8:
                                bongTai.itemOptions.add(new ItemOption(101, 5));
                                break;
                        }
                    } else if (bongTai.template.id == 1155) {
                        bongTai.itemOptions.add(new ItemOption(72, 3));
                        int rdUp1 = Util.nextInt(0, 8);
                        switch (rdUp1) {
                            case 0:
                                bongTai.itemOptions.add(new ItemOption(50, Util.nextInt(5, 10)));
                                break;
                            case 1:
                                bongTai.itemOptions.add(new ItemOption(77, Util.nextInt(5, 10)));
                                break;
                            case 2:
                                bongTai.itemOptions.add(new ItemOption(103, Util.nextInt(5, 10)));
                                break;
                            case 3:
                                bongTai.itemOptions.add(new ItemOption(108, Util.nextInt(5, 10)));
                                break;
                            case 4:
                                bongTai.itemOptions.add(new ItemOption(94, Util.nextInt(5, 10)));
                                break;
                            case 5:
                                bongTai.itemOptions.add(new ItemOption(14, Util.nextInt(5, 10)));
                                break;
                            case 6:
                                bongTai.itemOptions.add(new ItemOption(80, Util.nextInt(5, 10)));
                                break;
                            case 7:
                                bongTai.itemOptions.add(new ItemOption(81, Util.nextInt(5, 10)));
                                break;
                            case 8:
                                bongTai.itemOptions.add(new ItemOption(101, Util.nextInt(5, 10)));
                                ;
                                break;
                        }
                    } else if (bongTai.template.id == 1156) {
                        bongTai.itemOptions.add(new ItemOption(72, 3));
                        int rdUp1 = Util.nextInt(0, 8);
                        switch (rdUp1) {
                            case 0:
                                bongTai.itemOptions.add(new ItemOption(50, Util.nextInt(5, 15)));
                                break;
                            case 1:
                                bongTai.itemOptions.add(new ItemOption(77, Util.nextInt(5, 15)));
                                break;
                            case 2:
                                bongTai.itemOptions.add(new ItemOption(103, Util.nextInt(5, 15)));
                                break;
                            case 3:
                                bongTai.itemOptions.add(new ItemOption(108, Util.nextInt(5, 15)));
                                break;
                            case 4:
                                bongTai.itemOptions.add(new ItemOption(94, Util.nextInt(5, 15)));
                                break;
                            case 5:
                                bongTai.itemOptions.add(new ItemOption(14, Util.nextInt(5, 15)));
                                break;
                            case 6:
                                bongTai.itemOptions.add(new ItemOption(80, Util.nextInt(5, 15)));
                                break;
                            case 7:
                                bongTai.itemOptions.add(new ItemOption(81, Util.nextInt(5, 15)));
                                break;
                            case 8:
                                bongTai.itemOptions.add(new ItemOption(101, Util.nextInt(5, 15)));
                                ;
                                break;
                        }
                    }
                    sendEffectSuccessCombine(player);
                } else {
                    sendEffectFailCombine(player);
                }
                InventoryServiceNew.gI().sendItemBags(player);
                Service.getInstance().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
    }

    private void moChiSoLinhThu(Player player) {
        if (player.combineNew.itemsCombine.size() == 3) {
            int gold = player.combineNew.goldCombine;
            if (player.inventory.gold < gold) {
                Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện");
                return;
            }
            int ruby = player.combineNew.rubyCombine;
            if (player.inventory.ruby < ruby) {
                Service.gI().sendThongBao(player, "Không đủ hồng ngọc để thực hiện");
                return;
            }
            Item ChienLinh = null;
            Item damathuat = null;
            Item honthu = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (item.template.id >= 1149 && item.template.id <= 1151) {
                    ChienLinh = item;
                } else if (item.template.id == 2030) {
                    damathuat = item;
                } else if (item.template.id == 2029) {
                    honthu = item;
                }
            }
            if (ChienLinh != null && damathuat.quantity >= 99 && honthu.quantity >= 99) {
                player.inventory.gold -= gold;
                player.inventory.ruby -= ruby;
                InventoryServiceNew.gI().subQuantityItemsBag(player, damathuat, 99);
                InventoryServiceNew.gI().subQuantityItemsBag(player, honthu, 99);
                if (Util.isTrue(player.combineNew.ratioCombine, 100)) {
                    ChienLinh.itemOptions.add(new Item.ItemOption(206, 0));
                    int rdUp = Util.nextInt(0, 7);
                    if (rdUp == 1) {
                        ChienLinh.itemOptions.add(new Item.ItemOption(50, Util.nextInt(5, 25)));
                    } else if (rdUp == 1) {
                        ChienLinh.itemOptions.add(new Item.ItemOption(77, Util.nextInt(5, 25)));
                    } else if (rdUp == 2) {
                        ChienLinh.itemOptions.add(new Item.ItemOption(103, Util.nextInt(5, 25)));
                    } else if (rdUp == 3) {
                        ChienLinh.itemOptions.add(new Item.ItemOption(108, Util.nextInt(5, 25)));
                    } else if (rdUp == 4) {
                        ChienLinh.itemOptions.add(new Item.ItemOption(94, Util.nextInt(5, 15)));
                    } else if (rdUp == 5) {
                        ChienLinh.itemOptions.add(new Item.ItemOption(14, Util.nextInt(5, 15)));
                    } else if (rdUp == 6) {
                        ChienLinh.itemOptions.add(new Item.ItemOption(80, Util.nextInt(5, 25)));
                    } else if (rdUp == 7) {
                        ChienLinh.itemOptions.add(new Item.ItemOption(81, Util.nextInt(5, 25)));
                    }
                    sendEffectSuccessCombine(player);
                } else {
                    sendEffectFailCombine(player);
                }
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
    }

    public void openCreateItemAngel(Player player) {
        // Công thức vip + x999 Mảnh thiên sứ + đá nâng cấp + đá may mắn
        if (player.combineNew.itemsCombine.size() < 2 || player.combineNew.itemsCombine.size() > 4) {
            Service.getInstance().sendThongBao(player, "Thiếu vật phẩm, vui lòng thêm vào");
            return;
        }
        if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.isCongThucVip())
                .count() != 1) {
            Service.getInstance().sendThongBao(player, "Thiếu Công thức Vip");
            return;
        }
        if (player.combineNew.itemsCombine.stream()
                .filter(item -> item.isNotNullItem() && item.isManhTS() && item.quantity >= 999).count() != 1) {
            Service.getInstance().sendThongBao(player, "Thiếu Mảnh thiên sứ");
            return;
        }
        // if (player.combineNew.itemsCombine.size() == 3 &&
        // player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem()
        // && item.isDaNangCap()).count() != 1 || player.combineNew.itemsCombine.size()
        // == 4 && player.combineNew.itemsCombine.stream().filter(item ->
        // item.isNotNullItem() && item.isDaNangCap()).count() != 1) {
        // Service.getInstance().sendThongBao(player, "Thiếu Đá nâng cấp");
        // return;
        // }
        // if (player.combineNew.itemsCombine.size() == 3 &&
        // player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem()
        // && item.isDaMayMan()).count() != 1 || player.combineNew.itemsCombine.size()
        // == 4 && player.combineNew.itemsCombine.stream().filter(item ->
        // item.isNotNullItem() && item.isDaMayMan()).count() != 1) {
        // Service.getInstance().sendThongBao(player, "Thiếu Đá may mắn");
        // return;
        // }
        Item mTS = null, daNC = null, daMM = null, CtVip = null;
        for (Item item : player.combineNew.itemsCombine) {
            if (item.isNotNullItem()) {
                if (item.isManhTS()) {
                    mTS = item;
                } else if (item.isDaNangCap()) {
                    daNC = item;
                } else if (item.isDaMayMan()) {
                    daMM = item;
                } else if (item.isCongThucVip()) {
                    CtVip = item;
                }
            }
        }
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {// check chỗ trống hành trang
            if (player.inventory.gold < 2000000000) {
                Service.getInstance().sendThongBao(player, "Không đủ vàng để thực hiện");
                return;
            }
            player.inventory.gold -= 2000000000;

            int tilemacdinh = 35;
            int tileLucky = 20;
            if (daNC != null) {
                tilemacdinh += (daNC.template.id - 1073) * 10;
            } else {
                tilemacdinh = tilemacdinh;
            }
            if (daMM != null) {
                tileLucky += tileLucky * (daMM.template.id - 1078) * 10 / 100;
            } else {
                tileLucky = tileLucky;
            }
            if (Util.nextInt(0, 100) < tilemacdinh) {
                Item itemCtVip = player.combineNew.itemsCombine.stream()
                        .filter(item -> item.isNotNullItem() && item.isCongThucVip()).findFirst().get();
                if (daNC != null) {
                    Item itemDaNangC = player.combineNew.itemsCombine.stream()
                            .filter(item -> item.isNotNullItem() && item.isDaNangCap()).findFirst().get();
                }
                if (daMM != null) {
                    Item itemDaMayM = player.combineNew.itemsCombine.stream()
                            .filter(item -> item.isNotNullItem() && item.isDaMayMan()).findFirst().get();
                }
                Item itemManh = player.combineNew.itemsCombine.stream()
                        .filter(item -> item.isNotNullItem() && item.isManhTS() && item.quantity >= 999).findFirst()
                        .get();

                tilemacdinh = Util.nextInt(0, 50);
                if (tilemacdinh == 49) {
                    tilemacdinh = 20;
                } else if (tilemacdinh == 48 || tilemacdinh == 47) {
                    tilemacdinh = 19;
                } else if (tilemacdinh == 46 || tilemacdinh == 45) {
                    tilemacdinh = 18;
                } else if (tilemacdinh == 44 || tilemacdinh == 43) {
                    tilemacdinh = 17;
                } else if (tilemacdinh == 42 || tilemacdinh == 41) {
                    tilemacdinh = 16;
                } else if (tilemacdinh == 40 || tilemacdinh == 39) {
                    tilemacdinh = 15;
                } else if (tilemacdinh == 38 || tilemacdinh == 37) {
                    tilemacdinh = 14;
                } else if (tilemacdinh == 36 || tilemacdinh == 35) {
                    tilemacdinh = 13;
                } else if (tilemacdinh == 34 || tilemacdinh == 33) {
                    tilemacdinh = 12;
                } else if (tilemacdinh == 32 || tilemacdinh == 31) {
                    tilemacdinh = 11;
                } else if (tilemacdinh == 30 || tilemacdinh == 29) {
                    tilemacdinh = 10;
                } else if (tilemacdinh <= 28 || tilemacdinh >= 26) {
                    tilemacdinh = 9;
                } else if (tilemacdinh <= 25 || tilemacdinh >= 23) {
                    tilemacdinh = 8;
                } else if (tilemacdinh <= 22 || tilemacdinh >= 20) {
                    tilemacdinh = 7;
                } else if (tilemacdinh <= 19 || tilemacdinh >= 17) {
                    tilemacdinh = 6;
                } else if (tilemacdinh <= 16 || tilemacdinh >= 14) {
                    tilemacdinh = 5;
                } else if (tilemacdinh <= 13 || tilemacdinh >= 11) {
                    tilemacdinh = 4;
                } else if (tilemacdinh <= 10 || tilemacdinh >= 8) {
                    tilemacdinh = 3;
                } else if (tilemacdinh <= 7 || tilemacdinh >= 5) {
                    tilemacdinh = 2;
                } else if (tilemacdinh <= 4 || tilemacdinh >= 2) {
                    tilemacdinh = 1;
                } else if (tilemacdinh <= 1) {
                    tilemacdinh = 0;
                }
                short[][] itemIds = {{1048, 1051, 1054, 1057, 1060}, {1049, 1052, 1055, 1058, 1061},
                {1050, 1053, 1056, 1059, 1062}}; // thứ tự td - 0,nm - 1, xd - 2

                Item itemTS = ItemService.gI().DoThienSu(
                        itemIds[itemCtVip.template.gender > 2 ? player.gender : itemCtVip.template.gender][itemManh
                                .typeIdManh()],
                        itemCtVip.template.gender);

                tilemacdinh += 10;

                if (tilemacdinh > 0) {
                    for (byte i = 0; i < itemTS.itemOptions.size(); i++) {
                        if (itemTS.itemOptions.get(i).optionTemplate.id != 21
                                && itemTS.itemOptions.get(i).optionTemplate.id != 30) {
                            itemTS.itemOptions.get(i).param += (itemTS.itemOptions.get(i).param * tilemacdinh / 100);
                        }
                    }
                }
                tilemacdinh = Util.nextInt(0, 100);

                if (tilemacdinh <= tileLucky) {
                    if (tilemacdinh >= (tileLucky - 3)) {
                        tileLucky = 3;
                    } else if (tilemacdinh <= (tileLucky - 4) && tilemacdinh >= (tileLucky - 10)) {
                        tileLucky = 2;
                    } else {
                        tileLucky = 1;
                    }
                    itemTS.itemOptions.add(new Item.ItemOption(15, tileLucky));
                    ArrayList<Integer> listOptionBonus = new ArrayList<>();
                    listOptionBonus.add(50);
                    listOptionBonus.add(77);
                    listOptionBonus.add(103);
                    listOptionBonus.add(98);
                    listOptionBonus.add(99);
                    for (int i = 0; i < tileLucky; i++) {
                        tilemacdinh = Util.nextInt(0, listOptionBonus.size());
                        itemTS.itemOptions.add(new ItemOption(listOptionBonus.get(tilemacdinh), Util.nextInt(1, 5)));
                        listOptionBonus.remove(tilemacdinh);
                    }
                }

                InventoryServiceNew.gI().addItemBag(player, itemTS);
                sendEffectSuccessCombine(player);
                if (mTS != null) {
                    InventoryServiceNew.gI().subQuantityItemsBag(player, mTS, 999);
                }
                if (CtVip != null) {
                    InventoryServiceNew.gI().subQuantityItemsBag(player, CtVip, 1);
                }
                if (daNC != null) {
                    InventoryServiceNew.gI().subQuantityItemsBag(player, daNC, 1);
                    ;
                }
                if (daMM != null) {
                    InventoryServiceNew.gI().subQuantityItemsBag(player, daMM, 1);
                }

                InventoryServiceNew.gI().sendItemBags(player);
                Service.getInstance().sendMoney(player);
                reOpenItemCombine(player);

            } else {
                sendEffectFailCombine(player);
                if (mTS != null) {
                    InventoryServiceNew.gI().subQuantityItemsBag(player, mTS, 99);
                }
                if (CtVip != null) {
                    InventoryServiceNew.gI().subQuantityItemsBag(player, CtVip, 1);
                }
                if (daNC != null) {
                    InventoryServiceNew.gI().subQuantityItemsBag(player, daNC, 1);
                    ;
                }
                if (daMM != null) {
                    InventoryServiceNew.gI().subQuantityItemsBag(player, daMM, 1);
                }

                InventoryServiceNew.gI().sendItemBags(player);
                Service.getInstance().sendMoney(player);
                reOpenItemCombine(player);
            }

        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

    private void epSaoTrangBi(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {
            int ruby = player.combineNew.rubyCombine;
            if (player.inventory.ruby < ruby) {
                Service.gI().sendThongBao(player, "Không đủ ngọc hồng để thực hiện");
                return;
            }
            Item trangBi = null;
            Item daPhaLe = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (isTrangBiPhaLeHoa(item)) {
                    trangBi = item;
                } else if (isDaPhaLe(item)) {
                    daPhaLe = item;
                }
            }
            int star = 0; // sao pha lê đã ép
            int starEmpty = 0; // lỗ sao pha lê
            if (trangBi != null && daPhaLe != null) {
                Item.ItemOption optionStar = null;
                for (Item.ItemOption io : trangBi.itemOptions) {
                    if (io.optionTemplate.id == 102) {
                        star = io.param;
                        optionStar = io;
                    } else if (io.optionTemplate.id == 107) {
                        starEmpty = io.param;
                    }
                }
                if (star < starEmpty) {
                    player.inventory.ruby -= ruby;
                    int optionId = getOptionDaPhaLe(daPhaLe);
                    int param = getParamDaPhaLe(daPhaLe);
                    Item.ItemOption option = null;
                    for (Item.ItemOption io : trangBi.itemOptions) {
                        if (io.optionTemplate.id == optionId) {
                            option = io;
                            break;
                        }
                    }
                    if (option != null) {
                        option.param += param;
                    } else {
                        trangBi.itemOptions.add(new Item.ItemOption(optionId, param));
                    }
                    if (optionStar != null) {
                        optionStar.param++;
                    } else {
                        trangBi.itemOptions.add(new Item.ItemOption(102, 1));
                    }

                    InventoryServiceNew.gI().subQuantityItemsBag(player, daPhaLe, 1);
                    sendEffectSuccessCombine(player);
                }
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
    }

    private void phaLeHoaTrangBi(Player player, int type) {
        if (!player.combineNew.itemsCombine.isEmpty()) {
            long gold = player.combineNew.goldCombine;
            int gem = player.combineNew.gemCombine;
            while (player.combineNew.dapdo >= 0) {
                if (player.inventory.gold < gold) {
                    Service.gI().sendThongBao(player, "Không Đủ Vàng Để Thực Hiện");
                    break;
                }
                Item item = player.combineNew.itemsCombine.get(0);
                if (isTrangBiPhaLeHoa(item)) {
                    int star = 0;
                    Item.ItemOption optionStar = null;
                    for (Item.ItemOption io : item.itemOptions) {
                        if (io.optionTemplate.id == 107) {
                            star = io.param;
                            optionStar = io;
                            break;
                        }
                    }
                    if (star < MAX_STAR_ITEM) {
                        player.inventory.gold -= gold;
                        byte ratio = (optionStar != null && optionStar.param > 6) ? (byte) 3 : 1;
                        if (Util.isTrue(player.combineNew.ratioCombine, 100 * ratio)) {
                            if (optionStar == null) {
                                item.itemOptions.add(new Item.ItemOption(107, 1));
                            } else {
                                optionStar.param++;
                            }
                            sendEffectSuccessCombine(player);
                            if (optionStar != null && optionStar.param >= 7) {
                                ServerNotify.gI().notify("Chúc mừng " + player.name + " vừa pha lê hóa "
                                        + "thành công " + item.template.name + " lên " + optionStar.param
                                        + " sao pha lê");
                            }
                            break;
                        } else {
                            sendEffectFailCombine(player);
                        }
                    }
                    player.combineNew.dapdo -= 1;
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.gI().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
        if (type == 1) {
            if (player.combineNew.itemsCombine.isEmpty()) {
                return;
            }
            long gold = player.combineNew.goldCombine;
            int gem = player.combineNew.gemCombine;
            if (player.inventory.gold < gold) {
                Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện");
                return;
            } else if (player.inventory.gem < gem) {
                Service.gI().sendThongBao(player, "Không đủ ngọc để thực hiện");
                return;
            }
            Item item = player.combineNew.itemsCombine.get(0);
            if (!isTrangBiPhaLeHoa(item)) {
                return;
            }
            int star = 0;
            Item.ItemOption optionStar = null;
            for (Item.ItemOption io : item.itemOptions) {
                if (io.optionTemplate.id == 107) {
                    star = io.param;
                    optionStar = io;
                    break;
                }
            }
            boolean flag = false;
            for (int i = 0; i < 100; i++) {
                gold = player.combineNew.goldCombine;
                gem = player.combineNew.gemCombine;
                if (player.inventory.gold < gold) {
                    Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện");
                    break;
                } else if (player.inventory.gem < gem) {
                    Service.gI().sendThongBao(player, "Không đủ ngọc để thực hiện");
                    break;
                }
                if (star < MAX_STAR_ITEM) {
                    player.inventory.gold -= gold;
                    player.inventory.gem -= gem;
                    byte ratio = (optionStar != null && optionStar.param > 6) ? (byte) 3 : 1;
                    if (Util.isTrue(player.combineNew.ratioCombine, 100 * ratio)) {
                        if (optionStar == null) {
                            item.itemOptions.add(new Item.ItemOption(107, 1));
                        } else {
                            optionStar.param++;
                        }
                        sendEffectSuccessCombine(player);
                        flag = true;
                        if (optionStar != null && optionStar.param >= 7) {
                            // ServerNotify.gI().notify("Chúc mừng " + player.name + " vừa pha lê hóa "
                            // + "thành công " + item.template.name + " lên " + optionStar.param + " sao pha
                            // lê");
                        }
                        Service.gI().sendThongBao(player, "Nâng cấp thành công sau " + (i + 1) + " lần");
                        break;
                    }
                }
            }
            if (!flag) {
                sendEffectFailCombine(player);
            }
            InventoryServiceNew.gI().sendItemBags(player);
            Service.gI().sendMoney(player);
            reOpenItemCombine(player);
        }
    }

    private void renKiemZ(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {
            int gold = player.combineNew.goldCombine;
            if (player.inventory.gold < gold) {
                Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện");
                return;
            }

            int gem = player.combineNew.gemCombine;
            if (player.inventory.gem < gem) {
                Service.gI().sendThongBao(player, "Không đủ ngọc để thực hiện");
                return;
            }

            Item manhKiemZ = null;
            Item quangKiemZ = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (item.template.id >= 555 && item.template.id <= 567) {
                    manhKiemZ = item;
                } else if (item.template.id == 1995) {
                    quangKiemZ = item;
                }
            }

            if (manhKiemZ != null && quangKiemZ != null && quangKiemZ.quantity >= 1) {
                // Item findItemBag = InventoryServiceNew.gI().findItemBag(player, 1200);
                // //Nguyên liệu
                // if (findItemBag != null) {
                // Service.gI().sendThongBao(player, "Con đã có Kiếm Z trong hành trang rồi,
                // không thể rèn nữa.");
                // return;
                // }
                player.inventory.gold -= gold;
                player.inventory.gem -= gem;
                InventoryServiceNew.gI().subQuantityItemsBag(player, quangKiemZ, 99);
                if (Util.isTrue(player.combineNew.ratioCombine, 100)) {
                    manhKiemZ.template = ItemService.gI().getTemplate(1996);
                    manhKiemZ.itemOptions.clear();
                    Random rand = new Random();
                    int ratioCombine = rand.nextInt(60) + 1;
                    int level = 0;
                    if (ratioCombine <= 40) {
                        level = 1 + rand.nextInt(4);
                    } else if (ratioCombine <= 70) {
                        level = 5 + rand.nextInt(4);
                    } else if (ratioCombine <= 90) {
                        level = 9 + rand.nextInt(4);
                    } else if (ratioCombine <= 95) {
                        level = 13 + rand.nextInt(3);
                    } else {
                        level = 16;
                    }
                    manhKiemZ.itemOptions.add(new Item.ItemOption(0, level * 200 + 10000));
                    manhKiemZ.itemOptions.add(new Item.ItemOption(49, level * 1 + 20));
                    manhKiemZ.itemOptions.add(new Item.ItemOption(14, level));
                    manhKiemZ.itemOptions.add(new Item.ItemOption(97, level));
                    manhKiemZ.itemOptions.add(new Item.ItemOption(30, 0));
                    manhKiemZ.itemOptions.add(new Item.ItemOption(72, level));
                    sendEffectSuccessCombine(player);
                } else {
                    sendEffectFailCombine(player);
                }
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
    }

    private void nhapNgocRong(Player player) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            if (!player.combineNew.itemsCombine.isEmpty()) {
                Item item = player.combineNew.itemsCombine.get(0);
                if (item != null && item.isNotNullItem() && (item.template.id > 14 && item.template.id <= 20)
                        && item.quantity >= 7) {
                    Item nr = ItemService.gI().createNewItem((short) (item.template.id - 1));
                    sendEffectSuccessCombine(player);
                    InventoryServiceNew.gI().addItemBag(player, nr);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, item, 7);
                    InventoryServiceNew.gI().sendItemBags(player);
                    reOpenItemCombine(player);
                    // sendEffectCombineDB(player, item.template.iconID);
                }
            }
        }
    }

    private void nangCapVatPham(Player player) {
        if (player.combineNew.itemsCombine.size() >= 2 && player.combineNew.itemsCombine.size() < 4) {
            if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.type < 5)
                    .count() != 1) {
                return;
            }
            if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.type == 14)
                    .count() != 1) {
                return;
            }
            if (player.combineNew.itemsCombine.size() == 3 && player.combineNew.itemsCombine.stream()
                    .filter(item -> item.isNotNullItem() && item.template.id == 987).count() != 1) {
                return;// admin
            }
            Item itemDo = null;
            Item itemDNC = null;
            Item itemDBV = null;
            for (int j = 0; j < player.combineNew.itemsCombine.size(); j++) {
                if (player.combineNew.itemsCombine.get(j).isNotNullItem()) {
                    if (player.combineNew.itemsCombine.size() == 3
                            && player.combineNew.itemsCombine.get(j).template.id == 987) {
                        itemDBV = player.combineNew.itemsCombine.get(j);
                        continue;
                    }
                    if (player.combineNew.itemsCombine.get(j).template.type < 5) {
                        itemDo = player.combineNew.itemsCombine.get(j);
                    } else {
                        itemDNC = player.combineNew.itemsCombine.get(j);
                    }
                }
            }
            if (isCoupleItemNangCapCheck(itemDo, itemDNC)) {
                int countDaNangCap = player.combineNew.countDaNangCap;
                int gold = player.combineNew.goldCombine;
                short countDaBaoVe = player.combineNew.countDaBaoVe;
                if (player.inventory.gold < gold) {
                    Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện");
                    return;
                }

                if (itemDNC.quantity < countDaNangCap) {
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 3) {
                    if (Objects.isNull(itemDBV)) {
                        return;
                    }
                    if (itemDBV.quantity < countDaBaoVe) {
                        return;
                    }
                }

                int level = 0;
                Item.ItemOption optionLevel = null;
                for (Item.ItemOption io : itemDo.itemOptions) {
                    if (io.optionTemplate.id == 72) {
                        level = io.param;
                        optionLevel = io;
                        break;
                    }
                }
                if (level < MAX_LEVEL_ITEM) {
                    player.inventory.gold -= gold;
                    Item.ItemOption option = null;
                    Item.ItemOption option2 = null;
                    for (Item.ItemOption io : itemDo.itemOptions) {
                        if (io.optionTemplate.id == 47
                                || io.optionTemplate.id == 6
                                || io.optionTemplate.id == 0
                                || io.optionTemplate.id == 7
                                || io.optionTemplate.id == 14
                                || io.optionTemplate.id == 22
                                || io.optionTemplate.id == 23) {
                            option = io;
                        } else if (io.optionTemplate.id == 27
                                || io.optionTemplate.id == 28) {
                            option2 = io;
                        }
                    }
                    if (Util.isTrue(player.combineNew.ratioCombine, 100)) {
                        option.param += (option.param * 10 / 100);
                        if (option2 != null) {
                            option2.param += (option2.param * 10 / 100);
                        }
                        if (optionLevel == null) {
                            itemDo.itemOptions.add(new Item.ItemOption(72, 1));
                        } else {
                            optionLevel.param++;
                        }
                        if (optionLevel != null && optionLevel.param >= 5) {
                            ServerNotify.gI().notify("Chúc mừng cư dân " + player.name + " vừa nâng cấp "
                                    + "thành công " + itemDo.template.name + " lên +" + optionLevel.param);
                        }
                        sendEffectSuccessCombine(player);
                    } else {
                        if ((level == 2 || level == 4 || level == 6) && (player.combineNew.itemsCombine.size() != 3)) {
                            option.param -= (option.param * 10 / 100);
                            if (option2 != null) {
                                option2.param -= (option2.param * 10 / 100);
                            }
                            optionLevel.param--;
                        }
                        sendEffectFailCombine(player);
                    }
                    if (player.combineNew.itemsCombine.size() == 3) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, itemDBV, countDaBaoVe);
                    }
                    InventoryServiceNew.gI().subQuantityItemsBag(player, itemDNC, player.combineNew.countDaNangCap);
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.gI().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
    }

    // --------------------------------------------------------------------------
    /**
     * r
     * Hiệu ứng mở item
     *
     * @param player
     */
    public void sendEffectOpenItem(Player player, short icon1, short icon2) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(OPEN_ITEM);
            msg.writer().writeShort(icon1);
            msg.writer().writeShort(icon2);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hiệu ứng đập đồ thành công
     *
     * @param player
     */
    public void sendEffectSuccessCombine(Player player) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(COMBINE_SUCCESS);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Hiệu ứng đập đồ thất bại
     *
     * @param player
     */
    public void sendEffectFailCombine(Player player) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(COMBINE_FAIL);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gửi lại danh sách đồ trong tab combine
     *
     * @param player
     */
    public void reOpenItemCombine(Player player) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(REOPEN_TAB_COMBINE);
            msg.writer().writeByte(player.combineNew.itemsCombine.size());
            for (Item it : player.combineNew.itemsCombine) {
                for (int j = 0; j < player.inventory.itemsBag.size(); j++) {
                    if (it == player.inventory.itemsBag.get(j)) {
                        msg.writer().writeByte(j);
                    }
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hiệu ứng ghép ngọc rồng
     *
     * @param player
     * @param icon
     */
    private void sendEffectCombineDB(Player player, short icon) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(COMBINE_DRAGON_BALL);
            msg.writer().writeShort(icon);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --------------------------------------------------------------------------Ratio,
    // cost combine
    private int getGoldPhaLeHoa(int star) {
        switch (star) {
            case 0:
                return 150_000;
            case 1:
                return 250_000;
            case 2:
                return 350_000;
            case 3:
                return 450_000;
            case 4:
                return 600_000;
            case 5:
                return 700_000;
            case 6:
                return 1_200_000;
            case 7:
                return 2_500_000;
            case 8:
                return 4_500_000;
        }
        return 0;
    }

    private int getDiemNangcapChanmenh(int star) {
        switch (star) {
            case 0:
                return 10;
            case 1:
                return 10;
            case 2:
                return 10;
            case 3:
                return 10;
            case 4:
                return 10;
            case 5:
                return 10;
            case 6:
                return 10;
            case 7:
                return 10;
        }
        return 0;
    }

    private int getDaNangcapChanmenh(int star) {
        switch (star) {
            case 0:
                return 20;
            case 1:
                return 20;
            case 2:
                return 20;
            case 3:
                return 20;
            case 4:
                return 20;
            case 5:
                return 20;
            case 6:
                return 20;
            case 7:
                return 20;
        }
        return 0;
    }

    private float getTiLeNangcapChanmenh(int star) {
        switch (star) {
            case 0:
                return 40;
            case 1:
                return 30;
            case 2:
                return 25;
            case 3:
                return 10;
            case 4:
                return 8;
            case 5:
                return 5f;
            case 6:
                return 3f;
            case 7:
                return 3f;
        }
        return 0;
    }

    private float getRatioPhaLeHoa(int star) { // tile dap do chi hat mit
        switch (star) {
            case 0:
                return 70f;// 5tr vang
            case 1:
                return 50f; // 10tr
            case 2:
                return 30f; // 20tr
            case 3:
                return 20f; // 40tr
            case 4:
                return 10f; // 50tr
            case 5:
                return 5f; // 60tr
            case 6:
                return 4f; // 70tr
            case 7:
                return 2f; // 80tr
            case 8:
                return 1f; // 100tr

        }

        return 0;
    }

    private float getRatioNangmeo(int lvmeo) { // tile nang khi chi hat mit
        switch (lvmeo) {
            case 1:
                return 20f;
            // case 2:
            // return 30f;
            // case 3:
            // return 20f;
            // case 4:
            // return 10f;
            // case 5:
            // return 10f;
            // case 6:
            // return 5f;
            // case 7:
            // return 2f;
        }

        return 0;
    }

    private float getRatioNangkhi(int lvkhi) { // tile nang khi chi hat mit
        switch (lvkhi) {
            case 1:
                return 100f;
            case 2:
                return 30f;
            case 3:
                return 20f;
            case 4:
                return 10f;
            case 5:
                return 10f;
            case 6:
                return 5f;
            case 7:
                return 2f;
        }

        return 0;
    }

    private float getRatioNangluffy(int lvluffy) { // tile nang khi chi hat mit
        switch (lvluffy) {
            case 1:
                return 70f;
            case 2:
                return 30f;
            case 3:
                return 30f;
            case 4:
                return 30f;
            case 5:
                return 30f;
            case 6:
                return 25;
            case 7:
                return 20f;
        }

        return 0;
    }

    private float getRationangbt(int lvbt) { // tỉ lệ nâng cấp bông tai c1 và c2
        switch (lvbt) {
            case 1:
                return 70f;
            case 2:
                return 50f;
            case 3:
                return 30f;
        }
        return 0;
    }

    private int getGoldnangbt(int lvbt) {
        return GOLD_BONG_TAI2 + 200000000 * lvbt;
    }

    private int getgemdnangbt(int lvbt) {
        return GEM_BONG_TAI2;
    }

    private int getRubydnangbt(int lvbt) {
        return RUBY_BONG_TAI2 + 2000 * lvbt;
    }

    private int getcountmvbtnangbt(int lvbt) {// so luong mảnh vỡ bông tai cần nâng cấp
        switch (lvbt) {
            case 1:
                return 99;
            case 2:
                return 999;
            case 3:
                return 9999;
        }
        return 0;
    }

    private boolean checkbongtai(Item item) {
        if (item.template.id == 454 || item.template.id == 921 || item.template.id == 1155
                || item.template.id == 1156) {
            return true;
        }
        return false;
    }

    private int lvbt(Item bongtai) {
        switch (bongtai.template.id) {
            case 454:
                return 1;
            case 921:
                return 2;
            case 1155:
                return 3;
            case 1156:
                return 4;
        }

        return 0;

    }

    private int getGoldnangmeo(int lvmeo) {
        return GOLD_NANG_KHI + 100000000 * lvmeo;
    }

    private int getRubydnangmeo(int lvmeo) {
        return RUBY_NANG_KHI + 25000;
    }

    private int getcountdnsnangmeo(int lvmeo) {
        return 10 + 10 * lvmeo;
    }

    private boolean checkctmeo(Item item) {
        if ((item.template.id >= 1411 && item.template.id <= 1412)) {
            return true;
        }
        return false;
    }

    private int getGoldnangkhi(int lvkhi) {
        return GOLD_NANG_KHI + 100000000 * lvkhi;
    }

    private int getRubydnangkhi(int lvkhi) {
        return RUBY_NANG_KHI + 2000 * lvkhi;
    }

    private int getcountdnsnangkhi(int lvkhi) {
        return 10 + 7 * lvkhi;
    }

    private boolean checkctkhi(Item item) {
        if ((item.template.id >= 2055 && item.template.id <= 2062)) {
            return true;
        }
        return false;
    }

    private int getGoldnangluffy(int lvluffy) {
        return GOLD_NANG_KHI + 100000000 * lvluffy;
    }

    private int getRubydnangluffy(int lvluffy) {
        return RUBY_NANG_LUFFY * 2;
    }

    private int getcountdnsnangluffy(int lvluffy) {
        return 10 + 15 * lvluffy;
    }

    private boolean checkctluffy(Item item) {
        if ((item.template.id >= 2068 && item.template.id <= 2075)) {
            return true;
        }
        return false;
    }

    private int lvkhi(Item ctkhi) {
        switch (ctkhi.template.id) {
            case 2055:
                return 1;
            case 2056:
                return 2;
            case 2057:
                return 3;
            case 2058:
                return 4;
            case 2059:
                return 5;
            case 2060:
                return 6;
            case 2061:
                return 7;
        }

        return 0;

    }

    private short getidctkhisaukhilencap(int lvkhicu) {
        switch (lvkhicu) {
            case 1:
                return 2056;
            case 2:
                return 2057;
            case 3:
                return 2058;
            case 4:
                return 2059;
            case 5:
                return 2060;
            case 6:
                return 2061;
            case 7:
                return 2062;
        }
        return 0;
    }

    private int lvmeo(Item ctmeo) {
        switch (ctmeo.template.id) {
            case 1411:
                return 1;
        }

        return 0;

    }

    private short getidctmeosaukhilencap(int lvmeocu) {
        switch (lvmeocu) {
            case 1:
                return 1412;
        }
        return 0;
    }

    private int lvluffy(Item ctluffy) {
        switch (ctluffy.template.id) {
            case 2068:
                return 1;
            case 2069:
                return 2;
            case 2070:
                return 3;
            case 2071:
                return 4;
            case 2072:
                return 5;
            case 2073:
                return 6;
            case 2074:
                return 7;
        }

        return 0;

    }

    private short getidctluffysaukhilencap(int lvluffycu) {
        switch (lvluffycu) {
            case 1:
                return 2069;
            case 2:
                return 2070;
            case 3:
                return 2071;
            case 4:
                return 2072;
            case 5:
                return 2073;
            case 6:
                return 2074;
            case 7:
                return 2075;
        }
        return 0;
    }

    private int getGemPhaLeHoa(int star) {
        switch (star) {
            case 0:
                return 10;
            case 1:
                return 20;
            case 2:
                return 30;
            case 3:
                return 40;
            case 4:
                return 50;
            case 5:
                return 60;
            case 6:
                return 70;
            case 7:
                return 80;
            case 8:
                return 90;
        }
        return 0;
    }

    private int getGemEpSao(int star) {

        return 0;
    }

    private double getTileNangCapDo(int level) {
        switch (level) {
            case 0:
                return 60;
            case 1:
                return 30;
            case 2:
                return 20;
            case 3:
                return 10;
            case 4:
                return 5;
            case 5:
                return 2;
            case 6:
                return 1;
            case 7: // 7 sao
                return 0.5;
            case 8:
                return 0.3;
        }
        return 0;
    }

    private int getCountDaNangCapDo(int level) {
        switch (level) {
            case 0:
                return 3;
            case 1:
                return 7;
            case 2:
                return 11;
            case 3:
                return 17;
            case 4:
                return 23;
            case 5:
                return 35;
            case 6:
                return 50;
            case 7:
                return 70;
            case 8:
                return 70;
            case 9:
                return 70;
            case 10:
                return 70;
            case 11:
                return 70;
            case 12:
                return 70;
        }
        return 0;
    }

    private int getCountDaBaoVe(int level) {
        return level + 1;
    }

    private int getGoldNangCapDo(int level) {
        switch (level) {
            case 0:
                return 20000;
            case 1:
                return 45000;
            case 2:
                return 80000;
            case 3:
                return 95000;
            case 4:
                return 100000;
            case 5:
                return 120000;
            case 6:
                return 140000;
            case 7:
                return 150000;
            case 8:
                return 250000;
        }
        return 0;
    }

    // --------------------------------------------------------------------------check
    private boolean isCoupleItemNangCap(Item item1, Item item2) {
        Item trangBi = null;
        Item daNangCap = null;
        if (item1 != null && item1.isNotNullItem()) {
            if (item1.template.type < 5) {
                trangBi = item1;
            } else if (item1.template.type == 14) {
                daNangCap = item1;
            }
        }
        if (item2 != null && item2.isNotNullItem()) {
            if (item2.template.type < 5) {
                trangBi = item2;
            } else if (item2.template.type == 14) {
                daNangCap = item2;
            }
        }
        if (trangBi != null && daNangCap != null) {
            if (trangBi.template.type == 0 && daNangCap.template.id == 223) {
                return true;
            } else if (trangBi.template.type == 1 && daNangCap.template.id == 222) {
                return true;
            } else if (trangBi.template.type == 2 && daNangCap.template.id == 224) {
                return true;
            } else if (trangBi.template.type == 3 && daNangCap.template.id == 221) {
                return true;
            } else if (trangBi.template.type == 4 && daNangCap.template.id == 220) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isCoupleItemNangCapCheck(Item trangBi, Item daNangCap) {
        if (trangBi != null && daNangCap != null) {
            if (trangBi.template.type == 0 && daNangCap.template.id == 223) {
                return true;
            } else if (trangBi.template.type == 1 && daNangCap.template.id == 222) {
                return true;
            } else if (trangBi.template.type == 2 && daNangCap.template.id == 224) {
                return true;
            } else if (trangBi.template.type == 3 && daNangCap.template.id == 221) {
                return true;
            } else if (trangBi.template.type == 4 && daNangCap.template.id == 220) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean issachTuyetKy(Item item) {
        if (item != null && item.isNotNullItem()) {
            if (item.template.type == 77) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean checkHaveOption(Item item, int viTriOption, int idOption) {
        if (item != null && item.isNotNullItem()) {
            if (item.itemOptions.get(viTriOption).optionTemplate.id == idOption) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isDaPhaLe(Item item) {
        return item != null && item.template != null
                && (item.template.type == 30 || (item.template.id >= 14 && item.template.id <= 20));
    }

    private boolean isTrangBiPhaLeHoa(Item item) {
        if (item != null && item.isNotNullItem()) {
            if (item.template.type < 5 || item.template.type == 32 || item.template.type == 11) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private int getParamDaPhaLe(Item daPhaLe) {
        if (daPhaLe.template.type == 30) {
            return daPhaLe.itemOptions.get(0).param;
        }
        switch (daPhaLe.template.id) {
            case 20:
                return 5; // +5%hp
            case 19:
                return 5; // +5%ki
            case 18:
                return 5; // +5%hp/30s
            case 17:
                return 5; // +5%ki/30s
            case 16:
                return 3; // +3%sđ
            case 15:
                return 2; // +2%giáp
            case 14:
                return 5; // +5%né đòn
            default:
                return -1;
        }
    }

    private int getOptionDaPhaLe(Item daPhaLe) {
        if (daPhaLe.template.type == 30) {
            return daPhaLe.itemOptions.get(0).optionTemplate.id;
        }
        switch (daPhaLe.template.id) {
            case 20:
                return 77;
            case 19:
                return 103;
            case 18:
                return 80;
            case 17:
                return 81;
            case 16:
                return 50;
            case 15:
                return 94;
            case 14:
                return 108;
            default:
                return -1;
        }
    }

    /**
     * Trả về id item c0
     *
     * @param gender
     * @param type
     * @return
     */
    private int getTempIdItemC0(int gender, int type) {
        if (type == 4) {
            return 12;
        }
        switch (gender) {
            case 0:
                switch (type) {
                    case 0:
                        return 0;
                    case 1:
                        return 6;
                    case 2:
                        return 21;
                    case 3:
                        return 27;
                }
                break;
            case 1:
                switch (type) {
                    case 0:
                        return 1;
                    case 1:
                        return 7;
                    case 2:
                        return 22;
                    case 3:
                        return 28;
                }
                break;
            case 2:
                switch (type) {
                    case 0:
                        return 2;
                    case 1:
                        return 8;
                    case 2:
                        return 23;
                    case 3:
                        return 29;
                }
                break;
        }
        return -1;
    }

    // Trả về tên đồ c0
    private String getNameItemC0(int gender, int type) {
        if (type == 4) {
            return "Rada cấp 1";
        }
        switch (gender) {
            case 0:
                switch (type) {
                    case 0:
                        return "Áo vải 3 lỗ";
                    case 1:
                        return "Quần vải đen";
                    case 2:
                        return "Găng thun đen";
                    case 3:
                        return "Giầy nhựa";
                }
                break;
            case 1:
                switch (type) {
                    case 0:
                        return "Áo sợi len";
                    case 1:
                        return "Quần sợi len";
                    case 2:
                        return "Găng sợi len";
                    case 3:
                        return "Giầy sợi len";
                }
                break;
            case 2:
                switch (type) {
                    case 0:
                        return "Áo vải thô";
                    case 1:
                        return "Quần vải thô";
                    case 2:
                        return "Găng vải thô";
                    case 3:
                        return "Giầy vải thô";
                }
                break;
        }
        return "";
    }

    // --------------------------------------------------------------------------Text
    // tab combine
    private String getTextTopTabCombine(int type) {
        switch (type) {
            case EP_SAO_TRANG_BI:
                return "Ta Sẽ Khai Triển Phép";
            case PHA_LE_HOA_TRANG_BI:
                return "Ta sẽ phù phép\ncho trang bị của ngươi\ntrở thành trang bị pha lê";
            case NHAP_NGOC_RONG:
                return "Ta sẽ phù phép\ncho 7 viên Ngọc Rồng\nthành 1 viên Ngọc Rồng cấp cao";
            case CHE_TAO_PHUOC:
                return "Xưởng Chế Tạo!";
            case REN_KIEM_Z:
                return "Ta sẽ rèn\ncho con thanh\nKiếm Z này";
            case NANG_CAP_VAT_PHAM:
                return "Ta Sẽ Khai Triển Phép";
            case PHAN_RA_DO_THAN_LINH:
                return "Ta sẽ phân rã \n  trang bị của người thành điểm!";
            case NANG_CAP_DO_TS:
                return "Ta sẽ nâng cấp \n  trang bị của người thành\n đồ thiên sứ!";
            case NANG_CAP_SKH_VIP:
                return "Ngọc Rồng KuRoKo\nNâp Cấp Trang Bị\n [ SET KÍCH HOẠT VIP ]";
            case NANG_CAP_BONG_TAI:
                return "Ta sẽ phù phép\ncho bông tai Porata của ngươi\ntiến hóa thêm 1 cấp";
            case MO_CHI_SO_BONG_TAI:
                return "Ta sẽ phù phép\ncho bông tai Porata của ngươi\ncó 1 chỉ số ngẫu nhiên";
            case MO_CHI_SO_Chien_Linh:
                return "Ta sẽ phù phép\ncho Chiến Linh của ngươi\ncó 1 chỉ số ngẫu nhiên";
            case NANG_CAP_KHI:
                return "Ta sẽ phù phép\ncho Cải trang của ngươi\nTăng một cấp!!";
            case NANG_CAP_MEO:
                return "Ta sẽ giúp ngươi cho mèo ăn\ncho mèo của ngươi\nTăng một cấp!!";
            case NANG_CAP_LUFFY:
                return "Ta sẽ Giúp \ncho Cải trang Luffy của ngươi\nthức tỉnh!!";
            case Nang_Chien_Linh:
                return "Ta sẽ biến linh thú của ngươi \nThành Chiến Linh!!!";
            case NANG_CAP_DO_KICH_HOAT:
                return "Ta sẽ phù phép\ntrang bị kích hoạt Thiên sứ";
            case NANG_CAP_DO_KICH_HOAT_THUONG:
                return "Ngọc Rồng KuRoKo\nNâng Cấp Trang Bị\n [ SET KÍCH HOẠT ]";
            case CHE_TAO_TRANG_BI_TS:
                return "Chế tạo\ntrang bị thiên sứ";
            case GIAM_DINH_SACH:
                return "Ta sẽ phù phép\ngiám định sách đó cho ngươi";
            case TAY_SACH:
                return "Ta sẽ phù phép\ntẩy sách đó cho ngươi";
            case NANG_CAP_SACH_TUYET_KY:
                return "Ta sẽ phù phép\nnâng cấp Sách Tuyệt Kỹ cho ngươi";
            case PHUC_HOI_SACH:
                return "Ta sẽ phù phép\nphục hồi sách cho ngươi";
            case PHAN_RA_SACH:
                return "Ta sẽ phù phép\nphân rã sách cho ngươi";
            case DOI_DIEM:
                return "Thức Ăn";
            case NANG_CAP_CHAN_MENH:
                return "Ta sẽ Nâng cấp\nChân Mệnh của ngươi\ncao hơn một bậc";
            case NANG_CAP_SARINGAN:
                return "Ta sẽ Nâng cấp\nSaringan của ngươi\ncao hơn một bậc";
            case COMBINE_TAN_DAN_FRAGMENT:
                return "Ta sẽ ghép mảnh tàn đan\nthành đan hoàn chỉnh";
            case UPGRADE_TUTIEN_DAN:
                return "Ta sẽ nâng cấp đan\ncho ngươi";
            default:
                return "";
        }
    }

    private String getTextInfoTabCombine(int type) {
        String congThucName = Manager.ITEM_TEMPLATES.get(1804) != null ? Manager.ITEM_TEMPLATES.get(1804).name
                : "Công Thức Đan Dược";
        switch (type) {
            case EP_SAO_TRANG_BI:
                return "Chọn Trang Bị\n(Áo, Quần, Găng, Giày Hoặc Rađa) Có Ô Đặt Sao Pha Lê\nChọn Loại Sao Pha Lê\n"
                        + "Sau Đó Chọn 'Nâng Cấp'";
            case PHA_LE_HOA_TRANG_BI:
                return "Chọn Trang Bị\n(Áo, Quần, Găng, Giày Hoặc Rađa)\nSau Đó Chọn 'Nâng Cấp'";
            case NHAP_NGOC_RONG:
                return "Vào Hành Trang\nChọn 7 Viên Ngọc Cùng Sao\nSau Đó Chọn 'Làm Phép'";
            case CHE_TAO_PHUOC:
                return "Chọn Các Vật Phẩm Cần Chế Tạo!\n"
                        + "-Bảng Chế Tạo-\n"
                        + "5 Ngọc Ma = Đại Đao\n"
                        + "5 Ngọc Huyết = Huyết Đao\n";
            // + "200 Ngọc Độc = Kiếm Độc\n"
            // + "200 Ngọc Bóng Đêm = Dao Bóng Đêm\n"
            // + "200 Ngọc Quỷ Lửa = Ma Kiếm\n"
            // + "200 Ngọc Thiên Phong = Phong Kiếm\n"
            // + "200 Ngọc Đại Binh = Đại Thần Kiếm"

            case NANG_CAP_VAT_PHAM:
                return "Vào Túi Đồ\nChọn Trang Bị\n(Áo, Quần, Găng, Giày Hoặc Rađa)\nChọn Loại Đá Để Nâng Cấp\n (Đá Saphia, Titan, Lục Bảo, Ruby, Thạch Anh)\n"
                        + "Sau Đó Chọn 'Nâng Cấp'";
            case PHAN_RA_DO_THAN_LINH:
                return "Vào Hành Trang\nChọn Trang Bị\n(Áo, Quần, Găng, Giày Hoặc Rađa)\nChọn Loại Đá Để Phân Rã\n"
                        + "Sau Đó Chọn 'Phân Rã'";
            case NANG_CAP_DO_TS:
                return "Vào Hành Trang\nChọn 2 Trang Bị Hủy Diệt Bất Kì\nKèm 1 Món Đồ Thần Linh\n Và 5 Mảnh Thiên Sứ\n "
                        + "Sẽ Cho Ra Đồ Thiên Sứ Từ 0-15% Chỉ Số\nSau Đó Chọn 'Nâng Cấp'";
            case NANG_CAP_SKH_VIP:
                return "[ NÂNG CẤP TRANG BỊ KÍCH HOẠT VIP ]\n"
                        + "\nYÊU CẦU\n+ 1 Trang Bị Hủy Diệt Bất Kì\nChọn Tiếp Ngẫu Nhiên 2 Món Thần Linh\n+ 500tr Vàng\n"
                        + "LƯU Ý : Đồ Kích Hoạt Vip\nSẽ Ra Cùng Loại Và Hành Tinh \n Với Đồ Kích Hoạt Đã Chọn Ban Đầu!\n"
                        + "\n"
                        + "Sau Đó Chỉ Cần Chọn 'Nâng Cấp'";
            case NANG_CAP_BONG_TAI:
                return "Vào Hành Trang\nChọn Bông Tai Porata\nChọn Mảnh Bông Tai Để Nâng Cấp\n99 Mảnh Cho Bông Tai Cho Cấp 2\n999 Mảnh Cho Bông Tai Cho Cấp 3\n9999 Mảnh Cho Bông Tai Cho Cấp 4\nSau Đó Chọn 'Nâng Cấp'";
            case MO_CHI_SO_BONG_TAI:
                return "Vào Hành Trang\nChọn Bông Tai Porata\nChọn Mảnh Hồn Bông Tai Số Lượng 99 Cái\nVà Đá Xanh Lam Để Nâng Cấp\nSau Đó Chọn 'Nâng Cấp'";
            case MO_CHI_SO_Chien_Linh:
                return "Vào Hành Trang\nChọn Chiến Linh\nChọn Đá Ma Thuật Số Lượng 99 Cái\nVà x99 Hồn Thú Để Nâng Cấp\nSau Đó Chọn 'Nâng Cấp'";
            case NANG_CAP_KHI:
                return "Vào Hành Trang\nChọn Cải Little Girl \nChọn Đá Little Girl Để Nâng Cấp\nSau Đó Chọn 'Nâng Cấp'";
            case NANG_CAP_MEO:
                return "Vào Hành Trang\nChọn Mèo \nChọn Thức Ăn Cho Mèo Để Cho Mèo Ăn\nSau Đó Chọn 'Cho Ăn'";
            case NANG_CAP_LUFFY:
                return "Vào hành trang\nChọn Cải Luffy \nChọn Đá thức tỉnh để nâng cấp\nSau đó chọn 'Nâng cấp'";
            case Nang_Chien_Linh:
                return "Vào hành trang\nChọn Linh Thú \nChọn x10 Thăng tinh thạch để nâng cấp\nSau đó chọn 'Nâng cấp'";
            case NANG_CAP_DO_KICH_HOAT:
                return "Vào hành trang\nchọn 1 trang bị thiên sứ , 2 trang bị kích hoạt\n "
                        + " và 500tr vàng\n"
                        + "Sau đó chọn 'Nâng Cấp'";
            case NANG_CAP_DO_KICH_HOAT_THUONG:
                return "[ NÂNG CẤP TRANG BỊ KÍCH HOẠT ]\n" + "\nYÊU CẦU\n+ 2 món đồ Thần Linh\n+ 500tr vàng\n\n "
                        + "Lưu Ý : Đồ Kích Hoạt\nsẽ ra cùng loại với DTL ban đầu\nvà cùng hành tinh với bạn!\n" + "\n"
                        + "Sau đó chỉ cần chọn 'Nâng Cấp'";
            case REN_KIEM_Z:
                return "VChọn Kiếm Z\nChọn Quặng Z, số lượng\n99 cái\nSau đó chọn 'Rèn Kiếm'\n Ngẫu nhiên Kiếm Z cấp 1 đến cấp 16";
            case CHE_TAO_TRANG_BI_TS:
                return "Cần 1 công thức vip\n"
                        + "999 Mảnh trang bị tương ứng\n"
                        + "1 đá nâng cấp (tùy chọn)\n"
                        + "1 đá may mắn (tùy chọn)\n";
            case GIAM_DINH_SACH:
                return "Vào hành trang chọn\n1 sách cần giám định";
            case TAY_SACH:
                return "Vào hành trang chọn\n1 sách cần tẩy";
            case NANG_CAP_SACH_TUYET_KY:
                return "Vào hành trang chọn\nSách Tuyệt Kỹ 1 cần nâng cấp và 10 Kìm bấm giấy";
            case PHUC_HOI_SACH:
                return "Vào hành trang chọn\nCác Sách Tuyệt Kỹ cần phục hồi";
            case PHAN_RA_SACH:
                return "Vào hành trang chọn\n1 sách cần phân rã";
            case DOI_DIEM:
                return "Vào hành trang\nChọn x59 Thức Ăn\nSau đó chọn 'Nâng cấp'";
            case NANG_CAP_CHAN_MENH:
                return "Vào hành trang\nChọn Chân mệnh muốn nâng cấp\nChọn Đá Hoàng Kim\n"
                        + "Sau đó chọn 'Nâng cấp'\n"
                        + "Nếu cho thêm thỏi vàng vào sẽ giúp giảm số lượng đá Hoàng Kim và tăng thêm tỉ lệ\n\n"
                        + "Lưu ý: Khi Nâng cấp Thành công sẽ tăng thêm % chỉ số của cấp trước đó";
            case NANG_CAP_SARINGAN:
                return "Vào hành trang\nChọn Saringan muốn nâng cấp\nChọn Bí Thuật\n"
                        + "Sau đó chọn 'Nâng cấp'\n"
                        + "Nếu cho thêm thỏi vàng vào sẽ giúp giảm số lượng Bí Thuật và tăng thêm tỉ lệ\n\n"
                        + "Lưu ý: Khi Nâng cấp Thành công sẽ tăng thêm % chỉ số của cấp trước đó";
            case COMBINE_TAN_DAN_FRAGMENT:
                String fragmentName = Manager.ITEM_TEMPLATES.get(1805) != null ? Manager.ITEM_TEMPLATES.get(1805).name
                        : "Mảnh Tàn Đan";
                String resultDanName = Manager.ITEM_TEMPLATES.get(1806) != null ? Manager.ITEM_TEMPLATES.get(1806).name
                        : "Đan Tu Tiên";
                return "Vào hành trang\nChọn 99 " + fragmentName + "\nChọn 99 " + congThucName
                        + "\nSau đó chọn 'Ghép Đan'\n"
                        + "Chi phí: 100M Gold\n"
                        + "Tỉ lệ thành công: 100%\n\n"
                        + "Kết quả: 1 " + resultDanName;
            case UPGRADE_TUTIEN_DAN:
                return "Vào hành trang\nChọn đan Tu Tiên (bất kỳ cấp nào)\nChọn 99 " + congThucName + "\n"
                        + "Sau đó chọn 'Nâng Cấp'\n"
                        + "Chi phí: 100M Gold\n"
                        + "Tỉ lệ thành công: Giảm dần theo cấp\n\n"
                        + "Tất cả các cấp đều cần: x99 đan + x99 công thức";
            default:
                return "";
        }
    }

    /**
     * Lấy NPC theo loại combine
     */
    public Npc getNpcByType(int combineType) {
        switch (combineType) {
            case COMBINE_TAN_DAN_FRAGMENT:
            case UPGRADE_TUTIEN_DAN:
                return this.kaido; // Luyện Dược Sư cho Tu Tiên combine
            default:
                return this.baHatMit; // Default NPC
        }
    }
}
