package Dragon.thuongnhanthanbi;

import Dragon.utils.TimeUtil;

public class thuongnhanthanbi {

    public static final byte HOUR_OPEN = 18;
    public static final byte MIN_OPEN = 0;
    public static final byte SECOND_OPEN = 0;

    public static final byte HOUR_CLOSE = 22;
    public static final byte MIN_CLOSE = 0;
    public static final byte SECOND_CLOSE = 0;

    private static thuongnhanthanbi i;

    public static long TIME_OPEN;
    public static long TIME_CLOSE;

    private int day = -1;

    public static thuongnhanthanbi gI() {
        if (i == null) {
            i = new thuongnhanthanbi();
        }
        i.setTime();
        return i;
    }

    public void setTime() {
        if (i.day == -1 || i.day != TimeUtil.getCurrDay()) {
            i.day = TimeUtil.getCurrDay();
            try {
                this.TIME_OPEN = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_OPEN + ":" + MIN_OPEN + ":" + SECOND_OPEN, "dd/MM/yyyy HH:mm:ss");
                this.TIME_CLOSE = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_CLOSE + ":" + MIN_CLOSE + ":" + SECOND_CLOSE, "dd/MM/yyyy HH:mm:ss");
            } catch (Exception e) {

            }
        }
    }

}
