package Dragon.services;

import Dragon.consts.ConstNpc;
import Dragon.models.player.Player;
import Dragon.server.Client;
import Dragon.server.ServerManager;
import com.girlkun.network.server.GirlkunSessionManager;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;

public class AdminPanelService {
    
    private static AdminPanelService instance;
    
    public static AdminPanelService gI() {
        if (instance == null) {
            instance = new AdminPanelService();
        }
        return instance;
    }
    
    /**
     * Display admin panel with system metrics
     * @param player The admin player requesting the panel
     */
    public void showAdminPanel(Player player) {
        try {
            // Get system metrics with fallback handling
            SystemMetrics metrics = getSystemMetrics();
            
            String adminMessage = "|4|Người đang chơi: " + metrics.playerCount + "\n"
                    + "|8|Active threads: " + metrics.activeThreads + " | Sessions: " + metrics.sessionCount + "\n"
                    + "|7|CPU: " + metrics.cpuUsage + "% | RAM: " + metrics.usedMemoryStr + "/" + metrics.totalMemoryStr + "GB\n"
                    + "|7|Server uptime: " + ServerManager.timeStart;
            
            NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_ADMIN, 21587, adminMessage,
                    "Menu Admin", "Call Boss", "Buff Item", "GIFTCODE", "Nạp", "Đóng");
                    
        } catch (Exception e) {
            // Fallback admin panel if system metrics fail
            showFallbackAdminPanel(player);
        }
    }
    
    /**
     * Get system metrics with proper error handling
     * @return SystemMetrics object containing all system information
     */
    private SystemMetrics getSystemMetrics() {
        SystemMetrics metrics = new SystemMetrics();
        DecimalFormat df = new DecimalFormat("0.00");
        
        // Try multiple approaches to get system info
        if (!getSystemInfoWithOSHI(metrics, df)) {
            if (!getSystemInfoWithNativeCommands(metrics, df)) {
                getFallbackSystemInfo(metrics, df);
            }
        }
        
        // Thread and session info - always available
        metrics.activeThreads = Thread.activeCount();
        metrics.sessionCount = GirlkunSessionManager.gI().getSessions().size();
        metrics.playerCount = Client.gI().getPlayers().size();
        
        return metrics;
    }
    
    /**
     * Try to get system info using OSHI library (if available)
     */
    private boolean getSystemInfoWithOSHI(SystemMetrics metrics, DecimalFormat df) {
        try {
            // This will only work if OSHI library is added to dependencies
            Class<?> systemInfoClass = Class.forName("oshi.SystemInfo");
            Object systemInfo = systemInfoClass.getDeclaredConstructor().newInstance();
            
            Object hal = systemInfoClass.getMethod("getHardware").invoke(systemInfo);
            Object memory = hal.getClass().getMethod("getMemory").invoke(hal);
            Object processor = hal.getClass().getMethod("getProcessor").invoke(hal);
            
            // Get memory info
            long totalMemory = (Long) memory.getClass().getMethod("getTotal").invoke(memory);
            long availableMemory = (Long) memory.getClass().getMethod("getAvailable").invoke(memory);
            long usedMemory = totalMemory - availableMemory;
            
            metrics.usedMemoryStr = df.format((double) usedMemory / (1024 * 1024 * 1024));
            metrics.totalMemoryStr = df.format((double) totalMemory / (1024 * 1024 * 1024));
            
            // Get CPU info
            double[] cpuLoad = (double[]) processor.getClass().getMethod("getProcessorCpuLoadBetweenTicks").invoke(processor);
            if (cpuLoad != null && cpuLoad.length > 0) {
                metrics.cpuUsage = df.format(cpuLoad[0] * 100);
            } else {
                metrics.cpuUsage = "N/A";
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Try to get system info using native commands
     */
    private boolean getSystemInfoWithNativeCommands(SystemMetrics metrics, DecimalFormat df) {
        try {
            // Get memory info from /proc/meminfo (Linux)
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                Process proc = Runtime.getRuntime().exec("cat /proc/meminfo");
                java.util.Scanner scanner = new java.util.Scanner(proc.getInputStream());
                
                long totalMemory = 0, availableMemory = 0;
                
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("MemTotal:")) {
                        totalMemory = Long.parseLong(line.replaceAll("[^0-9]", "")) * 1024;
                    } else if (line.startsWith("MemAvailable:")) {
                        availableMemory = Long.parseLong(line.replaceAll("[^0-9]", "")) * 1024;
                        break;
                    }
                }
                scanner.close();
                
                long usedMemory = totalMemory - availableMemory;
                metrics.usedMemoryStr = df.format((double) usedMemory / (1024 * 1024 * 1024));
                metrics.totalMemoryStr = df.format((double) totalMemory / (1024 * 1024 * 1024));
                
                // Get CPU usage from /proc/stat
                Process cpuProc = Runtime.getRuntime().exec("cat /proc/loadavg");
                java.util.Scanner cpuScanner = new java.util.Scanner(cpuProc.getInputStream());
                if (cpuScanner.hasNext()) {
                    String loadAvg = cpuScanner.next();
                    double load = Double.parseDouble(loadAvg);
                    metrics.cpuUsage = df.format(load * 100 / Runtime.getRuntime().availableProcessors());
                } else {
                    metrics.cpuUsage = "N/A";
                }
                cpuScanner.close();
                
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    
    /**
     * Fallback to JVM info if system info is not available
     */
    private void getFallbackSystemInfo(SystemMetrics metrics, DecimalFormat df) {
        // Use Runtime for memory info - JVM only
        Runtime runtime = Runtime.getRuntime();
        
        long totalMemory = runtime.maxMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = runtime.totalMemory() - freeMemory;
        
        // Memory in GB - JVM memory usage
        metrics.usedMemoryStr = df.format((double) usedMemory / (1024 * 1024 * 1024));
        metrics.totalMemoryStr = df.format((double) totalMemory / (1024 * 1024 * 1024));
        
        // Try to get CPU info with modern approach, fallback if not available
        try {
            com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
                    .getOperatingSystemMXBean();
            double cpuLoad = osBean.getProcessCpuLoad();
            metrics.cpuUsage = cpuLoad >= 0 ? df.format(cpuLoad * 100) + " (JVM)" : "N/A";
        } catch (Exception e) {
            metrics.cpuUsage = "Unavailable";
        }
    }
    
    /**
     * Show fallback admin panel if all system metrics fail
     * @param player The admin player
     */
    private void showFallbackAdminPanel(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_ADMIN, 21587,
                "|4|Người đang chơi: " + Client.gI().getPlayers().size() + "\n"
                        + "|8|System info: Unavailable\n"
                        + "|7|Server uptime: " + ServerManager.timeStart,
                "Menu Admin", "Call Boss", "Buff Item", "GIFTCODE", "Nạp", "Đóng");
    }
    
    /**
     * Inner class to hold system metrics data
     */
    private static class SystemMetrics {
        public String cpuUsage = "N/A";
        public String usedMemoryStr = "0.00";
        public String totalMemoryStr = "0.00";
        public int activeThreads = 0;
        public int sessionCount = 0;
        public int playerCount = 0;
    }
}
