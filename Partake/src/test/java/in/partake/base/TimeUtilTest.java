package in.partake.base;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.Date;

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
        Date now = new Date();
        assertThat(TimeUtil.getCurrentTime(), is(greaterThanOrEqualTo(now.getTime())));
        assertThat(TimeUtil.getCurrentDate().getTime(), is(greaterThanOrEqualTo(now.getTime())));
        assertThat(TimeUtil.getCurrentDateTime().getTime(), is(greaterThanOrEqualTo(now.getTime())));

        TimeUtil.setCurrentDate(new Date(0L));
        assertThat(TimeUtil.getCurrentTime(), is(0L));
        assertThat(TimeUtil.getCurrentDate().getTime(), is(0L));
        assertThat(TimeUtil.getCurrentDateTime().getTime(), is(0L));

        TimeUtil.setCurrentTime(0L);
        assertThat(TimeUtil.getCurrentTime(), is(0L));
        assertThat(TimeUtil.getCurrentDate().getTime(), is(0L));
        assertThat(TimeUtil.getCurrentDateTime().getTime(), is(0L));

        TimeUtil.setCurrentDateTime(new DateTime(0L));
        assertThat(TimeUtil.getCurrentTime(), is(0L));
        assertThat(TimeUtil.getCurrentDate().getTime(), is(0L));
        assertThat(TimeUtil.getCurrentDateTime().getTime(), is(0L));

        TimeUtil.resetCurrentDate();
        assertThat(TimeUtil.getCurrentTime(), is(not(0L)));
        assertThat(TimeUtil.getCurrentDate().getTime(), is(not(0L)));
        assertThat(TimeUtil.getCurrentDateTime().getTime(), is(not(0L)));
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
        DateTime date1 = TimeUtil.getCurrentDateTime();
        DateTime date2 = TimeUtil.dateTimeFromTimeString(TimeUtil.getTimeString(date1));
        Assert.assertEquals(date1, date2);
    }

    @Test
    public void dateConverterCornerTest1() {
        DateTime date1 = new DateTime(Long.MAX_VALUE);
        DateTime date2 = TimeUtil.dateTimeFromTimeString(TimeUtil.getTimeString(date1));
        Assert.assertEquals(date1, date2);
    }

    @Test
    public void dateConverterCornerTest2() {
        DateTime date1 = new DateTime(0);
        DateTime date2 = TimeUtil.dateTimeFromTimeString(TimeUtil.getTimeString(date1));
        Assert.assertEquals(date1, date2);
    }

    @Test
    public void testOneDayBefore() {
        DateTime date = TimeUtil.getCurrentDateTime();
        DateTime before = TimeUtil.oneDayBefore(date);

        assertThat(before.getTime(), is(date.getTime() - 3600 * 24 * 1000));
    }

    @Test
    public void testHalfDayBefore() {
        DateTime date = TimeUtil.getCurrentDateTime();
        DateTime before = TimeUtil.halfDayBefore(date);

        assertThat(before.getTime(), is(date.getTime() - 3600 * 12 * 1000));
    }

    @Test
    public void testOneDayAfter() {
        DateTime date = TimeUtil.getCurrentDateTime();
        DateTime after = TimeUtil.oneDayAfter(date);

        assertThat(after.getTime(), is(date.getTime() + 3600 * 24 * 1000));
    }

}
