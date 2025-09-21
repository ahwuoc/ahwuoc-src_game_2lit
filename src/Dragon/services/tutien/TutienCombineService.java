package Dragon.services.tutien;

import Dragon.models.item.Item;
import Dragon.models.player.Player;
import Dragon.server.Manager;
import Dragon.services.*;
import Dragon.utils.Util;

public class TutienCombineService {

    private static TutienCombineService instance;

    public static TutienCombineService gI() {
        if (instance == null) {
            instance = new TutienCombineService();
        }
        return instance;
    }

    public void combineTanDanFragment(Player player) {
        if (player.combineNew.itemsCombine.size() != 2) {
            Service.gI().sendThongBao(player, "Cần chọn 99 mảnh tàn đan và 99 công thức!");
            return;
        }

        int cost = 100_000_000; // 100M gold
        if (player.inventory.gold < cost) {
            Service.gI().sendThongBao(player, "Không đủ Gold! Cần " + Util.numberToMoney(cost));
            return;
        }

        // Tìm mảnh tàn đan và công thức
        Item fragment = null;
        Item congThuc = null;

        for (Item item : player.combineNew.itemsCombine) {
            if (item.template.id == 1805) {
                fragment = item;
            } else if (item.template.id == 1804) {
                congThuc = item;
            }
        }

        if (fragment == null || congThuc == null) {
            Service.gI().sendThongBao(player, "Cần 99 " + (fragment != null ? fragment.template.name : "Mảnh Tàn Đan")
                    + " và 99 " + (congThuc != null ? congThuc.template.name : "Công Thức Đan Dược") + "!");
            return;
        }

        // Kiểm tra số lượng mảnh
        if (fragment.quantity < 99) {
            Service.gI().sendThongBao(player, "Cần ít nhất 99 " + fragment.template.name + "!");
            return;
        }

        // Kiểm tra số lượng công thức
        if (congThuc.quantity < 99) {
            Service.gI().sendThongBao(player, "Cần ít nhất 99 " + congThuc.template.name + "!");
            return;
        }

        // Kiểm tra túi đồ có chỗ trống không
        if (InventoryServiceNew.gI().getCountEmptyBag(player) < 1) {
            Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            return;
        }

        // Trừ gold
        player.inventory.gold -= cost;
        Service.gI().sendMoney(player);

        // Xóa 99 mảnh và 99 công thức
        InventoryServiceNew.gI().subQuantityItemsBag(player, fragment, 99);
        InventoryServiceNew.gI().subQuantityItemsBag(player, congThuc, 99);

        // Tạo đan mới (ID: 1806)
        Item newDan = new Item();
        newDan.template = Dragon.server.Manager.ITEM_TEMPLATES.get(1806);
        newDan.quantity = 1;
        InventoryServiceNew.gI().addItemBag(player, newDan);
        InventoryServiceNew.gI().sendItemBags(player);

        Service.gI().sendThongBao(player, "Ghép đan thành công! Nhận được " + newDan.template.name);

        // Gửi effect thành công
        Dragon.services.func.CombineServiceNew.gI().sendEffectSuccessCombine(player);

        // Clear items và reopen tab
        player.combineNew.itemsCombine.clear();
        Dragon.services.func.CombineServiceNew.gI().reOpenItemCombine(player);
    }

    public void upgradeTutienDan(Player player) {

        if (player.combineNew.itemsCombine.size() != 2) {
            Service.gI().sendThongBao(player, "Cần chọn đan Tu Tiên và công thức!");
            return;
        }

        // Tìm đan và công thức
        Item dan = null;
        Item congThuc = null;

        for (Item item : player.combineNew.itemsCombine) {
            if (item.template.id >= 1806 && item.template.id <= 1815) {
                dan = item;
            } else if (item.template.id == 1804) {
                congThuc = item;
            }
        }

        if (dan == null || congThuc == null) {
            Service.gI().sendThongBao(player, "Cần đan Tu Tiên và công thức!");
            return;
        }

        int currentLevel = dan.template.id - 1805;
        int targetLevel = currentLevel + 1;

        if (currentLevel >= 10) {
            Service.gI().sendThongBao(player, "Đan đã đạt cấp tối đa!");
            return;
        }

        int requiredQuantity = 99;
        int cost = 100_000_000;

        if (player.inventory.gold < cost) {
            Service.gI().sendThongBao(player, "Không đủ Gold! Cần " + Util.numberToMoney(cost));
            return;
        }

        // Kiểm tra số lượng đan
        if (dan.quantity < requiredQuantity) {
            Service.gI().sendThongBao(player, "Cần ít nhất " + requiredQuantity + " " + dan.template.name + "!");
            return;
        }

        // Kiểm tra số lượng công thức
        if (congThuc.quantity < requiredQuantity) {
            Service.gI().sendThongBao(player, "Cần ít nhất " + requiredQuantity + " " + congThuc.template.name + "!");
            return;
        }

        // Kiểm tra túi đồ có chỗ trống không
        if (InventoryServiceNew.gI().getCountEmptyBag(player) < 1) {
            Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            return;
        }

        // Trừ gold
        player.inventory.gold -= cost;
        Service.gI().sendMoney(player);

        // Xóa đan và công thức
        InventoryServiceNew.gI().subQuantityItemsBag(player, dan, requiredQuantity);
        InventoryServiceNew.gI().subQuantityItemsBag(player, congThuc, requiredQuantity);

        // Kiểm tra thành công
        int successRate = getSuccessRateForLevel(targetLevel);
        if (Util.nextInt(1, 100) <= successRate) {
            // Thành công - tạo đan cấp cao hơn
            int newDanId = 1805 + targetLevel; // Tạo đan cấp tiếp theo
            if (newDanId <= 1815) {
                Item newDan = new Item();
                newDan.template = Manager.ITEM_TEMPLATES.get(newDanId);
                newDan.quantity = 1;
                InventoryServiceNew.gI().addItemBag(player, newDan);
                InventoryServiceNew.gI().sendItemBags(player);

                Service.gI().sendThongBao(player, "Nâng cấp đan thành công! Nhận được " + newDan.template.name);

                Dragon.services.func.CombineServiceNew.gI().sendEffectSuccessCombine(player);
                Dragon.services.func.CombineServiceNew.gI().sendEffectOpenItem(player,
                        dan.template.iconID, newDan.template.iconID);
            }
        } else {
            // Thất bại
            Service.gI().sendThongBao(player,
                    "Nâng cấp đan thất bại! Đã mất " + Util.numberToMoney(cost) + " Gold");

            // Gửi effect thất bại
            Dragon.services.func.CombineServiceNew.gI().sendEffectFailCombine(player);
        }

        // Clear items và reopen tab
        player.combineNew.itemsCombine.clear();
        Dragon.services.func.CombineServiceNew.gI().reOpenItemCombine(player);
    }

    public void showInfoCombineTanDanFragment(Player player) {
        showInfoCombineTanDanFragment(player, null);
    }

    public void showInfoCombineTanDanFragment(Player player, Dragon.models.npc.Npc npc) {
        if (player.combineNew.itemsCombine.size() == 2) {
            Item fragment = null;
            Item congThuc = null;

            for (Item item : player.combineNew.itemsCombine) {
                if (item.template.id == 1805) {
                    fragment = item;
                } else if (item.template.id == 1804) {
                    congThuc = item;
                }
            }

            if (fragment != null && congThuc != null) {
                String npcSay = "|7|Ghép Mảnh Tàn Đan\n\n";
                npcSay += "|2|Mảnh Tàn Đan: " + fragment.template.name + " (x" + fragment.quantity + ")\n";
                npcSay += "|2|Công Thức: " + congThuc.template.name + " (x" + congThuc.quantity + ")\n\n";
                npcSay += "|1|Chi phí: 100M Gold\n";
                npcSay += "|3|Tỉ lệ thành công: 100%\n\n";
                npcSay += "|6|Kết quả: 1 "
                        + (Manager.ITEM_TEMPLATES.get(1806) != null ? Manager.ITEM_TEMPLATES.get(1806).name
                        : "Đan Tu Tiên");
                Dragon.models.npc.Npc currentNpc = npc != null ? npc
                        : Dragon.services.func.CombineServiceNew.gI()
                                .getNpcByType(Dragon.services.func.CombineServiceNew.COMBINE_TAN_DAN_FRAGMENT);
                currentNpc.createOtherMenu(player, Dragon.consts.ConstNpc.MENU_START_COMBINE, npcSay, "Ghép Đan");
            } else {
                Dragon.models.npc.Npc currentNpc = npc != null ? npc
                        : Dragon.services.func.CombineServiceNew.gI()
                                .getNpcByType(Dragon.services.func.CombineServiceNew.COMBINE_TAN_DAN_FRAGMENT);
                currentNpc.createOtherMenu(player, Dragon.consts.ConstNpc.IGNORE_MENU,
                        "Cần 99 Mảnh Tàn Đan và 99 Công Thức Đan Dược", "Đóng");
            }
        } else {
            Dragon.models.npc.Npc currentNpc = npc != null ? npc
                    : Dragon.services.func.CombineServiceNew.gI()
                            .getNpcByType(Dragon.services.func.CombineServiceNew.COMBINE_TAN_DAN_FRAGMENT);
            currentNpc.createOtherMenu(player, Dragon.consts.ConstNpc.IGNORE_MENU,
                    "Cần chọn 2 item: Mảnh Tàn Đan và Công Thức", "Đóng");
        }
    }

    public void showInfoUpgradeTutienDan(Player player) {
        showInfoUpgradeTutienDan(player, null);
    }

    public void showInfoUpgradeTutienDan(Player player, Dragon.models.npc.Npc npc) {

        if (player.combineNew.itemsCombine.size() == 2) {
            Item dan = null;
            Item congThuc = null;

            for (Item item : player.combineNew.itemsCombine) {
                if (item.template.id >= 1806 && item.template.id <= 1815) {
                    dan = item;
                } else if (item.template.id == 1804) {
                    congThuc = item;
                }
            }

            if (dan != null && congThuc != null) {
                // Xác định cấp hiện tại và cấp đích
                int currentLevel = dan.template.id - 1805; // 1806 = cấp 1, 1807 = cấp 2, ...
                int targetLevel = currentLevel + 1; // Nâng cấp lên cấp tiếp theo

                if (currentLevel >= 10) {
                    Dragon.services.func.CombineServiceNew.gI()
                            .getNpcByType(Dragon.services.func.CombineServiceNew.UPGRADE_TUTIEN_DAN)
                            .createOtherMenu(player, Dragon.consts.ConstNpc.IGNORE_MENU,
                                    "Đan đã đạt cấp tối đa!", "Đóng");
                    return;
                }

                int requiredQuantity = 99;
                int cost = 100_000_000;
                int successRate = getSuccessRateForLevel(targetLevel);
                int newDanId = 1805 + targetLevel;

                String npcSay = "|7|Nâng Cấp Đan Tu Tiên\n\n";
                npcSay += "|2|Đan hiện tại: " + dan.template.name + " (Cấp " + currentLevel + ")\n";
                npcSay += "|2|Công thức: " + congThuc.template.name + " (x" + congThuc.quantity + ")\n\n";
                npcSay += "|1|Chi phí: " + Util.numberToMoney(cost) + " Gold\n";
                npcSay += "|3|Tỉ lệ thành công: " + successRate + "%\n";
                npcSay += "|4|Cần: " + requiredQuantity + " đan + " + requiredQuantity + " công thức\n\n";
                npcSay += "|6|Kết quả: 1 "
                        + (Manager.ITEM_TEMPLATES.get(newDanId) != null ? Manager.ITEM_TEMPLATES.get(newDanId).name
                        : "Đan Tu Tiên cấp " + targetLevel);

                Dragon.models.npc.Npc currentNpc = npc != null ? npc
                        : Dragon.services.func.CombineServiceNew.gI()
                                .getNpcByType(Dragon.services.func.CombineServiceNew.UPGRADE_TUTIEN_DAN);
                currentNpc.createOtherMenu(player, Dragon.consts.ConstNpc.MENU_START_COMBINE, npcSay, "Nâng Cấp");
            } else {
                Dragon.models.npc.Npc currentNpc = npc != null ? npc
                        : Dragon.services.func.CombineServiceNew.gI()
                                .getNpcByType(Dragon.services.func.CombineServiceNew.UPGRADE_TUTIEN_DAN);
                currentNpc.createOtherMenu(player, Dragon.consts.ConstNpc.IGNORE_MENU,
                        "Cần đan Tu Tiên và công thức", "Đóng");
            }
        } else {
            Dragon.models.npc.Npc currentNpc = npc != null ? npc
                    : Dragon.services.func.CombineServiceNew.gI()
                            .getNpcByType(Dragon.services.func.CombineServiceNew.UPGRADE_TUTIEN_DAN);
            currentNpc.createOtherMenu(player, Dragon.consts.ConstNpc.IGNORE_MENU,
                    "Cần chọn 2 item: Đan Tu Tiên và Công Thức", "Đóng");
        }
    }

    private int getSuccessRateForLevel(int level) {
        return Math.max(10, 100 - (level * 10)); // Cấp 1: 90%, cấp 2: 80%, ..., cấp 10: 10%
    }

}
