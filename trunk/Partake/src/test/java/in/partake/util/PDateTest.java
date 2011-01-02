package in.partake.util;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class PDateTest {
	@Before
	public void resetPDate() {
		PDate.resetCurrentDate();
	}

    @Test
    public void testCurrentDate1() {
        PDate now = new PDate(new Date(1L));
        Assert.assertFalse(now.equals(PDate.getCurrentDate()));

        PDate.setCurrentDate(now);
        Assert.assertEquals(now, PDate.getCurrentDate());
    }

    @Test
    public void testCurrentDate2() {
        long d1 = PDate.getCurrentTime();
        long d2 = new Date().getTime();
        long d3 = PDate.getCurrentTime();
        long d4 = new Date().getTime();
        
        Assert.assertTrue(d1 <= d2);
        Assert.assertTrue(d2 <= d3);
        Assert.assertTrue(d3 <= d4);
    }
}
