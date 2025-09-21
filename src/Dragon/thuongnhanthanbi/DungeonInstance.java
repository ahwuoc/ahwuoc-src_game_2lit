package Dragon.thuongnhanthanbi;

import Dragon.models.player.Player;
import Dragon.models.map.Zone;
import Dragon.models.mob.Mob;
import Dragon.services.Service;
import Dragon.utils.Util;
import com.girlkun.network.io.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import Dragon.utils.Logger;

public class DungeonInstance {

    // Config constants -
    public static final double WAVE_POWER_MULTIPLIER = 1.5; // Hệ số tăng sức mạnh mỗi wave
    public static final int BASE_KILLS_REQUIRED = 10; // Số quái cần giết cơ bản
    public static final int KILLS_INCREASE_PER_WAVE = 5; // Tăng số quái cần giết mỗi wave
    public static final long WAVE_TIME_LIMIT = 600000; // Thời gian tối đa cho mỗi wave (10 phút)
    public static final long WAVE_INTERVAL = 30000; // Thời gian chờ giữa các wave (30 giây)

    // Timing constants
    public static final int COUNTDOWN_SECONDS = 5; // Thời gian đếm ngược trước khi bắt đầu wave
    public static final int COUNTDOWN_INTERVAL = 1000; // Thời gian giữa các thông báo đếm ngược (1 giây)
    public static final int KICK_DELAY_SECONDS = 3; // Thời gian chờ trước khi kick player (3 giây)
    public static final long NOTIFICATION_INTERVAL = 60000; // Thời gian giữa các thông báo (1 phút)

    // Wave configuration
    public static final int BASE_MOB_LEVEL = 80;
    public static final int MOB_LEVEL_INCREASE_PER_WAVE = 5; //

    // Reward configuration
    public static final long BASE_EXP_REWARD = 1000; // Exp cơ bản mỗi wave
    public static final int SPECIAL_REWARD_CHANCE = 20; //

    // Map configuration
    public static final int KICK_MAP_ID = 2; //
    public static final int KICK_ZONE_ID = -1; //
    public static final int KICK_X = 164; //

    // Cấu trúc: {template_id, hp, damage, x, y, p_dame, p_tiem_nang, status,
    // last_time_die, exp_reward, special_chance}
    public static Object[][] ENTRY_MOB_SPAWN = {
        {99, 5000000L, 100000L, 225, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 255, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 295, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 325, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 365, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 395, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 435, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 465, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 505, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 535, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 575, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 605, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 645, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 675, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 715, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 745, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 785, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 815, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 855, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 885, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 925, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 955, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 995, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 1025, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 1065, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 1095, 624, 10, 50, 5, 0, 1000L, 20},
        {99, 5000000L, 100000L, 1128, 624, 10, 50, 5, 0, 1000L, 20}
    };

    private String instanceId;
    private Zone zone;
    private Player owner;
    private int currentWave;
    private boolean isActive;
    private long waveInterval;
    private List<Mob> currentWaveMobs;
    private DungeonWaveConfig waveConfig;
    private boolean waveCompleted;
    private long waveCompletionTime;
    private long waveStartTime;
    private long waveTimeLimit;
    private int totalKillsThisWave;
    private int requiredKillsThisWave;
    private long lastNotificationTime;
    private boolean dungeonStarted;

    public DungeonInstance(String instanceId, Zone zone, Player owner) {
        this.instanceId = instanceId;
        this.zone = zone;
        this.owner = owner;
        this.currentWave = 1;
        this.isActive = true;
        this.waveInterval = WAVE_INTERVAL;
        this.currentWaveMobs = new ArrayList<>();
        this.waveConfig = new DungeonWaveConfig();
        this.waveCompleted = false;
        this.waveTimeLimit = WAVE_TIME_LIMIT;
        this.totalKillsThisWave = 0;
        this.requiredKillsThisWave = BASE_KILLS_REQUIRED;
        this.lastNotificationTime = 0;
        this.dungeonStarted = false;
    }

    public void update() {
        if (!isActive) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        if (!waveCompleted && dungeonStarted) {
            updateWaveTimer(currentTime);
            if (currentTime - waveStartTime >= waveTimeLimit) {
                failWave();
                return;
            }
        }
    }

    public void startWave() {
        if (!isActive) {
            return;
        }
        try {
            waveCompleted = false;
            clearPreviousMobs();
            currentWaveMobs.clear();
            startWaveCountdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearPreviousMobs() {
        if (zone != null && zone.mobs != null) {
            zone.mobs.removeIf(mob -> mob.name != null && mob.name.contains("Wave"));
        }
    }

    private void startWaveCountdown() {
        Service.gI().sendThongBao(owner,
                "Chuẩn bị cho Wave " + currentWave + "! Bắt đầu trong " + COUNTDOWN_SECONDS + " giây...");
        
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        final int[] timeWait = {COUNTDOWN_SECONDS};
        
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (timeWait[0] > 0) {
                    Service.gI().sendThongBao(owner, "Wave " + currentWave + " bắt đầu trong: " + timeWait[0] + " giây");
                    timeWait[0]--;
                } else {
                    scheduler.shutdown();
                    if (isActive) {
                        requiredKillsThisWave = BASE_KILLS_REQUIRED + (currentWave - 1) * KILLS_INCREASE_PER_WAVE;
                        executeWaveStart();
                    }
                }
            } catch (Exception e) {
                Logger.logException(DungeonInstance.class, e, "Error in wave countdown for player: " + (owner != null ? owner.name : "null"));
                scheduler.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void executeWaveStart() {
        waveStartTime = System.currentTimeMillis();
        lastNotificationTime = 0;
        dungeonStarted = true;

        WaveData waveData = waveConfig.getWaveData(currentWave);
        for (int i = 0; i < waveData.mobCount; i++) {
            spawnMob(waveData, i);
        }
        zone.mapInfo(owner);
        Service.gI().sendThongBao(owner,
                "Wave " + currentWave + " bắt đầu! Tiêu diệt " + requiredKillsThisWave + " quái vật trong 10 phút!");
    }

    private Object[] getSpawnPosition(int mobIndex) {
        int spawnIndex = mobIndex % ENTRY_MOB_SPAWN.length;
        return ENTRY_MOB_SPAWN[spawnIndex];
    }

    private long getSpawnHp(int mobIndex) {
        int spawnIndex = mobIndex % ENTRY_MOB_SPAWN.length;
        return (Long) ENTRY_MOB_SPAWN[spawnIndex][1]; // HP ở vị trí thứ 2
    }

    private long getSpawnDamage(int mobIndex) {
        int spawnIndex = mobIndex % ENTRY_MOB_SPAWN.length;
        return (Long) ENTRY_MOB_SPAWN[spawnIndex][2]; // Damage ở vị trí thứ 3
    }

    private int getSpawnTemplateId(int mobIndex) {
        int spawnIndex = mobIndex % ENTRY_MOB_SPAWN.length;
        return (Integer) ENTRY_MOB_SPAWN[spawnIndex][0];
    }

    private int getSpawnPDame(int mobIndex) {
        int spawnIndex = mobIndex % ENTRY_MOB_SPAWN.length;
        return (Integer) ENTRY_MOB_SPAWN[spawnIndex][5]; // p_dame ở vị trí thứ 6
    }

    private int getSpawnPTiemNang(int mobIndex) {
        int spawnIndex = mobIndex % ENTRY_MOB_SPAWN.length;
        return (Integer) ENTRY_MOB_SPAWN[spawnIndex][6]; // p_tiem_nang ở vị trí thứ 7
    }

    private int getSpawnStatus(int mobIndex) {
        int spawnIndex = mobIndex % ENTRY_MOB_SPAWN.length;
        return (Integer) ENTRY_MOB_SPAWN[spawnIndex][7]; // status ở vị trí thứ 8
    }

    private int getSpawnLastTimeDie(int mobIndex) {
        int spawnIndex = mobIndex % ENTRY_MOB_SPAWN.length;
        return (Integer) ENTRY_MOB_SPAWN[spawnIndex][8]; // last_time_die ở vị trí thứ 9
    }

    private long getSpawnExpReward(int mobIndex) {
        int spawnIndex = mobIndex % ENTRY_MOB_SPAWN.length;
        return (Long) ENTRY_MOB_SPAWN[spawnIndex][9]; // exp_reward ở vị trí thứ 10
    }

    private int getSpawnSpecialChance(int mobIndex) {
        int spawnIndex = mobIndex % ENTRY_MOB_SPAWN.length;
        return (Integer) ENTRY_MOB_SPAWN[spawnIndex][10]; // special_chance ở vị trí thứ 11
    }

    private void spawnMob(WaveData waveData, int mobIndex) {
        int nextMobId = getNextMobId();
        Mob newMob = new Mob();
        newMob.id = nextMobId;
        int templateId = getSpawnTemplateId(mobIndex);
        newMob.tempId = templateId;
        newMob.name = waveData.mobName + " (Wave " + currentWave + ")";
        newMob.level = (byte) waveData.mobLevel;

        long mobHp = getSpawnHp(mobIndex);
        long mobDamage = getSpawnDamage(mobIndex);

        // Tăng sức mạnh theo wave
        long baseHp = (long) (mobHp * Math.pow(WAVE_POWER_MULTIPLIER, currentWave - 1));
        long baseDamage = (long) (mobDamage * Math.pow(WAVE_POWER_MULTIPLIER, currentWave - 1));

        newMob.point.setHpFull(baseHp);
        newMob.point.sethp(baseHp);
        newMob.point.dame = baseDamage;

        Object[] spawnPos = getSpawnPosition(mobIndex);
        newMob.location.x = (Integer) spawnPos[3];
        newMob.location.y = (Integer) spawnPos[4];

        newMob.zone = zone;
        newMob.pDame = (byte) getSpawnPDame(mobIndex);
        newMob.pTiemNang = (byte) getSpawnPTiemNang(mobIndex);
        newMob.status = (byte) getSpawnStatus(mobIndex);
        newMob.lastTimeDie = getSpawnLastTimeDie(mobIndex);
        newMob.setTiemNang();

        zone.mobs.add(newMob);
        currentWaveMobs.add(newMob);
        sendMobSpawnMessage(newMob);
    }

    private void sendMobSpawnMessage(Mob mob) {
        try {
            Message msg = new Message(-13);
            msg.writer().writeByte(mob.id);
            msg.writer().writeByte(mob.tempId);
            msg.writer().writeByte(mob.lvMob);
            msg.writer().writeDouble(Util.limitDouble(mob.point.gethp()));
            Service.gI().sendMessAllPlayerInMap(zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void onMobKilled() {
        totalKillsThisWave++;
        Service.gI().sendThongBao(owner,
                "Tiêu diệt thành công! Tiến độ: " + totalKillsThisWave + "/" + requiredKillsThisWave);
        if (totalKillsThisWave >= requiredKillsThisWave && !waveCompleted) {
            completeWave();
        }
    }

    private void completeWave() {
        waveCompleted = true;
        waveCompletionTime = System.currentTimeMillis();

        giveWaveRewards();

        Service.gI().sendThongBao(owner, "Wave " + currentWave + " hoàn thành! Đã tiêu diệt " + totalKillsThisWave + "/"
                + requiredKillsThisWave + " quái vật. Wave tiếp theo sẽ bắt đầu sau 5 giây...");
        totalKillsThisWave = 0;

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        final int[] timeWait = {COUNTDOWN_SECONDS};
        
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (timeWait[0] > 0) {
                    Service.gI().sendThongBao(owner, "Wave tiếp theo bắt đầu trong: " + timeWait[0] + " giây");
                    timeWait[0]--;
                } else {
                    scheduler.shutdown();
                    if (isActive && owner != null && owner.zone != null) {
                        startNextWave();
                    }
                }
            } catch (Exception e) {
                Logger.logException(DungeonInstance.class, e, "Error in next wave countdown for player: " + (owner != null ? owner.name : "null"));
                scheduler.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void startNextWave() {
        currentWave++;
        waveCompleted = false;
        totalKillsThisWave = 0;
        startWave();
    }

    private void giveWaveRewards() {
        long totalExpReward = 0;
        for (int i = 0; i < ENTRY_MOB_SPAWN.length; i++) {
            totalExpReward += getSpawnExpReward(i) * currentWave;
        }
        Service.gI().addSMTN(owner, (byte) 2, (long) totalExpReward, true);

        if (Util.isTrue(getSpawnSpecialChance(0), 100)) {
            Service.gI().sendThongBao(owner, "Bạn nhận được phần thưởng đặc biệt từ wave " + currentWave + "!");
        }
    }

    private int getNextMobId() {
        int nextMobId = 0;
        for (Mob existingMob : zone.mobs) {
            if (existingMob.id >= nextMobId) {
                nextMobId = existingMob.id + 1;
            }
        }
        return nextMobId;
    }

    private void updateWaveTimer(long currentTime) {
        long timeElapsed = currentTime - waveStartTime;
        long timeRemaining = waveTimeLimit - timeElapsed;
        long minutesRemaining = timeRemaining / 60000;

        if (timeRemaining > 0 && currentTime - lastNotificationTime >= NOTIFICATION_INTERVAL) {
            if (minutesRemaining > 0) {
                Service.gI().sendThongBao(owner, "Còn " + minutesRemaining + " phút để hoàn thành wave " + currentWave
                        + "! Đã tiêu diệt: " + totalKillsThisWave + "/" + requiredKillsThisWave);
                lastNotificationTime = currentTime;
            }
        }
    }

    private void failWave() {
        isActive = false;
        Service.gI().sendThongBao(owner,
                "Thất bại! Không hoàn thành wave " + currentWave + " trong thời gian quy định. Chỉ tiêu diệt được "
                + totalKillsThisWave + "/" + requiredKillsThisWave + " quái vật.");
        Dragon.thuongnhanthanbi.Dungeon_Manager.gI().penalizePlayerForFailure(owner);
        kickPlayerFromDungeon();
    }

    private void kickPlayerFromDungeon() {
        try {
            Service.gI().sendThongBao(owner, "Bạn sẽ được đưa ra khỏi dungeon sau " + KICK_DELAY_SECONDS + " giây...");
            if (owner.zone != null) {
                owner.zone.mobs.removeIf(mob -> mob.name != null && mob.name.contains("Wave"));
            }

            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            
            scheduler.schedule(() -> {
                try {
                    if (owner != null) {
                        Dragon.thuongnhanthanbi.Dungeon_Manager.gI().removePlayerCompletely(owner);

                        Dragon.services.func.ChangeMapService.gI().changeMapBySpaceShip(owner, KICK_MAP_ID,
                                KICK_ZONE_ID, KICK_X);

                        Service.gI().sendThongBao(owner, "Bạn đã bị đưa ra khỏi dungeon do thất bại!");
                    }
                    scheduler.shutdown();
                } catch (Exception e) {
                    Logger.logException(DungeonInstance.class, e, "Error kicking player from dungeon");
                }
            }, KICK_DELAY_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("Error in kickPlayerFromDungeon: " + e.getMessage());
        }
    }

    public String getInstanceId() {
        return instanceId;
    }

    public Zone getZone() {
        return zone;
    }

    public Player getOwner() {
        return owner;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public List<Mob> getCurrentWaveMobs() {
        return currentWaveMobs;
    }

    public int getRequiredKillsThisWave() {
        return requiredKillsThisWave;
    }

    public int getTotalKillsThisWave() {
        return totalKillsThisWave;
    }

    public boolean isDungeonStarted() {
        return dungeonStarted;
    }

    public boolean isMobFromThisInstance(int mobId) {
        if (zone != null && zone.mobs != null) {
            for (Mob mob : zone.mobs) {
                if (mob.id == mobId && mob.name != null && mob.name.contains("Wave")) {
                    return true;
                }
            }
        }
        return false;
    }

    private static class DungeonWaveConfig {

        public WaveData getWaveData(int wave) {
            WaveData data = new WaveData();
            data.mobCount = ENTRY_MOB_SPAWN.length;
            data.mobName = "Quái Địa Cung";
            data.mobLevel = BASE_MOB_LEVEL + (wave - 1) * MOB_LEVEL_INCREASE_PER_WAVE;
            return data;
        }
    }

    private static class WaveData {

        int mobCount;
        String mobName;
        int mobLevel;
        long baseHp;
        long baseDamage;
    }
}
