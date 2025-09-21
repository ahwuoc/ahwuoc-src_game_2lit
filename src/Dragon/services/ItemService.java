package Dragon.services;

import Dragon.models.Template;
import Dragon.models.Template.ItemOptionTemplate;
import Dragon.models.item.Item;
import Dragon.models.map.ItemMap;
import Dragon.models.player.Player;
import Dragon.models.shop.ItemShop;
import Dragon.server.Manager;
import Dragon.services.func.CombineServiceNew;
import Dragon.utils.TimeUtil;
import Dragon.utils.Util;
import Dragon.models.item.Item.ItemOption;
import Dragon.models.map.Zone;

import java.util.*;
import java.util.stream.Collectors;

public class ItemService {

    private static ItemService i;

    public static ItemService gI() {
        if (i == null) {
            i = new ItemService();
        }
        return i;
    }

    public ItemMap itemMapSKH(Zone zone, int tempId, int quantity, int x, int y, long playerId, int skhid) {
        ItemMap item = createItemMapSetKichHoat(zone, tempId, quantity, x, y, playerId);
        if (item != null) {
            item.options.addAll(ItemService.gI().getListOptionItemShop((short) tempId));
            item.options.add(new Item.ItemOption(skhid, 1));
            item.options.add(new Item.ItemOption(optionIdSKH(skhid), 1));
            item.options.add(new Item.ItemOption(30, 1));
        }
        return item;
    }

    public ItemMap createItemMapSetKichHoat(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap item = new ItemMap(zone, tempId, quantity, x, y, playerId);
        item.quantity = quantity;
        item.options = createItemNull().itemOptions;
        item.itemTemplate = getTemplate(tempId);

        return item;
    }

    public void OpenItem1611(Player player, Item itemUse) {
        int random = Util.nextInt(1, 2);
        if (Util.isTrue(30, 100)) {
            player.point_vnd += random;
            short[] bkt = {1612, 1613, 1614, 1615, 1616};
            Item batbo = Util.batbo(bkt[Util.nextInt(bkt.length)]);
            InventoryServiceNew.gI().addItemBag(player, batbo);
            Service.gI().sendThongBao(player, "Đã Bắt Được " + batbo.template.name + " và " + random + " điểm sự kiện ");
        } else {
            Service.gI().sendThongBao(player, "Bắt Hụt Rồi");
        }
    }

//    public void OpenItem1684(Player player, Item itemUse) {
//        int random = Util.nextInt(1, 2);
//        if (Util.isTrue(30, 100)) {
//            player.point_cauca += 1;
//            short[] bkt = {1002, 1003};
//            Item cauca = Util.cauca(bkt[Util.nextInt(bkt.length)]);
//            InventoryServiceNew.gI().addItemBag(player, cauca);
//            Service.gI().sendThongBao(player, "Đã Bắt Được " + cauca.template.name);
//        } else {
//            Service.gI().sendThongBao(player, "Đứt Dây Câu Rồi");
//        }
//    }
    public Item itemtlinhItem(short tempId) {
        return randomcs_dtl(tempId, 1);

    }

    public Item randomcs_dtl(int itemId, int gender) {
        Item dotl = createItemSetKichHoat(itemId, 1);
        List<Integer> ao = Arrays.asList(555, 557, 559);
        List<Integer> quan = Arrays.asList(556, 558, 560);
        List<Integer> gang = Arrays.asList(562, 564, 566);
        List<Integer> giay = Arrays.asList(563, 565, 567);
        int ntl = 561;
        if (ao.contains(itemId)) {
            dotl.itemOptions.add(new Item.ItemOption(47, Util.highlightsItem(gender == 2, new Random().nextInt(501) + 400))); // áo
        }
        if (quan.contains(itemId)) {
            dotl.itemOptions.add(new Item.ItemOption(22, Util.highlightsItem(gender == 0, new Random().nextInt(16) + 30))); // quần
        }
        if (gang.contains(itemId)) {
            dotl.itemOptions.add(new Item.ItemOption(0, Util.highlightsItem(gender == 2, new Random().nextInt(2500) + 2000))); //găng
        }
        if (giay.contains(itemId)) {
            dotl.itemOptions.add(new Item.ItemOption(23, Util.highlightsItem(gender == 1, new Random().nextInt(16) + 30))); // giày
        }
        if (ntl == itemId) {
            dotl.itemOptions.add(new Item.ItemOption(14, new Random().nextInt(5) + 12)); //chí mạng 17-19%
        }
        dotl.itemOptions.add(new Item.ItemOption(21, 16));// yêu cầu sm 80 tỉ
        dotl.itemOptions.add(new Item.ItemOption(30, 1));// ko the gd
        return dotl;
    }

    public int randomSKHTId(byte gender) {
        if (gender == 3) {
            gender = 2;
        }
        int[][] options = {{127, 128, 129}, {130, 131, 132}, {133, 134, 135}};
        int skht1 = 25;
        int skht2 = 35;
        int skhtc = 40;
        int skhtId = -1;
        int rd = Util.nextInt(1, 100);
        if (rd <= skht1) {
            skhtId = 0;
        } else if (rd <= skht1 + skht2) {
            skhtId = 1;
        } else if (rd <= skht1 + skht2 + skhtc) {
            skhtId = 2;
        }
        return options[gender][skhtId];
    }

    public Item itemSKHT(int itemId, int skhtId) {
        Item item = createItemSetKichHoat(itemId, 1);
        if (item != null) {
            item.itemOptions.addAll(ItemService.gI().getListOptionItemShop((short) itemId));
            item.itemOptions.add(new Item.ItemOption(skhtId, 1));
            item.itemOptions.add(new Item.ItemOption(optionIdSKH(skhtId), 1));
            item.itemOptions.add(new Item.ItemOption(30, 1));
        }
        return item;
    }

    public short getItemIdByIcon(short IconID) {
        for (int i = 0; i < Manager.ITEM_TEMPLATES.size(); i++) {
            if (Manager.ITEM_TEMPLATES.get(i).iconID == IconID) {
                return Manager.ITEM_TEMPLATES.get(i).id;
            }
        }
        return -1;
    }

    public Item createItemNull() {
        Item item = new Item();
        return item;
    }

    public void removeAndAddOptionTemplate(List<Item.ItemOption> itemOptions, int removeId) {
        int id = 0;
        int param = 0;
        int[] rd203 = new int[]{50, 77, 103};
        int[] rd212 = new int[]{5, 14, 94, 108, 97};
        int[] rd210 = new int[]{77, 103, 50};
        int[] rd216 = new int[]{77, 103, 50};
        int[] rd194 = new int[]{5, 14, 94, 108, 97};
        int[] rd198 = new int[]{5, 14, 94, 108, 97};
        int[] rd202 = new int[]{5, 14, 94, 108, 97};
        int[] rd199 = new int[]{77, 103, 50};
        int[] rd200 = new int[]{77, 103, 50};
        int random203 = new Random().nextInt(rd203.length);
        int random212 = new Random().nextInt(rd212.length);
        int random210 = new Random().nextInt(rd210.length);
        int random216 = new Random().nextInt(rd216.length);
        int random194 = new Random().nextInt(rd194.length);
        int random198 = new Random().nextInt(rd198.length);
        int random199 = new Random().nextInt(rd199.length);
        int random200 = new Random().nextInt(rd200.length);
        int random202 = new Random().nextInt(rd202.length);

        boolean shouldExecute = false;

        switch (removeId) {
            case 194:
                id = rd194[random194];
                ;
                param = Util.nextInt(1, 5);
                shouldExecute = true;
                break;
            // phước ngẫu nghiên sức đánh
            case 195:
                id = 50;
                param = Util.nextInt(1, 35);
                shouldExecute = true;
                break;
            case 196:
                id = 77;
                param = Util.nextInt(1, 35);
                shouldExecute = true;
                break;
            case 197:
                id = 103;
                param = Util.nextInt(1, 35);
                shouldExecute = true;
                break;
            case 198:
                id = rd198[random198];
                ;
                param = Util.nextInt(1, 3);
                shouldExecute = true;
                break;
            case 199:
                id = 50;
                param = Util.nextInt(25, 55);
                shouldExecute = true;
                break;
            case 200:
                id = 77;
                param = Util.nextInt(25, 55);
                shouldExecute = true;
                break;
            case 201:
                id = 103;
                param = Util.nextInt(25, 55);
                shouldExecute = true;
                break;
            case 202:
                id = rd202[random202];
                ;
                param = Util.nextInt(1, 10);
                shouldExecute = true;
                break;
            case 203:
                id = 50;
                param = Util.nextInt(5, 20);
                shouldExecute = true;
                break;
            case 204:
                id = 77;
                param = Util.nextInt(5, 20);
                shouldExecute = true;
                break;
            case 205:
                id = 103;
                param = Util.nextInt(5, 20);
                shouldExecute = true;
                break;
            case 206:
                id = 103;
                param = Util.nextInt(3000, 5000);
                shouldExecute = true;
                break;
            case 212:
                id = rd212[random212];
                ;
                param = Util.nextInt(1, 15);
                shouldExecute = true;
                break;
            case 210:
                id = rd210[random210];
                ;
                param = Util.nextInt(100, 300);
                shouldExecute = true;
                break;
            case 213:
                id = 50;
                param = Util.nextInt(15, 30);
                shouldExecute = true;
                break;
            case 214:
                id = 77;
                param = Util.nextInt(15, 30);
                shouldExecute = true;
                break;
            case 215:
                id = 103;
                param = Util.nextInt(15, 30);
                shouldExecute = true;
                break;
            case 216:
                id = rd216[random216];
                ;
                param = Util.nextInt(1, 5);
                shouldExecute = true;
                break;
            default:
                break;
        }

        if (shouldExecute && itemOptions.stream().anyMatch(io -> io.optionTemplate.id == removeId)) {
            itemOptions.removeIf(io -> io.optionTemplate.id == removeId); // Xóa optionTemplate có id cần xóa
            itemOptions.add(new ItemOption(new Item.ItemOption(id, param))); // Thêm optionTemplate mới có id vào danh sách
        }
    }

    public Item createItemFromItemShop(ItemShop itemShop) {
        if ("BILL".equals(itemShop.tabShop.shop.tagName)) {
            Item item = new Item();
            item.template = itemShop.temp;
            item.quantity = 1;
            item.content = item.getContent();
            item.info = item.getInfo();

            for (Item.ItemOption io : itemShop.options) {
                item.itemOptions.add(new Item.ItemOption(io));
            }

            item.itemOptions.forEach(c -> {
                if (c.optionTemplate.id != 21 && c.optionTemplate.id != 30) {
                    if (Util.nextInt(0, 500) < 300) {
                        c.param = c.param + ((c.param * Util.nextInt(1, 5)) / 100);
                    } else if (Util.nextInt(0, 500) < 450) {
                        c.param = c.param + ((c.param * Util.nextInt(1, 10)) / 100);
                    } else {
                        c.param = c.param + ((c.param * Util.nextInt(1, 15)) / 100);
                    }
                }
            });

            return item;
        } else {
            Item item = new Item();
            item.template = itemShop.temp;
            item.quantity = 1;
            item.content = item.getContent();
            item.info = item.getInfo();

            for (Item.ItemOption io : itemShop.options) {
                item.itemOptions.add(new Item.ItemOption(io));

                removeAndAddOptionTemplate(item.itemOptions, new Item.ItemOption(io).optionTemplate.id);

            }

            return item;
        }
    }

    public Item copyItem(Item item) {
        Item it = new Item();
        it.itemOptions = new ArrayList<>();
        it.template = item.template;
        it.info = item.info;
        it.content = item.content;
        it.quantity = item.quantity;
        it.createTime = item.createTime;
        for (Item.ItemOption io : item.itemOptions) {
            it.itemOptions.add(new Item.ItemOption(io));
        }
        return it;
    }

    public Item createNewItem(short tempId) {
        return createNewItem(tempId, 1);
    }

    public Item otptl(short tempId) {
        return otptl(tempId, 1);
    }

    public Item otpts(short tempId) {
        return otpts(tempId, 1);
    }

    public Item otphd(short tempId) {
        return otphd(tempId, 1);
    }

    public Item otptl(short tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();
        if (item.template.type == 0) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(47, Util.nextInt(2000, 2500)));
        }
        if (item.template.type == 1) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(22, Util.nextInt(45, 65)));
        }
        if (item.template.type == 2) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(0, Util.nextInt(5000, 5750)));
        }
        if (item.template.type == 3) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(23, Util.nextInt(45, 46)));
        }
        if (item.template.type == 4) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(14, Util.nextInt(15, 18)));
        }
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item otphd(short tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();
        if (item.template.type == 0) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(47, Util.nextInt(2000, 2500)));
        }
        if (item.template.type == 1) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(22, Util.nextInt(95, 110)));
        }
        if (item.template.type == 2) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(0, Util.nextInt(9500, 11000)));
        }
        if (item.template.type == 3) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(23, Util.nextInt(95, 110)));
        }
        if (item.template.type == 4) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(14, Util.nextInt(18, 22)));
        }
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createNewItem(short tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();

        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item otpts(short tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();
        if (item.template.type == 0) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(47, Util.nextInt(2000, 2500)));
        }
        if (item.template.type == 1) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(22, Util.nextInt(150, 200)));
        }
        if (item.template.type == 2) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(0, Util.nextInt(13000, 18000)));
        }
        if (item.template.type == 3) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(23, Util.nextInt(150, 200)));
        }
        if (item.template.type == 4) {
            item.itemOptions.add(new ItemOption(21, 80));
            item.itemOptions.add(new ItemOption(14, Util.nextInt(20, 25)));
        }
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createItemSetKichHoat(int tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.itemOptions = createItemNull().itemOptions;
        item.createTime = System.currentTimeMillis();
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createItemDoHuyDiet(int tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.itemOptions = createItemNull().itemOptions;
        item.createTime = System.currentTimeMillis();
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createItemFromItemMap(ItemMap itemMap) {
        Item item = createNewItem(itemMap.itemTemplate.id, itemMap.quantity);
        item.itemOptions = itemMap.options;
        return item;
    }

    public ItemOptionTemplate getItemOptionTemplate(int id) {
        return Manager.ITEM_OPTION_TEMPLATES.get(id);
    }

    public Template.ItemTemplate getTemplate(int id) {
        return Manager.ITEM_TEMPLATES.get(id);
    }

    public boolean isItemActivation(Item item) {
        return false;
    }

    public int getPercentTrainArmor(Item item) {
        if (item != null) {
            switch (item.template.id) {
                case 529:
                case 534:
                    return 10;
                case 530:
                case 535:
                    return 20;
                case 531:
                case 536:
                    return 30;
                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }

    public boolean isTrainArmor(Item item) {
        if (item != null) {
            switch (item.template.id) {
                case 529:
                case 534:
                case 530:
                case 535:
                case 531:
                case 536:
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public boolean isOutOfDateTime(Item item) {
        if (item != null) {
            for (Item.ItemOption io : item.itemOptions) {
                if (io.optionTemplate.id == 93) {
                    int dayPass = (int) TimeUtil.diffDate(new Date(), new Date(item.createTime), TimeUtil.DAY);
                    if (dayPass != 0) {
                        io.param -= dayPass;
                        if (io.param <= 0) {
                            return true;
                        } else {
                            item.createTime = System.currentTimeMillis();
                        }
                    }
                }
            }
        }
        return false;
    }

    public void OpenSKH(Player player, int itemUseId, int select) throws Exception {
        if (select < 0 || select > 4) {
            return;
        }
        Item itemUse = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, itemUseId);
        int[][] items = {{0, 6, 21, 27, 12}, {1, 7, 22, 28, 12}, {2, 8, 23, 29, 12}};
        int[][] options = {{128, 129, 127}, {130, 131, 132}, {133, 135, 134}};
        int skhv1 = 25;// ti le
        int skhv2 = 35;//ti le
        int skhc = 40;//ti le
        int skhId = -1;

        int rd = Util.nextInt(1, 100);
        if (rd <= skhv1) {
            skhId = 0;
        } else if (rd <= skhv1 + skhv2) {
            skhId = 1;
        } else if (rd <= skhv1 + skhv2 + skhc) {
            skhId = 2;
        }
        Item item = null;
        switch (itemUseId) {
            case 2000:
                item = itemSKH(items[0][select], options[0][skhId]);
                break;
            case 2001:
                item = itemSKH(items[1][select], options[1][skhId]);
                break;
            case 2002:
                item = itemSKH(items[2][select], options[2][skhId]);
                break;
        }
        if (item != null && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            InventoryServiceNew.gI().addItemBag(player, item);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.gI().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
            InventoryServiceNew.gI().subQuantityItemsBag(player, itemUse, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

    public int randomSKHId(byte gender) {
        if (gender == 3) {
            gender = 2;
        }
        int[][] options = {{128, 129, 127}, {130, 131, 132}, {133, 135, 134}};
        int skhv1 = 25;
        int skhv2 = 35;
        int skhc = 40;
        int skhId = -1;
        int rd = Util.nextInt(1, 100);
        if (rd <= skhv1) {
            skhId = 0;
        } else if (rd <= skhv1 + skhv2) {
            skhId = 1;
        } else if (rd <= skhv1 + skhv2 + skhc) {
            skhId = 2;
        }
        return options[gender][skhId];
    }

    public void OpenDHD(Player player, int itemUseId, int select) throws Exception {
        if (select < 0 || select > 4) {
            return;
        }
        Item itemUse = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, itemUseId);
        int gender = -1;
        switch (itemUseId) {
            case 2003: //td
                gender = 0;
                break;
            case 2004: //xd
                gender = 2;
                break;
            case 2005: //nm
                gender = 1;
                break;
        }
        int[][] items = {{650, 651, 657, 658, 656}, {652, 653, 659, 660, 656}, {654, 655, 661, 662, 656}}; //td, namec,xd
        Item item = randomCS_DHD(items[gender][select], gender);

        if (item != null && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            InventoryServiceNew.gI().addItemBag(player, item);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.gI().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
            InventoryServiceNew.gI().subQuantityItemsBag(player, itemUse, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

    public void OpenItem736(Player player, Item itemUse) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 1) {
                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 2 ô trống hành trang");
                return;
            }
            short[] icon = new short[2];
            int rd = Util.nextInt(1, 100);
            int rac = 50;
            int ruby = 20;
            int dbv = 10;
            int vb = 10;
            int bh = 5;
            int ct = 5;
            Item item = randomRac();
            if (rd <= rac) {
                item = randomRac();
            } else if (rd <= rac + ruby) {
                item = Manager.RUBY_REWARDS.get(Util.nextInt(0, Manager.RUBY_REWARDS.size() - 1));
            } else if (rd <= rac + ruby + dbv) {
                item = daBaoVe();
            } else if (rd <= rac + ruby + dbv + vb) {
                item = vanBay2011(true);
            } else if (rd <= rac + ruby + dbv + vb + bh) {
                item = phuKien2011(true);
            } else if (rd <= rac + ruby + dbv + vb + bh + ct) {
                item = caitrang2011(true);
            }
            if (item.template.id == 861) {
                item.quantity = Util.nextInt(10, 30);
            }
            icon[0] = itemUse.template.iconID;
            icon[1] = item.template.iconID;
            InventoryServiceNew.gI().subQuantityItemsBag(player, itemUse, 1);
            InventoryServiceNew.gI().addItemBag(player, item);
            InventoryServiceNew.gI().sendItemBags(player);
            player.inventory.event++;
            Service.gI().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } catch (Exception e) {

        }
    }

    public void settaiyoken(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1048);
        Item quan = ItemService.gI().otpts((short) 1051);
        Item gang = ItemService.gI().otpts((short) 1054);
        Item giay = ItemService.gI().otpts((short) 1057);
        Item nhan = ItemService.gI().otpts((short) 1060);
        ao.itemOptions.add(new Item.ItemOption(127, 0));
        quan.itemOptions.add(new Item.ItemOption(127, 0));
        gang.itemOptions.add(new Item.ItemOption(127, 0));
        giay.itemOptions.add(new Item.ItemOption(127, 0));
        nhan.itemOptions.add(new Item.ItemOption(127, 0));
        ao.itemOptions.add(new Item.ItemOption(139, 0));
        quan.itemOptions.add(new Item.ItemOption(139, 0));
        gang.itemOptions.add(new Item.ItemOption(139, 0));
        giay.itemOptions.add(new Item.ItemOption(139, 0));
        nhan.itemOptions.add(new Item.ItemOption(139, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setgenki(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1048);
        Item quan = ItemService.gI().otpts((short) 1051);
        Item gang = ItemService.gI().otpts((short) 1054);
        Item giay = ItemService.gI().otpts((short) 1057);
        Item nhan = ItemService.gI().otpts((short) 1060);
        ao.itemOptions.add(new Item.ItemOption(128, 0));
        quan.itemOptions.add(new Item.ItemOption(128, 0));
        gang.itemOptions.add(new Item.ItemOption(128, 0));
        giay.itemOptions.add(new Item.ItemOption(128, 0));
        nhan.itemOptions.add(new Item.ItemOption(128, 0));
        ao.itemOptions.add(new Item.ItemOption(140, 0));
        quan.itemOptions.add(new Item.ItemOption(140, 0));
        gang.itemOptions.add(new Item.ItemOption(140, 0));
        giay.itemOptions.add(new Item.ItemOption(140, 0));
        nhan.itemOptions.add(new Item.ItemOption(140, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setkamejoko(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1048);
        Item quan = ItemService.gI().otpts((short) 1051);
        Item gang = ItemService.gI().otpts((short) 1054);
        Item giay = ItemService.gI().otpts((short) 1057);
        Item nhan = ItemService.gI().otpts((short) 1060);
        ao.itemOptions.add(new Item.ItemOption(129, 0));
        quan.itemOptions.add(new Item.ItemOption(129, 0));
        gang.itemOptions.add(new Item.ItemOption(129, 0));
        giay.itemOptions.add(new Item.ItemOption(129, 0));
        nhan.itemOptions.add(new Item.ItemOption(129, 0));
        ao.itemOptions.add(new Item.ItemOption(141, 0));
        quan.itemOptions.add(new Item.ItemOption(141, 0));
        gang.itemOptions.add(new Item.ItemOption(141, 0));
        giay.itemOptions.add(new Item.ItemOption(141, 0));
        nhan.itemOptions.add(new Item.ItemOption(141, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setgodki(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1049);
        Item quan = ItemService.gI().otpts((short) 1052);
        Item gang = ItemService.gI().otpts((short) 1055);
        Item giay = ItemService.gI().otpts((short) 1058);
        Item nhan = ItemService.gI().otpts((short) 1061);
        ao.itemOptions.add(new Item.ItemOption(130, 0));
        quan.itemOptions.add(new Item.ItemOption(130, 0));
        gang.itemOptions.add(new Item.ItemOption(130, 0));
        giay.itemOptions.add(new Item.ItemOption(130, 0));
        nhan.itemOptions.add(new Item.ItemOption(130, 0));
        ao.itemOptions.add(new Item.ItemOption(142, 0));
        quan.itemOptions.add(new Item.ItemOption(142, 0));
        gang.itemOptions.add(new Item.ItemOption(142, 0));
        giay.itemOptions.add(new Item.ItemOption(142, 0));
        nhan.itemOptions.add(new Item.ItemOption(142, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setgoddam(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1049);
        Item quan = ItemService.gI().otpts((short) 1052);
        Item gang = ItemService.gI().otpts((short) 1055);
        Item giay = ItemService.gI().otpts((short) 1058);
        Item nhan = ItemService.gI().otpts((short) 1061);
        ao.itemOptions.add(new Item.ItemOption(131, 0));
        quan.itemOptions.add(new Item.ItemOption(131, 0));
        gang.itemOptions.add(new Item.ItemOption(131, 0));
        giay.itemOptions.add(new Item.ItemOption(131, 0));
        nhan.itemOptions.add(new Item.ItemOption(131, 0));
        ao.itemOptions.add(new Item.ItemOption(143, 0));
        quan.itemOptions.add(new Item.ItemOption(143, 0));
        gang.itemOptions.add(new Item.ItemOption(143, 0));
        giay.itemOptions.add(new Item.ItemOption(143, 0));
        nhan.itemOptions.add(new Item.ItemOption(143, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setsummon(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1049);
        Item quan = ItemService.gI().otpts((short) 1052);
        Item gang = ItemService.gI().otpts((short) 1055);
        Item giay = ItemService.gI().otpts((short) 1058);
        Item nhan = ItemService.gI().otpts((short) 1061);
        ao.itemOptions.add(new Item.ItemOption(132, 0));
        quan.itemOptions.add(new Item.ItemOption(132, 0));
        gang.itemOptions.add(new Item.ItemOption(132, 0));
        giay.itemOptions.add(new Item.ItemOption(132, 0));
        nhan.itemOptions.add(new Item.ItemOption(132, 0));
        ao.itemOptions.add(new Item.ItemOption(144, 0));
        quan.itemOptions.add(new Item.ItemOption(144, 0));
        gang.itemOptions.add(new Item.ItemOption(144, 0));
        giay.itemOptions.add(new Item.ItemOption(144, 0));
        nhan.itemOptions.add(new Item.ItemOption(144, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setgodgalick(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1050);
        Item quan = ItemService.gI().otpts((short) 1053);
        Item gang = ItemService.gI().otpts((short) 1056);
        Item giay = ItemService.gI().otpts((short) 1059);
        Item nhan = ItemService.gI().otpts((short) 1062);
        ao.itemOptions.add(new Item.ItemOption(133, 0));
        quan.itemOptions.add(new Item.ItemOption(133, 0));
        gang.itemOptions.add(new Item.ItemOption(133, 0));
        giay.itemOptions.add(new Item.ItemOption(133, 0));
        nhan.itemOptions.add(new Item.ItemOption(133, 0));
        ao.itemOptions.add(new Item.ItemOption(136, 0));
        quan.itemOptions.add(new Item.ItemOption(136, 0));
        gang.itemOptions.add(new Item.ItemOption(136, 0));
        giay.itemOptions.add(new Item.ItemOption(136, 0));
        nhan.itemOptions.add(new Item.ItemOption(136, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setmonkey(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1050);
        Item quan = ItemService.gI().otpts((short) 1053);
        Item gang = ItemService.gI().otpts((short) 1056);
        Item giay = ItemService.gI().otpts((short) 1059);
        Item nhan = ItemService.gI().otpts((short) 1062);
        ao.itemOptions.add(new Item.ItemOption(134, 0));
        quan.itemOptions.add(new Item.ItemOption(134, 0));
        gang.itemOptions.add(new Item.ItemOption(134, 0));
        giay.itemOptions.add(new Item.ItemOption(134, 0));
        nhan.itemOptions.add(new Item.ItemOption(134, 0));
        ao.itemOptions.add(new Item.ItemOption(137, 0));
        quan.itemOptions.add(new Item.ItemOption(137, 0));
        gang.itemOptions.add(new Item.ItemOption(137, 0));
        giay.itemOptions.add(new Item.ItemOption(137, 0));
        nhan.itemOptions.add(new Item.ItemOption(137, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setgodhp(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1050);
        Item quan = ItemService.gI().otpts((short) 1053);
        Item gang = ItemService.gI().otpts((short) 1056);
        Item giay = ItemService.gI().otpts((short) 1059);
        Item nhan = ItemService.gI().otpts((short) 1062);
        ao.itemOptions.add(new Item.ItemOption(135, 0));
        quan.itemOptions.add(new Item.ItemOption(135, 0));
        gang.itemOptions.add(new Item.ItemOption(135, 0));
        giay.itemOptions.add(new Item.ItemOption(135, 0));
        nhan.itemOptions.add(new Item.ItemOption(135, 0));
        ao.itemOptions.add(new Item.ItemOption(138, 0));
        quan.itemOptions.add(new Item.ItemOption(138, 0));
        gang.itemOptions.add(new Item.ItemOption(138, 0));
        giay.itemOptions.add(new Item.ItemOption(138, 0));
        nhan.itemOptions.add(new Item.ItemOption(138, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void set1taiyoken(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1987);
        Item ao = ItemService.gI().otphd((short) 650);
        Item quan = ItemService.gI().otphd((short) 651);
        Item gang = ItemService.gI().otphd((short) 657);
        Item giay = ItemService.gI().otphd((short) 658);
        Item nhan = ItemService.gI().otphd((short) 656);
        ao.itemOptions.add(new Item.ItemOption(127, 0));
        quan.itemOptions.add(new Item.ItemOption(127, 0));
        gang.itemOptions.add(new Item.ItemOption(127, 0));
        giay.itemOptions.add(new Item.ItemOption(127, 0));
        nhan.itemOptions.add(new Item.ItemOption(127, 0));
        ao.itemOptions.add(new Item.ItemOption(139, 0));
        quan.itemOptions.add(new Item.ItemOption(139, 0));
        gang.itemOptions.add(new Item.ItemOption(139, 0));
        giay.itemOptions.add(new Item.ItemOption(139, 0));
        nhan.itemOptions.add(new Item.ItemOption(139, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void set1genki(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1987);
        Item ao = ItemService.gI().otphd((short) 650);
        Item quan = ItemService.gI().otphd((short) 651);
        Item gang = ItemService.gI().otphd((short) 657);
        Item giay = ItemService.gI().otphd((short) 658);
        Item nhan = ItemService.gI().otphd((short) 656);
        ao.itemOptions.add(new Item.ItemOption(128, 0));
        quan.itemOptions.add(new Item.ItemOption(128, 0));
        gang.itemOptions.add(new Item.ItemOption(128, 0));
        giay.itemOptions.add(new Item.ItemOption(128, 0));
        nhan.itemOptions.add(new Item.ItemOption(128, 0));
        ao.itemOptions.add(new Item.ItemOption(140, 0));
        quan.itemOptions.add(new Item.ItemOption(140, 0));
        gang.itemOptions.add(new Item.ItemOption(140, 0));
        giay.itemOptions.add(new Item.ItemOption(140, 0));
        nhan.itemOptions.add(new Item.ItemOption(140, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void set1kamejoko(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1987);
        Item ao = ItemService.gI().otphd((short) 650);
        Item quan = ItemService.gI().otphd((short) 651);
        Item gang = ItemService.gI().otphd((short) 657);
        Item giay = ItemService.gI().otphd((short) 658);
        Item nhan = ItemService.gI().otphd((short) 656);
        ao.itemOptions.add(new Item.ItemOption(129, 0));
        quan.itemOptions.add(new Item.ItemOption(129, 0));
        gang.itemOptions.add(new Item.ItemOption(129, 0));
        giay.itemOptions.add(new Item.ItemOption(129, 0));
        nhan.itemOptions.add(new Item.ItemOption(129, 0));
        ao.itemOptions.add(new Item.ItemOption(141, 0));
        quan.itemOptions.add(new Item.ItemOption(141, 0));
        gang.itemOptions.add(new Item.ItemOption(141, 0));
        giay.itemOptions.add(new Item.ItemOption(141, 0));
        nhan.itemOptions.add(new Item.ItemOption(141, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void set2godki(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1987);
        Item ao = ItemService.gI().otphd((short) 652);
        Item quan = ItemService.gI().otphd((short) 653);
        Item gang = ItemService.gI().otphd((short) 659);
        Item giay = ItemService.gI().otphd((short) 660);
        Item nhan = ItemService.gI().otphd((short) 656);
        ao.itemOptions.add(new Item.ItemOption(130, 0));
        quan.itemOptions.add(new Item.ItemOption(130, 0));
        gang.itemOptions.add(new Item.ItemOption(130, 0));
        giay.itemOptions.add(new Item.ItemOption(130, 0));
        nhan.itemOptions.add(new Item.ItemOption(130, 0));
        ao.itemOptions.add(new Item.ItemOption(142, 0));
        quan.itemOptions.add(new Item.ItemOption(142, 0));
        gang.itemOptions.add(new Item.ItemOption(142, 0));
        giay.itemOptions.add(new Item.ItemOption(142, 0));
        nhan.itemOptions.add(new Item.ItemOption(142, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void set1goddam(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1987);
        Item ao = ItemService.gI().otphd((short) 652);
        Item quan = ItemService.gI().otphd((short) 653);
        Item gang = ItemService.gI().otphd((short) 659);
        Item giay = ItemService.gI().otphd((short) 660);
        Item nhan = ItemService.gI().otphd((short) 656);
        ao.itemOptions.add(new Item.ItemOption(131, 0));
        quan.itemOptions.add(new Item.ItemOption(131, 0));
        gang.itemOptions.add(new Item.ItemOption(131, 0));
        giay.itemOptions.add(new Item.ItemOption(131, 0));
        nhan.itemOptions.add(new Item.ItemOption(131, 0));
        ao.itemOptions.add(new Item.ItemOption(143, 0));
        quan.itemOptions.add(new Item.ItemOption(143, 0));
        gang.itemOptions.add(new Item.ItemOption(143, 0));
        giay.itemOptions.add(new Item.ItemOption(143, 0));
        nhan.itemOptions.add(new Item.ItemOption(143, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void set1summon(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1987);
        Item ao = ItemService.gI().otphd((short) 652);
        Item quan = ItemService.gI().otphd((short) 653);
        Item gang = ItemService.gI().otphd((short) 659);
        Item giay = ItemService.gI().otphd((short) 660);
        Item nhan = ItemService.gI().otphd((short) 656);
        ao.itemOptions.add(new Item.ItemOption(132, 0));
        quan.itemOptions.add(new Item.ItemOption(132, 0));
        gang.itemOptions.add(new Item.ItemOption(132, 0));
        giay.itemOptions.add(new Item.ItemOption(132, 0));
        nhan.itemOptions.add(new Item.ItemOption(132, 0));
        ao.itemOptions.add(new Item.ItemOption(144, 0));
        quan.itemOptions.add(new Item.ItemOption(144, 0));
        gang.itemOptions.add(new Item.ItemOption(144, 0));
        giay.itemOptions.add(new Item.ItemOption(144, 0));
        nhan.itemOptions.add(new Item.ItemOption(144, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void set1godgalick(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1987);
        Item ao = ItemService.gI().otphd((short) 654);
        Item quan = ItemService.gI().otphd((short) 655);
        Item gang = ItemService.gI().otphd((short) 661);
        Item giay = ItemService.gI().otphd((short) 662);
        Item nhan = ItemService.gI().otphd((short) 656);
        ao.itemOptions.add(new Item.ItemOption(133, 0));
        quan.itemOptions.add(new Item.ItemOption(133, 0));
        gang.itemOptions.add(new Item.ItemOption(133, 0));
        giay.itemOptions.add(new Item.ItemOption(133, 0));
        nhan.itemOptions.add(new Item.ItemOption(133, 0));
        ao.itemOptions.add(new Item.ItemOption(136, 0));
        quan.itemOptions.add(new Item.ItemOption(136, 0));
        gang.itemOptions.add(new Item.ItemOption(136, 0));
        giay.itemOptions.add(new Item.ItemOption(136, 0));
        nhan.itemOptions.add(new Item.ItemOption(136, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setmonkey1(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1987);
        Item ao = ItemService.gI().otphd((short) 654);
        Item quan = ItemService.gI().otphd((short) 655);
        Item gang = ItemService.gI().otphd((short) 661);
        Item giay = ItemService.gI().otphd((short) 662);
        Item nhan = ItemService.gI().otphd((short) 656);
        ao.itemOptions.add(new Item.ItemOption(134, 0));
        quan.itemOptions.add(new Item.ItemOption(134, 0));
        gang.itemOptions.add(new Item.ItemOption(134, 0));
        giay.itemOptions.add(new Item.ItemOption(134, 0));
        nhan.itemOptions.add(new Item.ItemOption(134, 0));
        ao.itemOptions.add(new Item.ItemOption(137, 0));
        quan.itemOptions.add(new Item.ItemOption(137, 0));
        gang.itemOptions.add(new Item.ItemOption(137, 0));
        giay.itemOptions.add(new Item.ItemOption(137, 0));
        nhan.itemOptions.add(new Item.ItemOption(137, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setgodhp1(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1987);
        Item ao = ItemService.gI().otphd((short) 654);
        Item quan = ItemService.gI().otphd((short) 655);
        Item gang = ItemService.gI().otphd((short) 661);
        Item giay = ItemService.gI().otphd((short) 662);
        Item nhan = ItemService.gI().otphd((short) 656);
        ao.itemOptions.add(new Item.ItemOption(135, 0));
        quan.itemOptions.add(new Item.ItemOption(135, 0));
        gang.itemOptions.add(new Item.ItemOption(135, 0));
        giay.itemOptions.add(new Item.ItemOption(135, 0));
        nhan.itemOptions.add(new Item.ItemOption(135, 0));
        ao.itemOptions.add(new Item.ItemOption(138, 0));
        quan.itemOptions.add(new Item.ItemOption(138, 0));
        gang.itemOptions.add(new Item.ItemOption(138, 0));
        giay.itemOptions.add(new Item.ItemOption(138, 0));
        nhan.itemOptions.add(new Item.ItemOption(138, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void set14taiyoken(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1986);
        Item ao = ItemService.gI().otptl((short) 555);
        Item quan = ItemService.gI().otptl((short) 556);
        Item gang = ItemService.gI().otptl((short) 562);
        Item giay = ItemService.gI().otptl((short) 563);
        Item nhan = ItemService.gI().otptl((short) 561);
        ao.itemOptions.add(new Item.ItemOption(127, 0));
        quan.itemOptions.add(new Item.ItemOption(127, 0));
        gang.itemOptions.add(new Item.ItemOption(127, 0));
        giay.itemOptions.add(new Item.ItemOption(127, 0));
        nhan.itemOptions.add(new Item.ItemOption(127, 0));
        ao.itemOptions.add(new Item.ItemOption(139, 0));
        quan.itemOptions.add(new Item.ItemOption(139, 0));
        gang.itemOptions.add(new Item.ItemOption(139, 0));
        giay.itemOptions.add(new Item.ItemOption(139, 0));
        nhan.itemOptions.add(new Item.ItemOption(139, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void set14genki(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1986);
        Item ao = ItemService.gI().otptl((short) 555);
        Item quan = ItemService.gI().otptl((short) 556);
        Item gang = ItemService.gI().otptl((short) 562);
        Item giay = ItemService.gI().otptl((short) 563);
        Item nhan = ItemService.gI().otptl((short) 561);
        ao.itemOptions.add(new Item.ItemOption(128, 0));
        quan.itemOptions.add(new Item.ItemOption(128, 0));
        gang.itemOptions.add(new Item.ItemOption(128, 0));
        giay.itemOptions.add(new Item.ItemOption(128, 0));
        nhan.itemOptions.add(new Item.ItemOption(128, 0));
        ao.itemOptions.add(new Item.ItemOption(140, 0));
        quan.itemOptions.add(new Item.ItemOption(140, 0));
        gang.itemOptions.add(new Item.ItemOption(140, 0));
        giay.itemOptions.add(new Item.ItemOption(140, 0));
        nhan.itemOptions.add(new Item.ItemOption(140, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void set14kamejoko(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1986);
        Item ao = ItemService.gI().otptl((short) 555);
        Item quan = ItemService.gI().otptl((short) 556);
        Item gang = ItemService.gI().otptl((short) 562);
        Item giay = ItemService.gI().otptl((short) 563);
        Item nhan = ItemService.gI().otptl((short) 561);
        ao.itemOptions.add(new Item.ItemOption(129, 0));
        quan.itemOptions.add(new Item.ItemOption(129, 0));
        gang.itemOptions.add(new Item.ItemOption(129, 0));
        giay.itemOptions.add(new Item.ItemOption(129, 0));
        nhan.itemOptions.add(new Item.ItemOption(129, 0));
        ao.itemOptions.add(new Item.ItemOption(141, 0));
        quan.itemOptions.add(new Item.ItemOption(141, 0));
        gang.itemOptions.add(new Item.ItemOption(141, 0));
        giay.itemOptions.add(new Item.ItemOption(141, 0));
        nhan.itemOptions.add(new Item.ItemOption(141, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void set14godki(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1986);
        Item ao = ItemService.gI().otptl((short) 557);
        Item quan = ItemService.gI().otptl((short) 558);
        Item gang = ItemService.gI().otptl((short) 564);
        Item giay = ItemService.gI().otptl((short) 565);
        Item nhan = ItemService.gI().otptl((short) 561);
        ao.itemOptions.add(new Item.ItemOption(130, 0));
        quan.itemOptions.add(new Item.ItemOption(130, 0));
        gang.itemOptions.add(new Item.ItemOption(130, 0));
        giay.itemOptions.add(new Item.ItemOption(130, 0));
        nhan.itemOptions.add(new Item.ItemOption(130, 0));
        ao.itemOptions.add(new Item.ItemOption(142, 0));
        quan.itemOptions.add(new Item.ItemOption(142, 0));
        gang.itemOptions.add(new Item.ItemOption(142, 0));
        giay.itemOptions.add(new Item.ItemOption(142, 0));
        nhan.itemOptions.add(new Item.ItemOption(142, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void set14goddam(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1986);
        Item ao = ItemService.gI().otptl((short) 557);
        Item quan = ItemService.gI().otptl((short) 558);
        Item gang = ItemService.gI().otptl((short) 564);
        Item giay = ItemService.gI().otptl((short) 565);
        Item nhan = ItemService.gI().otptl((short) 561);
        ao.itemOptions.add(new Item.ItemOption(131, 0));
        quan.itemOptions.add(new Item.ItemOption(131, 0));
        gang.itemOptions.add(new Item.ItemOption(131, 0));
        giay.itemOptions.add(new Item.ItemOption(131, 0));
        nhan.itemOptions.add(new Item.ItemOption(131, 0));
        ao.itemOptions.add(new Item.ItemOption(143, 0));
        quan.itemOptions.add(new Item.ItemOption(143, 0));
        gang.itemOptions.add(new Item.ItemOption(143, 0));
        giay.itemOptions.add(new Item.ItemOption(143, 0));
        nhan.itemOptions.add(new Item.ItemOption(143, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void set14summon(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1986);
        Item ao = ItemService.gI().otptl((short) 557);
        Item quan = ItemService.gI().otptl((short) 558);
        Item gang = ItemService.gI().otptl((short) 564);
        Item giay = ItemService.gI().otptl((short) 565);
        Item nhan = ItemService.gI().otptl((short) 561);
        ao.itemOptions.add(new Item.ItemOption(132, 0));
        quan.itemOptions.add(new Item.ItemOption(132, 0));
        gang.itemOptions.add(new Item.ItemOption(132, 0));
        giay.itemOptions.add(new Item.ItemOption(132, 0));
        nhan.itemOptions.add(new Item.ItemOption(132, 0));
        ao.itemOptions.add(new Item.ItemOption(144, 0));
        quan.itemOptions.add(new Item.ItemOption(144, 0));
        gang.itemOptions.add(new Item.ItemOption(144, 0));
        giay.itemOptions.add(new Item.ItemOption(144, 0));
        nhan.itemOptions.add(new Item.ItemOption(144, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void set14godgalick(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1986);
        Item ao = ItemService.gI().otptl((short) 559);
        Item quan = ItemService.gI().otptl((short) 560);
        Item gang = ItemService.gI().otptl((short) 566);
        Item giay = ItemService.gI().otptl((short) 567);
        Item nhan = ItemService.gI().otptl((short) 561);
        ao.itemOptions.add(new Item.ItemOption(133, 0));
        quan.itemOptions.add(new Item.ItemOption(133, 0));
        gang.itemOptions.add(new Item.ItemOption(133, 0));
        giay.itemOptions.add(new Item.ItemOption(133, 0));
        nhan.itemOptions.add(new Item.ItemOption(133, 0));
        ao.itemOptions.add(new Item.ItemOption(136, 0));
        quan.itemOptions.add(new Item.ItemOption(136, 0));
        gang.itemOptions.add(new Item.ItemOption(136, 0));
        giay.itemOptions.add(new Item.ItemOption(136, 0));
        nhan.itemOptions.add(new Item.ItemOption(136, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setmonkey14(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1986);
        Item ao = ItemService.gI().otptl((short) 559);
        Item quan = ItemService.gI().otptl((short) 560);
        Item gang = ItemService.gI().otptl((short) 566);
        Item giay = ItemService.gI().otptl((short) 567);
        Item nhan = ItemService.gI().otptl((short) 561);
        ao.itemOptions.add(new Item.ItemOption(134, 0));
        quan.itemOptions.add(new Item.ItemOption(134, 0));
        gang.itemOptions.add(new Item.ItemOption(134, 0));
        giay.itemOptions.add(new Item.ItemOption(134, 0));
        nhan.itemOptions.add(new Item.ItemOption(134, 0));
        ao.itemOptions.add(new Item.ItemOption(137, 0));
        quan.itemOptions.add(new Item.ItemOption(137, 0));
        gang.itemOptions.add(new Item.ItemOption(137, 0));
        giay.itemOptions.add(new Item.ItemOption(137, 0));
        nhan.itemOptions.add(new Item.ItemOption(137, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setgodhp14(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1986);
        Item ao = ItemService.gI().otptl((short) 559);
        Item quan = ItemService.gI().otptl((short) 560);
        Item gang = ItemService.gI().otptl((short) 566);
        Item giay = ItemService.gI().otptl((short) 567);
        Item nhan = ItemService.gI().otptl((short) 561);
        ao.itemOptions.add(new Item.ItemOption(135, 0));
        quan.itemOptions.add(new Item.ItemOption(135, 0));
        gang.itemOptions.add(new Item.ItemOption(135, 0));
        giay.itemOptions.add(new Item.ItemOption(135, 0));
        nhan.itemOptions.add(new Item.ItemOption(135, 0));
        ao.itemOptions.add(new Item.ItemOption(138, 0));
        quan.itemOptions.add(new Item.ItemOption(138, 0));
        gang.itemOptions.add(new Item.ItemOption(138, 0));
        giay.itemOptions.add(new Item.ItemOption(138, 0));
        nhan.itemOptions.add(new Item.ItemOption(138, 0));
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set hủy diệt  ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void settaiyoken19(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1985);
        Item ao = ItemService.gI().otpts((short) 1048);
        Item quan = ItemService.gI().otpts((short) 1051);
        Item gang = ItemService.gI().otpts((short) 1054);
        Item giay = ItemService.gI().otpts((short) 1057);
        Item nhan = ItemService.gI().otpts((short) 1060);
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setgenki19(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1985);
        Item ao = ItemService.gI().otpts((short) 1048);
        Item quan = ItemService.gI().otpts((short) 1051);
        Item gang = ItemService.gI().otpts((short) 1054);
        Item giay = ItemService.gI().otpts((short) 1057);
        Item nhan = ItemService.gI().otpts((short) 1060);
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setkamejoko19(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1985);
        Item ao = ItemService.gI().otpts((short) 1048);
        Item quan = ItemService.gI().otpts((short) 1051);
        Item gang = ItemService.gI().otpts((short) 1054);
        Item giay = ItemService.gI().otpts((short) 1057);
        Item nhan = ItemService.gI().otpts((short) 1060);
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setgodki19(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1985);
        Item ao = ItemService.gI().otpts((short) 1049);
        Item quan = ItemService.gI().otpts((short) 1052);
        Item gang = ItemService.gI().otpts((short) 1055);
        Item giay = ItemService.gI().otpts((short) 1058);
        Item nhan = ItemService.gI().otpts((short) 1061);
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setgoddam19(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1985);
        Item ao = ItemService.gI().otpts((short) 1049);
        Item quan = ItemService.gI().otpts((short) 1052);
        Item gang = ItemService.gI().otpts((short) 1055);
        Item giay = ItemService.gI().otpts((short) 1058);
        Item nhan = ItemService.gI().otpts((short) 1061);
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setsummon19(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1985);
        Item ao = ItemService.gI().otpts((short) 1049);
        Item quan = ItemService.gI().otpts((short) 1052);
        Item gang = ItemService.gI().otpts((short) 1055);
        Item giay = ItemService.gI().otpts((short) 1058);
        Item nhan = ItemService.gI().otpts((short) 1061);
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setgodgalick16(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1985);
        Item ao = ItemService.gI().otpts((short) 1050);
        Item quan = ItemService.gI().otpts((short) 1053);
        Item gang = ItemService.gI().otpts((short) 1056);
        Item giay = ItemService.gI().otpts((short) 1059);
        Item nhan = ItemService.gI().otpts((short) 1062);
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setmonkey16(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1985);
        Item ao = ItemService.gI().otpts((short) 1050);
        Item quan = ItemService.gI().otpts((short) 1053);
        Item gang = ItemService.gI().otpts((short) 1056);
        Item giay = ItemService.gI().otpts((short) 1059);
        Item nhan = ItemService.gI().otpts((short) 1062);
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public void setgodhp16(Player player) throws Exception {
//        for (int i = 0 ; i < 12;i++){
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1985);
        Item ao = ItemService.gI().otpts((short) 1050);
        Item quan = ItemService.gI().otpts((short) 1053);
        Item gang = ItemService.gI().otpts((short) 1056);
        Item giay = ItemService.gI().otpts((short) 1059);
        Item nhan = ItemService.gI().otpts((short) 1062);
        ao.itemOptions.add(new Item.ItemOption(30, 0));
        quan.itemOptions.add(new Item.ItemOption(30, 0));
        gang.itemOptions.add(new Item.ItemOption(30, 0));
        giay.itemOptions.add(new Item.ItemOption(30, 0));
        nhan.itemOptions.add(new Item.ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
//    }
    }

    public Item itemSKH(int itemId, int skhId) {
        Item item = createItemSetKichHoat(itemId, 1);
        if (item != null) {
            item.itemOptions.addAll(ItemService.gI().getListOptionItemShop((short) itemId));
            item.itemOptions.add(new Item.ItemOption(skhId, 1));
            item.itemOptions.add(new Item.ItemOption(optionIdSKH(skhId), 1));
            item.itemOptions.add(new Item.ItemOption(30, 1));
        }
        return item;
    }

    public int optionItemSKH(int typeItem) {
        switch (typeItem) {
            case 0:
                return 47;
            case 1:
                return 6;
            case 2:
                return 0;
            case 3:
                return 7;
            default:
                return 14;
        }
    }

    public int pagramItemSKH(int typeItem) {
        switch (typeItem) {
            case 0:
            case 2:
                return Util.nextInt(5);
            case 1:
            case 3:
                return Util.nextInt(20, 30);
            default:
                return Util.nextInt(3);
        }
    }

    public int optionIdSKH(int skhId) {
        switch (skhId) {
            case 127: //Set KAMI Taiyoken
                return 139;
            case 128: //Set KAMI Genki
                return 140;
            case 129: //Set KAMI Kamejoko
                return 141;
            case 130: //Set KAMI KI
                return 142;
            case 131: //Set KAMI Dame
                return 143;
            case 132: //Set KAMI Summon
                return 144;
            case 133: //Set KAMI Galick
                return 136;
            case 134: //Set KAMI Monkey
                return 137;
            case 135: //Set KAMI HP
                return 138;
        }
        return 0;
    }

    public Item itemDHD(int itemId, int dhdId) {
        Item item = createItemSetKichHoat(itemId, 1);
        if (item != null) {
            item.itemOptions.add(new Item.ItemOption(dhdId, 1));
            item.itemOptions.add(new Item.ItemOption(optionIdDHD(dhdId), 1));
            item.itemOptions.add(new Item.ItemOption(30, 1));
        }
        return item;
    }

    public int optionIdDHD(int skhId) {
        switch (skhId) {
            case 127: //Set KAMI Taiyoken
                return 139;
            case 128: //Set KAMI Genki
                return 140;
            case 129: //Set KAMI Kamejoko
                return 141;
            case 130: //Set KAMI KI
                return 142;
            case 131: //Set KAMI Dame
                return 143;
            case 132: //Set KAMI Summon
                return 144;
            case 133: //Set KAMI Galick
                return 136;
            case 134: //Set KAMI Monkey
                return 137;
            case 135: //Set KAMI HP
                return 138;
        }
        return 0;
    }

    public Item randomCS_DHD(int itemId, int gender) {
        Item it = createItemSetKichHoat(itemId, 1);
        List<Integer> ao = Arrays.asList(650, 652, 654);
        List<Integer> quan = Arrays.asList(651, 653, 655);
        List<Integer> gang = Arrays.asList(657, 659, 661);
        List<Integer> giay = Arrays.asList(658, 660, 662);
        int nhd = 656;
        if (ao.contains(itemId)) {
            it.itemOptions.add(new Item.ItemOption(47, Util.highlightsItem(gender == 2, new Random().nextInt(1001) + 1800))); // áo từ 1800-2800 giáp
        }
        if (quan.contains(itemId)) {
            it.itemOptions.add(new Item.ItemOption(22, Util.highlightsItem(gender == 0, new Random().nextInt(16) + 85))); // hp 85-100k
        }
        if (gang.contains(itemId)) {
            it.itemOptions.add(new Item.ItemOption(0, Util.highlightsItem(gender == 2, new Random().nextInt(150) + 8500))); // 8500-10000
        }
        if (giay.contains(itemId)) {
            it.itemOptions.add(new Item.ItemOption(23, Util.highlightsItem(gender == 1, new Random().nextInt(11) + 80))); // ki 80-90k
        }
        if (nhd == itemId) {
            it.itemOptions.add(new Item.ItemOption(14, new Random().nextInt(3) + 17)); //chí mạng 17-19%
        }
        it.itemOptions.add(new Item.ItemOption(21, 80));// yêu cầu sm 80 tỉ
        it.itemOptions.add(new Item.ItemOption(30, 1));// ko the gd
        return it;
    }

    //Cải trang sự kiện 20/11
    public Item caitrang2011(boolean rating) {
        Item item = createItemSetKichHoat(680, 1);
        item.itemOptions.add(new Item.ItemOption(76, 1));//VIP
        item.itemOptions.add(new Item.ItemOption(77, 28));//hp 28%
        item.itemOptions.add(new Item.ItemOption(103, 25));//ki 25%
        item.itemOptions.add(new Item.ItemOption(147, 24));//sd 26%
        item.itemOptions.add(new Item.ItemOption(117, 18));//Đẹp + 18% sd
        if (Util.isTrue(995, 1000) && rating) {// tỉ lệ ra hsd
            item.itemOptions.add(new Item.ItemOption(93, new Random().nextInt(3) + 1));//hsd
        }
        return item;
    }

    //610 - bong hoa
    //Phụ kiện bó hoa 20/11
    public Item phuKien2011(boolean rating) {
        Item item = createItemSetKichHoat(954, 1);
        item.itemOptions.add(new Item.ItemOption(77, new Random().nextInt(5) + 5));
        item.itemOptions.add(new Item.ItemOption(103, new Random().nextInt(5) + 5));
        item.itemOptions.add(new Item.ItemOption(147, new Random().nextInt(5) + 5));
        if (Util.isTrue(1, 100)) {
            item.itemOptions.get(Util.nextInt(item.itemOptions.size() - 1)).param = 10;
        }
        item.itemOptions.add(new Item.ItemOption(30, 1));//ko the gd
        if (Util.isTrue(995, 1000) && rating) {// tỉ lệ ra hsd
            item.itemOptions.add(new Item.ItemOption(93, new Random().nextInt(3) + 1));//hsd
        }
        return item;
    }

    public Item vanBay2011(boolean rating) {
        Item item = createItemSetKichHoat(795, 1);
        item.itemOptions.add(new Item.ItemOption(89, 1));
        item.itemOptions.add(new Item.ItemOption(30, 1));//ko the gd
        if (Util.isTrue(950, 1000) && rating) {// tỉ lệ ra hsd
            item.itemOptions.add(new Item.ItemOption(93, new Random().nextInt(3) + 1));//hsd
        }
        return item;
    }

    public Item daBaoVe() {
        Item item = createItemSetKichHoat(987, 1);
        item.itemOptions.add(new Item.ItemOption(30, 1));//ko the gd
        return item;
    }

    public Item randomRac() {
        short[] racs = {20, 19, 18, 17};
        Item item = createItemSetKichHoat(racs[Util.nextInt(racs.length - 1)], 1);
        if (optionRac(item.template.id) != 0) {
            item.itemOptions.add(new Item.ItemOption(optionRac(item.template.id), 1));
        }
        return item;
    }

    public byte optionRac(short itemId) {
        switch (itemId) {
            case 220:
                return 71;
            case 221:
                return 70;
            case 222:
                return 69;
            case 224:
                return 67;
            case 223:
                return 68;
            default:
                return 0;
        }
    }

    public void openBoxVip(Player player) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 1) {
            Service.gI().sendThongBao(player, "Bạn phải có ít nhất 2 ô trống hành trang");
            return;
        }
        if (player.inventory.event < 3000) {
            Service.gI().sendThongBao(player, "Bạn không đủ bông...");
            return;
        }
        Item item;
        if (Util.isTrue(45, 100)) {
            item = caitrang2011(false);
        } else {
            item = phuKien2011(false);
        }
        short[] icon = new short[2];
        icon[0] = 6983;
        icon[1] = item.template.iconID;
        InventoryServiceNew.gI().addItemBag(player, item);
        InventoryServiceNew.gI().sendItemBags(player);
        player.inventory.event -= 3000;
        Service.gI().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
        CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
    }

    public void giaobong(Player player, int quantity) {
        if (quantity > 10000) {
            return;
        }
        try {
            Item itemUse = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 610);
            if (itemUse.quantity < quantity) {
                Service.gI().sendThongBao(player, "Bạn không đủ bông...");
                return;
            }
            InventoryServiceNew.gI().subQuantityItemsBag(player, itemUse, quantity);
            Item item = createItemSetKichHoat(736, (quantity / 100));
            item.itemOptions.add(new Item.ItemOption(30, 1));//ko the gd
            InventoryServiceNew.gI().addItemBag(player, item);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.gI().sendThongBao(player, "Bạn đã nhận được x" + (quantity / 100) + " " + item.template.name);
        } catch (Exception e) {

            Service.gI().sendThongBao(player, "Bạn không đủ bông...");
        }
    }

    public Item PK_WC(int itemId) {
        Item phukien = createItemSetKichHoat(itemId, 1);
        int co = 983;
        int cup = 982;
        int bong = 966;
        if (cup == itemId) {
            phukien.itemOptions.add(new Item.ItemOption(77, new Random().nextInt(6) + 5)); // hp 5-10%
        }
        if (co == itemId) {
            phukien.itemOptions.add(new Item.ItemOption(103, new Random().nextInt(6) + 5)); // ki 5-10%
        }
        if (bong == itemId) {
            phukien.itemOptions.add(new Item.ItemOption(50, new Random().nextInt(6) + 5)); // sd 5- 10%
        }
        phukien.itemOptions.add(new Item.ItemOption(192, 1));//WORLDCUP
        phukien.itemOptions.add(new Item.ItemOption(193, 1));//(2 món kích hoạt ....)
        if (Util.isTrue(99, 100)) {// tỉ lệ ra hsd
            phukien.itemOptions.add(new Item.ItemOption(93, new Random().nextInt(2) + 1));//hsd
        }
        return phukien;
    }

    //Cải trang Gohan WC
    public Item CT_WC(boolean rating) {
        Item caitrang = createItemSetKichHoat(883, 1);
        caitrang.itemOptions.add(new Item.ItemOption(77, 30));// hp 30%
        caitrang.itemOptions.add(new Item.ItemOption(103, 15));// ki 15%
        caitrang.itemOptions.add(new Item.ItemOption(50, 20));// sd 20%
        caitrang.itemOptions.add(new Item.ItemOption(192, 1));//WORLDCUP
        caitrang.itemOptions.add(new Item.ItemOption(193, 1));//(2 món kích hoạt ....)
        if (Util.isTrue(99, 100) && rating) {// tỉ lệ ra hsd
            caitrang.itemOptions.add(new Item.ItemOption(93, new Random().nextInt(2) + 1));//hsd
        }
        return caitrang;
    }

    public void openDTS(Player player) {
        //check sl đồ tl, đồ hd
        if (player.combineNew.itemsCombine.stream().filter(item -> item.template.id >= 555 && item.template.id <= 567).count() < 1) {
            Service.gI().sendThongBao(player, "Thiếu đồ thần linh");
            return;
        }
        if (player.combineNew.itemsCombine.stream().filter(item -> item.template.id >= 650 && item.template.id <= 662).count() < 2) {
            Service.gI().sendThongBao(player, "Thiếu đồ hủy diệt");
            return;
        }
        if (player.combineNew.itemsCombine.size() != 3) {
            Service.gI().sendThongBao(player, "Thiếu đồ");
            return;
        }
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            Item itemTL = player.combineNew.itemsCombine.stream().filter(item -> item.template.id >= 555 && item.template.id <= 567).findFirst().get();
            List<Item> itemHDs = player.combineNew.itemsCombine.stream().filter(item -> item.template.id >= 650 && item.template.id <= 662).collect(Collectors.toList());
            short[][] itemIds = {{1048, 1051, 1054, 1057, 1060}, {1049, 1052, 1055, 1058, 1061}, {1050, 1053, 1056, 1059, 1062}}; // thứ tự td - 0,nm - 1, xd - 2

            Item itemTS = DoThienSu(itemIds[player.gender][itemTL.template.type], player.gender);
            InventoryServiceNew.gI().addItemBag(player, itemTS);

            InventoryServiceNew.gI().subQuantityItemsBag(player, itemTL, 1);
            itemHDs.forEach(item -> InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1));

            InventoryServiceNew.gI().sendItemBags(player);
            Service.gI().sendThongBao(player, "Bạn đã nhận được " + itemTS.template.name);
        } else {
            Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

    public Item DoThienSu(int itemId, int gender) {
        Item dots = createItemSetKichHoat(itemId, 1);
        List<Integer> ao = Arrays.asList(1048, 1049, 1050);
        List<Integer> quan = Arrays.asList(1051, 1052, 1053);
        List<Integer> gang = Arrays.asList(1054, 1055, 1056);
        List<Integer> giay = Arrays.asList(1057, 1058, 1059);
        List<Integer> nhan = Arrays.asList(1060, 1061, 1062);
        //áo
        if (ao.contains(itemId)) {
            dots.itemOptions.add(new Item.ItemOption(47, Util.highlightsItem(gender == 2, new Random().nextInt(1201) + 2800))); // áo từ 2800-4000 giáp
        }
        //quần
        if (Util.isTrue(80, 100)) {
            if (quan.contains(itemId)) {
                dots.itemOptions.add(new Item.ItemOption(22, Util.highlightsItem(gender == 0, new Random().nextInt(11) + 120))); // hp 120k-130k
            }
        } else {
            if (quan.contains(itemId)) {
                dots.itemOptions.add(new Item.ItemOption(22, Util.highlightsItem(gender == 0, new Random().nextInt(21) + 130))); // hp 130-150k 15%
            }
        }
        //găng
        if (Util.isTrue(80, 100)) {
            if (gang.contains(itemId)) {
                dots.itemOptions.add(new Item.ItemOption(0, Util.highlightsItem(gender == 2, new Random().nextInt(651) + 9350))); // 9350-10000
            }
        } else {
            if (gang.contains(itemId)) {
                dots.itemOptions.add(new Item.ItemOption(0, Util.highlightsItem(gender == 2, new Random().nextInt(1001) + 10000))); // gang 15% 10-11k -xayda 12k1
            }
        }
        //giày
        if (Util.isTrue(80, 100)) {
            if (giay.contains(itemId)) {
                dots.itemOptions.add(new Item.ItemOption(23, Util.highlightsItem(gender == 1, new Random().nextInt(21) + 90))); // ki 90-110k
            }
        } else {
            if (giay.contains(itemId)) {
                dots.itemOptions.add(new Item.ItemOption(23, Util.highlightsItem(gender == 1, new Random().nextInt(21) + 110))); // ki 110-130k
            }
        }

        if (nhan.contains(itemId)) {
            dots.itemOptions.add(new Item.ItemOption(14, Util.highlightsItem(gender == 1, new Random().nextInt(3) + 18))); // nhẫn 18-20%
        }
        dots.itemOptions.add(new Item.ItemOption(21, 120));
        dots.itemOptions.add(new Item.ItemOption(30, 1));
        return dots;
    }

    public List<Item.ItemOption> getListOptionItemShop(short id) {
        List<Item.ItemOption> list = new ArrayList<>();
        Manager.SHOPS.forEach(shop -> shop.tabShops.forEach(tabShop -> tabShop.itemShops.forEach(itemShop -> {
            if (itemShop.temp.id == id && list.size() == 0) {
                list.addAll(itemShop.options);
            }
        })));
        return list;
    }
}
