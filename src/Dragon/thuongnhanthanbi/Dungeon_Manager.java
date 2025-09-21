package Dragon.thuongnhanthanbi;

import Dragon.models.player.Player;
import Dragon.services.func.ChangeMapService;
import Dragon.models.map.Zone;
import Dragon.services.MapService;
import Dragon.services.Service;
import Dragon.utils.TimeUtil;
import Dragon.server.Manager;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public class Dungeon_Manager {

    public static final int MAP_ID = 36;

    public static class TimeRange {

        public final int openHour, openMin, openSec;
        public final int closeHour, closeMin, closeSec;

        public TimeRange(int openHour, int openMin, int openSec,
                int closeHour, int closeMin, int closeSec) {
            this.openHour = openHour;
            this.openMin = openMin;
            this.openSec = openSec;
            this.closeHour = closeHour;
            this.closeMin = closeMin;
            this.closeSec = closeSec;
        }
    }

    public static final TimeRange[] TIME_RANGES = {
        new TimeRange(17, 0, 0, 18, 0, 0), // 17:00 - 18:00
        new TimeRange(20, 0, 0, 21, 0, 0), // 20:00 - 21:00
        new TimeRange(22, 0, 0, 23, 0, 0) // 22:00 - 23:00
    };

    public static final int AVAILABLE = 7;

    public static final int MAX_PARTICIPATION_PER_DAY = 3; // Số lần tham gia tối đa mỗi ngày
    public static final int PENALTY_FOR_FAILURE = 1; // Số lần bị trừ khi thất bại

    private static Dungeon_Manager i;

    public static long[] TIME_OPEN_ARRAY;
    public static long[] TIME_CLOSE_ARRAY;

    private int day = -1;
    private long lastResetTime = 0;

    private Map<String, DungeonInstance> activeInstances;
    private Map<Long, String> playerToInstance;
    private Map<Long, Zone> playerOriginalZones;
    private Map<Long, Integer> playerParticipationCount;
    private Map<Long, Integer> playerRemainingAttempts;

    private Dungeon_Manager() {
        this.activeInstances = new ConcurrentHashMap<>();
        this.playerToInstance = new ConcurrentHashMap<>();
        this.playerOriginalZones = new ConcurrentHashMap<>();
        this.playerParticipationCount = new ConcurrentHashMap<>();
        this.playerRemainingAttempts = new ConcurrentHashMap<>();
    }

    public DungeonInstance createDungeonInstance(Player player) {
        if (player.zone == null || player.zone.map.mapId != MAP_ID) {
            return null;
        }

        if (!canPlayerJoinDungeon(player)) {
            Service.gI().sendThongBao(player, "Bạn đã hết lượt tham gia Địa Cung hôm nay! Vui lòng quay lại vào ngày mai.");
            return null;
        }

        String existingInstanceId = playerToInstance.get(player.id);
        if (existingInstanceId != null) {
            DungeonInstance existingInstance = activeInstances.get(existingInstanceId);
            if (existingInstance != null && existingInstance.isActive()) {
                return existingInstance;
            } else {
                cleanupInstance(existingInstanceId);
            }
        }

        String instanceId = UUID.randomUUID().toString();

        Zone playerZone = playerOriginalZones.get(player.id);
        if (playerZone == null) {
            playerZone = findAvailableZone(player.zone.map);
            if (playerZone == null) {
                Service.gI().sendThongBao(player, "Địa Cung đang quá tải! Vui lòng thử lại sau.");
                return null;
            }
            playerOriginalZones.put(player.id, playerZone);
        } else {
            if (!player.zone.map.zones.contains(playerZone)) {
                playerZone = findAvailableZone(player.zone.map);
                if (playerZone != null) {
                    playerOriginalZones.put(player.id, playerZone);
                } else {
                    Service.gI().sendThongBao(player, "Địa Cung đang quá tải! Vui lòng thử lại sau.");
                    return null;
                }
            }
        }

        DungeonInstance instance = new DungeonInstance(instanceId, playerZone, player);

        activeInstances.put(instanceId, instance);
        playerToInstance.put(player.id, instanceId);

        try {
            incrementPlayerParticipation(player);
            movePlayerToZone(player, playerZone);
            instance.startWave();
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Moves player to their zone
     */
    private void movePlayerToZone(Player player, Zone zone) {
        try {
            if (player.zone != null && player.zone.zoneId != zone.zoneId) {
                player.zone.removePlayer(player);
            }

            zone.addPlayer(player);
            player.zone = zone;
            zone.mapInfo(player);
        } catch (Exception e) {
            System.out.println("Error moving player to zone: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getActiveInstanceCount() {
        return activeInstances.size();
    }

    public DungeonInstance getPlayerInstance(Player player) {
        String instanceId = playerToInstance.get(player.id);
        if (instanceId != null) {
            return activeInstances.get(instanceId);
        }
        return null;
    }

    public void updateInstances() {
        List<String> instancesToRemove = new ArrayList<>();

        for (Map.Entry<String, DungeonInstance> entry : activeInstances.entrySet()) {
            DungeonInstance instance = entry.getValue();
            if (instance.isActive()) {
                instance.update();
            } else {
                instancesToRemove.add(entry.getKey());
            }
        }

        for (String instanceId : instancesToRemove) {
            cleanupInstance(instanceId);
        }
    }

    private void cleanupInstance(String instanceId) {
        DungeonInstance instance = activeInstances.remove(instanceId);
        if (instance != null) {
            playerToInstance.remove(instance.getOwner().id);
            Zone zone = instance.getZone();
            if (zone != null) {
                zone.mobs.removeIf(mob -> mob.name != null && mob.name.contains("Wave"));
            }
        }
    }

    public void cleanupPlayerInstance(Player player) {
        String instanceId = playerToInstance.get(player.id);
        if (instanceId != null) {
            cleanupInstance(instanceId);
        }
    }

    public void removePlayerCompletely(Player player) {
        String instanceId = playerToInstance.get(player.id);
        if (instanceId != null) {
            cleanupInstance(instanceId);
        }
        playerOriginalZones.remove(player.id);
    }

    public void onPlayerLeaveDungeon(Player player) {
        try {
            if (player == null) {
                return;
            }

            String instanceId = playerToInstance.get(player.id);
            if (instanceId != null) {
                DungeonInstance instance = activeInstances.get(instanceId);
                if (instance != null && instance.isActive()) {
                    if (instance.isDungeonStarted()) {
                        Service.gI().sendThongBao(player, "Bạn đã rời khỏi Địa Cung! Hành động này được coi như thất bại.");
                        penalizePlayerForFailure(player);
                    }
                }
            }

            removePlayerCompletely(player);
        } catch (Exception e) {
            System.out.println("Lỗi khi player rời khỏi dungeon: " + e.getMessage());
        }
    }

    public Zone getPlayerZone(Player player) {
        return playerOriginalZones.get(player.id);
    }

    public static Dungeon_Manager gI() {
        if (i == null) {
            i = new Dungeon_Manager();
        }
        i.setTime();
        return i;
    }

    public void setTime() {
        if (i.day == -1 || i.day != TimeUtil.getCurrDay()) {
            resetDailyDungeon();

            i.day = TimeUtil.getCurrDay();
            try {
                TIME_OPEN_ARRAY = new long[TIME_RANGES.length];
                TIME_CLOSE_ARRAY = new long[TIME_RANGES.length];

                for (int i = 0; i < TIME_RANGES.length; i++) {
                    TimeRange range = TIME_RANGES[i];
                    TIME_OPEN_ARRAY[i] = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " "
                            + range.openHour + ":" + range.openMin + ":" + range.openSec, "dd/MM/yyyy HH:mm:ss");
                    TIME_CLOSE_ARRAY[i] = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " "
                            + range.closeHour + ":" + range.closeMin + ":" + range.closeSec, "dd/MM/yyyy HH:mm:ss");
                }
            } catch (Exception e) {
                System.out.println("Lỗi khi set thời gian: " + e.getMessage());
            }
        }
    }

    public void update(Player player) {
        if (player.zone == null || !MapService.gI().isdiacung(player.zone.map.mapId)) {
            return;
        }
        try {
            if (player.isAdmin()) {
                return;
            }

            long now = System.currentTimeMillis();
            boolean isInTimeSlot = false;
            for (int i = 0; i < TIME_RANGES.length; i++) {
                if (now >= TIME_OPEN_ARRAY[i] && now <= TIME_CLOSE_ARRAY[i]) {
                    isInTimeSlot = true;
                    break;
                }
            }

            if (!isInTimeSlot) {
                cleanupPlayerInstance(player);
                kickOutOfMap(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void globalUpdate() {
        updateInstances();
        checkAndResetDaily();
    }

    private void kickOutOfMap(Player player) {
        Service.getInstance().sendThongBao(player, "Không Trong Thời Gian Diễn Ra Phó Bản!");
        ChangeMapService.gI().changeMapBySpaceShip(player, 2, -1, 164);
    }

    public void changeMap(Player player, byte index) {
        try {
            // Admin có thể vào dungeon bất cứ lúc nào
            if (player.isAdmin()) {
                ChangeMapService.gI().changeMap(player,
                        player.mapdiacung.get(index).map.mapId, -1, 50, 50);
                return;
            }

            long now = System.currentTimeMillis();
            boolean isInTimeSlot = false;
            for (int i = 0; i < TIME_RANGES.length; i++) {
                if (now >= TIME_OPEN_ARRAY[i] && now <= TIME_CLOSE_ARRAY[i]) {
                    isInTimeSlot = true;
                    break;
                }
            }

            if (isInTimeSlot) {
                ChangeMapService.gI().changeMap(player,
                        player.mapdiacung.get(index).map.mapId, -1, 50, 50);
            } else {
                Service.getInstance().sendThongBao(player, "Phó Bản Địa Cung Chưa Mở!");
                Service.getInstance().hideWaitDialog(player);
            }
        } catch (Exception e) {

        }
    }

    public void joinMapDiacung(Player player) {
        Zone savedZone = playerOriginalZones.get(player.id);
        if (savedZone != null) {
            ChangeMapService.gI().changeMap(player, MAP_ID, savedZone.zoneId, 50, 50);
        } else {
            ChangeMapService.gI().changeMapNonSpaceship(player, MAP_ID, -1, 50);
        }

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                if (player.zone != null && player.zone.map.mapId == MAP_ID) {
                    Zone expectedZone = playerOriginalZones.get(player.id);
                    if (expectedZone != null && player.zone.zoneId != expectedZone.zoneId) {
                        movePlayerToZone(player, expectedZone);
                    }

                    DungeonInstance instance = createDungeonInstance(player);
                    if (instance != null) {
                        Service.gI().sendThongBao(player, "Chào mừng đến với Địa Cung! Chuẩn bị chiến đấu!");
                    } else {
                        Service.gI().sendThongBao(player, "Địa Cung đang quá tải! Vui lòng thử lại sau.");
                        ChangeMapService.gI().changeMapBySpaceShip(player, 2, -1, 164);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private Zone findAvailableZone(Dragon.models.map.Map map) {
        try {
            List<Zone> allZones = map.zones;
            if (allZones == null || allZones.isEmpty()) {
                return null;
            }
            Zone bestZone = null;
            int minPlayers = Integer.MAX_VALUE;
            int totalZones = allZones.size();
            int fullZones = 0;

            for (Zone zone : allZones) {
                int playerCount = zone.getNumOfPlayers();

                if (playerCount >= zone.maxPlayer) {
                    fullZones++;
                }

                if (isZoneSuitableForDungeon(zone)) {
                    if (playerCount < minPlayers) {
                        minPlayers = playerCount;
                        bestZone = zone;
                    }
                }
            }

            if (fullZones >= totalZones) {
                return null;
            }

            if (bestZone != null) {
                return bestZone;
            }

            if (!allZones.isEmpty()) {
                bestZone = allZones.get(0);
                return bestZone;
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isZoneSuitableForDungeon(Zone zone) {
        try {
            if (zone.getNumOfPlayers() >= zone.maxPlayer) {
                return false;
            }

            if (zone.bosses != null && !zone.bosses.isEmpty()) {
                return false;
            }

            if (zone.finishdiacung || zone.finishBlackBallWar || zone.finishMapMaBu) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return true; // Default to true if error
        }
    }

    public boolean isDungeonOverloaded() {
        try {
            Dragon.models.map.Map dungeonMap = MapService.gI().getMapById(MAP_ID);
            if (dungeonMap == null || dungeonMap.zones == null) {
                return true; // Consider overloaded if map doesn't exist
            }

            int totalZones = dungeonMap.zones.size();
            int fullZones = 0;

            for (Zone zone : dungeonMap.zones) {
                if (zone.getNumOfPlayers() >= zone.maxPlayer) {
                    fullZones++;
                }
            }

            return fullZones >= totalZones;
        } catch (Exception e) {
            return true;
        }
    }

    public void fixPlayerZoneAssignment(Player player) {
        try {
            Zone savedZone = playerOriginalZones.get(player.id);
            if (savedZone != null && player.zone != null && player.zone.map.mapId == MAP_ID) {
                if (player.zone.zoneId != savedZone.zoneId) {
                    movePlayerToZone(player, savedZone);
                    Service.gI().sendThongBao(player, "Đã sửa lỗi zone assignment!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPlayerInDungeon(Player player) {
        try {
            if (player == null || player.zone == null) {
                return false;
            }

            if (player.zone.map.mapId != MAP_ID) {
                return false;
            }

            String instanceId = playerToInstance.get(player.id);
            if (instanceId == null) {
                return false;
            }

            DungeonInstance instance = activeInstances.get(instanceId);
            return instance != null && instance.isActive();
        } catch (Exception e) {
            return false;
        }
    }

    public void preventZoneChange(Player player) {
        if (isPlayerInDungeon(player)) {
            Service.gI().sendThongBaoOK(player, "Không thể đổi khu vực khi đang trong Địa Cung!");
        }
    }

    public String getCurrentTimeSlotInfo() {
        long now = System.currentTimeMillis();
        for (int i = 0; i < TIME_RANGES.length; i++) {
            if (now >= TIME_OPEN_ARRAY[i] && now <= TIME_CLOSE_ARRAY[i]) {
                TimeRange range = TIME_RANGES[i];
                return String.format("Khung giờ %d: %02d:%02d - %02d:%02d",
                        i + 1, range.openHour, range.openMin, range.closeHour, range.closeMin);
            }
        }
        return "Địa Cung đang đóng cửa";
    }

    // Lấy thông tin số lần tham gia của player
    public String getPlayerParticipationInfo(Player player) {
        try {
            if (player == null) {
                return "Không có thông tin";
            }

            int participationCount = getPlayerParticipationCount(player.id);
            int remainingAttempts = getPlayerRemainingAttempts(player.id);

            return String.format("Đã tham gia: %d lần | Còn lại: %d lần",
                    participationCount, remainingAttempts);
        } catch (Exception e) {
            return "Lỗi khi lấy thông tin";
        }
    }

    public void resetDailyDungeon() {
        try {
            lastResetTime = System.currentTimeMillis();

            int totalInstances = activeInstances.size();
            int totalPlayers = playerToInstance.size();
            int totalZones = playerOriginalZones.size();

            List<Player> playersToKick = new ArrayList<>();
            for (Map.Entry<Long, String> entry : playerToInstance.entrySet()) {
                Player player = findPlayerById(entry.getKey());
                if (player != null && player.zone != null && player.zone.map.mapId == MAP_ID) {
                    playersToKick.add(player);
                }
            }

            for (Player player : playersToKick) {
                try {
                    Service.gI().sendThongBao(player, "Địa Cung đã reset cho ngày mới! Vui lòng tham gia lại.");
                    ChangeMapService.gI().changeMapBySpaceShip(player, 2, -1, 164);
                } catch (Exception e) {
                    System.out.println("Lỗi khi kick player " + player.name + ": " + e.getMessage());
                }
            }

            activeInstances.clear();
            playerToInstance.clear();
            playerOriginalZones.clear();
            resetPlayerParticipation();
            Dragon.models.map.Map dungeonMap = MapService.gI().getMapById(MAP_ID);
            if (dungeonMap != null && dungeonMap.zones != null) {
                for (Zone zone : dungeonMap.zones) {
                    if (zone.mobs != null) {
                        zone.mobs.removeIf(mob -> mob.name != null && mob.name.contains("Wave"));
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Lỗi khi reset dungeon: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Player findPlayerById(long playerId) {
        try {
            Dragon.models.map.Map dungeonMap = MapService.gI().getMapById(MAP_ID);
            if (dungeonMap != null && dungeonMap.zones != null) {
                for (Zone zone : dungeonMap.zones) {
                    for (Player player : zone.getHumanoids()) {
                        if (player.id == playerId) {
                            return player;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void checkAndResetDaily() {
        try {
            long currentTime = System.currentTimeMillis();
            long timeSinceLastReset = currentTime - lastResetTime;
            long oneDayInMillis = 24 * 60 * 60 * 1000; // 24 giờ

            if (lastResetTime == 0 || timeSinceLastReset >= oneDayInMillis) {
                resetDailyDungeon();
                lastResetTime = currentTime;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean canPlayerJoinDungeon(Player player) {
        try {
            if (player == null) {
                return false;
            }

            int remainingAttempts = getPlayerRemainingAttempts(player.id);
            return remainingAttempts > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // Lấy số lần còn lại của player
    public int getPlayerRemainingAttempts(long playerId) {
        return playerRemainingAttempts.getOrDefault(playerId, MAX_PARTICIPATION_PER_DAY);
    }

    // Lấy số lần đã tham gia của player
    public int getPlayerParticipationCount(long playerId) {
        return playerParticipationCount.getOrDefault(playerId, 0);
    }

    // Tăng số lần tham gia khi player vào dungeon
    public void incrementPlayerParticipation(Player player) {
        try {
            long playerId = player.id;
            int currentCount = playerParticipationCount.getOrDefault(playerId, 0);
            int remainingAttempts = playerRemainingAttempts.getOrDefault(playerId, MAX_PARTICIPATION_PER_DAY);

            playerParticipationCount.put(playerId, currentCount + 1);
            playerRemainingAttempts.put(playerId, remainingAttempts - 1);

            Service.gI().sendThongBao(player, "Bạn còn " + (remainingAttempts - 1) + " lần tham gia Địa Cung hôm nay!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void penalizePlayerForFailure(Player player) {
        try {
            long playerId = player.id;
            int remainingAttempts = playerRemainingAttempts.getOrDefault(playerId, MAX_PARTICIPATION_PER_DAY);
            int newRemainingAttempts = Math.max(0, remainingAttempts - PENALTY_FOR_FAILURE);

            playerRemainingAttempts.put(playerId, newRemainingAttempts);

            if (newRemainingAttempts <= 0) {
                Service.gI().sendThongBao(player, "Bạn đã hết lượt tham gia Địa Cung hôm nay! Vui lòng quay lại vào ngày mai.");
            } else {
                Service.gI().sendThongBao(player, "Bạn bị mất " + PENALTY_FOR_FAILURE + " lượt do thất bại! Còn lại: " + newRemainingAttempts + " lượt.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetPlayerParticipation() {
        try {
            playerParticipationCount.clear();
            playerRemainingAttempts.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
