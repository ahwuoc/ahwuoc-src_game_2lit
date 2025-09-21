package Dragon.models.map;

import java.util.ArrayList;
import java.util.List;
import Dragon.models.Template.ItemTemplate;
import Dragon.models.item.Item;
import Dragon.models.item.Item.ItemOption;
import Dragon.models.player.Player;
import Dragon.utils.Util;
import Dragon.services.ItemMapService;
import Dragon.services.ItemService;
import Dragon.services.Service;

public class ItemMap {

    public Zone zone;
    public int itemMapId;
    public ItemTemplate itemTemplate;
    public int quantity;

    public int x;
    public int y;
    public long playerId;
    public List<ItemOption> options;

    private long createTime;

    public boolean isBlackBall;
    public boolean isNamecBall;
    public boolean isDoanhTraiBall;
    private Object item;

    public ItemMap(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        this.zone = zone;
        this.itemMapId = zone.countItemAppeaerd++;
        if (zone.countItemAppeaerd >= 2000000000) {
            zone.countItemAppeaerd = 0;
        }
        this.itemTemplate = ItemService.gI().getTemplate((short) tempId);
        this.quantity = quantity;
        this.x = x;
        this.y = y;
        this.playerId = playerId != -1 ? Math.abs(playerId) : playerId;
        this.createTime = System.currentTimeMillis();
        this.options = new ArrayList<>();
        this.isBlackBall = ItemMapService.gI().isBlackBall(this.itemTemplate.id);
        this.isNamecBall = ItemMapService.gI().isNamecBall(this.itemTemplate.id);
        this.lastTimeMoveToPlayer = System.currentTimeMillis();
        this.zone.addItem(this);
    }

    public ItemMap(Zone zone, ItemTemplate temp, int quantity, int x, int y, long playerId) {
        this.zone = zone;
        this.itemMapId = zone.countItemAppeaerd++;
        if (zone.countItemAppeaerd >= 2000000000) {
            zone.countItemAppeaerd = 0;
        }
        this.itemTemplate = temp;
        this.quantity = quantity;
        this.x = x;
        this.y = y;
        this.playerId = playerId != -1 ? Math.abs(playerId) : playerId;
        this.createTime = System.currentTimeMillis();
        this.options = new ArrayList<>();
        this.isBlackBall = ItemMapService.gI().isBlackBall(this.itemTemplate.id);
        this.isNamecBall = ItemMapService.gI().isNamecBall(this.itemTemplate.id);
        this.lastTimeMoveToPlayer = System.currentTimeMillis();
        this.zone.addItem(this);
    }

    public ItemMap(ItemMap itemMap) {
        this.zone = itemMap.zone;
        this.itemMapId = itemMap.itemMapId;
        this.itemTemplate = itemMap.itemTemplate;
        this.quantity = itemMap.quantity;
        this.x = itemMap.x;
        this.y = itemMap.y;
        this.playerId = itemMap.playerId;
        this.options = itemMap.options;
        this.isBlackBall = itemMap.isBlackBall;
        this.isNamecBall = itemMap.isNamecBall;
        this.lastTimeMoveToPlayer = itemMap.lastTimeMoveToPlayer;
        this.createTime = System.currentTimeMillis();
        this.zone.addItem(this);

    }

    public void update() {
        if (this.isBlackBall) {
            if (Util.canDoWithTime(lastTimeMoveToPlayer, timeMoveToPlayer)) {
                if (this.zone != null && !this.zone.getPlayers().isEmpty()) {
                    Player player = this.zone.getPlayers().get(0);
                    if (player.zone != null && player.zone.equals(this.zone)) {
                        this.x = player.location.x;
                        this.y = this.zone.map.yPhysicInTop(this.x, player.location.y - 24);
                        reAppearItem();
                        this.lastTimeMoveToPlayer = System.currentTimeMillis();
                    }
                }
            }
            return;
        }

        if (Util.canDoWithTime(createTime, 20000) && !this.isNamecBall) {
            if (this.zone.map.mapId != 99 && this.zone.map.mapId != 99
                    && this.zone.map.mapId != 99 && this.itemTemplate.id != 99) {
                ItemMapService.gI().removeItemMapAndSendClient(this);
            }
        }
        if (Util.canDoWithTime(createTime, 15000)) {
            this.playerId = -1;
        }
    }

    private final int timeMoveToPlayer = 10000;
    private long lastTimeMoveToPlayer;

    private void reAppearItem() {
        ItemMapService.gI().sendItemMapDisappear(this);
        Service.gI().dropItemMap(this.zone, this);
    }

    public void dispose() {
        this.zone = null;
        this.itemTemplate = null;
        this.options = null;
    }

    public Object getItem() {
        return this.item;
    }

}
