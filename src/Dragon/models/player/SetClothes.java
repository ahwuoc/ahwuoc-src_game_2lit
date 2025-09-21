package Dragon.models.player;

import Dragon.models.item.Item;
import Dragon.services.Service;

public class SetClothes {

    private Player player;

    public SetClothes(Player player) {
        this.player = player;
    }

    public byte songoku;
    public byte thienXinHang;
    public byte kirin;

    public byte ocTieu;
    public byte pikkoroDaimao;
    public byte picolo;

    public byte kakarot;
    public byte cadic;
    public byte nappa;

    public byte setBom;
    public byte set8;

    public byte tromcho;

    public byte worldcup;
    public byte setDHD;
    public byte thienSuClothes = 0;
    public boolean godClothes;
    public int ctHaiTac = -1;

    public void setup() {
        setDefault();
        setupSKT();
        this.godClothes = true;
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id > 567 || item.template.id < 555) {
                    this.godClothes = false;
                    break;
                }
            } else {
                this.godClothes = false;
                break;
            }
        }
        Item ct = this.player.inventory.itemsBody.get(5);
        if (ct.isNotNullItem()) {
            switch (ct.template.id) {
                case 618:
                case 619:
                case 620:
                case 621:
                case 622:
                case 623:
                case 624:
                case 626:
                case 627:
                    this.ctHaiTac = ct.template.id;
                    break;

            }
        }
    }

    public boolean IsSetThienSu() {
        int[][] DoThienSu = new int[][]{
            {1048, 1051, 1054, 1057, 1060}, // td
            {1049, 1052, 1055, 1058, 1060}, // namec
            {1050, 1053, 1056, 1059, 1060},// xayda
        };
        int z = 0;
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id == DoThienSu[this.player.gender][i]) {
                    z++;
                } else {
                    return false;
                }
            } else {
                return false;
            }

        }
        return z == 5;
    }

    public boolean IsSetHuyDiet() {
        int[][] DoHuyDiet = new int[][]{
            {650, 651, 657, 658, 656}, // td
            {652, 653, 659, 660, 656}, // namec
            {654, 655, 661, 662, 656},// xayda
        };
        int z = 0;
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id == DoHuyDiet[this.player.gender][i]) {
                    z++;
                } else {
                    return false;
                }
            } else {
                return false;
            }

        }
        return z == 5;
    }

    private void setupSKT() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 129:
                        case 141:
                            isActSet = true;
                            songoku++;
                            break;
                        case 127:
                        case 139:
                            isActSet = true;
                            thienXinHang++;
                            break;
                        case 128:
                        case 140:
                            isActSet = true;
                            kirin++;
                            break;
                        case 131:
                        case 143:
                            isActSet = true;
                            ocTieu++;
                            break;
                        case 132:
                        case 144:
                            isActSet = true;
                            pikkoroDaimao++;
                            break;
                        case 130:
                        case 142:
                            isActSet = true;
                            picolo++;
                            break;
                        case 135:
                        case 138:
                            isActSet = true;
                            nappa++;
                            break;
                        case 133:
                        case 136:
                            isActSet = true;
                            kakarot++;
                            break;
                        case 134:
                        case 137:
                            isActSet = true;
                            cadic++;
                            break;
                        case 188:
                        case 189:
                            isActSet = true;
                            tromcho++;
                            break;
                        case 72:
                            if (io.param == 8) {
                                isActSet = true;
                                set8++;
                            }
                            break;
                        case 21:
                            if (io.param == 80) {
                                setDHD++;
                            }
                            break;
                        case 190: // Bomb set option
                            isActSet = true;
                            setBom++;
                            break;
                    }

                    if (isActSet) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void setDefault() {
        this.songoku = 0;
        this.thienXinHang = 0;
        this.kirin = 0;
        this.ocTieu = 0;
        this.pikkoroDaimao = 0;
        this.picolo = 0;
        this.kakarot = 0;
        this.cadic = 0;
        this.nappa = 0;
        this.setDHD = 0;
        this.worldcup = 0;
        this.godClothes = false;
        this.ctHaiTac = -1;
        this.tromcho = 0;
        this.set8 = 0;
        this.setBom = 0;
    }

    public boolean isSetBomComplete() {
        return setBom == 5;
    }

    public double getBomDamageMultiplier() {
        return isSetBomComplete() ? 3.0 : 1.0; // 200% increase = 3x damage
    }

    public void dispose() {
        this.player = null;
    }
}
