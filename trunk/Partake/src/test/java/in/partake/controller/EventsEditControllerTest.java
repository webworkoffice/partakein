package in.partake.controller;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.User;
import in.partake.resource.Constants;
import in.partake.util.PDate;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

public final class EventsEditControllerTest extends AbstractPartakeControllerTest {

	/**
	 * {@link EventsEditController#editNew()}メソッドについて、以下のように
	 * 実装されていることを保証する。
	 * <ul>
	 * <li>EventsEditControllerが正常にインスタンス化できること
	 * <li>開始時刻・終了時刻・締切時刻が現在の24時間後になっていること
	 * <li>開始時刻・終了時刻・締切時刻の分（minute）が0になっていること
	 * </ul>
	 * 
	 * @author skypencil (@eller86)
	 * @throws InterruptedException 
	 */
    @Test
	public void testToEditNewTest() throws InterruptedException {		
	    TimeZone timeZone = TimeZone.getDefault();
	    
	    // 現在時刻に依存するテストケースであるため、少なくとも分（minute）が異なる2点でテストする必要がある
	    PDate.setCurrentDate(new PDate(2010, 1, 1, 0, 0, 0, timeZone));
		editNewTestInner();

		PDate.setCurrentDate(new PDate(2010, 1, 1, 0, 1, 20, timeZone));
        editNewTestInner();

        PDate.setCurrentDate(new PDate(2015, 12, 31, 23, 59, 59, timeZone));
        editNewTestInner();

        PDate.setCurrentDate(new PDate(2000, 10, 30, 23, 59, 48, timeZone));
        editNewTestInner();

        PDate.resetCurrentDate();
	}
	
	private void editNewTestInner() {
		EventsEditController controller = new EventsEditController();
		Date oneDayAfter = new Date(PDate.getCurrentTime() + 1000 * 3600 * 24);
		final Calendar oneDayAfterCalendar = Calendar.getInstance();
		oneDayAfterCalendar.setTime(oneDayAfter);

		final String result = controller.editNew();
		Assert.assertEquals(Action.INPUT, result);

		// Start Date
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.YEAR), controller.getSyear());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.MONTH) + 1, controller.getSmonth());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.DAY_OF_MONTH), controller.getSday());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.HOUR_OF_DAY), controller.getShour());
		Assert.assertEquals(0, controller.getSmin());	// 2回繰り返すことで、ここがたまたま通ってしまう可能性を排除

		// End Date
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.YEAR), controller.getEyear());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.MONTH) + 1, controller.getEmonth());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.DAY_OF_MONTH), controller.getEday());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.HOUR_OF_DAY), controller.getEhour());
		Assert.assertEquals(0, controller.getEmin());

		// Deadline
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.YEAR), controller.getDyear());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.MONTH) + 1, controller.getDmonth());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.DAY_OF_MONTH), controller.getDday());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.HOUR_OF_DAY), controller.getDhour());
		Assert.assertEquals(0, controller.getDmin());
	}

	/**
	 * @see http://code.google.com/p/partakein/issues/detail?id=120
	 */
    @Test
    @Ignore("We should have issue120 user in DB. Ignore this for now.")
	public void testIssue120() throws DAOException {
		UserEx loginUser = createUserEx("issue120");
		Map<String, Object> session = new HashMap<String, Object>();
		session.put(Constants.ATTR_USER, loginUser);

		EventsEditController controller = new EventsEditController();
		controller.setSession(session);

		Assert.assertEquals(Action.INPUT, controller.editNew());
		controller.setTitle("TITLE");
		controller.setSummary("SUMMARY");
		controller.setDescription("DESCRIPTION");
		controller.setCategory("CATEGORY");
		Assert.assertEquals(Action.SUCCESS, controller.create());
		int thisYear = Calendar.getInstance(Locale.JAPAN).get(Calendar.YEAR);
		Assert.assertEquals(thisYear, controller.getSyear());
		Assert.assertEquals(thisYear, controller.getEyear());
		Assert.assertEquals(thisYear, controller.getDyear());
		String eventId = controller.getEventId();

		controller = new EventsEditController();
		controller.setSession(session);
		controller.setEventId(eventId);
		Assert.assertEquals(Action.SUCCESS, controller.edit());	// TODO this assert fail. why? -> because UserEx("Issue120") isn't stored.
		Assert.assertEquals(thisYear, controller.getSyear());
		Assert.assertEquals(thisYear, controller.getEyear());
		Assert.assertEquals(thisYear, controller.getDyear());
	}

	private UserEx createUserEx(String userId) {
		User user = new User();
		user.setId(userId);
		UserEx userEx = new UserEx(user, null);
		return userEx;
	}
}