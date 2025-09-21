package Dragon.Bot;

import Dragon.services.Service;
import Dragon.models.map.Zone;
import Dragon.models.player.*;
import Dragon.services.SkillService;
import Dragon.models.mob.Mob;
import Dragon.services.PlayerService;
import Dragon.utils.Util;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Mobb {

    private Mob mAttack;
    private static final Random RANDOM = new Random(); // Sử dụng static Random

    public long lastTimeChanM;

    public Bot bot;

    public Mobb(Bot b) {
        this.bot = b;
    }

    public void update() {
        this.Attack();
        this.chanGeMap();
    }

    public void GetMobAttack() {
        if (this.bot.zone.mobs.size() >= 1) {
            if (this.mAttack == null || this.mAttack.isDie()) {
                mAttack = this.bot.zone.mobs.get(RANDOM.nextInt(this.bot.zone.mobs.size()));
            }
        }
    }

    public void Attack() {
        this.GetMobAttack();
        if (Util.isTrue(50, 100)) {
            this.bot.playerSkill.skillSelect = this.bot.playerSkill.skills.get(0);
        } else {
            this.bot.playerSkill.skillSelect = this.bot.playerSkill.skills.get(1);
        }
        if (this.mAttack != null) {
            if (this.bot.UseLastTimeSkill()) {
                PlayerService.gI().playerMove(this.bot, this.mAttack.location.x, this.mAttack.location.y);
                SkillService.gI().useSkill(this.bot, null, this.mAttack, null);
            }
        }
    }

    public void chanGeMap() {
        if (this.lastTimeChanM < ((System.currentTimeMillis() - 150000) - RANDOM.nextInt(150000))) {
            this.bot.joinMap();
        }
    }

}
