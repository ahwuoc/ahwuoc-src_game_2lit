package Dragon.jdbc.daos;

import Dragon.models.boss.BossData;
import Dragon.models.boss.TypeAppear;
import Dragon.utils.Logger;
import com.girlkun.database.GirlkunDB;
import Dragon.server.Manager;
import Dragon.models.Template.ItemTemplate;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BossDataService {

    private static BossDataService instance;

    public static BossDataService getInstance() {
        if (instance == null) {
            instance = new BossDataService();
        }
        return instance;
    }

    /**
     * Load tất cả boss data từ database
     */
    public List<BossData> loadAllBosses() {
        List<BossData> bosses = new ArrayList<>();
        Connection con = null;
        try {
            con = GirlkunDB.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM bosses WHERE is_active = TRUE ORDER BY id");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BossData bossData = mapResultSetToBossData(rs);
                if (bossData != null) {
                    // Load thêm outfit, skills, texts, rewards
                    loadBossOutfit(con, bossData);
                    loadBossSkills(con, bossData);
                    loadBossTexts(con, bossData);
                    bosses.add(bossData);
                }
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            Logger.logException(BossDataService.class, e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    Logger.logException(BossDataService.class, e);
                }
            }
        }
        return bosses;
    }

    /**
     * Load boss data theo ID
     */
    public BossData loadBossById(int bossId) {
        Connection con = null;
        try {
            con = GirlkunDB.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM bosses WHERE id = ? AND is_active = TRUE");
            ps.setInt(1, bossId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                BossData bossData = mapResultSetToBossData(rs);
                if (bossData != null) {
                    loadBossOutfit(con, bossData);
                    loadBossSkills(con, bossData);
                    loadBossTexts(con, bossData);
                    return bossData;
                }
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            Logger.logException(BossDataService.class, e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    Logger.logException(BossDataService.class, e);
                }
            }
        }
        return null;
    }

    /**
     * Map ResultSet thành BossData object
     */
    private BossData mapResultSetToBossData(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        byte gender = rs.getByte("gender");
        double dame = rs.getDouble("dame");

        // Parse JSON arrays
        String hpJson = rs.getString("hp_json");
        String mapJoinJson = rs.getString("map_join_json");
        String bossesTogetherJson = rs.getString("bosses_appear_together_json");

        double[] hp = parseJsonToDoubleArray(hpJson);
        int[] mapJoin = parseJsonToIntArray(mapJoinJson);
        int[] bossesTogether = bossesTogetherJson != null ? parseJsonToIntArray(bossesTogetherJson) : null;

        int secondsRest = rs.getInt("seconds_rest");
        TypeAppear typeAppear = TypeAppear.values()[rs.getInt("type_appear")];

        // Tạo BossData với constructor phù hợp
        BossData bossData = new BossData(name, gender, new short[] { -1, -1, -1, -1, -1, -1 },
                dame, hp, mapJoin, new int[][] {}, // skillTemp sẽ được load sau
                new String[] {}, new String[] {}, new String[] {},
                secondsRest);
        bossData.setId(rs.getInt("id"));
        bossData.setTypeAppear(typeAppear);
        bossData.setBossesAppearTogether(bossesTogether);
        return bossData;
    }

    /**
     * Load outfit cho boss
     */
    private void loadBossOutfit(Connection con, BossData bossData) {
        try {
            // 1) New simplified schema: one row with item_id -> map via ItemTemplate(head,
            // body, leg)
            short[] outfit = new short[6]; // head, body, leg, bag, aura, eff
            boolean populated = false;
            PreparedStatement psSimple = con.prepareStatement(
                    "SELECT item_id FROM boss_outfits WHERE boss_id = ? ORDER BY id LIMIT 1");
            psSimple.setInt(1, bossData.getId());
            ResultSet rsSimple = psSimple.executeQuery();
            if (rsSimple.next()) {
                short itemId = rsSimple.getShort("item_id");
                ItemTemplate tpl = null;
                for (ItemTemplate it : Manager.ITEM_TEMPLATES) {
                    if (it.id == itemId) {
                        tpl = it;
                        break;
                    }
                }
                if (tpl != null) {
                    outfit[0] = (short) tpl.head;
                    outfit[1] = (short) tpl.body;
                    outfit[2] = (short) tpl.leg;
                    // other slots remain 0
                    bossData.setOutfit(outfit);
                    populated = true;
                }
            }
            rsSimple.close();
            psSimple.close();

            if (populated) {
                return;
            }

            // 2) Legacy schema: multiple rows with slot_type + item_id
            PreparedStatement psLegacy = con.prepareStatement(
                    "SELECT slot_type, item_id FROM boss_outfits WHERE boss_id = ? ORDER BY slot_type");
            psLegacy.setInt(1, bossData.getId());
            ResultSet rsLegacy = psLegacy.executeQuery();
            while (rsLegacy.next()) {
                String slotType = rsLegacy.getString("slot_type");
                short itemId = rsLegacy.getShort("item_id");
                switch (slotType) {
                    case "head":
                        outfit[0] = itemId;
                        break;
                    case "body":
                        outfit[1] = itemId;
                        break;
                    case "leg":
                        outfit[2] = itemId;
                        break;
                    case "bag":
                        outfit[3] = itemId;
                        break;
                    case "aura":
                        outfit[4] = itemId;
                        break;
                    case "eff":
                        outfit[5] = itemId;
                        break;
                }
            }
            bossData.setOutfit(outfit);

            rsLegacy.close();
            psLegacy.close();

        } catch (Exception e) {
            Logger.logException(BossDataService.class, e);
        }
    }

    /**
     * Load skills cho boss
     */
    private void loadBossSkills(Connection con, BossData bossData) {
        try {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT skill_id, skill_level, cooldown FROM boss_skills WHERE boss_id = ? ORDER BY id");
            ps.setInt(1, bossData.getId());
            ResultSet rs = ps.executeQuery();

            List<int[]> skills = new ArrayList<>();
            while (rs.next()) {
                int skillId = rs.getInt("skill_id");
                int skillLevel = rs.getInt("skill_level");
                int cooldown = rs.getInt("cooldown");
                skills.add(new int[] { skillId, skillLevel, cooldown });
            }

            int[][] skillArray = new int[skills.size()][];
            for (int i = 0; i < skills.size(); i++) {
                skillArray[i] = skills.get(i);
            }
            bossData.setSkillTemp(skillArray);

            rs.close();
            ps.close();

        } catch (Exception e) {
            Logger.logException(BossDataService.class, e);
        }
    }

    private void loadBossTexts(Connection con, BossData bossData) {
        try {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT text_type, text_content FROM boss_texts WHERE boss_id = ? ORDER BY text_type, display_order");
            ps.setInt(1, bossData.getId());
            ResultSet rs = ps.executeQuery();

            List<String> startTexts = new ArrayList<>();
            List<String> middleTexts = new ArrayList<>();
            List<String> endTexts = new ArrayList<>();

            while (rs.next()) {
                String textType = rs.getString("text_type");
                String textContent = rs.getString("text_content");

                switch (textType) {
                    case "start":
                        startTexts.add(textContent);
                        break;
                    case "middle":
                        middleTexts.add(textContent);
                        break;
                    case "end":
                        endTexts.add(textContent);
                        break;
                }
            }

            bossData.setTextS(startTexts.toArray(new String[0]));
            bossData.setTextM(middleTexts.toArray(new String[0]));
            bossData.setTextE(endTexts.toArray(new String[0]));

            rs.close();
            ps.close();

        } catch (Exception e) {
            Logger.logException(BossDataService.class, e);
        }
    }

    /**
     * Parse JSON string thành double array
     */
    private double[] parseJsonToDoubleArray(String json) {
        if (json == null || json.isEmpty()) {
            return new double[0];
        }

        try {
            JSONArray jsonArray = (JSONArray) JSONValue.parse(json);
            double[] result = new double[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++) {
                result[i] = ((Number) jsonArray.get(i)).doubleValue();
            }
            return result;
        } catch (Exception e) {
            Logger.logException(BossDataService.class, e);
            return new double[0];
        }
    }

    /**
     * Parse JSON string thành int array
     */
    private int[] parseJsonToIntArray(String json) {
        if (json == null || json.isEmpty()) {
            return new int[0];
        }

        try {
            JSONArray jsonArray = (JSONArray) JSONValue.parse(json);
            int[] result = new int[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++) {
                result[i] = ((Number) jsonArray.get(i)).intValue();
            }
            return result;
        } catch (Exception e) {
            Logger.logException(BossDataService.class, e);
            return new int[0];
        }
    }

    /**
     * Thêm boss mới vào database
     */
    public boolean createBoss(BossData bossData) {
        Connection con = null;
        try {
            con = GirlkunDB.getConnection();
            con.setAutoCommit(false);

            // Insert vào bảng bosses
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO bosses (id, name, gender, dame, hp_json, map_join_json, "
                            + "seconds_rest, type_appear, bosses_appear_together_json) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

            ps.setInt(1, bossData.getId());
            ps.setString(2, bossData.getName());
            ps.setByte(3, bossData.getGender());
            ps.setDouble(4, bossData.getDame());
            ps.setString(5, convertArrayToJson(bossData.getHp()));
            ps.setString(6, convertArrayToJson(bossData.getMapJoin()));
            ps.setInt(7, bossData.getSecondsRest());
            ps.setInt(8, bossData.getTypeAppear().ordinal());
            ps.setString(9,
                    bossData.getBossesAppearTogether() != null ? convertArrayToJson(bossData.getBossesAppearTogether())
                            : null);

            ps.executeUpdate();
            ps.close();

            // Insert outfit, skills, texts...
            // (Có thể implement thêm các method helper)
            con.commit();
            return true;

        } catch (Exception e) {
            Logger.logException(BossDataService.class, e);
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    Logger.logException(BossDataService.class, ex);
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    Logger.logException(BossDataService.class, e);
                }
            }
        }
    }

    /**
     * Convert array thành JSON string
     */
    @SuppressWarnings("unchecked")
    private String convertArrayToJson(double[] array) {
        JSONArray jsonArray = new JSONArray();
        for (double value : array) {
            jsonArray.add(value);
        }
        return jsonArray.toJSONString();
    }

    @SuppressWarnings("unchecked")
    private String convertArrayToJson(int[] array) {
        JSONArray jsonArray = new JSONArray();
        for (int value : array) {
            jsonArray.add(value);
        }
        return jsonArray.toJSONString();
    }
}
