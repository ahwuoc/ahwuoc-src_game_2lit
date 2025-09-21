package Dragon.services;

import Dragon.consts.ConstPlayer;
import Dragon.models.boss.Boss;
import Dragon.models.boss.BossID;
import Dragon.models.intrinsic.Intrinsic;
import Dragon.models.mob.Mob;
import Dragon.models.mob.MobMe;
import Dragon.models.player.Pet;
import Dragon.models.player.Player;
import Dragon.models.player.SkillSpecial;
import Dragon.models.player.TestDame;
import Dragon.models.skill.Skill;
import com.girlkun.network.io.Message;
import Dragon.services.func.ChangeMapService;
import Dragon.utils.Logger;
import Dragon.utils.SkillUtil;
import Dragon.utils.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import Dragon.De2.Thu_TrieuHoi;
import Dragon.jdbc.daos.PlayerDAO;
import Dragon.models.player.Clone;

public class SkillService {

    private static SkillService i;

    private SkillService() {

    }

    public static SkillService gI() {
        if (i == null) {
            i = new SkillService();
        }
        return i;
    }

    public boolean useSkill(Player player, Player plTarget, Mob mobTarget, Message message) {
        if (player.effectSkill.isHaveEffectSkill()) {
            return false;
        }
        if (player.playerSkill == null) {
            return false;
        }
        if (player.playerSkill.skillSelect.template.type == 2 && canUseSkillWithMana(player)
                && canUseSkillWithCooldown(player)) {
            useSkillBuffToPlayer(player, plTarget);
            return true;
        }
        if ((player.effectSkill.isHaveEffectSkill()
                && (player.playerSkill.skillSelect.template.id != Skill.TU_SAT
                && player.playerSkill.skillSelect.template.id != Skill.QUA_CAU_KENH_KHI
                && player.playerSkill.skillSelect.template.id != Skill.MAKANKOSAPPO))
                || (plTarget != null && !canAttackPlayer(player, plTarget))
                || (mobTarget != null && mobTarget.isDie())
                || !canUseSkillWithMana(player) || !canUseSkillWithCooldown(player)) {
            return false;
        }
        if (player.effectSkill.useTroi) {
            EffectSkillService.gI().removeUseTroi(player);
        }
        if (player.effectSkill.isCharging) {
            EffectSkillService.gI().stopCharge(player);
        }
        byte st = -1;
        byte skillId = -1;
        Short dx = -1;
        Short dy = -1;
        byte dir = -1;
        Short x = -1;
        Short y = -1;

        try {
            st = message.reader().readByte();
            skillId = message.reader().readByte();
            dx = message.reader().readShort();
            dy = message.reader().readShort();
            dir = message.reader().readByte();
            x = message.reader().readShort();
            y = message.reader().readShort();
        } catch (Exception e) {

        }
        if (st == 20 && skillId != player.playerSkill.skillSelect.template.id) {
            selectSkill(player, skillId);
            return false;
        }
        // Handle all active clones
        for (Clone clone : player.clones) {
            if (clone != null && clone.zone != null) {
                clone.playerSkill.skillSelect = player.playerSkill.skillSelect;
                switch (player.playerSkill.skillSelect.template.type) {
                    case 1:
                        clone.attackWithMaster(plTarget, mobTarget);
                        break;
                    case 2:
                        useSkillBuffToPlayer(clone, plTarget);
                        break;
                    case 3:
                        useSkillAlone(clone);
                        break;
                    case 4:
                        userSkillSpecial(clone, st, skillId, dx, dy, dir, x, y);
                        break;
                    default:
                        return false;
                }
            }
        }

        // Keep backward compatibility with single clone
        if (player.clone != null && player.clone.zone != null) {
            player.clone.playerSkill.skillSelect = player.playerSkill.skillSelect;
            switch (player.playerSkill.skillSelect.template.type) {
                case 1:
                    player.clone.attackWithMaster(plTarget, mobTarget);
                    break;
                case 2:
                    useSkillBuffToPlayer(player.clone, plTarget);
                    break;
                case 3:
                    useSkillAlone(player.clone);
                    break;
                case 4:
                    userSkillSpecial(player.clone, st, skillId, dx, dy, dir, x, y);
                    break;
                default:
                    return false;
            }
        }
        switch (player.playerSkill.skillSelect.template.type) {
            case 1:
                useSkillAttack(player, plTarget, mobTarget);
                break;
            case 2:
                useSkillBuffToPlayer(player, plTarget);
                break;
            case 3:
                useSkillAlone(player);
                break;
            case 4:
                userSkillSpecial(player, st, skillId, dx, dy, dir, x, y);
                break;
            default:
                return false;
        }
        return true;
    }

    public static void setEffectFusion(Player pl, int type) {
        Message msg;
        try {
            msg = new Message(125);
            msg.writer().writeByte(type);
            msg.writer().writeInt((int) pl.id);
            Service.getInstance().sendMessAllPlayerInMap(pl, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void startSkillClone(Player player) {
        if (!canUseSkillWithCooldown(player)) {
            return;
        }

        // Get skill level to determine number of clones
        Skill phanThanSkill = player.playerSkill.getSkillbyId(Skill.PHAN_THAN);
        if (phanThanSkill == null || phanThanSkill.skillId == -1) {
            Service.getInstance().sendThongBao(player, "Bạn chưa học skill Phân Thân!");
            return;
        }

        int skillLevel = phanThanSkill.point;
        int maxClones = Math.min(skillLevel, 5); // Maximum 5 clones for balance

        // Check if player already has maximum clones
        int activeClones = 0;
        for (Clone clone : player.clones) {
            if (clone != null && clone.zone != null) {
                activeClones++;
            }
        }

        if (activeClones >= maxClones) {
            Service.getInstance().sendThongBao(player, "Đã tạo tối đa " + maxClones + " phân thân!");
            return;
        }

        // Create new clone
        setEffectFusion(player, 0);
        Clone.callPoint(player);

        Service.getInstance().sendThongBao(player,
                "Tạo thành công phân thân! (" + (activeClones + 1) + "/" + maxClones + ")");
    }

    // Method to show clone status
    public void showCloneStatus(Player player) {
        Skill phanThanSkill = player.playerSkill.getSkillbyId(Skill.PHAN_THAN);
        if (phanThanSkill == null || phanThanSkill.skillId == -1) {
            Service.getInstance().sendThongBao(player, "Bạn chưa học skill Phân Thân!");
            return;
        }

        int skillLevel = phanThanSkill.point;
        int maxClones = Math.min(skillLevel, 5);
        int activeClones = 0;

        for (Clone clone : player.clones) {
            if (clone != null && clone.zone != null) {
                activeClones++;
            }
        }

        Service.getInstance().sendThongBao(player,
                "Phân Thân: " + activeClones + "/" + maxClones + " (Level " + skillLevel + ")");
    }

    public void userSkillSpecial(Player player, byte st, byte skillId, short dx, short dy, byte dir, short x, short y) {
        switch (skillId) {
            case Skill.SUPER_KAME:
                if (player.inventory.itemsBody.get(12).isNotNullItem()) {
                    for (int i = 0; i < player.inventory.itemsBody.get(12).itemOptions.size(); i++) {
                        if (player.inventory.itemsBody.get(12).itemOptions.get(i).optionTemplate.id == 219) {
                            if (player.inventory.itemsBody.get(12).itemOptions.get(i).param == 0) {
                                Service.getInstance().sendThongBao(player,
                                        "Phục hồi sách hoặc tháo sách ra để dùng skill");
                                return;
                            }
                        }
                    }
                    switch (player.inventory.itemsBody.get(12).template.id) {
                        case 1383:
                            Service.getInstance().SendImgSkill9(skillId, 2);// gửi ảnh tới cilent
                            sendEffSkillSpecialID24(player, dir, 2);
                            break;
                        case 1384:
                            Service.getInstance().SendImgSkill9(skillId, 3);// gửi ảnh tới cilent
                            sendEffSkillSpecialID24(player, dir, 3);
                            break;
                        default:
                            sendEffSkillSpecialID24(player, dir, 0);
                            break;
                    }
                } else {
                    sendEffSkillSpecialID24(player, dir, 0);
                }
                break;
            case Skill.MA_PHONG_BA:
                if (player.inventory.itemsBody.get(12).isNotNullItem()) {
                    for (int i = 0; i < player.inventory.itemsBody.get(12).itemOptions.size(); i++) {
                        if (player.inventory.itemsBody.get(12).itemOptions.get(i).optionTemplate.id == 219) {
                            if (player.inventory.itemsBody.get(12).itemOptions.get(i).param == 0) {
                                Service.getInstance().sendThongBao(player,
                                        "Phục hồi sách hoặc tháo sách ra để dùng skill");
                                return;
                            }
                        }
                    }
                    switch (player.inventory.itemsBody.get(12).template.id) {
                        case 1385:
                            Service.getInstance().SendImgSkill9(skillId, 2);// gửi ảnh tới cilent
                            sendEffSkillSpecialID26(player, dir, 2);
                            break;
                        case 1386:
                            Service.getInstance().SendImgSkill9(skillId, 3);// gửi ảnh tới cilent
                            sendEffSkillSpecialID26(player, dir, 3);
                            break;
                        default:
                            sendEffSkillSpecialID26(player, dir, 0);
                            break;
                    }
                } else {
                    sendEffSkillSpecialID26(player, dir, 0);
                }
                break;
            case Skill.LIEN_HOAN_CHUONG:
                if (player.inventory.itemsBody.get(12).isNotNullItem()) {
                    for (int i = 0; i < player.inventory.itemsBody.get(12).itemOptions.size(); i++) {
                        if (player.inventory.itemsBody.get(12).itemOptions.get(i).optionTemplate.id == 219) {
                            if (player.inventory.itemsBody.get(12).itemOptions.get(i).param == 0) {
                                Service.getInstance().sendThongBao(player,
                                        "Phục hồi sách hoặc tháo sách ra để dùng skill");
                                return;
                            }
                        }
                    }
                    switch (player.inventory.itemsBody.get(12).template.id) {
                        case 1387:
                            Service.getInstance().SendImgSkill9(skillId, 2);// gửi ảnh tới cilent
                            sendEffSkillSpecialID25(player, dir, 2);
                            break;
                        case 1388:
                            Service.getInstance().SendImgSkill9(skillId, 3);// gửi ảnh tới cilent
                            sendEffSkillSpecialID25(player, dir, 3);
                            break;
                        default:
                            sendEffSkillSpecialID25(player, dir, 0);
                            break;
                    }
                } else {
                    sendEffSkillSpecialID25(player, dir, 0);
                }
                break;
        }
        affterUseSkill(player, player.playerSkill.skillSelect.template.id);
        player.skillSpecial.setSkillSpecial(dir, dx, dy, x, y);

    }

    public void updateSkillSpecial(Player player) {
        try {
            if (player.isDie() || player.effectSkill.isHaveEffectSkill()) {
                player.skillSpecial.closeSkillSpecial();
                return;
            }
            if (player.skillSpecial.skillSpecial.template.id == Skill.MA_PHONG_BA) {
                if (Util.canDoWithTime(player.skillSpecial.lastTimeSkillSpecial, SkillSpecial.TIME_GONG)) {
                    player.skillSpecial.lastTimeSkillSpecial = System.currentTimeMillis();
                    player.skillSpecial.closeSkillSpecial();
                    Skill curSkill = SkillUtil.getSkillbyId(player, Skill.MA_PHONG_BA);
                    int timeBinh = SkillUtil.getTimeBinh(curSkill.point);// thời gian biến thành bình

                    // hút người
                    for (Player playerMap : player.zone.getHumanoids()) {
                        if (playerMap != null && playerMap != player) {
                            if (player.skillSpecial.dir == -1 && !playerMap.isDie()
                                    && Util.getDistance(player, playerMap) <= 500
                                    && this.canAttackPlayer(player, playerMap)) {
                                player.skillSpecial.playersTaget.add(playerMap);
                            } else if (player.skillSpecial.dir == 1 && !playerMap.isDie()
                                    && Util.getDistance(player, playerMap) <= 500
                                    && this.canAttackPlayer(player, playerMap)) {
                                player.skillSpecial.playersTaget.add(playerMap);
                            }
                        }
                    }

                    // hút quái
                    for (Mob mobMap : player.zone.mobs) {
                        if (player.skillSpecial.dir == -1 && !mobMap.isDie()
                                && Util.getDistance(player, mobMap) <= 500) {
                            player.skillSpecial.mobsTaget.add(mobMap);
                        } else if (player.skillSpecial.dir == 1 && !mobMap.isDie()
                                && Util.getDistance(player, mobMap) <= 500) {
                            player.skillSpecial.mobsTaget.add(mobMap);
                        }
                        if (mobMap == null) {
                            continue;
                        }
                    }

                    // bắt đầu hút
                    if (player.inventory.itemsBody.get(12).isNotNullItem()) {
                        for (int i = 0; i < player.inventory.itemsBody.get(12).itemOptions.size(); i++) {
                            if (player.inventory.itemsBody.get(12).itemOptions.get(i).optionTemplate.id == 219) {
                                player.inventory.itemsBody.get(12).itemOptions.get(i).param -= 1;
                                InventoryServiceNew.gI().sendItemBody(player);
                            }
                        }
                        switch (player.inventory.itemsBody.get(12).template.id) {
                            case 1385:
                                this.startSkillSpecialID26(player, 2);
                                break;
                            case 1386:
                                this.startSkillSpecialID26(player, 3);
                                break;
                            default:
                                this.startSkillSpecialID26(player, 0);
                                break;
                        }
                    } else {
                        this.startSkillSpecialID26(player, 0);
                    }

                    // biến quái - bình
                    for (Mob mobMap : player.zone.mobs) {
                        if (!MapService.gI().isMapOffline(player.zone.map.mapId)) {
                            if (player.skillSpecial.dir == -1 && !mobMap.isDie()
                                    && Util.getDistance(player, mobMap) <= 500) {
                                player.skillSpecial.mobsTaget.add(mobMap);
                            } else if (player.skillSpecial.dir == 1 && !mobMap.isDie()
                                    && Util.getDistance(player, mobMap) <= 500) {
                                player.skillSpecial.mobsTaget.add(mobMap);
                            }
                            if (mobMap == null) {
                                continue;
                            }
                            EffectSkillService.gI().sendMobToMaPhongBa(player, mobMap, timeBinh);// biến mob thành bình
                            this.playerAttackMob(player, mobMap, false, true); // trừ dame
                        }
                    }

                    // biến người - bình
                    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

                    for (Player playerMap : player.zone.getHumanoids()) {
                        if (!MapService.gI().isMapOffline(player.zone.map.mapId)) {
                            if (playerMap != null && playerMap != player) {
                                if (player.skillSpecial != null && !playerMap.isDie()
                                        && Util.getDistance(player, playerMap) <= 500
                                        && this.canAttackPlayer(player, playerMap)) {
                                    player.skillSpecial.playersTaget.add(playerMap);

                                    if (playerMap != null && playerMap.id != player.id) {
                                        ItemTimeService.gI().sendItemTime(playerMap, 4133, timeBinh / 1000);
                                        EffectSkillService.gI().setMaPhongBa(playerMap, System.currentTimeMillis(),
                                                timeBinh);
                                        Service.getInstance().Send_Caitrang(playerMap);

                                        double ptdame = 0;

                                        switch (curSkill.point) {
                                            case 1:
                                            case 2:
                                                ptdame = 0.01;
                                                break;
                                            case 3:
                                            case 4:
                                                ptdame = 0.02;
                                                break;
                                            case 5:
                                            case 6:
                                                ptdame = 0.03;
                                                break;
                                            case 7:
                                            case 8:
                                                ptdame = 0.04;
                                                break;
                                            case 9:
                                                ptdame = 0.06;
                                                break;
                                            default:
                                                ptdame = 0.01;
                                                break;
                                        }

                                        int dameHit = (int) (player.nPoint.hpMax * ptdame);

                                        for (int i = 0; i < timeBinh / 1000; i++) {
                                            final int index = i;
                                            executorService.schedule(() -> {
                                                if (!playerMap.isDie()) {
                                                    PlayerService.gI().subHPPlayer(playerMap, dameHit);
                                                    PlayerService.gI().sendInfoHpMpMoney(playerMap); // Gửi thông tin HP
                                                    // cho người chơi
                                                    // bị nhốt
                                                    Service.getInstance().Send_Info_NV(playerMap);
                                                }
                                            }, index, TimeUnit.SECONDS);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (playerMap != null && playerMap != player) {
                                if (playerMap.id == -player.id) {
                                    if (player.skillSpecial != null && !playerMap.isDie()
                                            && Util.getDistance(player, playerMap) <= 500
                                            && this.canAttackPlayer(player, playerMap)) {
                                        player.skillSpecial.playersTaget.add(playerMap);

                                        if (playerMap != null && playerMap.id != player.id) {
                                            ItemTimeService.gI().sendItemTime(playerMap, 4133, timeBinh / 1000);
                                            EffectSkillService.gI().setMaPhongBa(playerMap, System.currentTimeMillis(),
                                                    timeBinh);
                                            Service.getInstance().Send_Caitrang(playerMap);

                                            double ptdame = 0;

                                            switch (curSkill.point) {
                                                case 1:
                                                case 2:
                                                    ptdame = 0.01;
                                                    break;
                                                case 3:
                                                case 4:
                                                    ptdame = 0.02;
                                                    break;
                                                case 5:
                                                case 6:
                                                    ptdame = 0.03;
                                                    break;
                                                case 7:
                                                case 8:
                                                    ptdame = 0.04;
                                                    break;
                                                case 9:
                                                    ptdame = 0.06;
                                                    break;
                                                default:
                                                    ptdame = 0.01;
                                                    break;
                                            }

                                            int dameHit = (int) (player.nPoint.hpMax * ptdame);

                                            for (int i = 0; i < timeBinh / 1000; i++) {
                                                final int index = i;
                                                executorService.schedule(() -> {
                                                    if (!playerMap.isDie()) {
                                                        PlayerService.gI().subHPPlayer(playerMap, dameHit);
                                                        PlayerService.gI().sendInfoHpMpMoney(playerMap); // Gửi thông
                                                        // tin HP cho
                                                        // người chơi
                                                        // bị nhốt
                                                        Service.getInstance().Send_Info_NV(playerMap);
                                                    }
                                                }, index, TimeUnit.SECONDS);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Sau khi hoàn thành tất cả các tác vụ, hủy bỏ ScheduledExecutorService
                    executorService.shutdown();
                }
            } else {
                // SUPER KAME
                if (player.skillSpecial.stepSkillSpecial == 0
                        && Util.canDoWithTime(player.skillSpecial.lastTimeSkillSpecial, SkillSpecial.TIME_GONG)) {
                    player.skillSpecial.lastTimeSkillSpecial = System.currentTimeMillis();
                    player.skillSpecial.stepSkillSpecial = 1;
                    if (player.skillSpecial.skillSpecial.template.id == Skill.SUPER_KAME) {
                        if (player.inventory.itemsBody.get(12).isNotNullItem()) {
                            for (int i = 0; i < player.inventory.itemsBody.get(12).itemOptions.size(); i++) {
                                if (player.inventory.itemsBody.get(12).itemOptions.get(i).optionTemplate.id == 219) {
                                    player.inventory.itemsBody.get(12).itemOptions.get(i).param -= 1;
                                    InventoryServiceNew.gI().sendItemBody(player);
                                }
                            }
                            switch (player.inventory.itemsBody.get(12).template.id) {
                                case 1383:
                                    this.startSkillSpecialID24(player, 2);
                                    break;
                                case 1384:
                                    this.startSkillSpecialID24(player, 3);
                                    break;
                                default:
                                    this.startSkillSpecialID24(player, 0);
                                    break;
                            }
                        } else {
                            this.startSkillSpecialID24(player, 0);
                        }
                    } else {
                        // CA DIC LIEN HOAN CHUONG
                        if (player.inventory.itemsBody.get(12).isNotNullItem()) {
                            for (int i = 0; i < player.inventory.itemsBody.get(12).itemOptions.size(); i++) {
                                if (player.inventory.itemsBody.get(12).itemOptions.get(i).optionTemplate.id == 219) {
                                    player.inventory.itemsBody.get(12).itemOptions.get(i).param -= 1;
                                    InventoryServiceNew.gI().sendItemBody(player);
                                }
                            }
                            switch (player.inventory.itemsBody.get(12).template.id) {
                                case 1387:
                                    this.startSkillSpecialID25(player, 2);
                                    break;
                                case 1388:
                                    this.startSkillSpecialID25(player, 3);
                                    break;
                                default:
                                    this.startSkillSpecialID25(player, 0);
                                    break;
                            }
                        } else {
                            this.startSkillSpecialID25(player, 0);
                        }
                    }
                } else if (player.skillSpecial.stepSkillSpecial == 1
                        && !Util.canDoWithTime(player.skillSpecial.lastTimeSkillSpecial, SkillSpecial.TIME_END_24_25)) {
                    if (MapService.gI().isMapOffline(player.zone.map.mapId)) {
                        for (Player playerMap : player.zone.getHumanoids()) {
                            if (playerMap != null && playerMap.id == -player.id) {
                                if (player.skillSpecial.dir == -1 && !playerMap.isDie()
                                        && playerMap.location.x <= player.location.x - 15
                                        && Math.abs(playerMap.location.x
                                                - player.skillSpecial._xPlayer) <= player.skillSpecial._xObjTaget
                                        && Math.abs(playerMap.location.y
                                                - player.skillSpecial._yPlayer) <= player.skillSpecial._yObjTaget
                                        && this.canAttackPlayer(player, playerMap)) {
                                    this.playerAttackPlayer(player, playerMap, false);
                                    PlayerService.gI().sendInfoHpMpMoney(playerMap);
                                }
                                if (player.skillSpecial.dir == 1 && !playerMap.isDie()
                                        && playerMap.location.x >= player.location.x + 15
                                        && Math.abs(playerMap.location.x
                                                - player.skillSpecial._xPlayer) <= player.skillSpecial._xObjTaget
                                        && Math.abs(playerMap.location.y
                                                - player.skillSpecial._yPlayer) <= player.skillSpecial._yObjTaget
                                        && this.canAttackPlayer(player, playerMap)) {
                                    this.playerAttackPlayer(player, playerMap, false);
                                    PlayerService.gI().sendInfoHpMpMoney(playerMap);
                                }
                            }
                        }
                        return;
                    }
                    for (Player playerMap : player.zone.getHumanoids()) {
                        if (player.skillSpecial.dir == -1 && !playerMap.isDie()
                                && playerMap.location.x <= player.location.x - 15
                                && Math.abs(playerMap.location.x
                                        - player.skillSpecial._xPlayer) <= player.skillSpecial._xObjTaget
                                && Math.abs(playerMap.location.y
                                        - player.skillSpecial._yPlayer) <= player.skillSpecial._yObjTaget
                                && this.canAttackPlayer(player, playerMap)) {
                            this.playerAttackPlayer(player, playerMap, false);
                            PlayerService.gI().sendInfoHpMpMoney(playerMap);
                        }
                        if (player.skillSpecial.dir == 1 && !playerMap.isDie()
                                && playerMap.location.x >= player.location.x + 15
                                && Math.abs(playerMap.location.x
                                        - player.skillSpecial._xPlayer) <= player.skillSpecial._xObjTaget
                                && Math.abs(playerMap.location.y
                                        - player.skillSpecial._yPlayer) <= player.skillSpecial._yObjTaget
                                && this.canAttackPlayer(player, playerMap)) {
                            this.playerAttackPlayer(player, playerMap, false);
                            PlayerService.gI().sendInfoHpMpMoney(playerMap);
                        }
                        if (playerMap == null) {
                            continue;
                        }
                    }
                    for (Mob mobMap : player.zone.mobs) {
                        if (player.skillSpecial.dir == -1 && !mobMap.isDie()
                                && mobMap.location.x <= player.skillSpecial._xPlayer - 15
                                && Math.abs(mobMap.location.x
                                        - player.skillSpecial._xPlayer) <= player.skillSpecial._xObjTaget
                                && Math.abs(mobMap.location.y
                                        - player.skillSpecial._yPlayer) <= player.skillSpecial._yObjTaget) {
                            this.playerAttackMob(player, mobMap, false, false);
                        }
                        if (player.skillSpecial.dir == 1 && !mobMap.isDie()
                                && mobMap.location.x >= player.skillSpecial._xPlayer + 15
                                && Math.abs(mobMap.location.x
                                        - player.skillSpecial._xPlayer) <= player.skillSpecial._xObjTaget
                                && Math.abs(mobMap.location.y
                                        - player.skillSpecial._yPlayer) <= player.skillSpecial._yObjTaget) {
                            this.playerAttackMob(player, mobMap, false, false);
                        }
                        if (mobMap == null) {
                            continue;
                        }
                    }
                } else if (player.skillSpecial.stepSkillSpecial == 1) {
                    player.skillSpecial.closeSkillSpecial();
                }
            }
        } catch (Exception e) {
        }
    }

    public void sendCurrLevelSpecial(Player player, Skill skill) {
        Message message = null;
        try {
            message = Service.getInstance().messageSubCommand((byte) 62);
            message.writer().writeShort(skill.skillId);
            message.writer().writeByte(0);
            message.writer().writeShort(skill.currLevel);
            player.sendMessage(message);
        } catch (final Exception ex) {
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }
        }
    }

    // ============================================================================
    // Skill SuperKame
    public void sendEffSkillSpecialID24(Player player, byte dir, int TypePaintSkill) {
        Message message = null;
        try {
            message = new Message(-45);// passt code k dc vcb
            message.writer().writeByte(20);
            message.writer().writeInt((int) player.id);
            message.writer().writeShort(24);
            message.writer().writeByte(1);
            message.writer().writeByte(dir); // -1 trai | 1 phai
            message.writer().writeShort(2000);
            message.writer().writeByte(0);
            message.writer().writeByte(TypePaintSkill);// đoạn này là skill paint
            message.writer().writeByte(0);
            Service.getInstance().sendMessAllPlayerInMap(player, message);
            message.cleanup();
        } catch (Exception e) {
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }
        }
    }

    // Skill liên hoàn chưởng
    public void sendEffSkillSpecialID25(Player player, byte dir, int typeskill) { // Tư thế gồng + hào quang
        Message message = null;
        try {
            message = new Message(-45);// passt code k dc vcb
            message.writer().writeByte(20);
            message.writer().writeInt((int) player.id);
            message.writer().writeShort(25);
            message.writer().writeByte(2);
            message.writer().writeByte(dir); // -1 trai | 1 phai
            message.writer().writeShort(2000);
            message.writer().writeByte(0);
            message.writer().writeByte(typeskill); // type skill : 0 = defaule, 1,2 = type mới
            message.writer().writeByte(0);
            Service.getInstance().sendMessAllPlayerInMap(player, message);
            message.cleanup();
        } catch (Exception e) {
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }
        }
    }

    // Skill Ma phong ba
    public void sendEffSkillSpecialID26(Player player, byte dir, int typeskill) {
        Message message = null;
        try {
            message = new Message(-45);// passt code k dc vcb
            message.writer().writeByte(20);
            message.writer().writeInt((int) player.id);
            message.writer().writeShort(26); // id effect
            message.writer().writeByte(3);
            message.writer().writeByte(dir); // -1 trai | 1 phai
            message.writer().writeShort(SkillSpecial.TIME_GONG);
            message.writer().writeByte(0);
            message.writer().writeByte(typeskill);
            message.writer().writeByte(0);
            Service.getInstance().sendMessAllPlayerInMap(player, message);
            message.cleanup();
        } catch (Exception e) {
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }
        }
    }

    public void startSkillSpecialID24(Player player, int TypePaintSkill) {
        Message message = null;
        try {
            message = new Message(-45);
            message.writer().writeByte(21);
            message.writer().writeInt((int) player.id);
            message.writer().writeShort(player.skillSpecial.skillSpecial.template.id);
            message.writer().writeShort(
                    player.skillSpecial._xPlayer + ((player.skillSpecial.dir == -1) ? (-player.skillSpecial._xObjTaget)
                            : player.skillSpecial._xObjTaget));
            message.writer().writeShort(player.skillSpecial._xPlayer);
            message.writer().writeShort(3000); // thời gian skill chưởng chưởng nè
            message.writer().writeShort(player.skillSpecial._yObjTaget);
            message.writer().writeByte(TypePaintSkill);
            message.writer().writeByte(TypePaintSkill);
            message.writer().writeByte(TypePaintSkill);
            Service.getInstance().sendMessAllPlayerInMap(player, message);
            message.cleanup();
        } catch (final Exception ex) {
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }
        }
    }

    public void startSkillSpecialID25(Player player, int typeskill) { // bắt đầu sử dụng skill
        Message message = null;
        try {
            message = new Message(-45);
            message.writer().writeByte(21);
            message.writer().writeInt((int) player.id);
            message.writer().writeShort(player.skillSpecial.skillSpecial.template.id);
            message.writer().writeShort(
                    player.skillSpecial._xPlayer + ((player.skillSpecial.dir == -1) ? (-player.skillSpecial._xObjTaget)
                            : player.skillSpecial._xObjTaget));
            message.writer().writeShort(player.skillSpecial._yPlayer);
            message.writer().writeShort(3000); // thời gian skill chưởng chưởng nè
            message.writer().writeShort(25);
            message.writer().writeByte(typeskill); // skill tung ra : 0 = skill mặc định
            message.writer().writeByte(typeskill); // skill kết : 0 = skill mặc định
            message.writer().writeByte(typeskill); // skill kết : 0 = skill mặc định
            Service.getInstance().sendMessAllPlayerInMap(player, message);
            message.cleanup();
        } catch (final Exception ex) {
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }
        }
    }

    public void startSkillSpecialID26(Player player, int typeskill) {
        Message message = null;
        try {
            message = new Message(-45);
            message.writer().writeByte(21);
            message.writer().writeInt((int) player.id);
            message.writer().writeShort(26);
            message.writer().writeShort(player.skillSpecial._xPlayer + ((player.skillSpecial.dir == -1) ? (-75) : 75));
            message.writer().writeShort(player.skillSpecial._yPlayer);
            message.writer().writeShort(3000);
            message.writer().writeShort(player.skillSpecial._yObjTaget);
            message.writer().writeByte(typeskill);
            final byte size = (byte) (player.skillSpecial.playersTaget.size() + player.skillSpecial.mobsTaget.size());
            message.writer().writeByte(size);
            for (Player playerMap : player.skillSpecial.playersTaget) {
                message.writer().writeByte(1);
                message.writer().writeInt((int) playerMap.id);
            }
            for (Mob mobMap : player.skillSpecial.mobsTaget) {
                message.writer().writeByte(0);
                message.writer().writeByte(mobMap.id);
            }
            message.writer().writeByte(2); // ảnh bình để hút vào : 0 = defaule ; 1 = ảnh cạnh ảnh 0; 2 = ảnh cạnh ảnh 1
            Service.getInstance().sendMessAllPlayerInMap(player, message);
            message.cleanup();
        } catch (final Exception ex) {
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }
        }
    }

    // này hoc5 skill nha
    public void learSkillSpecial(Player player, byte skillID) {
        Message message = null;
        try {
            Skill curSkill = SkillUtil.createSkill(skillID, 1);
            SkillUtil.setSkill(player, curSkill);
            message = Service.getInstance().messageSubCommand((byte) 23);
            message.writer().writeShort(curSkill.skillId);
            player.sendMessage(message);
            message.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }

        }
    }

    public void learSkillSpecial(Player player, byte skillID, byte level) {
        Message message = null;
        try {
            Skill curSkill = SkillUtil.createSkill(skillID, level);
            SkillUtil.setSkill(player, curSkill);
            message = Service.getInstance().messageSubCommand((byte) 23);
            message.writer().writeShort(curSkill.skillId);
            message.writer().writeByte(level);
            player.sendMessage(message);
            message.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }

        }
    }

    public void useSkillTranformation(Player player) {
        if (!canUseSkillWithCooldown(player, Skill.SUPER_TRANFORMATION)) {
            return;
        }
        EffectSkillService.gI().sendEffectTranformation(player);
        EffectSkillService.gI().setIsTranformation(player);
        player.effectSkill.lastTimeTranformation = System.currentTimeMillis();
        affterUseSkill(player, player.playerSkill.skillSelect.template.id);
    }

    private boolean canUseSkillWithCooldown(Player player, int skillId) {
        long cooldownTime = 300000;
        long cooldownTime1 = 3000;
        long currentTime = System.currentTimeMillis();
        if (skillId == Skill.SUPER_TRANFORMATION) {
            return (currentTime - player.effectSkill.lastTimeTranformation) > cooldownTime;
        } else if (skillId == Skill.EVOLUTION) {
            return (currentTime - player.effectSkill.lastTimeEvolution) >= cooldownTime1;
        }
        return false;
    }

    public void setSkillEvolution(Player player) {
        if ((player.effectSkill.isTranformation || player.effectSkill.isEvolution)) {
            if (canUseSkillWithCooldown(player, Skill.EVOLUTION)) {
                player.isbienhinh++;
                PlayerDAO.saveisBienHinh(player);
                player.effectSkill.isTranformation = false;
                EffectSkillService.gI().sendEffectVolution(player);
                EffectSkillService.gI().setIsEvolution(player);
                player.lastTimeTranformation = System.currentTimeMillis();
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
            } else {
                Service.getInstance().sendThongBao(player, "Đợi 3 Giây Để Biến Hình Tiếp");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Bạn Chưa Biến Hình");
        }
    }

    public void useSkillEvolution(Player player) {

        switch (player.effectSkill.levelTranformation) {
            case 1:
                if (player.isbienhinh == 0) {
                    Service.getInstance().sendThongBao(player, "Skill Biến Hình Phải Từ Cấp 2 Mới Có Thể Tiến Hóa");
                }
                break;
            case 2:
                if (player.isbienhinh < 1) {
                    setSkillEvolution(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Đã Đạt Cấp Tối Đa");
                }
                break;
            case 3:
                if (player.isbienhinh < 2) {
                    setSkillEvolution(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Đã Đạt Cấp Tối Đa");
                }
                break;
            case 4:
                if (player.isbienhinh < 3) {
                    setSkillEvolution(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Đã Đạt Cấp Tối Đa");
                }
                break;
            case 5:
                if (player.isbienhinh < 4) {
                    setSkillEvolution(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Đã Đạt Cấp Tối Đa");
                }
                break;
            default:
        }
    }

    // =============================================================================
    public void useSkillAttack(Player player, Player plTarget, Mob mobTarget) {
        if (!player.isBoss) {
            if (player.isPet) {
                if (player.nPoint.stamina > 0) {
                    player.nPoint.numAttack++;
                    boolean haveCharmPet = ((Pet) player).master.charms.tdDeTu > System.currentTimeMillis();
                    if (haveCharmPet ? player.nPoint.numAttack >= 5 : player.nPoint.numAttack >= 2) {
                        player.nPoint.numAttack = 0;
                        player.nPoint.stamina--;
                    }
                } else if (player.isTrieuhoipet && ((Thu_TrieuHoi) player).masterr.CapBacThan != -1
                        && ((Thu_TrieuHoi) player).masterr.TrieuHoipet != null) {
                    if (((Thu_TrieuHoi) player).masterr.ThanLevel < 100) {
                        ((Thu_TrieuHoi) player).masterr.ExpThan += Util.nextInt(10000, 50000);
                        if ((((Thu_TrieuHoi) player).masterr.ExpThan >= 3000000L
                                + ((Thu_TrieuHoi) player).masterr.ThanLevel * 1500000L)
                                && ((Thu_TrieuHoi) player).masterr.ThanLevel < 100) {
                            ((Thu_TrieuHoi) player).masterr.ThanLevel++;
                            ((Thu_TrieuHoi) player).masterr.ExpThan = 0;
                            ((Thu_TrieuHoi) player).masterr.DameThan += Util.GioiHannext(10000,
                                    10000L + ((((Thu_TrieuHoi) player).masterr.CapBacThan + 1) * 1000L));
                            ((Thu_TrieuHoi) player).masterr.MauThan += Util.GioiHannext(50000,
                                    50000L + ((((Thu_TrieuHoi) player).masterr.CapBacThan + 1) * 5000L));
                        }
                    } else if (((Thu_TrieuHoi) player).masterr.CapBacThan >= 10
                            && ((Thu_TrieuHoi) player).masterr.CapBacThan <= 10) {
                        if (Util.isTrue(5f, 100)) {
                            int tnhan;
                            switch (((Thu_TrieuHoi) player).masterr.CapBacThan) {
                                default:
                                    tnhan = (((Thu_TrieuHoi) player).masterr.ThanLevel + 1) * 1;
                                    break;
                            }
                            ((Thu_TrieuHoi) player).masterr.inventory.ruby += tnhan;
                            Service.gI().sendThongBao(((Thu_TrieuHoi) player).masterr,
                                    "Bạn vừa nhận " + tnhan + " Hồng ngọc, Do Chiến Thần tìm được");
                            Service.gI().sendMoney(((Thu_TrieuHoi) player).masterr);
                        }
                    }
                } else {
                    ((Pet) player).askPea();
                    return;
                }
            } else {
                if (player.nPoint.stamina > 0) {
                    if (player.charms.tdDeoDai < System.currentTimeMillis()) {
                        player.nPoint.numAttack++;
                        if (player.nPoint.numAttack == 5) {
                            player.nPoint.numAttack = 0;
                            // player.nPoint.stamina--; phước không tốn thể lực
                            PlayerService.gI().sendCurrentStamina(player);
                        }
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Thể lực đã cạn kiệt, hãy nghỉ ngơi để lấy lại sức");
                    return;
                }
            }
        }
        List<Mob> mobs;
        boolean miss = false;
        switch (player.playerSkill.skillSelect.template.id) {
            case Skill.KAIOKEN: // kaioken
                long hpUse = (long) (player.nPoint.hpMax / 100 * 10);
                if (player.nPoint.hp <= hpUse) {
                    break;
                } else {
                    player.nPoint.setHp((long) (player.nPoint.mp - hpUse));
                    PlayerService.gI().sendInfoHpMpMoney(player);
                    Service.getInstance().Send_Info_NV(player);
                }
            case Skill.DRAGON:
            case Skill.DEMON:
            case Skill.GALICK:
            case Skill.LIEN_HOAN:
                if (plTarget != null && Util.getDistance(player, plTarget) > Skill.RANGE_ATTACK_CHIEU_DAM) {
                    miss = true;
                }
                if (mobTarget != null && Util.getDistance(player, mobTarget) > Skill.RANGE_ATTACK_CHIEU_DAM) {
                    miss = true;
                }
            case Skill.KAMEJOKO:
            case Skill.MASENKO:
            case Skill.ANTOMIC:
                if (plTarget != null) {
                    playerAttackPlayer(player, plTarget, miss);
                }
                if (mobTarget != null) {
                    playerAttackMob(player, mobTarget, miss, false);
                    // LIEN_HOAN: thêm sát thương lan sang 1-2 quái gần mục tiêu trong phạm vi đấm
                    if (player.playerSkill != null
                            && player.playerSkill.skillSelect != null
                            && player.playerSkill.skillSelect.template != null
                            && player.playerSkill.skillSelect.template.id == Skill.LIEN_HOAN) {
                        int extraHit = 0;
                        int maxExtra = 5; // lan thêm tối đa 5 quái
                        // Tăng phạm vi lan theo level của kỹ năng LIEN_HOAN
                        int point = 0;
                        if (player.playerSkill != null && player.playerSkill.skillSelect != null) {
                            point = player.playerSkill.skillSelect.point;
                        }
                        int splashRange = Skill.RANGE_ATTACK_CHIEU_DAM + point * 20; // mỗi cấp +20 phạm vi
                        for (Mob mob : player.zone.mobs) {
                            if (mob == null || mob.equals(mobTarget) || mob.isDie()) {
                                continue;
                            }
                            // dùng cùng phạm vi cận chiến như mục tiêu chính
                            if (Util.getDistance(mob, mobTarget) <= splashRange) {
                                playerAttackMob(player, mob, miss, false);
                                extraHit++;
                                if (extraHit >= maxExtra) {
                                    break;
                                }
                            }
                        }
                    }
                }
                if (player.mobMe != null) {
                    player.mobMe.attack(plTarget, mobTarget);
                }
                if (player.playerSkill != null && player.playerSkill.skillSelect != null
                        && player.playerSkill.skillSelect.template != null) {
                    affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                }
                break;
            // ******************************************************************
            case Skill.QUA_CAU_KENH_KHI:
                if (!player.playerSkill.prepareQCKK) {
                    // bắt đầu tụ quả cầu
                    player.playerSkill.prepareQCKK = !player.playerSkill.prepareQCKK;
                    player.playerSkill.lastTimePrepareQCKK = System.currentTimeMillis();
                    sendPlayerPrepareSkill(player, 4000);
                } else {
                    // ném cầu
                    player.playerSkill.prepareQCKK = !player.playerSkill.prepareQCKK;
                    mobs = new ArrayList<>();
                    if (plTarget != null) {
                        playerAttackPlayer(player, plTarget, false);
                        for (Mob mob : player.zone.mobs) {
                            if (!mob.isDie()
                                    && Util.getDistance(plTarget, mob) <= SkillUtil
                                    .getRangeQCKK(player.playerSkill.skillSelect.point)) {
                                mobs.add(mob);
                            }
                        }
                    }
                    if (mobTarget != null) {
                        playerAttackMob(player, mobTarget, false, true);
                        for (Mob mob : player.zone.mobs) {
                            if (!mob.equals(mobTarget) && !mob.isDie()
                                    && Util.getDistance(mob, mobTarget) <= SkillUtil
                                    .getRangeQCKK(player.playerSkill.skillSelect.point)) {
                                mobs.add(mob);
                            }
                        }
                    }
                    for (Mob mob : mobs) {
                        // mob.injured(player, player.point.getDameAttack(), true);
                    }
                    PlayerService.gI().sendInfoHpMpMoney(player);
                    affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                }
                break;
            case Skill.MAKANKOSAPPO:
                if (!player.playerSkill.prepareLaze) {
                    // bắt đầu nạp laze
                    player.playerSkill.prepareLaze = !player.playerSkill.prepareLaze;
                    player.playerSkill.lastTimePrepareLaze = System.currentTimeMillis();
                    sendPlayerPrepareSkill(player, 3000);
                } else {
                    // bắn laze
                    player.playerSkill.prepareLaze = !player.playerSkill.prepareLaze;
                    if (plTarget != null) {
                        playerAttackPlayer(player, plTarget, false);
                    }
                    if (mobTarget != null) {
                        playerAttackMob(player, mobTarget, false, true);
                        // mobTarget.attackMob(player, false, true);
                    }
                    affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                }
                PlayerService.gI().sendInfoHpMpMoney(player);
                break;
            case Skill.SOCOLA:
                EffectSkillService.gI().sendEffectUseSkill(player, Skill.SOCOLA);
                int timeSocola = SkillUtil.getTimeSocola();
                if (plTarget != null) {
                    EffectSkillService.gI().setSocola(plTarget, System.currentTimeMillis(), timeSocola);
                    Service.getInstance().Send_Caitrang(plTarget);
                    ItemTimeService.gI().sendItemTime(plTarget, 3780, timeSocola / 1000);
                }
                if (mobTarget != null) {
                    EffectSkillService.gI().sendMobToSocola(player, mobTarget, timeSocola);
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.DICH_CHUYEN_TUC_THOI:
                int timeChoangDCTT = SkillUtil.getTimeDCTT(player.playerSkill.skillSelect.point);
                if (plTarget != null) {
                    Service.getInstance().setPos(player, plTarget.location.x, plTarget.location.y);
                    playerAttackPlayer(player, plTarget, miss);
                    EffectSkillService.gI().setBlindDCTT(plTarget, System.currentTimeMillis(), timeChoangDCTT);
                    EffectSkillService.gI().sendEffectPlayer(player, plTarget, EffectSkillService.TURN_ON_EFFECT,
                            EffectSkillService.BLIND_EFFECT);
                    PlayerService.gI().sendInfoHpMpMoney(plTarget);
                    ItemTimeService.gI().sendItemTime(plTarget, 3779, timeChoangDCTT / 1000);
                }
                if (mobTarget != null) {
                    Service.getInstance().setPos(player, mobTarget.location.x, mobTarget.location.y);
                    // mobTarget.attackMob(player, false, false);
                    playerAttackMob(player, mobTarget, false, false);
                    mobTarget.effectSkill.setStartBlindDCTT(System.currentTimeMillis(), timeChoangDCTT);
                    EffectSkillService.gI().sendEffectMob(player, mobTarget, EffectSkillService.TURN_ON_EFFECT,
                            EffectSkillService.BLIND_EFFECT);
                }
                player.nPoint.isCrit100 = true;
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.THOI_MIEN:
                EffectSkillService.gI().sendEffectUseSkill(player, Skill.THOI_MIEN);
                int timeSleep = SkillUtil.getTimeThoiMien(player.playerSkill.skillSelect.point);
                if (plTarget != null) {
                    EffectSkillService.gI().setThoiMien(plTarget, System.currentTimeMillis(), timeSleep);
                    EffectSkillService.gI().sendEffectPlayer(player, plTarget, EffectSkillService.TURN_ON_EFFECT,
                            EffectSkillService.SLEEP_EFFECT);
                    ItemTimeService.gI().sendItemTime(plTarget, 3782, timeSleep / 1000);
                }
                if (mobTarget != null) {
                    mobTarget.effectSkill.setThoiMien(System.currentTimeMillis(), timeSleep);
                    EffectSkillService.gI().sendEffectMob(player, mobTarget, EffectSkillService.TURN_ON_EFFECT,
                            EffectSkillService.SLEEP_EFFECT);
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.TROI:
                EffectSkillService.gI().sendEffectUseSkill(player, Skill.TROI);
                int timeHold = SkillUtil.getTimeTroi(player.playerSkill.skillSelect.point);
                EffectSkillService.gI().setUseTroi(player, System.currentTimeMillis(), timeHold);
                if (plTarget != null && (!plTarget.playerSkill.prepareQCKK && !plTarget.playerSkill.prepareLaze
                        && !plTarget.playerSkill.prepareTuSat)) {
                    player.effectSkill.plAnTroi = plTarget;
                    EffectSkillService.gI().sendEffectPlayer(player, plTarget, EffectSkillService.TURN_ON_EFFECT,
                            EffectSkillService.HOLD_EFFECT);
                    EffectSkillService.gI().setAnTroi(plTarget, player, System.currentTimeMillis(), timeHold);
                }
                if (mobTarget != null) {
                    player.effectSkill.mobAnTroi = mobTarget;
                    EffectSkillService.gI().sendEffectMob(player, mobTarget, EffectSkillService.TURN_ON_EFFECT,
                            EffectSkillService.HOLD_EFFECT);
                    mobTarget.effectSkill.setTroi(System.currentTimeMillis(), timeHold);
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                if (plTarget != null
                        && plTarget.isBoss
                        && MapService.gI().isMapHuyDiet(player.zone.map.mapId)
                        && Util.isTrue(40, 100)) {
                    EffectSkillService.gI().removeUseTroi(player);
                    EffectSkillService.gI().removeAnTroi(plTarget);
                    Service.getInstance().chat(plTarget, "Chiêu đó không có tác dụng đâu haha");
                }
                break;
        }
        if (!player.isBoss) {
            player.effectSkin.lastTimeAttack = System.currentTimeMillis();
        }
    }

    private void useSkillAlone(Player player) {
        List<Mob> mobs;
        List<Player> players;
        switch (player.playerSkill.skillSelect.template.id) {
            case Skill.THAI_DUONG_HA_SAN:
                int timeStun = SkillUtil.getTimeStun(player.playerSkill.skillSelect.point);
                if (player.setClothes.thienXinHang == 5) {
                    timeStun *= 2;
                }
                mobs = new ArrayList<>();
                players = new ArrayList<>();
                if (!MapService.gI().isMapOffline(player.zone.map.mapId)) {
                    List<Player> playersMap = player.zone.getHumanoids();
                    for (Player pl : playersMap) {
                        if (pl.nPoint == null) {
                            break;
                        }
                        if (pl != null && !player.equals(pl) && !pl.nPoint.khangTDHS) {
                            if (Util.getDistance(player, pl) <= SkillUtil
                                    .getRangeStun(player.playerSkill.skillSelect.point)
                                    && canAttackPlayer(player, pl) && (!pl.playerSkill.prepareQCKK
                                    && !pl.playerSkill.prepareLaze && !pl.playerSkill.prepareTuSat)) {
                                if (player.isPet && ((Pet) player).master.equals(pl)) {
                                    continue;
                                }
                                EffectSkillService.gI().startStun(pl, System.currentTimeMillis(), timeStun);
                                players.add(pl);
                            }
                        }
                    }
                }
                for (Mob mob : player.zone.mobs) {
                    if (Util.getDistance(player, mob) <= SkillUtil.getRangeStun(player.playerSkill.skillSelect.point)) {
                        mob.effectSkill.startStun(System.currentTimeMillis(), timeStun);
                        mobs.add(mob);
                    }
                }
                EffectSkillService.gI().sendEffectBlindThaiDuongHaSan(player, players, mobs, timeStun);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.DE_TRUNG:
                EffectSkillService.gI().sendEffectUseSkill(player, Skill.DE_TRUNG);
                if (player.mobMe != null) {
                    player.mobMe.mobMeDie();
                }
                player.mobMe = new MobMe(player);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.BIEN_KHI:
                EffectSkillService.gI().sendEffectMonkey(player);
                EffectSkillService.gI().setIsMonkey(player);
                EffectSkillService.gI().sendEffectMonkey(player);

                Service.getInstance().sendSpeedPlayer(player, 0);
                Service.getInstance().Send_Caitrang(player);
                Service.getInstance().sendSpeedPlayer(player, -1);
                if (!player.isPet) {
                    PlayerService.gI().sendInfoHpMp(player);
                }
                Service.getInstance().point(player);
                Service.getInstance().Send_Info_NV(player);
                Service.getInstance().sendInfoPlayerEatPea(player);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.KHIEN_NANG_LUONG:
                EffectSkillService.gI().setStartShield(player);
                EffectSkillService.gI().sendEffectPlayer(player, player, EffectSkillService.TURN_ON_EFFECT,
                        EffectSkillService.SHIELD_EFFECT);
                ItemTimeService.gI().sendItemTime(player, 3784, player.effectSkill.timeShield / 1000);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.HUYT_SAO:
                int tileHP = SkillUtil.getPercentHPHuytSao(player.playerSkill.skillSelect.point);
                if (player.zone != null) {
                    if (!MapService.gI().isMapOffline(player.zone.map.mapId)) {
                        List<Player> playersMap = player.zone.getNotBosses();
                        for (Player pl : playersMap) {
                            if (pl != null && !pl.isPet && !pl.isNewPet) {
                                if (pl.effectSkill != null) {
                                    if (pl.effectSkill.useTroi) {
                                        EffectSkillService.gI().removeUseTroi(pl);
                                    }
                                }
                                if (!pl.isBoss && pl.gender != ConstPlayer.NAMEC
                                        && player.cFlag == pl.cFlag && pl != null && pl.nPoint != null) {
                                    EffectSkillService.gI().setStartHuytSao(pl, tileHP);
                                    EffectSkillService.gI().sendEffectPlayer(pl, pl, EffectSkillService.TURN_ON_EFFECT,
                                            EffectSkillService.HUYT_SAO_EFFECT);
                                    pl.nPoint.calPoint();
                                    pl.nPoint.setHp((long) (pl.nPoint.hp + (pl.nPoint.hp * tileHP / 100)));
                                    Service.getInstance().point(pl);
                                    Service.getInstance().Send_Info_NV(pl);
                                    ItemTimeService.gI().sendItemTime(pl, 3781, 30);
                                    PlayerService.gI().sendInfoHpMp(pl);
                                }
                            }

                        }
                    } else {
                        if (player != null) {
                            EffectSkillService.gI().setStartHuytSao(player, tileHP);
                            EffectSkillService.gI().sendEffectPlayer(player, player, EffectSkillService.TURN_ON_EFFECT,
                                    EffectSkillService.HUYT_SAO_EFFECT);
                            player.nPoint.calPoint();
                            player.nPoint.setHp((long) (player.nPoint.hp + (player.nPoint.hp * tileHP / 100)));
                            Service.getInstance().point(player);
                            Service.getInstance().Send_Info_NV(player);
                            ItemTimeService.gI().sendItemTime(player, 3781, 30);
                            PlayerService.gI().sendInfoHpMp(player);
                        }
                    }
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.TAI_TAO_NANG_LUONG:
                EffectSkillService.gI().startCharge(player);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.TU_SAT:
                if (!player.playerSkill.prepareTuSat) {
                    // gồng tự sát
                    player.playerSkill.prepareTuSat = !player.playerSkill.prepareTuSat;
                    player.playerSkill.lastTimePrepareTuSat = System.currentTimeMillis();
                    sendPlayerPrepareBom(player, 2000);
                } else {// Đoạn dưới chỉnh từ 1500 lên 1800 , nếu bom lỗi thì chỉnh lại ở đấy.
                    if (!player.isBoss && !Util.canDoWithTime(player.playerSkill.lastTimePrepareTuSat, 1800)) {
                        player.playerSkill.skillSelect.lastTimeUseThisSkill = System.currentTimeMillis();
                        player.playerSkill.prepareTuSat = false;
                        return;
                    }
                    // nổ
                    player.playerSkill.prepareTuSat = !player.playerSkill.prepareTuSat;
                    int rangeBom = SkillUtil.getRangeBom(player.playerSkill.skillSelect.point);

                    long dameMob = (long) player.nPoint.hpMax;
                    if (player.nPoint.satThuongBom > 0) {
                        dameMob = (long) (dameMob * player.nPoint.satThuongBom / 100);
                    }
                    for (Mob mob : player.zone.mobs) {
                        mob.injured(player, dameMob, true);
                    }
                    List<Player> playersMap = null;
                    if (player.isBoss) {
                        playersMap = player.zone.getNotBosses();
                    } else {
                        playersMap = player.zone.getHumanoids();
                    }
                    if (!MapService.gI().isMapOffline(player.zone.map.mapId)) {
                        for (Player pl : playersMap) {
                            if (!player.equals(pl) && canAttackPlayer(player, pl)) {
                                long damePlayer = (long) player.nPoint.hpMax;
                                if (player.nPoint.satThuongBom > 0) {
                                    damePlayer = (long) (damePlayer * player.nPoint.satThuongBom / 100);
                                }
                                pl.injured(player, pl.isBoss ? damePlayer / 2 : damePlayer, false, false);
                                PlayerService.gI().sendInfoHpMpMoney(pl);
                                Service.getInstance().Send_Info_NV(pl);
                            }
                        }

                    }

                    affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                    player.injured(null, 2100000000, true, false);
                    if (player.effectSkill.tiLeHPHuytSao != 0) {
                        player.effectSkill.tiLeHPHuytSao = 0;
                        EffectSkillService.gI().removeHuytSao(player);
                    }
                }
                break;
        }
    }

    private void useSkillBuffToPlayer(Player player, Player plTarget) {
        switch (player.playerSkill.skillSelect.template.id) {
            case Skill.TRI_THUONG:
                List<Player> players = new ArrayList();
                int percentTriThuong = SkillUtil.getPercentTriThuong(player.playerSkill.skillSelect.point);
                int point = player.playerSkill.skillSelect.point;
                if (canHsPlayer(player, plTarget)) {
                    players.add(plTarget);
                    List<Player> playersMap = player.zone.getNotBosses();
                    for (Player pl : playersMap) {
                        if (!pl.equals(plTarget)) {
                            try {
                                if (canHsPlayer(player, plTarget) && Util.getDistance(player, pl) <= 300) {
                                    players.add(pl);
                                } else {
                                    Service.getInstance().sendThongBao(player,
                                            "Thưa ngài, ngài không thể hồi sinh cho hắn");
                                }
                            } catch (Exception e) {
                                Service.getInstance().sendThongBao(player, "Không thể trị thương cho hắn");

                            }
                        }
                    }
                    playerAttackPlayer(player, plTarget, false);
                    for (Player pl : players) {
                        boolean isDie = pl.isDie();
                        long hpHoi = (long) (pl.nPoint.hpMax * percentTriThuong / 100);
                        long mpHoi = (long) (pl.nPoint.mpMax * percentTriThuong / 100);
                        pl.nPoint.addHp(hpHoi);
                        pl.nPoint.addMp(mpHoi);
                        if (isDie) {
                            Service.getInstance().hsChar(pl, hpHoi, mpHoi);
                            PlayerService.gI().sendInfoHpMp(pl);
                        } else {
                            Service.getInstance().Send_Info_NV(pl);
                            PlayerService.gI().sendInfoHpMp(pl);
                        }
                    }
                    long hpHoiMe = (long) (player.nPoint.hp * percentTriThuong / 100);
                    player.nPoint.addHp(hpHoiMe);
                    PlayerService.gI().sendInfoHp(player);
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
        }
    }

    private void phanSatThuongInternal(Player plAtt, Player plTarget, double dame) {
        int percentPST = plTarget.nPoint.tlPST;
        if (percentPST != 0) {
            double damePST = dame * percentPST / 100;
            Message msg;
            try {
                msg = new Message(56);
                msg.writer().writeInt((int) plAtt.id);
                if (damePST >= plAtt.nPoint.hp) {
                    damePST = plAtt.nPoint.hp - 1;
                }
                damePST = plAtt.injured(null, damePST, true, false);
                msg.writeDouble(plAtt.nPoint.hp);
                msg.writeDouble(damePST);
                msg.writer().writeBoolean(false);
                msg.writer().writeByte(36);
                Service.getInstance().sendMessAllPlayerInMap(plAtt, msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(SkillService.class, e);
            }
        }
    }

    private void hutHPMPInternal(Player player, double dame, boolean attackMob) {
        int tiLeHutHp = player.nPoint.getTileHutHp(attackMob);
        int tiLeHutMp = player.nPoint.getTiLeHutMp();
        double hpHoi = dame * tiLeHutHp / 100;
        double mpHoi = dame * tiLeHutMp / 100;
        if (hpHoi > 0 || mpHoi > 0) {
            PlayerService.gI().hoiPhuc(player, hpHoi > 0 ? hpHoi : 0, mpHoi > 0 ? mpHoi : 0);
        }
    }

    private void phanSatThuong(Player plAtt, Player plTarget, long dame) {
        phanSatThuongInternal(plAtt, plTarget, (double) dame);
    }

    private void phanSatThuong(Player plAtt, Player plTarget, double dame) {
        phanSatThuongInternal(plAtt, plTarget, dame);
    }

    private void hutHPMP(Player player, long dame, boolean attackMob) {
        hutHPMPInternal(player, (double) dame, attackMob);
    }

    private void hutHPMP(Player player, double dame, boolean attackMob) {
        hutHPMPInternal(player, dame, attackMob);
    }

    private void playerAttackPlayer(Player plAtt, Player plInjure, boolean miss) {
        if (plInjure.effectSkill.anTroi) {
            plAtt.nPoint.isCrit100 = true;
        }
        double dameHit = plInjure.injured(plAtt, miss ? 0 : plAtt.nPoint.getDameAttackDouble(false), false, false);
        phanSatThuong(plAtt, plInjure, dameHit);
        hutHPMP(plAtt, dameHit, false);
        Message msg;
        try {
            msg = new Message(-60);
            msg.writer().writeInt((int) plAtt.id); // id pem
            msg.writer().writeByte(plAtt.playerSkill.skillSelect.skillId); // skill pem
            msg.writer().writeByte(1); // số người pem
            msg.writer().writeInt((int) plInjure.id); // id ăn pem
            byte typeSkill = SkillUtil.getTyleSkillAttack(plAtt.playerSkill.skillSelect);
            msg.writer().writeByte(typeSkill == 2 ? 0 : 1); // read continue
            msg.writer().writeByte(typeSkill); // type skill
            msg.writer().writeDouble(Dragon.utils.Util.limitDouble(dameHit)); // dame ăn
            msg.writer().writeBoolean(plInjure.isDie()); // is die
            msg.writer().writeBoolean(plAtt.nPoint.isCrit); // crit
            if (typeSkill != 1) {
                Service.getInstance().sendMessAllPlayerInMap(plAtt, msg);
                msg.cleanup();
            } else {
                plInjure.sendMessage(msg);
                msg.cleanup();
                msg = new Message(-60);
                msg.writer().writeInt((int) plAtt.id); // id pem
                msg.writer().writeByte(plAtt.playerSkill.skillSelect.skillId); // skill pem
                msg.writer().writeByte(1); // số người pem
                msg.writer().writeInt((int) plInjure.id); // id ăn pem
                msg.writer().writeByte(typeSkill == 2 ? 0 : 1); // read continue
                msg.writer().writeByte(0); // type skill
                msg.writer().writeDouble(Dragon.utils.Util.limitDouble(dameHit)); // dame ăn
                msg.writer().writeBoolean(plInjure.isDie()); // is die
                msg.writer().writeBoolean(plAtt.nPoint.isCrit); // crit
                Service.getInstance().sendMessAnotherNotMeInMap(plInjure, msg);
                msg.cleanup();
            }
            if (plInjure.isDie() && !plAtt.isBoss) {
                plAtt.fightMabu.changePoint((byte) 5);
            }
        } catch (Exception e) {
            Logger.logException(SkillService.class, e);
        }
    }

    private void playerAttackMob(Player plAtt, Mob mob, boolean miss, boolean dieWhenHpFull) {
        if (!mob.isDie()) {
            double dameHit = plAtt.nPoint.getDameAttackDouble(true);
            if (plAtt.charms.tdBatTu > System.currentTimeMillis() && plAtt.nPoint.hp == 1) {
                dameHit = 0;
            }
            if (plAtt.charms.tdManhMe > System.currentTimeMillis()) {
                dameHit += (dameHit * 150 / 100);
            }
            if (plAtt.isPet) {
                if (((Pet) plAtt).charms.tdDeTu > System.currentTimeMillis()) {
                    dameHit *= 2;
                }
            }
            if (miss) {
                dameHit = 0;
            }
            hutHPMP(plAtt, dameHit, true);
            sendPlayerAttackMob(plAtt, mob);
            mob.injured(plAtt, dameHit, dieWhenHpFull);
        }
    }

    public void sendPlayerPrepareSkill(Player player, int affterMiliseconds) {
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(4);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(player.playerSkill.skillSelect.skillId);
            msg.writer().writeShort(affterMiliseconds);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendPlayerPrepareBom(Player player, int affterMiliseconds) {
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(7);
            msg.writer().writeInt((int) player.id);
            // msg.writer().writeShort(player.playerSkill.skillSelect.skillId);
            msg.writer().writeShort(104);
            msg.writer().writeShort(affterMiliseconds);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public boolean canUseSkillWithMana(Player player) {
        if (player.playerSkill.skillSelect != null) {
            if (player.playerSkill.skillSelect.template.id == Skill.KAIOKEN) {
                long hpUse = (long) (player.nPoint.hpMax / 100 * 10);
                if (player.nPoint.hp <= hpUse) {
                    return false;
                }
            }
            switch (player.playerSkill.skillSelect.template.manaUseType) {
                case 0:
                    if (player.nPoint.mp >= player.playerSkill.skillSelect.manaUse) {
                        return true;
                    } else {
                        return false;
                    }
                case 1:
                    int mpUse = (int) (player.nPoint.mpMax * player.playerSkill.skillSelect.manaUse / 100);
                    if (player.nPoint.mp >= mpUse) {
                        return true;
                    } else {
                        return false;
                    }
                case 2:
                    if (player.nPoint.mp > 0) {
                        return true;
                    } else {
                        return false;
                    }
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public boolean canUseSkillWithCooldown(Player player) {
        return Util.canDoWithTime(player.playerSkill.skillSelect.lastTimeUseThisSkill,
                player.playerSkill.skillSelect.coolDown - 50);
    }

    private void affterUseSkill(Player player, int skillId) {
        Intrinsic intrinsic = player.playerIntrinsic.intrinsic;
        switch (skillId) {
            case Skill.DICH_CHUYEN_TUC_THOI:
                if (intrinsic.id == 6) {
                    player.nPoint.dameAfter = intrinsic.param1;
                }
                break;
            case Skill.THOI_MIEN:
                if (intrinsic.id == 7) {
                    player.nPoint.dameAfter = intrinsic.param1;
                }
                break;
            case Skill.SOCOLA:
                if (intrinsic.id == 14) {
                    player.nPoint.dameAfter = intrinsic.param1;
                }
                break;
            case Skill.TROI:
                if (intrinsic.id == 22) {
                    player.nPoint.dameAfter = intrinsic.param1;
                }
                break;
        }
        setMpAffterUseSkill(player);
        setLastTimeUseSkill(player, skillId);
    }

    private void setMpAffterUseSkill(Player player) {
        if (player.playerSkill.skillSelect != null) {
            switch (player.playerSkill.skillSelect.template.manaUseType) {
                case 0:
                    if (player.nPoint.mp >= player.playerSkill.skillSelect.manaUse) {
                        player.nPoint.setMp((long) (player.nPoint.mp - player.playerSkill.skillSelect.manaUse));
                    }
                    break;
                case 1:
                    int mpUse = (int) (player.nPoint.mpMax * player.playerSkill.skillSelect.manaUse / 100);
                    if (player.nPoint.mp >= mpUse) {
                        player.nPoint.setMp((long) (player.nPoint.mp - mpUse));
                    }
                    break;
                case 2:
                    player.nPoint.setMp(0);
                    break;
            }
            PlayerService.gI().sendInfoHpMpMoney(player);
        }
    }

    private void setLastTimeUseSkill(Player player, int skillId) {
        Intrinsic intrinsic = player.playerIntrinsic.intrinsic;
        int subTimeParam = 0;
        switch (skillId) {
            case Skill.TRI_THUONG:
                if (intrinsic.id == 10) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.THAI_DUONG_HA_SAN:
                if (intrinsic.id == 3) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.QUA_CAU_KENH_KHI:
                if (intrinsic.id == 4) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.KHIEN_NANG_LUONG:
                if (intrinsic.id == 5 || intrinsic.id == 15 || intrinsic.id == 20) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.MAKANKOSAPPO:
                if (intrinsic.id == 11) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.DE_TRUNG:
                if (intrinsic.id == 12) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.TU_SAT:
                if (intrinsic.id == 19) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.HUYT_SAO:
                if (intrinsic.id == 21) {
                    subTimeParam = intrinsic.param1;
                }
                break;
        }
        int coolDown = player.playerSkill.skillSelect.coolDown;
        player.playerSkill.skillSelect.lastTimeUseThisSkill = System.currentTimeMillis()
                - (coolDown * subTimeParam / 100);
        if (subTimeParam != 0) {
            Service.getInstance().sendTimeSkill(player);
        }
    }

    private boolean canHsPlayer(Player player, Player plTarget) {
        if (plTarget == null) {
            return false;
        }
        if (plTarget.isBoss) {
            return false;
        }
        if (plTarget.typePk == ConstPlayer.PK_ALL) {
            return false;
        }
        if (plTarget.typePk == ConstPlayer.PK_PVP) {
            return false;
        }
        if (player.cFlag != 0) {
            if (plTarget.cFlag != 0 && plTarget.cFlag != player.cFlag) {
                return false;
            }
        } else if (plTarget.cFlag != 0) {
            return false;
        }
        return true;
    }

    private boolean canAttackPlayer(Player p1, Player p2) {
        if (p1.haveDuongTang || p2.haveDuongTang) {
            return true;
        }
        if (p1.isDie() || p2.isDie()) {
            return false;
        }
        if (p1.zone.map.mapId == 129 && p1.typePk > 0 && p2.typePk > 0) {
            return true;
        }

        if (p1.typePk == ConstPlayer.PK_ALL || p2.typePk == ConstPlayer.PK_ALL) {
            return true;
        }
        if ((p1.cFlag != 0 && p2.cFlag != 0)
                && (p1.cFlag == 8 || p2.cFlag == 8 || p1.cFlag != p2.cFlag)) {
            return true;
        }
        if (p1.pvp == null || p2.pvp == null) {
            return false;
        }
        if (p1.pvp.isInPVP(p2) || p2.pvp.isInPVP(p1)) {
            return true;
        }
        return false;
    }

    private void sendPlayerAttackMob(Player plAtt, Mob mob) {
        Message msg;
        try {
            msg = new Message(54);
            msg.writer().writeInt((int) plAtt.id);
            msg.writer().writeByte(plAtt.playerSkill.skillSelect.skillId);
            msg.writer().writeByte(mob.id);
            Service.getInstance().sendMessAllPlayerInMap(plAtt, msg);
            msg.cleanup();

        } catch (Exception e) {

        }
    }

    public void selectSkill(Player player, int skillId) {
        Skill skillBefore = player.playerSkill.skillSelect;
        for (Skill skill : player.playerSkill.skills) {
            if (skill.skillId != -1 && skill.template.id == skillId) {
                player.playerSkill.skillSelect = skill;
                switch (skillBefore.template.id) {
                    case Skill.DRAGON:
                    case Skill.KAMEJOKO:
                    case Skill.DEMON:
                    case Skill.MASENKO:
                    case Skill.LIEN_HOAN:
                    case Skill.GALICK:
                    case Skill.ANTOMIC:
                        switch (skill.template.id) {
                            case Skill.DRAGON:
                            case Skill.KAMEJOKO:
                            case Skill.DEMON:
                            case Skill.MASENKO:
                            case Skill.LIEN_HOAN:
                            case Skill.GALICK:
                            case Skill.ANTOMIC:
                                // skill.lastTimeUseThisSkill = System.currentTimeMillis() + (skill.coolDown /
                                // 100);
                                break;
                        }
                        break;
                    case Skill.PHAN_THAN:
                        startSkillClone(player);
                        break;
                }
                break;
            }
        }
    }
}
