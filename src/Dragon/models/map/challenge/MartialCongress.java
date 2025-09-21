package Dragon.models.map.challenge;

import Dragon.models.boss.dhvt.ChaPa;
import Dragon.models.boss.dhvt.ChanXu;
import Dragon.models.boss.dhvt.JackyChun;
import Dragon.models.boss.dhvt.LiuLiu;
import Dragon.models.boss.dhvt.ODo;
import Dragon.models.boss.dhvt.PonPut;
import Dragon.models.boss.dhvt.SoiHecQuyn;
import Dragon.models.boss.dhvt.TauPayPay;
import Dragon.models.boss.dhvt.ThienXinHang;
import Dragon.models.boss.dhvt.ThienXinHangClone;
import Dragon.models.boss.dhvt.Xinbato;
import Dragon.models.boss.dhvt.Yamcha;
import Dragon.consts.ConstPlayer;
import Dragon.models.boss.Boss;
import Dragon.models.boss.BossStatus;
import Dragon.models.player.Player;
import Dragon.services.EffectSkillService;
import Dragon.services.ItemTimeService;
import Dragon.services.PlayerService;
import Dragon.services.Service;
import Dragon.services.func.ChangeMapService;
import Dragon.utils.Util;

/**
 * @author Duy Béo
 */
public class MartialCongress {

    private Player player;

    private Boss boss; // Không dùng @Setter vì đã có setter thủ công
    private int time;  // Không dùng @Setter vì đã có setter thủ công
    private int timeWait; // Không dùng @Setter vì đã có setter thủ công

    private Player npc;

    private int round;

    public void update() {
        if (time > 0) {
            time--;
            if (player.isDie()) {
                die();
                return;
            }
            if (player.location != null && !player.isDie() && player != null && player.zone != null) {
                if (boss.isDie()) {
                    round++;
                    boss.leaveMap();
                    toTheNextRound();
                }
                if (player.location.y > 264) {
                    leave();
                }
            } else {
//                endChallenge();
                if (boss != null) {
                    boss.leaveMap();
                }
                MartialCongressManager.gI().remove(this);
            }

        } else {
            timeOut();
        }
        if (timeWait > 0) {
            switch (timeWait) {
                case 10:
                    Service.getInstance().chat(npc, "Trận đấu giữa " + player.name + " VS " + boss.name + " sắp diễn ra");
                    ready();
                    break;
                case 8:
                    Service.getInstance().chat(npc, "Xin quý vị khán giả cho 1 tràng pháo tay để cổ vũ cho 2 đối thủ nào");
                    break;
                case 4:
                    Service.getInstance().chat(npc, "Mọi người ngồi sau hãy ổn định chỗ ngồi,trận đấu sẽ bắt đầu sau 3 giây nữa");
                    break;
                case 2:
                    Service.getInstance().chat(npc, "Trận đấu bắt đầu");
                    break;
                case 1:
                    Service.getInstance().chat(player, "Ok");
                    Service.getInstance().chat(boss, "Ok");
                    break;
            }
            timeWait--;
        }
    }

    public void ready() {
        EffectSkillService.gI().startStun(boss, System.currentTimeMillis(), 10000);
        EffectSkillService.gI().startStun(player, System.currentTimeMillis(), 10000);
        ItemTimeService.gI().sendItemTime(player, 3779, 10000 / 1000);
        Util.setTimeout(() -> {
            if (boss.effectSkill != null) {
                EffectSkillService.gI().removeStun(boss);
            }
            MartialCongressService.gI().sendTypePK(player, boss);
            PlayerService.gI().changeAndSendTypePK(this.player, ConstPlayer.PK_PVP);
            boss.changeStatus(BossStatus.ACTIVE);
        }, 10000);
    }

    public void toTheNextRound() {
        try {
            PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.NON_PK);
            Boss bss = null;
            switch (round) {
                case 0:
                    bss = new SoiHecQuyn(player);
                    break;
                case 1:
                    bss = new ODo(player);
                    break;
                case 2:
                    bss = new Xinbato(player);
                    break;
                case 3:
                    bss = new ChaPa(player);
                    break;
                case 4:
                    bss = new PonPut(player);
                    break;
                case 5:
                    bss = new ChanXu(player);
                    break;
                case 6:
                    bss = new TauPayPay(player);
                    break;
                case 7:
                    bss = new Yamcha(player);
                    break;
                case 8:
                    bss = new JackyChun(player);
                    break;
                case 9:
                    bss = new ThienXinHang(player);
                    break;
                case 10:
                    bss = new LiuLiu(player);
                    break;
                default:
                    champion();
                    return;
            }
            MartialCongressService.gI().moveFast(player, 335, 264);
            Service.gI().hsChar(player, player.nPoint.hpMax, player.nPoint.mpMax);
            setTimeWait(11);
            setBoss(bss);
            setTime(185);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (round > 0 && round < 11) {
//            bss.joinMap();
//        }

    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setTimeWait(int timeWait) {
        this.timeWait = timeWait;
    }

    // Getters that were previously provided by Lombok @Getter
    public Player getPlayer() {
        return player;
    }

    public Boss getBoss() {
        return boss;
    }

    public int getTime() {
        return time;
    }

    public int getTimeWait() {
        return timeWait;
    }

    public Player getNpc() {
        return npc;
    }

    public int getRound() {
        return round;
    }

    // Setters for fields that previously used @Setter
    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setNpc(Player npc) {
        this.npc = npc;
    }

    private void die() {
        Service.getInstance().sendThongBao(player, "Bạn bị xử thua vì chết queo");
        if (player.zone != null) {
            endChallenge();
        }
    }

    private void timeOut() {
        Service.getInstance().sendThongBao(player, "Bạn bị xử thua vì hết thời gian");
        endChallenge();
    }

    private void champion() {
        Service.getInstance().sendThongBao(player, "Chúc mừng " + player.name + " vừa đoạt giải vô địch");
        endChallenge();
    }

    public void leave() {
        setTime(0);
        EffectSkillService.gI().removeStun(player);
        Service.getInstance().sendThongBao(player, "Bạn bị xử thua vì rời khỏi võ đài");
        endChallenge();
    }

    private void reward() {
        if (player.levelWoodChest < round) {
            player.levelWoodChest = round;
        }
    }

    public void endChallenge() {
        reward();
        if (player.zone != null) {
            PlayerService.gI().hoiSinh(player);
        }
        PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.NON_PK);
        if (player != null && player.zone != null && player.zone.map.mapId == 129) {
            Util.setTimeout(() -> {
                ChangeMapService.gI().changeMapNonSpaceship(player, 129, player.location.x, 360);
            }, 500);
        }
        if (boss != null) {
            boss.leaveMap();
        }
        MartialCongressManager.gI().remove(this);
    }
}
