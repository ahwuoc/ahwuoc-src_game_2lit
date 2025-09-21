package Dragon.jdbc.daos;

import Dragon.models.mob.Mob;
import Dragon.models.player.Player;
import Dragon.utils.Logger;
import com.girlkun.database.GirlkunDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MobRewardCache {

    private static MobRewardCache instance;

    // Cache maps
    private Map<Integer, List<MobRewardGroup>> groupsCache = new ConcurrentHashMap<>();
    private Map<Integer, List<MobRewardItem>> itemsCache = new ConcurrentHashMap<>();
    private Map<Integer, List<MobRewardOption>> optionsCache = new ConcurrentHashMap<>();

    // Cache status
    private boolean isInitialized = false;
    private long lastRefreshTime = 0;

    public static MobRewardCache getInstance() {
        if (instance == null) {
            instance = new MobRewardCache();
        }
        return instance;
    }

    /**
     * Initialize cache - Load tất cả data từ database vào memory
     */
    public void initializeCache() {
        try {
            loadAllGroups();
            loadAllItems();
            loadAllOptions();

            isInitialized = true;
            lastRefreshTime = System.currentTimeMillis();

        } catch (Exception e) {
            Logger.logException(MobRewardCache.class, e);
            Logger.log("MobRewardCache: Failed to initialize cache!");
        }
    }

    /**
     * Load tất cả groups từ database
     */
    private void loadAllGroups() {
        Connection con = null;
        try {
            con = GirlkunDB.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT id, mob_id, map_restriction, planet_restriction, is_active "
                    + "FROM mob_reward_groups WHERE is_active = 1 ORDER BY mob_id, id");
            ResultSet rs = ps.executeQuery();

            Map<Integer, List<MobRewardGroup>> tempGroups = new HashMap<>();

            while (rs.next()) {
                MobRewardGroup group = new MobRewardGroup();
                group.id = rs.getInt("id");
                group.mobId = rs.getInt("mob_id");
                group.mapRestriction = rs.getString("map_restriction");
                group.planetRestriction = rs.getInt("planet_restriction");
                group.isActive = rs.getBoolean("is_active");

                tempGroups.computeIfAbsent(group.mobId, k -> new ArrayList<>()).add(group);
            }

            groupsCache.clear();
            groupsCache.putAll(tempGroups);

            rs.close();
            ps.close();

        } catch (Exception e) {
            Logger.logException(MobRewardCache.class, e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    Logger.logException(MobRewardCache.class, e);
                }
            }
        }
    }

    /**
     * Load tất cả items từ database
     */
    private void loadAllItems() {
        Connection con = null;
        try {
            con = GirlkunDB.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT id, group_id, item_id, quantity_min, quantity_max, drop_rate "
                    + "FROM mob_reward_items ORDER BY group_id, id");
            ResultSet rs = ps.executeQuery();

            Map<Integer, List<MobRewardItem>> tempItems = new HashMap<>();

            while (rs.next()) {
                MobRewardItem item = new MobRewardItem();
                item.id = rs.getInt("id");
                item.groupId = rs.getInt("group_id");
                item.itemId = rs.getShort("item_id");
                item.quantityMin = rs.getInt("quantity_min");
                item.quantityMax = rs.getInt("quantity_max");
                item.dropRate = rs.getDouble("drop_rate");

                tempItems.computeIfAbsent(item.groupId, k -> new ArrayList<>()).add(item);
            }

            itemsCache.clear();
            itemsCache.putAll(tempItems);

            rs.close();
            ps.close();

        } catch (Exception e) {
            Logger.logException(MobRewardCache.class, e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    Logger.logException(MobRewardCache.class, e);
                }
            }
        }
    }

    /**
     * Load tất cả options từ database
     */
    private void loadAllOptions() {
        Connection con = null;
        try {
            con = GirlkunDB.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT option_id, param, reward_item_id "
                    + "FROM mob_reward_item_options ORDER BY reward_item_id, id");
            ResultSet rs = ps.executeQuery();

            Map<Integer, List<MobRewardOption>> tempOptions = new HashMap<>();

            while (rs.next()) {
                MobRewardOption option = new MobRewardOption();
                option.optionId = rs.getInt("option_id");
                option.param = rs.getInt("param");
                int rewardItemId = rs.getInt("reward_item_id");

                tempOptions.computeIfAbsent(rewardItemId, k -> new ArrayList<>()).add(option);
            }

            optionsCache.clear();
            optionsCache.putAll(tempOptions);

            rs.close();
            ps.close();

        } catch (Exception e) {
            Logger.logException(MobRewardCache.class, e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    Logger.logException(MobRewardCache.class, e);
                }
            }
        }
    }

    /**
     * Get reward groups của mob từ cache
     */
    public List<MobRewardGroup> getMobRewardGroups(int mobId) {
        if (!isInitialized) {
            Logger.log("MobRewardCache: Cache not initialized, returning empty list");
            return new ArrayList<>();
        }

        return groupsCache.getOrDefault(mobId, new ArrayList<>());
    }

    /**
     * Get reward items của một group từ cache
     */
    public List<MobRewardItem> getRewardItems(int groupId) {
        if (!isInitialized) {
            return new ArrayList<>();
        }

        return itemsCache.getOrDefault(groupId, new ArrayList<>());
    }

    /**
     * Get options của một reward item từ cache
     */
    public List<MobRewardOption> getRewardOptions(int rewardItemId) {
        if (!isInitialized) {
            return new ArrayList<>();
        }

        return optionsCache.getOrDefault(rewardItemId, new ArrayList<>());
    }

    /**
     * Refresh cache - Reload tất cả data từ database
     */
    public void refreshCache() {
        Logger.log("MobRewardCache: Refreshing cache...");
        initializeCache();
    }

    /**
     * Check cache status
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Get cache statistics
     */
    public String getCacheStats() {
        if (!isInitialized) {
            return "Cache not initialized";
        }

        return String.format("Cache Stats - Groups: %d, Items: %d, Options: %d, Last Refresh: %d ms ago",
                groupsCache.size(), itemsCache.size(), optionsCache.size(),
                System.currentTimeMillis() - lastRefreshTime);
    }

    /**
     * Clear cache
     */
    public void clearCache() {
        groupsCache.clear();
        itemsCache.clear();
        optionsCache.clear();
        isInitialized = false;
        Logger.log("MobRewardCache: Cache cleared");
    }

    /**
     * Inner classes để chứa reward data
     */
    public static class MobRewardGroup {

        public int id;
        public int mobId;
        public String mapRestriction;
        public int planetRestriction;
        public boolean isActive;
    }

    public static class MobRewardItem {

        public int id;
        public int groupId;
        public short itemId;
        public int quantityMin;
        public int quantityMax;
        public double dropRate;
    }

    public static class MobRewardOption {

        public int optionId;
        public int param;
    }
}
