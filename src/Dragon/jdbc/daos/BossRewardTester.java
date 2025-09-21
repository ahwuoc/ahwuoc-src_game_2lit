package Dragon.jdbc.daos;

import Dragon.utils.Logger;

import java.util.List;

/**
 * Test class for BossRewardService
 */
public class BossRewardTester {

    /**
     * Test boss rewards for a specific boss ID
     */
    public static void testBossRewards(int bossId) {
        Logger.log("=== TESTING BOSS REWARDS FOR ID: " + bossId + " ===");

        BossRewardService service = BossRewardService.getInstance();
        List<BossRewardCache.BossReward> rewards = service.getBossRewards(bossId);

        if (rewards.isEmpty()) {
            Logger.log("No rewards found for boss ID: " + bossId);
        } else {
            Logger.log("Found " + rewards.size() + " rewards for boss ID: " + bossId);
            for (BossRewardCache.BossReward reward : rewards) {
                Logger.log("  - " + reward.toString());
            }
        }

        Logger.log("=== END TEST ===");
    }

    /**
     * Test multiple boss IDs
     */
    public static void testMultipleBossRewards(int[] bossIds) {
        for (int bossId : bossIds) {
            testBossRewards(bossId);
        }
    }
}
