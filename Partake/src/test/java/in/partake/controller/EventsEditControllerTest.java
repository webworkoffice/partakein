package in.partake.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
	 * <li>開始時刻・終了時刻・締切時刻の分（minute）が0になっていること
	 * </ul>
	 * 
	 * 既知の問題：午前にテストケースを実行した場合、時（hour）が24時間表記になっていることをテストできない
	 * 
	 * @author skypencil (@eller86)
	 * @throws InterruptedException 
	 */
	@Test
	public void editNewTest() throws InterruptedException {
		try {
			editNewTestInner();

			// 現在時刻に依存するテストケースであるため、少なくとも分（minute）が異なる2点でテストする必要がある
			TimeUnit.SECONDS.sleep(70);
			editNewTestInner();
		} catch (InterruptedException e) {
			// we cannot ignore this exception
			// because this exception means that our testcase didn't run twice.
			throw e;
		}
	}
	
	private void editNewTestInner() {
		EventsEditController controller = null;
		Date oneDayAfter = null;
		final Calendar oneDayAfterCalendar = Calendar.getInstance();
		for (int i = 0; i < 20 && (controller == null || oneDayAfterCalendar.get(Calendar.HOUR_OF_DAY) != controller.getShour()); ++i) {
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
}