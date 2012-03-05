package in.partake.service.mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dao.mock.MockConnection;
import in.partake.model.daofacade.deprecated.DeprecatedUserDAOFacade;
import in.partake.model.daofacade.deprecated.DeprecatedUserDAOFacade.UserCount;
import in.partake.model.dto.User;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class UserServiceTest extends MockServiceTestBase {
    private DataIterator<User> mockIter;
	private IUserAccess userAccess;

	@Before
    public void setup() throws Exception {
        createFixtures();
        TimeUtil.setCurrentTime(System.currentTimeMillis());
    }

    @SuppressWarnings("unchecked")
	private void createFixtures() throws Exception {
        userAccess = getFactory().getUserAccess();
        mockIter = mock(DataIterator.class);
        when(userAccess.getIterator(any(MockConnection.class))).thenReturn(mockIter);
    }

    @Test(expected = NullPointerException.class)
    public void getUserByNullExId() throws DAOException {
        DeprecatedUserDAOFacade.get().getUserExById(null);
    }

    @Test(expected = NullPointerException.class)
    public void loginByOpenIDByNullId() throws DAOException {
        DeprecatedUserDAOFacade.get().loginByOpenID(null);
    }

    @Test
    public void getUserByExId() throws DAOException {
    	final String userExId = "userExId";
        when(userAccess.find(any(MockConnection.class), eq(userExId))).thenReturn(createUser(userExId));
        User user = DeprecatedUserDAOFacade.get().getUserExById(userExId);
        verify(userAccess, times(1)).find(any(MockConnection.class), eq(userExId));
        Assert.assertEquals(userExId, user.getId());
    }

	@Test
    public void countUserEmpty() throws DAOException {
        DeprecatedUserDAOFacade service = DeprecatedUserDAOFacade.get();
        UserCount count = service.countUsers();
        Assert.assertEquals(0, count.activeUser);
        Assert.assertEquals(0, count.user);
    }

    @Test
    public void countOnlyOneActiveUser() throws DAOException {
        User user = new User();
        user.setLastLoginAt(new Date(TimeUtil.getCurrentTime()));
        when(mockIter.hasNext()).thenReturn(true, false);
        when(mockIter.next()).thenReturn(user);

        DeprecatedUserDAOFacade service = DeprecatedUserDAOFacade.get();
        UserCount count = service.countUsers();
        verify(mockIter, times(2)).hasNext();
        verify(mockIter, times(1)).next();
        Assert.assertEquals(1, count.activeUser);
        Assert.assertEquals(1, count.user);
    }

    @Test
    public void countOnlyOneUserWhoLogin29DaysAgo() throws DAOException {
        User user = new User();
        user.setLastLoginAt(new Date(TimeUtil.getCurrentTime() - 29L * 24 * 60 * 60 * 1000));
        when(mockIter.hasNext()).thenReturn(true, false);
        when(mockIter.next()).thenReturn(user);

        DeprecatedUserDAOFacade service = DeprecatedUserDAOFacade.get();
        UserCount count = service.countUsers();
        verify(mockIter, times(2)).hasNext();
        verify(mockIter, times(1)).next();
        Assert.assertEquals(1, count.activeUser);
        Assert.assertEquals(1, count.user);
    }

    @Test
    public void countOnlyOneUserWhoLogin29DaysAgo2() throws DAOException {
        User user = new User();
        user.setLastLoginAt(new Date(TimeUtil.getCurrentTime() - 30L * 24 * 60 * 60 * 1000 + 1));
        when(mockIter.hasNext()).thenReturn(true, false);
        when(mockIter.next()).thenReturn(user);

        DeprecatedUserDAOFacade service = DeprecatedUserDAOFacade.get();
        UserCount count = service.countUsers();
        verify(mockIter, times(2)).hasNext();
        verify(mockIter, times(1)).next();
        Assert.assertEquals(1, count.activeUser);
        Assert.assertEquals(1, count.user);
    }

    @Test
    public void countOnlyOneUserWhoLogin30DaysAgo() throws DAOException {
        User user = new User();
        user.setLastLoginAt(new Date(TimeUtil.getCurrentTime() - 30L * 24 * 60 * 60 * 1000));
        when(mockIter.hasNext()).thenReturn(true, false);
        when(mockIter.next()).thenReturn(user);

        DeprecatedUserDAOFacade service = DeprecatedUserDAOFacade.get();
        UserCount count = service.countUsers();
        verify(mockIter, times(2)).hasNext();
        verify(mockIter, times(1)).next();
        Assert.assertEquals(0, count.activeUser);
        Assert.assertEquals(1, count.user);
    }
}
