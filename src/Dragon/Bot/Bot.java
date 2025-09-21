package Dragon.Bot;

import Dragon.services.EffectSkillService;
import Dragon.services.Service;
import Dragon.consts.ConstPlayer;
import Dragon.models.skill.NClass;
import Dragon.models.skill.Skill;
import Dragon.models.Template.SkillTemplate;
import Dragon.services.SkillService;
import Dragon.server.*;
import Dragon.models.map.Map;
import Dragon.models.map.Zone;
import Dragon.services.func.ChangeMapService;
import Dragon.services.PlayerService;
import Dragon.services.MapService;
import Dragon.models.item.Item;
import Dragon.utils.Util;
import Dragon.models.player.*;
import java.util.Random;

public class Bot extends Player {

    private static final Random RANDOM = new Random(); // Static Random để tránh tạo mới liên tục

    private short head_;
    private short body_;
    private short leg_;
    public int type;
    private int index_ = 0;
    public ShopBot shop;
    public Sanb boss;
    public Mobb mo1;

    private Player plAttack;

    private int[] TraiDat = new int[]{1, 2, 3, 4, 6, 29, 30, 28, 27, 42};
    private int[] Namec = new int[]{8, 9, 10, 11, 12, 13, 33, 34, 32, 31};
    private int[] XayDa = new int[]{15, 16, 17, 18, 19, 20, 37, 36, 35, 44, 52};

    public Bot(short head, short body, short leg, int type, String name, ShopBot shop, short flag) {
        this.head_ = head;
        this.body_ = body;
        this.leg_ = leg;
        this.shop = shop;
        this.name = name;
        this.id = RANDOM.nextInt(2000000000);
        this.type = type;
        this.isBot = true;
    }

    public int MapToPow() {
        // Tìm map có nhiều quái nhất dựa trên sức mạnh
        int bestMapId = findBestMapWithMobs();
        if (bestMapId != -1) {
            return bestMapId;
        }
        
        // Fallback về logic cũ nếu không tìm được map phù hợp
        int mapId = 21;
        if (this.nPoint.power < 2000000000) {
            if (this.gender == 0 || this.gender == 1 || this.gender == 2) {
                mapId = (RANDOM.nextBoolean()) ? 2 : 3;
            }
        }
        return mapId;
    }
    
    /**
     * Tìm map tốt nhất có nhiều quái để bot có thể săn lùng với load balancing
     * @return mapId của map phù hợp nhất, -1 nếu không tìm thấy
     */
    private int findBestMapWithMobs() {
        int[] huntingMaps;
        
        // Chọn danh sách map dựa trên sức mạnh
        if (this.nPoint.power < 500000) {
            huntingMaps = TraiDat; // Map Trái Đất cho newbie
        } else if (this.nPoint.power < 50000000) {
            huntingMaps = Namec; // Map Namec cho mid-level
        } else {
            huntingMaps = XayDa; // Map Xayda cho high-level
        }
        
        return findBalancedMap(huntingMaps);
    }
    
    /**
     * Tìm map cân bằng tải để tránh tập trung quá nhiều bot
     * @param mapIds danh sách map cần kiểm tra
     * @return mapId phù hợp nhất
     */
    private int findBalancedMap(int[] mapIds) {
        // Shuffle map list để tránh bias
        int[] shuffledMaps = mapIds.clone();
        for (int i = shuffledMaps.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            int temp = shuffledMaps[i];
            shuffledMaps[i] = shuffledMaps[j];
            shuffledMaps[j] = temp;
        }
        
        int bestMapId = -1;
        double bestScore = -1;
        
        // Kiểm tra tất cả map và chọn map tốt nhất
        for (int mapId : shuffledMaps) {
            double score = calculateMapScore(mapId);
            if (score > bestScore) {
                bestScore = score;
                bestMapId = mapId;
            }
        }
        
        // Nếu không có map nào đủ tốt, chọn map ít đông nhất
        if (bestScore <= 0) {
            bestMapId = findLeastCrowdedMap(shuffledMaps);
        }
        
        return bestMapId;
    }
    
    /**
     * Tìm map ít đông nhất khi tất cả map đều quá tải
     * @param mapIds danh sách map cần kiểm tra
     * @return mapId của map ít bot nhất
     */
    private int findLeastCrowdedMap(int[] mapIds) {
        int leastCrowdedMap = mapIds[0];
        int minBotCount = Integer.MAX_VALUE;
        
        for (int mapId : mapIds) {
            int botCount = countBotsInMap(mapId);
            if (botCount < minBotCount) {
                minBotCount = botCount;
                leastCrowdedMap = mapId;
            }
        }
        
        return leastCrowdedMap;
    }
    
    /**
     * Đếm số bot trong map
     * @param mapId ID của map
     * @return số lượng bot
     */
    private int countBotsInMap(int mapId) {
        try {
            Map map = MapService.gI().getMapById(mapId);
            if (map == null || map.zones.isEmpty()) {
                return 0;
            }
            
            int totalBots = 0;
            for (Zone zone : map.zones) {
                if (zone.getHumanoids() != null) {
                    for (Player p : zone.getHumanoids()) {
                        if (p.isBot) {
                            totalBots++;
                        }
                    }
                }
            }
            return totalBots;
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Tính điểm cho map dựa trên số quái và mật độ bot
     * @param mapId ID của map
     * @return điểm từ 0-100, càng cao càng tốt
     */
    private double calculateMapScore(int mapId) {
        try {
            Map map = MapService.gI().getMapById(mapId);
            if (map == null || map.zones.isEmpty()) {
                return 0;
            }
            
            int totalMobs = 0;
            int totalBots = 0;
            
            // Đếm tổng số quái, bot và player trong tất cả zone
            for (Zone zone : map.zones) {
                if (zone.mobs != null) {
                    for (Dragon.models.mob.Mob mob : zone.mobs) {
                        if (mob.point.hp > 0 && mob.status != 0) {
                            totalMobs++;
                        }
                    }
                }
                
                if (zone.getHumanoids() != null) {
                    for (Player p : zone.getHumanoids()) {
                        if (p.isBot) {
                            totalBots++;
                        }
                    }
                }
            }
            
            // Không chọn map không có quái
            if (totalMobs < 3) {
                return 0;
            }
            
            // Tính tỷ lệ quái/bot (lý tưởng là 2-3 quái/bot)
            double mobToBotRatio = totalBots > 0 ? (double) totalMobs / totalBots : totalMobs;
            
            // Điểm cơ bản dựa trên số quái
            double mobScore = Math.min(totalMobs / 10.0, 10); // Max 10 điểm
            
            // Penalty nếu quá nhiều bot (overcrowding)
            double crowdingPenalty = 0;
            if (totalBots > totalMobs * 0.8) { // Nếu bot > 80% số quái
                crowdingPenalty = (totalBots - totalMobs * 0.8) / totalMobs * 5;
            }
            
            // Bonus nếu tỷ lệ quái/bot tốt (2-4 quái/bot)
            double ratioBonus = 0;
            if (mobToBotRatio >= 2 && mobToBotRatio <= 4) {
                ratioBonus = 3;
            } else if (mobToBotRatio >= 1.5 && mobToBotRatio <= 6) {
                ratioBonus = 1;
            }
            
            // Random factor để tránh tất cả bot chọn cùng map
            double randomFactor = RANDOM.nextDouble() * 2; // 0-2 điểm random
            
            // Thêm penalty mạnh hơn cho map quá đông (cho 10000+ bot)
            double heavyCrowdingPenalty = 0;
            if (totalBots > 50) { // Nếu quá 50 bot trong map
                heavyCrowdingPenalty = Math.pow(totalBots - 50, 1.5) / 10;
            }
            
            double finalScore = mobScore + ratioBonus - crowdingPenalty - heavyCrowdingPenalty + randomFactor;
            return Math.max(0, finalScore);
            
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Đếm số lượng quái còn sống trong map (giữ lại cho tương thích)
     * @param mapId ID của map cần kiểm tra
     * @return số lượng quái còn sống
     */
    @SuppressWarnings("unused")
    private int countAliveMobsInMap(int mapId) {
        try {
            Map map = MapService.gI().getMapById(mapId);
            if (map == null || map.zones.isEmpty()) {
                return 0;
            }
            
            int totalAliveMobs = 0;
            for (Zone zone : map.zones) {
                if (zone.mobs != null) {
                    for (Dragon.models.mob.Mob mob : zone.mobs) {
                        // Kiểm tra quái còn sống (status != 0 là chết)
                        if (mob.point.hp > 0 && mob.status != 0) {
                            totalAliveMobs++;
                        }
                    }
                }
            }
            return totalAliveMobs;
        } catch (Exception e) {
            return 0;
        }
    }

    public void joinMap() {
        Zone zone = getRandomZone(MapToPow());
        if (zone != null) {
            ChangeMapService.gI().goToMap(this, zone);
            this.zone.load_Me_To_Another(this);
            this.mo1.lastTimeChanM = System.currentTimeMillis();
        }
    }
    
    /**
     * Kiểm tra xem bot có nên chuyển map không (khi quái chết hết)
     * @return true nếu nên chuyển map
     */
    public boolean shouldChangeMap() {
        if (this.zone == null) {
            return true;
        }
        
        // Kiểm tra số quái còn sống trong zone hiện tại
        int aliveMobsInCurrentZone = 0;
        if (this.zone.mobs != null) {
            for (Dragon.models.mob.Mob mob : this.zone.mobs) {
                if (mob.point.hp > 0 && mob.status != 0) {
                    aliveMobsInCurrentZone++;
                }
            }
        }
        
        // Chuyển map nếu ít hơn 2 con quái còn sống
        return aliveMobsInCurrentZone < 2;
    }
    
    /**
     * Tự động chuyển map khi cần thiết
     */
    public void autoChangeMapIfNeeded() {
        if (shouldChangeMap()) {
            // Tìm map mới có nhiều quái
            int newMapId = MapToPow();
            if (newMapId != this.zone.map.mapId) {
                Zone newZone = getRandomZone(newMapId);
                if (newZone != null) {
                    ChangeMapService.gI().goToMap(this, newZone);
                    this.zone.load_Me_To_Another(this);
                    this.mo1.lastTimeChanM = System.currentTimeMillis();
                }
            }
        }
    }

    public Zone getRandomZone(int mapId) {
        Map map = MapService.gI().getMapById(mapId);
        Zone zone = null;
        try {
            if (map != null) {
                zone = map.zones.stream()
                        .filter(z -> z.getNumOfPlayers() == 0)
                        .findFirst()
                        .orElseGet(() -> {
                            Zone randomZone = map.zones.get(Util.nextInt(0, map.zones.size() - 1));
                            return randomZone.isFullPlayer() ? null : randomZone;
                        });
            }
        } catch (Exception ignored) {
        }
        
        if (zone != null) {
            this.index_ = 0;
            return zone;
        } else {
            this.index_ += 1;
            if (this.index_ >= 20) {
                BotManager.gI().bot.remove(this);
                ChangeMapService.gI().exitMap(this);
                return null;
            } else {
                // Tránh đệ quy vô hạn bằng cách thử map khác
                int[] alternativeMaps = {21, 1, 2, 3};
                for (int altMapId : alternativeMaps) {
                    if (altMapId != mapId) {
                        Zone altZone = getRandomZoneNonRecursive(altMapId);
                        if (altZone != null) {
                            return altZone;
                        }
                    }
                }
                return null;
            }
        }
    }
    
    private Zone getRandomZoneNonRecursive(int mapId) {
        Map map = MapService.gI().getMapById(mapId);
        if (map != null && !map.zones.isEmpty()) {
            return map.zones.get(RANDOM.nextInt(map.zones.size()));
        }
        return null;
    }

    @Override
    public short getHead() {
        if (effectSkill.isMonkey) {
            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
        } else {
            return this.head_;
        }
    }

    @Override
    public short getBody() {
        if (effectSkill.isMonkey) {
            return 193;
        } else {
            return this.body_;
        }
    }

    @Override
    public short getLeg() {
        if (effectSkill.isMonkey) {
            return 194;
        } else {
            return this.leg_;
        }
    }

    @Override
    public void update() {
        super.update();
        this.increasePoint();
        
        // Tự động chuyển map nếu cần (chỉ cho bot quái - type 0)
        if (this.type == 0) {
            autoChangeMapIfNeeded();
        }
        
        switch (this.type) {
            case 0:
                this.mo1.update();
                break;
            case 1:
                this.shop.update();
                break;
            case 2:
                this.boss.update();
                break;
        }
        if (this.isDie()) {
            Service.gI().hsChar(this, nPoint.hpMax, nPoint.mpMax);
        }
    }

    public void leakSkill() {
        for (NClass n : Manager.gI().NCLASS) {
            if (n.classId == this.gender) {
                for (SkillTemplate Template : n.skillTemplatess) {
                    for (Skill skills : Template.skillss) {
                        Skill cloneSkill = new Skill(skills);
                        this.playerSkill.skills.add(cloneSkill);
                        break;
                    }
                }
                break;
            }
        }
    }

    public boolean UseLastTimeSkill() {
        if (this.playerSkill.skillSelect.lastTimeUseThisSkillbot < (System.currentTimeMillis() - this.playerSkill.skillSelect.coolDown)) {
            this.playerSkill.skillSelect.lastTimeUseThisSkillbot = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    private void increasePoint() {
        long tiemNangUse = 0;
        int point = 0;
        if (this.nPoint != null) {
            if (Util.isTrue(50, 100)) {
                point = 100;
                int pointHp = point * 20;
                tiemNangUse = (long) (point * (2 * (this.nPoint.hpg + 1000) + pointHp - 20) / 2);
                if (doUseTiemNang(tiemNangUse)) {
                    this.nPoint.hpMax += point;
                    this.nPoint.hpg += point;
                    Service.gI().point(this);
                }
            } else {
                point = 10;
                tiemNangUse = (long) (point * (2 * this.nPoint.dameg + point - 1) / 2 * 100);
                if (doUseTiemNang(tiemNangUse)) {
                    this.nPoint.dameg += point;
                    Service.gI().point(this);
                }
            }
        }
    }

    private boolean doUseTiemNang(long tiemNang) {
        if (this.nPoint.tiemNang < tiemNang) {
            return false;
        } else {
            this.nPoint.tiemNang -= tiemNang;
            return true;
        }
    }

    public void useSkill(int skillId) {
        new Thread(() -> {
            switch (skillId) {
                case Skill.BIEN_KHI:
                    EffectSkillService.gI().sendEffectMonkey(this);
                    EffectSkillService.gI().setIsMonkey(this);
                    EffectSkillService.gI().sendEffectMonkey(this);

                    Service.getInstance().sendSpeedPlayer(this, 0);
                    Service.getInstance().Send_Caitrang(this);
                    Service.getInstance().sendSpeedPlayer(this, -1);
                    PlayerService.gI().sendInfoHpMp(this);
                    Service.getInstance().point(this);
                    Service.getInstance().Send_Info_NV(this);
                    Service.getInstance().sendInfoPlayerEatPea(this);
                    break;
                case Skill.QUA_CAU_KENH_KHI:
                    this.playerSkill.prepareQCKK = !this.playerSkill.prepareQCKK;
                    this.playerSkill.lastTimePrepareQCKK = System.currentTimeMillis();
                    SkillService.gI().sendPlayerPrepareSkill(this, 1000);
                    break;
                case Skill.MAKANKOSAPPO:
                    this.playerSkill.prepareLaze = !this.playerSkill.prepareLaze;
                    this.playerSkill.lastTimePrepareLaze = System.currentTimeMillis();
                    SkillService.gI().sendPlayerPrepareSkill(this, 3000);
                    break;
            }
        }).start();
    }

}
