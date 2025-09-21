package Dragon.jdbc.daos;

import Dragon.models.map.ItemMap;
import Dragon.models.mob.Mob;
import Dragon.models.player.Player;
import Dragon.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class MobRewardService {

    private static MobRewardService instance;
    private MobRewardCache cache;

    public static MobRewardService getInstance() {
        if (instance == null) {
            instance = new MobRewardService();
        }
        return instance;
    }

    private MobRewardService() {
        this.cache = MobRewardCache.getInstance();
    }

    /**
     * Process rewards khi mob bị giết từ cache
     */
    public List<ItemMap> processRewards(Mob mob, Player player, int x, int yEnd) {
        List<ItemMap> sqlDrops = new ArrayList<>();
        List<MobRewardCache.MobRewardGroup> groups = cache.getMobRewardGroups(mob.tempId);
        for (MobRewardCache.MobRewardGroup group : groups) {
            if (checkGroupRestrictions(group, mob, player)) {
                List<MobRewardCache.MobRewardItem> items = cache.getRewardItems(group.id);
                for (MobRewardCache.MobRewardItem item : items) {
                    if (shouldDropReward(item)) {
                        ItemMap itemMap = createItemMap(mob, player, x, yEnd, item);
                        if (itemMap != null) {
                            sqlDrops.add(itemMap);
                        }
                    }
                }
            }
        }
        return sqlDrops;
    }

    /**
     * Refresh cache khi cần update data
     */
    public void refreshCache() {
        cache.refreshCache();
    }

    /**
     * Get cache statistics
     */
    public String getCacheStats() {
        return cache.getCacheStats();
    }

    /**
     * Check xem có nên drop reward không
     */
    private boolean shouldDropReward(MobRewardCache.MobRewardItem item) {
        return Dragon.utils.Util.isTrue((int) item.dropRate, 100);
    }

    /**
     * Check group restrictions (map và planet)
     */
    private boolean checkGroupRestrictions(MobRewardCache.MobRewardGroup group, Mob mob, Player player) {
        // Check map restriction
        if (!checkMapRestriction(group.mapRestriction, mob.zone.map.mapId)) {
            return false;
        }

        // Check planet restriction
        if (!checkPlanetRestriction(group.planetRestriction, player.gender)) {
            return false;
        }

        return true;
    }

    /**
     * Check planet restriction
     */
    private boolean checkPlanetRestriction(int planetRestriction, int playerGender) {
        if (planetRestriction == -1) {
            return true; // No restriction
        }
        return planetRestriction == playerGender;
    }

    /**
     * Check map restriction với logic phức tạp
     */
    private boolean checkMapRestriction(String mapRestriction, int mapId) {
        if (mapRestriction == null || mapRestriction.isEmpty()) {
            return true; // No restriction
        }

        try {
            String restriction = mapRestriction.trim();

            // Xử lý exclusion (!)
            if (restriction.startsWith("!")) {
                return !checkMapInRange(restriction.substring(1), mapId);
            }

            // Xử lý inclusion
            return checkMapInRange(restriction, mapId);

        } catch (Exception e) {
            Logger.logException(MobRewardService.class, e);
            return false; // Safe fallback
        }
    }

    /**
     * Kiểm tra map có trong range không
     */
    private boolean checkMapInRange(String rangeStr, int mapId) {
        String[] ranges = rangeStr.split(",");

        for (String range : ranges) {
            range = range.trim();

            if (range.contains("-")) {
                String[] parts = range.split("-");
                if (parts.length == 2) {
                    int start = Integer.parseInt(parts[0].trim());
                    int end = Integer.parseInt(parts[1].trim());
                    if (mapId >= start && mapId <= end) {
                        return true;
                    }
                }
            } else {
                int singleMap = Integer.parseInt(range);
                if (mapId == singleMap) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Create ItemMap từ reward item
     */
    private ItemMap createItemMap(Mob mob, Player player, int x, int yEnd, MobRewardCache.MobRewardItem item) {
        // Calculate quantity
        int quantity = item.quantityMin;
        if (item.quantityMax > item.quantityMin) {
            quantity = Dragon.utils.Util.nextInt(item.quantityMin, item.quantityMax);
        }

        // Create ItemMap
        ItemMap itemMap = new ItemMap(mob.zone, item.itemId, quantity, x, yEnd, player.id);

        // Get and add options from cache
        List<MobRewardCache.MobRewardOption> options = cache.getRewardOptions(item.id);
        if (!options.isEmpty()) {
            itemMap.options = new ArrayList<>();
            for (MobRewardCache.MobRewardOption option : options) {
                Dragon.models.item.Item.ItemOption itemOption = new Dragon.models.item.Item.ItemOption(option.optionId,
                        option.param);
                itemMap.options.add(itemOption);
            }
        }

        return itemMap;
    }

}
