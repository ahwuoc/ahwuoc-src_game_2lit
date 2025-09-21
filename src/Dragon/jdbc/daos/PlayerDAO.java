package Dragon.jdbc.daos;

import Dragon.card.Card;
import com.girlkun.database.GirlkunDB;
import Dragon.models.ThanhTich.CheckDataDay;
//import Dragon.models.ThanhTich.ThanhTich;
import Dragon.models.item.Item;
import Dragon.models.item.ItemTime;
import Dragon.models.player.Friend;
import Dragon.models.player.Fusion;
import Dragon.models.player.Inventory;
import Dragon.models.player.Player;
import Dragon.models.skill.Skill;
import Dragon.server.Manager;
import Dragon.services.InventoryServiceNew;
import Dragon.services.ItemTimeService;
import Dragon.services.MapService;
import Dragon.services.Service;
import Dragon.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class PlayerDAO {

    public static boolean updateTimeLogout;

    public static boolean createNewPlayer(int userId, String name, byte gender, int hair) {
        try {
            JSONArray dataArray = new JSONArray();
            // phước Tạo Nhân Vật
            dataArray.add(10000); // vàng
            dataArray.add(10000000); // ngọc xanh
            dataArray.add(0); // hồng ngọc
            dataArray.add(0); // point
            dataArray.add(0); // event

            String inventory = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(1); // map
            dataArray.add(651); // x
            dataArray.add(312); // y
            String location = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(1); // giới hạn sức mạnh
            dataArray.add(100); // sức mạnh
            dataArray.add(0); // tiềm năng
            dataArray.add(1000); // thể lực
            dataArray.add(1000); // thể lực đầy
            dataArray.add(gender == 0 ? 200 : 100); // hp gốc
            dataArray.add(gender == 1 ? 200 : 100); // ki gốc
            dataArray.add(gender == 2 ? 20 : 15); // sức đánh gốc
            dataArray.add(0); // giáp gốc
            dataArray.add(0); // chí mạng gốc
            dataArray.add(0); // năng động
            dataArray.add(gender == 0 ? 200 : 100); // hp hiện tại
            dataArray.add(gender == 1 ? 200 : 100); // ki hiện tại
            String point = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(4); // level
            dataArray.add(5); // curent pea
            dataArray.add(0); // is upgrade
            dataArray.add(new Date().getTime()); // last time harvest
            dataArray.add(new Date().getTime()); // last time upgrade
            String magicTree = dataArray.toJSONString();
            dataArray.clear();
            /**
             *
             * [
             * {"temp_id":"1","option":[[5,7],[7,3]],"create_time":"49238749283748957""},
             * {"temp_id":"1","option":[[5,7],[7,3]],"create_time":"49238749283748957""},
             * {"temp_id":"-1","option":[],"create_time":"0""}, ... ]
             */

            // int idAo = gender == 0 ? 0 : gender == 1 ? 1 : 2;
            // int idQuan = gender == 0 ? 6 : gender == 1 ? 7 : 8;
            // int def = gender == 2 ? 3 : 2;
            // int hp = gender == 0 ? 30 : 20;
            JSONArray item = new JSONArray();
            JSONArray options = new JSONArray();
            JSONArray opt = new JSONArray();
            // for (int i = 0; i < 13; i++) {
            // if (i == 0) { //áo
            // opt.add(47); //id option
            // opt.add(def); //param option
            // item.add(idAo); //id item
            // item.add(1); //số lượng
            // options.add(opt.toJSONString());
            // opt.clear();
            // } else if (i == 1) { //quần
            // opt.add(6); //id option
            // opt.add(hp); //param option
            // item.add(idQuan); //id item
            // item.add(1); //số lượng
            // options.add(opt.toJSONString());
            // opt.clear();
            // } else {
            // item.add(-1); //id item
            // item.add(0); //số lượng
            // }
            // item.add(options.toJSONString()); //full option item
            // item.add(System.currentTimeMillis()); //thời gian item được tạo
            // dataArray.add(item.toJSONString());
            // options.clear();
            // item.clear();
            // }
            String itemsBody = dataArray.toJSONString();
            dataArray.clear();

            for (int i = 0; i < 50; i++) { // item tạo player
                switch (i) {
                    case 0:
                        opt.add(30); // id option khác
                        opt.add(0); // param option
                        options.add(opt.toJSONString());
                        opt.clear();
                        item.add(1761); // id item
                        item.add(1); // số lượng
                        break;

                    case 1:
                        // thỏi vàng
                        opt.add(95); // id option cấm giao dịch
                        opt.add(2); // param option
                        options.add(opt.toJSONString());
                        opt.clear(); // Xóa opt để chuẩn bị cho option tiếp theo
                        opt.add(96); // id option khác
                        opt.add(2); // param option
                        options.add(opt.toJSONString());
                        opt.clear();
                        opt.add(47); // id option khác
                        opt.add(100); // param option
                        options.add(opt.toJSONString());
                        opt.clear();
                        opt.add(30); // id option khác
                        opt.add(0); // param option
                        options.add(opt.toJSONString());
                        opt.clear();
                        item.add(1740); // id item
                        item.add(1); // số lượng
                        break;

                    default:
                        item.add(-1); // id item
                        item.add(0); // số lượng
                        break;
                }

                item.add(options.toJSONString()); // full option item
                item.add(System.currentTimeMillis()); // thời gian item được tạo
                dataArray.add(item.toJSONString());
                options.clear(); // Xóa options để chuẩn bị cho vòng lặp tiếp theo
                item.clear(); // Xóa item để chuẩn bị cho vòng lặp tiếp theo
            }

            String itemsBag = dataArray.toJSONString();
            dataArray.clear();

            for (int i = 0; i < 20; i++) {
                if (i == 0) { // rada
                    opt.add(30); // id option
                    opt.add(1); // param option
                    item.add(460); // id item
                    item.add(1); // số lượng
                    options.add(opt.toJSONString());
                    opt.clear();
                } else {
                    item.add(-1); // id item
                    item.add(0); // số lượng
                }
                item.add(options.toJSONString()); // full option item
                item.add(System.currentTimeMillis()); // thời gian item được tạo
                dataArray.add(item.toJSONString());
                options.clear();
                item.clear();
            }
            String itemsBox = dataArray.toJSONString();
            dataArray.clear();

            for (int i = 0; i < 110; i++) {
                item.add(-1); // id item
                item.add(0); // số lượng
                item.add(options.toJSONString()); // full option item
                item.add(System.currentTimeMillis()); // thời gian item được tạo
                dataArray.add(item.toJSONString());
                options.clear();
                item.clear();
            }
            String itemsBoxLuckyRound = dataArray.toJSONString();
            dataArray.clear();

            String friends = dataArray.toJSONString();
            String enemies = dataArray.toJSONString();

            dataArray.add(0); // id nội tại
            dataArray.add(0); // chỉ số 1
            dataArray.add(0); // chỉ số 2
            dataArray.add(0); // số lần mở
            String intrinsic = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(0); // bổ huyết
            dataArray.add(0); // bổ khí
            dataArray.add(0); // giáp xên
            dataArray.add(0); // cuồng nộ
            dataArray.add(0); // ẩn danh
            dataArray.add(0); // bổ huyết
            dataArray.add(0); // bổ khí
            dataArray.add(0); // giáp xên
            dataArray.add(0); // cuồng nộ
            dataArray.add(0); // ẩn danh
            dataArray.add(0); // mở giới hạn sức mạnh
            dataArray.add(0); // máy dò
            dataArray.add(0); // máy dò2
            dataArray.add(0); // thức ăn cold
            dataArray.add(0); // icon thức ăn cold
            String itemTime = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(0); // máy dò
            dataArray.add(0); // máy dò2
            dataArray.add(0); // thức ăn cold
            dataArray.add(0); // icon thức ăn cold
            dataArray.add(0); // icon thức ăn cold
            String data_item_time_sieu_cap = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(0); // Oocs
            dataArray.add(0); // Cá
            String dkhi = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(14); // id nhiệm vụ
            dataArray.add(0); // index nhiệm vụ con
            dataArray.add(0); // số lượng đã làm
            String task = dataArray.toJSONString();
            dataArray.clear();

            String mabuEgg = dataArray.toJSONString();
            String billEgg = dataArray.toJSONString();

            dataArray.add(System.currentTimeMillis()); // bùa trí tuệ
            dataArray.add(System.currentTimeMillis()); // bùa mạnh mẽ
            dataArray.add(System.currentTimeMillis()); // bùa da trâu
            dataArray.add(System.currentTimeMillis()); // bùa oai hùng
            dataArray.add(System.currentTimeMillis()); // bùa bất tử
            dataArray.add(System.currentTimeMillis()); // bùa dẻo dai
            dataArray.add(System.currentTimeMillis()); // bùa thu hút
            dataArray.add(System.currentTimeMillis()); // bùa đệ tử
            dataArray.add(System.currentTimeMillis()); // bùa trí tuệ x3
            dataArray.add(System.currentTimeMillis()); // bùa trí tuệ x4
            String charms = dataArray.toJSONString();
            dataArray.clear();

            int[] skillsArr = gender == 0 ? new int[]{0, 1, 6, 9, 10, 20, 22, 19, 24, 27, 28, 29}
                    : gender == 1 ? new int[]{2, 3, 7, 11, 12, 17, 18, 19, 26, 27, 28, 29}
                    : new int[]{4, 5, 8, 13, 14, 21, 23, 19, 25, 27, 28, 29};
            // [{"temp_id":"4","point":0,"last_time_use":0},]

            JSONArray skill = new JSONArray();
            for (int i = 0; i < skillsArr.length; i++) {
                skill.add(skillsArr[i]); // id skill
                if (i == 0 || i == 11) {
                    skill.add(1); // level skill
                } else {
                    skill.add(0); // level skill
                }
                skill.add(0); // thời gian sử dụng trước đó
                skill.add(0);
                dataArray.add(skill.toString());
                skill.clear();
            }
            String skills = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(gender == 0 ? 0 : gender == 1 ? 2 : 4);
            dataArray.add(-1);
            dataArray.add(-1);
            dataArray.add(-1);
            dataArray.add(-1);
            dataArray.add(-1);
            dataArray.add(-1);
            dataArray.add(-1);
            dataArray.add(-1);
            String skillsShortcut = dataArray.toJSONString();
            dataArray.clear();

            String petData = dataArray.toJSONString();

            JSONArray blackBall = new JSONArray();
            for (int i = 1; i <= 7; i++) {
                blackBall.add(0);
                blackBall.add(0);
                blackBall.add(0);
                dataArray.add(blackBall.toJSONString());
                blackBall.clear();
            }
            String dataBlackBall = dataArray.toString();
            dataArray.clear();

            dataArray.add(0); // id nội tại
            dataArray.add(0); // chỉ số 1
            String dataoff = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(-1); // id side task
            dataArray.add(0); // thời gian nhận
            dataArray.add(0); // số lượng đã làm
            dataArray.add(0); // số lượng cần làm
            dataArray.add(2); // nhiệm vụ mỗi ngày sidetask
            dataArray.add(0); // mức độ nhiệm vụ
            String dataSideTask = dataArray.toJSONString();
            dataArray.clear();

            String data_card = dataArray.toJSONString();
            String bill_data = dataArray.toJSONString();

            String trieuhoithu = "[-1]";

            GirlkunDB.executeUpdate("insert into player"
                    + "(account_id, name, head, gender, have_tennis_space_ship, clan_id_sv" + Manager.SERVER + ", "
                    + "data_inventory, data_location, data_point, data_magic_tree, items_body, "
                    + "items_bag, items_box, items_box_lucky_round, friends, enemies, data_intrinsic, data_item_time,"
                    + "data_task, data_mabu_egg, data_charm, skills, skills_shortcut, pet,"
                    + "data_black_ball, data_side_task, data_card, bill_data,data_item_time_sieu_cap,PointBoss,dataArchiverment,ResetSkill,PointCauCa,LastDoanhTrai,RuongItemC2,CuongNoC2,BoHuyetC2,BoKhiC2,DaBaoVe,DaNguSac,dothanlinh, data_offtrain,Thu_TrieuHoi,data_cai_trang_send) "
                    + "values ()", userId, name, hair, gender, 0, -1, inventory, location, point, magicTree,
                    itemsBody, itemsBag, itemsBox, itemsBoxLuckyRound, friends, enemies, intrinsic,
                    itemTime, task, mabuEgg, charms, skills, skillsShortcut, petData, dataBlackBall, dataSideTask,
                    data_card, bill_data, data_item_time_sieu_cap, 0, "[\"[-1,0]\"]", 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    dataoff, trieuhoithu, dkhi);
            Logger.success("Tạo Nhân Vật Thành công!\n");
            return true;
        } catch (Exception e) {
            System.err.print("\nError at 46\n");
            e.printStackTrace();
            return false;
        }
    }

    public static void updatePlayer(Player player) {
        if (player.isBot) {
            return;
        }
        if (player.iDMark.isLoadedAllDataPlayer()) {
            long st = System.currentTimeMillis();
            try {
                JSONArray dataArray = new JSONArray();
                int tv = 0;
                int cnc2 = 0;
                int bhc2 = 0;
                int bkc2 = 0;
                int dbv = 0;
                int dns = 0;
                int dothanlinh = 0;
                int ruongitemc2 = 0;
                // data kim lượng
                dataArray.add(player.inventory.gold > Inventory.LIMIT_GOLD
                        ? Inventory.LIMIT_GOLD
                        : player.inventory.gold);
                dataArray.add(player.inventory.gem);
                dataArray.add(player.inventory.ruby);
                dataArray.add(player.inventory.coupon);
                dataArray.add(player.inventory.event);
                String inventory = dataArray.toJSONString();
                dataArray.clear();

                int mapId = -1;
                mapId = player.mapIdBeforeLogout;
                int x = player.location.x;
                int y = player.location.y;
                long hp = (long) player.nPoint.hp;
                long mp = (long) player.nPoint.mp;
                long dame = (long) player.nPoint.dame;
                if (player.isDie()) {
                    // data tọa độ
                    mapId = 2;
                    x = 528;
                    y = 360;
                    hp = 1;
                    mp = 1;
                    dame = 1;
                } else {
                    if (MapService.gI().isMapDoanhTrai(mapId) || MapService.gI().isMapBlackBallWar(mapId)
                            || MapService.gI().isdiacung(mapId)
                            || MapService.gI().isMapBanDoKhoBau(mapId)
                            || MapService.gI().isMapKhiGas(mapId)
                            || MapService.gI().isMapMaBu(mapId)) {
                        mapId = 2;
                        x = 528;
                        y = 360;
                    }
                }

                // data vị trí
                dataArray.add(mapId);
                dataArray.add(x);
                dataArray.add(y);
                String location = dataArray.toJSONString();
                dataArray.clear();

                // data vị trí
                dataArray.add(player.typetrain);
                dataArray.add(player.istrain ? 1 : 0);
                String dataoff = dataArray.toJSONString();
                dataArray.clear();

                // data chỉ số
                dataArray.add(player.nPoint.limitPower);
                dataArray.add(player.nPoint.power);
                dataArray.add(player.nPoint.tiemNang);
                dataArray.add(player.nPoint.stamina);
                dataArray.add(player.nPoint.maxStamina);
                dataArray.add(player.nPoint.hpg);
                dataArray.add(player.nPoint.mpg);
                dataArray.add(player.nPoint.dameg);
                dataArray.add(player.nPoint.defg);
                dataArray.add(player.nPoint.critg);
                dataArray.add(0);
                dataArray.add(hp);
                dataArray.add(mp);
                dataArray.add(dame);
                String point = dataArray.toJSONString();
                dataArray.add(player.numKillSieuHang);
                dataArray.add(player.rankSieuHang);
                dataArray.clear();

                // data đậu thần
                dataArray.add(player.magicTree.level);
                dataArray.add(player.magicTree.currPeas);
                dataArray.add(player.magicTree.isUpgrade ? 1 : 0);
                dataArray.add(player.magicTree.lastTimeHarvest);
                dataArray.add(player.magicTree.lastTimeUpgrade);
                String magicTree = dataArray.toJSONString();
                dataArray.clear();

                // data body
                JSONArray dataItem = new JSONArray();
                for (Item item : player.inventory.itemsBody) {
                    JSONArray opt = new JSONArray();
                    if (item.isNotNullItem()) {
                        dataItem.add(item.template.id);
                        dataItem.add(item.quantity);
                        JSONArray options = new JSONArray();
                        for (Item.ItemOption io : item.itemOptions) {
                            opt.add(io.optionTemplate.id);
                            opt.add(io.param);
                            options.add(opt.toJSONString());
                            opt.clear();
                        }
                        dataItem.add(options.toJSONString());
                    } else {
                        dataItem.add(-1);
                        dataItem.add(0);
                        dataItem.add(opt.toJSONString());
                    }
                    dataItem.add(item.createTime);
                    dataArray.add(dataItem.toJSONString());
                    dataItem.clear();
                }
                String itemsBody = dataArray.toJSONString();
                dataArray.clear();

                // data bag
                for (Item item : player.inventory.itemsBag) {
                    JSONArray opt = new JSONArray();
                    if (item.isNotNullItem()) {
                        dataItem.add(item.template.id);
                        dataItem.add(item.quantity);
                        if (item.template.id == 457) {
                            tv += item.quantity;
                        }
                        if (item.template.id == 1099) {
                            cnc2 += item.quantity;
                        }
                        if (item.template.id == 1100) {
                            bhc2 += item.quantity;
                        }
                        if (item.template.id == 1101) {
                            bkc2 += item.quantity;
                        }
                        if (item.template.id == 987) {
                            dbv += item.quantity;
                        }
                        if (item.template.id == 674) {
                            dns += item.quantity;
                        }
                        if (item.isDTL()) {
                            dothanlinh += item.quantity;
                        }
                        if (item.template.id == 1278) {
                            ruongitemc2 += item.quantity;
                        }
                        JSONArray options = new JSONArray();
                        for (Item.ItemOption io : item.itemOptions) {
                            opt.add(io.optionTemplate.id);
                            opt.add(io.param);
                            options.add(opt.toJSONString());
                            opt.clear();
                        }
                        dataItem.add(options.toJSONString());
                    } else {
                        dataItem.add(-1);
                        dataItem.add(0);
                        dataItem.add(opt.toJSONString());
                    }
                    dataItem.add(item.createTime);
                    dataArray.add(dataItem.toJSONString());
                    dataItem.clear();
                }
                String itemsBag = dataArray.toJSONString();
                dataArray.clear();

                // data card
                // data box
                for (Item item : player.inventory.itemsBox) {
                    JSONArray opt = new JSONArray();
                    if (item.isNotNullItem()) {
                        dataItem.add(item.template.id);
                        dataItem.add(item.quantity);
                        if (item.template.id == 457) {
                            tv += item.quantity;
                        }
                        if (item.template.id == 1099) {
                            cnc2 += item.quantity;
                        }
                        if (item.template.id == 1100) {
                            bhc2 += item.quantity;
                        }
                        if (item.template.id == 1101) {
                            bkc2 += item.quantity;
                        }
                        if (item.template.id == 987) {
                            dbv += item.quantity;
                        }
                        if (item.isDTL()) {
                            dothanlinh += item.quantity;
                        }
                        if (item.template.id == 674) {
                            dns += item.quantity;
                        }
                        if (item.template.id == 1278) {
                            ruongitemc2 += item.quantity;
                        }
                        JSONArray options = new JSONArray();
                        for (Item.ItemOption io : item.itemOptions) {
                            opt.add(io.optionTemplate.id);
                            opt.add(io.param);
                            options.add(opt.toJSONString());
                            opt.clear();
                        }
                        dataItem.add(options.toJSONString());
                    } else {
                        dataItem.add(-1);
                        dataItem.add(0);
                        dataItem.add(opt.toJSONString());
                    }
                    dataItem.add(item.createTime);
                    dataArray.add(dataItem.toJSONString());
                    dataItem.clear();
                }
                String itemsBox = dataArray.toJSONString();
                dataArray.clear();

                // data box crack ball
                for (Item item : player.inventory.itemsBoxCrackBall) {
                    JSONArray opt = new JSONArray();
                    if (item.isNotNullItem()) {
                        dataItem.add(item.template.id);
                        dataItem.add(item.quantity);
                        JSONArray options = new JSONArray();
                        for (Item.ItemOption io : item.itemOptions) {
                            opt.add(io.optionTemplate.id);
                            opt.add(io.param);
                            options.add(opt.toJSONString());
                            opt.clear();
                        }
                        dataItem.add(options.toJSONString());
                    } else {
                        dataItem.add(-1);
                        dataItem.add(0);
                        dataItem.add(opt.toJSONString());
                    }
                    dataItem.add(item.createTime);
                    dataArray.add(dataItem.toJSONString());
                    dataItem.clear();
                }
                String itemsBoxLuckyRound = dataArray.toJSONString();
                dataArray.clear();

                // data bạn bè
                JSONArray dataFE = new JSONArray();
                for (Friend f : player.friends) {
                    dataFE.add(f.id);
                    dataFE.add(f.name);
                    dataFE.add(f.head);
                    dataFE.add(f.body);
                    dataFE.add(f.leg);
                    dataFE.add(f.bag);
                    dataFE.add(f.power);
                    dataArray.add(dataFE.toJSONString());
                    dataFE.clear();
                }
                String friend = dataArray.toJSONString();
                dataArray.clear();

                // data kẻ thù
                for (Friend e : player.enemies) {
                    dataFE.add(e.id);
                    dataFE.add(e.name);
                    dataFE.add(e.head);
                    dataFE.add(e.body);
                    dataFE.add(e.leg);
                    dataFE.add(e.bag);
                    dataFE.add(e.power);
                    dataArray.add(dataFE.toJSONString());
                    dataFE.clear();
                }
                String enemy = dataArray.toJSONString();
                dataArray.clear();

                // data nội tại
                dataArray.add(player.playerIntrinsic.intrinsic.id);
                dataArray.add(player.playerIntrinsic.intrinsic.param1);
                dataArray.add(player.playerIntrinsic.intrinsic.param2);
                dataArray.add(player.playerIntrinsic.countOpen);
                String intrinsic = dataArray.toJSONString();
                dataArray.clear();

                // data item time
                dataArray.add((player.itemTime.isUseBoHuyet
                        ? (ItemTime.TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeBoHuyet))
                        : 0));
                dataArray.add((player.itemTime.isUseBoKhi
                        ? (ItemTime.TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeBoKhi))
                        : 0));
                dataArray.add((player.itemTime.isUseGiapXen
                        ? (ItemTime.TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeGiapXen))
                        : 0));
                dataArray.add((player.itemTime.isUseCuongNo
                        ? (ItemTime.TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeCuongNo))
                        : 0));
                dataArray.add((player.itemTime.isUseAnDanh
                        ? (ItemTime.TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeAnDanh))
                        : 0));
                dataArray.add((player.itemTime.isOpenPower
                        ? (ItemTime.TIME_OPEN_POWER - (System.currentTimeMillis() - player.itemTime.lastTimeOpenPower))
                        : 0));
                dataArray.add((player.itemTime.isUseMayDo
                        ? (ItemTime.TIME_MAY_DO - (System.currentTimeMillis() - player.itemTime.lastTimeUseMayDo))
                        : 0));
                dataArray.add((player.itemTime.isUseMayDo2
                        ? (ItemTime.TIME_MAY_DO - (System.currentTimeMillis() - player.itemTime.lastTimeUseMayDo2))
                        : 0));
                dataArray.add((player.itemTime.isEatMeal
                        ? (ItemTime.TIME_EAT_MEAL - (System.currentTimeMillis() - player.itemTime.lastTimeEatMeal))
                        : 0));
                dataArray.add(player.itemTime.iconMeal);
                dataArray.add((player.itemTime.isUseTDLT
                        ? ((player.itemTime.timeTDLT - (System.currentTimeMillis() - player.itemTime.lastTimeUseTDLT))
                        / 60 / 1000)
                        : 0));
                String itemTime = dataArray.toJSONString();
                dataArray.clear();

                dataArray.add(player.vip);
                dataArray.add(player.timevip);
                dataArray.add(player.tutien);
                String data_diem = dataArray.toJSONString();
                dataArray.clear();

                dataArray.add(player.TUTIEN[0]);
                dataArray.add(player.TUTIEN[1]);
                dataArray.add(player.TUTIEN[2]);
                String Bkttutien = dataArray.toJSONString();
                dataArray.clear();
                if (player.Ma_cot > 0) {
                    dataArray.add(player.Bkt_Tu_Ma);
                    dataArray.add(player.Exp_Tu_Ma);
                    dataArray.add(player.Ma_Hoa);
                    dataArray.add(player.BktLasttimeMaHoa);
                    dataArray.add(player.Ma_cot);
                }
                String BktTuMa = dataArray.toJSONString();
                dataArray.clear();

                dataArray.add(player.DauLaDaiLuc[0]);
                dataArray.add(player.DauLaDaiLuc[1]);
                dataArray.add(player.DauLaDaiLuc[2]);
                dataArray.add(player.DauLaDaiLuc[3]);
                dataArray.add(player.DauLaDaiLuc[4]);
                dataArray.add(player.DauLaDaiLuc[5]);
                dataArray.add(player.DauLaDaiLuc[6]);
                dataArray.add(player.DauLaDaiLuc[7]);
                dataArray.add(player.DauLaDaiLuc[8]);
                dataArray.add(player.DauLaDaiLuc[9]);
                dataArray.add(player.DauLaDaiLuc[10]);
                dataArray.add(player.DauLaDaiLuc[11]);
                dataArray.add(player.DauLaDaiLuc[12]);
                dataArray.add(player.DauLaDaiLuc[13]);
                dataArray.add(player.DauLaDaiLuc[14]);
                dataArray.add(player.DauLaDaiLuc[15]);
                dataArray.add(player.DauLaDaiLuc[16]);
                dataArray.add(player.DauLaDaiLuc[17]);
                dataArray.add(player.DauLaDaiLuc[18]);
                dataArray.add(player.DauLaDaiLuc[19]);
                dataArray.add(player.DauLaDaiLuc[20]);
                String BktDLDL = dataArray.toJSONString();
                dataArray.clear();

                // data nhiệm vụ
                dataArray.add(player.playerTask.taskMain.id);
                dataArray.add(player.playerTask.taskMain.index);
                dataArray.add(player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count);
                String task = dataArray.toJSONString();
                dataArray.clear();

                // data nhiệm vụ hàng ngày
                dataArray
                        .add(player.playerTask.sideTask.template != null ? player.playerTask.sideTask.template.id : -1);
                dataArray.add(player.playerTask.sideTask.receivedTime);
                dataArray.add(player.playerTask.sideTask.count);
                dataArray.add(player.playerTask.sideTask.maxCount);
                dataArray.add(player.playerTask.sideTask.leftTask);
                dataArray.add(player.playerTask.sideTask.level);
                String sideTask = dataArray.toJSONString();
                dataArray.clear();
                String dataThanhTich = dataArray.toJSONString();
                dataArray.clear();
                // data trứng bư
                if (player.mabuEgg != null) {
                    dataArray.add(player.mabuEgg.lastTimeCreate);
                    dataArray.add(player.mabuEgg.timeDone);
                }
                String mabuEgg = dataArray.toJSONString();
                dataArray.clear();

                dataArray.add(player.isTitleUse == true ? 1 : 0);
                dataArray.add(player.lastTimeTitle1);
                String dhtime = dataArray.toJSONString();
                dataArray.clear();

                // data trứng bill
                if (player.billEgg != null) {
                    dataArray.add(player.billEgg.lastTimeCreate);
                    dataArray.add(player.billEgg.timeDone);
                }
                String billEgg = dataArray.toJSONString();
                dataArray.clear();

                // data bùa
                dataArray.add(player.charms.tdTriTue);
                dataArray.add(player.charms.tdManhMe);
                dataArray.add(player.charms.tdDaTrau);
                dataArray.add(player.charms.tdOaiHung);
                dataArray.add(player.charms.tdBatTu);
                dataArray.add(player.charms.tdDeoDai);
                dataArray.add(player.charms.tdThuHut);
                dataArray.add(player.charms.tdDeTu);
                dataArray.add(player.charms.tdTriTue3);
                dataArray.add(player.charms.tdTriTue4);
                String charm = dataArray.toJSONString();
                dataArray.clear();

                // data skill
                JSONArray dataSkill = new JSONArray();
                for (Skill skill : player.playerSkill.skills) {
                    dataSkill.add(skill.template.id);
                    dataSkill.add(skill.point);
                    dataSkill.add(skill.lastTimeUseThisSkill);
                    dataSkill.add(skill.currLevel);
                    dataArray.add(dataSkill.toJSONString());
                    dataSkill.clear();
                }
                String skills = dataArray.toJSONString();
                dataArray.clear();
                dataArray.clear();

                // data skill shortcut
                for (int skillId : player.playerSkill.skillShortCut) {
                    dataArray.add(skillId);
                }
                String skillShortcut = dataArray.toJSONString();
                dataArray.clear();

                String pet = dataArray.toJSONString();
                String petInfo = dataArray.toJSONString();
                String petPoint = dataArray.toJSONString();
                String petBody = dataArray.toJSONString();
                String petSkill = dataArray.toJSONString();

                // data pet
                if (player.pet != null) {
                    dataArray.add(player.pet.typePet);
                    dataArray.add(player.pet.gender);
                    dataArray.add(player.pet.name);
                    dataArray.add(player.fusion.typeFusion);
                    int timeLeftFusion = (int) (Fusion.TIME_FUSION
                            - (System.currentTimeMillis() - player.fusion.lastTimeFusion));
                    dataArray.add(timeLeftFusion < 0 ? 0 : timeLeftFusion);
                    dataArray.add(player.pet.status);

                    petInfo = dataArray.toJSONString();
                    dataArray.clear();

                    dataArray.add(player.pet.nPoint.limitPower);
                    dataArray.add(player.pet.nPoint.power);
                    dataArray.add(player.pet.nPoint.tiemNang);
                    dataArray.add(player.pet.nPoint.stamina);
                    dataArray.add(player.pet.nPoint.maxStamina);
                    dataArray.add(player.pet.nPoint.hpg);
                    dataArray.add(player.pet.nPoint.mpg);
                    dataArray.add(player.pet.nPoint.dameg);
                    dataArray.add(player.pet.nPoint.defg);
                    dataArray.add(player.pet.nPoint.critg);
                    dataArray.add(player.pet.nPoint.hp);
                    dataArray.add(player.pet.nPoint.mp);
                    dataArray.add(player.pet.nPoint.dame);
                    petPoint = dataArray.toJSONString();
                    dataArray.clear();

                    JSONArray items = new JSONArray();
                    JSONArray options = new JSONArray();
                    JSONArray opt = new JSONArray();
                    for (Item item : player.pet.inventory.itemsBody) {
                        if (item.isNotNullItem()) {
                            dataItem.add(item.template.id);
                            dataItem.add(item.quantity);
                            for (Item.ItemOption io : item.itemOptions) {
                                opt.add(io.optionTemplate.id);
                                opt.add(io.param);
                                options.add(opt.toJSONString());
                                opt.clear();
                            }
                            dataItem.add(options.toJSONString());
                        } else {
                            dataItem.add(-1);
                            dataItem.add(0);
                            dataItem.add(options.toJSONString());
                        }

                        dataItem.add(item.createTime);
                        items.add(dataItem.toJSONString());
                        dataItem.clear();
                        options.clear();
                    }
                    petBody = items.toJSONString();

                    JSONArray petSkills = new JSONArray();
                    for (Skill s : player.pet.playerSkill.skills) {
                        JSONArray pskill = new JSONArray();
                        if (s.skillId != -1) {
                            pskill.add(s.template.id);
                            pskill.add(s.point);
                        } else {
                            pskill.add(-1);
                            pskill.add(0);
                        }
                        petSkills.add(pskill.toJSONString());
                    }
                    petSkill = petSkills.toJSONString();

                    dataArray.add(petInfo);
                    dataArray.add(petPoint);
                    dataArray.add(petBody);
                    dataArray.add(petSkill);
                    pet = dataArray.toJSONString();
                }
                dataArray.clear();

                // data thưởng ngọc rồng đen
                for (int i = 0; i < player.rewardBlackBall.timeOutOfDateReward.length; i++) {
                    JSONArray dataBlackBall = new JSONArray();
                    dataBlackBall.add(player.rewardBlackBall.timeOutOfDateReward[i]);
                    dataBlackBall.add(player.rewardBlackBall.lastTimeGetReward[i]);
                    dataBlackBall.add(player.rewardBlackBall.quantilyBlackBall[i]);
                    dataArray.add(dataBlackBall.toJSONString());
                    dataBlackBall.clear();
                }
                String dataBlackBall = dataArray.toJSONString();
                dataArray.clear();

                // data item time siêu cấp
                dataArray.add((player.itemTime.isUseBoHuyet2
                        ? (ItemTime.TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeBoHuyet2))
                        : 0));
                dataArray.add((player.itemTime.isUseBoKhi2
                        ? (ItemTime.TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeBoKhi2))
                        : 0));
                dataArray.add((player.itemTime.isUseGiapXen2
                        ? (ItemTime.TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeGiapXen2))
                        : 0));
                dataArray.add((player.itemTime.isUseCuongNo2
                        ? (ItemTime.TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeCuongNo2))
                        : 0));
                dataArray.add((player.itemTime.isUseAnDanh2
                        ? (ItemTime.TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeAnDanh2))
                        : 0));

                String itemTimeSC = dataArray.toJSONString();
                dataArray.clear();

                // data duoi khi
                dataArray.add((player.itemTime.isdkhi
                        ? (ItemTime.TIME_DUOI_KHI - (System.currentTimeMillis() - player.itemTime.lastTimedkhi))
                        : 0));
                dataArray.add(player.itemTime.icondkhi);
                String timeduoikhi = dataArray.toJSONString();
                dataArray.clear();

                dataArray.add((player.itemTime.isX2EXP
                        ? (ItemTime.TIME_MAY_DO - (System.currentTimeMillis() - player.itemTime.lastX2EXP))
                        : 0));
                if (player.gender == 0) {
                    dataArray.add((player.itemTime.isX3EXP
                            ? (ItemTime.BA_MUOI_PHUT - (System.currentTimeMillis() - player.itemTime.lastX3EXP))
                            : 0));
                } else if (player.gender == 2) {
                    dataArray.add((player.itemTime.isX3EXP
                            ? (ItemTime.BON_MUOI_PHUT - (System.currentTimeMillis() - player.itemTime.lastX3EXP))
                            : 0));
                } else if (player.gender == 1) {
                    dataArray.add((player.itemTime.isX3EXP
                            ? (ItemTime.NAM_MUOI_PHUT - (System.currentTimeMillis() - player.itemTime.lastX3EXP))
                            : 0));
                }
                dataArray.add((player.itemTime.isX5EXP
                        ? (ItemTime.TIME_MAY_DO - (System.currentTimeMillis() - player.itemTime.lastX5EXP))
                        : 0));
                dataArray.add((player.itemTime.isX7EXP
                        ? (ItemTime.TIME_MAY_DO - (System.currentTimeMillis() - player.itemTime.lastX7EXP))
                        : 0));
                dataArray.add((player.itemTime.isbkt
                        ? (ItemTime.TIME_MAY_DO - (System.currentTimeMillis() - player.itemTime.lastbkt))
                        : 0));

                String Binh_can = dataArray.toJSONString();
                dataArray.clear();

                dataArray.add((player.itemTime.isnuocmiakhonglo
                        ? (ItemTime.TIME_NUOC_MIA - (System.currentTimeMillis() - player.itemTime.lastnuocmiakhonglo))
                        : 0));
                dataArray.add((player.itemTime.isnuocmiathom
                        ? (ItemTime.TIME_NUOC_MIA - (System.currentTimeMillis() - player.itemTime.lastnuocmiathom))
                        : 0));
                dataArray.add((player.itemTime.isnuocmiasaurieng
                        ? (ItemTime.TIME_NUOC_MIA - (System.currentTimeMillis() - player.itemTime.lastnuocmiasaurieng))
                        : 0));
                String Nuoc_mia = dataArray.toJSONString();
                dataArray.clear();

                dataArray.add((player.itemTime.is1Trung
                        ? (ItemTime.TIME_TRUNG_THU_10P - (System.currentTimeMillis() - player.itemTime.last1Trung))
                        : 0));
                dataArray.add((player.itemTime.is2Trung
                        ? (ItemTime.TIME_TRUNG_THU_10P - (System.currentTimeMillis() - player.itemTime.last2Trung))
                        : 0));
                dataArray.add((player.itemTime.isgaQuay
                        ? (ItemTime.TIME_TRUNG_THU_10P - (System.currentTimeMillis() - player.itemTime.lastgaQuay))
                        : 0));
                dataArray.add((player.itemTime.isthapCam
                        ? (ItemTime.TIME_MAY_DO - (System.currentTimeMillis() - player.itemTime.lastthapCam))
                        : 0));
                dataArray.add((player.itemTime.isAnhTrang
                        ? (ItemTime.TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastAnhTrang))
                        : 0));
                String Trung_thu = dataArray.toJSONString();
                dataArray.clear();

                dataArray.add(player.CheckDayOnl);
                dataArray.add(player.diemdanh);
                String diemdanh = dataArray.toJSONString();
                dataArray.clear();

                dataArray.add(player.CheckDayOnl);
                dataArray.add(player.diemdanhsk);
                String diemdanhsk = dataArray.toJSONString();
                dataArray.clear();

                if (player.CapBacThan >= 0 && player.CapBacThan <= 10) {
                    dataArray.add(player.CapBacThan);
                    dataArray.add(player.TenThan);
                    dataArray.add(player.ThucAnThan);
                    dataArray.add(player.DameThan);
                    dataArray.add(player.ThanLastTimeThucan);
                    dataArray.add(player.ThanLevel);
                    dataArray.add(player.ExpThan);
                    dataArray.add(player.MauThan);
                } else {
                    dataArray.add(-1);
                }
                String Thu_TrieuHoi = dataArray.toJSONString();
                dataArray.clear();

                String trieuhoithu = "[-1]";

                String query = " update player set data_item_time_sieu_cap = ?, head = ?, have_tennis_space_ship = ?,"
                        + "clan_id_sv" + Manager.SERVER
                        + " = ?, data_inventory = ?, data_location = ?, data_point = ?, data_magic_tree = ?,"
                        + "items_body = ?, items_bag = ?, items_box = ?, items_box_lucky_round = ?, friends = ?,"
                        + "enemies = ?, data_intrinsic = ?, data_item_time = ?, data_task = ?, data_mabu_egg = ?, pet = ?,"
                        + "data_black_ball = ?, data_side_task = ?, data_charm = ?, skills = ?,"
                        + "skills_shortcut = ?, pointPvp=?, NguHanhSonPoint=?,data_card=?,"
                        + "bill_data =?,thoi_vang = ?,dataArchiverment = ? , PointBoss = ?,ResetSkill = ?,"
                        + "LastDoanhTrai = ?,DataDay = ? ,"
                        + "RuongItemC2 = ? , CuongNoC2 = ? , BoHuyetC2 = ? , BoKhiC2 = ? ,DaBaoVe = ? ,"
                        + " DaNguSac = ?,dothanlinh = ? ,Binh_can_data = ? ,point_gapthu = ? ,pointSb = ?, data_offtrain =?,dhtime = ?, diemdanh=?,point_vnd = ?,thankhi = ?,blackballdata = ?, data_diem =?,Bkttutien=?,Captutien=?, Exptutien= ?,Bkt_Tu_Ma=?, BktDLDL= ?,ChuyenSinh = ?,Thu_TrieuHoi= ?,capboss= ?,data_cai_trang_send =?,checkNhanQua = ? ,diemdanhsk = ?,Nuoc_mia = ?, kemtraicay = ?, nuocmia = ?  "
                        + ", isbienhinh =?,Trung_thu=? where id = ?";
                GirlkunDB.executeUpdate(query,
                        itemTimeSC,
                        player.head,
                        player.haveTennisSpaceShip,
                        (player.clan != null ? player.clan.id : -1),
                        inventory,
                        location,
                        point,
                        magicTree,
                        itemsBody,
                        itemsBag,
                        itemsBox,
                        itemsBoxLuckyRound,
                        friend,
                        enemy,
                        intrinsic,
                        itemTime,
                        task,
                        mabuEgg,
                        pet,
                        dataBlackBall,
                        sideTask,
                        charm,
                        skills,
                        skillShortcut,
                        player.pointPvp,
                        player.NguHanhSonPoint,
                        JSONValue.toJSONString(player.Cards),
                        billEgg,
                        tv, dataThanhTich, player.PointBoss, player.ResetSkill, player.LastDoanhTrai,
                        CheckDataDay.SaveDataDay(player),
                        ruongitemc2, cnc2, bhc2, bkc2, dbv, dns, dothanlinh,
                        Binh_can,
                        player.point_gapthu,
                        player.pointSb,
                        dataoff,
                        dhtime,
                        diemdanh,
                        player.point_vnd,
                        player.thankhi,
                        player.blackballdata,
                        data_diem,
                        Bkttutien,
                        player.Captutien,
                        player.Exptutien,
                        BktTuMa,
                        BktDLDL,
                        player.ChuyenSinh,
                        Thu_TrieuHoi,
                        player.capboss,
                        timeduoikhi,
                        player.luotNhanBuaMienPhi,
                        diemdanhsk,
                        Nuoc_mia,
                        player.kemtraicay,
                        player.nuocmia,
                        player.isbienhinh,
                        Trung_thu,
                        player.id);
                // phước save
                Logger.log(Logger.RED, player.name + " Save! " + (System.currentTimeMillis() - st) + "\n");
            } catch (Exception e) {
                System.err.print("\nError at 47\n");
                e.printStackTrace();
            }
        }
    }

    public static boolean addvnd(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set vnd = (vnd + ?), active = ? where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().actived ? 1 : 0);
            ps.setInt(3, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().vnd += num;
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "Lỗi update VND " + player.name);
            return false;
        } finally {
        }
        if (num > 1000) {
            insertHistoryGold(player, num);
        }
        return true;
    }

    // public static boolean subvnd(Player player, int num) {
    // PreparedStatement ps = null;
    // try (Connection con = GirlkunDB.getConnection();) {
    // ps = con.prepareStatement("update account set vnd = (vnd - ?), active = ?
    // where id = ?");
    // ps.setInt(1, num);
    // ps.setInt(2, player.getSession().actived ? 1 : 0);
    // ps.setInt(3, player.getSession().userId);
    // ps.executeUpdate();
    // ps.close();
    // player.getSession().vnd -= num;
    // } catch (Exception e) {
    // Logger.logException(PlayerDAO.class, e, "Lỗi update vnd " + player.name);
    // return false;
    // } finally {
    // }
    // if (num > 1000) {
    // insertHistoryGold(player, num);
    // }
    // return true;
    // }
    public static void saveisBienHinh(Player player) {
        try (Connection con = GirlkunDB.getConnection(); PreparedStatement ps = con.prepareStatement("update player set isbienhinh = ? where id = ?");) {
            ps.setInt(1, player.isbienhinh);
            ps.setInt(2, (int) player.id);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
        }
    }

    public static boolean subvnd(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set vnd = (vnd - ?), active = ? where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().actived ? 1 : 0);
            ps.setInt(3, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().vnd -= num;
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "Lỗi update vnd" + player.name);
            return false;
        } finally {
        }
        if (num > 1000) {
            insertHistoryGold(player, num);
        }
        return true;
    }

    public static boolean subGoldBar(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set thoi_vang = (thoi_vang - ?), active = 1 where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().goldBar -= num;
        } catch (Exception e) {
            System.err.print("\nError at 48\n");
            e.printStackTrace();
            return false;
        } finally {
        }
        return false;
    }

    /*
     * public static boolean subvndBar(Player player, int num) {
     * PreparedStatement ps = null;
     * try (Connection con = GirlkunDB.getConnection();) {
     * ps = con.
     * prepareStatement("update account set vnd = (vnd - ?), active = ? where id = ?"
     * );
     * ps.setInt(1, num);
     * ps.setInt(2, player.getSession().actived ? 1 : 0);
     * ps.setInt(3, player.getSession().userId);
     * ps.executeUpdate();
     * ps.close();
     * player.getSession().vnd -= num;
     * } catch (Exception e) {
     * System.err.print("\nError at 49\n");
     * e.printStackTrace();
     * return false;
     * } finally {
     * }
     * return false;
     * }
     * 
     * public static boolean subvang(Player player, int num) {
     * PreparedStatement ps = null;
     * try (Connection con = GirlkunDB.getConnection();) {
     * ps = con.
     * prepareStatement("update account set thoi_vang = (thoi_vang - ?), mtvgt = 1 where id = ?"
     * );
     * ps.setInt(1, num);
     * ps.setInt(2, player.getSession().userId);
     * ps.executeUpdate();
     * ps.close();
     * player.getSession().vang -= num;
     * } catch (Exception e) {
     * System.err.print("\nError at 50\n");
     * e.printStackTrace();
     * return false;
     * } finally {
     * }
     * return false;
     * }
     */
    public static boolean subvip1(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set thoi_vang = (thoi_vang - ?), vip1 = 1 where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().vip1 -= num;
        } catch (Exception e) {
            System.err.print("\nError at 51\n");
            e.printStackTrace();
            return false;
        } finally {
        }
        return false;
    }

    public static boolean subvip2(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set thoi_vang = (thoi_vang - ?), vip2 = 1 where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().vip2 -= num;
        } catch (Exception e) {
            System.err.print("\nError at 52\n");
            e.printStackTrace();
            return false;
        } finally {
        }
        return false;
    }

    public static boolean subvip3(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set thoi_vang = (thoi_vang - ?), vip3 = 1 where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().vip3 -= num;
        } catch (Exception e) {
            System.err.print("\nError at 53\n");
            e.printStackTrace();
            return false;
        } finally {
        }
        return false;
    }

    public static boolean subvip4(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set thoi_vang = (thoi_vang - ?), vip4 = 1 where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().vip4 -= num;
        } catch (Exception e) {
            System.err.print("\nError at 54\n");
            e.printStackTrace();
            return false;
        } finally {
        }
        return false;
    }

    public static boolean subvip5(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set thoi_vang = (thoi_vang - ?), vip5 = 1 where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().vip5 -= num;
        } catch (Exception e) {
            System.err.print("\nError at 55\n");
            e.printStackTrace();
            return false;
        } finally {
        }
        return false;
    }

    public static boolean subvip6(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set thoi_vang = (thoi_vang - ?), vip6 = 1 where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().vip6 -= num;
        } catch (Exception e) {
            System.err.print("\nError at 56\n");
            e.printStackTrace();

            return false;
        } finally {
        }
        return false;
    }

    public static boolean subvnNbi(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set gioithieu = (gioithieu - ?), active = ? where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().mtvgtd ? 1 : 0);
            ps.setInt(3, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().gioithieu -= num;
        } catch (Exception e) {
            System.err.print("\nError at 57\n");
            e.printStackTrace();
            return false;
        } finally {
        }
        return false;
    }

    public static boolean subvnVip1(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set gioithieu = (gioithieu - ?), vip1 = ? where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().vip1d ? 1 : 0);
            ps.setInt(3, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().gioithieu -= num;
        } catch (Exception e) {
            System.err.print("\nError at 58\n");
            e.printStackTrace();
            return false;
        } finally {
        }
        return false;
    }

    public static boolean subvnVip2(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set vnd = (vnd - ?), vip2 = ? where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().vip2d ? 1 : 0);
            ps.setInt(3, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().vnd -= num;
        } catch (Exception e) {
            System.err.print("\nError at 59\n");
            e.printStackTrace();
            return false;
        } finally {
        }
        return false;
    }

    public static boolean subvnVip3(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set vnd = (vnd - ?), vip3 = ? where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().vip3d ? 1 : 0);
            ps.setInt(3, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().vnd -= num;
        } catch (Exception e) {
            System.err.print("\nError at 60\n");
            e.printStackTrace();
            return false;
        } finally {
        }
        return false;
    }

    public static boolean subvnVip4(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set vnd = (vnd - ?), vip4 = ? where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().vip4d ? 1 : 0);
            ps.setInt(3, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().vnd -= num;
        } catch (Exception e) {
            System.err.print("\nError at 61\n");
            e.printStackTrace();
            return false;
        } finally {
        }
        return false;
    }

    public static boolean subvnVip5(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set vnd = (vnd - ?), vip5 = ? where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().vip5d ? 1 : 0);
            ps.setInt(3, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().vnd -= num;
        } catch (Exception e) {
            System.err.print("\nError at 62\n");
            e.printStackTrace();
            return false;
        } finally {
        }
        return false;
    }

    public static boolean subvnVip6(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set vnd = (vnd - ?), vip6 = ? where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().vip6d ? 1 : 0);
            ps.setInt(3, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().vnd -= num;
        } catch (Exception e) {
            System.err.print("\nError at 63\n");
            e.printStackTrace();
            return false;
        } finally {
        }
        return false;
    }

    /*
     * public static boolean subcoinBar(Player player, int num) {
     * PreparedStatement ps = null;
     * try (Connection con = GirlkunDB.getConnection();) {
     * ps = con.
     * prepareStatement("update account set coin = (coin - ?), active = ? where id = ?"
     * );
     * ps.setInt(1, num);
     * ps.setInt(2, player.getSession().actived ? 1 : 0);
     * ps.setInt(3, player.getSession().userId);
     * ps.executeUpdate();
     * ps.close();
     * player.getSession().coinBar -= num;
     * } catch (Exception e) {
     * System.err.print("\nError at 64\n");
     * e.printStackTrace();
     * return false;
     * } finally {
     * }
     * if (num > 1000) {
     * insertHistoryGold(player, num);
     * }
     * return true;
     * }
     */
    public static boolean setIs_gift_box(Player player) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set is_gift_box = 0 where id = ?");
            ps.setInt(1, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            System.err.print("\nError at 65\n");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void addHistoryReceiveGoldBar(Player player, int goldBefore, int goldAfter,
            int goldBagBefore, int goldBagAfter, int goldBoxBefore, int goldBoxAfter) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("insert into history_receive_goldbar(player_id,player_name,gold_before_receive,"
                    + "gold_after_receive,gold_bag_before,gold_bag_after,gold_box_before,gold_box_after) values (?,?,?,?,?,?,?,?)");
            ps.setInt(1, (int) player.id);
            ps.setString(2, player.name);
            ps.setInt(3, goldBefore);
            ps.setInt(4, goldAfter);
            ps.setInt(5, goldBagBefore);
            ps.setInt(6, goldBagAfter);
            ps.setInt(7, goldBoxBefore);
            ps.setInt(8, goldBoxAfter);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.print("\nError at 66\n");
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
                System.err.print("\nError at 67\n");
                e.printStackTrace();
            }
        }
    }

    public static void updateItemReward(Player player) {
        String dataItemReward = "";
        for (Item item : player.getSession().itemsReward) {
            if (item.isNotNullItem()) {
                dataItemReward += "{" + item.template.id + ":" + item.quantity;
                if (!item.itemOptions.isEmpty()) {
                    dataItemReward += "|";
                    for (Item.ItemOption io : item.itemOptions) {
                        dataItemReward += "[" + io.optionTemplate.id + ":" + io.param + "],";
                    }
                    dataItemReward = dataItemReward.substring(0, dataItemReward.length() - 1) + "};";
                }
            }
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("update account set reward = ? where id = ?");
            ps.setString(1, dataItemReward);
            ps.setInt(2, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            System.err.print("\nError at 68\n");
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
                System.err.print("\nError at 69\n");
                e.printStackTrace();
            }
        }
    }

    public static boolean insertHistoryGold(Player player, int quantily) {
        PreparedStatement ps = null;
        try (Connection con = GirlkunDB.getConnection();) {
            ps = con.prepareStatement("insert into history_gold(name,gold) values (?,?)");
            ps.setString(1, player.name);
            ps.setInt(2, quantily);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            System.err.print("\nError at 70\n");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean checkLogout(Connection con, Player player) {
        long lastTimeLogout = 0;
        long lastTimeLogin = 0;
        try {
            PreparedStatement ps = con.prepareStatement("select * from account where id = ? limit 1");
            ps.setInt(1, player.getSession().userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();
                lastTimeLogin = rs.getTimestamp("last_time_login").getTime();
            }
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                System.err.print("\nError at 71\n");
                ex.printStackTrace();
            }
        } catch (Exception e) {
            System.err.print("\nError at 72\n");
            e.printStackTrace();
            return false;
        }
        return lastTimeLogout > lastTimeLogin;
    }

    public static void LogNapTIen(String uid, String menhgia, String seri, String code, String tranid) {
        String UPDATE_PASS = "INSERT INTO naptien(uid,sotien,seri,code,loaithe,time,noidung,tinhtrang,tranid,magioithieu) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try {
            Connection conn = GirlkunDB.getConnection();
            PreparedStatement ps = null;
            // UPDATE NRSD,
            ps = conn.prepareStatement(UPDATE_PASS);
            conn.setAutoCommit(false);
            // NGOC RONG SAO DEN
            ps.setString(1, uid);
            ps.setString(2, menhgia);
            ps.setString(3, seri);
            ps.setString(4, code);

            ps.setString(5, "VIETTEL");
            ps.setString(6, "123123123123");
            ps.setString(7, "dang nap the");
            ps.setString(8, "0");
            ps.setString(9, tranid);
            ps.setString(10, "0");
            if (ps.executeUpdate() == 1) {
            }

            conn.commit();
            // UPDATE NRSD
            conn.close();
        } catch (SQLException e) {
            System.err.print("\nError at 73\n");
            e.printStackTrace();
        }
    }

}
