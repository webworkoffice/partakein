package in.partake.util;

import java.util.Date;

/**
 * Partake 用の時刻。Immutable Object として実装すること。
 * 
 * @author shinyak
 *
 */
public class PDate {
    private static PDate currentDate;
    private Date date;
    
    public PDate(Date date) {
        this.date = date;
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
    
    public static void resetCurrentDate() {
        PDate.currentDate = null;
    }
}
