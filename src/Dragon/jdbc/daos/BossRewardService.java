package Dragon.jdbc.daos;

import Dragon.models.map.ItemMap;
import Dragon.models.boss.Boss;
import Dragon.models.player.Player;
import Dragon.utils.Logger;
import java.util.ArrayList;
import java.util.List;

public class BossRewardService {

    private static BossRewardService instance;
    private BossRewardCache cache;

    public static BossRewardService getInstance() {
        if (instance == null) {
            instance = new BossRewardService();
        }
        return instance;
    }

    private BossRewardService() {
        this.cache = BossRewardCache.getInstance();
    }

    /**
     * Process rewards khi boss bị giết từ cache
     */
    public List<ItemMap> processRewards(Boss boss, Player player, int x, int yEnd) {
        List<ItemMap> drops = new ArrayList<>();

        List<BossRewardCache.BossReward> rewards = cache.getBossRewards((int) boss.id);

        for (BossRewardCache.BossReward reward : rewards) {
            // Check drop rate
            boolean willDrop = shouldDropReward(reward.dropRate);
            Dragon.utils.Logger.log("Boss " + boss.id + " item " + reward.itemId + " rate=" + reward.dropRate
                    + "% willDrop=" + willDrop);

            if (willDrop) {
                ItemMap itemMap = new ItemMap(boss.zone, reward.itemId, reward.quantity, x, yEnd, player.id);

                // Add options from cache
                List<BossRewardCache.BossRewardOption> options = cache.getBossRewardOptions(reward.id);
                if (!options.isEmpty()) {
                    itemMap.options = new ArrayList<>();
                    for (BossRewardCache.BossRewardOption option : options) {
                        Dragon.models.item.Item.ItemOption itemOption = new Dragon.models.item.Item.ItemOption(
                                option.optionId, option.param);
                        itemMap.options.add(itemOption);
                    }
                    Logger.log("Added " + options.size() + " options to item " + reward.itemId);
                }

                drops.add(itemMap);
            }
        }

        return drops;
    }

    public void refreshCache() {
        cache.refreshCache();
    }

    public String getCacheStats() {
        return cache.getCacheStats();
    }

    private boolean shouldDropReward(double dropRate) {
        return Dragon.utils.Util.isTrue((int) dropRate, 100);
    }

    public List<BossRewardCache.BossReward> getBossRewards(int bossId) {
        return cache.getBossRewards(bossId);
    }
}
