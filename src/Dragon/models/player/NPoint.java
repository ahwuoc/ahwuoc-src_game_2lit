package Dragon.models.player;

import Dragon.De2.Thu_TrieuHoi;
import Dragon.card.Card;
import Dragon.card.OptionCard;
import Dragon.consts.ConstNpc;
import Dragon.consts.ConstPlayer;
import Dragon.consts.ConstRatio;
import Dragon.models.boss.BossID;
import Dragon.models.intrinsic.Intrinsic;
import Dragon.models.item.Item;
import Dragon.models.npc.Npc;
import Dragon.models.skill.Skill;
import Dragon.server.Manager;
import Dragon.services.EffectSkillService;
import Dragon.services.InventoryServiceNew;
import Dragon.services.ItemService;
import Dragon.services.MapService;
import Dragon.services.PlayerService;
import Dragon.services.Service;
import Dragon.services.TaskService;
import Dragon.utils.Logger;
import Dragon.utils.SkillUtil;
import Dragon.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class NPoint {

    public static final byte MAX_LIMIT = 1;

    private Player player;

    public NPoint(Player player) {
        this.player = player;
        this.tlHp = new ArrayList<>();
        this.tlMp = new ArrayList<>();
        this.tlDef = new ArrayList<>();
        this.tlDame = new ArrayList<>();
        this.tlDameAttMob = new ArrayList<>();
        this.tlSDDep = new ArrayList<>();
        this.tlTNSM = new ArrayList<>();
        this.tlDameCrit = new ArrayList<>();
    }

    public boolean wearingThodaica;

    public boolean isCrit;
    public boolean isCrit100;

    private Intrinsic intrinsic;
    private int percentDameIntrinsic;
    public int dameAfter;

    /*-----------------------Chỉ số cơ bản------------------------------------*/
    public byte numAttack;
    public short stamina, maxStamina;

    public byte limitPower;
    public double power;
    public double tiemNang;

    public double hp, hpMax, hpg;
    public double mp, mpMax, mpg;
    public double dame, dameg;
    public double def, defg;
    public int crit, critg;
    public byte speed = 5;

    public boolean teleport;

    public boolean isDraburaFrost; // Cải trang Dracula Frost
    public boolean isDrabura; // Cải trang Dracula Frost
    public boolean isThoDaiCa; // Cải trang Dracula Frost
    public boolean khangTDHS;
    public boolean khangTM;

    /**
     * Chỉ số cộng thêm
     */
    public double hpAdd, mpAdd, dameAdd, defAdd, critAdd, hpHoiAdd, mpHoiAdd;

    /**
     *
     *
     * //+#% sức đánh chí mạng
     */
    public short satThuongBom;
    public List<Integer> tlDameCrit;

    /**
     * Tỉ lệ hp, mp cộng thêm
     */
    public List<Integer> tlHp, tlMp;

    /**
     * Tỉ lệ giáp cộng thêm
     */
    public List<Integer> tlDef;

    /**
     * Tỉ lệ sức đánh/ sức đánh khi đánh quái
     */
    public List<Integer> tlDame, tlDameAttMob;

    /**
     * Lượng hp, mp hồi mỗi 30s, mp hồi cho người khác
     */
    public double hpHoi, mpHoi, mpHoiCute;

    /**
     * Tỉ lệ hp, mp hồi cộng thêm
     */
    public short tlHpHoi, tlMpHoi;

    /**
     * Tỉ lệ hp, mp hồi bản thân và đồng đội cộng thêm
     */
    public short tlHpHoiBanThanVaDongDoi, tlMpHoiBanThanVaDongDoi;

    /**
     * Tỉ lệ hút hp, mp khi đánh, hp khi đánh quái
     */
    public short tlHutHp, tlHutMp, tlHutHpMob;

    /**
     * Tỉ lệ hút hp, mp xung quanh mỗi 5s
     */
    public short tlHutHpMpXQ;

    /**
     * Tỉ lệ phản sát thương
     */
    public short tlPST;

    /**
     * Tỉ lệ tiềm năng sức mạnh
     */
    public List<Integer> tlTNSM;

    /**
     * Tỉ lệ vàng cộng thêm
     */
    public short tlGold;

    /**
     * Tỉ lệ né đòn
     */
    public int tlNeDon;

    /**
     * Tỉ lệ sức đánh đẹp cộng thêm cho bản thân và người xung quanh
     */
    public List<Integer> tlSDDep;

    /**
     * Tỉ lệ giảm sức đánh
     */
    public short tlSubSD;

    public int voHieuChuong;

    /*------------------------Effect skin-------------------------------------*/
    public Item trainArmor;
    public boolean wornTrainArmor;
    public boolean wearingTrainArmor;

    public boolean wearingVoHinh;
    public boolean isKhongLanh;

    public short tlHpGiamODo;
    public short test;

    /*-------------------------------------------------------------------------*/
    /**
     * Tính toán mọi chỉ số sau khi có thay đổi
     */
    public void calPoint() {
        if (this.player.pet != null && this.player.pet.nPoint != null) {
            this.player.pet.nPoint.setPointWhenWearClothes();
        }
        this.setPointWhenWearClothes();
    }

    private void setPointWhenWearClothes() {
        resetPoint();
        if (this.player.rewardBlackBall.timeOutOfDateReward[1] > System.currentTimeMillis()) {
            tlHutMp += RewardBlackBall.R2S_1;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[3] > System.currentTimeMillis()) {
            tlDameAttMob.add(RewardBlackBall.R4S_2);
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[4] > System.currentTimeMillis()) {
            tlPST += RewardBlackBall.R5S_1;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[5] > System.currentTimeMillis()) {
            tlPST += RewardBlackBall.R6S_1;
            tlNeDon += RewardBlackBall.R6S_2;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[6] > System.currentTimeMillis()) {
            tlHpHoi += RewardBlackBall.R7S_1;
            tlHutHp += RewardBlackBall.R7S_2;
        }
        Card card = player.Cards.stream().filter(r -> r != null && r.Used == 1).findFirst().orElse(null);
        if (card != null) {
            for (OptionCard io : card.Options) {
                if (io.active == card.Level || (card.Level == -1 && io.active == 0)) {
                    switch (io.id) {
                        case 0: // Tấn công +#
                            this.dameAdd += io.param;
                            break;
                        case 2: // HP, KI+#000
                            this.hpAdd += io.param * 1000;
                            this.mpAdd += io.param * 1000;
                            break;
                        case 3:// fake
                            this.voHieuChuong += io.param;
                            break;
                        case 5: // +#% sức đánh chí mạng
                            this.tlDameCrit.add(io.param);
                            break;
                        case 6: // HP+#
                            this.hpAdd += io.param;
                            break;
                        case 7: // KI+#
                            this.mpAdd += io.param;
                            break;
                        case 8: // Hút #% HP, KI xung quanh mỗi 5 giây
                            this.tlHutHpMpXQ += io.param;
                            break;
                        case 14: // Chí mạng+#%
                            this.critAdd += io.param;
                            break;
                        case 19: // Tấn công+#% khi đánh quái
                            this.tlDameAttMob.add(io.param);
                            break;
                        case 22: // HP+#K
                            this.hpAdd += io.param * 1000;
                            break;
                        case 23: // MP+#K
                            this.mpAdd += io.param * 1000;
                            break;
                        case 27: // +# HP/30s
                            this.hpHoiAdd += io.param;
                            break;
                        case 28: // +# KI/30s
                            this.mpHoiAdd += io.param;
                            break;
                        case 33: // dịch chuyển tức thời
                            this.teleport = true;
                            break;
                        case 47: // Giáp+#
                            this.defAdd += io.param;
                            break;
                        case 48: // HP/KI+#
                            this.hpAdd += io.param;
                            this.mpAdd += io.param;
                            break;
                        case 49: // Tấn công+#%
                        case 50: // Sức đánh+#%
                            this.tlDame.add(io.param);
                            break;
                        case 77: // HP+#%
                            this.tlHp.add(io.param);
                            break;
                        case 80: // HP+#%/30s
                            this.tlHpHoi += io.param;
                            break;
                        case 81: // MP+#%/30s
                            this.tlMpHoi += io.param;
                            break;
                        case 88: // Cộng #% exp khi đánh quái
                            this.tlTNSM.add(io.param);
                            break;
                        case 94: // Giáp #%
                            this.tlDef.add(io.param);
                            break;
                        case 95: // Biến #% tấn công thành HP
                            this.tlHutHp += io.param;
                            break;
                        case 96: // Biến #% tấn công thành MP
                            this.tlHutMp += io.param;
                            break;
                        case 97: // Phản #% sát thương
                            this.tlPST += io.param;
                            break;
                        case 100: // +#% vàng từ quái
                            this.tlGold += io.param;
                            break;
                        case 101: // +#% TN,SM
                            this.tlTNSM.add(io.param);
                            break;
                        case 103: // KI +#%
                            this.tlMp.add(io.param);
                            break;
                        case 104: // Biến #% tấn công quái thành HP
                            this.tlHutHpMob += io.param;
                            break;
                        case 147: // +#% sức đánh
                            this.tlDame.add(io.param);
                            break;
                        case 198: // Sức đánh+#%
                            this.tlDame.add(io.param);
                            break;
                    }
                }
            }
        }
        this.player.setClothes.worldcup = 0;
        for (Item item : this.player.inventory.itemsBody) {
            if (item.isNotNullItem()) {
                switch (item.template.id) {
                    case 966:
                    case 982:
                    case 983:
                    case 883:
                    case 904:
                        player.setClothes.worldcup++;
                }
                if (item.template.id >= 592 && item.template.id <= 594) {
                    teleport = true;
                }

                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 0: // Tấn công +#
                            this.dameAdd += io.param;
                            break;
                        case 2: // HP, KI+#000
                            this.hpAdd += io.param * 1000;
                            this.mpAdd += io.param * 1000;
                            break;
                        case 3:// fake
                            this.voHieuChuong += io.param;
                            break;
                        case 5: // +#% sức đánh chí mạng
                            this.tlDameCrit.add(io.param);
                            break;
                        case 6: // HP+#
                            this.hpAdd += io.param;
                            break;
                        case 7: // KI+#
                            this.mpAdd += io.param;
                            break;
                        case 8: // Hút #% HP, KI xung quanh mỗi 5 giây
                            this.tlHutHpMpXQ += io.param;
                            break;
                        case 14: // Chí mạng+#%
                            this.critAdd += io.param;
                            break;
                        case 19: // Tấn công+#% khi đánh quái
                            this.tlDameAttMob.add(io.param);
                            break;
                        case 22: // HP+#K
                            this.hpAdd += io.param * 1000;
                            break;
                        case 23: // MP+#K
                            this.mpAdd += io.param * 1000;
                            break;
                        case 27: // +# HP/30s
                            this.hpHoiAdd += io.param;
                            break;
                        case 28: // +# KI/30s
                            this.mpHoiAdd += io.param;
                            break;
                        case 33: // dịch chuyển tức thời
                            this.teleport = true;
                            break;
                        case 47: // Giáp+#
                            this.defAdd += io.param;
                            break;
                        case 48: // HP/KI+#
                            this.hpAdd += io.param;
                            this.mpAdd += io.param;
                            break;
                        case 49: // Tấn công+#%
                        case 50: // Sức đánh+#%
                            this.tlDame.add(io.param);
                            break;
                        case 77: // HP+#%
                            this.tlHp.add(io.param);
                            break;
                        case 80: // HP+#%/30s
                            this.tlHpHoi += io.param;
                            break;
                        case 81: // MP+#%/30s
                            this.tlMpHoi += io.param;
                            break;
                        // Phước làm option cho ván bay phục hồi theo %
                        case 89:
                            this.tlMpHoi += 2;
                            this.tlHpHoi += 2;
                            break;
                        case 88: // Cộng #% exp khi đánh quái
                            this.tlTNSM.add(io.param);
                            break;
                        case 94: // Giáp #%
                            this.tlDef.add(io.param);
                            break;
                        case 95: // Biến #% tấn công thành HP
                            this.tlHutHp += io.param;
                            break;
                        case 96: // Biến #% tấn công thành MP
                            this.tlHutMp += io.param;
                            break;
                        case 97: // Phản #% sát thương
                            this.tlPST += io.param;
                            break;
                        case 100: // +#% vàng từ quái
                            this.tlGold += io.param;
                            break;
                        case 101: // +#% TN,SM
                            this.tlTNSM.add(io.param);
                            break;
                        case 103: // KI +#%
                            this.tlMp.add(io.param);
                            break;
                        case 104: // Biến #% tấn công quái thành HP
                            this.tlHutHpMob += io.param;
                            break;
                        case 105: // Vô hình khi không đánh quái và boss
                            this.wearingVoHinh = true;
                            break;
                        case 106: // Không ảnh hưởng bởi cái lạnh
                            this.isKhongLanh = true;
                            break;
                        case 108: // #% Né đòn
                            this.tlNeDon += io.param;// đối nghịch
                            break;
                        case 109: // Hôi, giảm #% HP
                            this.tlHpGiamODo += io.param;
                            break;
                        case 116: // Kháng thái dương hạ san
                            this.khangTDHS = true;
                            break;
                        case 117: // Đẹp +#% SĐ cho mình và người xung quanh
                            this.tlSDDep.add(io.param);
                            break;
                        case 147: // +#% sức đánh
                            this.tlDame.add(io.param);
                            break;
                        case 75: // Giảm 50% sức đánh, HP, KI và +#% SM, TN, vàng từ quái
                            this.tlSubSD += 50;
                            this.tlTNSM.add(io.param);
                            this.tlGold += io.param;
                            break;
                        case 162: // Cute hồi #% KI/s bản thân và xung quanh
                            this.mpHoiCute += io.param;
                            break;
                        case 173: // Phục hồi #% HP và KI cho đồng đội
                            this.tlHpHoiBanThanVaDongDoi += io.param;
                            this.tlMpHoiBanThanVaDongDoi += io.param;
                            break;
                        case 115: // Thỏ Đại Ca
                            this.wearingThodaica = true;
                            this.player.effectSkin.lastTimeThodaica = System.currentTimeMillis();
                            break;
                        case 26: // Dracula hóa đá
                            this.isDrabura = true;
                            break;
                        case 212: // Dracula Frost
                            this.isDraburaFrost = true;
                            break;
                        case 191: // +#% sát thương bom
                            this.satThuongBom += io.param;
                            break;
                    }
                }
            }
        }
        if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 921) {
                    for (Item.ItemOption io : item.itemOptions) {
                        switch (io.optionTemplate.id) {
                            case 14: // Chí mạng+#%
                                this.critAdd += io.param;
                                break;
                            case 50: // Sức đánh+#%
                                this.tlDame.add(io.param);
                                break;
                            case 77: // HP+#%
                                this.tlHp.add(io.param);
                                break;
                            case 80: // HP+#%/30s
                                this.tlHpHoi += io.param;
                                break;
                            case 81: // MP+#%/30s
                                this.tlMpHoi += io.param;
                                break;
                            case 94: // Giáp #%
                                this.tlDef.add(io.param);
                                break;
                            case 103: // KI +#%
                                this.tlMp.add(io.param);
                                break;
                            case 108: // #% Né đòn
                                this.tlNeDon += io.param;
                                break;
                        }
                    }
                    break;
                }
            }
        } else if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1155) {
                    for (Item.ItemOption io : item.itemOptions) {
                        switch (io.optionTemplate.id) {
                            case 14: // Chí mạng+#%
                                this.critAdd += io.param;
                                break;
                            case 50: // Sức đánh+#%
                                this.tlDame.add(io.param);
                                break;
                            case 77: // HP+#%
                                this.tlHp.add(io.param);
                                break;
                            case 80: // HP+#%/30s
                                this.tlHpHoi += io.param;
                                break;
                            case 81: // MP+#%/30s
                                this.tlMpHoi += io.param;
                                break;
                            case 94: // Giáp #%
                                this.tlDef.add(io.param);
                                break;
                            case 103: // KI +#%
                                this.tlMp.add(io.param);
                                break;
                            case 108: // #% Né đòn
                                this.tlNeDon += io.param;
                                break;
                        }
                    }
                    break;
                }
            }
        } else if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1156) {
                    for (Item.ItemOption io : item.itemOptions) {
                        switch (io.optionTemplate.id) {
                            case 14: // Chí mạng+#%
                                this.critAdd += io.param;
                                break;
                            case 50: // Sức đánh+#%
                                this.tlDame.add(io.param);
                                break;
                            case 77: // HP+#%
                                this.tlHp.add(io.param);
                                break;
                            case 80: // HP+#%/30s
                                this.tlHpHoi += io.param;
                                break;
                            case 81: // MP+#%/30s
                                this.tlMpHoi += io.param;
                                break;
                            case 94: // Giáp #%
                                this.tlDef.add(io.param);
                                break;
                            case 103: // KI +#%
                                this.tlMp.add(io.param);
                                break;
                            case 108: // #% Né đòn
                                this.tlNeDon += io.param;
                                break;
                        }
                    }
                    break;
                }
            }
        }
        setDameTrainArmor();
        setBasePoint();
    }

    private void setDameTrainArmor() {
        if (!this.player.isPet && !this.player.isBoss && !this.player.isTrieuhoipet && !this.player.isClone) {
            if (this.player.inventory.itemsBody.size() < 7) {
                return;
            }
            try {
                Item gtl = this.player.inventory.itemsBody.get(6);
                if (gtl.isNotNullItem()) {
                    this.wearingTrainArmor = true;
                    this.wornTrainArmor = true;
                    this.player.inventory.trainArmor = gtl;
                    this.tlSubSD += ItemService.gI().getPercentTrainArmor(gtl);
                } else {
                    if (this.wornTrainArmor) {
                        this.wearingTrainArmor = false;
                        for (Item.ItemOption io : this.player.inventory.trainArmor.itemOptions) {
                            if (io.optionTemplate.id == 9 && io.param > 0) {
                                this.tlDame
                                        .add(ItemService.gI().getPercentTrainArmor(this.player.inventory.trainArmor));
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    public void setBasePoint() {
        setHpMax();
        setHp();
        setMpMax();
        setMp();
        setDame();
        setDef();
        setCrit();
        setHpHoi();
        setMpHoi();
        setNeDon();
    }

    private void setNeDon() {

    }

    private void setHpHoi() {
        this.hpHoi = this.hpMax / 100;
        this.hpHoi += this.hpHoiAdd;
        this.hpHoi += ((long) this.hpMax * this.tlHpHoi / 100);
        this.hpHoi += ((long) this.hpMax * this.tlHpHoiBanThanVaDongDoi / 100);
    }

    private void setMpHoi() {
        this.mpHoi = this.mpMax / 100;
        this.mpHoi += this.mpHoiAdd;
        this.mpHoi += ((long) this.mpMax * this.tlMpHoi / 100);
        this.mpHoi += ((long) this.mpMax * this.tlMpHoiBanThanVaDongDoi / 100);
    }

    private void setHpMax() {
        this.hpMax = this.hpg;
        this.hpMax += this.hpAdd;
        if (this.player.isPl() && this.player.Captutien > 0) {
            double oldHpMax = this.hpMax;
            int bonusHp = this.player.HpKiGiaptutien(this.player.Captutien);
            this.hpMax += bonusHp;
        }
        if (this.player.effectSkill.isTranformation || this.player.effectSkill.isEvolution) {
            if (!this.player.isPet || (this.player.isPet
                    && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = SkillUtil.getPercentDameMonkey((byte) player.isbienhinh);
                this.hpMax += ((long) this.hpMax * (percent * 10) / 100);
            }
        }
        if (this.player.clan != null && this.player.clan.level >= 10) {
            hpMax += ((long) hpMax * 5 / 100);
        }
        if (this.player.clan != null && this.player.clan.level >= 15) {
            hpMax += ((long) hpMax * 10 / 100);
        }
        if (this.player.clan != null && this.player.clan.level >= 20) {
            hpMax += ((long) hpMax * 15 / 100);
        }
        if (this.player.setClothes.set8 == 5) {
            this.hpMax += ((long) this.hpMax * 5 / 100);
        }
        // đồ
        for (Integer tl : this.tlHp) {
            this.hpMax += ((long) this.hpMax * tl / 100);
        }
        if (this.player.itemTime != null && this.player.itemTime.isgaQuay) {
            this.hpMax += this.hpMax * 10 / 100;
        }
        if (this.player.itemTime != null && this.player.itemTime.is1Trung) {
            this.hpMax += this.hpMax * 5 / 100;
        }
        if (this.player.itemTime != null && this.player.itemTime.isthapCam) {
            this.hpMax += this.hpMax * 5 / 100;
        }
        // set nappa
        if (this.player.setClothes.nappa == 5) {
            this.hpMax += ((long) this.hpMax * 100 / 100);
        }
        // set worldcup
        if (this.player.setClothes.worldcup == 2) {
            this.hpMax += ((long) this.hpMax * 10 / 100);
        }
        // ngọc rồng đen 1 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[0] > System.currentTimeMillis()) {
            this.hpMax += ((long) this.hpMax * RewardBlackBall.R1S_1 / 100);
        }
        if (this.player.lastTimeTitle1 > 0 && player.isTitleUse) {
            this.hpMax += ((long) this.hpMax * 10 / 100);
        }
        if (this.player.setClothes.tromcho == 5) {
            this.hpMax *= 2;
        }
        // khỉ
        if (this.player.effectSkill.isMonkey) {
            if (!this.player.isPet || (this.player.isPet
                    && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = SkillUtil.getPercentHpMonkey(player.effectSkill.levelMonkey);
                this.hpMax += ((long) this.hpMax * percent / 100);
            }
        }
        // pet mabư
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.hpMax += ((long) this.hpMax * 5 / 100);
        }
        // pet berus
        if (this.player.isPet && ((Pet) this.player).typePet == 2// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.hpMax += ((long) this.hpMax * 10 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 3// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.hpMax += ((long) this.hpMax * 13 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.hpMax += ((long) this.hpMax * 15 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.hpMax += ((long) this.hpMax * 18 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.hpMax += ((long) this.hpMax * 20 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.hpMax += ((long) this.hpMax * 25 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.hpMax += ((long) this.hpMax * 30 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 9// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.hpMax += ((long) this.hpMax * 40 / 100);// chi so hp
        }

        // pet mabư
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.hpMax += ((long) this.hpMax * 5 / 100);
        }
        // pet berus
        if (this.player.isPet && ((Pet) this.player).typePet == 2// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.hpMax += ((long) this.hpMax * 10 / 100);// chi so hp
        }
        // pet Broly
        if (this.player.isPet && ((Pet) this.player).typePet == 3// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.hpMax += ((long) this.hpMax * 13 / 100);// chi so hp
        }
        // Pet Ubb
        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.hpMax += ((long) this.hpMax * 15 / 100);// chi so hp
        }
        // Pet Xên Con
        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.hpMax += ((long) this.hpMax * 18 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.hpMax += ((long) this.hpMax * 20 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.hpMax += ((long) this.hpMax * 25 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.hpMax += ((long) this.hpMax * 30 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 9// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.hpMax += ((long) this.hpMax * 40 / 100);// chi so hp
        }
        // pet mabư
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.hpMax += ((long) this.hpMax * 5 / 100);
        }
        // pet berus
        if (this.player.isPet && ((Pet) this.player).typePet == 2// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.hpMax += ((long) this.hpMax * 10 / 100);// chi so hp
        }
        // pet Broly
        if (this.player.isPet && ((Pet) this.player).typePet == 3// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.hpMax += ((long) this.hpMax * 13 / 100);// chi so hp
        }
        // Pet Ubb
        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.hpMax += ((long) this.hpMax * 15 / 100);// chi so hp
        }
        // Pet Xên Con
        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.hpMax += ((long) this.hpMax * 18 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.hpMax += ((long) this.hpMax * 20 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.hpMax += ((long) this.hpMax * 25 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.hpMax += ((long) this.hpMax * 30 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 9// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.hpMax += ((long) this.hpMax * 40 / 100);// chi so hp
        }
        // pet mabư
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.hpMax += ((long) this.hpMax * 5 / 100);
        }
        // pet berus
        if (this.player.isPet && ((Pet) this.player).typePet == 2// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.hpMax += ((long) this.hpMax * 10 / 100);// chi so hp
        }
        // pet Broly
        if (this.player.isPet && ((Pet) this.player).typePet == 3// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.hpMax += ((long) this.hpMax * 13 / 100);// chi so hp
        }
        // Pet Ubb
        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.hpMax += ((long) this.hpMax * 15 / 100);// chi so hp
        }
        // Pet Xên Con
        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.hpMax += ((long) this.hpMax * 18 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.hpMax += ((long) this.hpMax * 20 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.hpMax += ((long) this.hpMax * 25 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.hpMax += ((long) this.hpMax * 30 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 9// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.hpMax += ((long) this.hpMax * 40 / 100);// chi so hp
        }
        // phù
        if (this.player.zone != null && MapService.gI().isMapBlackBallWar(this.player.zone.map.mapId)) {
            this.hpMax *= this.player.effectSkin.xHPKI;
        }
        // +hp đệ
        if (this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            this.hpMax += this.player.pet.nPoint.hpMax;
        }
        // huýt sáo
        if (!this.player.isPet
                || (this.player.isPet
                && ((Pet) this.player).status != Pet.FUSION)) {
            if (this.player.effectSkill.tiLeHPHuytSao != 0) {
                this.hpMax += ((long) this.hpMax * this.player.effectSkill.tiLeHPHuytSao / 100L);

            }
        }

        // bổ huyết
        if (this.player.itemTime != null && this.player.itemTime.isUseBoHuyet) {
            this.hpMax *= 1.5;
        } // item sieu cawsp
        if (this.player.itemTime != null && this.player.itemTime.isUseBoHuyet2) {
            this.hpMax *= 1.6;
        }
        if (this.player.itemTime != null && this.player.itemTime.isnuocmiakhonglo) {
            this.hpMax *= 1.10;
        }
        if (this.player.itemTime != null && this.player.itemTime.isnuocmiathom) {
            this.hpMax *= 1.10;
        }
        if (this.player.itemTime != null && this.player.itemTime.isnuocmiasaurieng) {
            this.hpMax *= 1.10;
        }
        if (this.player.zone != null && MapService.gI().isMapCold(this.player.zone.map)
                && !this.isKhongLanh) {
            this.hpMax /= 2.2;
        }
        // mèo mun
        if (this.player.effectFlagBag.useMeoMun) {
            this.hpMax += ((long) this.hpMax * 15 / 100);
        }
        if (this.player.itemTime.isdkhi) {
            if (!this.player.isPet || (this.player.isPet
                    && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = 100;
                this.hpMax += ((long) this.hpMax * percent / 100);
                // this.tlDameCrit.add(5);
            }
        }
        if (this.player.isPl()) {
            if (this.player.TrieuHoipet != null && this.player.TrieuHoipet.getStatus() != Thu_TrieuHoi.GOHOME
                    && (this.player.CapBacThan == 7 || this.player.CapBacThan == 8 || this.player.CapBacThan == 9
                    || this.player.CapBacThan == 10)) {
                switch (this.player.CapBacThan) {
                    case 7:
                    case 8:
                        this.hpMax += this.hpMax * ((this.player.ThanLevel + 1) / 5) / 100;
                        break;
                    case 9:
                        this.hpMax += this.hpMax * ((this.player.ThanLevel + 1) / 3) / 100;
                        break;
                    default:
                        this.hpMax += this.hpMax * (this.player.ThanLevel + 1) / 100;
                        break;
                }
            }
            if (this.player.TrieuHoipet != null && this.player.TrieuHoipet.getStatus() != Thu_TrieuHoi.GOHOME
                    && (this.player.CapBacThan == 0 || this.player.CapBacThan == 3 || this.player.CapBacThan == 4)) {
                switch (this.player.CapBacThan) {
                    case 0:
                    case 3:
                        this.hpMax += ((this.player.ThanLevel + 1) * 20);
                        break;
                    default:
                        this.hpMax += ((this.player.ThanLevel + 1) * 30);
                        break;
                }
            }
        }
    }

    private void setHp() {
        if (this.hp > this.hpMax) {
            this.hp = this.hpMax;
        }
    }

    private void setMpMax() {
        this.mpMax = this.mpg;
        this.mpMax += this.mpAdd;
        if (this.player.isPl() && this.player.Captutien > 0) {
            double oldMpMax = this.mpMax;
            int bonusKi = this.player.KiTutien(this.player.Captutien);
            this.mpMax += bonusKi;
        }
        if (this.player.effectSkill.isTranformation || this.player.effectSkill.isEvolution) {
            if (!this.player.isPet || (this.player.isPet
                    && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = SkillUtil.getPercentDameMonkey((byte) player.isbienhinh);
                this.mpMax += ((long) this.mpMax * (percent * 10) / 100);
            }
        }
        if (this.player.clan != null && this.player.clan.level >= 10) {
            mpMax += ((long) mpMax * 5 / 100);
        }
        if (this.player.clan != null && this.player.clan.level >= 15) {
            mpMax += ((long) mpMax * 10 / 100);
        }
        if (this.player.clan != null && this.player.clan.level >= 20) {
            mpMax += ((long) mpMax * 15 / 100);
        }
        if (this.player.setClothes.set8 == 5) {
            this.mpMax += ((long) this.mpMax * 5 / 100);
        }
        if (this.player.itemTime != null && this.player.itemTime.isgaQuay) {
            this.mpMax += this.mpMax * 10 / 100;
        }
        if (this.player.itemTime != null && this.player.itemTime.is1Trung) {
            this.mpMax += this.mpMax * 5 / 100;
        }
        if (this.player.itemTime != null && this.player.itemTime.isthapCam) {
            this.mpMax += this.mpMax * 5 / 100;
        }
        // đồ
        for (Integer tl : this.tlMp) {
            this.mpMax += (this.mpMax * tl / 100);
        }
        if (this.player.setClothes.picolo == 5) {
            this.mpMax *= 2;
        }
        // ngọc rồng đen 3 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[2] > System.currentTimeMillis()) {
            this.mpMax += (this.mpMax * RewardBlackBall.R3S_1 / 100);
        }
        // set worldcup
        if (this.player.setClothes.worldcup == 2) {
            this.mpMax += ((long) this.mpMax * 10 / 100);
        }
        if (this.player.setClothes.tromcho == 5) {
            this.mpMax *= 2;
        }
        // pet mabư
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.mpMax += ((long) this.mpMax * 5 / 100);
        }
        // pet berus
        if (this.player.isPet && ((Pet) this.player).typePet == 2// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.mpMax += ((long) this.mpMax * 10 / 100);// chi so hp
        }
        // pet Broly
        if (this.player.isPet && ((Pet) this.player).typePet == 3// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.mpMax += ((long) this.mpMax * 13 / 100);// chi so hp
        }
        // Pet Ubb
        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.mpMax += ((long) this.mpMax * 15 / 100);// chi so hp
        }
        // Pet Xên Con
        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.mpMax += ((long) this.mpMax * 18 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.mpMax += ((long) this.mpMax * 20 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.mpMax += ((long) this.mpMax * 25 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.mpMax += ((long) this.mpMax * 30 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 9// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.mpMax += ((long) this.mpMax * 40 / 100);// chi so hp
        }
        // pet mabư
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.mpMax += ((long) this.mpMax * 5 / 100);
        }
        // pet berus
        if (this.player.isPet && ((Pet) this.player).typePet == 2// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.mpMax += ((long) this.mpMax * 10 / 100);// chi so hp
        }
        // pet Broly
        if (this.player.isPet && ((Pet) this.player).typePet == 3// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.mpMax += ((long) this.mpMax * 13 / 100);// chi so hp
        }
        // Pet Ubb
        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.mpMax += ((long) this.mpMax * 15 / 100);// chi so hp
        }
        // Pet Xên Con
        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.mpMax += ((long) this.mpMax * 18 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.mpMax += ((long) this.mpMax * 20 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.mpMax += ((long) this.mpMax * 25 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.mpMax += ((long) this.mpMax * 30 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 9// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.mpMax += ((long) this.mpMax * 40 / 100);// chi so hp
        }
        // pet mabư
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.mpMax += ((long) this.mpMax * 5 / 100);
        }
        // pet berus
        if (this.player.isPet && ((Pet) this.player).typePet == 2// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.mpMax += ((long) this.mpMax * 10 / 100);// chi so hp
        }
        // pet Broly
        if (this.player.isPet && ((Pet) this.player).typePet == 3// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.mpMax += ((long) this.mpMax * 13 / 100);// chi so hp
        }
        // Pet Ubb
        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.mpMax += ((long) this.mpMax * 15 / 100);// chi so hp
        }
        // Pet Xên Con
        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.mpMax += ((long) this.mpMax * 18 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.mpMax += ((long) this.mpMax * 20 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.mpMax += ((long) this.mpMax * 25 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.mpMax += ((long) this.mpMax * 30 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 9// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.mpMax += ((long) this.mpMax * 40 / 100);// chi so hp
        }
        // pet mabư
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.mpMax += ((long) this.mpMax * 5 / 100);
        }
        // pet berus
        if (this.player.isPet && ((Pet) this.player).typePet == 2// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.mpMax += ((long) this.mpMax * 10 / 100);// chi so hp
        }
        // pet Broly
        if (this.player.isPet && ((Pet) this.player).typePet == 3// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.mpMax += ((long) this.mpMax * 13 / 100);// chi so hp
        }
        // Pet Ubb
        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.mpMax += ((long) this.mpMax * 15 / 100);// chi so hp
        }
        // Pet Xên Con
        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.mpMax += ((long) this.mpMax * 18 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.mpMax += ((long) this.mpMax * 20 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.mpMax += ((long) this.mpMax * 25 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.mpMax += ((long) this.mpMax * 30 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 9// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.mpMax += ((long) this.mpMax * 40 / 100);// chi so hp
        }
        // hợp thể
        if (this.player.fusion.typeFusion != 0) {
            this.mpMax += this.player.pet.nPoint.mpMax;
        }
        // bổ khí
        if (this.player.itemTime != null && this.player.itemTime.isUseBoKhi) {
            this.mpMax *= 1.5;
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseBoKhi2) {
            this.mpMax *= 1.6;
        }
        // phù
        if (this.player.zone != null && MapService.gI().isMapBlackBallWar(this.player.zone.map.mapId)) {
            this.mpMax *= this.player.effectSkin.xHPKI;
        }
        // xiên cá
        if (this.player.effectFlagBag.useXienCa) {
            this.mpMax += ((long) this.mpMax * 15 / 100);
        }
        if (this.player.lastTimeTitle1 > 0 && player.isTitleUse) {
            this.mpMax += ((long) this.mpMax * 10 / 100);
        }
        if (this.player.isPl()) {
            if (this.player.TrieuHoipet != null && this.player.TrieuHoipet.getStatus() != Thu_TrieuHoi.GOHOME
                    && (this.player.CapBacThan == 7 || this.player.CapBacThan == 8 || this.player.CapBacThan == 9
                    || this.player.CapBacThan == 10)) {
                switch (this.player.CapBacThan) {
                    case 7:
                    case 8:
                        this.mpMax += this.mpMax * ((this.player.ThanLevel + 1) / 5) / 100;
                        break;
                    case 9:
                        this.mpMax += this.mpMax * ((this.player.ThanLevel + 1) / 3) / 100;
                        break;
                    default:
                        this.mpMax += this.mpMax * (this.player.ThanLevel + 1) / 100;
                        break;
                }
            }
            if (this.player.TrieuHoipet != null && this.player.TrieuHoipet.getStatus() != Thu_TrieuHoi.GOHOME
                    && (this.player.CapBacThan == 1 || this.player.CapBacThan == 3 || this.player.CapBacThan == 4)) {
                switch (this.player.CapBacThan) {
                    case 1:
                    case 3:
                        this.mpMax += ((this.player.ThanLevel + 1) * 20);
                        break;
                    default:
                        this.mpMax += ((this.player.ThanLevel + 1) * 30);
                        break;
                }
            }
        }
    }

    private void setMp() {
        if (this.mp > this.mpMax) {
            this.mp = this.mpMax;
        }
    }

    private void setDame() {
        this.dame = this.dameg;
        this.dame += this.dameAdd;
        if (this.player.isPl() && this.player.Captutien > 0) {
            double oldDame = this.dame;
            int bonusDame = this.player.Dametutien(this.player.Captutien);
            this.dame += bonusDame;
        }
        if (this.player.clan != null && this.player.clan.level >= 10) {
            dame += ((long) dame * 5 / 100);
        }
        if (this.player.clan != null && this.player.clan.level >= 15) {
            dame += ((long) dame * 10 / 100);
        }
        if (this.player.clan != null && this.player.clan.level >= 20) {
            dame += ((long) dame * 15 / 100);
        }
        if (this.player.itemTime != null && this.player.itemTime.isnuocmiasaurieng) {
            this.dame *= 1.10;
        }
        if (this.player.setClothes.set8 == 5) {
            this.dame += ((long) this.dame * 5 / 100);
        }
        // đồ
        try {
            if (this != null) {
                for (Integer tl : this.tlDame) {
                    this.dame += ((long) this.dame * tl / 100);
                }
            }
        } catch (NoSuchElementException e) {

        }
        for (Integer tl : this.tlSDDep) {
            this.dame += ((long) this.dame * tl / 100);
        }
        // pet mabư
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.dame += ((long) this.dame * 5 / 100);
        }
        // pet berus
        if (this.player.isPet && ((Pet) this.player).typePet == 2// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.dame += ((long) this.dame * 10 / 100);// chi so hp
        }
        // pet Broly
        if (this.player.isPet && ((Pet) this.player).typePet == 3// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.dame += ((long) this.dame * 13 / 100);// chi so hp
        }
        // Pet Ubb
        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.dame += ((long) this.dame * 15 / 100);// chi so hp
        }
        // Pet Xên Con
        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.dame += ((long) this.dame * 18 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.dame += ((long) this.dame * 20 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.dame += ((long) this.dame * 25 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.dame += ((long) this.dame * 30 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 9// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.dame += ((long) this.dame * 40 / 100);// chi so hp
        }
        // pet mabư
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.dame += ((long) this.dame * 5 / 100);
        }
        // pet berus
        if (this.player.isPet && ((Pet) this.player).typePet == 2// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.dame += ((long) this.dame * 10 / 100);// chi so hp
        }
        // pet Broly
        if (this.player.isPet && ((Pet) this.player).typePet == 3// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.dame += ((long) this.dame * 13 / 100);// chi so hp
        }
        // Pet Ubb
        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.dame += ((long) this.dame * 15 / 100);// chi so hp
        }
        // Pet Xên Con
        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.dame += ((long) this.dame * 18 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.dame += ((long) this.dame * 20 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.dame += ((long) this.dame * 25 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.dame += ((long) this.dame * 30 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 9// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.dame += ((long) this.dame * 40 / 100);// chi so hp
        }
        // pet mabư
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.dame += ((long) this.dame * 5 / 100);
        }
        // pet berus
        if (this.player.isPet && ((Pet) this.player).typePet == 2// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.dame += ((long) this.dame * 10 / 100);// chi so hp
        }
        // pet Broly
        if (this.player.isPet && ((Pet) this.player).typePet == 3// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.dame += ((long) this.dame * 13 / 100);// chi so hp
        }
        // Pet Ubb
        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.dame += ((long) this.dame * 15 / 100);// chi so hp
        }
        // Pet Xên Con
        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.dame += ((long) this.dame * 18 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.dame += ((long) this.dame * 20 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.dame += ((long) this.dame * 25 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.dame += ((long) this.dame * 30 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 9// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.dame += ((long) this.dame * 40 / 100);// chi so hp
        }
        // pet mabư
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.dame += ((long) this.dame * 5 / 100);
        }
        // pet berus
        if (this.player.isPet && ((Pet) this.player).typePet == 2// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.dame += ((long) this.dame * 10 / 100);// chi so hp
        }
        // pet Broly
        if (this.player.isPet && ((Pet) this.player).typePet == 3// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.dame += ((long) this.dame * 13 / 100);// chi so hp
        }
        // Pet Ubb
        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.dame += ((long) this.dame * 15 / 100);// chi so hp
        }
        // Pet Xên Con
        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.dame += ((long) this.dame * 18 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 6// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.dame += ((long) this.dame * 20 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 7// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.dame += ((long) this.dame * 25 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 8// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.dame += ((long) this.dame * 30 / 100);// chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 9// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.dame += ((long) this.dame * 40 / 100);// chi so hp
        }
        // thức ăn
        if (!this.player.isPet && this.player.itemTime.isEatMeal
                || this.player.isPet && ((Pet) this.player).master.itemTime.isEatMeal) {
            this.dame += ((long) this.dame * 10 / 100);
        }
        // hợp thể
        if (this.player.fusion.typeFusion != 0) {
            this.dame += this.player.pet.nPoint.dame;
        }
        if (this.player.effectSkill.isTranformation || this.player.effectSkill.isEvolution) {
            if (!this.player.isPet || (this.player.isPet
                    && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = SkillUtil.getPercentDameMonkey((byte) player.isbienhinh);
                this.dame += ((long) this.dame * (percent * 10) / 100);
            }
        }
        // cuồng nộ
        if (this.player.itemTime != null && this.player.itemTime.isUseCuongNo) {
            this.dame *= 1.5;
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseCuongNo2) {
            this.dame += this.dame * 0.6;
        }
        if (this.player.itemTime != null && this.player.itemTime.isgaQuay) {
            this.dame += this.dame * 5 / 100;
        }
        if (this.player.itemTime != null && this.player.itemTime.is1Trung) {
            this.dame += this.dame * 5 / 100;
        }
        if (this.player.itemTime != null && this.player.itemTime.is2Trung) {
            this.tlDameCrit.add(5);
        }
        if (this.player.itemTime != null && this.player.itemTime.isthapCam) {
            this.dame += this.dame * 5 / 100;
            this.tlDameCrit.add(5);
        }

        // giảm dame
        this.dame -= ((long) this.dame * tlSubSD / 100);
        // map cold
        if (this.player.zone != null && MapService.gI().isMapCold(this.player.zone.map)
                && !this.isKhongLanh) {
            this.dame /= 2.2;
        }
        // ngọc rồng đen 1 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[0] > System.currentTimeMillis()) {
            this.dame += ((long) this.dame * RewardBlackBall.R1S_2 / 100);
        }
        // set worldcup
        if (this.player.setClothes.worldcup == 2) {
            this.dame += ((long) this.dame * 10 / 100);
            this.tlDameCrit.add(20);
        }
        if (this.player.itemTime.isdkhi) {
            if (!this.player.isPet || (this.player.isPet
                    && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = 100;
                this.dame += ((long) this.dame * percent / 100);
                this.tlDameCrit.add(5);
            }
        }
        // phóng heo
        if (this.player.effectFlagBag.usePhongHeo) {
            this.dame += ((long) this.dame * 15 / 100);
        }
        if (this.player.setClothes.tromcho == 5) {
            this.dame *= 2;
        }
        // khỉ
        if (this.player.effectSkill.isMonkey) {
            if (!this.player.isPet || (this.player.isPet
                    && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = SkillUtil.getPercentDameMonkey(player.effectSkill.levelMonkey);
                this.dame += ((long) this.dame * percent / 100);
            }
        }
        if (this.player.lastTimeTitle1 > 0 && player.isTitleUse) {
            this.dame += ((long) this.dame * 10 / 100);
        }
        if (this.player.isPl()) {
            if (this.player.TrieuHoipet != null && this.player.TrieuHoipet.getStatus() != Thu_TrieuHoi.GOHOME
                    && (this.player.CapBacThan == 5 || this.player.CapBacThan == 7 || this.player.CapBacThan == 8
                    || this.player.CapBacThan == 9
                    || this.player.CapBacThan == 10)) {
                switch (this.player.CapBacThan) {
                    case 5:
                    case 7:
                    case 8:
                        this.dame += this.dame * ((this.player.ThanLevel + 1) / 5) / 100;
                        break;
                    case 9:
                        this.dame += this.dame * ((this.player.ThanLevel + 1) / 2) / 100;
                        break;
                    default:
                        this.dame += this.dame * ((this.player.ThanLevel + 1)) / 100;
                        break;
                }
            }
            if (this.player.TrieuHoipet != null && this.player.TrieuHoipet.getStatus() != Thu_TrieuHoi.GOHOME
                    && (this.player.CapBacThan == 2 || this.player.CapBacThan == 3)) {
                this.dame += ((this.player.ThanLevel + 1) * 10);
            }
        }
    }

    private void setDef() {
        this.def = this.defg * 4;
        this.def += this.defAdd;
        if (this.player.isPl() && this.player.Captutien > 0) {
            this.def += this.def * this.player.HpKiGiaptutien(this.player.Captutien)
                    / 100d;
        }
        // đồ
        for (Integer tl : this.tlDef) {
            this.def += (this.def * tl / 100);
        }
        // ngọc rồng đen 2 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[1] > System.currentTimeMillis()) {
            this.def += ((long) this.def * RewardBlackBall.R2S_2 / 100);
        }
        if (this.player.itemTime != null && this.player.itemTime.isnuocmiasaurieng) {
            this.def *= 1.10;
        }
        if (this.player.isPl()) {
            if (this.player.TrieuHoipet != null && this.player.TrieuHoipet.getStatus() != Thu_TrieuHoi.GOHOME
                    && (this.player.CapBacThan == 4 || this.player.CapBacThan == 7 || this.player.CapBacThan == 8
                    || this.player.CapBacThan == 9
                    || this.player.CapBacThan == 10)) {
                switch (this.player.CapBacThan) {
                    case 4:
                        this.def += ((this.player.ThanLevel + 1) * 30);
                        break;
                    case 7:
                    case 8:
                        this.def += this.def * ((this.player.ThanLevel + 1) / 5) / 100;
                        break;
                    case 9:
                        this.def += this.def * ((this.player.ThanLevel + 1) / 3) / 100;
                        break;
                    default:
                        this.def += this.def * (this.player.ThanLevel + 1) / 100;
                        break;
                }
            }
        }
    }

    private void setCrit() {
        this.crit = this.critg;
        this.crit += this.critAdd;
        // ngọc rồng đen 3 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[2] > System.currentTimeMillis()) {
            this.crit += RewardBlackBall.R3S_2;
        }
        // biến khỉ
        if (this.player.effectSkill.isMonkey) {
            this.crit = 110;
        }
        if (this.player.itemTime != null && this.player.itemTime.is2Trung) {
            this.crit += this.crit * 5 / 100;
        }
        if (this.player.itemTime != null && this.player.itemTime.is1Trung) {
            this.crit += this.crit * 5 / 100;
        }
        if (this.player.itemTime != null && this.player.itemTime.isnuocmiathom) {
            this.crit *= 1.10;
        }
    }

    private void resetPoint() {
        this.voHieuChuong = 0;
        this.hpAdd = 0;
        this.isThoDaiCa = false; // Cải trang Thỏ Đại Ca
        this.isDrabura = false; // Cải trang Dracula
        this.isDraburaFrost = false; // Cải trang Dracula Frost
        this.mpAdd = 0;
        this.dameAdd = 0;
        this.defAdd = 0;
        this.critAdd = 0;
        this.tlHp.clear();
        this.tlMp.clear();
        this.tlDef.clear();
        this.tlDame.clear();
        this.tlDameCrit.clear();
        this.tlDameAttMob.clear();
        this.tlHpHoiBanThanVaDongDoi = 0;
        this.tlMpHoiBanThanVaDongDoi = 0;
        this.hpHoi = 0;
        this.mpHoi = 0;
        this.mpHoiCute = 0;
        this.tlHpHoi = 0;
        this.tlMpHoi = 0;
        this.tlHutHp = 0;
        this.tlHutMp = 0;
        this.tlHutHpMob = 0;
        this.tlHutHpMpXQ = 0;
        this.tlPST = 0;
        this.tlTNSM.clear();
        this.tlDameAttMob.clear();
        this.tlGold = 0;
        this.tlNeDon = 0;
        this.tlSDDep.clear();
        this.tlSubSD = 0;
        this.tlHpGiamODo = 0;
        this.test = 0;
        this.teleport = false;

        this.wearingVoHinh = false;
        this.isKhongLanh = false;
        this.khangTDHS = false;
    }

    private void addHpInternal(double hp) {
        this.hp += hp;
        if (this.hp > this.hpMax) {
            this.hp = this.hpMax;
        }
    }

    private void addMpInternal(double mp) {
        this.mp += mp;
        if (this.mp > this.mpMax) {
            this.mp = this.mpMax;
        }
    }

    private void setHpInternal(double hp) {
        if (hp > this.hpMax) {
            this.hp = this.hpMax;
        } else {
            this.hp = hp;
        }
    }

    private void setMpInternal(double mp) {
        if (mp > this.mpMax) {
            this.mp = this.mpMax;
        } else {
            this.mp = mp;
        }
    }

    private void setDameInternal(double dame) {
        if (dame > this.dameg) {
            this.dame = this.dameg;
        } else {
            this.dame = dame;
        }
    }

    public void addHp(long hp) {
        addHpInternal((double) hp);
    }

    public void addHp(double hp) {
        addHpInternal(hp);
    }

    public void addMp(long mp) {
        addMpInternal((double) mp);
    }

    public void addMp(double mp) {
        addMpInternal(mp);
    }

    public void setHp(long hp) {
        setHpInternal((double) hp);
    }

    public void setHp(double hp) {
        setHpInternal(hp);
    }

    public void setMp(long mp) {
        setMpInternal((double) mp);
    }

    public void setMp(double mp) {
        setMpInternal(mp);
    }

    public void setDame(long dame) {
        setDameInternal((double) dame);
    }

    public void setDame(double dame) {
        setDameInternal(dame);
    }

    private void setIsCrit() {
        if (intrinsic != null && intrinsic.id == 25
                && this.getCurrPercentHP() <= intrinsic.param1) {
            isCrit = true;
        } else if (isCrit100) {
            isCrit100 = false;
            isCrit = true;
        } else {
            isCrit = Util.isTrue(this.crit, ConstRatio.PER100);
        }
    }

    private double calculateDameAttack(boolean isAttackMob) {
        setIsCrit();
        double dameAttack = this.dame;
        intrinsic = this.player.playerIntrinsic.intrinsic;
        Skill skillSelect = player.playerSkill.skillSelect;
        if (skillSelect == null) {
            return 10;
        }
        percentDameIntrinsic = 0;
        int percentDameSkill = 0;
        byte percentXDame = 0;
        switch (skillSelect.template.id) {
            case Skill.DRAGON:
                if (intrinsic.id == 1) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.KAMEJOKO:
                if (intrinsic.id == 2) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.songoku == 5) {
                    percentXDame = 100;
                }
                break;
            case Skill.GALICK:
                if (intrinsic.id == 16) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.kakarot == 5) {
                    percentXDame = 100;
                }
                break;
            case Skill.ANTOMIC:
                if (intrinsic.id == 17) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.DEMON:
                if (intrinsic.id == 8) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.MASENKO:
                if (intrinsic.id == 9) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.KAIOKEN:
                if (intrinsic.id == 26) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.kirin == 5) {
                    percentXDame = 70;
                }
                break;
            case Skill.LIEN_HOAN:
                if (intrinsic.id == 13) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.ocTieu == 5) {
                    percentXDame = 50;
                }
                break;
            case Skill.DICH_CHUYEN_TUC_THOI:
                dameAttack *= 2;
                dameAttack = (double) (((long) Util.nextInt(95, 105) * dameAttack) / 100);
                return dameAttack;
            case Skill.MAKANKOSAPPO:
                percentDameSkill = skillSelect.damage;
                double dameSkill = (this.mpMax * percentDameSkill / 100);
                return dameSkill;
            case Skill.QUA_CAU_KENH_KHI:
                double dame = this.dame * 40;
                if (this.player.setClothes.kirin == 5) {
                    dame *= 2;
                }
                dame = dame + (Util.nextInt(-5, 5) * dame / 100);
                return dame;
        }
        if (intrinsic.id == 18 && this.player.effectSkill.isMonkey) {
            percentDameIntrinsic = intrinsic.param1;
        }
        if (percentDameSkill != 0) {
            dameAttack = dameAttack * percentDameSkill / 100;
        }
        dameAttack += (dameAttack * percentDameIntrinsic / 100);
        dameAttack += (dameAttack * dameAfter / 100);

        if (isAttackMob) {
            for (Integer tl : this.tlDameAttMob) {
                dameAttack += (dameAttack * tl / 100);
            }
        }
        dameAfter = 0;
        if (this.player.isPet && ((Pet) this.player).master.charms.tdDeTu > System.currentTimeMillis()) {
            dameAttack *= 2;
        }
        if (isCrit) {
            dameAttack *= 2;
            for (Integer tl : this.tlDameCrit) {
                dameAttack += (dameAttack * tl / 120);
            }
        }
        dameAttack += (dameAttack * percentXDame / 100);
        dameAttack = ((double) Util.nextInt(95, 105) * dameAttack) / 100;
        if (player.isPl()) {
            if (player.inventory.haveOption(player.inventory.itemsBody, 5, 159)) {
                if (Util.canDoWithTime(player.lastTimeUseOption, 60000)
                        && (player.playerSkill.skillSelect.skillId == Skill.KAMEJOKO
                        || player.playerSkill.skillSelect.skillId == Skill.ANTOMIC
                        || player.playerSkill.skillSelect.skillId == Skill.MASENKO)) {
                    dameAttack *= player.inventory.getParam(player.inventory.itemsBody.get(5), 159);
                    player.lastTimeUseOption = System.currentTimeMillis();
                }
            }
        }
        if (this.player.TrieuHoipet != null && this.player.TrieuHoipet.getStatus() != Thu_TrieuHoi.GOHOME
                && (this.player.CapBacThan == 6 || this.player.CapBacThan == 9
                || this.player.CapBacThan == 10)) {
            switch (this.player.CapBacThan) {
                case 6:
                case 9:
                    dameAttack += dameAttack * ((this.player.ThanLevel + 1) / 3) / 100;
                    break;
                default:
                    dameAttack += dameAttack * (this.player.ThanLevel + 1) / 100;
                    break;
            }
        }
        return dameAttack;
    }

    public long getDameAttack(boolean isAttackMob) {
        return (long) calculateDameAttack(isAttackMob);
    }

    public double getDameAttackDouble(boolean isAttackMob) {
        return calculateDameAttack(isAttackMob);
    }

    public int getCurrPercentHP() {
        if (this.hpMax == 0) {
            return 100;
        }
        return (int) (this.hp * 100 / this.hpMax);
    }

    public int getCurrPercentMP() {
        return (int) (this.mp * 100 / this.mpMax);
    }

    public void setFullHpMpDame() {
        this.hp = this.hpMax;
        this.mp = this.mpMax;
        this.dame = this.dameg;
    }

    public void setFullHp() {
        this.hp = this.hpMax;
    }

    public void setFullMp() {
        this.mp = this.mpMax;
    }

    private void subtractHP(double sub) {
        this.hp -= sub;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    private void subtractMP(double sub) {
        this.mp -= sub;
        if (this.mp < 0) {
            this.mp = 0;
        }
    }

    public void subHP(long sub) {
        subtractHP((double) sub);
    }

    public void subHP(double sub) {
        subtractHP(sub);
    }

    public void subMP(long sub) {
        subtractMP((double) sub);
    }

    public void subMP(double sub) {
        subtractMP(sub);
    }

    public void setFullHpMp() {
        this.hp = this.hpMax;
        this.mp = this.mpMax;
    }

    private double calculateSucManhTiemNang(double tiemNang) {
        if (power < getPowerLimit()) {
            for (Integer tl : this.tlTNSM) {
                tiemNang += (tiemNang * tl / 100);
            }
            if (this.player.cFlag != 0) {
                if (this.player.cFlag == 8) {
                    tiemNang += (tiemNang * 10 / 100);
                } else {
                    tiemNang += (tiemNang * 5 / 100);
                }
            }
            double tn = tiemNang;
            if (this.player.charms.tdTriTue > System.currentTimeMillis()) {
                tiemNang += tn;
            }
            if (this.player.charms.tdTriTue3 > System.currentTimeMillis()) {
                tiemNang += tn * 2;
            }
            if (this.player.charms.tdTriTue4 > System.currentTimeMillis()) {
                tiemNang += tn * 3;
            }
            if (this.player.itemTime.isX2EXP && this.player.itemTime.lastX2EXP > 0) {
                tiemNang += tn * 2;
            }
            if (this.player.itemTime.isX3EXP && this.player.itemTime.lastX3EXP > 0) {
                tiemNang += tn * 3;
            }
            if (this.player.itemTime.isX5EXP && this.player.itemTime.lastX5EXP > 0) {
                tiemNang += tn * 5;
            }
            if (this.player.itemTime.isX7EXP && this.player.itemTime.lastX7EXP > 0) {
                tiemNang += tn * 7;
            }
            if (this.player.clan != null && this.player.clan.level >= 10) {
                tiemNang += (tn * 10 / 100);
            }
            if (this.player.clan != null && this.player.clan.level >= 15) {
                tiemNang += (tn * 15 / 100);
            }
            if (this.player.clan != null && this.player.clan.level >= 20) {
                tiemNang += (tn * 20 / 100);
            }
            if (this.player.vip >= 1 && this.player.vip <= 4) {
                tiemNang += (tn * 20 / 100);
            }
            if (this.intrinsic != null && this.intrinsic.id == 24) {
                tiemNang += (tn * this.intrinsic.param1 / 100);
            }
            if (this.power >= 9000000000000000000L) {
                tiemNang -= (tn * 80 / 100);
            }

            if (this.player.isPet) {
                if (((Pet) this.player).master.charms.tdDeTu > System.currentTimeMillis()) {
                    tiemNang += tn * 2;
                }
            }
            tiemNang *= Manager.RATE_EXP_SERVER;
            tiemNang = calSubTNSM(tiemNang);
            if (tiemNang <= 0) {
                tiemNang = 1;
            }
        } else {
            tiemNang = 10;
        }
        return tiemNang;
    }

    public long calSucManhTiemNang(long tiemNang) {
        return (long) calculateSucManhTiemNang((double) tiemNang);
    }

    public double calSucManhTiemNang(double tiemNang) {
        return calculateSucManhTiemNang(tiemNang);
    }

    private double calculateTiemNangReduction(double tiemNang) {
        if (power >= 9000000000000000000L) { // 1 triệu tỷ
            tiemNang -= (tiemNang * 99 / 100); // Giảm 99%
        }
        return tiemNang;
    }

    public long calSubTNSM(long tiemNang) {
        return (long) calculateTiemNangReduction((double) tiemNang);
    }

    public double calSubTNSM(double tiemNang) {
        return calculateTiemNangReduction(tiemNang);
    }

    public short getTileHutHp(boolean isMob) {
        if (isMob) {
            return (short) (this.tlHutHp + this.tlHutHpMob);
        } else {
            return this.tlHutHp;
        }
    }

    public short getTiLeHutMp() {
        return this.tlHutMp;
    }

    private double calculateDamageWithDefense(double dame) {
        if (dame <= 0) {
            return -1;
        }

        double def = this.def;
        dame -= def;

        if (this.player.itemTime.isUseGiapXen || this.player.itemTime.isUseGiapXen2) {
            dame /= this.player.itemTime.isUseGiapXen ? 2 : 3;
        }

        if (this.player.itemTime.isbkt) {
            dame *= 0.2;
        }

        if (dame < 0) {
            dame = 1;
        }

        return dame;
    }

    public long subDameInjureWithDeff(long dame) {
        return (long) calculateDamageWithDefense((double) dame);
    }

    public double subDameInjureWithDeff(double dame) {
        return calculateDamageWithDefense(dame);
    }

    /*------------------------------------------------------------------------*/
    public boolean canOpenPower() {
        return this.power >= getPowerLimit();
    }

    public long getPowerLimit() {
        switch (limitPower) {
            case 0:
                return 9223372036854775807L;
            case 1:
                return 9223372036854775807L;
            default:
                return 0;
        }
    }

    public long getPowerNextLimit() {
        switch (limitPower + 1) {
            case 0:
                return 9223372036854775807L;
            case 1:
                return 9223372036854775807L;
            default:
                return 0;
        }
    }

    public int getHpMpLimit() {
        if (limitPower == 0) {
            return 700000;
        }
        if (limitPower == 1) {
            return 700000;
        }
        return 0;
    }

    public int getDameLimit() {
        if (limitPower == 0) {
            return 37000;
        }
        if (limitPower == 1) {
            return 37000;
        }
        return 0;
    }

    public int getDefLimit() {
        if (limitPower == 0) {
            return 1;
        }
        if (limitPower == 1) {
            return 1;
        }
        return 0;
    }

    public byte getCritLimit() {
        if (limitPower == 0) {
            return 5;
        }
        if (limitPower == 1) {
            return 10;
        }
        return 0;
    }

    public int getexp() {
        int[] expTable = {5000, 10000, 20000, 40000, 80000, 120000, 240000, 500000};
        if (player.typetrain >= 0 && player.typetrain < expTable.length) {
            return expTable[player.typetrain];
        } else {
            return 0;
        }
    }

    public String getNameNPC(Player player, Npc npc, byte type) {
        if (type == 2) {
            switch (npc.tempId) {
                case ConstNpc.THAN_MEO_KARIN:
                case ConstNpc.THUONG_DE:
                case ConstNpc.THAN_VU_TRU:
                case ConstNpc.TO_SU_KAIO:
                case ConstNpc.BILL:
                    return "ta";
            }
        } else if (type == 1) {
            switch (npc.tempId) {
                case ConstNpc.THAN_MEO_KARIN:
                    return "Yajirô";
                case ConstNpc.THUONG_DE:
                    return "Mr.PôPô";
                case ConstNpc.THAN_VU_TRU:
                    return "Khỉ Bubbles";
                case ConstNpc.TO_SU_KAIO:
                    return "ta";
                case ConstNpc.BILL:
                    return "Whis";
            }
        }
        return "NRO HAPPY";
    }

    public int getExpbyNPC(Player player, Npc npc, byte type) {
        if (type == 2) {
            switch (npc.tempId) {
                case ConstNpc.THAN_MEO_KARIN:
                    return 5000;
                case ConstNpc.THUONG_DE:
                    return 20000;
                case ConstNpc.THAN_VU_TRU:
                    return 80000;
                case ConstNpc.TO_SU_KAIO:
                    return 120000;
                case ConstNpc.BILL:
                    return 500000;
            }
        } else if (type == 1) {
            switch (npc.tempId) {
                case ConstNpc.THAN_MEO_KARIN:
                    return 5000;
                case ConstNpc.THUONG_DE:
                    return 10000;
                case ConstNpc.THAN_VU_TRU:
                    return 40000;
                case ConstNpc.TO_SU_KAIO:
                    return 120000;
                case ConstNpc.BILL:
                    return 240000;
            }
        }
        return 0;
    }

    // **************************************************************************
    // POWER - TIEM NANG
    public void powerUp(long power) {
        this.power += power;
        TaskService.gI().checkDoneTaskPower(player, (long) this.power);
    }

    public void tiemNangUp(long tiemNang) {
        this.tiemNang += tiemNang;
    }

    public void powerUp(double power) {
        this.power += power;
        TaskService.gI().checkDoneTaskPower(player, (long) this.power);
    }

    public void tiemNangUp(double tiemNang) {
        this.tiemNang += tiemNang;
    }

    public boolean doUseTiemNang(byte type, short point) {
        if (point <= 0 || point > 1000) {
            return false;
        }
        long tiemNangUse = 0;
        if (type == 0) {
            long pointHp = point * 20;
            tiemNangUse = (long) (point * (2 * (this.hpg + 1000) + pointHp - 20) / 2);
        }
        if (type == 1) {
            int pointMp = point * 20;
            tiemNangUse = (long) (point * (2 * (this.mpg + 1000) + pointMp - 20) / 2);
        }
        if (type == 2) {
            tiemNangUse = (long) (point * (2 * this.dameg + point - 1) / 2 * 100);
        }
        if (type == 3) {
            tiemNangUse = (long) (2 * (this.defg + 5) / 2 * 100000);
        }
        if (this.tiemNang >= tiemNangUse && this.tiemNang - tiemNangUse >= 0 && tiemNangUse != 0) {
            return true;
        }
        return false;
    }

    public void increasePoint(byte type, short point) {
        if (point <= 0 || point > 1000) {
            return;
        }
        long tiemNangUse = 0;
        if (type == 0) {
            int pointHp = point * 20;
            tiemNangUse = (long) (point * (2 * (this.hpg + 1000) + pointHp - 20) / 2);
            if ((this.hpg + pointHp) <= getHpMpLimit()) {
                if (doUseTiemNang(tiemNangUse)) {
                    hpg += pointHp;
                }
            } else {
                Service.gI().sendThongBaoOK(player, "Đã Đạt Tối Đa Sức Mạnh!");
                return;
            }
        }
        if (type == 1) {
            int pointMp = point * 20;
            tiemNangUse = (long) (point * (2 * (this.mpg + 1000) + pointMp - 20) / 2);
            if ((this.mpg + pointMp) <= getHpMpLimit()) {
                if (doUseTiemNang(tiemNangUse)) {
                    mpg += pointMp;
                }
            } else {
                Service.gI().sendThongBaoOK(player, "Đã Đạt Tối Đa Sức Mạnh!");
                return;
            }
        }
        if (type == 2) {
            tiemNangUse = (long) (point * (2 * this.dameg + point - 1) / 2 * 100);
            if ((this.dameg + point) <= getDameLimit()) {
                if (doUseTiemNang(tiemNangUse)) {
                    dameg += point;
                }
            } else {
                Service.gI().sendThongBaoOK(player, "Đã Đạt Tối Đa Sức Mạnh!");
                return;
            }
        }
        if (type == 3) {
            tiemNangUse = (long) (2 * (this.defg + 5) / 2 * 100000);
            if ((this.defg + point) <= getDefLimit()) {
                if (doUseTiemNang(tiemNangUse)) {
                    defg += point;
                }
            } else {
                Service.gI().sendThongBaoOK(player, "Đã Đạt Tối Đa Sức Mạnh!");
                return;
            }
        }
        if (type == 4) {
            tiemNangUse = 50000000L;
            for (int i = 0; i < this.critg; i++) {
                tiemNangUse *= 5L;
            }
            if ((this.critg + point) <= getCritLimit()) {
                if (doUseTiemNang(tiemNangUse)) {
                    critg += point;
                }
            } else {
                Service.gI().sendThongBaoOK(player, "Đã Đạt Tối Đa Sức Mạnh!");
                return;
            }
        }
        Service.gI().point(player);
    }

    private boolean doUseTiemNang(long tiemNang) {
        if (this.tiemNang < tiemNang) {
            Service.gI().sendThongBaoOK(player, "Bạn Không Đủ Tiềm Năng!");
            return false;
        }
        if (this.tiemNang >= tiemNang && this.tiemNang - tiemNang >= 0) {
            this.tiemNang -= tiemNang;
            TaskService.gI().checkDoneTaskUseTiemNang(player);
            return true;
        }
        return false;
    }

    // --------------------------------------------------------------------------
    private long lastTimeHoiPhuc;
    private long lastTimeHoiStamina;

    public void update() {
        if (player != null && player.effectSkill != null) {
            if (player.effectSkill.isCharging && player.effectSkill.countCharging < 10) {
                int tiLeHoiPhuc = SkillUtil.getPercentCharge(player.playerSkill.skillSelect.point);
                if (player.effectSkill.isCharging && !player.isDie() && !player.effectSkill.isHaveEffectSkill()
                        && (hp < hpMax || mp < mpMax)) {
                    PlayerService.gI().hoiPhuc(player, (long) (hpMax / 100 * tiLeHoiPhuc),
                            (long) (mpMax / 100 * tiLeHoiPhuc));
                    if (player.effectSkill.countCharging % 3 == 0) {
                        Service.gI().chat(player, "Phục hồi năng lượng " + getCurrPercentHP() + "%");
                    }
                } else {
                    EffectSkillService.gI().stopCharge(player);
                }
                if (++player.effectSkill.countCharging >= 10) {
                    EffectSkillService.gI().stopCharge(player);
                }
            }
            if (Util.canDoWithTime(lastTimeHoiPhuc, 30000)) {
                PlayerService.gI().hoiPhuc(this.player, (long) hpHoi, (long) mpHoi);
                this.lastTimeHoiPhuc = System.currentTimeMillis();
            }
            if (Util.canDoWithTime(lastTimeHoiStamina, 60000) && this.stamina < this.maxStamina) {
                this.stamina++;
                this.lastTimeHoiStamina = System.currentTimeMillis();
                if (!this.player.isBoss && !this.player.isPet && !this.player.isTrieuhoipet) {
                    PlayerService.gI().sendCurrentStamina(this.player);
                }
            }
        }
        // hồi phục 30s
        // hồi phục thể lực
    }

    public void dispose() {
        this.intrinsic = null;
        this.player = null;
        this.tlHp = null;
        this.tlMp = null;
        this.tlDef = null;
        this.tlDame = null;
        this.tlDameAttMob = null;
        this.tlSDDep = null;
        this.tlTNSM = null;
    }

}
