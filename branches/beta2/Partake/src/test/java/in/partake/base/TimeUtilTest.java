package in.partake.base;

import in.partake.base.TimeUtil;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class TimeUtilTest {
	@Before
	public void setUp() {
		TimeUtil.resetCurrentDate();
	}

    @Test
    public void testCurrentDate1() {
        Date now = new Date(1L);
        Assert.assertFalse(now.equals(TimeUtil.getCurrentDate()));

        TimeUtil.setCurrentDate(now);
        Assert.assertEquals(now, TimeUtil.getCurrentDate());
    }

    @Test
    public void testCurrentDate2() {
        long d1 = TimeUtil.getCurrentTime();
        long d2 = new Date().getTime();
        long d3 = TimeUtil.getCurrentTime();
        long d4 = new Date().getTime();
        
        Assert.assertTrue(d1 <= d2);
        Assert.assertTrue(d2 <= d3);
        Assert.assertTrue(d3 <= d4);
    }
}
