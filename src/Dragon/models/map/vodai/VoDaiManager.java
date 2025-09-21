package Dragon.models.map.vodai;

import Dragon.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bùi Kim Trường
 */
public class VoDaiManager {

    private static VoDaiManager i;
    private long lastUpdate;
    private static List<VoDai> list = new ArrayList<>();
    private static List<VoDai> toRemove = new ArrayList<>();

    public static VoDaiManager gI() {
        if (i == null) {
            i = new VoDaiManager();
        }
        return i;
    }

    public void update() {
        if (Util.canDoWithTime(lastUpdate, 1000)) {
            lastUpdate = System.currentTimeMillis();
            synchronized (list) {
                for (VoDai mc : list) {
                    try {
                        mc.update();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                list.removeAll(toRemove);
            }
        }
    }

    public void add(VoDai mc) {
        synchronized (list) {
            list.add(mc);
        }
    }

    public void remove(VoDai mc) {
        synchronized (toRemove) {
            toRemove.add(mc);
        }
    }
}
