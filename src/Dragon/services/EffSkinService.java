/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dragon.services;

import Dragon.models.player.Player;
import Dragon.utils.Logger;
import com.girlkun.network.io.Message;

/**
 *
 * @author Administrator
 */
public class EffSkinService {

    public static final byte TURN_ON_EFFECT = 1;
    public static final byte TURN_OFF_EFFECT = 0;
    public static final byte TURN_OFF_ALL_EFFECT = 2;
    public static final byte BLIND_EFFECT = 40;
    public static final byte SLEEP_EFFECT = 41;
    public static final byte STONE_EFFECT = 42;
    private static EffSkinService i;

    public static EffSkinService gI() {
        if (i == null) {
            i = new EffSkinService();
        }
        return i;
    }

    public void setHoaDa(Player player, long lastTimeHoaDa, int timeHoaDa) {
        player.effectSkin.isHoaDa = true;
        player.effectSkin.lastTimeHoaDa = lastTimeHoaDa;
        player.effectSkin.timeHoaDa = timeHoaDa;
    }

    public void removeHoaDa(Player player) {
        player.effectSkin.isHoaDa = false;
        Service.getInstance().Send_Caitrang(player);
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, STONE_EFFECT);
    }

    public void sendEffectPlayer(Player plUseSkill, Player plTarget, byte toggle, byte effect) {
        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(toggle); //0: hủy hiệu ứng, 1: bắt đầu hiệu ứng
            msg.writer().writeByte(0); //0: vào phần phayer, 1: vào phần mob
            if (toggle == TURN_OFF_ALL_EFFECT) {
                msg.writer().writeInt((int) plTarget.id);
            } else {
                msg.writer().writeByte(effect); //loại hiệu ứng
                msg.writer().writeInt((int) plTarget.id); //id player dính effect
                msg.writer().writeInt((int) plUseSkill.id); //id player dùng skill
            }
            Service.getInstance().sendMessAllPlayerInMap(plUseSkill, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(EffectSkillService.class, e);
        }
    }
}
