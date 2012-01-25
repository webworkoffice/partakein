package in.partake.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Partake 用の時刻。Immutable Object として実装すること。
 *
 * @author shinyak
 */
// TODO: We should remove PDate object.
// However, get/set CurrentDate, waitForTick() are useful for testing purpose.
// Maybe we should extract TimeUtil.
public class PDate {
    private static PDate currentDate;
    private Date date;

    public PDate(Date date) {
        this.date = new Date(date.getTime());
    }

    public PDate(long time) {
        this.date = new Date(time);
    }

    public PDate(int year, int month, int day, int hour, int min, int sec, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        calendar.setTimeZone(timeZone);

        date = calendar.getTime();
    }

    /**
     * 現在時刻を返す。もし、現在時刻が陽に設定されていれば、その時刻を返す。
     * そうでなければ、OS から現在時刻を取得して返す。
     * @return
     */
    public static PDate getCurrentDate() {
        if (currentDate != null) { return currentDate; }
        return new PDate(new Date());
    }

    /**
     * 現在時刻をミリ秒単位で返す。現在時刻が陽に設定されていれば、その現在時刻を返す。
     * そうでなければ、new Date().getTime() と同じ。
     * @return
     */
    public static long getCurrentTime() {
        if (currentDate != null) { return currentDate.date.getTime(); }
        return new Date().getTime();
    }

    public static void setCurrentDate(PDate currentDate) {
        PDate.currentDate = currentDate;
    }

    public static void setCurrentTime(long time) {
        PDate.currentDate = new PDate(time);
    }

    public static void waitForTick() {
        if (PDate.currentDate == null) {
            long now = new Date().getTime();
            do {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // ignore.
                }
            } while (now == new Date().getTime());
        } else {
            PDate.setCurrentDate(new PDate(currentDate.getDate().getTime() + 20));
        }
    }

    public static void resetCurrentDate() {
        PDate.currentDate = null;
    }

    // ----------------------------------------------------------------------

    public Date getDate() {
        return (Date) date.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PDate)) { return false; }

        PDate lhs = this;
        PDate rhs = (PDate) obj;

        if (lhs.date == rhs.date) { return true; }
        if (lhs.date == null || rhs.date == null) { return false; }
        return lhs.date.equals(rhs.date);
    }

    @Override
    public int hashCode() {
        if (date == null) { return 0; }
        return date.hashCode();
    }

}
