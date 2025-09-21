package Dragon.services;

import Dragon.models.player.Player;
import Dragon.models.item.Item;
import Dragon.models.map.ItemMap;
import Dragon.utils.Logger;
import com.girlkun.database.GirlkunDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class GiftCodeService {

    private static GiftCodeService instance;
    private Dragon.jdbc.daos.GiftCodeCache cache;

    public static GiftCodeService getInstance() {
        if (instance == null) {
            instance = new GiftCodeService();
        }
        return instance;
    }

    private GiftCodeService() {
        this.cache = Dragon.jdbc.daos.GiftCodeCache.getInstance();
    }

    /**
     * Check và sử dụng gift code
     */
    public GiftCodeResult useGiftCode(Player player, String code) {
        try {
            Logger.log("GiftCodeService: Player " + player.name + " trying to use code: " + code);

            // Check gift code exists in cache
            Dragon.jdbc.daos.GiftCodeCache.GiftCodeData giftCode = cache.getGiftCode(code);
            if (giftCode == null) {
                Logger.log("GiftCodeService: Gift code not found in cache: " + code);
                return new GiftCodeResult(false, "Mã quà tặng không tồn tại!");
            }

            Logger.log("GiftCodeService: Found gift code: " + giftCode.code + " (" + giftCode.name + ")");

            String name = giftCode.name;
            int maxUses = giftCode.maxUses;
            int currentUses = giftCode.currentUses;
            Timestamp expiredDate = giftCode.expiredDate;
            boolean isActive = giftCode.isActive;
            String playerLimitType = giftCode.playerLimitType;
            int vipLevelMin = giftCode.vipLevelMin;

            // Check if active
            if (!isActive) {
                return new GiftCodeResult(false, "Mã quà tặng đã bị vô hiệu hóa!");
            }

            // Check expired
            if (expiredDate != null && expiredDate.before(new Timestamp(System.currentTimeMillis()))) {
                return new GiftCodeResult(false, "Mã quà tặng đã hết hạn!");
            }

            // Check max uses
            if (maxUses > 0 && currentUses >= maxUses) {
                return new GiftCodeResult(false, "Mã quà tặng đã hết lượt sử dụng!");
            }

            // Check if player already used
            Connection con = GirlkunDB.getConnection();
            if (hasPlayerUsedCode(con, giftCode.id, (int) player.id)) {
                Logger.log("GiftCodeService: Player " + player.name + " already used code: " + code);
                con.close();
                return new GiftCodeResult(false, "Bạn đã sử dụng mã quà tặng này rồi!");
            }

            // Check player restrictions
            if (!checkPlayerRestrictionsFromCache(giftCode.id, player, playerLimitType, vipLevelMin)) {
                Logger.log("GiftCodeService: Player " + player.name + " failed restrictions for code: " + code);
                con.close();
                return new GiftCodeResult(false, "Bạn không đủ điều kiện sử dụng mã quà tặng này!");
            }

            // Get items from cache
            List<GiftItem> items = getGiftItemsFromCache(giftCode.id);
            if (items.isEmpty()) {
                Logger.log("GiftCodeService: No items found for code: " + code);
                con.close();
                return new GiftCodeResult(false, "Mã quà tặng không có phần thưởng!");
            }

            Logger.log("GiftCodeService: Found " + items.size() + " items for code: " + code);

            // Mark as used
            markAsUsed(con, giftCode.id, player);

            // Give items to player
            giveItemsToPlayer(player, items);

            Logger.log("GiftCodeService: Successfully gave items to player " + player.name + " for code: " + code);
            con.close();
            return new GiftCodeResult(true, "Nhận quà thành công từ mã: " + name, items);

        } catch (Exception e) {
            Logger.logException(GiftCodeService.class, e);
            return new GiftCodeResult(false, "Lỗi hệ thống khi sử dụng mã quà tặng!");
        }
    }

    private boolean hasPlayerUsedCode(Connection con, int giftCodeId, int playerId) throws SQLException {
        PreparedStatement ps = con.prepareStatement(
                "SELECT 1 FROM gift_code_usage WHERE gift_code_id = ? AND player_id = ?");
        ps.setInt(1, giftCodeId);
        ps.setInt(2, playerId);
        ResultSet rs = ps.executeQuery();
        boolean used = rs.next();
        rs.close();
        ps.close();
        return used;
    }

    private boolean checkPlayerRestrictions(Connection con, int giftCodeId, Player player,
            String playerLimitType, int vipLevelMin) throws SQLException {

        switch (playerLimitType) {
            case "VIP_ONLY":
                return player.vip >= vipLevelMin;

            case "SPECIFIC_PLAYERS":
                PreparedStatement ps = con.prepareStatement(
                        "SELECT 1 FROM gift_code_player_restrictions WHERE gift_code_id = ? "
                        + "AND player_id = ? AND restriction_type = 'ALLOWED'");
                ps.setInt(1, giftCodeId);
                ps.setInt(2, (int) player.id);
                ResultSet rs = ps.executeQuery();
                boolean allowed = rs.next();
                rs.close();
                ps.close();
                return allowed;

            case "EXCLUDE_PLAYERS":
                PreparedStatement ps2 = con.prepareStatement(
                        "SELECT 1 FROM gift_code_player_restrictions WHERE gift_code_id = ? "
                        + "AND player_id = ? AND restriction_type = 'BLOCKED'");
                ps2.setInt(1, giftCodeId);
                ps2.setInt(2, (int) player.id);
                ResultSet rs2 = ps2.executeQuery();
                boolean blocked = rs2.next();
                rs2.close();
                ps2.close();
                return !blocked;

            default: // NONE
                return true;
        }
    }

    private List<GiftItem> getGiftItems(Connection con, int giftCodeId) throws SQLException {
        List<GiftItem> items = new ArrayList<>();

        PreparedStatement ps = con.prepareStatement(
                "SELECT gci.id, gci.item_id, gci.quantity FROM gift_code_items gci "
                + "WHERE gci.gift_code_id = ?");
        ps.setInt(1, giftCodeId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            GiftItem item = new GiftItem();
            item.giftItemId = rs.getInt("id");
            item.itemId = rs.getInt("item_id");
            item.quantity = rs.getInt("quantity");
            item.options = getItemOptions(con, item.giftItemId);
            items.add(item);
        }

        rs.close();
        ps.close();
        return items;
    }

    private List<ItemOption> getItemOptions(Connection con, int giftItemId) throws SQLException {
        List<ItemOption> options = new ArrayList<>();

        PreparedStatement ps = con.prepareStatement(
                "SELECT option_id, param FROM gift_code_item_options WHERE gift_code_item_id = ?");
        ps.setInt(1, giftItemId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            options.add(new ItemOption(rs.getInt("option_id"), rs.getInt("param")));
        }

        rs.close();
        ps.close();
        return options;
    }

    private void markAsUsed(Connection con, int giftCodeId, Player player) throws SQLException {
        // Update current_uses
        PreparedStatement ps1 = con.prepareStatement(
                "UPDATE gift_codes SET current_uses = current_uses + 1 WHERE id = ?");
        ps1.setInt(1, giftCodeId);
        ps1.executeUpdate();
        ps1.close();

        // Insert usage record
        PreparedStatement ps2 = con.prepareStatement(
                "INSERT INTO gift_code_usage (gift_code_id, player_id, player_name) VALUES (?, ?, ?)");
        ps2.setInt(1, giftCodeId);
        ps2.setInt(2, (int) player.id);
        ps2.setString(3, player.name);
        ps2.executeUpdate();
        ps2.close();
    }

    private void giveItemsToPlayer(Player player, List<GiftItem> items) {
        for (GiftItem giftItem : items) {
            Item item = ItemService.gI().createNewItem((short) giftItem.itemId, giftItem.quantity);

            // Add options
            if (!giftItem.options.isEmpty()) {
                for (ItemOption option : giftItem.options) {
                    item.itemOptions.add(new Item.ItemOption(option.optionId, option.param));
                }
            }

            InventoryServiceNew.gI().addItemBag(player, item);
        }

        InventoryServiceNew.gI().sendItemBags(player);
        Service.gI().sendThongBao(player, "Đã nhận " + items.size() + " phần quà!");
    }

    /**
     * Check player restrictions từ cache
     */
    private boolean checkPlayerRestrictionsFromCache(int giftCodeId, Player player,
            String playerLimitType, int vipLevelMin) {

        switch (playerLimitType) {
            case "VIP_ONLY":
                Logger.log("GiftCodeService: Checking VIP restriction - player vip=" + player.vip
                        + " required=" + vipLevelMin);
                return player.vip >= vipLevelMin;

            case "SPECIFIC_PLAYERS":
                List<Integer> allowedPlayers = cache.getAllowedPlayers(giftCodeId);
                boolean allowed = allowedPlayers.contains((int) player.id);
                Logger.log("GiftCodeService: Checking specific players - player_id=" + player.id
                        + " allowed=" + allowed + " (allowed_list=" + allowedPlayers + ")");
                return allowed;

            case "EXCLUDE_PLAYERS":
                // TODO: Implement exclude logic if needed
                return true;

            default: // NONE
                Logger.log("GiftCodeService: No restrictions for player " + player.name);
                return true;
        }
    }

    /**
     * Get gift items từ cache
     */
    private List<GiftItem> getGiftItemsFromCache(int giftCodeId) {
        List<GiftItem> items = new ArrayList<>();

        List<Dragon.jdbc.daos.GiftCodeCache.GiftCodeItem> cacheItems = cache.getGiftCodeItems(giftCodeId);

        for (Dragon.jdbc.daos.GiftCodeCache.GiftCodeItem cacheItem : cacheItems) {
            GiftItem item = new GiftItem();
            item.giftItemId = cacheItem.id;
            item.itemId = cacheItem.itemId;
            item.quantity = cacheItem.quantity;

            // Get options from cache
            List<Dragon.jdbc.daos.GiftCodeCache.GiftCodeItemOption> cacheOptions = cache.getItemOptions(cacheItem.id);

            for (Dragon.jdbc.daos.GiftCodeCache.GiftCodeItemOption cacheOption : cacheOptions) {
                item.options.add(new ItemOption(cacheOption.optionId, cacheOption.param));
            }

            items.add(item);
            Logger.log("GiftCodeService: Prepared item " + item.itemId + " x" + item.quantity
                    + " with " + item.options.size() + " options");
        }

        return items;
    }

    // Inner classes
    public static class GiftCodeResult {

        public boolean success;
        public String message;
        public List<GiftItem> items;

        public GiftCodeResult(boolean success, String message) {
            this.success = success;
            this.message = message;
            this.items = new ArrayList<>();
        }

        public GiftCodeResult(boolean success, String message, List<GiftItem> items) {
            this.success = success;
            this.message = message;
            this.items = items;
        }
    }

    public static class GiftItem {

        public int giftItemId;
        public int itemId;
        public int quantity;
        public List<ItemOption> options = new ArrayList<>();
    }

    public static class ItemOption {

        public int optionId;
        public int param;

        public ItemOption(int optionId, int param) {
            this.optionId = optionId;
            this.param = param;
        }
    }
}
