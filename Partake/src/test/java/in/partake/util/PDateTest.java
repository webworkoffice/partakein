package in.partake.util;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

public class PDateTest {
    
    @Test
    public void testCurrentDate1() {
        PDate now = new PDate(new Date());
        PDate.setCurrentDate(now);
        
        Assert.assertEquals(now, PDate.getCurrentDate());
    }

    @Test
    public void testCurrentDate2() {
        PDate.resetCurrentDate();
        
        long d1 = PDate.getCurrentTime();
        long d2 = new Date().getTime();
        long d3 = PDate.getCurrentTime();
        long d4 = new Date().getTime();
        
        Assert.assertTrue(d1 <= d2);
        Assert.assertTrue(d2 <= d3);
        Assert.assertTrue(d3 <= d4);
    }
}
