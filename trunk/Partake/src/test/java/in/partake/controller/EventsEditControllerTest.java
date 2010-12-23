package in.partake.controller;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

public final class EventsEditControllerTest {

	/**
	 * {@link EventsEditController#editNew()}メソッドについて、以下のように
	 * 実装されていることを保証する。
	 * <ul>
	 * <li>EventsEditControllerが正常にインスタンス化できること
	 * <li>開始時刻・終了時刻・締切時刻が現在の24時間後になっていること
	 * </ul>
	 * 
	 * @author skypencil (@eller86)
	 */
	@Test
	public void editNewTest() {
		EventsEditController controller = null;
		Date oneDayAfter = null;
		final Calendar oneDayAfterCalendar = Calendar.getInstance();
		for (int i = 0; i < 20 && (controller == null || oneDayAfterCalendar.get(Calendar.MINUTE) != controller.getSmin()); ++i) {
			oneDayAfter = new Date(new Date().getTime() + 1000 * 3600 * 24);

			// ここで他のプロセスにCPU取られると、Controller内部のoneDayAfterとここで宣言したローカル変数のoneDayAfterが
			// equalsじゃなくなる可能性があるので、forで何度か試行する

			controller = new EventsEditController();
			oneDayAfterCalendar.setTime(oneDayAfter);
		}

		final String result = controller.editNew();
		Assert.assertEquals(Action.INPUT, result);

		// Start Date
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.YEAR), controller.getSyear());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.MONTH) + 1, controller.getSmonth());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.DAY_OF_MONTH), controller.getSday());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.HOUR_OF_DAY), controller.getShour());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.MINUTE), controller.getSmin());

		// End Date
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.YEAR), controller.getEyear());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.MONTH) + 1, controller.getEmonth());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.DAY_OF_MONTH), controller.getEday());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.HOUR_OF_DAY), controller.getEhour());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.MINUTE), controller.getEmin());

		// Deadline
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.YEAR), controller.getDyear());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.MONTH) + 1, controller.getDmonth());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.DAY_OF_MONTH), controller.getDday());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.HOUR_OF_DAY), controller.getDhour());
		Assert.assertEquals(oneDayAfterCalendar.get(Calendar.MINUTE), controller.getDmin());
	}
}