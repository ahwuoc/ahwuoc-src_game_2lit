package Dragon.models.npc;

import Dragon.models.player.Player;
import Dragon.server.Client;
import Dragon.services.Service;
import Dragon.utils.Util;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DEV By TeEt
 */
public class DuaHau {

    // sl defaut
    public int Count = 1;
    public int plDuaHau;
    public float BinhNuoc;
    public int Phanbon;
    // đủ củi - nước > nấu
    public long ThoiGianChoDH;
    public long ThoiGianTrong;
    // sau khi chở > nấu
    public boolean ChoXong = false;
    public boolean NauXong = false;
    // sau khi nấu chờ lấy > ko lấy qua turn
    public long ThoiGianChoDuaHau;
    // player add list
    public List<Player> ListPlDuaHau = new ArrayList<>();
    // bảo trì
    public boolean baotri = false;
    // get insrance
    private static DuaHau instance;

    public static DuaHau gI() {
        if (instance == null) {
            instance = new DuaHau();
        }
        return instance;
    }

    public void addListPlDuaHau(Player pl) {
        if (!ListPlDuaHau.equals(pl)) {
            ListPlDuaHau.add(pl);
        }
    }

    public void removeListPlDuaHau(Player pl) {
        ListPlDuaHau.remove(pl);
    }

    public void run() {
        try {
            if (ThoiGianChoDH != 0 && Util.canDoWithTime(ThoiGianChoDH, 1000)) {
                ThoiGianChoDH = 0;
            }
            if (ThoiGianTrong != 0 && Util.canDoWithTime(ThoiGianTrong, 1000)) {
                ThoiGianTrong = 0;
            }
            if (ThoiGianChoDuaHau != 0 && Util.canDoWithTime(ThoiGianChoDuaHau, 1000)) {
                ThoiGianChoDuaHau = 0;
            }
            if (BinhNuoc >= 1 && Phanbon >= 1) {
                if (ChoXong == false && ((ThoiGianChoDH - System.currentTimeMillis()) / 1000) == 0) {
                    ChoXong = true;
                    ThoiGianTrong = System.currentTimeMillis() + (1000 * 15);
                } else if (NauXong == false && ChoXong == true && ((ThoiGianTrong - System.currentTimeMillis()) / 1000) == 0) {
                    NauXong = true;
                    ThoiGianChoDuaHau = System.currentTimeMillis() + (1000 * 15);
                    if (!ListPlDuaHau.isEmpty()) {
                        for (int i = 0; i < ListPlDuaHau.size(); i++) {
                            Player pl = this.ListPlDuaHau.get(i);
                            if (pl != null && Client.gI().getPlayer(pl.name) != null) {
                                pl.DuaHau += plDuaHau;
                                Service.getInstance().sendThongBaoFromAdmin(pl, "|7|Dưa hấu đã chín rồi mời bạn đến nhận");
                                ListPlDuaHau.clear();
                                break;
                            }
                        }
                    }
                } else if (NauXong == true && ((ThoiGianChoDuaHau - System.currentTimeMillis()) / 1000) == 0) {
                    Service.gI().sendThongBaoAllPlayer("dưa hấu đã trồng xong, tiếp tục trồng dưa hấu thôi nào");
                    plDuaHau = 0;
                    ListPlDuaHau.clear();
                    Phanbon = 0;
                    BinhNuoc = 0;
                    NauXong = false;
                    ChoXong = false;
                    Count++;
                    ThoiGianChoDuaHau = -1;
                    ThoiGianChoDH = System.currentTimeMillis() + (1000 * 15);
                }
            } else {
                ThoiGianChoDH = System.currentTimeMillis() + (1000 * 15);
            }
        } catch (Exception e) {
            System.out.print("Return Thread Fail");
        }
    }
}
