package Dragon.models.boss;

import Dragon.jdbc.daos.GodGK;
import Dragon.models.boss.list_boss.AnTrom;
import Dragon.models.boss.list_boss.BLACK.*;
import Dragon.models.boss.list_boss.Cooler.Cooler;
import Dragon.models.boss.list_boss.Doraemon.Doraemon;
import Dragon.models.boss.list_boss.FideBack.Kingcold;
import Dragon.models.boss.list_boss.Mabu;
import Dragon.models.boss.list_boss.cell.Xencon;
import Dragon.models.boss.list_boss.ginyu.Tieudoitruong;
import Dragon.models.boss.list_boss.android.*;
import Dragon.models.boss.list_boss.cell.SieuBoHung;
import Dragon.models.boss.list_boss.cell.XenBoHung;
import Dragon.models.boss.list_boss.doanh_trai.*;
import Dragon.models.boss.list_boss.Broly.Broly;
import Dragon.models.boss.list_boss.Doraemon.Nobita;
import Dragon.models.boss.list_boss.Doraemon.Xeko;
import Dragon.models.boss.list_boss.Doraemon.Xuka;
import Dragon.models.boss.list_boss.FideBack.FideRobot;
import Dragon.models.boss.list_boss.fide.Fide;
import Dragon.models.boss.list_boss.Doraemon.Chaien;
import Dragon.models.boss.list_boss.NRD.Rong1Sao;
import Dragon.models.boss.list_boss.NRD.Rong2Sao;
import Dragon.models.boss.list_boss.NRD.Rong3Sao;
import Dragon.models.boss.list_boss.NRD.Rong4Sao;
import Dragon.models.boss.list_boss.NRD.Rong5Sao;
import Dragon.models.boss.list_boss.NRD.Rong6Sao;
import Dragon.models.boss.list_boss.NRD.Rong7Sao;
import Dragon.models.boss.list_boss.Kaido.KAIDO1;
import Dragon.models.boss.list_boss.Mabu12h.MabuBoss;
import Dragon.models.boss.list_boss.Mabu12h.BuiBui;
import Dragon.models.boss.list_boss.Mabu12h.BuiBui2;
import Dragon.models.boss.list_boss.Mabu12h.Drabura;
import Dragon.models.boss.list_boss.Mabu12h.Drabura2;
import Dragon.models.boss.list_boss.Mabu12h.Yacon;
import Dragon.models.boss.list_boss.Rungboss.bill;
import Dragon.models.boss.list_boss.Rungboss.bill_1;
import Dragon.models.boss.list_boss.Rungboss.bill_2;
import Dragon.models.boss.list_boss.Rungboss.whis;
import Dragon.models.boss.list_boss.Rungboss.whis_2;
import Dragon.models.boss.list_boss.nappa.*;
import Dragon.yadat.*;
import Dragon.models.map.Zone;
import Dragon.models.player.Player;
import com.girlkun.network.io.Message;
import Dragon.server.ServerManager;
import Dragon.services.ItemMapService;
import Dragon.services.MapService;
import Dragon.models.boss.list_boss.cell.xensandetu;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import Dragon.models.boss.list_boss.AnTrom;
import Dragon.models.boss.list_boss.Kaido.GokuSsj4;
import Dragon.models.boss.list_boss.KhiXayDa;
import Dragon.models.boss.list_boss.ThoDaiCa;
import Dragon.models.boss.list_boss.TrainOffline.Bubbles;
import Dragon.models.boss.list_boss.TrainOffline.Popo;
import Dragon.models.boss.list_boss.TrainOffline.Yajiro;
import Dragon.models.boss.list_boss.bosssukien.gachincua;
import Dragon.models.boss.list_boss.bosssukien.gozila;
import Dragon.models.boss.list_boss.bosssukien.kong;
import Dragon.models.boss.list_boss.bosssukien.ngualomao;
import Dragon.models.boss.list_boss.bosssukien.voichinnga;
import Dragon.models.boss.list_boss.phuoc.phuocboss1;
import Dragon.models.boss.list_boss.phuoc.phuocboss2;
import Dragon.models.boss.list_boss.phuoc.phuocboss3;
import Dragon.models.boss.list_boss.phuoc.phuocboss4;
import Dragon.models.boss.list_boss.ginyu.So1;
import Dragon.models.boss.list_boss.ginyu.So2;
import Dragon.models.boss.list_boss.ginyu.So3;
import Dragon.models.boss.list_boss.ginyu.So4;
import Dragon.models.boss.list_boss.sanca.Sanca;
import Dragon.services.Service;
import Dragon.services.func.ChangeMapService;
import Dragon.utils.Util;
import java.io.IOException;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class BossManager implements Runnable {

    private static BossManager I;
    public static final byte ratioReward = 50;

    public static BossManager gI() {
        if (BossManager.I == null) {
            BossManager.I = new BossManager();
        }
        return BossManager.I;
    }

    private BossManager() {
        this.bosses = new ArrayList<>();
    }

    private boolean loadedBoss;
    private final List<Boss> bosses;

    public List<Boss> getBosses() {
        return this.bosses;
    }

    public void addBoss(Boss boss) {
        this.bosses.add(boss);
    }

    public void removeBoss(Boss boss) {
        this.bosses.remove(boss);
    }

    public void loadBoss() {
        if (this.loadedBoss) {
            return;
        }
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.loadedBoss = true;
        new Thread(BossManager.I, "Update boss").start();
    }

    public void loadMultipleBosses(int bossId, int quantity) {
        try {
            int successCount = 0;
            for (int i = 0; i < quantity; i++) {
                Boss boss = this.createBoss(bossId);
                if (boss != null) {
                    successCount++;
                    System.out.println("[BossManager] Boss " + (i + 1) + "/" + quantity + " created: " + boss.name);
                } else {
                    System.out.println("[BossManager] Failed to create boss " + (i + 1) + "/" + quantity + " (ID: "
                            + bossId + ")");
                }
            }
            System.out.println("[BossManager] Total created: " + successCount + "/" + quantity + " bosses");
        } catch (Exception e) {
            System.out.println("[BossManager] Exception loading multiple bosses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadBossArray(int[] bossIds) {
        try {
            System.out.println("[BossManager] Loading boss array with " + bossIds.length + " different IDs");
            int successCount = 0;
            for (int i = 0; i < bossIds.length; i++) {
                Boss boss = this.createBoss(bossIds[i]);
                if (boss != null) {
                    successCount++;
                    System.out.println("[BossManager] Boss " + (i + 1) + "/" + bossIds.length + " created: " + boss.name
                            + " (ID: " + bossIds[i] + ")");
                } else {
                    System.out.println("[BossManager] Failed to create boss " + (i + 1) + "/" + bossIds.length
                            + " (ID: " + bossIds[i] + ")");
                }
            }
            System.out.println("[BossManager] Total created: " + successCount + "/" + bossIds.length + " bosses");
        } catch (Exception e) {
            System.out.println("[BossManager] Exception loading boss array: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadBossesWithQuantities(int[] bossIds, int[] quantities) {
        try {
            if (bossIds.length != quantities.length) {
                throw new IllegalArgumentException("Boss IDs and quantities arrays must have the same length");
            }

            int totalBosses = 0;
            for (int qty : quantities) {
                totalBosses += qty;
            }
            System.out.println("[BossManager] Loading " + totalBosses + " bosses with custom quantities");

            int successCount = 0;
            for (int i = 0; i < bossIds.length; i++) {
                System.out.println("[BossManager] Creating " + quantities[i] + " bosses of ID: " + bossIds[i]);
                for (int j = 0; j < quantities[i]; j++) {
                    Boss boss = this.createBoss(bossIds[i]);
                    if (boss != null) {
                        successCount++;
                        System.out.println("[BossManager] Boss " + (j + 1) + "/" + quantities[i] + " created: "
                                + boss.name + " (ID: " + bossIds[i] + ")");
                    } else {
                        System.out.println("[BossManager] Failed to create boss " + (j + 1) + "/" + quantities[i]
                                + " (ID: " + bossIds[i] + ")");
                    }
                }
            }
            System.out.println("[BossManager] Total created: " + successCount + "/" + totalBosses + " bosses");
        } catch (Exception e) {
            System.out.println("[BossManager] Exception loading bosses with quantities: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Boss createBossBdkb(int bossID, int dame, int hp, Zone zone) {
        try {
            switch (bossID) {
                default:
                    return null;
            }
        } catch (Exception e) {

            return null;
        }
    }

    public Boss createBoss(int bossID) {
        try {
            if (bossID <= BossID.BROLY_THUONG + 5 && bossID >= BossID.BROLY_THUONG) {

                return new Broly(MapService.gI().getZone(Util.randomMapBossBroly()), 500, 10000);
            }
            switch (bossID) {
                case BossID.PHUOCBOSS1:
                    return new phuocboss1();
                case BossID.PHUOCBOSS2:
                    return new phuocboss2();
                case BossID.PHUOCBOSS3:
                    return new phuocboss3();
                case BossID.PHUOCBOSS4:
                    return new phuocboss4();
                case BossID.ANDROID_13:
                    return new Android13();
                case BossID.ANDROID_14:
                    return new Android14();
                case BossID.ANDROID_15:
                    return new Android15();
                case BossID.ANDROID_19:
                    return new Android19();
                case BossID.ANTROM:
                    return new AnTrom();
                case BossID.KUKU:
                    return new Kuku();
                case BossID.MAP_DAU_DINH:
                    return new MapDauDinh();
                case BossID.RAMBO:
                    return new Rambo();
                case BossID.DRABURA:
                    return new Drabura();
                case BossID.DRABURA_2:
                    return new Drabura2();
                case BossID.BUI_BUI:
                    return new BuiBui();
                case BossID.BUI_BUI_2:
                    return new BuiBui2();
                case BossID.YA_CON:
                    return new Yacon();
                case BossID.MABU_12H:
                    return new MabuBoss();
                case BossID.Rong_1Sao:
                    return new Rong1Sao();
                case BossID.Rong_2Sao:
                    return new Rong2Sao();
                case BossID.Rong_3Sao:
                    return new Rong3Sao();
                case BossID.Rong_4Sao:
                    return new Rong4Sao();
                case BossID.Rong_5Sao:
                    return new Rong5Sao();
                case BossID.Rong_6Sao:
                    return new Rong6Sao();
                case BossID.Rong_7Sao:
                    return new Rong7Sao();
                case BossID.FIDE:
                    return new Fide();
                case BossID.DR_KORE:
                    return new DrKore();
                case BossID.PIC:
                    return new Pic();
                case BossID.POC:
                    return new Poc();
                case BossID.KING_KONG:
                    return new KingKong();
                case BossID.XEN_BO_HUNG:
                    return new XenBoHung();
                case BossID.SIEU_BO_HUNG:
                    return new SieuBoHung();
                case BossID.VUA_COLD:
                    return new Kingcold();
                case BossID.FIDE_ROBOT:
                    return new FideRobot();
                case BossID.COOLER:
                    return new Cooler();
                case BossID.ZAMASZIN:
                    return new ZamasKaio();
                case BossID.BLACK2:
                    return new SuperBlack2();
                case BossID.BLACK1:
                    return new BlackGokuTl();
                case BossID.BILL:
                    return new bill();
                case BossID.WHIS:
                    return new whis();
                case BossID.BILL1:
                    return new bill_1();
                case BossID.WHIS1:
                    return new whis();
                case BossID.BILL2:
                    return new bill_2();
                case BossID.WHIS2:
                    return new whis_2();
                case BossID.BLACK:
                    return new Black();
                case BossID.BLACK3:
                    return new BlackGokuBase();
                case BossID.XEN_CON_1:
                    return new Xencon();
                case BossID.MABU:
                    return new Mabu();
                case BossID.TIEU_DOI_TRUONG:
                    return new Tieudoitruong();
                case BossID.SO_4:
                    return new So4();
                case BossID.SO_3:
                    return new So3();
                case BossID.SO_2:
                    return new So2();
                case BossID.SO_1:
                    return new So1();
                case BossID.XEN_SAN_DE_TU:
                    return new xensandetu();
                case BossID.BOSS_YADAT:
                    return new yadatprovip();
                case BossID.BOSS_YADAT1:
                    return new yadatprovip1();
                case BossID.BOSS_YADAT3:
                    return new yadatprovip3();
                case BossID.KAIDO:
                    return new KAIDO1();
                case BossID.YARI:
                    return new Yajiro();
                case BossID.MR_POPO:
                    return new Popo();
                case BossID.BUBBLES:
                    return new Bubbles();
                case BossID.KONG:
                    return new kong();
                case BossID.GOZILA:
                    return new gozila();
                case BossID.VOICHINNGA:
                    return new voichinnga();
                case BossID.GACHINCUA:
                    return new gachincua();
                case BossID.NGUALOMAO:
                    return new ngualomao();
                case BossID.XUKA:
                    return new Xuka();
                case BossID.NOBITA:
                    return new Nobita();
                case BossID.XEKO:
                    return new Xeko();
                case BossID.CHAIEN:
                    return new Chaien();
                case BossID.DORAEMON:
                    return new Doraemon();
                case BossID.GOKU_SSJ4:
                    return new GokuSsj4();
                case BossID.THO_DAI_CA:
                    return new ThoDaiCa();
                case BossID.SAN_CA:
                    return new Sanca();
                case BossID.KHIXAYDA:
                    return new KhiXayDa();
                default:
                    return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean existBossOnPlayer(Player player) {
        return player.zone.getBosses().size() > 0;
    }

    public void teleBoss(Player pl, Message _msg) {
        if (_msg != null) {
            try {
                int id = _msg.reader().readInt();
                Boss b = getBossById(id);
                if (b == null) {
                    Player player = GodGK.loadById(id);
                    if (player != null && player.zone != null) {
                        ChangeMapService.gI().changeMapYardrat(pl, player.zone, player.location.x, player.location.y);
                        return;
                    } else {
                        Service.gI().sendThongBao(pl, "Nó trốn rồi");
                        return;
                    }
                }
                if (!b.isDie()) {
                    boolean present = isBossInZone(b);
                    if (!present && b.zone != null) {
                        try {
                            ChangeMapService.gI().changeMapYardrat(b, b.zone, b.location.x, b.location.y);
                            present = isBossInZone(b);
                        } catch (Exception ignored) {
                        }
                    }
                    if (present) {
                        ChangeMapService.gI().changeMapYardrat(pl, b.zone, b.location.x, b.location.y);
                    } else {
                        Service.gI().sendThongBao(pl, "Nó trốn rồi");
                    }
                } else {
                    Service.gI().sendThongBao(pl, "Boss Hẹo Rồi");
                }
            } catch (IOException e) {
                System.out.println("Loi tele boss");
                e.printStackTrace();
            }
        }
    }

    public void summonBoss(Player pl, Message _msg) {
        if (!pl.getSession().isAdmin) {
            Service.gI().sendThongBao(pl, "Chỉ dành cho Admin");
            return;
        }
        if (_msg != null) {
            try {
                int id = _msg.reader().readInt();
                Boss b = getBossById(id);
                if (b != null && b.zone != null) {
                    ChangeMapService.gI().changeMapYardrat(b, pl.zone, pl.location.x, pl.location.y);
                    return;
                }
                if (b == null) {
                    Player player = GodGK.loadById(id);
                    if (player != null && player.zone != null) {
                        ChangeMapService.gI().changeMapYardrat(player, pl.zone, pl.location.x, pl.location.y);
                    } else {
                        Service.gI().sendThongBao(pl, "Nó trốn rồi");
                    }
                }
            } catch (IOException e) {
                System.out.println("Loi summon boss");
            }
        }
    }

    public void showListBoss(Player player) {
        if (!player.isAdmin()) {
            return;
        }
        Message msg;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(1);
            msg.writer().writeUTF("Xem Boss");
            msg.writer()
                    .writeByte(
                            (int) bosses.stream()
                                    .filter(boss -> {
                                        int map0 = safeFirstMapId(boss);
                                        if (map0 == -1) {
                                            return false;
                                        }
                                        return !MapService.gI().isMapMaBu(map0)
                                                && !MapService.gI().isMapDoanhTrai(map0)
                                                && !MapService.gI().isMapBlackBallWar(map0);
                                    })
                                    .count());
            for (int i = 0; i < bosses.size(); i++) {
                Boss boss = this.bosses.get(i);
                int map0 = safeFirstMapId(boss);
                if (map0 == -1
                        || MapService.gI().isMapMaBu(map0)
                        || MapService.gI().isMapBlackBallWar(map0)
                        || MapService.gI().isMapBanDoKhoBau(map0)
                        || MapService.gI().isMapDoanhTrai(map0)) {
                    continue;
                }
                msg.writer().writeInt((int) boss.id);
                msg.writer().writeInt((int) boss.id);
                msg.writer().writeShort(boss.data[0].getOutfit()[0]);
                if (player.getSession().version == 15) {// version
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(boss.data[0].getOutfit()[1]);
                msg.writer().writeShort(boss.data[0].getOutfit()[2]);
                msg.writer().writeUTF(boss.data[0].getName());
                if (isBossInZone(boss)) {
                    msg.writer().writeUTF("Sống");
                    msg.writer()
                            .writeUTF("Thông Tin Boss\n" + "|7|Map : " + boss.zone.map.mapName + "("
                                    + boss.zone.map.mapId + ") \nZone: " + boss.zone.zoneId + "\nHP: "
                                    + Util.powerToString((long) boss.nPoint.hp) + "\nDame: "
                                    + Util.powerToString((long) boss.nPoint.dame));
                } else {
                    msg.writer().writeUTF("Chết");
                    msg.writer().writeUTF("Boss Respawn\n|7|Time to Reset : "
                            + (boss.secondsRest <= 0 ? "BossAppear" : boss.secondsRest + " giây"));
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dobossmember(Player player) {
        Message msg;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Boss");
            msg.writer()
                    .writeByte((int) bosses.stream()
                            .filter(boss -> {
                                int map0 = safeFirstMapId(boss);
                                if (map0 == -1) {
                                    return false;
                                }
                                return !MapService.gI().isMapMaBu(map0)
                                        && !MapService.gI().isMapDoanhTrai(map0)
                                        && !(boss instanceof AnTrom)
                                        && !MapService.gI().isMapBanDoKhoBau(map0)
                                        && !MapService.gI().isMapKhiGas(map0)
                                        && !MapService.gI().isMapBlackBallWar(map0);
                            })
                            .count());
            for (int i = 0; i < bosses.size(); i++) {
                Boss boss = this.bosses.get(i);
                int map0 = safeFirstMapId(boss);
                if (map0 == -1
                        || MapService.gI().isMapMaBu(map0)
                        || boss instanceof AnTrom
                        || MapService.gI().isMapBlackBallWar(map0)
                        || MapService.gI().isMapDoanhTrai(map0)
                        || MapService.gI().isMapBanDoKhoBau(map0)
                        || MapService.gI().isMapKhiGas(map0)) {
                    continue;
                }
                msg.writer().writeInt((int) boss.id);
                msg.writer().writeInt((int) boss.id);
                msg.writer().writeShort(boss.data[0].getOutfit()[0]);
                if (player.getSession().version == 15) {// version
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(boss.data[0].getOutfit()[1]);
                msg.writer().writeShort(boss.data[0].getOutfit()[2]);
                msg.writer().writeUTF(boss.data[0].getName());
                if (isBossInZone(boss)) {
                    msg.writer().writeUTF("Sống");
                    msg.writer()
                            .writeUTF("Thông Tin\n" + "|7|Bản Đồ: " + boss.zone.map.mapName + "\nKhu Vực: "
                                    + boss.zone.zoneId + "\nMáu: " + Util.powerToString((long) boss.nPoint.hp)
                                    + "\nDame: " + Util.powerToString((long) boss.nPoint.dame));
                } else {
                    msg.writer().writeUTF("Chết");
                    msg.writer().writeUTF("Boss Hồi Sinh\n|7|Thời Gian Hồi Sinh : "
                            + (boss.secondsRest <= 0 ? "Boss Biến Mất" : boss.secondsRest + " Giây"));
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private int safeFirstMapId(Boss boss) {
        try {
            if (boss == null || boss.data == null || boss.data.length == 0) {
                return -1;
            }
            int[] mj = boss.data[0].getMapJoin();
            if (mj == null || mj.length == 0) {
                return -1;
            }
            // Prefer the first map that is NOT filtered by UI rules
            for (int mapId : mj) {
                if (!MapService.gI().isMapMaBu(mapId)
                        && !MapService.gI().isMapBlackBallWar(mapId)
                        && !MapService.gI().isMapBanDoKhoBau(mapId)
                        && !MapService.gI().isMapDoanhTrai(mapId)
                        && !MapService.gI().isMapKhiGas(mapId)) {
                    return mapId;
                }
            }
            // None suitable -> indicate hidden
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    public synchronized void callBoss(Player player, int mapId) {
        try {
            if (BossManager.gI().existBossOnPlayer(player)
                    || player.zone.items.stream()
                            .anyMatch(itemMap -> ItemMapService.gI().isBlackBall(itemMap.itemTemplate.id))
                    || player.zone.getPlayers().stream().anyMatch(p -> p.iDMark.isHoldBlackBall())) {
                return;
            }
            Boss k = null;
            switch (mapId) {
                case 16:
                    k = BossManager.gI().createBoss(BossID.Rong_1Sao);
                    break;
                case 17:
                    k = BossManager.gI().createBoss(BossID.Rong_2Sao);
                    break;
                case 18:
                    k = BossManager.gI().createBoss(BossID.Rong_3Sao);
                    break;
                case 19:
                    k = BossManager.gI().createBoss(BossID.Rong_4Sao);
                    break;
                case 20:
                    k = BossManager.gI().createBoss(BossID.Rong_5Sao);
                    break;
                case 21:
                    k = BossManager.gI().createBoss(BossID.Rong_6Sao);
                    break;
                case 22:
                    k = BossManager.gI().createBoss(BossID.Rong_7Sao);
                    break;
            }
            if (k != null) {
                k.currentLevel = 0;
                k.joinMapByZone(player);
            }
        } catch (Exception e) {

        }
    }

    public Boss getBossById(int bossId) {
        return BossManager.gI().bosses.stream().filter(boss -> boss.id == bossId && !boss.isDie()).findFirst()
                .orElse(null);
    }

    public static String covertString(String value) {
        String temp = Normalizer.normalize(value, Normalizer.Form.NFD);
        try {
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("");
        } catch (Exception ex) {
            return temp;
        }
    }

    public Boss getBossByName(String name) {
        try {
            for (Boss boss : this.bosses) {
                if (boss.currentLevel > 0) {
                    if (covertString(boss.data[0].getName()).equalsIgnoreCase(covertString(name))) {
                        return boss;
                    }
                }
                if (boss.name == null) {
                    continue;
                }
                if (covertString(boss.name).equalsIgnoreCase(covertString(name))) {
                    return boss;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public void run() {
        while (ServerManager.isRunning) {
            try {
                long st = System.currentTimeMillis();
                // Duyệt trên snapshot để tránh ConcurrentModification khi thêm/xóa boss
                List<Boss> snapshot = new ArrayList<>(this.bosses);
                for (Boss boss : snapshot) {
                    try {
                        // Watchdog: nếu boss có zone nhưng không có trong danh sách bosses của zone ->
                        // tự sửa
                        if (boss != null && boss.zone != null) {
                            boolean registered = false;
                            try {
                                registered = boss.zone.getBosses().contains(boss);
                            } catch (Exception ig) {
                                registered = false;
                            }
                            if (!registered) {
                                // cố gắng đưa boss vào lại zone hiện tại
                                try {
                                    ChangeMapService.gI().changeMapYardrat(boss, boss.zone, boss.location.x,
                                            boss.location.y);
                                } catch (Exception t) {
                                    System.out.println(
                                            "[BossManager] Auto-repair boss not registered in zone: id=" + boss.id);
                                }
                            }
                        }
                        boss.update();
                    } catch (Exception ex) {
                        // Không để 1 boss lỗi chặn cả vòng lặp
                        System.out
                                .println("[BossManager] Boss update error for id=" + boss.id + ": " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                long elapsed = System.currentTimeMillis() - st;
                long delay = 150 - elapsed;
                if (delay < 5) {
                    delay = 5; // clamp tránh giá trị âm hoặc quá nhỏ
                }
                Thread.sleep(delay);
            } catch (Exception e) {
                // swallow và tiếp tục vòng lặp
            }

        }
    }

    // Helpers
    private boolean isBossInZone(Boss boss) {
        try {
            return boss != null && boss.zone != null && boss.zone.getBosses() != null
                    && boss.zone.getBosses().contains(boss);
        } catch (Exception e) {
            return false;
        }
    }
}
