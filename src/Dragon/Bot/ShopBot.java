package Dragon.Bot;

import Dragon.services.Service;
import Dragon.models.map.Zone;
import Dragon.services.PlayerService;
import Dragon.services.ItemService;
import Dragon.models.item.Item;
import Dragon.models.player.*;
import Dragon.services.ChatGlobalService;
import Dragon.services.func.ChangeMapService;
import Dragon.services.func.Trade;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class ShopBot {

    public int idItem;
    public int idItTd;
    public int slot;

    private long lastimeChat;
    private long lastimeChatTrain;
    private Trade trade;

    public Bot bot;
    private Player pl;

    public ShopBot(int item, int traodoi, int slot) {
        this.idItem = item;
        this.idItTd = traodoi;
        this.slot = slot;
    }

    public ShopBot(ShopBot shop) {
        this.idItem = shop.idItem;
        this.idItTd = shop.idItTd;
        this.slot = shop.slot;
    }

    public void update() {
        this.mapL();
        this.chat();
    }

    public String getChat() {
        Item it = ItemService.gI().createNewItem((short) this.idItem);
        Item it1 = ItemService.gI().createNewItem((short) this.idItTd);

        // Danh sách các câu chat ngẫu nhiên
        List<String> chatOptions = new ArrayList<>();
        chatOptions.add(String.format("Bạn có thể mua x%d %s với giá %s tại Map %s Khu %d", this.slot, it1.template.name, it.template.name, this.bot.zone.map.mapName, this.bot.zone.zoneId));
        chatOptions.add(String.format("Bán x%d %s để nhận %s tại Map %s Khu %d", this.slot, it1.template.name, it.template.name, this.bot.zone.map.mapName, this.bot.zone.zoneId));
        chatOptions.add(String.format("Mua ngay x%d %s với %s tại Map %s Khu %d", this.slot, it1.template.name, it.template.name, this.bot.zone.map.mapName, this.bot.zone.zoneId));

        // Chọn ngẫu nhiên một câu từ danh sách
        Random random = new Random();
        int randomIndex = random.nextInt(chatOptions.size());
        return chatOptions.get(randomIndex);
    }

    public void chat() {
        if (this.lastimeChat < (System.currentTimeMillis() - ((100 + new Random().nextInt(100)) * 1000))) {
            ChatGlobalService.gI().chat1(this.bot, this.getChat());
            this.lastimeChat = System.currentTimeMillis();
        }
        if (this.lastimeChatTrain < (System.currentTimeMillis() - ((5 + new Random().nextInt(5)) * 1000))) {
            Service.gI().chat(this.bot, getChat());
            this.lastimeChatTrain = System.currentTimeMillis();
        }
    }

    public void activeTraDe(Player pl) {
        trade = new Trade(pl, bot);
        this.pl = pl;
        this.trade.openTabTrade();
    }

    public void CheckTraDe(List<Item> item) {
        int slot1 = item.stream()
                .filter(it -> it.template.id == this.idItTd && it.quantity >= this.slot)
                .mapToInt(it -> it.quantity)
                .findFirst()
                .orElse(0);
        boolean check = slot1 > 0;
        if (check) {
            active(slot1);
        } else {
            this.trade.cancelTrade();
        }
    }

    public void active(int sl) {
        int sl1 = (int) Math.round((double) sl / this.slot);
        Item it = ItemService.gI().createNewItem((short) this.idItem, sl1);
        it.itemOptions.add(new Item.ItemOption(210, 1));
        this.trade.addItemBot(it);
        this.trade.lockTran(this.bot);
        this.trade.acceptTrade();
    }

    public void mapL() {
        if (this.bot.zone.map.mapId != 11) {
            Zone zone = this.bot.getRandomZone(11);
            if (zone != null) {
                ChangeMapService.gI().goToMap(this.bot, zone);
                this.bot.zone.load_Me_To_Another(this.bot);
                int randomX = new Random().nextInt(1326);
                int fixedY = 336;

                PlayerService.gI().playerMove(this.bot, randomX, fixedY);
            }
        }
    }

    void setItem(int head, int leg, int body) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
