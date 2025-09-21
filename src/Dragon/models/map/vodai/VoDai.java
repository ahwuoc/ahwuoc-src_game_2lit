package Dragon.models.map.vodai;

import Dragon.models.boss.vodai.Bongbang;
import Dragon.models.boss.vodai.Nguoivohinh;
import Dragon.models.boss.vodai.Satan;
import Dragon.models.boss.vodai.Thodaubac;
import Dragon.models.boss.vodai.Dracula;
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
 * @author Bùi Kim Trường
 */
public class VoDai {

    private Player player;

    private Boss boss; // Không dùng @Setter vì đã có setter thủ công

    private Player npc;

    private int time;  // Không dùng @Setter vì đã có setter thủ công
    private int round;

    private int timeWait;

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
                if (player.location.y > 360) {
                    leave();
                }
            } else {
                if (boss != null) {
                    boss.leaveMap();
                }
                VoDaiManager.gI().remove(this);
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
            VoDaiService.gI().sendTypePK(player, boss);
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
                    bss = new Dracula(player);
                    break;
                case 1:
                    bss = new Nguoivohinh(player);
                    break;
                case 2:
                    bss = new Bongbang(player);
                    break;
                case 3:
                    bss = new Satan(player);
                    break;
                case 4:
                    bss = new Thodaubac(player);
                    break;
                default:
                    champion();
                    return;
            }
            VoDaiService.gI().moveFast(player, 386, 336);
            Service.gI().hsChar(player, player.nPoint.hpMax, player.nPoint.mpMax);
            setTimeWait(11);
            setBoss(bss);
            setTime(185);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    // Getters previously provided by Lombok
    public Player getPlayer() {
        return player;
    }

    public Boss getBoss() {
        return boss;
    }

    public Player getNpc() {
        return npc;
    }

    public int getTime() {
        return time;
    }

    public int getRound() {
        return round;
    }

    public int getTimeWait() {
        return timeWait;
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
        if (player != null && player.zone != null && player.zone.map.mapId == 112) {
            Util.setTimeout(() -> {
                ChangeMapService.gI().changeMapNonSpaceship(player, 112, player.location.x, 336);
            }, 500);
        }
        if (boss != null) {
            boss.leaveMap();
        }
        VoDaiManager.gI().remove(this);
    }
}
