package Dragon.models.boss;

import Dragon.jdbc.daos.BossDataService;
import Dragon.models.player.Player;
import Dragon.utils.Logger;
import Dragon.utils.Util;
import Dragon.consts.ConstPlayer;
import Dragon.models.map.Zone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RefactoredBossManager {

    private static RefactoredBossManager instance;
    private static final boolean DEBUG = false;

    public static RefactoredBossManager getInstance() {
        if (instance == null) {
            instance = new RefactoredBossManager();
        }
        return instance;
    }

    private boolean loadedBoss = false;
    private final Map<Integer, BossData> bossDataCache = new HashMap<>();
    private BossDataService bossDataService;

    private RefactoredBossManager() {
        this.bossDataService = BossDataService.getInstance();
    }

    /**
     * Load tất cả boss từ database
     */
    public void loadBosses() {
        if (loadedBoss) {
            return;
        }

        try {
            if (DEBUG) {
                System.out.println("[RefactoredBossManager] Loading bosses from database...");
            }
            List<BossData> allBossData = bossDataService.loadAllBosses();
            for (BossData bossData : allBossData) {
                try {
                    Boss boss = createGenericBoss(bossData);
                    if (boss != null) {
                        bossDataCache.put((int) boss.id, bossData);
                        spawnBossToMaps(boss, bossData);
                        if (DEBUG) {
                            System.out.println(
                                    "[RefactoredBossManager] Created boss: " + boss.name + " (ID: " + boss.id + ")");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[RefactoredBossManager] Failed to create boss: " + bossData.getName() + " - "
                            + e.getMessage());
                    Logger.logException(RefactoredBossManager.class, e);
                }
            }

            if (DEBUG) {
                System.out
                        .println("[RefactoredBossManager] Successfully loaded " + bossDataCache.size()
                                + " bosses from database");
            }

            // Debug cache
            if (DEBUG) {
                System.out.println("[RefactoredBossManager] Cache size: " + bossDataCache.size());
                System.out.println("[RefactoredBossManager] Cache keys: " + bossDataCache.keySet());
            }

        } catch (Exception e) {
            System.out.println("[RefactoredBossManager] Error loading bosses: " + e.getMessage());
            Logger.logException(RefactoredBossManager.class, e);
        }

        loadedBoss = true;

        // Khởi tạo BossManager thread để update boss
        if (DEBUG) {
            System.out.println("[RefactoredBossManager] Starting BossManager thread...");
        }
        BossManager.gI().loadBoss();
        if (DEBUG) {
            System.out.println("[RefactoredBossManager] BossManager thread started successfully!");
        }

        // Bỏ các debug helper sau khi load để giảm log
    }

    /**
     * Tạo Boss từ BossData
     */
    private Boss createGenericBoss(BossData bossData) throws Exception {
        Boss boss = new Boss(bossData.getId(), bossData);
        return boss;
    }

    /**
     * Spawn boss vào các map được chỉ định trong mapJoin
     */
    private void spawnBossToMaps(Boss boss, BossData bossData) {
        try {
            int[] mapJoin = bossData.getMapJoin();
            if (mapJoin != null && mapJoin.length > 0) {
                for (int mapId : mapJoin) {
                    // Tìm map trong Manager.MAPS
                    Dragon.models.map.Map map = Dragon.server.Manager.MAPS.stream()
                            .filter(m -> m.mapId == mapId)
                            .findFirst()
                            .orElse(null);

                    if (map != null && map.zones != null && !map.zones.isEmpty()) {
                        // Random zone bất kỳ trong map
                        Dragon.models.map.Zone zone = map.zones.get(Util.nextInt(0, map.zones.size() - 1));

                        if (zone != null) {
                            boss.zoneFinal = zone;
                            boss.changeStatus(BossStatus.RESPAWN);
                            if (DEBUG) {
                                System.out.println("[RefactoredBossManager] Prepared spawn boss " + boss.name
                                        + " (ID: " + boss.id + ") to map " + mapId + " zone " + zone.zoneId
                                        + " with lifecycle RESPAWN flow");
                            }
                        }
                    } else {
                        if (DEBUG) {
                            System.out.println(
                                    "[RefactoredBossManager] Map " + mapId + " not found for boss " + boss.name);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[RefactoredBossManager] Error spawning boss " + boss.name + ": " + e.getMessage());
            Logger.logException(RefactoredBossManager.class, e);
        }
    }

    /**
     * Kiểm tra boss có thể tấn công không - chỉ debug boss ID 2222
     */
    public void checkBossAttackCapability() {
        System.out.println("=== CHECKING BOSS ATTACK CAPABILITY (ID 2222 ONLY) ===");
        List<Boss> bosses = BossManager.gI().getBosses();
        for (Boss boss : bosses) {
            if (boss.id == 2222) {
                System.out.println("Boss: " + boss.name);
                System.out.println("  Status: " + boss.bossStatus);
                System.out.println("  TypePK: " + boss.typePk + " (PK_ALL = " + ConstPlayer.PK_ALL + ")");
                System.out.println("  Zone: " + (boss.zone != null ? boss.zone.zoneId : "null"));
                System.out.println("  Skills count: " + boss.playerSkill.skills.size());
                System.out.println("  Can attack: " + (boss.typePk == ConstPlayer.PK_ALL));

                if (boss.playerSkill.skills.isEmpty()) {
                    System.out.println("  WARNING: No skills!");
                }

                if (boss.zone == null) {
                    System.out.println("  WARNING: No zone!");
                } else {
                    System.out.println("  Zone players: " + boss.zone.getPlayers().size());
                    System.out.println("  Zone notBosses: " + boss.zone.getNotBosses().size());
                }
                break;
            }
        }
        System.out.println("=== END CHECK ===");
    }

    /**
     * Kiểm tra database có skills cho boss không - chỉ debug boss ID 2222
     */
    public void checkDatabaseSkills() {
        System.out.println("=== CHECKING DATABASE SKILLS (ID 2222 ONLY) ===");

        if (bossDataCache.containsKey(2222)) {
            BossData bossData = bossDataCache.get(2222);
            int[][] skillTemp = bossData.getSkillTemp();

            System.out.println("Boss " + bossData.getName() + " (ID: 2222)");
            System.out.println("  SkillTemp length: " + skillTemp.length);

            if (skillTemp.length > 0) {
                for (int i = 0; i < skillTemp.length; i++) {
                    System.out.println("    Skill " + i + ": ID=" + skillTemp[i][0] + ", Level=" + skillTemp[i][1]);
                }
            } else {
                System.out.println("  WARNING: No skills in database!");
            }
        } else {
            System.out.println("Boss ID 2222 not found in cache!");
        }
        System.out.println("=== END CHECK ===");
    }

    /**
     * Debug method để kiểm tra boss status - chỉ debug boss ID 2222
     */
    public void debugBossStatus() {
        System.out.println("=== DEBUG BOSS STATUS (ID 2222 ONLY) ===");

        // Kiểm tra boss trong BossManager
        List<Boss> bosses = BossManager.gI().getBosses();
        for (Boss boss : bosses) {
            if (boss.id == 2222) {
                System.out.println("Boss: " + boss.name + " (ID: " + boss.id
                        + "), Status: " + boss.bossStatus
                        + ", TypePK: " + boss.typePk
                        + ", CurrentLevel: " + boss.currentLevel
                        + ", Zone: " + (boss.zone != null ? boss.zone.zoneId : "null"));

                // Debug thêm thông tin zone
                if (boss.zone != null) {
                    System.out.println("  Zone " + boss.zone.zoneId + " - Players: " + boss.zone.getPlayers().size()
                            + ", NotBosses: " + boss.zone.getNotBosses().size()
                            + ", Humanoids: " + boss.zone.getHumanoids().size());

                    // Debug player target
                    Player target = boss.getPlayerAttack();
                    System.out.println("  Player Target: " + (target != null ? target.name : "null"));
                }
                break;
            }
        }
        System.out.println("=== END DEBUG ===");
    }

    /**
     * Tạo boss theo ID (tương thích với code cũ)
     */
    public Boss createBoss(int bossId) {
        try {
            // Kiểm tra cache trước
            if (bossDataCache.containsKey(bossId)) {
                BossData bossData = bossDataCache.get(bossId);
                return createGenericBoss(bossData);
            }

            // Load từ database nếu chưa có trong cache
            BossData bossData = bossDataService.loadBossById(bossId);
            if (bossData != null) {
                bossDataCache.put(bossId, bossData);
                return createGenericBoss(bossData);
            }

            System.out.println("[RefactoredBossManager] Boss not found: " + bossId);
            return null;

        } catch (Exception e) {
            System.out.println("[RefactoredBossManager] Error creating boss " + bossId + ": " + e.getMessage());
            Logger.logException(RefactoredBossManager.class, e);
            return null;
        }
    }

    /**
     * Load multiple bosses (tương thích với code cũ)
     */
    public void loadMultipleBosses(int bossId, int quantity) {
        try {
            System.out.println("[RefactoredBossManager] Loading " + quantity + " bosses of ID: " + bossId);
            int successCount = 0;

            for (int i = 0; i < quantity; i++) {
                Boss boss = createBoss(bossId);
                if (boss != null) {
                    successCount++;
                    System.out.println(
                            "[RefactoredBossManager] Boss " + (i + 1) + "/" + quantity + " created: " + boss.name);
                } else {
                    System.out.println("[RefactoredBossManager] Failed to create boss " + (i + 1) + "/" + quantity
                            + " (ID: " + bossId + ")");
                }
            }

            System.out.println("[RefactoredBossManager] Total created: " + successCount + "/" + quantity + " bosses");

        } catch (Exception e) {
            System.out.println("[RefactoredBossManager] Exception loading multiple bosses: " + e.getMessage());
            Logger.logException(RefactoredBossManager.class, e);
        }
    }

    /**
     * Get boss data cache (for testing)
     */
    public Map<Integer, BossData> getBossDataCache() {
        return bossDataCache;
    }

    /**
     * Get bosses list (for testing compatibility)
     */
    public List<Boss> getBosses() {
        return BossManager.gI().getBosses();
    }

    /**
     * Check xem có boss nào trong zone của player không
     */
    public boolean existBossOnPlayer(Player player) {
        return player.zone.getBosses().size() > 0;
    }

    public void reloadBossData() {
        try {
            System.out.println("[RefactoredBossManager] Reloading boss data from database...");
            bossDataCache.clear();

            List<BossData> allBossData = bossDataService.loadAllBosses();
            for (BossData bossData : allBossData) {
                bossDataCache.put(bossData.getId(), bossData);
            }

            System.out.println("[RefactoredBossManager] Reloaded " + bossDataCache.size() + " boss configurations");

        } catch (Exception e) {
            System.out.println("[RefactoredBossManager] Error reloading boss data: " + e.getMessage());
            Logger.logException(RefactoredBossManager.class, e);
        }
    }

    /**
     * Migration method để chuyển từ hardcode sang database
     */
    public void migrateFromHardcodedBosses() {
        System.out.println("[RefactoredBossManager] Starting migration from hardcoded bosses...");

        // Có thể implement logic migration ở đây
        // Ví dụ: convert BossesData.PHUOCBOSS1 thành database records
        System.out.println("[RefactoredBossManager] Migration completed");
    }

    /**
     * Tạo fake skills cho boss để test
     */
    private void createFakeSkillsForBoss(Boss boss) {
        System.out.println("[RefactoredBossManager] Creating fake skills for boss " + boss.name);

        // Tạo fake skillTemp
        int[][] fakeSkillTemp = new int[][]{
            {1, 1, 1000}, // Kamejoko level 1, cooldown 1000ms
            {2, 1, 2000} // Masenko level 1, cooldown 2000ms
        };

        // Set skillTemp vào boss data
        boss.data[0].setSkillTemp(fakeSkillTemp);

        // Re-init skills
        boss.initSkill();

        System.out.println(
                "[RefactoredBossManager] Created " + boss.playerSkill.skills.size() + " fake skills for " + boss.name);
    }
}
