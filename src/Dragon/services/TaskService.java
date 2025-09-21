package Dragon.services;

import Dragon.consts.ConstMob;
import Dragon.consts.ConstNpc;
import Dragon.consts.ConstPlayer;
import Dragon.models.player.Player;
import Dragon.consts.ConstTask;
import Dragon.models.boss.Boss;
import Dragon.models.item.Item;
import Dragon.models.map.ItemMap;
import Dragon.models.map.Zone;
import Dragon.models.mob.Mob;
import Dragon.models.npc.Npc;
import Dragon.models.task.SideTaskTemplate;
import Dragon.models.task.SubTaskMain;
import Dragon.models.task.TaskMain;
import Dragon.server.Controller;
import Dragon.server.Manager;
import com.girlkun.network.io.Message;
import Dragon.utils.Logger;
import Dragon.utils.Util;

public class TaskService {

    /**
     * Làm cùng số người trong bang
     */
    private static final byte NMEMBER_DO_TASK_TOGETHER = 1;

    private static Dragon.services.TaskService i;

    public static Dragon.services.TaskService gI() {
        if (i == null) {
            i = new Dragon.services.TaskService();
        }
        return i;
    }

    public TaskMain getTaskMainByIdTemplate(int id) {
        for (TaskMain task : Manager.TASKS_TEMPLATE) {
            if (task.id == id) {
                return task;
            }
        }
        return null;
    }

    public TaskMain getTaskMainById(Player player, int id) {
        for (TaskMain tm : Manager.TASKS_TEMPLATE) {
            if (tm.id == id) {
                TaskMain newTaskMain = new TaskMain(tm);
                newTaskMain.detail = transformName(player, newTaskMain.detail);
                for (SubTaskMain stm : newTaskMain.subTasks) {
                    stm.mapId = (short) transformMapId(player, stm.mapId);
                    stm.npcId = (byte) transformNpcId(player, stm.npcId);
                    stm.notify = transformName(player, stm.notify);
                    stm.name = transformName(player, stm.name);
                }
                return newTaskMain;
            }
        }
        return player.playerTask.taskMain;
    }

    // gửi thông tin nhiệm vụ chính
    public void sendTaskMain(Player player) {
        Message msg;
        try {
            try {
                TaskServiceNew.getInstance().syncAllSubTaskMaxCountsForCurrentTask(player);
                TaskServiceNew.getInstance().prepareSubTaskMetaForUI(player);
            } catch (Exception e) {
                Logger.logException(TaskService.class, e);
            }
            msg = new Message(40);
            msg.writer().writeShort(player.playerTask.taskMain.id);
            // msg.writer().writeShort(12);
            msg.writer().writeByte(player.playerTask.taskMain.index);
            // msg.writer().writeUTF(player.playerTask.taskMain.name);
            msg.writer().writeUTF(player.playerTask.taskMain.name + "[" + player.playerTask.taskMain.id + "]");
            msg.writer().writeUTF(player.playerTask.taskMain.detail);
            msg.writer().writeByte(player.playerTask.taskMain.subTasks.size());
            for (SubTaskMain stm : player.playerTask.taskMain.subTasks) {
                msg.writer().writeUTF(stm.name);
                msg.writer().writeByte(stm.npcId);
                msg.writer().writeShort(stm.mapId);
                msg.writer().writeUTF(stm.notify);
            }
            msg.writer().writeShort(player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count);
            for (SubTaskMain stm : player.playerTask.taskMain.subTasks) {
                msg.writer().writeShort(stm.maxCount);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(TaskService.class, e);
        }
    }

    // chuyển sang task mới
    public void sendNextTaskMain(Player player) {
        rewardDoneTask(player);
        player.playerTask.taskMain = TaskService.gI().getTaskMainById(player, player.playerTask.taskMain.id + 1);
        sendTaskMain(player);
        Service.gI().sendThongBao(player, "Nhiệm Vụ Tiếp Theo Của Bạn Là "
                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).name);
    }

    // số lượng đã hoàn thành
    public void sendUpdateCountSubTask(Player player) {
        Message msg;
        try {
            msg = new Message(43);
            msg.writer().writeShort(player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    // chuyển sub task tiếp theo
    public void sendNextSubTask(Player player) {
        Message msg;
        try {
            msg = new Message(41);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    // gửi thông tin nhiệm vụ hiện tại
    public void sendInfoCurrentTask(Player player) {
        Service.gI().sendThongBao(player, "Nhiệm vụ hiện tại của bạn là "
                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).name);
    }

    public boolean checkDoneTaskTalkNpc(Player player, Npc npc) {
        // Delegate wholly to new SQL-based system
        try {
            return TaskServiceNew.getInstance().checkDoneTaskTalkNpc(player, npc);
        } catch (Exception e) {
            Logger.logException(TaskService.class, e);
            return false;
        }
    }

    // kiểm tra hoàn thành nhiệm vụ gia nhập bang hội
    public void checkDoneTaskJoinClan(Player player) {
        if (!player.isBoss && !player.isPet && !player.isClone) {
            // doneTask(player, ConstTask.TASK_13_0);
        }
    }

    // kiểm tra hoàn thành nhiệm vụ lấy item từ rương
    public void checkDoneTaskGetItemBox(Player player) {
        if (!player.isBoss && !player.isPet && !player.isClone) {
            // doneTask(player, ConstTask.TASK_0_3);
        }
    }

    // kiểm tra hoàn thành nhiệm vụ sức mạnh
    public void checkDoneTaskPower(Player player, long power) {
        if (!player.isBoss && !player.isPet && !player.isClone) {
        }
    }

    // kiểm tra hoàn thành nhiệm vụ khi player sử dụng tiềm năng
    public void checkDoneTaskUseTiemNang(Player player) {
        if (!player.isBoss && !player.isPet && !player.isClone) {
            // doneTask(player, ConstTask.TASK_3_0);
        }
    }

    // kiểm tra hoàn thành nhiệm vụ khi vào map nào đó
    public void checkDoneTaskGoToMap(Player player, Zone zoneJoin) {
        // Delegate to new SQL-based system (no-op if not supported)
        try {
            TaskServiceNew.getInstance().checkDoneTaskGoToMap(player, zoneJoin);
        } catch (Exception e) {
            // Method may not exist or feature not used; ignore
        }
    }

    // kiểm tra hoàn thành nhiệm vụ khi nhặt item
    public void checkDoneTaskPickItem(Player player, ItemMap item) {
        // Delegate to new SQL-based system
        try {
            TaskServiceNew.getInstance().checkDoneTaskPickItem(player, item);
        } catch (Exception e) {
            Logger.logException(TaskService.class, e);
        }
    }

    // kiểm tra hoàn thành nhiệm vụ kết bạn
    public void checkDoneTaskMakeFriend(Player player, Player friend) {
        if (!player.isBoss && !player.isPet && !player.isClone) {
            switch (friend.gender) {

            }
        }
    }

    // kiểm tra hoàn thành nhiệm vụ khi xác nhận menu npc nào đó
    public void checkDoneTaskConfirmMenuNpc(Player player, Npc npc, byte select) {
        if (!player.isBoss && !player.isPet && !player.isClone) {
            switch (npc.tempId) {

            }
        }
    }

    // kiểm tra hoàn thành nhiệm vụ khi tiêu diệt được boss
    public void checkDoneTaskKillBoss(Player player, Boss boss) {
        // Delegate to new SQL-based system
        try {
            TaskServiceNew.getInstance().checkDoneTaskKillBoss(player, boss);
        } catch (Exception e) {
            Logger.logException(TaskService.class, e);
        }
    }

    // kiểm tra hoàn thành nhiệm vụ khi giết được quái
    public void checkDoneTaskKillMob(Player player, Mob mob) {
        // Delegate to new SQL-based system
        try {
            TaskServiceNew.getInstance().checkDoneTaskKillMob(player, mob);
        } catch (Exception e) {
            Logger.logException(TaskService.class, e);
        }
    }

    // xong nhiệm vụ nào đó (legacy) - đã loại bỏ, luôn trả về false
    private boolean doneTask(Player player, int idTaskCustom) {
        return false;
    }

    private void npcSay(Player player, int npcId, String text) {
        npcId = transformNpcId(player, npcId);
        text = transformName(player, text);
        int avatar = NpcService.gI().getAvatar(npcId);
        NpcService.gI().createTutorial(player, avatar, text);
    }

    private void rewardDoneTask(Player player) {
        switch (player.playerTask.taskMain.id) {
            case 14:
                Service.gI().addSMTN(player, (byte) 0, 100000, false);
                Service.gI().addSMTN(player, (byte) 1, 100000, false);
                break;
            case 15:
                Service.gI().addSMTN(player, (byte) 0, 200000, false);
                Service.gI().addSMTN(player, (byte) 1, 200000, false);

                Item item = ItemService.gI().createNewItem((short) 1736);
                item.quantity = 50; // ✅ set số lượng ở đây
                InventoryServiceNew.gI().addItemBag(player, item);
                InventoryServiceNew.gI().sendItemBags(player);
                break;

        }

    }

    private void addDoneSubTask(Player player, int numDone) {
        // no-op in legacy system
    }

    private int transformMapId(Player player, int id) {
        if (id == ConstTask.MAP_NHA) {
            return (short) (player.gender + 21);
        } else if (id == ConstTask.MAP_200) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 1
                    : (player.gender == ConstPlayer.NAMEC
                            ? 8
                            : 15);
        } else if (id == ConstTask.MAP_VACH_NUI) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 39
                    : (player.gender == ConstPlayer.NAMEC
                            ? 40
                            : 41);
        } else if (id == ConstTask.MAP_200) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 2
                    : (player.gender == ConstPlayer.NAMEC
                            ? 9
                            : 16);
        } else if (id == ConstTask.MAP_TTVT) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 24
                    : (player.gender == ConstPlayer.NAMEC
                            ? 25
                            : 26);
        } else if (id == ConstTask.MAP_QUAI_BAY_600) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 3
                    : (player.gender == ConstPlayer.NAMEC
                            ? 11
                            : 17);
        } else if (id == ConstTask.MAP_LANG) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 0
                    : (player.gender == ConstPlayer.NAMEC
                            ? 7
                            : 14);
        } else if (id == ConstTask.MAP_QUY_LAO) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 5
                    : (player.gender == ConstPlayer.NAMEC
                            ? 13
                            : 20);
        }
        return id;
    }

    private int transformNpcId(Player player, int id) {
        if (id == ConstTask.NPC_NHA) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? ConstNpc.ONG_GOHAN
                    : (player.gender == ConstPlayer.NAMEC
                            ? ConstNpc.ONG_MOORI
                            : ConstNpc.ONG_PARAGUS);
        } else if (id == ConstTask.NPC_TTVT) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? ConstNpc.DR_DRIEF
                    : (player.gender == ConstPlayer.NAMEC
                            ? ConstNpc.CARGO
                            : ConstNpc.CUI);
        } else if (id == ConstTask.NPC_SHOP_LANG) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? ConstNpc.BUNMA
                    : (player.gender == ConstPlayer.NAMEC
                            ? ConstNpc.DENDE
                            : ConstNpc.APPULE);
        } else if (id == ConstTask.NPC_QUY_LAO) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? ConstNpc.QUY_LAO_KAME
                    : (player.gender == ConstPlayer.NAMEC
                            ? ConstNpc.TRUONG_LAO_GURU
                            : ConstNpc.VUA_VEGETA);
        }
        return id;
    }

    // replate %1 %2 -> chữ
    private String transformName(Player player, String text) {
        byte gender = player.gender;

        text = text.replaceAll(ConstTask.TEN_NPC_QUY_LAO, player.gender == ConstPlayer.TRAI_DAT
                ? "Quy Lão Kame"
                : (player.gender == ConstPlayer.NAMEC
                        ? "Trưởng lão Guru"
                        : "Vua Vegeta"));
        text = text.replaceAll(ConstTask.TEN_MAP_QUY_LAO, player.gender == ConstPlayer.TRAI_DAT
                ? "Đảo Kamê"
                : (player.gender == ConstPlayer.NAMEC
                        ? "Đảo Guru"
                        : "Vách núi đen"));
        text = text.replaceAll(ConstTask.TEN_QUAI_3000, player.gender == ConstPlayer.TRAI_DAT
                ? "ốc mượn hồn"
                : (player.gender == ConstPlayer.NAMEC
                        ? "ốc sên"
                        : "heo Xayda mẹ"));
        // ----------------------------------------------------------------------
        text = text.replaceAll(ConstTask.TEN_LANG, player.gender == ConstPlayer.TRAI_DAT
                ? "Làng Aru"
                : (player.gender == ConstPlayer.NAMEC
                        ? "Làng Mori"
                        : "Làng Kakarot"));
        text = text.replaceAll(ConstTask.TEN_NPC_NHA, player.gender == ConstPlayer.TRAI_DAT
                ? "Ông Gôhan"
                : (player.gender == ConstPlayer.NAMEC
                        ? "Ông Moori"
                        : "Ông Paragus"));
        text = text.replaceAll(ConstTask.TEN_QUAI_200, player.gender == ConstPlayer.TRAI_DAT
                ? "khủng long"
                : (player.gender == ConstPlayer.NAMEC
                        ? "lợn lòi"
                        : "quỷ đất"));
        text = text.replaceAll(ConstTask.TEN_MAP_200, player.gender == ConstPlayer.TRAI_DAT
                ? "Đồi hoa cúc"
                : (player.gender == ConstPlayer.NAMEC
                        ? "Đồi nấm tím"
                        : "Đồi hoang"));
        text = text.replaceAll(ConstTask.TEN_VACH_NUI, player.gender == ConstPlayer.TRAI_DAT
                ? "Vách núi Aru"
                : (player.gender == ConstPlayer.NAMEC
                        ? "Vách núi Moori"
                        : "Vách núi Kakarot"));
        text = text.replaceAll(ConstTask.TEN_MAP_500, player.gender == ConstPlayer.TRAI_DAT
                ? "Thung lũng tre"
                : (player.gender == ConstPlayer.NAMEC
                        ? "Thị trấn Moori"
                        : "Làng Plane"));
        text = text.replaceAll(ConstTask.TEN_NPC_TTVT, player.gender == ConstPlayer.TRAI_DAT
                ? "Dr. Brief"
                : (player.gender == ConstPlayer.NAMEC
                        ? "Cargo"
                        : "Cui"));
        text = text.replaceAll(ConstTask.TEN_QUAI_BAY_600, player.gender == ConstPlayer.TRAI_DAT
                ? "thằn lằn bay"
                : (player.gender == ConstPlayer.NAMEC
                        ? "phi long"
                        : "quỷ bay"));
        text = text.replaceAll(ConstTask.TEN_NPC_SHOP_LANG, player.gender == ConstPlayer.TRAI_DAT
                ? "Bunma"
                : (player.gender == ConstPlayer.NAMEC
                        ? "Dende"
                        : "Appule"));
        return text;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j <= 10; j++) {
                System.out.println("case ConstTask.TASK_" + i + "_" + j + ":");
                System.out.println("return player.playerTask.taskMain.id == " + i
                        + " && player.playerTask.taskMain.index == " + j + ";");
            }
        }
    }

    private boolean isCurrentTask(Player player, int idTaskCustom) {
        return idTaskCustom == (player.playerTask.taskMain.id << 10) + (player.playerTask.taskMain.index << 1);
    }

    public int getIdTask(Player player) {
        if (player.isPet || player.isClone || player.isBoss || player.playerTask == null
                || player.playerTask.taskMain == null) {
            return -1;
        }
        return (player.playerTask.taskMain.id << 10) + (player.playerTask.taskMain.index << 1);
    }

    // --------------------------------------------------------------------------
    public SideTaskTemplate getSideTaskTemplateById(int id) {
        if (id != -1) {
            return Manager.SIDE_TASKS_TEMPLATE.get(id);
        }
        return null;
    }

    public void changeSideTask(Player player, byte level) {
        if (player.playerTask.sideTask.leftTask > 0) {
            player.playerTask.sideTask.reset();
            SideTaskTemplate temp = Manager.SIDE_TASKS_TEMPLATE
                    .get(Util.nextInt(0, Manager.SIDE_TASKS_TEMPLATE.size() - 1));
            player.playerTask.sideTask.template = temp;
            player.playerTask.sideTask.maxCount = Util.nextInt(temp.count[level][0], temp.count[level][1]);
            player.playerTask.sideTask.leftTask--;
            player.playerTask.sideTask.level = level;
            player.playerTask.sideTask.receivedTime = System.currentTimeMillis();
            Service.gI().sendThongBao(player, "Bạn Nhận Được Nhiệm Vụ: " + player.playerTask.sideTask.getName());
        } else {
            Service.gI().sendThongBao(player,
                    "Bạn Đã Nhận Hết Nhiệm Vụ Hôm Nay. Hãy Chờ Tới Ngày Mai Rồi Nhận Tiếp");
        }
    }

    public void removeSideTask(Player player) {
        Service.gI().sendThongBao(player, "Bạn Vừa Hủy Bỏ Nhiệm Vụ " + player.playerTask.sideTask.getName());
        player.playerTask.sideTask.reset();
    }

    public void paySideTask(Player player) {
        if (player.playerTask.sideTask.template != null) {
            if (player.playerTask.sideTask.isDone()) {
                int goldReward = 0;
                Item ngocdoc = ItemService.gI().createNewItem((short) 1738);
                Item ngocquylua = ItemService.gI().createNewItem((short) 1744);
                switch (player.playerTask.sideTask.level) {
                    case ConstTask.EASY:
                        goldReward = ConstTask.GOLD_EASY;
                        break;
                    case ConstTask.NORMAL:
                        goldReward = ConstTask.GOLD_NORMAL;
                        break;
                    case ConstTask.HARD:
                        goldReward = ConstTask.GOLD_HARD;
                        break;
                    case ConstTask.VERY_HARD:
                        goldReward = ConstTask.GOLD_VERY_HARD;
                        InventoryServiceNew.gI().addItemBag(player, ngocdoc);
                        Service.gI().sendThongBao(player, "Bạn Nhận Được 1 " + ngocdoc.template.name);
                        break;
                    case ConstTask.HELL:
                        goldReward = ConstTask.GOLD_HELL;
                        InventoryServiceNew.gI().addItemBag(player, ngocquylua);
                        Service.gI().sendThongBao(player, "Bạn Nhận Được 1 " + ngocquylua.template.name);
                        break;
                }
                player.inventory.addGold(goldReward);
                Service.gI().sendMoney(player);
                Service.gI().sendThongBao(player, "Bạn Nhận Được "
                        + Util.numberToMoney(goldReward) + " Vàng");
                player.playerTask.sideTask.reset();
            } else {
                Service.gI().sendThongBao(player, "Bạn Chưa Hoàn Thành Nhiệm Vụ");
            }
        }
    }
    // phước nhiệm vụ check sql side task nha

    public void checkDoneSideTaskKillMob(Player player, Mob mob) {
        if (player.playerTask.sideTask.template != null) {
            if ((player.playerTask.sideTask.template.id == 0 && mob.tempId == ConstMob.OC_SEN)
                    || (player.playerTask.sideTask.template.id == 1 && mob.tempId == ConstMob.HEO_XANH_ME)
                    || (player.playerTask.sideTask.template.id == 2 && mob.tempId == ConstMob.UKULELE)
                    || (player.playerTask.sideTask.template.id == 3 && mob.tempId == ConstMob.DRUM)
                    || (player.playerTask.sideTask.template.id == 4 && mob.tempId == ConstMob.ROBOT_THEP)
                    || (player.playerTask.sideTask.template.id == 5 && mob.tempId == ConstMob.AKKUMAN)
                    || (player.playerTask.sideTask.template.id == 6 && mob.tempId == ConstMob.BULON)) {
                player.playerTask.sideTask.count++;
                notifyProcessSideTask(player);
            }
        }
    }

    private void notifyProcessSideTask(Player player) {
        int percentDone = player.playerTask.sideTask.getPercentProcess();
        boolean notify = false;
        if (percentDone != 100) {
            if (!player.playerTask.sideTask.notify90 && percentDone >= 90) {
                player.playerTask.sideTask.notify90 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify80 && percentDone >= 80) {
                player.playerTask.sideTask.notify80 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify70 && percentDone >= 70) {
                player.playerTask.sideTask.notify70 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify60 && percentDone >= 60) {
                player.playerTask.sideTask.notify60 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify50 && percentDone >= 50) {
                player.playerTask.sideTask.notify50 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify40 && percentDone >= 40) {
                player.playerTask.sideTask.notify40 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify30 && percentDone >= 30) {
                player.playerTask.sideTask.notify30 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify20 && percentDone >= 20) {
                player.playerTask.sideTask.notify20 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify10 && percentDone >= 10) {
                player.playerTask.sideTask.notify10 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify0 && percentDone >= 0) {
                player.playerTask.sideTask.notify0 = true;
                notify = true;
            }
            if (notify) {
                Service.gI().sendThongBao(player, "Nhiệm Vụ: "
                        + player.playerTask.sideTask.getName() + " Đã Hoàn Thành: "
                        + player.playerTask.sideTask.count + "/" + player.playerTask.sideTask.maxCount + " ("
                        + percentDone + "%)");
            }
        } else {
            Service.gI().sendThongBao(player, "Chúc Mừng Bạn Đã Hoàn Thành Nhiệm Vụ, "
                    + "Bây Giờ Hãy Quay Về Bò Mộng Trả Nhiệm Vụ.");
        }
    }
}
