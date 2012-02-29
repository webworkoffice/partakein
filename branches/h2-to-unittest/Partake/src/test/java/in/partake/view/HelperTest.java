package in.partake.view;

import in.partake.view.util.Helper;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public final class HelperTest {
	private static TimeZone defaultTimeZone;

	@BeforeClass
	public static void setTimeZone() {
		defaultTimeZone = TimeZone.getDefault();
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));
	}

	@AfterClass
	public static void resetTimeZone() {
		TimeZone.setDefault(defaultTimeZone);
	}

	// -----------------------------------------
	// readableDate
	@Test
	public void testReadableDateAtMorning() {
		Date date = createDate(2010, 1, 1, 9, 0);
		Assert.assertEquals("2010年1月1日(金) 09:00", Helper.readableDate(date));
	}

	@Test
	public void testReadableDateAtAfternoon() {
		Date date = createDate(2010, 1, 1, 15, 0);
		Assert.assertEquals("2010年1月1日(金) 15:00", Helper.readableDate(date));
	}

	@Test
	public void testReadableDateAtMidnight() {
		Date lastMin  = createDate(2010, 1, 1, 23, 59);
		Assert.assertEquals("2010年1月1日(金) 23:59", Helper.readableDate(lastMin));

		Date midnight = createDate(2010, 1, 1, 24, 0);
		Assert.assertEquals("2010年1月2日(土) 00:00", Helper.readableDate(midnight));
	}

	@Test
	public void testZeroSuppressed() {
		Date saturday  = createDate(2010, 01, 01, 11, 22);
		Assert.assertEquals("2010年1月1日(金) 11:22", Helper.readableDate(saturday));
	}

	@Test
	public void testZeropaddinged() {
		Date saturday  = createDate(0001, 12, 23, 00, 00);
		Assert.assertEquals("0001年12月23日(金) 00:00", Helper.readableDate(saturday));
	}

	@Test
	public void testReadableDateAllDaysOfWeek() {
		Date sunday    = createDate(2010, 12, 26, 00, 00);
		Assert.assertEquals("2010年12月26日(日) 00:00", Helper.readableDate(sunday));

		Date monday    = createDate(2010, 12, 27, 00, 00);
		Assert.assertEquals("2010年12月27日(月) 00:00", Helper.readableDate(monday));

		Date tuesday   = createDate(2010, 12, 28, 00, 00);
		Assert.assertEquals("2010年12月28日(火) 00:00", Helper.readableDate(tuesday));

		Date wednesday = createDate(2010, 12, 29, 00, 00);
		Assert.assertEquals("2010年12月29日(水) 00:00", Helper.readableDate(wednesday));

		Date thursday  = createDate(2010, 12, 30, 00, 00);
		Assert.assertEquals("2010年12月30日(木) 00:00", Helper.readableDate(thursday));

		Date friday    = createDate(2010, 12, 31, 00, 00);
		Assert.assertEquals("2010年12月31日(金) 00:00", Helper.readableDate(friday));

		Date saturday  = createDate(2011, 01, 01, 00, 00);
		Assert.assertEquals("2011年1月1日(土) 00:00", Helper.readableDate(saturday));
	}

	// -----------------------------------------
	// readableDuration
	@Test
	public void testReadableDuration1Day() {
		Date beginDate = createDate(2010, 1, 1,  9, 0);
		Date endDate   = createDate(2010, 1, 1, 14, 0);
		Assert.assertEquals("2010年1月1日(金) 09:00 - 14:00", Helper.readableDuration(beginDate, endDate));
	}

	@Test
	public void testReadableDuration2Day() {
		Date beginDate = createDate(2010, 1, 1, 20, 0);
		Date endDate   = createDate(2010, 1, 2, 05, 0);
		Assert.assertEquals("2010年1月1日(金) 20:00 - 2010年1月2日(土) 05:00", Helper.readableDuration(beginDate, endDate));
	}

	@Test
	public void testReadableDuration1Month() {
		Date beginDate = createDate(2010, 1, 1, 20, 0);
		Date endDate   = createDate(2010, 2, 1, 20, 0);
		Assert.assertEquals("2010年1月1日(金) 20:00 - 2010年2月1日(月) 20:00", Helper.readableDuration(beginDate, endDate));
	}

	@Test
	public void testReadableDuration1Year() {
		Date beginDate = createDate(2010, 1, 1, 20, 0);
		Date endDate   = createDate(2011, 1, 1, 20, 0);
		Assert.assertEquals("2010年1月1日(金) 20:00 - 2011年1月1日(土) 20:00", Helper.readableDuration(beginDate, endDate));
	}

	private Date createDate(int year, int month, int day, int hour, int minute) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("JST"), Locale.JAPANESE);
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		return calendar.getTime();
	}
}