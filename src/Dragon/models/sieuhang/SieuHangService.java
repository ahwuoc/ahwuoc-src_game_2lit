package Dragon.models.sieuhang;

import com.girlkun.network.io.Message;
import Dragon.jdbc.daos.GodGK;
import Dragon.models.boss.BossData;
import Dragon.models.map.Zone;
import Dragon.models.player.Player;
import Dragon.models.skill.Skill;
import Dragon.services.MapService;
import Dragon.services.Service;
import Dragon.services.func.ChangeMapService;
import Dragon.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Béo Mập :3
 */
public class SieuHangService {

    private static SieuHangService i;

    public static SieuHangService gI() {
        if (i == null) {
            i = new SieuHangService();
        }
        return i;
    }

    public boolean checkDangThiDau(Player player1, Player player2) {
        for (SieuHang sieuHang : SieuHangManager.gI().list) {
            if (player1.name.equals(sieuHang.getPlayer().name) || player1.name.equals(sieuHang.getBoss().name) || player2.name.equals(sieuHang.getPlayer().name) || player2.name.equals(sieuHang.getBoss().name)) {
                return true;
            }
        }
        return false;
    }

    public void startChallenge(Player player, int id) throws Exception {
        if (player != null && player.rankSieuHang == 1) {
            Service.gI().sendThongBao(player, "Top 1 rồi đánh gì nữa cha");
            return;
        }
        if (player != null && player.zone.map.mapId != 113) {
            return;
        }
        Player pl = GodGK.loadByIdSieuHang(id);
        if (pl != null) {
            pl.nPoint.calPoint();
            if (Math.abs(player.rankSieuHang - pl.rankSieuHang) > 30) {
                Service.gI().sendThongBao(player, "Không thể thách đấu đối thủ cách quá 30 hạng");
                return;
            }
            if (checkDangThiDau(player, pl)) {
                Service.gI().sendThongBao(player, "Đối thủ đang thi đấu");
                return;
            }
            List<Skill> skillList = new ArrayList<>();
            for (byte i = 0; i < pl.playerSkill.skills.size(); i++) {
                Skill skill = pl.playerSkill.skills.get(i);
                if (skill.point > 0) {
                    skillList.add(skill);
                }
            }
            int[][] skillTemp = new int[skillList.size()][5];
            for (byte i = 0; i < skillList.size(); i++) {
                Skill skill = skillList.get(i);
                if (skill.point > 0) {
                    skillTemp[i][0] = skill.template.id;
                    skillTemp[i][1] = skill.point;
                    skillTemp[i][2] = skill.coolDown;
                }
            }

            BossData data = new BossData(
                    pl.name,
                    pl.gender,
                    new short[]{pl.getHead(), pl.getBody(), pl.getLeg(), pl.getFlagBag(), pl.getAura(), pl.getEffFront()},
                    (long) pl.nPoint.dame,
                    new long[]{(long) pl.nPoint.hpMax},
                    new int[]{140},
                    skillTemp,
                    new String[]{}, //text chat 1
                    new String[]{}, //text chat 2
                    new String[]{}, //text chat 3
                    5
            );

            ClonePlayer boss = new ClonePlayer(player, data, (int) pl.id);
            boss.rankSieuHang = pl.rankSieuHang;
            Zone zone = getMapChalllenge(113);
            if (zone != null) {
                ChangeMapService.gI().changeMap(player, zone, player.location.x, 360);
                Util.setTimeout(() -> {
                    SieuHang mc = new SieuHang();
                    mc.setPlayer(player);
                    mc.toTheNextRound(boss);
                    SieuHangManager.gI().add(mc);
                }, 500);
            }
        }
    }

    public void moveFast(Player pl, int x, int y) {
        Message msg;
        try {
            msg = new Message(58);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            msg.writer().writeInt((int) pl.id);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendTypePK(Player player, Player boss) {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 35);
            msg.writer().writeInt((int) boss.id);
            msg.writer().writeByte(3);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public Zone getMapChalllenge(int mapId) {
        Zone map = MapService.gI().getMapWithRandZone(mapId);
        if (map.getNumOfBosses() < 1) {
            return map;
        }
        return null;
    }
}
