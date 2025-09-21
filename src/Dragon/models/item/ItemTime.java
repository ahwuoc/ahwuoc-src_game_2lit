package Dragon.models.item;

import Dragon.models.item.Item.ItemOption;
import Dragon.models.player.NPoint;
import Dragon.models.player.Player;
import Dragon.services.InventoryServiceNew;
import Dragon.services.ItemService;
import Dragon.services.Service;
import Dragon.utils.Util;
import Dragon.services.ItemTimeService;
import Dragon.models.map.Zone;
import Dragon.server.ServerNotify;
import java.util.List;

public class ItemTime {

    //id item text
    public static final byte DOANH_TRAI = 0;
    public static final byte BAN_DO_KHO_BAU = 1;

    public static final byte TEXT_NHAN_BUA_MIEN_PHI = 3;
    public static final byte PHUOC_COUNT_NHIEM_VU = 4;

    public static final int TIME_ITEM = 600000;
    public static final int TIME_OPEN_POWER = 86400000;
    public static final byte KHI_GASS = 2;
    public static final int TIME_NUOC_MIA = 900000000;
    public static final int TIME_MAY_DO = 1800000;
    public static final int NAM_MUOI_PHUT = 3000000;
    public static final int BON_MUOI_PHUT = 2400000;
    public static final int BA_MUOI_PHUT = 1800000;
    public static final int TIME_MAY_DO2 = 1800000;
    public static final int TIME_EAT_MEAL = 600000;
    public static final int TIME_DUOI_KHI = 300000;
    public static final int TIME_TRUNG_THU_10P = 600000;
    public static final int TIME_TRUNG_THU_30P = 1800000;
    public static final byte CAU_CA = 60;
    private Player player;

    public boolean isUseBoHuyet;
    public boolean isUseBoKhi;
    public boolean isUseGiapXen;
    public boolean isUseCuongNo;
    public boolean isUseAnDanh;
    public boolean isUseBoHuyet2;
    public boolean isUseBoKhi2;
    public boolean isUseGiapXen2;
    public boolean isUseCuongNo2;
    public boolean isUseAnDanh2;
    public boolean isbkt;
    public long lastTimeBoHuyet;
    public long lastTimeBoKhi;
    public long lastTimeGiapXen;
    public long lastTimeCuongNo;
    public long lastTimeAnDanh;
    public long lastTimebkt;
    public long lastTimeBoHuyet2;
    public long lastTimeBoKhi2;
    public long lastTimeGiapXen2;
    public long lastTimeCuongNo2;
    public long lastTimeAnDanh2;
    public boolean isdkhi;
    public long lastTimedkhi;
    public int icondkhi;

    public boolean isUseMayDo;
    public long lastTimeUseMayDo;//lastime de chung 1 cai neu time = nhau
    public boolean isUseMayDo2;
    public long lastTimeUseMayDo2;

    public boolean isOpenPower;
    public long lastTimeOpenPower;

    public boolean isUseTDLT;
//        public static final int TIME_DUOI_KHI = 300000;
    public long lastTimeUseTDLT;
    public int timeTDLT;

    public boolean isCauCa;
    public boolean isCauCa1;
    public boolean isCauCa2;
    public boolean isCauCa3;
    public long lastTimeCauCa;
    public int iconCauCa;

    public boolean isEatMeal;
    public long lastTimeEatMeal;
    public int iconMeal;
    public long lastX2EXP;
    public boolean isX2EXP;
    public long lastX3EXP;
    public boolean isX3EXP;
    public long lastX5EXP;
    public boolean isX5EXP;
    public long lastX7EXP;
    public boolean isX7EXP;
    public long lastbkt;
    public int IconX2EXP = 21881;

    public long lastnuocmiakhonglo;
    public boolean isnuocmiakhonglo;
    public long lastnuocmiathom;
    public boolean isnuocmiathom;
    public long lastnuocmiasaurieng;
    public boolean isnuocmiasaurieng;

    //Trung Thu
    public long last1Trung;
    public boolean is1Trung;
    public long last2Trung;
    public boolean is2Trung;
    public long lastgaQuay;
    public boolean isgaQuay;
    public long lastthapCam;
    public boolean isthapCam;

    public long lastAnhTrang;
    public boolean isAnhTrang;

    public ItemTime(Player player) {
        this.player = player;
    }
// Phước Câu Cá VIP PRO 2

    public void update() {

        // ------------------------------------------------------------------------------- Cần Câu 1
        if (isCauCa) {
            if (Util.canDoWithTime(lastTimeCauCa, 15000)) {
                isCauCa = false;

                int[] listCa1, listCa2, listCa3, listCa4, listCa5;

                // Phân loại danh sách cá theo mapId
                if (player.zone.map.mapId == 5) {
                    listCa1 = new int[]{1816, 1817, 1819, 1820, 1821}; // Siêu Hiếm
                    listCa2 = new int[]{1802, 1803, 1804, 1805, 1809, 1811, 1813, 1818}; // Hiếm
                    listCa3 = new int[]{1790, 1791, 1792, 1793, 1794, 1795, 1796, 1797, 1814, 1815}; // Trung
                    listCa4 = new int[]{1798, 1799, 1800, 1801, 1806, 1807, 1808, 1810, 1812, 1822, 1823}; // Thường
                    listCa5 = new int[]{1854, 1855, 1856, 1857, 1858, 1787, 1788, 1789}; // Rác
                } else if (player.zone.map.mapId == 6) {
                    listCa1 = new int[]{1847, 1848, 1834}; // Siêu Hiếm
                    listCa2 = new int[]{1844, 1845, 1846, 1849, 1850, 1851}; // Hiếm
                    listCa3 = new int[]{1838, 1839, 1840, 1841, 1842, 1852, 1853}; // Trung
                    listCa4 = new int[]{1830, 1831, 1832, 1833, 1835, 1836, 1837}; // Thường
                    listCa5 = new int[]{1854, 1855, 1856, 1857, 1858, 1787, 1788, 1789}; // Rác
                } else {
                    return; // Không hỗ trợ mapId khác
                }

                // Áp dụng hệ số mồi câu vào công thức tính tỷ lệ
                int rateBase = 1500; // Giá trị mặc định của khoảng random
                int rd = Util.nextInt(0, rateBase + Math.abs(player.rateModifier)); // Tăng khoảng nếu không có mồi

                if (rd <= 2) {
                    // Siêu Hiếm
                    Item ca1 = ItemService.gI().createNewItem((short) listCa1[Util.nextInt(listCa1.length)]);
                    ca1.itemOptions.add(new ItemOption(72, 5));
                    InventoryServiceNew.gI().addItemBag(player, ca1);
                    ServerNotify.gI().notify("Người Chơi " + this.player.name + " Vừa Câu Được " + ca1.template.name + "Siêu Siêu Hiếm!");
                    player.point_vnd += 1;
                } else if (rd <= 10) {
                    // Hiếm
                    Item ca2 = ItemService.gI().createNewItem((short) listCa2[Util.nextInt(listCa2.length)]);
                    ca2.itemOptions.add(new ItemOption(72, 4));
                    InventoryServiceNew.gI().addItemBag(player, ca2);
                    ServerNotify.gI().notify("Người Chơi " + this.player.name + " Vừa Câu Được " + ca2.template.name + "Hiếm!");
                    player.point_vnd += 1;
                } else if (rd <= 60) {
                    // Trung
                    Item ca3 = ItemService.gI().createNewItem((short) listCa3[Util.nextInt(listCa3.length)]);
                    InventoryServiceNew.gI().addItemBag(player, ca3);
                    player.chat("|4|Vừa Câu Được " + ca3.template.name + ", Cũng Thường Thôi!");
                    player.point_vnd += 1;
                } else if (rd <= 200) {
                    // Thường
                    Item ca4 = ItemService.gI().createNewItem((short) listCa4[Util.nextInt(listCa4.length)]);
                    InventoryServiceNew.gI().addItemBag(player, ca4);
                    player.chat("|3|Vừa Câu Được " + ca4.template.name + ", Tàm Tạm!");
                    player.point_vnd += 1;
                } else {
                    // Rác
                    Item ca5 = ItemService.gI().createNewItem((short) listCa5[Util.nextInt(listCa5.length)]);
                    InventoryServiceNew.gI().addItemBag(player, ca5);
                    player.chat("Vừa Câu Được " + ca5.template.name + ", Đen Rồi!");

                    player.point_vnd += 1;
                }
                InventoryServiceNew.gI().sendItemBags(player);
            }
        }

        // ------------------------------------------------------------------------------- Cần Câu 2
        if (isCauCa1) {
            if (Util.canDoWithTime(lastTimeCauCa, 14000)) {
                isCauCa1 = false;

                int[] listCa1, listCa2, listCa3, listCa4, listCa5;

                // Phân loại danh sách cá theo mapId
                if (player.zone.map.mapId == 5) {
                    listCa1 = new int[]{1816, 1817, 1819, 1820, 1821}; // Siêu Hiếm
                    listCa2 = new int[]{1802, 1803, 1804, 1805, 1809, 1811, 1813, 1818}; // Hiếm
                    listCa3 = new int[]{1790, 1791, 1792, 1793, 1794, 1795, 1796, 1797, 1814, 1815}; // Trung
                    listCa4 = new int[]{1798, 1799, 1800, 1801, 1806, 1807, 1808, 1810, 1812, 1822, 1823}; // Thường
                    listCa5 = new int[]{1854, 1855, 1856, 1857, 1858, 1787, 1788, 1789}; // Rác
                } else if (player.zone.map.mapId == 6) {
                    listCa1 = new int[]{1847, 1848, 1834}; // Siêu Hiếm
                    listCa2 = new int[]{1844, 1845, 1846, 1849, 1850, 1851}; // Hiếm
                    listCa3 = new int[]{1838, 1839, 1840, 1841, 1842, 1852, 1853}; // Trung
                    listCa4 = new int[]{1830, 1831, 1832, 1833, 1835, 1836, 1837}; // Thường
                    listCa5 = new int[]{1854, 1855, 1856, 1857, 1858, 1787, 1788, 1789}; // Rác
                } else {
                    return; // Không hỗ trợ mapId khác
                }

                // Áp dụng hệ số mồi câu vào công thức tính tỷ lệ
                int rateBase = 1490; // Giá trị mặc định của khoảng random
                int rd = Util.nextInt(0, rateBase + Math.abs(player.rateModifier)); // Tăng khoảng nếu không có mồi

                if (rd <= 2) {
                    // Siêu Hiếm
                    Item ca1 = ItemService.gI().createNewItem((short) listCa1[Util.nextInt(listCa1.length)]);
                    ca1.itemOptions.add(new ItemOption(72, 5));
                    InventoryServiceNew.gI().addItemBag(player, ca1);
                    ServerNotify.gI().notify("Người Chơi " + this.player.name + " Vừa Câu Được " + ca1.template.name + "Siêu Siêu Hiếm!");
                    player.point_vnd += 1;
                } else if (rd <= 10) {
                    // Hiếm
                    Item ca2 = ItemService.gI().createNewItem((short) listCa2[Util.nextInt(listCa2.length)]);
                    ca2.itemOptions.add(new ItemOption(72, 4));
                    InventoryServiceNew.gI().addItemBag(player, ca2);
                    ServerNotify.gI().notify("Người Chơi " + this.player.name + " Vừa Câu Được " + ca2.template.name + "Hiếm!");
                    player.point_vnd += 1;
                } else if (rd <= 60) {
                    // Trung
                    Item ca3 = ItemService.gI().createNewItem((short) listCa3[Util.nextInt(listCa3.length)]);
                    InventoryServiceNew.gI().addItemBag(player, ca3);
                    player.chat("|4|Vừa Câu Được " + ca3.template.name + ", Cũng Thường Thôi!");
                    player.point_vnd += 1;
                } else if (rd <= 200) {
                    // Thường
                    Item ca4 = ItemService.gI().createNewItem((short) listCa4[Util.nextInt(listCa4.length)]);
                    InventoryServiceNew.gI().addItemBag(player, ca4);
                    player.chat("|3|Vừa Câu Được " + ca4.template.name + ", Tàm Tạm!");
                    player.point_vnd += 1;
                } else {
                    // Rác
                    Item ca5 = ItemService.gI().createNewItem((short) listCa5[Util.nextInt(listCa5.length)]);
                    InventoryServiceNew.gI().addItemBag(player, ca5);
                    player.chat("Vừa Câu Được " + ca5.template.name + ", Đen Rồi!");
                    player.point_vnd += 1;
                }
                InventoryServiceNew.gI().sendItemBags(player);
            }
        }

        // ------------------------------------------------------------------------------- Cần Câu 3
        if (isCauCa2) {
            if (Util.canDoWithTime(lastTimeCauCa, 13000)) {
                isCauCa2 = false;

                int[] listCa1, listCa2, listCa3, listCa4, listCa5;

                // Phân loại danh sách cá theo mapId
                if (player.zone.map.mapId == 5) {
                    listCa1 = new int[]{1816, 1817, 1819, 1820, 1821}; // Siêu Hiếm
                    listCa2 = new int[]{1802, 1803, 1804, 1805, 1809, 1811, 1813, 1818}; // Hiếm
                    listCa3 = new int[]{1790, 1791, 1792, 1793, 1794, 1795, 1796, 1797, 1814, 1815}; // Trung
                    listCa4 = new int[]{1798, 1799, 1800, 1801, 1806, 1807, 1808, 1810, 1812, 1822, 1823}; // Thường
                    listCa5 = new int[]{1854, 1855, 1856, 1857, 1858, 1787, 1788, 1789}; // Rác
                } else if (player.zone.map.mapId == 6) {
                    listCa1 = new int[]{1847, 1848, 1834}; // Siêu Hiếm
                    listCa2 = new int[]{1844, 1845, 1846, 1849, 1850, 1851}; // Hiếm
                    listCa3 = new int[]{1838, 1839, 1840, 1841, 1842, 1852, 1853}; // Trung
                    listCa4 = new int[]{1830, 1831, 1832, 1833, 1835, 1836, 1837}; // Thường
                    listCa5 = new int[]{1854, 1855, 1856, 1857, 1858, 1787, 1788, 1789}; // Rác
                } else {
                    return; // Không hỗ trợ mapId khác
                }

                // Áp dụng hệ số mồi câu vào công thức tính tỷ lệ
                int rateBase = 1480; // Giá trị mặc định của khoảng random
                int rd = Util.nextInt(0, rateBase + Math.abs(player.rateModifier)); // Tăng khoảng nếu không có mồi

                if (rd <= 2) {
                    // Siêu Hiếm
                    Item ca1 = ItemService.gI().createNewItem((short) listCa1[Util.nextInt(listCa1.length)]);
                    ca1.itemOptions.add(new ItemOption(72, 5));
                    InventoryServiceNew.gI().addItemBag(player, ca1);
                    ServerNotify.gI().notify("Người Chơi " + this.player.name + " Vừa Câu Được " + ca1.template.name + "Siêu Siêu Hiếm!");
                    player.point_vnd += 1;
                } else if (rd <= 10) {
                    // Hiếm
                    Item ca2 = ItemService.gI().createNewItem((short) listCa2[Util.nextInt(listCa2.length)]);
                    ca2.itemOptions.add(new ItemOption(72, 4));
                    InventoryServiceNew.gI().addItemBag(player, ca2);
                    ServerNotify.gI().notify("Người Chơi " + this.player.name + " Vừa Câu Được " + ca2.template.name + "Hiếm!");
                    player.point_vnd += 1;
                } else if (rd <= 60) {
                    // Trung
                    Item ca3 = ItemService.gI().createNewItem((short) listCa3[Util.nextInt(listCa3.length)]);
                    InventoryServiceNew.gI().addItemBag(player, ca3);
                    player.chat("|4|Vừa Câu Được " + ca3.template.name + ", Cũng Thường Thôi!");
                    player.point_vnd += 1;
                } else if (rd <= 200) {
                    // Thường
                    Item ca4 = ItemService.gI().createNewItem((short) listCa4[Util.nextInt(listCa4.length)]);
                    InventoryServiceNew.gI().addItemBag(player, ca4);
                    player.chat("|3|Vừa Câu Được " + ca4.template.name + ", Tàm Tạm!");
                    player.point_vnd += 1;
                } else {
                    // Rác
                    Item ca5 = ItemService.gI().createNewItem((short) listCa5[Util.nextInt(listCa5.length)]);
                    InventoryServiceNew.gI().addItemBag(player, ca5);
                    player.chat("Vừa Câu Được " + ca5.template.name + ", Đen Rồi!");
                    player.point_vnd += 1;
                }
                InventoryServiceNew.gI().sendItemBags(player);
            }
        }

        // ------------------------------------------------------------------------------- Cần Câu 4
        if (isCauCa3) {
            if (Util.canDoWithTime(lastTimeCauCa, 9000)) {
                isCauCa3 = false;

                int[] listCa1, listCa2, listCa3, listCa4, listCa5;

                // Phân loại danh sách cá theo mapId
                if (player.zone.map.mapId == 5) {
                    listCa1 = new int[]{1816, 1817, 1819, 1820, 1821}; // Siêu Hiếm
                    listCa2 = new int[]{1802, 1803, 1804, 1805, 1809, 1811, 1813, 1818}; // Hiếm
                    listCa3 = new int[]{1790, 1791, 1792, 1793, 1794, 1795, 1796, 1797, 1814, 1815}; // Trung
                    listCa4 = new int[]{1798, 1799, 1800, 1801, 1806, 1807, 1808, 1810, 1812, 1822, 1823}; // Thường
                    listCa5 = new int[]{1854, 1855, 1856, 1857, 1858, 1787, 1788, 1789}; // Rác
                } else if (player.zone.map.mapId == 6) {
                    listCa1 = new int[]{1847, 1848, 1834}; // Siêu Hiếm
                    listCa2 = new int[]{1844, 1845, 1846, 1849, 1850, 1851}; // Hiếm
                    listCa3 = new int[]{1838, 1839, 1840, 1841, 1842, 1852, 1853}; // Trung
                    listCa4 = new int[]{1830, 1831, 1832, 1833, 1835, 1836, 1837}; // Thường
                    listCa5 = new int[]{1854, 1855, 1856, 1857, 1858, 1787, 1788, 1789}; // Rác
                } else {
                    return; // Không hỗ trợ mapId khác
                }

                // Áp dụng hệ số mồi câu vào công thức tính tỷ lệ
                int rateBase = 1500; // Giá trị mặc định của khoảng random
                int rd = Util.nextInt(0, rateBase + Math.abs(player.rateModifier)); // Tăng khoảng nếu không có mồi

                if (rd <= 1500) {
                    // Siêu Hiếm
                    Item ca1 = ItemService.gI().createNewItem((short) listCa1[Util.nextInt(listCa1.length)]);
                    ca1.itemOptions.add(new ItemOption(72, 5));
                    InventoryServiceNew.gI().addItemBag(player, ca1);
                    ServerNotify.gI().notify("Người Chơi " + this.player.name + " Vừa Câu Được " + ca1.template.name + "Siêu Siêu Hiếm!");
                    player.point_vnd += 1;
                } else if (rd <= 10) {
                    // Hiếm
                    Item ca2 = ItemService.gI().createNewItem((short) listCa2[Util.nextInt(listCa2.length)]);
                    ca2.itemOptions.add(new ItemOption(72, 4));
                    InventoryServiceNew.gI().addItemBag(player, ca2);
                    ServerNotify.gI().notify("Người Chơi " + this.player.name + " Vừa Câu Được " + ca2.template.name + "Hiếm!");
                    player.point_vnd += 1;
                } else if (rd <= 60) {
                    // Trung
                    Item ca3 = ItemService.gI().createNewItem((short) listCa3[Util.nextInt(listCa3.length)]);
                    InventoryServiceNew.gI().addItemBag(player, ca3);
                    player.chat("|4|Vừa Câu Được " + ca3.template.name + ", Cũng Thường Thôi!");
                    player.point_vnd += 1;
                } else if (rd <= 200) {
                    // Thường
                    Item ca4 = ItemService.gI().createNewItem((short) listCa4[Util.nextInt(listCa4.length)]);
                    InventoryServiceNew.gI().addItemBag(player, ca4);
                    player.chat("|3|Vừa Câu Được " + ca4.template.name + ", Tàm Tạm!");
                    player.point_vnd += 1;
                } else {
                    // Rác
                    Item ca5 = ItemService.gI().createNewItem((short) listCa5[Util.nextInt(listCa5.length)]);
                    InventoryServiceNew.gI().addItemBag(player, ca5);
                    player.chat("Vừa Câu Được " + ca5.template.name + ", Đen Rồi!");
                    player.point_vnd += 1;
                }
                InventoryServiceNew.gI().sendItemBags(player);
            }
        }

        if (isAnhTrang) {
            if (Util.canDoWithTime(lastAnhTrang, TIME_ITEM)) {
                isAnhTrang = false;
            }
        }
        if (isX3EXP) {
            if (player.gender == 0) {
                if (Util.canDoWithTime(lastX3EXP, BA_MUOI_PHUT)) {
                    isX3EXP = false;
                }
            } else if (player.gender == 2) {
                if (Util.canDoWithTime(lastX3EXP, BON_MUOI_PHUT)) {
                    isX3EXP = false;
                }
            } else if (player.gender == 2) {
                if (Util.canDoWithTime(lastX3EXP, NAM_MUOI_PHUT)) {
                    isX3EXP = false;
                }
            }

        }
        if (isX5EXP) {
            if (Util.canDoWithTime(lastX5EXP, TIME_MAY_DO)) {
                isX5EXP = false;
            }
        }
        if (isX7EXP) {
            if (Util.canDoWithTime(lastX7EXP, TIME_MAY_DO)) {
                isX7EXP = false;
            }
        }
        if (isX2EXP) {
            if (Util.canDoWithTime(lastX2EXP, TIME_MAY_DO)) {
                isX2EXP = false;
            }
        }
        if (isnuocmiakhonglo) {
            if (Util.canDoWithTime(lastnuocmiakhonglo, TIME_NUOC_MIA)) {
                isnuocmiakhonglo = false;
            }
        }
        if (isnuocmiathom) {
            if (Util.canDoWithTime(lastnuocmiathom, TIME_NUOC_MIA)) {
                isnuocmiathom = false;
            }
        }
        if (isnuocmiasaurieng) {
            if (Util.canDoWithTime(lastnuocmiasaurieng, TIME_NUOC_MIA)) {
                isnuocmiasaurieng = false;
            }
        }
        if (is1Trung) {
            if (Util.canDoWithTime(last1Trung, TIME_TRUNG_THU_10P)) {
                is1Trung = false;
            }
        }
        if (is2Trung) {
            if (Util.canDoWithTime(last2Trung, TIME_TRUNG_THU_10P)) {
                is2Trung = false;
            }
        }
        if (isgaQuay) {
            if (Util.canDoWithTime(lastgaQuay, TIME_TRUNG_THU_10P)) {
                isgaQuay = false;
            }
        }
        if (isthapCam) {
            if (Util.canDoWithTime(lastthapCam, TIME_MAY_DO)) {
                isthapCam = false;
            }
        }
        if (isEatMeal) {
            if (Util.canDoWithTime(lastTimeEatMeal, TIME_EAT_MEAL)) {
                isEatMeal = false;
                Service.gI().point(player);
            }
        }
        if (isUseBoHuyet) {
            if (Util.canDoWithTime(lastTimeBoHuyet, TIME_ITEM)) {
                isUseBoHuyet = false;
                Service.gI().point(player);
            }
        }

        if (isUseBoKhi) {
            if (Util.canDoWithTime(lastTimeBoKhi, TIME_ITEM)) {
                isUseBoKhi = false;
                Service.gI().point(player);
            }
        }

        if (isUseGiapXen) {
            if (Util.canDoWithTime(lastTimeGiapXen, TIME_ITEM)) {
                isUseGiapXen = false;
            }
        }
        if (isbkt) {
            if (Util.canDoWithTime(lastTimebkt, TIME_MAY_DO)) {
                isbkt = false;
            }
        }
        if (isUseCuongNo) {
            if (Util.canDoWithTime(lastTimeCuongNo, TIME_ITEM)) {
                isUseCuongNo = false;
                Service.gI().point(player);
            }
        }
        if (isUseAnDanh) {
            if (Util.canDoWithTime(lastTimeAnDanh, TIME_ITEM)) {
                isUseAnDanh = false;
            }
        }

        if (isUseBoHuyet2) {
            if (Util.canDoWithTime(lastTimeBoHuyet2, TIME_ITEM)) {
                isUseBoHuyet2 = false;
                Service.gI().point(player);
            }
        }

        if (isUseBoKhi2) {
            if (Util.canDoWithTime(lastTimeBoKhi2, TIME_ITEM)) {
                isUseBoKhi2 = false;
                Service.gI().point(player);
            }
        }
        if (isUseGiapXen2) {
            if (Util.canDoWithTime(lastTimeGiapXen2, TIME_ITEM)) {
                isUseGiapXen2 = false;
            }
        }
        if (isUseCuongNo2) {
            if (Util.canDoWithTime(lastTimeCuongNo2, TIME_ITEM)) {
                isUseCuongNo2 = false;
                Service.gI().point(player);
            }
        }
        if (isUseAnDanh2) {
            if (Util.canDoWithTime(lastTimeAnDanh2, TIME_ITEM)) {
                isUseAnDanh2 = false;
            }
        }
        if (isdkhi) {
            if (Util.canDoWithTime(lastTimedkhi, TIME_DUOI_KHI)) {
                isdkhi = false;
            }
        }
        if (isOpenPower) {
            if (Util.canDoWithTime(lastTimeOpenPower, TIME_OPEN_POWER)) {
                player.nPoint.limitPower++;
                if (player.nPoint.limitPower > NPoint.MAX_LIMIT) {
                    player.nPoint.limitPower = NPoint.MAX_LIMIT;
                }
                Service.gI().sendThongBao(player, "Giới hạn sức mạnh của bạn đã được tăng lên 1 bậc");
                isOpenPower = false;
            }
        }
        if (isUseMayDo) {
            if (Util.canDoWithTime(lastTimeUseMayDo, TIME_MAY_DO)) {
                isUseMayDo = false;
            }
        }
        if (isUseMayDo2) {
            if (Util.canDoWithTime(lastTimeUseMayDo2, TIME_MAY_DO2)) {
                isUseMayDo2 = false;
            }
        }
        if (isUseTDLT) {
            if (Util.canDoWithTime(lastTimeUseTDLT, timeTDLT)) {
                this.isUseTDLT = false;
                ItemTimeService.gI().sendCanAutoPlay(this.player);
            }
        }
    }

    public void dispose() {
        this.player = null;
    }
}
