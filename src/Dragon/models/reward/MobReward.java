package Dragon.models.reward;

import java.util.ArrayList;
import java.util.List;

public class MobReward {

    private int mobId;

    private List<ItemMobReward> itemReward;
    private List<ItemMobReward> goldReward;

    public MobReward(int mobId) {
        this.mobId = mobId;
        this.itemReward = new ArrayList<>();
        this.goldReward = new ArrayList<>();
    }

    public int getMobId() {
        return mobId;
    }

    public void setMobId(int mobId) {
        this.mobId = mobId;
    }

    public List<ItemMobReward> getItemReward() {
        return itemReward;
    }

    public void setItemReward(List<ItemMobReward> itemReward) {
        this.itemReward = itemReward;
    }

    public List<ItemMobReward> getGoldReward() {
        return goldReward;
    }

    public void setGoldReward(List<ItemMobReward> goldReward) {
        this.goldReward = goldReward;
    }
}
