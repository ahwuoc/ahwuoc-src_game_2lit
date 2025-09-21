package Dragon.models.boss;

import Dragon.models.boss.RefactoredBossManager;
import Dragon.models.boss.BossManager;

/**
 * Test class để kiểm tra boss attack system
 */
public class BossAttackTest {

    public static void main(String[] args) {
        System.out.println("=== BOSS ATTACK TEST ===");

        // Test RefactoredBossManager
        RefactoredBossManager refactoredManager = RefactoredBossManager.getInstance();
        refactoredManager.loadBosses();

        // Debug boss status
        refactoredManager.debugBossStatus();

        // Test BossManager
        BossManager bossManager = BossManager.gI();
        System.out.println("BossManager bosses count: " + bossManager.getBosses().size());

        System.out.println("=== END TEST ===");
    }
}
