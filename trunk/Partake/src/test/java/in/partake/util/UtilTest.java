package in.partake.util;

import java.util.Date;

import org.junit.Test;

import junit.framework.Assert;


public class UtilTest {

	@Test
	public void dateConverterTest() {
		Date date1 = new Date();
		Date date2 = Util.dateFromTimeString(Util.getTimeString(date1));
		Assert.assertEquals(date1, date2);
	}

	@Test
	public void dateConverterCornerTest1() {
		Date date1 = new Date(Long.MAX_VALUE);
		Date date2 = Util.dateFromTimeString(Util.getTimeString(date1));
		Assert.assertEquals(date1, date2);
	}

	@Test
	public void dateConverterCornerTest2() {
		Date date1 = new Date(0);
		Date date2 = Util.dateFromTimeString(Util.getTimeString(date1));
		Assert.assertEquals(date1, date2);
	}

	
}
