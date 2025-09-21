package Dragon.models.map.daihoi;

import Dragon.models.map.Map;
import Dragon.models.player.Player;
import java.util.ArrayList;

/**
 *
 * @author BTH
 */
public class DaiHoiManager {

    private static DaiHoiManager instance;

    public boolean openDHVT = false;
    public ArrayList<Long> lstIDPlayers = new ArrayList<>();
    public ArrayList<Long> lstIDPlayers2 = new ArrayList<>();
    public ArrayList<Player> lstPlayers = new ArrayList<>();
    public ArrayList<Player> lstPlayers2 = new ArrayList<>();
    public Map mapDhvt;
    public long tOpenDHVT = 0;
    public long tNextRound = 0;
    public byte roundNow = 0;
    public byte typeDHVT = 0;

    public int hourDHVT = 0;

    public static DaiHoiManager gI() {
        if (instance == null) {
            instance = new DaiHoiManager();
        }
        return instance;
    }

    public String nameRoundDHVT() {
        if (typeDHVT == (byte) 1) {
            return "Nhi Đồng";
        }
        return "Ngoại Hạng";
    }

    public String costRoundDHVT() {
        if (typeDHVT == (byte) 1) {
            return "2 ngọc";
        }
        return "10000 vàng";
    }

    public boolean isAssignDHVT(long id) {
        for (Long lstIDPlayer : lstIDPlayers) {
            if (lstIDPlayer == id) {
                return true;
            }
        }
        return false;
    }
}
