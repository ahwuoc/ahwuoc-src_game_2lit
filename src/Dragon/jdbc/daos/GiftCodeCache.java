package Dragon.jdbc.daos;

import Dragon.utils.Logger;
import com.girlkun.database.GirlkunDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GiftCodeCache {

    private static GiftCodeCache instance;

    // Cache maps
    private Map<String, GiftCodeData> giftCodesCache = new ConcurrentHashMap<>();
    private Map<Integer, List<GiftCodeItem>> itemsCache = new ConcurrentHashMap<>();
    private Map<Integer, List<GiftCodeItemOption>> optionsCache = new ConcurrentHashMap<>();
    private Map<Integer, List<Integer>> playerRestrictionsCache = new ConcurrentHashMap<>();

    // Cache status
    private boolean isInitialized = false;
    private long lastRefreshTime = 0;

    public static GiftCodeCache getInstance() {
        if (instance == null) {
            instance = new GiftCodeCache();
        }
        return instance;
    }

    /**
     * Initialize cache - Load tất cả gift code data từ database vào memory
     */
    public void initializeCache() {
        try {
            // Initialize cache silently
            loadAllGiftCodes();
            loadAllGiftCodeItems();
            loadAllGiftCodeItemOptions();
            loadAllPlayerRestrictions();

            isInitialized = true;
            lastRefreshTime = System.currentTimeMillis();
            Logger.log("GiftCodeCache: Loaded " + playerRestrictionsCache.size() + " restriction groups");

        } catch (Exception e) {
            Logger.logException(GiftCodeCache.class, e);
            Logger.log("GiftCodeCache: Failed to initialize cache!");
        }
    }

    private void loadAllGiftCodes() {
        Connection con = null;
        try {
            // Loading gift codes from database
            con = GirlkunDB.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT id, code, name, description, max_uses, current_uses, created_date, expired_date, "
                    + "is_active, player_limit_type, vip_level_min FROM gift_codes WHERE is_active = 1");
            ResultSet rs = ps.executeQuery();

            Map<String, GiftCodeData> tempCodes = new HashMap<>();

            while (rs.next()) {
                GiftCodeData giftCode = new GiftCodeData();
                giftCode.id = rs.getInt("id");
                giftCode.code = rs.getString("code");
                giftCode.name = rs.getString("name");
                giftCode.description = rs.getString("description");
                giftCode.maxUses = rs.getInt("max_uses");
                giftCode.currentUses = rs.getInt("current_uses");
                giftCode.createdDate = rs.getTimestamp("created_date");
                giftCode.expiredDate = rs.getTimestamp("expired_date");
                giftCode.isActive = rs.getBoolean("is_active");
                giftCode.playerLimitType = rs.getString("player_limit_type");
                giftCode.vipLevelMin = rs.getInt("vip_level_min");

                tempCodes.put(giftCode.code, giftCode);
                // Gift code loaded silently
            }

            giftCodesCache.clear();
            giftCodesCache.putAll(tempCodes);
            // Gift codes loaded silently

            rs.close();
            ps.close();

        } catch (Exception e) {
            Logger.logException(GiftCodeCache.class, e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    Logger.logException(GiftCodeCache.class, e);
                }
            }
        }
    }

    private void loadAllGiftCodeItems() {
        Connection con = null;
        try {
            // Loading gift code items from database
            con = GirlkunDB.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT gci.id, gci.gift_code_id, gci.item_id, gci.quantity "
                    + "FROM gift_code_items gci "
                    + "JOIN gift_codes gc ON gci.gift_code_id = gc.id "
                    + "WHERE gc.is_active = 1 ORDER BY gci.gift_code_id");
            ResultSet rs = ps.executeQuery();

            Map<Integer, List<GiftCodeItem>> tempItems = new HashMap<>();
            int itemCount = 0;

            while (rs.next()) {
                GiftCodeItem item = new GiftCodeItem();
                item.id = rs.getInt("id");
                item.giftCodeId = rs.getInt("gift_code_id");
                item.itemId = rs.getInt("item_id");
                item.quantity = rs.getInt("quantity");

                tempItems.computeIfAbsent(item.giftCodeId, k -> new ArrayList<>()).add(item);
                itemCount++;
                // Item loaded silently
            }

            itemsCache.clear();
            itemsCache.putAll(tempItems);
            Logger.log("GiftCodeCache: Successfully loaded " + itemCount + " gift code items");

            rs.close();
            ps.close();

        } catch (Exception e) {
            Logger.logException(GiftCodeCache.class, e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    Logger.logException(GiftCodeCache.class, e);
                }
            }
        }
    }

    private void loadAllGiftCodeItemOptions() {
        Connection con = null;
        try {
            Logger.log("GiftCodeCache: Loading gift code item options from database...");
            con = GirlkunDB.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT gcio.gift_code_item_id, gcio.option_id, gcio.param "
                    + "FROM gift_code_item_options gcio "
                    + "JOIN gift_code_items gci ON gcio.gift_code_item_id = gci.id "
                    + "JOIN gift_codes gc ON gci.gift_code_id = gc.id "
                    + "WHERE gc.is_active = 1 ORDER BY gcio.gift_code_item_id");
            ResultSet rs = ps.executeQuery();

            Map<Integer, List<GiftCodeItemOption>> tempOptions = new HashMap<>();
            int optionCount = 0;

            while (rs.next()) {
                GiftCodeItemOption option = new GiftCodeItemOption();
                option.optionId = rs.getInt("option_id");
                option.param = rs.getInt("param");
                int giftCodeItemId = rs.getInt("gift_code_item_id");

                tempOptions.computeIfAbsent(giftCodeItemId, k -> new ArrayList<>()).add(option);
                optionCount++;
                Logger.log("GiftCodeCache: Loaded option: optionId=" + option.optionId
                        + " param=" + option.param + " for gift_code_item_id=" + giftCodeItemId);
            }

            optionsCache.clear();
            optionsCache.putAll(tempOptions);
            Logger.log("GiftCodeCache: Successfully loaded " + optionCount + " gift code item options");

            rs.close();
            ps.close();

        } catch (Exception e) {
            Logger.logException(GiftCodeCache.class, e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    Logger.logException(GiftCodeCache.class, e);
                }
            }
        }
    }

    private void loadAllPlayerRestrictions() {
        Connection con = null;
        try {
            Logger.log("GiftCodeCache: Loading player restrictions from database...");
            con = GirlkunDB.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT gcpr.gift_code_id, gcpr.player_id, gcpr.restriction_type "
                    + "FROM gift_code_player_restrictions gcpr "
                    + "JOIN gift_codes gc ON gcpr.gift_code_id = gc.id "
                    + "WHERE gc.is_active = 1 ORDER BY gcpr.gift_code_id");
            ResultSet rs = ps.executeQuery();

            Map<Integer, List<Integer>> tempRestrictions = new HashMap<>();
            int restrictionCount = 0;

            while (rs.next()) {
                int giftCodeId = rs.getInt("gift_code_id");
                int playerId = rs.getInt("player_id");
                String restrictionType = rs.getString("restriction_type");

                if ("ALLOWED".equals(restrictionType)) {
                    tempRestrictions.computeIfAbsent(giftCodeId, k -> new ArrayList<>()).add(playerId);
                    restrictionCount++;
                    Logger.log("GiftCodeCache: Loaded restriction: gift_code_id=" + giftCodeId
                            + " allows player_id=" + playerId);
                }
            }

            playerRestrictionsCache.clear();
            playerRestrictionsCache.putAll(tempRestrictions);
            Logger.log("GiftCodeCache: Successfully loaded " + restrictionCount + " player restrictions");

            rs.close();
            ps.close();

        } catch (Exception e) {
            Logger.logException(GiftCodeCache.class, e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    Logger.logException(GiftCodeCache.class, e);
                }
            }
        }
    }

    /**
     * Get gift code data từ cache
     */
    public GiftCodeData getGiftCode(String code) {
        if (!isInitialized) {
            Logger.log("GiftCodeCache: Cache not initialized, returning null");
            return null;
        }

        return giftCodesCache.get(code);
    }

    /**
     * Get gift code items từ cache
     */
    public List<GiftCodeItem> getGiftCodeItems(int giftCodeId) {
        if (!isInitialized) {
            return new ArrayList<>();
        }

        return itemsCache.getOrDefault(giftCodeId, new ArrayList<>());
    }

    /**
     * Get item options từ cache
     */
    public List<GiftCodeItemOption> getItemOptions(int giftCodeItemId) {
        if (!isInitialized) {
            return new ArrayList<>();
        }

        return optionsCache.getOrDefault(giftCodeItemId, new ArrayList<>());
    }

    /**
     * Check player restrictions từ cache
     */
    public List<Integer> getAllowedPlayers(int giftCodeId) {
        if (!isInitialized) {
            return new ArrayList<>();
        }

        return playerRestrictionsCache.getOrDefault(giftCodeId, new ArrayList<>());
    }

    /**
     * Refresh cache - Reload tất cả data từ database
     */
    public void refreshCache() {
        Logger.log("GiftCodeCache: Refreshing cache...");
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

        int totalItems = itemsCache.values().stream().mapToInt(List::size).sum();
        int totalOptions = optionsCache.values().stream().mapToInt(List::size).sum();
        int totalRestrictions = playerRestrictionsCache.values().stream().mapToInt(List::size).sum();

        return String.format(
                "GiftCodeCache Stats - Codes: %d, Items: %d, Options: %d, Restrictions: %d, Last Refresh: %d ms ago",
                giftCodesCache.size(), totalItems, totalOptions, totalRestrictions,
                System.currentTimeMillis() - lastRefreshTime);
    }

    /**
     * Clear cache
     */
    public void clearCache() {
        giftCodesCache.clear();
        itemsCache.clear();
        optionsCache.clear();
        playerRestrictionsCache.clear();
        isInitialized = false;
        Logger.log("GiftCodeCache: Cache cleared");
    }

    // Inner classes
    public static class GiftCodeData {

        public int id;
        public String code;
        public String name;
        public String description;
        public int maxUses;
        public int currentUses;
        public Timestamp createdDate;
        public Timestamp expiredDate;
        public boolean isActive;
        public String playerLimitType;
        public int vipLevelMin;
    }

    public static class GiftCodeItem {

        public int id;
        public int giftCodeId;
        public int itemId;
        public int quantity;
    }

    public static class GiftCodeItemOption {

        public int optionId;
        public int param;
    }
}
