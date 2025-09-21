package Dragon.services;

import Dragon.consts.ConstNpc;
import Dragon.models.player.Player;
import Dragon.server.Client;
import Dragon.server.ServerManager;
import Dragon.Bot.BotManager;
import Dragon.Bot.Bot;
import Dragon.models.map.Map;
import Dragon.models.map.Zone;
import com.girlkun.network.server.GirlkunSessionManager;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class BotManagementService {

    private static BotManagementService instance;

    public static BotManagementService gI() {
        if (instance == null) {
            instance = new BotManagementService();
        }
        return instance;
    }

    /**
     * Show bot management panel with bot metrics to the player
     * 
     * @param player The admin player
     */
    public void showBotManagementPanel(Player player) {
        System.out.println("=== BotManagementService.showBotManagementPanel called ===");
        System.out.println("Player: " + (player != null ? player.name : "null"));

        try {
            System.out.println("Getting bot metrics...");
            BotMetrics metrics = getBotMetrics();
            System.out.println("Bot metrics retrieved successfully");
            System.out.println("- Total bots: " + metrics.totalBots);
            System.out.println("- Active bots: " + metrics.activeBots);
            System.out.println("- Bots by type: " + metrics.botsByType);
            System.out.println("- Map distribution: " + metrics.mapDistribution);

            String botMenu = "|4|Tổng số Bot: " + metrics.totalBots + "\n"
                    + "|4|Bot đang hoạt động: " + metrics.activeBots + "\n"
                    + "|4|Bot Quái: " + metrics.mobBots + "\n"
                    + "|4|Bot Shop: " + metrics.shopBots + "\n"
                    + "|4|Bot Boss: " + metrics.bossBots + "\n"
                    + "|4|Map có nhiều bot nhất: " + metrics.mostCrowdedMap + " (" + metrics.maxBotsInMap + " bot)\n"
                    + "|4|Tỷ lệ Bot/Player: " + metrics.botToPlayerRatio + "%\n"
                    + "|4|Thời gian hoạt động: " + getUptime();

            System.out.println("Bot menu content: " + botMenu);
            System.out.println("Calling NpcService.createMenuConMeo...");

            NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_BOT_MANAGEMENT, 21587, botMenu,
                    "Tạo Bot", "Xóa Bot", "Restart Bot", "Thống Kê", "Cài Đặt", "Đóng");

            System.out.println("Bot management panel created successfully!");

        } catch (Exception e) {
            System.out.println("Error showing bot management panel: " + e.getMessage());
            e.printStackTrace();
            showFallbackBotPanel(player);
        }
    }

    /**
     * Get bot metrics with proper error handling
     * 
     * @return BotMetrics object containing all bot information
     */
    private BotMetrics getBotMetrics() {
        BotMetrics metrics = new BotMetrics();
        DecimalFormat df = new DecimalFormat("0.00");

        try {
            // Get all bots from BotManager
            List<Bot> allBots = BotManager.gI().bot;
            metrics.totalBots = allBots.size();

            // Count bots by type and activity
            int mobBots = 0, shopBots = 0, bossBots = 0, activeBots = 0;
            HashMap<Integer, Integer> mapDistribution = new HashMap<>();

            for (Bot bot : allBots) {
                // Count by type
                switch (bot.type) {
                    case 0:
                        mobBots++;
                        break;
                    case 1:
                        shopBots++;
                        break;
                    case 2:
                        bossBots++;
                        break;
                }

                // Count active bots (not null and in a zone)
                if (bot.zone != null) {
                    activeBots++;

                    // Count bots per map
                    int mapId = bot.zone.map.mapId;
                    mapDistribution.put(mapId, mapDistribution.getOrDefault(mapId, 0) + 1);
                }
            }

            metrics.activeBots = activeBots;
            metrics.mobBots = mobBots;
            metrics.shopBots = shopBots;
            metrics.bossBots = bossBots;
            metrics.mapDistribution = mapDistribution;

            // Find most crowded map
            int maxBots = 0;
            int mostCrowdedMapId = -1;
            for (HashMap.Entry<Integer, Integer> entry : mapDistribution.entrySet()) {
                if (entry.getValue() > maxBots) {
                    maxBots = entry.getValue();
                    mostCrowdedMapId = entry.getKey();
                }
            }
            metrics.mostCrowdedMap = mostCrowdedMapId;
            metrics.maxBotsInMap = maxBots;

            // Calculate bot to player ratio
            int totalPlayers = Client.gI().getPlayers().size();
            if (totalPlayers > 0) {
                metrics.botToPlayerRatio = df.format((double) metrics.totalBots / totalPlayers * 100);
            } else {
                metrics.botToPlayerRatio = "N/A";
            }

            // Build bot type summary
            metrics.botsByType = "Quái:" + mobBots + " Shop:" + shopBots + " Boss:" + bossBots;

        } catch (Exception e) {
            System.out.println("Error getting bot metrics: " + e.getMessage());
            e.printStackTrace();
        }

        return metrics;
    }

    /**
     * Create new bots
     * 
     * @param player The admin player
     * @param count  Number of bots to create
     * @param type   Bot type (0=mob, 1=shop, 2=boss)
     */
    public void createBots(Player player, int count, int type) {
        try {
            for (int i = 0; i < count; i++) {
                // Create bot with random appearance
                short head = (short) (Math.random() * 100);
                short body = (short) (Math.random() * 100);
                short leg = (short) (Math.random() * 100);
                String name = "Bot_" + type + "_" + System.currentTimeMillis() + "_" + i;

                Bot bot = new Bot(head, body, leg, type, name, null, (short) 0);
                BotManager.gI().bot.add(bot);

                // Initialize bot and make it join a map
                if (type == 0) { // Mob bot
                    bot.joinMap();
                }
            }

            Service.gI().sendThongBao(player, "Đã tạo " + count + " bot loại " + type + " thành công!");

        } catch (Exception e) {
            Service.gI().sendThongBao(player, "Lỗi khi tạo bot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Remove bots by type
     * 
     * @param player The admin player
     * @param count  Number of bots to remove
     * @param type   Bot type to remove (-1 for all types)
     */
    public void removeBots(Player player, int count, int type) {
        try {
            List<Bot> allBots = BotManager.gI().bot;
            int removed = 0;

            for (int i = allBots.size() - 1; i >= 0 && removed < count; i--) {
                Bot bot = allBots.get(i);
                if (type == -1 || bot.type == type) {
                    // Remove bot from zone if it's in one
                    if (bot.zone != null) {
                        // Remove bot from zone's humanoids list
                        bot.zone.getHumanoids().remove(bot);
                    }
                    allBots.remove(i);
                    removed++;
                }
            }

            Service.gI().sendThongBao(player, "Đã xóa " + removed + " bot thành công!");

        } catch (Exception e) {
            Service.gI().sendThongBao(player, "Lỗi khi xóa bot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Restart all bots (make them rejoin maps)
     * 
     * @param player The admin player
     */
    public void restartBots(Player player) {
        try {
            List<Bot> allBots = BotManager.gI().bot;
            int restarted = 0;

            for (Bot bot : allBots) {
                if (bot.type == 0) { // Only restart mob bots
                    bot.joinMap();
                    restarted++;
                }
            }

            Service.gI().sendThongBao(player, "Đã restart " + restarted + " bot thành công!");

        } catch (Exception e) {
            Service.gI().sendThongBao(player, "Lỗi khi restart bot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show detailed bot statistics
     * 
     * @param player The admin player
     */
    public void showBotStatistics(Player player) {
        try {
            BotMetrics metrics = getBotMetrics();

            String stats = "|7|=== THỐNG KÊ BOT CHI TIẾT ===\n"
                    + "|4|Tổng số bot: " + metrics.totalBots + "\n"
                    + "|4|Bot đang hoạt động: " + metrics.activeBots + "\n"
                    + "|4|Bot không hoạt động: " + (metrics.totalBots - metrics.activeBots) + "\n"
                    + "|2|--- Phân loại ---\n"
                    + "|4|Bot Quái (type 0): " + metrics.mobBots + "\n"
                    + "|4|Bot Shop (type 1): " + metrics.shopBots + "\n"
                    + "|4|Bot Boss (type 2): " + metrics.bossBots + "\n"
                    + "|2|--- Phân bố Map ---\n"
                    + "|4|Map đông nhất: " + metrics.mostCrowdedMap + " (" + metrics.maxBotsInMap + " bot)\n"
                    + "|4|Tỷ lệ Bot/Player: " + metrics.botToPlayerRatio + "%";

            NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_BOT_STATS, 21587, stats,
                    "Refresh", "Xuất File", "Đóng");

        } catch (Exception e) {
            Service.gI().sendThongBao(player, "Lỗi khi hiển thị thống kê: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show fallback bot panel if all metrics fail
     * 
     * @param player The admin player
     */
    private void showFallbackBotPanel(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_BOT_MANAGEMENT, 21587,
                "|4|Tổng số Bot: " + BotManager.gI().bot.size() + "\n"
                        + "|4|Bot info: Unavailable\n"
                        + "|4|Server uptime: " + ServerManager.timeStart,
                "Tạo Bot", "Xóa Bot", "Restart Bot", "Thống Kê", "Cài Đặt", "Đóng");
    }

    /**
     * Get server uptime in readable format
     * 
     * @return Formatted uptime string
     */
    private String getUptime() {
        try {
            return ServerManager.timeStart;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * Inner class to hold bot metrics data
     */
    private static class BotMetrics {
        public int totalBots = 0;
        public int activeBots = 0;
        public int mobBots = 0;
        public int shopBots = 0;
        public int bossBots = 0;
        public String botsByType = "N/A";
        public HashMap<Integer, Integer> mapDistribution = new HashMap<>();
        public int mostCrowdedMap = -1;
        public int maxBotsInMap = 0;
        public String botToPlayerRatio = "N/A";
    }
}
