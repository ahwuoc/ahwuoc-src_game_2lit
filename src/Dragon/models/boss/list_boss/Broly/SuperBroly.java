package Dragon.models.boss.list_boss.Broly;

import Dragon.consts.ConstPlayer;
import Dragon.models.boss.Boss;
import Dragon.models.boss.BossData;
import Dragon.models.boss.BossID;
import Dragon.models.boss.BossManager;
import Dragon.models.map.Zone;
import Dragon.models.player.Player;
import Dragon.models.skill.Skill;
import Dragon.services.PetService;
import Dragon.utils.Util;
import Dragon.services.EffectSkillService;
import Dragon.services.Service;

public class SuperBroly extends Boss {

    private long lastUpdate = System.currentTimeMillis();
    private long timeJoinMap;
    protected Player playerAtt;
    private int timeLive = 200000000;
    public int petgender = Util.nextInt(0, 2);
    public Player mypett;

    public SuperBroly(BossData bossData) throws Exception {
        super(Util.randomBossId(), bossData);
    }

    public SuperBroly(Zone zone, long dame, long hp) throws Exception {
        super(Util.randomBossId(), new BossData(
                "Super Broly", //name
                ConstPlayer.TRAI_DAT, //gender
                new short[]{294, 295, 296, 28, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
                ((50000 + dame)), //dame
                new long[]{((16000000 + hp))}, //hp
                new int[]{zone.map.mapId}, //map join
                new int[][]{
                    {Skill.LIEN_HOAN, 7, 2000},
                    {Skill.DRAGON, 7, 2000},
                    {Skill.KAMEJOKO, 7, 2000},
                    {Skill.MASENKO, 7, 2000},
                    {Skill.ANTOMIC, 7, 2000},
                    {Skill.TAI_TAO_NANG_LUONG, 1, 15000},},
                new String[]{
                    "|-1|Gaaaaaa",
                    "|-2|Tới đây đi!"
                }, //text chat 1
                new String[]{"|-1|Các ngươi tới số rồi mới gặp phải ta",
                    "|-1|Gaaaaaa",
                    "|-2|Không ngờ..Hắn mạnh cỡ này sao..!!"
                }, //text chat 2
                new String[]{"|-1|Gaaaaaaaa!!!"}, //text chat 3
                1_000_000
        ));
        this.zone = zone;
    }

    @Override
    public void reward(Player plKill) {
        if (plKill.pet != null) {
            return;
        }
        if (plKill.pet == null) {
            PetService.gI().createNormalPet(plKill, petgender);
        }
        this.pet = null;
    }

    @Override
    public void active() {
        super.active();
        if (this.pet == null) {
            PetService.gI().createNormalPet(this, petgender);
            this.pet.nPoint.tlNeDon = 20000000;
        }
    }

    @Override
    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage / 2);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                if (damage > this.nPoint.hpMax * 0.3) {
                    damage = (long) (this.nPoint.hpMax * 3 / 10);
                }
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.pet.dispose();
                this.pet = null;
                this.setDie(plAtt);
                die(plAtt);
                BossManager.gI().createBoss(BossID.BROLY_THUONG);
            }
            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        this.dispose();
    }

    @Override
    public void joinMap() {
        super.joinMap();
    }

}
