package in.partake.daemon.impl;

import static org.mockito.Mockito.mock;
import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.fixture.TestDataProviderConstants;
import in.partake.service.ITwitterService;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EventReminderTaskTest implements TestDataProviderConstants {

    @BeforeClass
    public static void setUpOnce() throws Exception {
        PartakeApp.initialize("unittest");
    }

    @Before
    public void setUp() throws Exception {
        PartakeApp.getTestService().setDefaultFixtures();
        ITwitterService twitterService = mock(ITwitterService.class);
        PartakeTestApp.setTwitterService(twitterService);
    }

    @Test
    public void sendReminderWhenEmpty() throws Exception {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                daos.getEventAccess().truncate(con);
                return null;
            }
        }.execute();

        new EventReminderTask().execute();
    }


}
