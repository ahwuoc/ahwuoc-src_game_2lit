/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dragon.models.boss.list_boss;

import Dragon.consts.ConstPlayer;
import Dragon.models.boss.*;
import Dragon.models.item.Item;
import Dragon.models.map.ItemMap;
import Dragon.models.map.Zone;
import Dragon.models.player.Player;
import Dragon.server.Client;
import Dragon.services.EffectSkillService;
import Dragon.services.InventoryServiceNew;
import Dragon.services.ItemService;
import Dragon.services.MapService;
import Dragon.services.Service;
import Dragon.services.func.ChangeMapService;
import Dragon.utils.Util;
import Dragon.models.player.Inventory;

/**
 *
 * @author Administrator
 */
public class HoTong extends Boss {

    public HoTong(int bossID, BossData bossData, Zone zone, int x, int y) throws Exception {
        super(bossID, bossData);
        this.zone = zone;
        this.location.x = x;
        this.location.y = y;
    }
    long lasttimemove;

    @Override
    public void reward(Player plKill) {
        ItemMap it = new ItemMap(this.zone, 1132, 2, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                this.location.y - 24), plKill.id);
        Service.getInstance().dropItemMap(this.zone, it);
    }

    @Override
    public void active() {
        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }
        if (this.playerTarger != null && Client.gI().getPlayer(this.playerTarger.id) == null) {
            playerTarger.haveDuongTang = false;
            this.leaveMap();
        }
        if (Util.getDistance(playerTarger, this) > 500 && this.zone == this.playerTarger.zone) {
            Service.gI().sendThongBao(this.playerTarger, "Đi quá xa ,  Thỏ Ngọc  đã rời đi ! ");
            playerTarger.haveDuongTang = false;
            this.leaveMap();
        }
        if (Util.getDistance(playerTarger, this) > 300 && this.zone == this.playerTarger.zone) {
            Service.gI().sendThongBao(this.playerTarger, "Khoảng cách qua xa, Thỏ Ngọc sắp rời xa bạn!! ");
        }
        if (this.playerTarger != null && Util.getDistance(playerTarger, this) <= 300) {
            int dir = this.location.x - this.playerTarger.location.x <= 0 ? -1 : 1;
            if (Util.canDoWithTime(lasttimemove, 1000)) {
                lasttimemove = System.currentTimeMillis();
                this.moveTo(this.playerTarger.location.x + Util.nextInt(dir == -1 ? 0 : -30, dir == -1 ? 10 : 0), this.playerTarger.location.y);
            }
        }
        if (this.playerTarger != null && playerTarger.haveDuongTang && this.zone.map.mapId == this.mapCongDuc) { // xử lý khi đến map muốn đến
            playerTarger.haveDuongTang = false;
            int a = 50;
            for (int i = 0; i < 5; i++) {
                ItemMap it1 = new ItemMap(this.zone, Util.nextInt(1509, 1511), 1, this.location.x + a, this.zone.map.yPhysicInTop(this.location.x,
                        this.location.y - 24), playerTarger.id);
                Service.getInstance().dropItemMap(this.zone, it1);
                a += 20;
            }
            playerTarger.point_vnd++;
            Service.getInstance().sendMoney(playerTarger);
            this.leaveMap();
        }
        if (this.playerTarger != null && this.zone != null && this.zone.map.mapId != this.playerTarger.zone.map.mapId) {
            ChangeMapService.gI().changeMap(this, this.playerTarger.zone, this.playerTarger.location.x, this.playerTarger.location.y);
        }
        if (Util.canDoWithTime(this.lastTimeAttack, 10000)) {
            Service.gI().chat(this, playerTarger.name + ", Bạn Hãy Đưa Tôi Tới Cung Trăng " + MapService.gI().getMapById(this.mapCongDuc).mapName);
            this.lastTimeAttack = System.currentTimeMillis();
        }
    }

    @Override
    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage;
            }
            if (plAtt != this.playerTarger) {
                damage = (long) (this.nPoint.hpMax / 120);
            } else {
                damage = 0;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void joinMap() {
        if (zoneFinal != null) {
            joinMapByZone(zoneFinal);
            return;
        }
        if (this.zone == null) {
            if (this.parentBoss != null) {
                this.zone = parentBoss.zone;
            } else if (this.lastZone == null) {
                this.zone = getMapJoin();
            } else {
                this.zone = this.lastZone;
            }
        }
        if (this.zone != null) {
            if (this.currentLevel == 0) {
                if (this.parentBoss == null) {
                    ChangeMapService.gI().changeMap(this, this.zone, this.location.x, this.location.y);
                } else {
                    ChangeMapService.gI().changeMap(this, this.zone, this.location.x, this.location.y);;
                }
            } else {
                ChangeMapService.gI().changeMap(this, this.zone, this.location.x, this.location.y);
            }
            Service.getInstance().sendFlagBag(this);
        }
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        this.dispose();
    }
}
