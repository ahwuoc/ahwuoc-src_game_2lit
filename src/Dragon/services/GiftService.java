package Dragon.services;

import Dragon.MaQuaTang.MaQuaTang;
import Dragon.MaQuaTang.MaQuaTangManager;
import Dragon.models.player.Player;
import com.girlkun.database.GirlkunDB;
import Dragon.jdbc.daos.GiftDAO;
import Dragon.models.item.Item;
import com.girlkun.result.GirlkunResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

/**
 *
 * @Refactored By Ahwuocda 💖
 *
 */
public class GiftService {

    private static GiftService i;

    private GiftService() {

    }

    public String code;
    public int idGiftcode;
    public int gold;
    public int gem;
    public int dayexits;
    public Timestamp timecreate;
    public ArrayList<Item> listItem = new ArrayList<>();
    public static ArrayList<GiftService> gifts = new ArrayList<>();

    public static GiftService gI() {
        if (i == null) {
            i = new GiftService();
        }
        return i;
    }

    public void giftCode(Player player, String code) {
        // Sử dụng hệ thống gift code mới
        GiftCodeService.GiftCodeResult result = GiftCodeService.getInstance().useGiftCode(player, code);

        if (result.success) {
            Service.getInstance().sendThongBao(player, result.message);
        } else {
            // Thông báo lỗi cụ thể để dễ debug
            Service.getInstance().sendThongBao(player, result.message);
        }
    }

}
