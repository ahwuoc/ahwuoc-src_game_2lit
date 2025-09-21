package Dragon.services;

import Dragon.jdbc.daos.MobRewardService;
import Dragon.models.item.Item;
import Dragon.models.map.ItemMap;
import Dragon.models.mob.Mob;
import Dragon.models.player.Player;
// import Dragon.models.reward.ItemMobReward; // COMMENTED OUT FOR SQL
import Dragon.models.reward.MobReward;
import Dragon.server.Manager;
import Dragon.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler class for managing mob item drops when killed
 */
public class MobDropHandler {

    public static List<ItemMap> getItemMobReward(Mob mob, Player player, int x, int yEnd) {
        Dragon.utils.Logger.log("MobDropHandler: Starting getItemMobReward for mob ID: " + mob.tempId
                + ", player: " + player.name + ", map: " + player.zone.map.mapId);

        List<ItemMap> list = new ArrayList<>();
        MobReward mobReward = Manager.MOB_REWARDS.get(mob.tempId);
        if (mobReward == null) {
            Dragon.utils.Logger.log("MobDropHandler: No mob reward config found for mob ID: " + mob.tempId);
            return list;
        }

        Dragon.utils.Logger.log("MobDropHandler: Found mob reward config for mob ID: " + mob.tempId);

        if (MapStart(player.zone.map.mapId)) {
            Dragon.utils.Logger.log("MobDropHandler: Map is in start state, returning empty list");
            return new ArrayList<>();
        }

        // List<ItemMobReward> items = mobReward.getItemReward();
        // List<ItemMobReward> golds = mobReward.getGoldReward();
        handleSqlDrops(mob, player, x, yEnd, list);
        Dragon.utils.Logger.log("MobDropHandler: Final result - Total items to drop: " + list.size()
                + " for mob ID: " + mob.tempId + ", player: " + player.name);
        return list;
    }

    /**
     * Handles special item drops based on map and mob conditions - COMMENTED
     * OUT FOR SQL
     */
    @SuppressWarnings("unused")
    private static void handleSpecialItemDrops(Mob mob, Player player, int x, int yEnd, List<ItemMap> list) {
        if (mob.tempId > 0 && mob.zone.map.mapId >= 0 && mob.zone.map.mapId <= 3) {
            if (Util.isTrue(5, 100)) { // up bí kíp phước
                list.add(new ItemMap(mob.zone, 590, 1, x, player.location.y, player.id));
            }
        }
        if (mob.tempId > 0 && mob.zone.map.mapId >= 3 && mob.zone.map.mapId <= 99) {
            if (Util.isTrue(5, 100)) { // up nr phước
                list.add(new ItemMap(mob.zone, 20, 1, x, player.location.y, player.id));
            }
        }
    }

    /**
     * Handles crystal star drops - COMMENTED OUT FOR SQL
     */
    @SuppressWarnings("unused")
    private static void handleCrystalStarDrops(Mob mob, Player player, int x, int yEnd, List<ItemMap> list) {
        // Danh sách thông tin các sao pha lê: {itemId, optionId, optionLevel}
        int[][] saoPhaLeInfo = {
            {441, 95, 5}, // Sao Pha Lê Đỏ
            {442, 96, 5}, // Sao Pha Lê Xanh Dương
            {443, 97, 5}, // Sao Pha Lê Hồng
            {444, 98, 5}, // Sao Pha Lê Tím
            {445, 99, 5}, // Sao Pha Lê Cam
            {446, 10, 5}, // Sao Pha Lê Vàng
            {447, 101, 5} // Sao Pha Lê Xanh Lá Cây
        };

        if (mob.zone.map.mapId >= 3 && mob.zone.map.mapId <= 99) {
            if (Util.isTrue(5, 100)) { // Xác suất chung 10%
                // Random chọn 1 loại sao pha lên
                int randomIndex = Util.nextInt(0, saoPhaLeInfo.length - 1); // Random từ 0 đến 6
                int[] selectedSao = saoPhaLeInfo[randomIndex];

                // Tạo sao pha lê
                Item saoPhaLe = ItemService.gI().createNewItem((short) selectedSao[0]);
                saoPhaLe.itemOptions.add(new Item.ItemOption(selectedSao[1], selectedSao[2])); // Thêm option

                // Thêm vào túi và gửi thông báo
                InventoryServiceNew.gI().addItemBag(player, saoPhaLe);
                InventoryServiceNew.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn Vừa Nhận Được " + saoPhaLe.template.name);
            }
        }
    }

    /**
     * Handles random item drops (item 1804) - COMMENTED OUT FOR SQL
     */
    @SuppressWarnings("unused")
    private static void handleRandomItemDrops(Mob mob, Player player, int x, int yEnd, List<ItemMap> list) {
        if (Util.isTrue(30, 100)) { // Tỷ lệ 30%
            int quantity = Util.nextInt(1, 5); // Random số lượng từ 1-5
            ItemMap item1804 = new ItemMap(mob.zone, 1804, quantity, x, yEnd, player.id);
            list.add(item1804);
        }
    }

    /**
     * Handles equipment drops with random options - COMMENTED OUT FOR SQL
     */
    @SuppressWarnings("unused")
    private static void handleEquipmentDrops(Mob mob, Player player, int x, int yEnd, List<ItemMap> list) {
        if (mob.zone != null && mob.zone.map != null) {
            int[][] itemIds = new int[][]{
                // Trái đất
                {0, 6, 21, 27, 12}, // level 1
                {33, 35, 24, 30, 57}, // level 2
                {3, 9, 37, 39, 58}, // level 3
                {34, 36, 38, 40, 59}, // level 4
                {136, 140, 144, 148, 184}, // level 5
                {137, 141, 145, 149, 185}, // level 6
                {138, 142, 146, 150, 186}, // level 7
                {139, 143, 147, 151, 187}, // level 8
                {230, 242, 254, 266, 278}, // level 9
                {231, 243, 255, 267, 279}, // level 10
                {232, 244, 256, 268, 280}, // level 11
                {233, 245, 257, 269, 281}, // level 12
                // Namec
                {1, 7, 22, 28, 12}, // level 1
                {41, 43, 46, 47, 57}, // level 2
                {4, 10, 25, 31, 58}, // level 3
                {42, 44, 45, 48, 59}, // level 4
                {152, 156, 160, 164, 184}, // level 5
                {153, 157, 161, 165, 185}, // level 6
                {154, 158, 162, 166, 186}, // level 7
                {155, 159, 163, 167, 187}, // level 8
                {234, 246, 258, 270, 278}, // level 9
                {235, 247, 259, 271, 279}, // level 10
                {236, 248, 260, 272, 280}, // level 11
                {237, 249, 261, 273, 281}, // level 12
                // Xayda
                {2, 8, 23, 29, 12}, // level 1
                {49, 51, 53, 55, 57}, // level 2
                {5, 11, 26, 32, 58}, // level 3
                {50, 52, 54, 56, 59}, // level 4
                {168, 172, 176, 180, 184}, // level 5
                {169, 173, 177, 181, 185}, // level 6
                {170, 174, 178, 182, 186}, // level 7
                {171, 175, 179, 183, 187}, // level 8
                {238, 250, 262, 274, 278}, // level 9
                {239, 251, 263, 275, 279}, // level 10
                {240, 252, 264, 276, 280}, // level 11
                {241, 253, 265, 277, 281},
                // thần linh và hủy diệt
                {555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 567},
                {650, 651, 652, 653, 654, 655, 656, 657, 658, 659, 660, 661, 662}// level 12
            };

            if (Util.isTrue(2, 100)) {
                if (itemIds != null) {
                    // random level
                    int levelIndex = Util.nextInt(0, itemIds.length - 1);
                    int[] possibleItems = itemIds[levelIndex];

                    // random item trong level đó
                    int itemId = possibleItems[Util.nextInt(0, possibleItems.length - 1)];

                    // tạo item
                    Item item = ItemService.gI().createNewItem((short) itemId);

                    // ★ Thêm Option ngẫu nhiên
                    addRandomItemOptions(item);

                    // rớt ra map
                    ItemMap itemMap = new ItemMap(mob.zone, item.template.id, 1, x, yEnd, player.id);
                    itemMap.options = item.itemOptions; // gán option cho itemMap
                    list.add(itemMap);
                }
            }
        }
    }

    /**
     * Adds random options to an item based on probability
     */
    private static void addRandomItemOptions(Item item) {
        if (Util.isTrue(20, 100)) {
            item.itemOptions.add(new Item.ItemOption(0,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 5000) : Util.nextInt(5, 2500)));
            item.itemOptions.add(new Item.ItemOption(6,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 100000) : Util.nextInt(5, 90000)));
            item.itemOptions.add(new Item.ItemOption(7,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 100000) : Util.nextInt(5, 90000)));
        } else if (Util.isTrue(15, 100)) {
            item.itemOptions.add(new Item.ItemOption(0,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 15000) : Util.nextInt(5, 5000)));
            item.itemOptions.add(new Item.ItemOption(6,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 350000) : Util.nextInt(5, 250000)));
            item.itemOptions.add(new Item.ItemOption(7,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 350000) : Util.nextInt(5, 250000)));
            item.itemOptions.add(new Item.ItemOption(47,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 2000) : Util.nextInt(5, 1000)));
        } else if (Util.isTrue(10, 100)) {
            item.itemOptions.add(new Item.ItemOption(0,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 20000) : Util.nextInt(5, 10000)));
            item.itemOptions.add(new Item.ItemOption(6,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 550000) : Util.nextInt(5, 450000)));
            item.itemOptions.add(new Item.ItemOption(7,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 550000) : Util.nextInt(5, 450000)));
            item.itemOptions.add(new Item.ItemOption(95,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 50) : Util.nextInt(5, 40)));
            item.itemOptions.add(new Item.ItemOption(96,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 50) : Util.nextInt(5, 40)));

        } else if (Util.isTrue(5, 100)) {
            item.itemOptions.add(new Item.ItemOption(0,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 30000) : Util.nextInt(5, 15000)));
            item.itemOptions.add(new Item.ItemOption(6,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 1000000) : Util.nextInt(5, 900000)));
            item.itemOptions.add(new Item.ItemOption(7,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 1000000) : Util.nextInt(5, 900000)));
            item.itemOptions.add(new Item.ItemOption(108,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 50) : Util.nextInt(5, 40)));
        } else if (Util.isTrue(2, 100)) {
            item.itemOptions.add(new Item.ItemOption(50,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 50) : Util.nextInt(5, 25)));
            item.itemOptions.add(new Item.ItemOption(77,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 50) : Util.nextInt(5, 25)));
            item.itemOptions.add(new Item.ItemOption(103,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 50) : Util.nextInt(5, 25)));
            item.itemOptions.add(new Item.ItemOption(48,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 1000000) : Util.nextInt(5, 900000)));
        } else {
            item.itemOptions.add(new Item.ItemOption(50,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 50) : Util.nextInt(5, 25)));
            item.itemOptions.add(new Item.ItemOption(77,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 50) : Util.nextInt(5, 25)));
            item.itemOptions.add(new Item.ItemOption(103,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 50) : Util.nextInt(5, 25)));
            item.itemOptions.add(new Item.ItemOption(48,
                    Util.isTrue(5, 100) ? Util.nextInt(10, 1000000) : Util.nextInt(5, 900000)));
        }
    }

    /**
     * Handles SQL-based drops from database
     */
    private static void handleSqlDrops(Mob mob, Player player, int x, int yEnd, List<ItemMap> list) {
        try {
            Dragon.utils.Logger
                    .log("MobDropHandler: Starting SQL drops for mob ID: " + mob.tempId + ", player: " + player.name);
            List<ItemMap> sqlDrops = MobRewardService.getInstance().processRewards(mob, player, x, yEnd);
            Dragon.utils.Logger.log("MobDropHandler: SQL drops returned " + sqlDrops.size() + " items");
            list.addAll(sqlDrops);
            Dragon.utils.Logger.log("MobDropHandler: Total items after SQL drops: " + list.size());
        } catch (Exception e) {
            // Log error but don't break the drop system
            Dragon.utils.Logger.logException(MobDropHandler.class, e);
        }
    }

    /**
     * Checks if map is in start state (placeholder method - needs
     * implementation)
     */
    private static boolean MapStart(int mapId) {
        // TODO: Implement map start check logic
        return false;
    }
}
