package in.partake.base;

import in.partake.base.TimeUtil;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.Matchers.*;

import static org.junit.Assert.assertThat;
import org.junit.Assert;
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
        
        assertThat(d1, lessThanOrEqualTo(d2));
        assertThat(d2, lessThanOrEqualTo(d3));
        assertThat(d3, lessThanOrEqualTo(d4));
    }
    
    @Test
    public void dateConverterTest() {
        Date date1 = new Date();
        Date date2 = TimeUtil.dateFromTimeString(TimeUtil.getTimeString(date1));
        Assert.assertEquals(date1, date2);
    }

    @Test
    public void dateConverterCornerTest1() {
        Date date1 = new Date(Long.MAX_VALUE);
        Date date2 = TimeUtil.dateFromTimeString(TimeUtil.getTimeString(date1));
        Assert.assertEquals(date1, date2);
    }

    @Test
    public void dateConverterCornerTest2() {
        Date date1 = new Date(0);
        Date date2 = TimeUtil.dateFromTimeString(TimeUtil.getTimeString(date1));
        Assert.assertEquals(date1, date2);
    }
    
    @Test
    public void testOneDayBefore() {
        Date date = new Date();
        Date before = TimeUtil.oneDayBefore(date);
        
        assertThat(before.getTime(), is(date.getTime() - 3600 * 24 * 1000));        
    }
    
    @Test
    public void testHalfDayBefore() {
        Date date = new Date();
        Date before = TimeUtil.halfDayBefore(date);
        
        assertThat(before.getTime(), is(date.getTime() - 3600 * 12 * 1000));        
    }

    @Test
    public void testOneDayAfter() {
        Date date = new Date();
        Date after = TimeUtil.oneDayAfter(date);
        
        assertThat(after.getTime(), is(date.getTime() + 3600 * 24 * 1000));        
    }

}
