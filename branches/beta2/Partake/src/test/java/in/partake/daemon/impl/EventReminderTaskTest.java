package in.partake.daemon.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Envelope;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.fixture.TestDataProviderConstants;
import in.partake.model.fixture.impl.EventTestDataProvider;
import in.partake.service.ITwitterService;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class EventReminderTaskTest extends AbstractPartakeControllerTest implements TestDataProviderConstants {

    @Before
    public void setUp() throws Exception {
        super.setUp();

        PartakeApp.getTestService().setDefaultFixtures();
        ITwitterService twitterService = mock(ITwitterService.class);
        PartakeTestApp.setTwitterService(twitterService);
    }

    @Test
    public void sendReminderWhenEmpty() throws Exception {
        truncateEvents();
        new EventReminderTask().run();
    }

    @Test
    public void sendReminderForEnrolledUser() throws Exception {
        truncateEvents();

        Date now = new Date();
        TimeUtil.setCurrentDate(now);

        Event event = new EventTestDataProvider().create();
        event.setOwnerId(EVENT_OWNER_ID);
        event.setBeginDate(TimeUtil.oneDayAfter(now));
        event.setDeadline(null);
        storeEvent(event);

        Enrollment enrollment = new Enrollment(UUID.randomUUID().toString(), DEFAULT_USER_ID, event.getId(), "", ParticipationStatus.ENROLLED, false, ModificationStatus.ENROLLED, AttendanceStatus.UNKNOWN, now);
        storeEnrollment(enrollment);

        TimeUtil.setCurrentDate(TimeUtil.halfDayAfter(now));

        // Since no reminder is stored, reminder should be sent.
        new EventReminderTask().run();

        List<Envelope> envelopes = loadEnvelopes();
        boolean found = false;
        for (Envelope envelope : envelopes) {
            if (envelope == null)
                continue;
            if (envelope.getPostingType() == DirectMessagePostingType.POSTING_TWITTER_DIRECT && DEFAULT_USER_ID.equals(envelope.getReceiverId()))
                found = true;
        }
        assertThat(found, is(true));
    }

    private void truncateEvents() throws Exception {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                daos.getEventAccess().truncate(con);
                return null;
            }
        }.execute();

    }
}
