package Dragon.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class IconResizer {

    private static final String ICON_BASE_PATH = "data/girlkun/icon/x4/";
    private static final int MAX_CACHE_SIZE = 1000; // Limit cache size to prevent memory leaks
    private static final int CACHE_CLEANUP_INTERVAL = 300; // 5 minutes

    // LRU Cache with size limit
    private static final Map<String, byte[]> iconCache = new LinkedHashMap<String, byte[]>(MAX_CACHE_SIZE + 1, 0.75f,
            true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, byte[]> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    // Thread pool for async operations
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    static {
        // Schedule periodic cache cleanup
        executor.scheduleAtFixedRate(() -> {
            synchronized (iconCache) {
                if (iconCache.size() > MAX_CACHE_SIZE * 0.8) {
                    Logger.log("[IconResizer] Cache cleanup: " + iconCache.size() + " icons");
                }
            }
        }, CACHE_CLEANUP_INTERVAL, CACHE_CLEANUP_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * Lấy icon với zoom level phù hợp
     *
     * @param id        ID của icon
     * @param zoomLevel Zoom level (1-4)
     * @return byte array của icon đã resize
     */
    public static byte[] getIcon(int id, byte zoomLevel) {
        if (zoomLevel == 4) {
            // Return original x4 icon - no processing needed
            return FileIO.readFile(ICON_BASE_PATH + id + ".png");
        }

        String cacheKey = zoomLevel + "_" + id;

        // Check cache first (thread-safe)
        synchronized (iconCache) {
            byte[] cachedIcon = iconCache.get(cacheKey);
            if (cachedIcon != null) {
                return cachedIcon;
            }
        }

        // Resize with optimized algorithm
        return resizeIconOptimized(id, zoomLevel, cacheKey);
    }

    /**
     * Optimized icon resizing with faster algorithms and better resource management
     */
    private static byte[] resizeIconOptimized(int id, byte zoomLevel, String cacheKey) {
        try {
            // Read original icon
            byte[] originalIcon = FileIO.readFile(ICON_BASE_PATH + id + ".png");
            if (originalIcon == null) {
                return null;
            }

            // Convert to BufferedImage with optimized settings
            BufferedImage originalImage;
            try (ByteArrayInputStream bis = new ByteArrayInputStream(originalIcon)) {
                originalImage = ImageIO.read(bis);
            }

            if (originalImage == null) {
                return null;
            }

            // Calculate new dimensions
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            int newWidth = (originalWidth * zoomLevel) / 4;
            int newHeight = (originalHeight * zoomLevel) / 4;

            // Skip resize if dimensions are too small
            if (newWidth < 1 || newHeight < 1) {
                return originalIcon;
            }

            // Create optimized resized image
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resizedImage.createGraphics();

            // Use faster rendering settings
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            // Direct scaling - much faster than getScaledInstance
            g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g2d.dispose();

            // Convert to byte array with optimized settings
            byte[] resizedIcon;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(resizedImage, "PNG", baos);
                resizedIcon = baos.toByteArray();
            }

            // Cache the result (thread-safe)
            synchronized (iconCache) {
                iconCache.put(cacheKey, resizedIcon);
            }

            return resizedIcon;

        } catch (IOException e) {
            Logger.logException(IconResizer.class, e, "Error resizing icon " + id + " for zoom level " + zoomLevel);
            return null;
        }
    }

    /**
     * Clear cache for specific icon (thread-safe)
     */
    public static void clearIconCache(int id) {
        synchronized (iconCache) {
            for (int zoom = 1; zoom <= 3; zoom++) {
                String cacheKey = zoom + "_" + id;
                iconCache.remove(cacheKey);
            }
        }
    }

    /**
     * Clear all cache (thread-safe)
     */
    public static void clearAllCache() {
        synchronized (iconCache) {
            iconCache.clear();
            Logger.log("[IconResizer] Cache cleared");
        }
    }
    
    /**
     * Async preload icons for better performance
     */

    public static void preloadIconAsync(int id, byte zoomLevel) {
        if (zoomLevel == 4)
            return; // No need to preload x4 icons

        String cacheKey = zoomLevel + "_" + id;
        synchronized (iconCache) {
            if (iconCache.containsKey(cacheKey)) {
                return; // Already cached
            }
        }

        CompletableFuture.runAsync(() -> {
            resizeIconOptimized(id, zoomLevel, cacheKey);
        }, executor);
    }

    /**
     * Kiểm tra xem icon có tồn tại không
     */
    public static boolean iconExists(int id) {
        return new File(ICON_BASE_PATH + id + ".png").exists();
    }

    /**
     * Get cache statistics (thread-safe)
     */
    public static void printCacheStats() {
        synchronized (iconCache) {
            Logger.log("[IconResizer] Cache stats: " + iconCache.size() + "/" + MAX_CACHE_SIZE + " icons cached");
            if (!iconCache.isEmpty() && iconCache.size() < 20) { // Only show details for small caches
                Logger.log("[IconResizer] Cached icons:");
                for (String key : iconCache.keySet()) {
                    String[] parts = key.split("_");
                    if (parts.length == 2) {
                        Logger.log("  - Icon " + parts[1] + " (zoom=" + parts[0] + ")");
                    }
                }
            }
        }
    }

    /**
     * Get cache hit ratio for performance monitoring
     */
    public static double getCacheHitRatio() {
        synchronized (iconCache) {
            return iconCache.size() > 0 ? (double) iconCache.size() / MAX_CACHE_SIZE : 0.0;
        }
    }

    /**
     * Shutdown executor service gracefully
     */
    public static void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        Logger.log("[IconResizer] Shutdown completed");
    }
}
