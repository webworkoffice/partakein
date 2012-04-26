package in.partake.base;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;

/**
 * Time utility functions.
 *
 * @author shinyak
 */
public final class TimeUtil {
    private static Date currentDate;
    private static final DateFormat dateFormatForEvent = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private TimeUtil() {
        // Prevents from instantiation.
    }

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

    public static DateTime getCurrentDateTime() {
        if (currentDate != null)
            return new DateTime(currentDate.getTime());
        else
            return new DateTime(System.currentTimeMillis());
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

    public static void setCurrentDateTime(DateTime dt) {
        currentDate = new Date(dt.getTime());
    }

    public static void setCurrentDate(Date date) {
        currentDate = date;
    }

    public static void setCurrentTime(long time) {
        currentDate = new Date(time);
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
        } while (now == TimeUtil.getCurrentTime());
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

    public static Calendar calendar(Date date) {
        if (date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String formatForEvent(DateTime date) {
        return dateFormatForEvent.format(date.toDate());
    }

    public static String formatForEvent(Date date) {
        return dateFormatForEvent.format(date);
    }

    public static DateTime parseForEvent(String dateStr) {
        try {
            return new DateTime(dateFormatForEvent.parse(dateStr).getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    @Deprecated
    public static DateTime oneDayBefore(DateTime dt) {
        return dt.nDayBefore(1);
    }

    @Deprecated
    public static DateTime oneDayAfter(DateTime dt) {
        return dt.nDayAfter(1);
    }

    public static DateTime halfDayBefore(DateTime dt) {
        return new DateTime(dt.getTime() - 1000 * 3600 * 12);
    }

    public static DateTime halfDayAfter(DateTime dt) {
        return new DateTime(dt.getTime() + 1000 * 3600 * 12);
    }

    @Deprecated
    public static Date oneDayBefore(Date date) {
        return new Date(date.getTime() - 1000 * 3600 * 24);
    }

    @Deprecated
    public static Date halfDayBefore(Date date) {
        return new Date(date.getTime() - 1000 * 3600 * 12);
    }

    @Deprecated
    public static Date oneDayAfter(Date date) {
        return new Date(date.getTime() + 1000 * 3600 * 24);
    }

    @Deprecated
    public static Date halfDayAfter(Date date) {
        return new Date(date.getTime() + 1000 * 3600 * 12);
    }

    public static DateTime dateTimeFromTimeString(String timeString) {
        try {
            return new DateTime(Long.parseLong(timeString));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Deprecated
    public static Date dateFromTimeString(String timeString) {
        try {
            return new Date(Long.parseLong(timeString));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String getTimeString(DateTime date) {
        return getTimeString(date.getTime());
    }

    @Deprecated
    public static String getTimeString(Date date) {
        return getTimeString(date.getTime());
    }

    public static String getTimeString(long time) {
        return new Formatter().format("%020d", time).toString();
    }
}
