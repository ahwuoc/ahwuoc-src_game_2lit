package Dragon.nam;

import java.util.Random;
import Dragon.services.InventoryServiceNew;
import Dragon.services.ItemService;
import Dragon.models.player.Player;
import java.util.List;
import Dragon.models.item.Item;
import Dragon.services.Service;
import com.girlkun.network.io.Message;
import Dragon.utils.Util;

import java.util.ArrayList;

public class TamBaoService {

    public static TamBaoService instance;

    public static TamBaoService gI() {
        if (instance == null) {
            instance = new TamBaoService();
        }
        return instance;
    }

    public static List<Item> listItem = new ArrayList<>();
    public static List<Item> listItemVip = new ArrayList<>();
    public static int VAN_MAY = 100;

    public static int[] itemthg = {
        673, 1760, 1760, 16,
        16, 1760, 19, 1760, 20,
        673, 17, 18, 19, 1760,
        384, 20, 383, 385, 673,
        381, 382, 18, 1608, 1650};
    public static int[] itemvip = {
        673, 1760, 1760, 16,
        16, 382, 19, 1760, 20,
        673, 17, 18, 19, 1760,
        384, 20, 383, 385, 673,
        381, 1650, 1608, 1609, 1651};

    public static void loadItem() {

        // Trộn mảng itemthg
        shuffleArray(itemthg);
        // Trộn mảng itemvip
        shuffleArray(itemvip);
        for (int i = 0; i < 24; i++) {
            Item item = ItemService.gI().createNewItem((short) itemthg[i]);
            listItem.add(item);
            Item itemVip = ItemService.gI().createNewItem((short) itemvip[i]);
            listItemVip.add(itemVip);
        }
    }

    public static void shuffleArray(int[] array) {
        Random rand = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            // Hoán đổi phần tử array[i] và array[index]
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public static void sendDataQuay(Player player, byte type) {
        Message msg = null;
        try {
            msg = new Message(70); // Tạo message với ID 70
            msg.writer().writeByte(type); // Ghi loại quay (0: quay thường, 1: quay VIP)

            int size = listItem.size(); // Lấy số lượng item trong danh sách quay thường
            int size2 = listItemVip.size(); // Lấy số lượng item trong danh sách quay VIP

            // Kiểm tra loại quay (quay thường hoặc quay VIP)
            if (type == 0) {
                msg.writer().writeInt(size); // Ghi số lượng vật phẩm quay thường
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        msg.writer().writeInt(listItem.get(i).template != null ? listItem.get(i).template.id : -1); // Ghi ID của vật phẩm quay thường
                        msg.writer().writeInt(Util.nextInt(7)); // Ghi một giá trị ngẫu nhiên (có thể là chỉ số item)
                    }
                }
            } else {
                msg.writer().writeInt(size2); // Ghi số lượng vật phẩm quay VIP
                if (size2 > 0) {
                    for (int i = 0; i < size2; i++) {
                        msg.writer().writeInt(listItemVip.get(i).template != null ? listItemVip.get(i).template.id : -1); // Ghi ID của vật phẩm quay VIP
                        msg.writer().writeInt(Util.nextInt(7)); // Ghi một giá trị ngẫu nhiên (chỉ số item)
                    }
                }
            }
            msg.writer().writeInt(20); // Ghi giá trị "Vận may"
            msg.writer().writeInt(32252); // Ghi icon chìa khóa 1
            msg.writer().writeInt(32251); // Ghi icon chìa khóa 2
            player.sendMessage(msg); // Gửi message tới người chơi
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendListReceive(List<Item> list, Player player) {
        try {
            Message msg = new Message(70); // Tạo message với ID 70
            msg.writer().writeByte(1); // Ghi giá trị 1 để biểu thị danh sách nhận thưởng

            int size = list.size(); // Lấy số lượng vật phẩm trong danh sách nhận
            msg.writer().writeInt(size); // Ghi số lượng vật phẩm

            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    msg.writer().writeInt(list.get(i).template.id); // Ghi ID của vật phẩm nhận
                    msg.writer().writeInt(6); // Ghi giá trị (mặc định là 6) - có thể là chỉ số vật phẩm hoặc loại vật phẩm
                }
            }

            player.sendMessage(msg); // Gửi message tới người chơi
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readData(Message msg, Player player) {
        try {
            int avail = msg.reader().available();
            if (avail <= 0) {
                System.out.println("[TamBao] readData: empty payload, skip");
                return;
            }

            int type = msg.reader().readByte();

            // Mở UI
            if (type == 0 || type == 3) {
                sendDataQuay(player, (byte) type);
                return;
            }

            // Quay
            if (type == 1 || type == 4) {
                if (msg.reader().available() < 4) {
                    System.out.println("[TamBao] readData: not enough bytes for luotQuay, skip");
                    return;
                }
                int luotQuay = msg.reader().readInt();
                List<Item> receive = new ArrayList<>();

                shuffleArray(itemthg);
                shuffleArray(itemvip);

                listItem.clear();
                listItemVip.clear();
                for (int i = 0; i < 24; i++) {
                    Item item = ItemService.gI().createNewItem((short) itemthg[i]);
                    listItem.add(item);
                    Item itemVip = ItemService.gI().createNewItem((short) itemvip[i]);
                    listItemVip.add(itemVip);
                }

                for (int i = 0; i < luotQuay; i++) {
                    Item originalItem = (type == 1)
                            ? listItem.get(Util.nextInt(listItem.size() - 1))
                            : listItemVip.get(Util.nextInt(listItemVip.size() - 1));
                    Item clonedItem = ItemService.gI().createNewItem(originalItem.template.id);
                    clonedItem.itemOptions = new ArrayList<>(originalItem.itemOptions);
                    receive.add(clonedItem);
                }

                int dem = 0;
                int slKey = 0;
                for (Item it : player.inventory.itemsBag) {
                    if (it == null || it.template == null) {
                        dem++;
                        continue;
                    }
                    if ((type == 1 && it.template.id == 1752) || (type == 4 && it.template.id == 1753)) {
                        slKey += it.quantity;
                    }
                }

                if (slKey < luotQuay) {
                    Service.gI().sendThongBao(player, "Bạn Không Đủ Chìa Khóa");
                    return;
                }

                if (dem < luotQuay) {
                    Service.gI().sendThongBao(player, "Hành Trang Không Đủ Chỗ Trống");
                    return;
                }

                for (Item i : receive) {
                    if (i.template == null) {
                        return;
                    }

                    if (i.template.id == 1650 || i.template.id == 1651 || i.template.id == 1608 || i.template.id == 1609) {
                        sendThongBaoBenDuoi("Chúc Mừng " + player.name + " Đã Quay Được " + i.template.name);
                    }
                    if (i.template.id == 1608 || i.template.id == 1650) {
                        i.itemOptions.add(new Item.ItemOption(50, Util.isTrue(10, 100) ? Util.nextInt(10, 35) : Util.nextInt(5, 25)));
                        i.itemOptions.add(new Item.ItemOption(77, Util.isTrue(10, 100) ? Util.nextInt(10, 35) : Util.nextInt(5, 25)));
                        i.itemOptions.add(new Item.ItemOption(103, Util.isTrue(10, 100) ? Util.nextInt(10, 35) : Util.nextInt(5, 25)));
                        if (Util.isTrue(99, 100)) {
                            i.itemOptions.add(new Item.ItemOption(93, Util.isTrue(10, 100) ? Util.nextInt(1, 7) : Util.nextInt(1, 5)));
                        }
                        if (Util.isTrue(2, 100)) {
                            i.itemOptions.add(new Item.ItemOption(5, Util.isTrue(5, 100) ? Util.nextInt(5, 10) : Util.nextInt(2, 5)));
                        }
                        if (Util.isTrue(2, 100)) {
                            i.itemOptions.add(new Item.ItemOption(14, Util.isTrue(5, 100) ? Util.nextInt(3, 6) : Util.nextInt(2, 4)));
                        }
                        if (Util.isTrue(1, 100)) {
                            i.itemOptions.add(new Item.ItemOption(47, Util.isTrue(5, 100) ? Util.nextInt(1, 50) : Util.nextInt(1, 30)));
                        }
                    }
                    if (i.template.id == 1609 || i.template.id == 1651) {
                        i.itemOptions.add(new Item.ItemOption(50, Util.isTrue(10, 100) ? Util.nextInt(10, 55) : Util.nextInt(5, 45)));
                        i.itemOptions.add(new Item.ItemOption(77, Util.isTrue(10, 100) ? Util.nextInt(10, 55) : Util.nextInt(5, 45)));
                        i.itemOptions.add(new Item.ItemOption(103, Util.isTrue(10, 100) ? Util.nextInt(10, 55) : Util.nextInt(5, 45)));
                        if (Util.isTrue(90, 100)) {
                            i.itemOptions.add(new Item.ItemOption(93, Util.isTrue(10, 100) ? Util.nextInt(1, 7) : Util.nextInt(1, 5)));
                        }
                        if (Util.isTrue(3, 100)) {
                            i.itemOptions.add(new Item.ItemOption(5, Util.isTrue(10, 100) ? Util.nextInt(5, 15) : Util.nextInt(2, 10)));
                        }
                        if (Util.isTrue(3, 100)) {
                            i.itemOptions.add(new Item.ItemOption(14, Util.isTrue(10, 100) ? Util.nextInt(3, 9) : Util.nextInt(2, 6)));
                        }
                        if (Util.isTrue(2, 100)) {
                            i.itemOptions.add(new Item.ItemOption(47, Util.isTrue(10, 100) ? Util.nextInt(1, 100) : Util.nextInt(1, 50)));
                        }
                        if (Util.isTrue(2, 100)) {
                            i.itemOptions.add(new Item.ItemOption(95, Util.isTrue(10, 100) ? Util.nextInt(10, 20) : Util.nextInt(5, 10)));
                        }
                        if (Util.isTrue(2, 100)) {
                            i.itemOptions.add(new Item.ItemOption(96, Util.isTrue(10, 100) ? Util.nextInt(10, 20) : Util.nextInt(5, 10)));
                        }
                    }

                    if (InventoryServiceNew.gI().getCountEmptyBag(player) >= luotQuay) {
                        InventoryServiceNew.gI().addItemBag(player, i);
                    }
                }

                int need = luotQuay;
                for (Item it : player.inventory.itemsBag) {
                    if (need == 0) {
                        break;
                    }
                    if (it == null || it.template == null) {
                        continue;
                    }
                    if ((type == 1 && it.template.id == 1752) || (type == 4 && it.template.id == 1753)) {
                        int min = Math.min(need, it.quantity);
                        need -= min;
                        it.quantity -= min;
                    }
                }

                InventoryServiceNew.gI().sendItemBags(player);
                sendListReceive(receive, player);
                return;
            }

            System.out.println("[TamBao] readData: unknown type = " + type);
        } catch (java.io.EOFException eof) {
            System.out.println("[TamBao] readData: EOF (duplicate/short packet) -> skip");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendThongBaoBenDuoi(String text) {
        Message msg;
        try {
            msg = new Message(93);
            msg.writer().writeUTF(text);
            Service.gI().sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
