package in.partake.view;

import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.User;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.service.EventService;
import in.partake.service.UserService;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * view の help
 * @author shinyak
 *
 * NOTE: Helper の関数は h() しなくても動作することが求められる。
 */
public class Helper {
	private static final String[] DAYS = new String[] {
			"日", "月", "火", "水", "木", "金", "土"
	};
	
	/** 参加ステータスを表示します */
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
}
