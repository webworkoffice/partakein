package in.partake.service.mock;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.mock.MockConnection;
import in.partake.model.dao.mock.MockConnectionPool;
import in.partake.model.dto.Event;
import in.partake.service.MessageService;
import in.partake.util.PDate;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.*;


public class MessageServiceTest extends MockServiceTestBase {
        
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

    @Test
    public void sendRemindersEmpty() throws DAOException {
        MockConnectionPool pool = (MockConnectionPool) getPool();
        MockConnection mockCon = mock(MockConnection.class);
        pool.prepareConnection(mockCon);

        MessageService service = MessageService.get();
        IEventAccess eventAccess = getFactory().getEventAccess();

        @SuppressWarnings("unchecked")
        DataIterator<Event> iter = mock(DataIterator.class);
        doReturn(iter).when(eventAccess).getIterator(mockCon);
        doReturn(false).when(iter).hasNext();
        doThrow(new NoSuchElementException()).when(iter).next();

        try {
            service.sendReminders();
        } catch (DAOException e) {
            Assert.fail();
        }

        verify(eventAccess, times(1)).getIterator(mockCon);
        verify(mockCon, times(1)).beginTransaction();
        verify(mockCon, never()).rollback();
        verify(mockCon, times(1)).invalidate();
        verify(mockCon, times(1)).commit();
    }

    @Test
    public void sendRemindersWithException() throws DAOException {
        MockConnectionPool pool = (MockConnectionPool) getPool();
        MockConnection mockCon = mock(MockConnection.class);
        pool.prepareConnection((MockConnection) mockCon);

        MessageService service = MessageService.get();
        DAOException injectedException = new DAOException();
        doThrow(injectedException).when(getFactory().getEventAccess()).getIterator(any(PartakeConnection.class));
        doThrow(new DAOException()).when(mockCon).rollback();

        try {
            service.sendReminders();
            Assert.fail();
        } catch (DAOException thrownException) {
            Assert.assertSame(injectedException, thrownException);
        }

        verify(mockCon, times(1)).beginTransaction();
        verify(mockCon, times(1)).rollback();
        verify(mockCon, times(1)).invalidate();
        verify(mockCon, never()).commit();
    }
}
