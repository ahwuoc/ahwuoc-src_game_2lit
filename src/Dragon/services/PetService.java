package Dragon.services;

import Dragon.consts.ConstPlayer;
import Dragon.models.player.NewPet;
import Dragon.models.player.Pet;
import Dragon.models.player.Player;
import static Dragon.services.PetService.Thu_TrieuHoi;
import Dragon.services.func.ChangeMapService;
import Dragon.utils.SkillUtil;
import Dragon.utils.Util;
import Dragon.utils.Logger;
import Dragon.De2.Thu_TrieuHoi;

public class PetService {

    private static PetService i;

    // Pet type constants for better maintainability
    public static final byte PET_TYPE_NORMAL = 0;
    public static final byte PET_TYPE_MABU = 1;
    public static final byte PET_TYPE_BERUS = 2;
    public static final byte PET_TYPE_PIC = 3;
    public static final byte PET_TYPE_BROLY = 3;
    public static final byte PET_TYPE_UBB = 4;
    public static final byte PET_TYPE_XENCON = 5;
    public static final byte PET_TYPE_ANDROID21 = 6;
    public static final byte PET_TYPE_FU = 7;
    public static final byte PET_TYPE_KIDBILL = 8;
    public static final byte PET_TYPE_GOKUSSJ4 = 9;

    public static PetService gI() {
        if (i == null) {
            i = new PetService();
        }
        return i;
    }

    public void createAndroid21Vip(Player player, boolean isChange, byte gender) {
        createVipPet(player, isChange, gender, "$Android 21 Majin Form", PET_TYPE_ANDROID21,
                "Đệ tử vip vãi nồi đây...");
    }

    public void createFuVip(Player player, boolean isChange, byte gender) {
        createVipPet(player, isChange, gender, "$Fu", PET_TYPE_FU, "Đệ tử vip vãi nồi đây...");
    }

    public void createKidbillVip(Player player, boolean isChange, byte gender) {
        createVipPet(player, isChange, gender, "$Kid Bill", PET_TYPE_KIDBILL, "Đệ tử vip vãi nồi đây...");
    }

    public void createGokuSSJ4Vip(Player player, boolean isChange, byte gender) {
        createVipPet(player, isChange, gender, "$Goku SSJ4", PET_TYPE_GOKUSSJ4, "Đệ tử vip vãi nồi đây...");
    }

    /**
     * Common method for creating VIP pets to eliminate code duplication
     */
    private void createVipPet(Player player, boolean isChange, byte gender, String petName, byte petType,
            String message) {
        byte limitPower = preparePetChange(player, isChange);

        new Thread(() -> {
            try {
                Pet pet = new Pet(player);
                pet.name = petName;
                pet.gender = gender;
                pet.id = -player.id;
                pet.nPoint.power = 1500000;
                pet.typePet = petType;
                pet.nPoint.stamina = 1000;
                pet.nPoint.maxStamina = 1000;
                pet.nPoint.hpg = 5000;
                pet.nPoint.mpg = 5000;
                pet.nPoint.dameg = 320;
                pet.nPoint.defg = 250;
                pet.nPoint.critg = 25;

                // Initialize inventory
                for (int i = 0; i < 7; i++) {
                    pet.inventory.itemsBody.add(ItemService.gI().createItemNull());
                }

                // Initialize skills
                pet.playerSkill.skills.add(SkillUtil.createSkill(Util.nextInt(0, 2) * 2, 1));
                for (int i = 0; i < 4; i++) {
                    pet.playerSkill.skills.add(SkillUtil.createEmptySkill());
                }

                pet.nPoint.setFullHpMp();
                player.pet = pet;
                player.pet.nPoint.limitPower = limitPower;

                Service.gI().chatJustForMe(player, player.pet, message);
            } catch (Exception e) {
                Logger.logException(PetService.class, e,
                        "Error creating VIP pet: " + petName + " for player: " + player.name);
            }
        }).start();
    }

    /**
     * Helper method to prepare pet change - eliminates code duplication
     */
    private byte preparePetChange(Player player, boolean isChange) {
        byte limitPower;
        if (isChange) {
            limitPower = player.pet.nPoint.limitPower;
            if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
                player.pet.unFusion();
            }
            ChangeMapService.gI().exitMap(player.pet);
            player.pet.dispose();
            player.pet = null;
        } else {
            limitPower = 1;
        }
        return limitPower;
    }

    public void createNormalPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Service.gI().chatJustForMe(player, player.pet, "Xin hãy thu nhận làm đệ tử");
            } catch (Exception e) {
                Logger.logException(PetService.class, e, "Error creating normal pet for player: " + player.name);
            }
        }).start();
    }

    public void createNormalPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, false);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Service.gI().chatJustForMe(player, player.pet, "Xin hãy thu nhận làm đệ tử");
            } catch (Exception e) {
                Logger.logException(PetService.class, e, "Error creating normal pet for player: " + player.name);
            }
        }).start();
    }

    public void createMabuPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, true, false, false);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Service.gI().chatJustForMe(player, player.pet, "Oa oa oa...");
            } catch (Exception e) {
                Logger.logException(PetService.class, e, "Error creating Mabu pet for player: " + player.name);
            }
        }).start();
    }

    public void createMabuPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, true, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Service.gI().chatJustForMe(player, player.pet, "Oa oa oa...");
            } catch (Exception e) {
                Logger.logException(PetService.class, e, "Error creating Mabu pet for player: " + player.name);
            }
        }).start();
    }

    public void createBerusPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, true, false);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                // Removed Thread.sleep to prevent server lag
                Service.getInstance().chatJustForMe(player, player.pet, "Thần hủy diệt hiện thân tất cả quỳ xuống...");
            } catch (Exception e) {

            }
        }).start();
    }

    public void createBerusPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, true, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                // Removed Thread.sleep to prevent server lag
                Service.getInstance().chatJustForMe(player, player.pet, "Thần hủy diệt hiện thân tất cả quỳ xuống...");
            } catch (Exception e) {

            }
        }).start();
    }

    public void createBrolyPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet1(player, true, false, false);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                // Removed Thread.sleep to prevent server lag
                Service.getInstance().chatJustForMe(player, player.pet, "Thần hủy diệt hiện thân tất cả quỳ xuống...");
            } catch (Exception e) {

            }
        }).start();
    }

    public void createBrolyPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet1(player, true, false, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                // Removed Thread.sleep to prevent server lag
                Service.getInstance().chatJustForMe(player, player.pet, "Thần hủy diệt hiện thân tất cả quỳ xuống...");
            } catch (Exception e) {

            }
        }).start();
    }

    public void creatUbbPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet1(player, false, true, false);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                // Removed Thread.sleep to prevent server lag
                Service.getInstance().chatJustForMe(player, player.pet, "Thần hủy diệt hiện thân tất cả quỳ xuống...");
            } catch (Exception e) {

            }
        }).start();
    }

    public void creatUbbPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet1(player, false, true, false, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                // Removed Thread.sleep to prevent server lag
                Service.getInstance().chatJustForMe(player, player.pet, "Thần hủy diệt hiện thân tất cả quỳ xuống...");
            } catch (Exception e) {

            }
        }).start();
    }

    public void creatXenConPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet1(player, false, false, true);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                // Removed Thread.sleep to prevent server lag
                Service.getInstance().chatJustForMe(player, player.pet, "Thần hủy diệt hiện thân tất cả quỳ xuống...");
            } catch (Exception e) {

            }
        }).start();
    }

    public void creatXenConPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet1(player, false, false, true, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                // Removed Thread.sleep to prevent server lag
                Service.getInstance().chatJustForMe(player, player.pet, "Thần hủy diệt hiện thân tất cả quỳ xuống...");
            } catch (Exception e) {

            }
        }).start();
    }

    public void createPicPet(Player player, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, true);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                // Removed Thread.sleep to prevent server lag
                Service.getInstance().chatJustForMe(player, player.pet, "Sư Phụ Broly hiện thân tụi mày quỳ xuống...");
            } catch (Exception e) {

            }
        }).start();
    }

    public void createPicPet(Player player, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, false, false, true, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                // Removed Thread.sleep to prevent server lag
                Service.getInstance().chatJustForMe(player, player.pet, "Sư Phụ Broly hiện thân tụi mày quỳ xuống...");
            } catch (Exception e) {

            }
        }).start();
    }

    public void changeNormalPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createNormalPet(player, gender, limitPower);
    }

    public void changeNormalPet(Player player) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createNormalPet(player, limitPower);
    }

    public void changeMabuPet(Player player) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createMabuPet(player, limitPower);
    }

    public void changeMabuPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createMabuPet(player, gender, limitPower);
    }

    public void changeBerusPet(Player player) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createBerusPet(player, limitPower);
    }

    public void changeBerusPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createBerusPet(player, gender, limitPower);
    }

    public void changeBrolyPet(Player player) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createBrolyPet(player, limitPower);
    }

    public void changeBrolyPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createBrolyPet(player, gender, limitPower);
    }

    public void changeUbbPet(Player player) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        creatUbbPet(player, limitPower);
    }

    public void changeUbbPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        creatUbbPet(player, gender, limitPower);
    }

    public void changeXenConPet(Player player) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        creatXenConPet(player, limitPower);
    }

    public void changeXenConPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        creatXenConPet(player, gender, limitPower);
    }

    public void changePicPet(Player player) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createPicPet(player, limitPower);
    }

    public void changePicPet(Player player, int gender) {
        byte limitPower = player.pet.nPoint.limitPower;
        if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            player.pet.unFusion();
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.dispose();
        player.pet = null;
        createPicPet(player, gender, limitPower);
    }

    public void changeNamePet(Player player, String name) {
        try {
            if (!InventoryServiceNew.gI().isExistItemBag(player, 400)) {
                Service.gI().sendThongBao(player, "Bạn Cần Thẻ Đặt Tên Đệ Tử, Mua Tại Bardock");
                return;
            } else if (Util.haveSpecialCharacter(name)) {
                Service.gI().sendThongBao(player, "Tên Không Được Chứa Ký Tự Đặc Biệt");
                return;
            } else if (name.length() > 10) {
                Service.gI().sendThongBao(player, "Tên Quá Dài");
                return;
            }
            
            InventoryServiceNew.gI().subQuantityItemsBag(player, ItemService.gI().createItemSetKichHoat(400, 1), 1);
            player.pet.name = name;
            Service.gI().Send_Caitrang(player);
            Service.gI().sendThongBao(player, "Đổi tên đệ tử thành công");
        } catch (Exception e) {
            Logger.logException(PetService.class, e, "Error changing pet name for player: " + player.name);
        }
    }

    private int[] getDataPetNormal() {
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 105) * 20; // hp
        petData[1] = Util.nextInt(40, 105) * 20; // mp
        petData[2] = Util.nextInt(20, 45); // dame
        petData[3] = Util.nextInt(9, 50); // def
        petData[4] = Util.nextInt(0, 2); // crit
        return petData;
    }

    private int[] getDataPetMabu() {
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 105) * 20; // hp
        petData[1] = Util.nextInt(40, 105) * 20; // mp
        petData[2] = Util.nextInt(50, 120); // dame
        petData[3] = Util.nextInt(9, 50); // def
        petData[4] = Util.nextInt(0, 2); // crit
        return petData;
    }

    private int[] getDataPetBerus() {
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 110) * 20; // hp
        petData[1] = Util.nextInt(40, 110) * 20; // mp
        petData[2] = Util.nextInt(50, 130); // dame
        petData[3] = Util.nextInt(9, 50); // def
        petData[4] = Util.nextInt(0, 2); // crit
        return petData;
    }

    private int[] getDataPetPic() {
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 125) * 20; // hp
        petData[1] = Util.nextInt(40, 125) * 20; // mp
        petData[2] = Util.nextInt(80, 160); // dame
        petData[3] = Util.nextInt(10, 60); // def
        petData[4] = Util.nextInt(2, 5); // crit
        return petData;
    }

    private int[] getDataXencon() {
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 125) * 20; // hp
        petData[1] = Util.nextInt(40, 125) * 20; // mp
        petData[2] = Util.nextInt(80, 160); // dame
        petData[3] = Util.nextInt(10, 60); // def
        petData[4] = Util.nextInt(2, 5); // crit
        return petData;
    }

    private int[] getDataPetKaido() {
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 115) * 20; // hp
        petData[1] = Util.nextInt(40, 115) * 20; // mp
        petData[2] = Util.nextInt(70, 140); // dame
        petData[3] = Util.nextInt(9, 50); // def
        petData[4] = Util.nextInt(0, 2); // crit
        return petData;
    }

    private void createNewPet(Player player, boolean isMabu, boolean isBerus, boolean isPic,
            byte... gender) {
        int[] data = isBerus ? isMabu ? isPic ? getDataPetBerus() : getDataPetMabu() : getDataPetPic()
                : getDataPetNormal();
        Pet pet = new Pet(player);
        pet.name = "$" + (isMabu ? "Mabư" : isBerus ? "Berus" : isPic ? "Pic" : "Đệ tử");
        pet.gender = (gender != null && gender.length != 0) ? gender[0] : (byte) Util.nextInt(0, 2);
        pet.id = -player.id;
        pet.nPoint.power = isMabu || isBerus || isPic ? 1500000 : 2000;
        pet.typePet = (byte) (isMabu ? 1 : isBerus ? 2 : isPic ? 3 : 0);
        pet.nPoint.stamina = 1000;
        pet.nPoint.maxStamina = 1000;
        pet.nPoint.hpg = data[0];
        pet.nPoint.mpg = data[1];
        pet.nPoint.dameg = data[2];
        pet.nPoint.defg = data[3];
        pet.nPoint.critg = data[4];
        for (int i = 0; i < 7; i++) {
            pet.inventory.itemsBody.add(ItemService.gI().createItemNull());
        }
        pet.playerSkill.skills.add(SkillUtil.createSkill(Util.nextInt(0, 2) * 2, 1));
        for (int i = 0; i < 4; i++) {
            pet.playerSkill.skills.add(SkillUtil.createEmptySkill());
        }
        pet.nPoint.setFullHpMp();
        player.pet = pet;
    }

    private void createNewPet1(Player player, boolean isBroly, boolean isUbb, boolean isXencon, byte... gender) {
        int[] data = isBroly ? isUbb ? isXencon ? getDataPetKaido() : getDataPetPic() : getDataPetBerus()
                : getDataPetNormal();
        Pet pet = new Pet(player);
        pet.name = "$" + (isBroly ? "Broly" : isUbb ? "Ubb" : isXencon ? "Xên Con" : "Đệ tử");
        pet.gender = (gender != null && gender.length != 0) ? gender[0] : (byte) Util.nextInt(0, 2);
        pet.id = -player.id;
        pet.nPoint.power = isBroly || isUbb || isXencon ? 1500000 : 2000;
        pet.typePet = (byte) (isBroly ? 3 : isUbb ? 4 : isXencon ? 5 : 0);
        pet.nPoint.stamina = 1000;
        pet.nPoint.maxStamina = 1000;
        pet.nPoint.hpg = data[0];
        pet.nPoint.mpg = data[1];
        pet.nPoint.dameg = data[2];
        pet.nPoint.defg = data[3];
        pet.nPoint.critg = data[4];
        for (int i = 0; i < 7; i++) {
            pet.inventory.itemsBody.add(ItemService.gI().createItemNull());
        }
        pet.playerSkill.skills.add(SkillUtil.createSkill(Util.nextInt(0, 2) * 2, 1));
        for (int i = 0; i < 3; i++) {
            pet.playerSkill.skills.add(SkillUtil.createEmptySkill());
        }
        pet.nPoint.setFullHpMp();
        player.pet = pet;
    }

    public static void Pet2(Player pl, int h, int b, int l) {
        if (pl.newpet != null) {
            pl.newpet.dispose();
        }
        pl.newpet = new NewPet(pl, (short) h, (short) b, (short) l);
        pl.newpet.name = pl.inventory.itemsBody.get(7).template.name;
        pl.newpet.gender = pl.gender;
        pl.newpet.nPoint.tiemNang = 1;
        pl.newpet.nPoint.power = 80000000000L;
        pl.newpet.nPoint.limitPower = 10;
        pl.newpet.nPoint.hpg = 5000;
        pl.newpet.nPoint.mpg = 5000;
        pl.newpet.nPoint.hp = 5000;
        pl.newpet.nPoint.mp = 5000;
        pl.newpet.nPoint.dameg = 1;
        pl.newpet.nPoint.defg = 1;
        pl.newpet.nPoint.critg = 1;
        pl.newpet.nPoint.stamina = 1;
        pl.newpet.nPoint.setBasePoint();
        pl.newpet.nPoint.setFullHpMp();
    }

    public static void Thu_TrieuHoi(Player pl) {
        if (pl.TrieuHoipet != null) {
            pl.TrieuHoipet.dispose();
        }
        pl.TrieuHoipet = new Thu_TrieuHoi(pl);
        pl.TrieuHoipet.name = "$" + "[" + pl.NameThanthu(pl.CapBacThan) + "] " + pl.TenThan;
        pl.TrieuHoipet.gender = pl.gender;
        pl.TrieuHoipet.nPoint.tiemNang = 1;
        pl.TrieuHoipet.nPoint.power = 1;
        pl.TrieuHoipet.nPoint.limitPower = 1;
        pl.TrieuHoipet.nPoint.hpg = pl.MauThan;
        pl.TrieuHoipet.nPoint.mpg = 500000000;
        pl.TrieuHoipet.nPoint.hp = pl.MauThan;
        pl.TrieuHoipet.nPoint.mp = 500000000;
        pl.TrieuHoipet.nPoint.dameg = pl.DameThan;
        pl.TrieuHoipet.nPoint.defg = 1;
        pl.TrieuHoipet.nPoint.critg = 1;
        pl.TrieuHoipet.nPoint.stamina = 10000;
        pl.TrieuHoipet.nPoint.maxStamina = 10000;
        pl.TrieuHoipet.playerSkill.skills.add(SkillUtil.createSkill(17, 7));
        pl.TrieuHoipet.nPoint.setBasePoint();
        pl.TrieuHoipet.nPoint.setFullHpMp();
    }

    // --------------------------------------------------------------------------
}
