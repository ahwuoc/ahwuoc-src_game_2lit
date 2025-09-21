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
    private int type;
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
     * Tìm map tốt nhất có nhiều quái để bot có thể săn lùng
     * @return mapId của map có nhiều quái nhất, -1 nếu không tìm thấy
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
        
        int bestMapId = -1;
        int maxMobCount = 0;
        
        // Tìm map có nhiều quái sống nhất
        for (int mapId : huntingMaps) {
            int mobCount = countAliveMobsInMap(mapId);
            if (mobCount > maxMobCount) {
                maxMobCount = mobCount;
                bestMapId = mapId;
            }
        }
        
        // Chỉ chọn map nếu có ít nhất 3 con quái
        return maxMobCount >= 3 ? bestMapId : -1;
    }
    
    /**
     * Đếm số lượng quái còn sống trong map
     * @param mapId ID của map cần kiểm tra
     * @return số lượng quái còn sống
     */
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
