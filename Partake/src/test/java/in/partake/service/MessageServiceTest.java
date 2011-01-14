package in.partake.service;

import in.partake.util.PDate;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

public class MessageServiceTest {

    @Test
    public void testNeedsToSendWhenLastSentDateIsNull() throws Exception {
        Assert.assertTrue(needsToSend(
                        new PDate(2000, 1, 10, 0, 0, 0, TimeZone.getDefault()).getDate(),
                        new PDate(2000, 1,  9, 0, 0, 0, TimeZone.getDefault()).getDate(),
                        null));
        
        Assert.assertTrue(needsToSend(
                        new PDate(2000, 1, 10,  0,  0,  0, TimeZone.getDefault()).getDate(),
                        new PDate(2000, 1,  9, 23, 59, 59, TimeZone.getDefault()).getDate(),
                        null));
        
        Assert.assertFalse(needsToSend(
                        new PDate(2000, 1, 10, 0, 0, 0, TimeZone.getDefault()).getDate(),
                        new PDate(2000, 1, 10, 0, 0, 1, TimeZone.getDefault()).getDate(),
                        null));
    }
    
    @Test
    public void testNeedsToSendWhenLastSentDateIsNotNull() throws Exception {
        Assert.assertTrue(needsToSend(
                        new PDate(2000, 1, 10, 0, 0, 0, TimeZone.getDefault()).getDate(),
                        new PDate(2000, 1,  9, 0, 0, 0, TimeZone.getDefault()).getDate(),
                        new PDate(2000, 1,  9, 0, 0, 0, TimeZone.getDefault()).getDate()));

        Assert.assertTrue(needsToSend(
                        new PDate(2000, 1, 10,  0,  0,  1, TimeZone.getDefault()).getDate(),
                        new PDate(2000, 1, 10,  0,  0,  0, TimeZone.getDefault()).getDate(),
                        new PDate(2000, 1,  9, 22, 59, 59, TimeZone.getDefault()).getDate()));

        Assert.assertFalse(needsToSend(
                        new PDate(2000, 1, 10,  0, 0, 0, TimeZone.getDefault()).getDate(),
                        new PDate(2000, 1,  9,  0, 0, 0, TimeZone.getDefault()).getDate(),
                        new PDate(2000, 1,  9, 23, 0, 1, TimeZone.getDefault()).getDate()));
        
        Assert.assertFalse(needsToSend(
                        new PDate(2000, 1, 10, 0, 0, 0, TimeZone.getDefault()).getDate(),
                        new PDate(2000, 1,  9, 0, 0, 0, TimeZone.getDefault()).getDate(),
                        new PDate(2000, 1,  9, 0, 0, 1, TimeZone.getDefault()).getDate()));

    }
    
    
    private boolean needsToSend(Date now, Date targetDate, Date lastSent) throws Exception {
        Method method = MessageService.class.getDeclaredMethod("needsToSend", Date.class, Date.class, Date.class);
        method.setAccessible(true);
        Object args[] = { now, targetDate, lastSent };
        Boolean result = (Boolean)method.invoke(MessageService.class, args);
        return result;
    }
}
