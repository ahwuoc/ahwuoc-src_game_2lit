package Dragon.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;

public class IconResizer {

    private static final String ICON_BASE_PATH = "data/girlkun/icon/x4/";

    private static final ConcurrentHashMap<String, byte[]> iconCache = new ConcurrentHashMap<>();

    /**
     * Lấy icon với zoom level phù hợp
     *
     * @param id ID của icon
     * @param zoomLevel Zoom level (1-4)
     * @return byte array của icon đã resize
     */
    public static byte[] getIcon(int id, byte zoomLevel) {
        if (zoomLevel == 4) {
            // Trả về icon gốc từ x4
            return FileIO.readFile(ICON_BASE_PATH + id + ".png");
        }

        // Tạo key cho cache
        String cacheKey = zoomLevel + "_" + id;

        // Kiểm tra cache trong RAM trước
        byte[] cachedIcon = iconCache.get(cacheKey);
        if (cachedIcon != null) {
            return cachedIcon;
        }

        // Resize từ icon gốc x4 và lưu vào cache RAM
        return resizeIconFromX4(id, zoomLevel, cacheKey);
    }

    /**
     * Resize icon từ folder x4 và lưu vào cache RAM
     */
    private static byte[] resizeIconFromX4(int id, byte zoomLevel, String cacheKey) {
        try {
            // Đọc icon gốc từ x4
            byte[] originalIcon = FileIO.readFile(ICON_BASE_PATH + id + ".png");
            if (originalIcon == null) {
                return null;
            }

            // Chuyển đổi byte array thành BufferedImage
            ByteArrayInputStream bis = new ByteArrayInputStream(originalIcon);
            BufferedImage originalImage = ImageIO.read(bis);
            bis.close();

            if (originalImage == null) {
                return null;
            }

            // Tính toán kích thước mới - giảm 25% cho mỗi level
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            int newWidth = (originalWidth * zoomLevel) / 4;
            int newHeight = (originalHeight * zoomLevel) / 4;

            // Tạo image mới với kích thước đã resize
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resizedImage.createGraphics();

            // Vẽ image đã resize
            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            // Chuyển đổi thành byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "PNG", baos);
            byte[] resizedIcon = baos.toByteArray();
            baos.close();

            // Lưu vào cache RAM
            iconCache.put(cacheKey, resizedIcon);

            return resizedIcon;

        } catch (IOException e) {
            System.err.println("Error resizing icon " + id + " for zoom level " + zoomLevel);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Xóa cache cho một icon cụ thể
     */
    public static void clearIconCache(int id) {
        for (int zoom = 1; zoom <= 3; zoom++) {
            String cacheKey = zoom + "_" + id;
            iconCache.remove(cacheKey);
        }
    }

    /**
     * Xóa toàn bộ cache
     */
    public static void clearAllCache() {
        iconCache.clear();
    }

    /**
     * Kiểm tra xem icon có tồn tại không
     */
    public static boolean iconExists(int id) {
        return new File(ICON_BASE_PATH + id + ".png").exists();
    }

    /**
     * Lấy thống kê cache
     */
    public static void printCacheStats() {
        System.out.println("[IconResizer] Cache stats: " + iconCache.size() + " icons cached");
        if (!iconCache.isEmpty()) {
            System.out.println("[IconResizer] Cached icons:");
            for (String key : iconCache.keySet()) {
                String[] parts = key.split("_");
                if (parts.length == 2) {
                    System.out.println("  - Icon " + parts[1] + " (zoom=" + parts[0] + ")");
                }
            }
        }
    }
}
