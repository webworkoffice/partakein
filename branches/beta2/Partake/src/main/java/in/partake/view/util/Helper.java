package in.partake.view.util;

import in.partake.base.Util;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.User;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.Constants;
import in.partake.service.EventService;
import in.partake.service.UserService;
import in.partake.session.CSRFPrevention;
import in.partake.session.PartakeSession;
import in.partake.session.SessionUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

/**
 * View に関する定型処理をまとめたもの。
 * 
 * @author shinyak
 */
public final class Helper {
    private static final Logger LOGGER = Logger.getLogger(Helper.class);
    private static Policy antiSamyPolicy;

    static {
        try {
            InputStream is = new BufferedInputStream(Util.createInputSteram(Constants.ANTISAMY_POLICY_FILE_RELATIVE_LOCATION));
            antiSamyPolicy = Policy.getInstance(is);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PolicyException e) {
            e.printStackTrace();
        }
    }

    public static String getSessionToken() {
        PartakeSession session = SessionUtil.getSession();
        if (session == null) { return null; }
        return session.getCSRFPrevention().getSessionToken();
    }
    
    public static String tokenTags() {
        return sessionTokenInputTag() + onetimeTokenInputTag();
    }
    
    /** CSRF 対策用の token を発行。*/
    public static String sessionTokenInputTag() {
        PartakeSession session = (PartakeSession) ServletActionContext.getContext().getSession().get(Constants.ATTR_PARTAKE_SESSION);
        assert session != null;
        
        CSRFPrevention prevention = session.getCSRFPrevention(); 
        assert (prevention != null);

        String tokenInput  = String.format("<input type=\"hidden\" name=\"%s\" value=\"%s\" />", Constants.ATTR_PARTAKE_API_SESSION_TOKEN, prevention.getSessionToken());
        return tokenInput;
    }

    /** 重複チェック用の one time token を発行 */
    public static String onetimeTokenInputTag() {
        PartakeSession session = (PartakeSession) ServletActionContext.getContext().getSession().get(Constants.ATTR_PARTAKE_SESSION);
        assert session != null;
        
        CSRFPrevention prevention = session.getCSRFPrevention(); 
        assert (prevention != null);
        
        String onetimeInput = String.format("<input type=\"hidden\" name=\"%s\" value=\"%s\" />", Constants.ATTR_PARTAKE_ONETIME_TOKEN, prevention.issueOnetimeToken());

        return onetimeInput;
    }

    /**
     * escapeHTML の短縮関数名
     * @param s
     * @return
     */
    public static String h(String s) {
        return escapeHTML(s);
    }

    /**
     * HTML で利用されるタグなどを escape する。
     * @param s
     * @return
     */
    public static String escapeHTML(String s) {
        if (s == null) { return ""; }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < s.length(); i += Character.charCount(s.codePointAt(i))) {
            switch (s.codePointAt(i)) {
            case '&': builder.append("&amp;"); break;
            case '<': builder.append("&lt;"); break;
            case '>': builder.append("&gt;"); break;
            case '"': builder.append("&quot;"); break;
            case '\'': builder.append("&apos;"); break;
            default:  
                if (Character.isIdentifierIgnorable(s.codePointAt(i))) {
                    // ignore.
                } else {
                    for (int j = 0; j < Character.charCount(s.codePointAt(i)); ++j) {
                        builder.append(s.charAt(i + j));
                    }
                }
            }
        }

        return builder.toString();
    }

    /**
     * HTMLエスケープを施すが、"&amp;gt;"と"&amp;lt;"はそのままにする。
     * これはTwitterが'&gt;'と'&lt;'だけエスケープするという問題に対応するためのものである。
     *
     * @see http://code.google.com/p/twitter-api/issues/detail?id=845
     * @see http://www.mail-archive.com/twitter-development-talk@googlegroups.com/msg09528.html
     * @see http://code.google.com/p/partakein/issues/detail?id=148
     * @param s エスケープする文字列
     * @return エスケープされた文字列
     */
    public static String escapeTwitterResponse(String s) {
        return escapeHTML(s).replaceAll("&amp;gt;", "&gt;").replaceAll("&amp;lt;", "&lt;");
    }

    /**
     * テキストを整形する。
     * @param dirtyText
     * @return
     */
    public static String cleanupText(String dirtyText) {
        String s = dirtyText;

        if (s == null) { return ""; }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < s.length(); i += Character.charCount(s.codePointAt(i))) {
            switch (s.codePointAt(i)) {
            case '&': builder.append("&amp;"); break;
            case '<': builder.append("&lt;"); break;
            case '>': builder.append("&gt;"); break;
            case '"': builder.append("&quot;"); break;
            case '\'': builder.append("&apos;"); break;
            case '\n': builder.append("<br />"); break;
            default:                
                if (Character.isIdentifierIgnorable(s.codePointAt(i))) {
                    // ignore.
                } else {
                    for (int j = 0; j < Character.charCount(s.codePointAt(i)); ++j) {
                        builder.append(s.charAt(i + j));
                    }
                }
            }
        }

        return builder.toString();
    }


    /**
     * HTML の script など、危険と思われる要素を取り除く
     * @param dirtyHTML
     * @return
     */
    public static String cleanupHTML(String dirtyHTML) {
        // Hmm...
        if (antiSamyPolicy == null) { return ""; }

        try {
            AntiSamy antiSamy = new AntiSamy();
            CleanResults cr = antiSamy.scan(dirtyHTML, antiSamyPolicy);

            return cr.getCleanHTML();
        } catch (PolicyException e) {
            e.printStackTrace();
            return "";
        } catch (ScanException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static final String[] DAYS = new String[] {
        "日", "月", "火", "水", "木", "金", "土"
    };

    /** 参加ステータスを表示します */
    // TODO: Don't call Service from here!
    public static String enrollmentStatus(User user, Event event) {
        try {
            ParticipationStatus status = UserService.get().getParticipationStatus(user.getId(), event.getId());

            switch (status) {
            case ENROLLED: {
                int order = EventService.get().getOrderOfEnrolledEvent(event.getId(), user.getId());
                if (order <= event.getCapacity() || event.getCapacity() == 0) {
                    return "参加";
                } else {
                    return "補欠 (参加予定)";
                }
            }
            case RESERVED: {
                int order = EventService.get().getOrderOfEnrolledEvent(event.getId(), user.getId());
                if (order <= event.getCapacity() || event.getCapacity() == 0) {
                    return "仮参加";
                } else {
                    return "補欠 (仮参加予定)";
                }
            }
            case NOT_ENROLLED:
                return "未参加";
            case CANCELLED:
                return "キャンセル";
            default:
                return "エラー";
            }
        } catch (DAOException e) {
            e.printStackTrace();
            return "データベースエラー";
        }
    }   

    /** 日時を読みやすい形で表示します */
    public static String readableDate(Date d) {

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int date = cal.get(Calendar.DAY_OF_MONTH);
        int day = cal.get(Calendar.DAY_OF_WEEK);

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        String dayStr;
        switch (day) {
        case Calendar.SUNDAY:    dayStr = DAYS[0]; break;
        case Calendar.MONDAY:    dayStr = DAYS[1]; break;
        case Calendar.TUESDAY:   dayStr = DAYS[2]; break;
        case Calendar.WEDNESDAY: dayStr = DAYS[3]; break;
        case Calendar.THURSDAY:  dayStr = DAYS[4]; break;
        case Calendar.FRIDAY:    dayStr = DAYS[5]; break;
        case Calendar.SATURDAY:  dayStr = DAYS[6]; break;
        default: dayStr = "-";
        }

        return String.format("%04d年%d月%d日(%s) %02d:%02d", year, month, date, dayStr, hour, min);
    }

    public static String readableDuration(Date beginDate, Date endDate) {
        if (endDate == null) {
            return readableDate(beginDate);
        }

        if (areSameDay(beginDate, endDate)) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(endDate);

            return readableDate(beginDate) + 
            String.format(" - %02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        } else {
            return readableDate(beginDate) + " - " + readableDate(endDate);
        }
    }

    /**
     * @author skypencil (@eller86)
     */
    private static boolean areSameDay(Date beginDate, Date endDate) {
        assert beginDate != null;
        assert endDate != null;

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        return
        begin.get(Calendar.YEAR) == end.get(Calendar.YEAR) &&
        begin.get(Calendar.MONTH) == end.get(Calendar.MONTH) &&
        begin.get(Calendar.DAY_OF_MONTH) == end.get(Calendar.DAY_OF_MONTH);
    }

    public static String readableCapacity(int enrolled, int capacity) {
        if (capacity == 0) { return String.format("%d 人 / -", enrolled); }
        else { return String.format("%d 人 / %d 人", enrolled, capacity); }       
    }

    public static String readableReminder(Date date) {
        if (date == null) { return "未送付"; }
        return String.format("送付済 (%s)", readableDate(date));
    }

    // cache css version to invalidate user cache
    // see http://code.google.com/p/partakein/issues/detail?id=45
    private static String cachedCssVersion;
    public static String getCssVersion() {
        return cachedCssVersion == null ? "unknown" : cachedCssVersion;
    }
    public static void setCssVersion(String version) {
        cachedCssVersion = version;
    }

    public static String javascript(String... relativePaths) {
        // TODO: すべてつなげて minify したものを作成し、それを常によむようにすると高速化されるので、そうしたい。
        StringBuilder builder = new StringBuilder();
        
        String contextPath = ServletActionContext.getRequest().getContextPath();
        for (String relativePath : relativePaths) {
            String filePath = ServletActionContext.getServletContext().getRealPath(relativePath);
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                LOGGER.warn(String.format("specified file(%s) doesn't exist. check argument(%s) and stored files.", filePath, relativePath));
                continue;
            }
            long time = file.lastModified();
            
            String absolutePath = String.format("%s%s", contextPath, relativePath);
            builder.append(String.format("<script type=\"text/javascript\" src=\"%s?%d\"></script>\n", h(absolutePath), time));
        }
        
        return builder.toString();
    }
}
