package Dragon.jdbc.daos;

import Dragon.models.player.Player;
import Dragon.models.player.Pet;
import Dragon.models.item.Item;
import Dragon.models.clan.Clan;
import Dragon.models.clan.ClanMember;
import Dragon.models.player.Friend;
import Dragon.models.player.Enemy;
import Dragon.models.task.TaskMain;
import Dragon.models.npc.specialnpc.MabuEgg;
import Dragon.models.npc.specialnpc.BillEgg;
import Dragon.models.npc.specialnpc.MagicTree;
import Dragon.models.item.ItemTime;
import Dragon.models.player.Fusion;
import Dragon.card.Card;
import Dragon.models.skill.Skill;
import Dragon.services.*;
import Dragon.server.Client;
import Dragon.server.Manager;
import Dragon.server.ServerManager;
import Dragon.consts.ConstPlayer;
import Dragon.utils.TimeUtil;
import Dragon.utils.SkillUtil;
import com.girlkun.database.GirlkunDB;
import com.girlkun.result.GirlkunResultSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.util.*;

/**
 * Refactored Player Data Loader để loại bỏ duplicate code
 */
public class PlayerDataLoader {

    public enum LoadType {
        FULL_LOGIN, // Load đầy đủ cho login
        FULL_BY_ID, // Load đầy đủ theo ID
        SIEU_HANG_ONLY // Load rút gọn cho siêu hạng
    }

    /**
     * Load player data theo type khác nhau
     */
    public static Player loadPlayer(GirlkunResultSet rs, LoadType loadType) throws Exception {
        Player player = new Player();
        loadBaseInfo(player, rs, loadType);
        switch (loadType) {
            case FULL_LOGIN:
                loadFullLoginData(player, rs);
                break;
            case FULL_BY_ID:
                loadFullByIdData(player, rs);
                break;
            case SIEU_HANG_ONLY:
                loadSieuHangOnlyData(player, rs);
                break;
        }

        return player;
    }

    /**
     * Load thông tin cơ bản chung cho tất cả
     */
    private static void loadBaseInfo(Player player, GirlkunResultSet rs, LoadType loadType) throws Exception {
        player.id = rs.getInt("id");
        player.name = rs.getString("name");
        player.head = rs.getShort("head");
        player.gender = rs.getByte("gender");

        // Các field đặc biệt theo type
        if (loadType == LoadType.FULL_LOGIN) {
            player.PointBoss = rs.getInt("PointBoss");
            player.ResetSkill = rs.getInt("ResetSkill");
            player.LastDoanhTrai = rs.getLong("LastDoanhTrai");
            player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");
            player.violate = rs.getInt("violate");
            player.pointPvp = rs.getInt("pointPvp");
            player.NguHanhSonPoint = rs.getInt("NguHanhSonPoint");
            player.point_gapthu = rs.getInt("point_gapthu");
            player.point_vnd = rs.getInt("point_vnd");
            player.thankhi = rs.getInt("thankhi");
            player.blackballdata = rs.getInt("blackballdata");
            player.ChuyenSinh = rs.getInt("ChuyenSinh");
            player.capboss = rs.getInt("capboss");
            player.kemtraicay = rs.getInt("kemtraicay");
            player.nuocmia = rs.getInt("nuocmia");
            player.Captutien = rs.getInt("Captutien");
            player.Exptutien = rs.getLong("Exptutien");
            player.luotNhanBuaMienPhi = rs.getInt("checkNhanQua");
            player.isbienhinh = rs.getInt("isbienhinh");

            // Giới hạn Captutien
            if (player.Captutien > 5000) {
                player.Captutien = 5000;
            }
            if (player.Captutien > 10000) {
                player.Captutien = 10000;
            }
        } else if (loadType == LoadType.FULL_BY_ID) {
            player.Captutien = rs.getInt("Captutien");
            player.Exptutien = rs.getLong("Exptutien");
            player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");

            // Giới hạn Captutien
            if (player.Captutien > 5000) {
                player.Captutien = 5000;
            }
            if (player.Captutien > 10000) {
                player.Captutien = 10000;
            }
        }
        if (loadType != LoadType.SIEU_HANG_ONLY) {
            loadClanInfo(player, rs);
        }
    }

    /**
     * Load clan information
     */
    private static void loadClanInfo(Player player, GirlkunResultSet rs) throws Exception {
        int clanId = rs.getInt("clan_id_sv" + Manager.SERVER);
        if (clanId != -1) {
            Clan clan = ClanService.gI().getClanById(clanId);
            if (clan != null) {
                for (ClanMember cm : clan.getMembers()) {
                    if (cm.id == player.id) {
                        clan.addMemberOnline(player);
                        player.clan = clan;
                        player.clanMember = cm;
                        break;
                    }
                }
            }
        }
    }

    /**
     * Load inventory data (vàng, gem, ruby)
     */
    private static void loadInventoryData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("data_inventory"));

        player.inventory.gold = Long.parseLong(String.valueOf(dataArray.get(0)));
        player.inventory.gem = Integer.parseInt(String.valueOf(dataArray.get(1)));
        player.inventory.ruby = Integer.parseInt(String.valueOf(dataArray.get(2)));

        if (dataArray.size() >= 4) {
            player.inventory.coupon = Integer.parseInt(String.valueOf(dataArray.get(3)));
        } else {
            player.inventory.coupon = 0;
        }

        if (dataArray.size() >= 5) {
            player.inventory.event = Integer.parseInt(String.valueOf(dataArray.get(4)));
        } else {
            player.inventory.event = 0;
        }
    }

    /**
     * Load point data (power, hp, mp, dame, def)
     */
    private static LoadPointResult loadPointData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("data_point"));

        player.nPoint.limitPower = Byte.parseByte(String.valueOf(dataArray.get(0)));
        player.nPoint.power = Double.parseDouble(String.valueOf(dataArray.get(1)));
        player.nPoint.tiemNang = Double.parseDouble(String.valueOf(dataArray.get(2)));
        player.nPoint.stamina = Short.parseShort(String.valueOf(dataArray.get(3)));
        player.nPoint.maxStamina = Short.parseShort(String.valueOf(dataArray.get(4)));
        player.nPoint.hpg = Double.parseDouble(String.valueOf(dataArray.get(5)));
        player.nPoint.mpg = Double.parseDouble(String.valueOf(dataArray.get(6)));
        player.nPoint.dameg = Double.parseDouble(String.valueOf(dataArray.get(7)));
        player.nPoint.defg = Double.parseDouble(String.valueOf(dataArray.get(8)));
        player.nPoint.critg = Byte.parseByte(String.valueOf(dataArray.get(9)));

        double plHp = Double.parseDouble(String.valueOf(dataArray.get(11)));
        double plMp = Double.parseDouble(String.valueOf(dataArray.get(12)));

        return new LoadPointResult(plHp, plMp);
    }

    /**
     * Helper class để return multiple values
     */
    private static class LoadPointResult {

        final double hp;
        final double mp;

        LoadPointResult(double hp, double mp) {
            this.hp = hp;
            this.mp = mp;
        }
    }

    /**
     * Parse JSON Array với error handling
     */
    private static JSONArray parseJsonArray(String jsonString) {
        return (JSONArray) JSONValue.parse(jsonString);
    }

    /**
     * Load full data for login
     */
    private static void loadFullLoginData(Player player, GirlkunResultSet rs) throws Exception {
        // Load inventory
        loadInventoryData(player, rs);

        // Load điểm VIP
        loadDiemData(player, rs);

        // Load Bkt data
        loadBktData(player, rs);

        // Load DLDL data
        loadBktDLDLData(player, rs);

        // Load dhtime data
        loadDhTimeData(player, rs);

        // Load card data
        loadCardData(player, rs);

        // Load điểm danh data
        loadDiemDanhData(player, rs);

        // Load location data
        loadLocationData(player, rs);

        // Load points và nhận HP/MP
        LoadPointResult pointResult = loadPointData(player, rs);

        // Load magic tree
        loadMagicTreeData(player, rs);

        // Load black ball reward
        loadBlackBallData(player, rs);

        // Load items (body, bag, box)
        loadItemsData(player, rs);

        // Load friends & enemies
        loadFriendsEnemiesData(player, rs);

        // Load intrinsic
        loadIntrinsicData(player, rs);

        // Load item time
        loadItemTimeData(player, rs);

        // Load task data
        loadTaskData(player, rs);

        // Load off train data
        loadOffTrainData(player, rs);

        // Load mabu egg
        loadMabuEggData(player, rs);

        // Load bill egg
        loadBillEggData(player, rs);

        // Load charm data
        loadCharmData(player, rs);

        // Load Thu Trieu Hoi data
        loadThuTrieuHoiData(player, rs);

        // Load skills
        loadSkillsData(player, rs);

        // Load pet data
        loadPetData(player, rs);

        // Set HP/MP cuối cùng
        player.nPoint.hp = pointResult.hp;
        player.nPoint.mp = pointResult.mp;

        player.iDMark.setLoadedAllDataPlayer(true);
    }

    /**
     * Load full data by ID
     */
    private static void loadFullByIdData(Player player, GirlkunResultSet rs) throws Exception {
        // Load inventory
        loadInventoryData(player, rs);

        // Load points và nhận HP/MP
        LoadPointResult pointResult = loadPointData(player, rs);

        // Load location data
        loadLocationData(player, rs);

        // Load magic tree
        loadMagicTreeData(player, rs);

        // Load black ball reward
        loadBlackBallData(player, rs);

        // Load items (body, bag, box)
        loadItemsData(player, rs);

        // Load friends & enemies
        loadFriendsEnemiesData(player, rs);

        // Load intrinsic
        loadIntrinsicData(player, rs);

        // Load item time
        loadItemTimeData(player, rs);

        // Load task data
        loadTaskData(player, rs);

        // Load off train data
        loadOffTrainData(player, rs);

        // Load mabu egg
        loadMabuEggData(player, rs);

        // Load bill egg
        loadBillEggData(player, rs);

        // Load charm data
        loadCharmData(player, rs);

        // Load skills
        loadSkillsData(player, rs);

        // Load pet data
        loadPetData(player, rs);

        // Set HP/MP cuối cùng
        player.nPoint.hp = pointResult.hp;
        player.nPoint.mp = pointResult.mp;

        player.iDMark.setLoadedAllDataPlayer(true);
    }

    /**
     * Load minimal data for siêu hạng
     */
    private static void loadSieuHangOnlyData(Player player, GirlkunResultSet rs) throws Exception {
        // Load rank siêu hạng
        player.rankSieuHang = rs.getInt("rank_sieu_hang");
        if (player.rankSieuHang == 999999) {
            player.rankSieuHang = ServerManager.gI().getNumPlayer();
        }

        // Load points
        LoadPointResult pointResult = loadPointData(player, rs);

        // Load basic items for display
        loadBasicItemsData(player, rs);

        // Load basic skills
        loadBasicSkillsData(player, rs);

        // Set HP/MP với giá trị đơn giản
        player.nPoint.hp = (int) pointResult.hp;
        player.nPoint.mp = (int) pointResult.mp;

        player.iDMark.setLoadedAllDataPlayer(true);
    }

    // =========================== HELPER METHODS ===========================
    /**
     * Load điểm VIP data
     */
    private static void loadDiemData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("data_diem"));
        player.vip = Byte.parseByte(String.valueOf(dataArray.get(0)));
        player.timevip = Long.parseLong(String.valueOf(dataArray.get(1)));
        player.tutien = Long.parseLong(String.valueOf(dataArray.get(2)));
    }

    /**
     * Load Bkt tu tien data
     */
    private static void loadBktData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("Bkttutien"));
        player.TUTIEN[0] = Long.parseLong(String.valueOf(dataArray.get(0)));
        player.TUTIEN[1] = Long.parseLong(String.valueOf(dataArray.get(1)));
        if (player.TUTIEN[1] > 96) {
            player.TUTIEN[1] = 0;
        }
        player.TUTIEN[2] = Long.parseLong(String.valueOf(dataArray.get(2)));
        if (player.TUTIEN[2] > 50) {
            player.TUTIEN[2] = 0;
        }
    }

    /**
     * Load BktDauLaDaiLuc data
     */
    private static void loadBktDLDLData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("BktDLDL"));
        for (int i = 0; i < 21; i++) {
            player.DauLaDaiLuc[i] = Long.parseLong(String.valueOf(dataArray.get(i)));
        }
    }

    /**
     * Load dhtime data
     */
    private static void loadDhTimeData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("dhtime"));
        player.isTitleUse = Integer.parseInt(String.valueOf(dataArray.get(0))) == 1;
        player.lastTimeTitle1 = Long.parseLong(String.valueOf(dataArray.get(1)));
    }

    /**
     * Load card data
     */
    private static void loadCardData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("data_card"));
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject obj = (JSONObject) dataArray.get(i);
            player.Cards.add(new Card(
                    Short.parseShort(obj.get("id").toString()),
                    Byte.parseByte(obj.get("amount").toString()),
                    Byte.parseByte(obj.get("max").toString()),
                    Byte.parseByte(obj.get("level").toString()),
                    GodGK.loadOptionCard((JSONArray) JSONValue.parse(obj.get("option").toString())),
                    Byte.parseByte(obj.get("used").toString())));
        }
    }

    /**
     * Load điểm danh data
     */
    private static void loadDiemDanhData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("diemdanh"));
        player.CheckDayOnl = Byte.parseByte(String.valueOf(dataArray.get(0)));
        player.diemdanh = Byte.parseByte(String.valueOf(dataArray.get(1)));

        // Logic check ngày
        try {
            java.util.Calendar ngayvps = java.util.Calendar.getInstance();
            int ngayhomnay = ngayvps.get(java.util.Calendar.DAY_OF_MONTH);
            if (player.CheckDayOnl == 0) {
                player.CheckDayOnl = (byte) (ngayhomnay - 1);
            }
            if (ngayhomnay > player.CheckDayOnl) {
                player.CheckDayOnl = (byte) ngayhomnay;
                player.diemdanh = 0;
            }
        } catch (Exception e) {
            // Ignore calendar errors
        }

        // Load điểm danh skill
        dataArray = parseJsonArray(rs.getString("diemdanhsk"));
        player.CheckDayOnl = Byte.parseByte(String.valueOf(dataArray.get(0)));
        player.diemdanhsk = Byte.parseByte(String.valueOf(dataArray.get(1)));

        try {
            java.util.Calendar ngayvps = java.util.Calendar.getInstance();
            int ngayhomnay = ngayvps.get(java.util.Calendar.DAY_OF_MONTH);
            if (player.CheckDayOnl == 0) {
                player.CheckDayOnl = (byte) (ngayhomnay - 1);
            }
            if (ngayhomnay > player.CheckDayOnl) {
                player.CheckDayOnl = (byte) ngayhomnay;
                player.diemdanhsk = 0;
            }
        } catch (Exception e) {
            // Ignore calendar errors
        }
    }

    /**
     * Load location data
     */
    private static void loadLocationData(Player player, GirlkunResultSet rs) throws Exception {
        try {
            JSONArray dataArray = parseJsonArray(rs.getString("data_location"));
            int mapId = Integer.parseInt(String.valueOf(dataArray.get(0)));
            player.location.x = Integer.parseInt(String.valueOf(dataArray.get(1)));
            player.location.y = Integer.parseInt(String.valueOf(dataArray.get(2)));
            player.location.lastTimeplayerMove = System.currentTimeMillis();

            if (MapService.gI().isMapDoanhTrai(mapId) || MapService.gI().isMapBlackBallWar(mapId)
                    || MapService.gI().isdiacung(mapId) || MapService.gI().isMapBanDoKhoBau(mapId)
                    || MapService.gI().isMapKhiGas(mapId) || MapService.gI().isMapMaBu(mapId)) {
                mapId = 2;
                player.location.x = 528;
                player.location.y = 360;
            }
            player.zone = MapService.gI().getMapCanJoin(player, mapId, -1);
        } catch (Exception e) {
            // Use default location if error
        }
    }

    /**
     * Load magic tree data
     */
    private static void loadMagicTreeData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("data_magic_tree"));
        byte level = Byte.parseByte(String.valueOf(dataArray.get(0)));
        byte currPea = Byte.parseByte(String.valueOf(dataArray.get(1)));
        boolean isUpgrade = Byte.parseByte(String.valueOf(dataArray.get(2))) == 1;
        long lastTimeHarvest = Long.parseLong(String.valueOf(dataArray.get(3)));
        long lastTimeUpgrade = Long.parseLong(String.valueOf(dataArray.get(4)));
        player.magicTree = new MagicTree(player, level, currPea, lastTimeHarvest, isUpgrade, lastTimeUpgrade);
    }

    /**
     * Load black ball reward data
     */
    private static void loadBlackBallData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("data_black_ball"));
        for (int i = 0; i < dataArray.size(); i++) {
            JSONArray dataBlackBall = parseJsonArray(String.valueOf(dataArray.get(i)));
            player.rewardBlackBall.timeOutOfDateReward[i] = Long.parseLong(String.valueOf(dataBlackBall.get(0)));
            player.rewardBlackBall.lastTimeGetReward[i] = Long.parseLong(String.valueOf(dataBlackBall.get(1)));
            try {
                player.rewardBlackBall.quantilyBlackBall[i] = dataBlackBall.get(2) != null
                        ? Integer.parseInt(String.valueOf(dataBlackBall.get(2)))
                        : 0;
            } catch (Exception e) {
                player.rewardBlackBall.quantilyBlackBall[i] = player.rewardBlackBall.timeOutOfDateReward[i] != 0 ? 1
                        : 0;
            }
        }
    }

    /**
     * Load items data (body, bag, box)
     */
    private static void loadItemsData(Player player, GirlkunResultSet rs) throws Exception {
        // Load body items
        loadItemsFromString(player.inventory.itemsBody, rs.getString("items_body"));
        while (player.inventory.itemsBody.size() <= 12) {
            player.inventory.itemsBody.add(ItemService.gI().createItemNull());
        }

        // Load bag items
        loadItemsFromString(player.inventory.itemsBag, rs.getString("items_bag"));
        while (player.inventory.itemsBag.size() <= 126) {
            player.inventory.itemsBag.add(ItemService.gI().createItemNull());
        }

        // Load box items
        loadItemsFromString(player.inventory.itemsBox, rs.getString("items_box"));

        // Load box lucky round items
        JSONArray dataArray = parseJsonArray(rs.getString("items_box_lucky_round"));
        for (int i = 0; i < dataArray.size(); i++) {
            Item item = createItemFromJsonArray(parseJsonArray(dataArray.get(i).toString()));
            if (item != null && item.isNotNullItem()) {
                player.inventory.itemsBoxCrackBall.add(item);
            }
        }
    }

    /**
     * Helper method to load items from JSON string
     */
    private static void loadItemsFromString(List<Item> itemList, String jsonString) throws Exception {
        JSONArray dataArray = parseJsonArray(jsonString);
        for (int i = 0; i < dataArray.size(); i++) {
            Item item = createItemFromJsonArray(parseJsonArray(dataArray.get(i).toString()));
            if (item != null) {
                Dragon.models.Event.ResetParamItem.SetBasicChiSo(item);
                itemList.add(item);
            }
        }
    }

    /**
     * Create item from JSON array
     */
    private static Item createItemFromJsonArray(JSONArray dataItem) throws Exception {
        short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
        if (tempId == -1) {
            return ItemService.gI().createItemNull();
        }

        Item item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
        JSONArray options = parseJsonArray(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
        boolean flag = false;

        for (int j = 0; j < options.size(); j++) {
            JSONArray opt = parseJsonArray(String.valueOf(options.get(j)));
            if (tempId != 884) {
                item.itemOptions.add(new Item.ItemOption(
                        Integer.parseInt(String.valueOf(opt.get(0))),
                        Integer.parseInt(String.valueOf(opt.get(1)))));
            } else {
                item.itemOptions.add(new Item.ItemOption(
                        Integer.parseInt(String.valueOf(opt.get(0))) == 14 ? 5
                        : Integer.parseInt(String.valueOf(opt.get(0))),
                        Integer.parseInt(String.valueOf(opt.get(1)))));
            }
            if (Integer.parseInt(String.valueOf(opt.get(0))) == 50 && tempId == 884) {
                flag = true;
            }
        }

        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
        if (ItemService.gI().isOutOfDateTime(item)) {
            return ItemService.gI().createItemNull();
        }

        if (tempId == 884 && flag) {
            List<Item.ItemOption> itemsToRemove = new ArrayList<>();
            for (Item.ItemOption op : item.itemOptions) {
                if (op.optionTemplate.id == 50 || op.optionTemplate.id == 102) {
                    itemsToRemove.add(op);
                }
            }
            item.itemOptions.removeAll(itemsToRemove);
        }

        return item;
    }

    /**
     * Load friends and enemies data
     */
    private static void loadFriendsEnemiesData(Player player, GirlkunResultSet rs) throws Exception {
        // Load friends
        JSONArray dataArray = parseJsonArray(rs.getString("friends"));
        if (dataArray != null) {
            for (int i = 0; i < dataArray.size(); i++) {
                JSONArray dataFE = parseJsonArray(String.valueOf(dataArray.get(i)));
                Friend friend = new Friend();
                friend.id = Integer.parseInt(String.valueOf(dataFE.get(0)));
                friend.name = String.valueOf(dataFE.get(1));
                friend.head = Short.parseShort(String.valueOf(dataFE.get(2)));
                friend.body = Short.parseShort(String.valueOf(dataFE.get(3)));
                friend.leg = Short.parseShort(String.valueOf(dataFE.get(4)));
                friend.bag = Byte.parseByte(String.valueOf(dataFE.get(5)));
                friend.power = Double.parseDouble(String.valueOf(dataFE.get(6)));
                player.friends.add(friend);
            }
        }

        // Load enemies
        dataArray = parseJsonArray(rs.getString("enemies"));
        if (dataArray != null) {
            for (int i = 0; i < dataArray.size(); i++) {
                JSONArray dataFE = parseJsonArray(String.valueOf(dataArray.get(i)));
                Enemy enemy = new Enemy();
                enemy.id = Integer.parseInt(String.valueOf(dataFE.get(0)));
                enemy.name = String.valueOf(dataFE.get(1));
                enemy.head = Short.parseShort(String.valueOf(dataFE.get(2)));
                enemy.body = Short.parseShort(String.valueOf(dataFE.get(3)));
                enemy.leg = Short.parseShort(String.valueOf(dataFE.get(4)));
                enemy.bag = Byte.parseByte(String.valueOf(dataFE.get(5)));
                enemy.power = Double.parseDouble(String.valueOf(dataFE.get(6)));
                player.enemies.add(enemy);
            }
        }

        // Set rank siêu hạng
        player.rankSieuHang = rs.getInt("rank_sieu_hang");
        if (player.rankSieuHang == 999999) {
            player.rankSieuHang = ServerManager.gI().getNumPlayer();
        }
    }

    /**
     * Load intrinsic data
     */
    private static void loadIntrinsicData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("data_intrinsic"));
        byte intrinsicId = Byte.parseByte(String.valueOf(dataArray.get(0)));
        player.playerIntrinsic.intrinsic = IntrinsicService.gI().getIntrinsicById(intrinsicId);
        player.playerIntrinsic.intrinsic.param1 = Short.parseShort(String.valueOf(dataArray.get(1)));
        player.playerIntrinsic.intrinsic.param2 = Short.parseShort(String.valueOf(dataArray.get(2)));
        player.playerIntrinsic.countOpen = Byte.parseByte(String.valueOf(dataArray.get(3)));
        if (player.playerIntrinsic.intrinsic != null) {
            player.playerIntrinsic.intrinsic.SetMaxValue();
        }
    }

    /**
     * Load item time data
     */
    private static void loadItemTimeData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("data_item_time"));
        int timeBoHuyet = Integer.parseInt(String.valueOf(dataArray.get(0)));
        int timeBoKhi = Integer.parseInt(String.valueOf(dataArray.get(1)));
        int timeGiapXen = Integer.parseInt(String.valueOf(dataArray.get(2)));
        int timebkt = Integer.parseInt(String.valueOf(dataArray.get(2)));
        int timeCuongNo = Integer.parseInt(String.valueOf(dataArray.get(3)));
        int timeAnDanh = Integer.parseInt(String.valueOf(dataArray.get(4)));
        int timeOpenPower = Integer.parseInt(String.valueOf(dataArray.get(5)));
        int timeMayDo = Integer.parseInt(String.valueOf(dataArray.get(6)));
        int timeMeal = Integer.parseInt(String.valueOf(dataArray.get(7)));
        int iconMeal = Integer.parseInt(String.valueOf(dataArray.get(8)));

        int timeUseTDLT = 0;
        if (dataArray.size() == 10) {
            timeUseTDLT = Integer.parseInt(String.valueOf(dataArray.get(9)));
        }

        // Set item time values
        player.itemTime.lastTimeBoHuyet = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoHuyet);
        player.itemTime.lastTimeBoKhi = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoKhi);
        player.itemTime.lastTimeGiapXen = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeGiapXen);
        player.itemTime.lastTimeCuongNo = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeCuongNo);
        player.itemTime.lastTimeAnDanh = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeAnDanh);
        player.itemTime.lastTimeOpenPower = System.currentTimeMillis() - (ItemTime.TIME_OPEN_POWER - timeOpenPower);
        player.itemTime.lastTimeUseMayDo = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - timeMayDo);
        player.itemTime.lastTimeEatMeal = System.currentTimeMillis() - (ItemTime.TIME_EAT_MEAL - timeMeal);
        player.itemTime.timeTDLT = timeUseTDLT * 60 * 1000;
        player.itemTime.lastTimeUseTDLT = System.currentTimeMillis();

        player.itemTime.iconMeal = iconMeal;
        player.itemTime.isUseBoHuyet = timeBoHuyet != 0;
        player.itemTime.isUseBoKhi = timeBoKhi != 0;
        player.itemTime.isUseGiapXen = timeGiapXen != 0;
        player.itemTime.isbkt = timebkt != 0;
        player.itemTime.isUseCuongNo = timeCuongNo != 0;
        player.itemTime.isUseAnDanh = timeAnDanh != 0;
        player.itemTime.isOpenPower = timeOpenPower != 0;
        player.itemTime.isUseMayDo = timeMayDo != 0;
        player.itemTime.isEatMeal = timeMeal != 0;
        player.itemTime.isUseTDLT = timeUseTDLT != 0;

        // Load additional item time data
        loadAdditionalItemTimeData(player, rs);
    }

    /**
     * Load additional item time data (siêu cấp, bình cần, etc.)
     */
    private static void loadAdditionalItemTimeData(Player player, GirlkunResultSet rs) throws Exception {
        // Data siêu cấp
        JSONArray dataArray = parseJsonArray(rs.getString("data_item_time_sieu_cap"));
        int timeBoHuyetSC = Integer.parseInt(String.valueOf(dataArray.get(0)));
        int timeBoKhiSC = Integer.parseInt(String.valueOf(dataArray.get(1)));
        int timeGiapXenSC = Integer.parseInt(String.valueOf(dataArray.get(2)));
        int timeCuongNoSC = Integer.parseInt(String.valueOf(dataArray.get(3)));
        int timeAnDanhSC = Integer.parseInt(String.valueOf(dataArray.get(4)));

        player.itemTime.lastTimeBoHuyet2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoHuyetSC);
        player.itemTime.lastTimeBoKhi2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoKhiSC);
        player.itemTime.lastTimeGiapXen2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeGiapXenSC);
        player.itemTime.lastTimeCuongNo2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeCuongNoSC);
        player.itemTime.lastTimeAnDanh2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeAnDanhSC);

        player.itemTime.isUseBoHuyet2 = timeBoHuyetSC != 0;
        player.itemTime.isUseBoKhi2 = timeBoKhiSC != 0;
        player.itemTime.isUseGiapXen2 = timeGiapXenSC != 0;
        player.itemTime.isUseCuongNo2 = timeCuongNoSC != 0;
        player.itemTime.isUseAnDanh2 = timeAnDanhSC != 0;

        // Bình cần data
        dataArray = parseJsonArray(rs.getString("Binh_can_data"));
        int timex2 = Integer.parseInt(String.valueOf(dataArray.get(0)));
        int timex3 = Integer.parseInt(String.valueOf(dataArray.get(1)));
        int timex5 = Integer.parseInt(String.valueOf(dataArray.get(2)));
        int timex7 = Integer.parseInt(String.valueOf(dataArray.get(3)));

        player.itemTime.lastX2EXP = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - timex2);
        if (player.gender == 0) {
            player.itemTime.lastX3EXP = System.currentTimeMillis() - (ItemTime.BA_MUOI_PHUT - timex3);
        } else if (player.gender == 2) {
            player.itemTime.lastX3EXP = System.currentTimeMillis() - (ItemTime.BON_MUOI_PHUT - timex3);
        } else if (player.gender == 1) {
            player.itemTime.lastX3EXP = System.currentTimeMillis() - (ItemTime.NAM_MUOI_PHUT - timex3);
        }
        player.itemTime.lastX5EXP = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - timex5);
        player.itemTime.lastX7EXP = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - timex7);
        player.itemTime.lastbkt = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - timex7);
        player.itemTime.isX2EXP = timex2 != 0;
        player.itemTime.isX3EXP = timex3 != 0;
        player.itemTime.isX5EXP = timex5 != 0;
        player.itemTime.isX7EXP = timex7 != 0;

        // Load more item time data...
        loadNuocMiaData(player, rs);
        loadTrungThuData(player, rs);
        loadCaiTrangData(player, rs);
    }

    /**
     * Load nước mía data
     */
    private static void loadNuocMiaData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("Nuoc_mia"));
        int nuocmiakhonglo = Integer.parseInt(String.valueOf(dataArray.get(0)));
        int nuocmiathom = Integer.parseInt(String.valueOf(dataArray.get(1)));
        int nuocmiasaurieng = Integer.parseInt(String.valueOf(dataArray.get(2)));

        player.itemTime.lastnuocmiakhonglo = System.currentTimeMillis() - (ItemTime.TIME_NUOC_MIA - nuocmiakhonglo);
        player.itemTime.lastnuocmiathom = System.currentTimeMillis() - (ItemTime.TIME_NUOC_MIA - nuocmiathom);
        player.itemTime.lastnuocmiasaurieng = System.currentTimeMillis() - (ItemTime.TIME_NUOC_MIA - nuocmiasaurieng);

        player.itemTime.isnuocmiakhonglo = nuocmiakhonglo != 0;
        player.itemTime.isnuocmiathom = nuocmiathom != 0;
        player.itemTime.isnuocmiasaurieng = nuocmiasaurieng != 0;
    }

    /**
     * Load trung thu data
     */
    private static void loadTrungThuData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("Trung_thu"));
        int motTrung = Integer.parseInt(String.valueOf(dataArray.get(0)));
        int haiTrung = Integer.parseInt(String.valueOf(dataArray.get(1)));
        int gaQuay = Integer.parseInt(String.valueOf(dataArray.get(2)));
        int thapCam = Integer.parseInt(String.valueOf(dataArray.get(3)));
        int anhTrang = Integer.parseInt(String.valueOf(dataArray.get(4)));

        player.itemTime.last1Trung = System.currentTimeMillis() - (ItemTime.TIME_TRUNG_THU_10P - motTrung);
        player.itemTime.last2Trung = System.currentTimeMillis() - (ItemTime.TIME_TRUNG_THU_10P - haiTrung);
        player.itemTime.lastgaQuay = System.currentTimeMillis() - (ItemTime.TIME_TRUNG_THU_10P - gaQuay);
        player.itemTime.lastthapCam = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - thapCam);
        player.itemTime.lastAnhTrang = System.currentTimeMillis() - (ItemTime.TIME_ITEM - anhTrang);

        player.itemTime.is1Trung = motTrung != 0;
        player.itemTime.is2Trung = haiTrung != 0;
        player.itemTime.isgaQuay = gaQuay != 0;
        player.itemTime.isthapCam = thapCam != 0;
        player.itemTime.isAnhTrang = anhTrang != 0;
    }

    /**
     * Load cải trang data
     */
    private static void loadCaiTrangData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("data_cai_trang_send"));
        int timeduoikhi = Integer.parseInt(String.valueOf(dataArray.get(0)));
        int iconduoikhi = Integer.parseInt(String.valueOf(dataArray.get(1)));
        player.itemTime.lastTimedkhi = System.currentTimeMillis() - (ItemTime.TIME_DUOI_KHI - timeduoikhi);
        player.itemTime.isdkhi = timeduoikhi != 0;
        player.itemTime.icondkhi = iconduoikhi;
    }

    /**
     * Load task data
     */
    private static void loadTaskData(Player player, GirlkunResultSet rs) throws Exception {
        // Main task
        JSONArray dataArray = parseJsonArray(rs.getString("data_task"));
        TaskMain taskMain = TaskService.gI().getTaskMainById(player, Byte.parseByte(String.valueOf(dataArray.get(0))));
        taskMain.index = Byte.parseByte(String.valueOf(dataArray.get(1)));
        taskMain.subTasks.get(taskMain.index).count = Short.parseShort(String.valueOf(dataArray.get(2)));
        player.playerTask.taskMain = taskMain;

        // Side task
        dataArray = parseJsonArray(rs.getString("data_side_task"));
        String format = "dd-MM-yyyy";
        long receivedTime = Long.parseLong(String.valueOf(dataArray.get(1)));
        Date date = new Date(receivedTime);
        if (TimeUtil.formatTime(date, format).equals(TimeUtil.formatTime(new Date(), format))) {
            player.playerTask.sideTask.template = TaskService.gI()
                    .getSideTaskTemplateById(Integer.parseInt(String.valueOf(dataArray.get(0))));
            player.playerTask.sideTask.count = Integer.parseInt(String.valueOf(dataArray.get(2)));
            player.playerTask.sideTask.maxCount = Integer.parseInt(String.valueOf(dataArray.get(3)));
            player.playerTask.sideTask.leftTask = Integer.parseInt(String.valueOf(dataArray.get(4)));
            player.playerTask.sideTask.level = Integer.parseInt(String.valueOf(dataArray.get(5)));
            player.playerTask.sideTask.receivedTime = receivedTime;
        }
    }

    /**
     * Load off train data
     */
    private static void loadOffTrainData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("data_offtrain"));
        player.typetrain = Byte.parseByte(String.valueOf(dataArray.get(0)));
        player.istrain = Byte.parseByte(String.valueOf(dataArray.get(1))) == 1;
    }

    /**
     * Load mabu egg data
     */
    private static void loadMabuEggData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("data_mabu_egg"));
        if (dataArray.size() != 0) {
            player.mabuEgg = new MabuEgg(player,
                    Long.parseLong(String.valueOf(dataArray.get(0))),
                    Long.parseLong(String.valueOf(dataArray.get(1))));
        }
    }

    /**
     * Load bill egg data
     */
    private static void loadBillEggData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("bill_data"));
        if (dataArray.size() != 0) {
            player.billEgg = new BillEgg(player,
                    Long.parseLong(String.valueOf(dataArray.get(0))),
                    Long.parseLong(String.valueOf(dataArray.get(1))));
        }
    }

    /**
     * Load charm data
     */
    private static void loadCharmData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("data_charm"));
        player.charms.tdTriTue = Long.parseLong(String.valueOf(dataArray.get(0)));
        player.charms.tdManhMe = Long.parseLong(String.valueOf(dataArray.get(1)));
        player.charms.tdDaTrau = Long.parseLong(String.valueOf(dataArray.get(2)));
        player.charms.tdOaiHung = Long.parseLong(String.valueOf(dataArray.get(3)));
        player.charms.tdBatTu = Long.parseLong(String.valueOf(dataArray.get(4)));
        player.charms.tdDeoDai = Long.parseLong(String.valueOf(dataArray.get(5)));
        player.charms.tdThuHut = Long.parseLong(String.valueOf(dataArray.get(6)));
        player.charms.tdDeTu = Long.parseLong(String.valueOf(dataArray.get(7)));
        player.charms.tdTriTue3 = Long.parseLong(String.valueOf(dataArray.get(8)));
        player.charms.tdTriTue4 = Long.parseLong(String.valueOf(dataArray.get(9)));
    }

    /**
     * Load Thu Trieu Hoi data
     */
    private static void loadThuTrieuHoiData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("Thu_TrieuHoi"));
        player.CapBacThan = Integer.parseInt(String.valueOf(dataArray.get(0)));
        if (player.CapBacThan >= 0 && player.CapBacThan <= 10) {
            player.TenThan = String.valueOf(dataArray.get(1));
            player.ThucAnThan = Integer.parseInt(String.valueOf(dataArray.get(2)));
            player.DameThan = Long.parseLong(String.valueOf(dataArray.get(3)));
            player.ThanLastTimeThucan = Long.parseLong(String.valueOf(dataArray.get(4)));
            player.ThanLevel = Integer.parseInt(String.valueOf(dataArray.get(5)));
            if (player.ThanLevel > 100) {
                player.ThanLevel = 100;
            }
            player.ExpThan = Long.parseLong(String.valueOf(dataArray.get(6)));
            player.MauThan = Long.parseLong(String.valueOf(dataArray.get(7)));
        } else {
            player.CapBacThan = -1;
        }
    }

    /**
     * Load skills data
     */
    private static void loadSkillsData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("skills"));
        for (int i = 0; i < dataArray.size(); i++) {
            JSONArray dataSkill = parseJsonArray(String.valueOf(dataArray.get(i)));
            int tempId = Integer.parseInt(String.valueOf(dataSkill.get(0)));
            byte point = Byte.parseByte(String.valueOf(dataSkill.get(1)));
            Skill skill = null;
            if (point != 0) {
                skill = SkillUtil.createSkill(tempId, point);
            } else {
                skill = SkillUtil.createSkillLevel0(tempId);
            }
            if (player.ResetSkill != 0) {
                skill.lastTimeUseThisSkill = Long.parseLong(String.valueOf(dataSkill.get(2)));
            } else {
                skill.lastTimeUseThisSkill = 0L;
            }
            if (dataSkill.size() > 3) {
                skill.currLevel = Short.parseShort(String.valueOf(dataSkill.get(3)));
            }
            player.playerSkill.skills.add(skill);
        }
        player.ResetSkill = 1;

        // Load skill shortcut
        dataArray = parseJsonArray(rs.getString("skills_shortcut"));
        for (int i = 0; i < dataArray.size() && i < 10; i++) {
            player.playerSkill.skillShortCut[i] = Byte.parseByte(String.valueOf(dataArray.get(i)));
        }
        for (int i = dataArray.size(); i < 10; i++) {
            player.playerSkill.skillShortCut[i] = -1;
        }
        for (int i : player.playerSkill.skillShortCut) {
            if (player.playerSkill.getSkillbyId(i) != null && player.playerSkill.getSkillbyId(i).damage > 0) {
                player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(i);
                break;
            }
        }
        if (player.playerSkill.skillSelect == null) {
            player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(player.gender == ConstPlayer.TRAI_DAT
                    ? Skill.DRAGON
                    : (player.gender == ConstPlayer.NAMEC ? Skill.DEMON : Skill.GALICK));
        }
    }

    /**
     * Load pet data
     */
    private static void loadPetData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray petData = parseJsonArray(rs.getString("pet"));
        if (!petData.isEmpty()) {
            JSONArray dataArray = parseJsonArray(String.valueOf(petData.get(0)));
            Pet pet = new Pet(player);
            pet.id = -player.id;
            pet.typePet = Byte.parseByte(String.valueOf(dataArray.get(0)));
            pet.gender = Byte.parseByte(String.valueOf(dataArray.get(1)));
            pet.name = String.valueOf(dataArray.get(2));
            player.fusion.typeFusion = Byte.parseByte(String.valueOf(dataArray.get(3)));
            player.fusion.lastTimeFusion = System.currentTimeMillis()
                    - (Fusion.TIME_FUSION - Integer.parseInt(String.valueOf(dataArray.get(4))));
            pet.status = Byte.parseByte(String.valueOf(dataArray.get(5)));

            // Load pet stats
            dataArray = parseJsonArray(String.valueOf(petData.get(1)));
            pet.nPoint.limitPower = Byte.parseByte(String.valueOf(dataArray.get(0)));
            pet.nPoint.power = Double.parseDouble(String.valueOf(dataArray.get(1)));
            pet.nPoint.tiemNang = Double.parseDouble(String.valueOf(dataArray.get(2)));
            pet.nPoint.stamina = Short.parseShort(String.valueOf(dataArray.get(3)));
            pet.nPoint.maxStamina = Short.parseShort(String.valueOf(dataArray.get(4)));
            pet.nPoint.hpg = Double.parseDouble(String.valueOf(dataArray.get(5)));
            pet.nPoint.mpg = Double.parseDouble(String.valueOf(dataArray.get(6)));
            pet.nPoint.dameg = Double.parseDouble(String.valueOf(dataArray.get(7)));
            pet.nPoint.defg = Double.parseDouble(String.valueOf(dataArray.get(8)));
            pet.nPoint.critg = Integer.parseInt(String.valueOf(dataArray.get(9)));
            double hp = Double.parseDouble(String.valueOf(dataArray.get(10)));
            double mp = Double.parseDouble(String.valueOf(dataArray.get(11)));

            // Load pet items
            dataArray = parseJsonArray(String.valueOf(petData.get(2)));
            for (int i = 0; i < dataArray.size(); i++) {
                Item item = createItemFromJsonArray(parseJsonArray(String.valueOf(dataArray.get(i))));
                if (item != null) {
                    Dragon.models.Event.ResetParamItem.SetBasicChiSo(item);
                    pet.inventory.itemsBody.add(item);
                }
            }

            // Load pet skills
            dataArray = parseJsonArray(String.valueOf(petData.get(3)));
            for (int i = 0; i < dataArray.size(); i++) {
                JSONArray skillTemp = parseJsonArray(String.valueOf(dataArray.get(i)));
                int tempId = Integer.parseInt(String.valueOf(skillTemp.get(0)));
                byte point = Byte.parseByte(String.valueOf(skillTemp.get(1)));
                Skill skill = null;
                if (point != 0) {
                    skill = SkillUtil.createSkill(tempId, point);
                } else {
                    skill = SkillUtil.createSkillLevel0(tempId);
                }
                switch (skill.template.id) {
                    case Skill.KAMEJOKO:
                    case Skill.MASENKO:
                    case Skill.ANTOMIC:
                        skill.coolDown = 1000;
                        break;
                }
                pet.playerSkill.skills.add(skill);
            }
            if (pet.playerSkill.skills.size() < 5) {
                pet.playerSkill.skills.add(4, SkillUtil.createSkillLevel0(-1));
            }
            pet.nPoint.hp = hp;
            pet.nPoint.mp = mp;
            player.pet = pet;
        }

        GodGK.SetPlayer(player);
        GodGK.SetPlayer(player.pet);
    }

    /**
     * Load basic items for siêu hạng only
     */
    private static void loadBasicItemsData(Player player, GirlkunResultSet rs) throws Exception {
        // Load only body items for display
        loadItemsFromString(player.inventory.itemsBody, rs.getString("items_body"));
    }

    /**
     * Load basic skills for siêu hạng only
     */
    private static void loadBasicSkillsData(Player player, GirlkunResultSet rs) throws Exception {
        JSONArray dataArray = parseJsonArray(rs.getString("skills"));
        for (int i = 0; i < dataArray.size(); i++) {
            JSONArray dataSkill = parseJsonArray(String.valueOf(dataArray.get(i)));
            int tempId = Integer.parseInt(String.valueOf(dataSkill.get(0)));
            byte point = Byte.parseByte(String.valueOf(dataSkill.get(1)));
            Skill skill = null;
            if (point != 0) {
                skill = SkillUtil.createSkill(tempId, point);
            } else {
                skill = SkillUtil.createSkillLevel0(tempId);
            }
            skill.lastTimeUseThisSkill = Long.parseLong(String.valueOf(dataSkill.get(2)));
            player.playerSkill.skills.add(skill);
        }

        // Load skill shortcut
        dataArray = parseJsonArray(rs.getString("skills_shortcut"));
        for (int i = 0; i < dataArray.size() && i < 10; i++) {
            player.playerSkill.skillShortCut[i] = Byte.parseByte(String.valueOf(dataArray.get(i)));
        }
        // Fill remaining slots with -1 if player has less than 10 skill shortcuts
        for (int i = dataArray.size(); i < 10; i++) {
            player.playerSkill.skillShortCut[i] = -1;
        }
        for (int i : player.playerSkill.skillShortCut) {
            if (player.playerSkill.getSkillbyId(i) != null && player.playerSkill.getSkillbyId(i).damage > 0) {
                player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(i);
                break;
            }
        }
        if (player.playerSkill.skillSelect == null) {
            player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(player.gender == ConstPlayer.TRAI_DAT
                    ? Skill.DRAGON
                    : (player.gender == ConstPlayer.NAMEC ? Skill.DEMON : Skill.GALICK));
        }
    }
}
