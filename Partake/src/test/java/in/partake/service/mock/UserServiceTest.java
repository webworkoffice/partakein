package in.partake.service.mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.util.Date;

import junit.framework.Assert;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.mock.MockConnection;
import in.partake.model.dto.User;
import in.partake.service.UserService;
import in.partake.service.UserService.UserCount;
import in.partake.util.PDate;

import org.junit.Before;
import org.junit.Test;

public class UserServiceTest extends MockServiceTestBase {
    private DataIterator<User> mockIter;

	@Before
    public void setup() throws Exception {
        // create fixtures.
        reset();
        createFixtures();
        PDate.setCurrentTime(System.currentTimeMillis());
    }

    @SuppressWarnings("unchecked")
	private void createFixtures() throws Exception {
        IUserAccess userAccess = getFactory().getUserAccess();
        mockIter = mock(DataIterator.class);
        when(userAccess.getIterator(any(MockConnection.class))).thenReturn(mockIter);
    }

    @Test
    public void countUserEmpty() throws DAOException {
        UserService service = UserService.get();
        UserCount count = service.countUsers();
        Assert.assertEquals(0, count.activeUser);
        Assert.assertEquals(0, count.user);
    }

    @Test
    public void countOnlyOneActiveUser() throws DAOException {
        User user = new User();
        user.setLastLoginAt(new Date(PDate.getCurrentTime()));
        when(mockIter.hasNext()).thenReturn(true, false);
        when(mockIter.next()).thenReturn(user);

        UserService service = UserService.get();
        UserCount count = service.countUsers();
        verify(mockIter, times(2)).hasNext();
        verify(mockIter, times(1)).next();
        Assert.assertEquals(1, count.activeUser);
        Assert.assertEquals(1, count.user);
    }

    @Test
    public void countOnlyOneUserWhoLogin29DaysAgo() throws DAOException {
        User user = new User();
        user.setLastLoginAt(new Date(PDate.getCurrentTime() - 29L * 24 * 60 * 60 * 1000));
        when(mockIter.hasNext()).thenReturn(true, false);
        when(mockIter.next()).thenReturn(user);

        UserService service = UserService.get();
        UserCount count = service.countUsers();
        verify(mockIter, times(2)).hasNext();
        verify(mockIter, times(1)).next();
        Assert.assertEquals(1, count.activeUser);
        Assert.assertEquals(1, count.user);
    }

    @Test
    public void countOnlyOneUserWhoLogin29DaysAgo2() throws DAOException {
        User user = new User();
        user.setLastLoginAt(new Date(PDate.getCurrentTime() - 30L * 24 * 60 * 60 * 1000 + 1));
        when(mockIter.hasNext()).thenReturn(true, false);
        when(mockIter.next()).thenReturn(user);

        UserService service = UserService.get();
        UserCount count = service.countUsers();
        verify(mockIter, times(2)).hasNext();
        verify(mockIter, times(1)).next();
        Assert.assertEquals(1, count.activeUser);
        Assert.assertEquals(1, count.user);
    }

    @Test
    public void countOnlyOneUserWhoLogin30DaysAgo() throws DAOException {
        User user = new User();
        user.setLastLoginAt(new Date(PDate.getCurrentTime() - 30L * 24 * 60 * 60 * 1000));
        when(mockIter.hasNext()).thenReturn(true, false);
        when(mockIter.next()).thenReturn(user);

        UserService service = UserService.get();
        UserCount count = service.countUsers();
        verify(mockIter, times(2)).hasNext();
        verify(mockIter, times(1)).next();
        Assert.assertEquals(0, count.activeUser);
        Assert.assertEquals(1, count.user);
    }
}
