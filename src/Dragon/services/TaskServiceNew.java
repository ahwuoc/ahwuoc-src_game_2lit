package Dragon.services;

import Dragon.jdbc.daos.TaskCache;
import Dragon.models.boss.Boss;
import Dragon.models.item.Item;
import Dragon.models.map.ItemMap;
import Dragon.models.map.Zone;
import Dragon.models.mob.Mob;
import Dragon.models.npc.Npc;
import Dragon.models.player.Player;
import Dragon.utils.Logger;
import Dragon.server.Manager;
import Dragon.jdbc.daos.BossDataService;

import java.util.List;

public class TaskServiceNew {

    private static TaskServiceNew instance;
    private TaskCache cache;
    private final java.util.concurrent.ConcurrentHashMap<Integer, String> bossNameCache = new java.util.concurrent.ConcurrentHashMap<>();

    public static TaskServiceNew getInstance() {
        if (instance == null) {
            instance = new TaskServiceNew();
        }
        return instance;
    }

    private TaskServiceNew() {
        this.cache = TaskCache.getInstance();
    }

    public void ensureMaxCountSyncedForPlayer(Player player) {
        try {
            if (player == null || player.playerTask == null || player.playerTask.taskMain == null) {
                return;
            }
            int mainId = player.playerTask.taskMain.id;
            int subId = player.playerTask.taskMain.index;
            List<TaskCache.TaskRequirement> reqs = cache.getTaskRequirements(mainId, subId, null);
            for (TaskCache.TaskRequirement r : reqs) {
                ensureCurrentSubTaskMaxCount(player, r);
            }
        } catch (Exception e) {
            Logger.logException(TaskServiceNew.class, e);
        }
    }

    /**
     * Check task completion when using item
     */
    public void checkDoneTaskUseItem(Player player, int itemTemplateId) {
        if (!player.isBoss && !player.isPet && !player.isClone) {
           // Logger.log("TaskServiceNew: Player " + player.name + " used item template " + itemTemplateId);

            int currentTaskId = getCurrentTaskId(player);
            if (currentTaskId == -1) {
                return;
            }

            int taskMainId = getTaskMainId(currentTaskId);
            int taskSubId = getTaskSubId(currentTaskId);

            List<TaskCache.TaskRequirement> requirements = cache.getTaskRequirements(taskMainId, taskSubId,
                    "USE_ITEM");

            for (TaskCache.TaskRequirement req : requirements) {
                if (req.targetId == itemTemplateId) {
                    if (isCurrentTask(player, req.taskMainId, req.taskSubId)) {
                        //Logger.log(
                        //        "TaskServiceNew: ✅ Use-item task requirement matched and is current task - "
                         //       + req.toString());
                        //incrementTaskProgress(player, req, 1);
                    } else {
                       // Logger.log(
                        //        "TaskServiceNew: ❌ Use-item task requirement found but NOT current task - "
                         //       + req.toString());
                    }
                }
            }
        }
    }

    public void syncAllSubTaskMaxCountsForCurrentTask(Player player) {
        try {
            if (player == null || player.playerTask == null || player.playerTask.taskMain == null) {
                return;
            }
            int mainId = player.playerTask.taskMain.id;
            int totalSubs = player.playerTask.taskMain.subTasks.size();
            for (int sub = 0; sub < totalSubs; sub++) {
                List<TaskCache.TaskRequirement> reqs = cache.getTaskRequirements(mainId, sub, null);
                int maxTarget = 0;
                for (TaskCache.TaskRequirement r : reqs) {
                    if (r.targetCount > maxTarget) {
                        maxTarget = r.targetCount;
                    }
                }
                if (maxTarget > 0) {
                    Dragon.models.task.SubTaskMain stm = player.playerTask.taskMain.subTasks.get(sub);
                    if (stm.maxCount != maxTarget) {
                        Logger.log("TaskServiceNew: Sync subtask[" + sub + "] maxCount from " + stm.maxCount
                                + " -> " + maxTarget + " for task " + mainId + "_" + sub);
                        stm.maxCount = (short) maxTarget;
                    }
                }
            }
        } catch (Exception e) {
            Logger.logException(TaskServiceNew.class, e);
        }
    }

    public void prepareSubTaskMetaForUI(Player player) {
        try {
            if (player == null || player.playerTask == null || player.playerTask.taskMain == null) {
                return;
            }
            int mainId = player.playerTask.taskMain.id;
            int totalSubs = player.playerTask.taskMain.subTasks.size();
            for (int sub = 0; sub < totalSubs; sub++) {
                Dragon.models.task.SubTaskMain stm = player.playerTask.taskMain.subTasks.get(sub);
                List<TaskCache.TaskRequirement> reqs = cache.getTaskRequirements(mainId, sub, null);
                if (reqs.isEmpty()) {
                    if (stm.name == null || stm.name.isEmpty()) {
                        stm.name = "Nhiệm vụ";
                    }
                    if (stm.notify == null) {
                        stm.notify = "";
                    }
                    continue;
                }

                TaskCache.TaskRequirement r = reqs.get(0);
                String displayName;
                switch (r.requirementType) {
                    case "KILL_MOB": {
                        String mobName = null;
                        try {
                            for (Dragon.models.Template.MobTemplate mt : Manager.MOB_TEMPLATES) {
                                if (mt.id == (byte) r.targetId) {
                                    mobName = mt.name;
                                    break;
                                }
                            }
                        } catch (Exception ignored) {
                        }
                        displayName = mobName != null && !mobName.isEmpty()
                                ? ("Tiêu diệt " + mobName)
                                : "Tiêu diệt quái";
                        break;
                    }
                    case "KILL_BOSS": {
                        String bossName = null;
                        try {
                            bossName = bossNameCache.computeIfAbsent(r.targetId, id -> {
                                try {
                                    Dragon.models.boss.BossData bd = BossDataService.getInstance().loadBossById(id);
                                    return bd != null && bd.getName() != null ? bd.getName() : null;
                                } catch (Exception e) {
                                    return null;
                                }
                            });
                        } catch (Exception ignored) {
                        }
                        displayName = (bossName != null && !bossName.isEmpty()) ? ("Tiêu diệt " + bossName)
                                : "Tiêu diệt Boss";
                        break;
                    }
                    case "TALK_NPC": {
                        String npcName = null;
                        try {
                            for (Dragon.models.Template.NpcTemplate nt : Manager.NPC_TEMPLATES) {
                                if (nt.id == (byte) r.targetId) {
                                    npcName = nt.name;
                                    break;
                                }
                            }
                        } catch (Exception ignored) {
                        }
                        displayName = npcName != null && !npcName.isEmpty()
                                ? ("Gặp " + npcName)
                                : "Nói chuyện với NPC";
                        break;
                    }
                    case "PICK_ITEM":
                        String itemName = null;
                        try {
                            for (Dragon.models.Template.ItemTemplate it : Manager.ITEM_TEMPLATES) {
                                if (it.id == (short) r.targetId) {
                                    itemName = it.name;
                                    break;
                                }
                            }
                        } catch (Exception ignored) {
                        }
                        displayName = (itemName != null && !itemName.isEmpty())
                                ? ("Nhặt " + itemName)
                                : "Nhặt vật phẩm";
                        break;
                    case "GO_TO_MAP": {
                        String mapName = null;
                        try {
                            if (Manager.MAP_TEMPLATES != null) {
                                for (Dragon.models.Template.MapTemplate mt : Manager.MAP_TEMPLATES) {
                                    if (mt != null && mt.id == (short) r.targetId) {
                                        mapName = mt.name;
                                        break;
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                        }
                        displayName = (mapName != null && !mapName.isEmpty())
                                ? ("Đến " + mapName)
                                : "Di chuyển đến bản đồ";
                        break;
                    }
                    case "USE_ITEM": {
                        String useItemName = null;
                        try {
                            for (Dragon.models.Template.ItemTemplate it : Manager.ITEM_TEMPLATES) {
                                if (it.id == (short) r.targetId) {
                                    useItemName = it.name;
                                    break;
                                }
                            }
                        } catch (Exception ignored) {
                        }
                        displayName = (useItemName != null && !useItemName.isEmpty())
                                ? ("Sử dụng " + useItemName)
                                : "Sử dụng vật phẩm";
                        break;
                    }
                    default:
                        displayName = "Nhiệm vụ";
                }
                stm.name = displayName;
                stm.notify = "Hoàn thành nhiệm vụ";

                if ("TALK_NPC".equals(r.requirementType)) {
                    stm.npcId = (byte) r.targetId;
                } else if (stm.npcId == 0) {
                    stm.npcId = -1;
                }

                try {
                    if (r.mapRestriction != null && r.mapRestriction.matches("^\\d+$")) {
                        stm.mapId = (short) Integer.parseInt(r.mapRestriction);
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            Logger.logException(TaskServiceNew.class, e);
        }
    }

    public void checkDoneTaskKillMob(Player player, Mob mob) {
        if (!player.isBoss && !player.isPet && !player.isClone) {
            Logger.log("TaskServiceNew: Player " + player.name + " killed mob " + mob.tempId
                    + " at map " + mob.zone.map.mapId);

            int currentTaskId = getCurrentTaskId(player);
            if (currentTaskId == -1) {
                return;
            }

            int taskMainId = getTaskMainId(currentTaskId);
            int taskSubId = getTaskSubId(currentTaskId);

            List<TaskCache.TaskRequirement> requirements = cache.getTaskRequirements(taskMainId, taskSubId, "KILL_MOB");

            for (TaskCache.TaskRequirement req : requirements) {
                if (req.targetId == mob.tempId && checkMapRestriction(req.mapRestriction, mob.zone.map.mapId)) {
                    if (isCurrentTask(player, req.taskMainId, req.taskSubId)) {
                        Logger.log("TaskServiceNew: ✅ Mob task requirement matched and is current task - "
                                + req.toString());
                        incrementTaskProgress(player, req, 1);
                    } else {
                        Logger.log("TaskServiceNew: ❌ Mob task requirement found but NOT current task - "
                                + req.toString());
                    }
                }
            }
        }
    }

    public void checkDoneTaskKillBoss(Player player, Boss boss) {
        if (player != null && !player.isBoss && !player.isPet && !player.isClone) {
            Logger.log("TaskServiceNew: Player " + player.name + " killed boss " + boss.id);

            int currentTaskId = getCurrentTaskId(player);
            if (currentTaskId == -1) {
                return;
            }

            int taskMainId = getTaskMainId(currentTaskId);
            int taskSubId = getTaskSubId(currentTaskId);

            List<TaskCache.TaskRequirement> requirements = cache.getTaskRequirements(taskMainId, taskSubId,
                    "KILL_BOSS");

            for (TaskCache.TaskRequirement req : requirements) {
                if (req.targetId == (int) boss.id) {
                    if (isCurrentTask(player, req.taskMainId, req.taskSubId)) {
                        Logger.log("TaskServiceNew: ✅ Boss task requirement matched and is current task - "
                                + req.toString());
                        incrementTaskProgress(player, req, 1);
                    } else {
                        Logger.log("TaskServiceNew: ❌ Boss task requirement found but NOT current task - "
                                + req.toString());
                    }
                }
            }
        }
    }

    public boolean checkDoneTaskTalkNpc(Player player, Npc npc) {
        Logger.log("TaskServiceNew: Player " + player.name + " talked to NPC " + npc.tempId
                + " at map " + npc.mapId);

        int currentTaskId = getCurrentTaskId(player);
        Logger.log("TaskServiceNew: Current task ID for player " + player.name + ": " + currentTaskId);

        if (currentTaskId == -1) {
            Logger.log("TaskServiceNew: No current task for player " + player.name);
            return false;
        }

        int taskMainId = getTaskMainId(currentTaskId);
        int taskSubId = getTaskSubId(currentTaskId);

        Logger.log("TaskServiceNew: Current task: Main=" + taskMainId + ", Sub=" + taskSubId);

        List<TaskCache.TaskRequirement> requirements = cache.getTaskRequirements(taskMainId, taskSubId, "TALK_NPC");
        Logger.log("TaskServiceNew: Found " + requirements.size() + " TALK_NPC requirements for task " + taskMainId
                + "_" + taskSubId);

        for (TaskCache.TaskRequirement req : requirements) {
            Logger.log("TaskServiceNew: Checking requirement - " + req.toString());
            Logger.log("TaskServiceNew: NPC ID match: " + (req.targetId == npc.tempId)
                    + " (required: " + req.targetId + ", actual: " + npc.tempId + ")");
            Logger.log("TaskServiceNew: Map restriction check: " + checkMapRestriction(req.mapRestriction, npc.mapId)
                    + " (restriction: " + req.mapRestriction + ", map: " + npc.mapId + ")");

            if (req.targetId == npc.tempId && checkMapRestriction(req.mapRestriction, npc.mapId)) {
                if (isCurrentTask(player, req.taskMainId, req.taskSubId)) {
                    Logger.log(
                            "TaskServiceNew: ✅ NPC task requirement matched and is current task - " + req.toString());
                    return incrementTaskProgress(player, req, 1);
                } else {
                    Logger.log("TaskServiceNew: ❌ NPC task requirement found but NOT current task - " + req.toString());
                    Logger.log("TaskServiceNew: Required task: " + req.taskMainId + "_" + req.taskSubId
                            + ", Current task: " + taskMainId + "_" + taskSubId);
                    return false; // Không hoàn thành nếu không đúng thứ tự
                }
            } else {
                Logger.log("TaskServiceNew: Requirement not matched - " + req.toString());
            }
        }

        Logger.log("TaskServiceNew: No matching TALK_NPC requirements found for player " + player.name);
        return false;
    }

    public void checkDoneTaskPickItem(Player player, ItemMap item) {
        if (!player.isBoss && !player.isPet && !player.isClone && item != null) {
            Logger.log("TaskServiceNew: Player " + player.name + " picked item " + item.itemTemplate.id);

            int currentTaskId = getCurrentTaskId(player);
            if (currentTaskId == -1) {
                return;
            }

            int taskMainId = getTaskMainId(currentTaskId);
            int taskSubId = getTaskSubId(currentTaskId);

            List<TaskCache.TaskRequirement> requirements = cache.getTaskRequirements(taskMainId, taskSubId,
                    "PICK_ITEM");

            for (TaskCache.TaskRequirement req : requirements) {
                if (req.targetId == item.itemTemplate.id) {
                    if (isCurrentTask(player, req.taskMainId, req.taskSubId)) {
                        Logger.log("TaskServiceNew: ✅ Item task requirement matched and is current task - "
                                + req.toString());
                        incrementTaskProgress(player, req, 1);
                    } else {
                        Logger.log("TaskServiceNew: ❌ Item task requirement found but NOT current task - "
                                + req.toString());
                    }
                }
            }
        }
    }

    /**
     * Check task completion when going to map
     */
    public void checkDoneTaskGoToMap(Player player, Zone zoneJoin) {
        if (player.isPl() && !player.isBot) {
            Logger.log("TaskServiceNew: Player " + player.name + " entered map " + zoneJoin.map.mapId);

            int currentTaskId = getCurrentTaskId(player);
            if (currentTaskId == -1) {
                return;
            }

            int taskMainId = getTaskMainId(currentTaskId);
            int taskSubId = getTaskSubId(currentTaskId);

            List<TaskCache.TaskRequirement> requirements = cache.getTaskRequirements(taskMainId, taskSubId,
                    "GO_TO_MAP");

            for (TaskCache.TaskRequirement req : requirements) {
                if (req.targetId == zoneJoin.map.mapId) {
                    if (isCurrentTask(player, req.taskMainId, req.taskSubId)) {
                        Logger.log("TaskServiceNew: ✅ Map task requirement matched and is current task - "
                                + req.toString());
                        incrementTaskProgress(player, req, 1);
                    } else {
                        Logger.log("TaskServiceNew: ❌ Map task requirement found but NOT current task - "
                                + req.toString());
                    }
                }
            }
        }
    }

    /**
     * Check map restriction
     */
    private boolean checkMapRestriction(String mapRestriction, int mapId) {
        if (mapRestriction == null || mapRestriction.isEmpty()) {
            return true; // No restriction
        }

        try {
            String restriction = mapRestriction.trim();

            // Handle exclusion (!)
            if (restriction.startsWith("!")) {
                return !checkMapInRange(restriction.substring(1), mapId);
            }

            // Handle inclusion
            return checkMapInRange(restriction, mapId);

        } catch (Exception e) {
            Logger.logException(TaskServiceNew.class, e);
            return false; // Safe fallback
        }
    }

    private boolean checkMapInRange(String rangeStr, int mapId) {
        String[] ranges = rangeStr.split(",");

        for (String range : ranges) {
            range = range.trim();

            if (range.contains("-")) {
                String[] parts = range.split("-");
                if (parts.length == 2) {
                    int start = Integer.parseInt(parts[0].trim());
                    int end = Integer.parseInt(parts[1].trim());
                    if (mapId >= start && mapId <= end) {
                        return true;
                    }
                }
            } else {
                int singleMap = Integer.parseInt(range);
                if (mapId == singleMap) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean incrementTaskProgress(Player player, TaskCache.TaskRequirement req, int amount) {
        int currentProgress = getCurrentTaskProgress(player, req);
        int newProgress = currentProgress + amount;
        Logger.log("TaskServiceNew: Task progress " + req.taskMainId + "_" + req.taskSubId
                + ": " + currentProgress + " + " + amount + " = " + newProgress + "/" + req.targetCount);

        boolean maxChanged = ensureCurrentSubTaskMaxCount(player, req);
        setCurrentTaskProgress(player, req, newProgress);

        if (newProgress >= req.targetCount) {
            Logger.log("TaskServiceNew: Task completed! " + req.toString());
            completeTask(player, req.taskMainId, req.taskSubId);
            return true;
        } else {
            TaskService.gI().sendUpdateCountSubTask(player);
            if (maxChanged) {
                TaskService.gI().sendTaskMain(player);
            }
        }

        return false;
    }

    private void completeTask(Player player, int taskMainId, int taskSubId) {
        Logger.log("TaskServiceNew: Completing task " + taskMainId + "_" + taskSubId + " for player " + player.name);
        if (player == null || player.playerTask == null || player.playerTask.taskMain == null
                || player.playerTask.taskMain.id != taskMainId
                || player.playerTask.taskMain.index != taskSubId) {
            Logger.log("TaskServiceNew: Skip completion - player already moved to task "
                    + (player != null && player.playerTask != null && player.playerTask.taskMain != null
                            ? (player.playerTask.taskMain.id + "_" + player.playerTask.taskMain.index)
                            : "null"));
            return;
        }

        // Build a user-friendly completion message before mutating task state
        String mainTaskName = player.playerTask.taskMain.name;
        String subTaskName = null;
        try {
            if (player.playerTask.taskMain.index < player.playerTask.taskMain.subTasks.size()) {
                Dragon.models.task.SubTaskMain currentSub = player.playerTask.taskMain.subTasks
                        .get(player.playerTask.taskMain.index);
                subTaskName = currentSub != null ? currentSub.name : null;
            }
        } catch (Exception ignored) {
        }
        String friendlyMsg = "Hoàn thành nhiệm vụ: "
                + (mainTaskName != null && !mainTaskName.isEmpty() ? mainTaskName : ("Nhiệm vụ " + taskMainId))
                + " - "
                + (subTaskName != null && !subTaskName.isEmpty() ? subTaskName : ("Bước " + (taskSubId + 1)));

        List<TaskCache.TaskReward> rewards = cache.getTaskRewards(taskMainId, taskSubId);
        for (TaskCache.TaskReward reward : rewards) {
            giveRewardToPlayer(player, reward);
        }

        if (player.playerTask.taskMain.index < player.playerTask.taskMain.subTasks.size() - 1) {
            player.playerTask.taskMain.index += 1;
            syncMaxCountForCurrentSubTaskFromCache(player);
            try {
                if (player.playerTask.taskMain.index < player.playerTask.taskMain.subTasks.size()) {
                    player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count = 0;
                }
            } catch (Exception ignored) {
            }
            prepareSubTaskMetaForUI(player);
            TaskService.gI().sendTaskMain(player);
        } else {
            Dragon.models.task.TaskMain nextTemplate = TaskService.gI()
                    .getTaskMainByIdTemplate(player.playerTask.taskMain.id + 1);
            if (nextTemplate != null) {
                player.playerTask.taskMain = TaskService.gI().getTaskMainById(player,
                        player.playerTask.taskMain.id + 1);
                player.playerTask.taskMain.index = 0;
                syncMaxCountForCurrentSubTaskFromCache(player);
                try {
                    if (!player.playerTask.taskMain.subTasks.isEmpty()) {
                        player.playerTask.taskMain.subTasks.get(0).count = 0;
                    }
                } catch (Exception ignored) {
                }
                prepareSubTaskMetaForUI(player);
                Logger.log("TaskServiceNew: Moved to next main task: " + player.playerTask.taskMain.id + "_0");
                TaskService.gI().sendTaskMain(player);
            } else {
                Logger.log("TaskServiceNew: No next task template. Showing placeholder task.");
                Dragon.models.task.TaskMain placeholder = new Dragon.models.task.TaskMain();
                placeholder.id = player.playerTask.taskMain.id + 1;
                placeholder.name = "Nhiệm vụ sắp cập nhật";
                placeholder.detail = "Nhiệm vụ sẽ được cập nhật trong thời gian tới";
                Dragon.models.task.SubTaskMain sub = new Dragon.models.task.SubTaskMain();
                sub.name = "Nhiệm vụ sẽ được cập nhật trong thời gian tới";
                sub.notify = "";
                sub.npcId = (byte) -1;
                sub.mapId = (short) -1;
                sub.maxCount = (short) 1;
                sub.count = 0;
                placeholder.subTasks.add(sub);
                player.playerTask.taskMain = placeholder;
                player.playerTask.taskMain.index = 0;
                TaskService.gI().sendTaskMain(player);
            }
        }
        Service.gI().sendThongBao(player, friendlyMsg + "!");
        TaskService.gI().sendInfoCurrentTask(player);
    }

    private boolean ensureCurrentSubTaskMaxCount(Player player, TaskCache.TaskRequirement req) {
        try {
            if (player.playerTask == null || player.playerTask.taskMain == null) {
                return false;
            }
            if (player.playerTask.taskMain.id != req.taskMainId || player.playerTask.taskMain.index != req.taskSubId) {
                return false;
            }
            if (player.playerTask.taskMain.index >= player.playerTask.taskMain.subTasks.size()) {
                return false;
            }

            Dragon.models.task.SubTaskMain stm = player.playerTask.taskMain.subTasks
                    .get(player.playerTask.taskMain.index);
            if (stm.maxCount != req.targetCount) {
                Logger.log("TaskServiceNew: Sync maxCount from " + stm.maxCount + " -> " + req.targetCount
                        + " for task " + req.taskMainId + "_" + req.taskSubId);
                stm.maxCount = (short) req.targetCount;
                return true;
            }
        } catch (Exception e) {
            Logger.logException(TaskServiceNew.class, e);
        }
        return false;
    }

    // Lấy targetCount từ cache cho subtask hiện tại và set vào maxCount nếu tìm
    // thấy
    private void syncMaxCountForCurrentSubTaskFromCache(Player player) {
        try {
            if (player.playerTask == null || player.playerTask.taskMain == null) {
                return;
            }
            int mainId = player.playerTask.taskMain.id;
            int subId = player.playerTask.taskMain.index;
            List<TaskCache.TaskRequirement> allReqs = cache.getTaskRequirements(mainId, subId, null);
            int maxTarget = 0;
            for (TaskCache.TaskRequirement r : allReqs) {
                if (r.targetCount > maxTarget) {
                    maxTarget = r.targetCount;
                }
            }
            if (maxTarget > 0 && subId < player.playerTask.taskMain.subTasks.size()) {
                Dragon.models.task.SubTaskMain stm = player.playerTask.taskMain.subTasks.get(subId);
                if (stm.maxCount != maxTarget) {
                    Logger.log("TaskServiceNew: Sync next subtask maxCount to " + maxTarget
                            + " for task " + mainId + "_" + subId);
                    stm.maxCount = (short) maxTarget;
                }
            }
        } catch (Exception e) {
            Logger.logException(TaskServiceNew.class, e);
        }
    }

    private void giveRewardToPlayer(Player player, TaskCache.TaskReward reward) {
        Logger.log("TaskServiceNew: Giving reward " + reward.toString() + " to player " + player.name);

        switch (reward.rewardType) {
            case "ITEM":
                Item item = ItemService.gI().createNewItem((short) reward.rewardId, (int) reward.rewardQuantity);
                InventoryServiceNew.gI().addItemBag(player, item);
                InventoryServiceNew.gI().sendItemBags(player);
                break;

            case "GOLD":
                player.inventory.gold = Math.min(player.inventory.gold + reward.rewardQuantity,
                        Dragon.models.player.Inventory.LIMIT_GOLD);
                Service.gI().sendMoney(player);
                break;

            case "EXP":
                Service.gI().addSMTN(player, (byte) 1, reward.rewardQuantity, true);
                break;

            case "RUBY":
                player.inventory.ruby = Math.min(player.inventory.ruby + (int) reward.rewardQuantity, 2000000000);
                Service.gI().sendMoney(player);
                break;
        }

        if (reward.rewardDescription != null && !reward.rewardDescription.isEmpty()) {
            Service.gI().sendThongBao(player, reward.rewardDescription);
        }
    }

    private int getCurrentTaskId(Player player) {
        return TaskService.gI().getIdTask(player);
    }

    private int getTaskMainId(int taskId) {
        return taskId >> 10;
    }

    private int getTaskSubId(int taskId) {
        return (taskId >> 1) & 0x1FF;
    }

    private boolean isCurrentTask(Player player, int taskMainId, int taskSubId) {
        if (player.playerTask == null || player.playerTask.taskMain == null) {
            Logger.log("TaskServiceNew: Player task data is null");
            return false;
        }

        boolean isCurrent = player.playerTask.taskMain.id == taskMainId
                && player.playerTask.taskMain.index == taskSubId;

        Logger.log("TaskServiceNew: Task sequence check - Required: " + taskMainId + "_" + taskSubId
                + ", Current: " + player.playerTask.taskMain.id + "_" + player.playerTask.taskMain.index
                + ", Match: " + isCurrent);

        return isCurrent;
    }

    private int getCurrentTaskProgress(Player player, TaskCache.TaskRequirement req) {
        if (player.playerTask == null || player.playerTask.taskMain == null) {
            return 0;
        }
        if (player.playerTask.taskMain.id == req.taskMainId
                && player.playerTask.taskMain.index == req.taskSubId) {

            if (player.playerTask.taskMain.index < player.playerTask.taskMain.subTasks.size()) {
                return (int) player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count;
            }
        }

        return 0;
    }

    private void setCurrentTaskProgress(Player player, TaskCache.TaskRequirement req, int progress) {
        if (player.playerTask == null || player.playerTask.taskMain == null) {
            return;
        }

        if (player.playerTask.taskMain.id == req.taskMainId
                && player.playerTask.taskMain.index == req.taskSubId) {

            if (player.playerTask.taskMain.index < player.playerTask.taskMain.subTasks.size()) {
                player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count = (short) progress;
            }
        }
    }

    /**
     * Refresh cache
     */
    public void refreshCache() {
        cache.refreshCache();
    }

    /**
     * Get cache stats
     */
    public String getCacheStats() {
        return cache.getCacheStats();
    }
}
