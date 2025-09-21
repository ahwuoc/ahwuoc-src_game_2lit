package Dragon.jdbc.daos;

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

public class BossRewardCache {

    private static BossRewardCache instance;

    // Cache maps
    private Map<Integer, List<BossReward>> rewardsCache = new ConcurrentHashMap<>();
    private Map<Integer, List<BossRewardOption>> optionsCache = new ConcurrentHashMap<>();

    // Cache status
    private boolean isInitialized = false;
    private long lastRefreshTime = 0;

    public static BossRewardCache getInstance() {
        if (instance == null) {
            instance = new BossRewardCache();
        }
        return instance;
    }

    /**
     * Initialize cache - Load tất cả boss rewards từ database vào memory
     */
    public void initializeCache() {
        try {
            // Initialize cache silently
            loadAllBossRewards();
            loadAllBossRewardOptions();

            isInitialized = true;
            lastRefreshTime = System.currentTimeMillis();

        } catch (Exception e) {
            Logger.logException(BossRewardCache.class, e);
            Logger.log("BossRewardCache: Failed to initialize cache!");
        }
    }
    /**
     * Load tất cả boss rewards từ database
     */
    private void loadAllBossRewards() {
        Connection con = null;
        try {
            con = GirlkunDB.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT id, boss_id, item_id, quantity, drop_rate FROM boss_rewards ORDER BY boss_id");
            ResultSet rs = ps.executeQuery();

            Map<Integer, List<BossReward>> tempRewards = new HashMap<>();

            while (rs.next()) {
                BossReward reward = new BossReward();
                reward.id = rs.getInt("id");
                reward.bossId = rs.getInt("boss_id");
                reward.itemId = rs.getInt("item_id");
                reward.quantity = rs.getInt("quantity");
                reward.dropRate = rs.getDouble("drop_rate");

                tempRewards.computeIfAbsent(reward.bossId, k -> new ArrayList<>()).add(reward);
            }

            rewardsCache.clear();
            rewardsCache.putAll(tempRewards);

            rs.close();
            ps.close();

        } catch (Exception e) {
            Logger.logException(BossRewardCache.class, e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    Logger.logException(BossRewardCache.class, e);
                }
            }
        }
    }

    /**
     * Load tất cả boss reward options từ database
     */
    private void loadAllBossRewardOptions() {
        Connection con = null;
        try {
            con = GirlkunDB.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT boss_reward_id, option_id, param FROM boss_reward_options ORDER BY boss_reward_id");
            ResultSet rs = ps.executeQuery();

            Map<Integer, List<BossRewardOption>> tempOptions = new HashMap<>();

            while (rs.next()) {
                BossRewardOption option = new BossRewardOption();
                option.optionId = rs.getInt("option_id");
                option.param = rs.getInt("param");
                int bossRewardId = rs.getInt("boss_reward_id");

                tempOptions.computeIfAbsent(bossRewardId, k -> new ArrayList<>()).add(option);
            }

            optionsCache.clear();
            optionsCache.putAll(tempOptions);

            rs.close();
            ps.close();

        } catch (Exception e) {
            Logger.logException(BossRewardCache.class, e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    Logger.logException(BossRewardCache.class, e);
                }
            }
        }
    }

    /**
     * Get boss rewards từ cache
     */
    public List<BossReward> getBossRewards(int bossId) {
        if (!isInitialized) {
            Logger.log("BossRewardCache: Cache not initialized, returning empty list");
            return new ArrayList<>();
        }

        return rewardsCache.getOrDefault(bossId, new ArrayList<>());
    }

    /**
     * Get boss reward options từ cache
     */
    public List<BossRewardOption> getBossRewardOptions(int bossRewardId) {
        if (!isInitialized) {
            return new ArrayList<>();
        }

        return optionsCache.getOrDefault(bossRewardId, new ArrayList<>());
    }

    /**
     * Refresh cache - Reload tất cả data từ database
     */
    public void refreshCache() {
        Logger.log("BossRewardCache: Refreshing cache...");
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

        int totalRewards = rewardsCache.values().stream()
                .mapToInt(List::size)
                .sum();

        int totalOptions = optionsCache.values().stream()
                .mapToInt(List::size)
                .sum();

        return String.format(
                "BossRewardCache Stats - Bosses: %d, Total Rewards: %d, Total Options: %d, Last Refresh: %d ms ago",
                rewardsCache.size(), totalRewards, totalOptions,
                System.currentTimeMillis() - lastRefreshTime);
    }

    /**
     * Clear cache
     */
    public void clearCache() {
        rewardsCache.clear();
        optionsCache.clear();
        isInitialized = false;
        Logger.log("BossRewardCache: Cache cleared");
    }

    /**
     * Inner classes để chứa boss reward data
     */
    public static class BossReward {

        public int id;
        public int bossId;
        public int itemId;
        public int quantity;
        public double dropRate;

        @Override
        public String toString() {
            return String.format("BossReward{id=%d, bossId=%d, itemId=%d, quantity=%d, dropRate=%.2f}",
                    id, bossId, itemId, quantity, dropRate);
        }
    }

    public static class BossRewardOption {

        public int optionId;
        public int param;

        @Override
        public String toString() {
            return String.format("BossRewardOption{optionId=%d, param=%d}", optionId, param);
        }
    }
}
