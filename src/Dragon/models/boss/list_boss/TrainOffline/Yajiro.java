package Dragon.models.boss.list_boss.TrainOffline;

import Dragon.consts.ConstPlayer;
import Dragon.models.boss.*;
import Dragon.models.map.ItemMap;
import Dragon.models.map.Zone;
import Dragon.models.player.Player;
import Dragon.server.ServerNotify;
import Dragon.services.Service;
import Dragon.services.SkillService;
import Dragon.services.func.ChangeMapService;
import Dragon.utils.SkillUtil;
import Dragon.utils.Util;

/**
 * @Stole By MITCHIKEN ZALO 0358689793
 */
public class Yajiro extends Boss {

    public Yajiro() throws Exception {
        super(BossID.YARI, BossesData.YAJIRO);
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(5, 100)) { // 5% cơ hội rơi item
            int[] itemIds = {1744, 1745, 1746}; // Danh sách 3 item có thể rơi
            int randomIndex = Util.nextInt(0, itemIds.length - 1); // Chọn ngẫu nhiên 1 item trong 3
            int itemId = itemIds[randomIndex]; // Lấy ID item ngẫu nhiên

            Service.gI().dropItemMap(this.zone, new ItemMap(zone, itemId, 2, this.location.x, this.location.y, plKill.id));
        } else {
            // Nếu không rơi 1 trong 3 item trên, thực hiện rơi item trong else
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, Util.nextInt(16, 20), 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }
    }

    @Override
    public void active() {

        attack();
    }

    @Override
    public void checkPlayerDie(Player player) {
        if (player.isDie()) {
            this.chat("Quá gà");
            ChangeMapService.gI().changeMapYardrat(this, this.zone, 322, 408);
            this.changeToTypeNonPK();
            player.rsfight();
        }

    }

    @Override
    public void joinMapByZone(Zone zone) {
        if (zone != null) {
            this.zone = zone;
            ChangeMapService.gI().changeMapYardrat(this, this.zone, 322, 408);
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.zone.getNumOfPlayers() < 1) {
            this.changeToTypeNonPK();
            ChangeMapService.gI().changeMapYardrat(this, this.zone, 322, 408);
            nPoint.setFullHpMp();
        }
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100)) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl.isDie() || pl.isfake) {
                    if (pl == null) {
                        this.changeToTypeNonPK();
                        return;
                    }
                    return;
                }
                if (pl.istry || pl.isfight) {
                    if (pl.playerTask.taskMain.id != 5 && pl.playerTask.taskMain.index != 5) {
                        this.changeToTypePK();
                    }
                    if (this.isDie()) {
                        Service.gI().hsChar(this, this.nPoint.hpMax, this.nPoint.mpMax);
                    }
                }
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70));
                        } else {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50));
                        }
                    }
                    SkillService.gI().useSkill(this, pl, null, null);
                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
//                Logger.logException(Boss.class, ex);
            }
        }
    }

    @Override
    public Player getPlayerAttack() {
        if (this.playerTarger != null && (this.playerTarger.isDie() || !this.zone.equals(this.playerTarger.zone))) {
            this.playerTarger = null;
        }
        if (this.playerTarger == null || Util.canDoWithTime(this.lastTimeTargetPlayer, this.timeTargetPlayer)) {
            this.playerTarger = this.zone.getplayertrain();
            this.lastTimeTargetPlayer = System.currentTimeMillis();
        }
        return this.playerTarger;
    }

    @Override
    public void die(Player plKill) {
        if (plKill != null) {
            reward(plKill);
        }
        this.playerkill.rsfight();
    }

    @Override
    public void leaveMap() {
    }
}
