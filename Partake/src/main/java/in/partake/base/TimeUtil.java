package in.partake.base;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Partake 用の時刻。Immutable Object として実装すること。
 *
 * @author shinyak
 */
public class TimeUtil {
    private static Date currentDate;

    /**
     * Resets the current date.
     */
    public static void resetCurrentDate() {
        TimeUtil.currentDate = null;
    }

    /**
     * 現在時刻を返す。もし、現在時刻が陽に設定されていれば、その時刻を返す。
     * そうでなければ、OS から現在時刻を取得して返す。
     * @return
     */
    public static Date getCurrentDate() {
        if (currentDate != null)
            return currentDate;
        else
            return new Date();
    }

    /**
     * 現在時刻をミリ秒単位で返す。現在時刻が陽に設定されていれば、その現在時刻を返す。
     * そうでなければ、new Date().getTime() と同じ。
     * @return
     */
    public static long getCurrentTime() {
        if (currentDate != null)
            return currentDate.getTime();
        else
            return new Date().getTime();
    }

    public static void setCurrentDate(Date date) {
        currentDate = date;
    }

    public static void setCurrentTime(long time) {
        currentDate = new Date(time);
    }

    public static Date create(int year, int month, int date, int hour, int min, int sec, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, date);
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        calendar.setTimeZone(timeZone);

        return calendar.getTime();
    }
    
    /**
     * Waits for a while.
     */
    public static void waitForTick() {
        if (currentDate != null) {
            setCurrentTime(currentDate.getTime() + 20);
            return;
        }
        
        long now = new Date().getTime();
        do {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // ignore.
            }
        } while (now == new Date().getTime());
    }
}
