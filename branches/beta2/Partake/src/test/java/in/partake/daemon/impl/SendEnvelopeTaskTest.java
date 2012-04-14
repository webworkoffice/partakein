package in.partake.daemon.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Envelope;
import in.partake.model.dto.DirectMessage;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;
import in.partake.model.fixture.TestDataProviderConstants;
import in.partake.service.ITwitterService;

import java.util.Date;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SendEnvelopeTaskTest implements TestDataProviderConstants {

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
    public void sendFromEmptyQueue() throws Exception {
        assertThat(countEnvelope(), is(0));
        new SendEnvelopeTask().run();
        assertThat(countEnvelope(), is(0));

        verify(PartakeApp.getTwitterService(), never()).sendDirectMesage(anyString(), anyString(), anyLong(), anyString());
        verify(PartakeApp.getTwitterService(), never()).updateStatus(anyString(), anyString(), anyString());
    }

    @Test
    public void sendDirectMessage() throws Exception {
        String messageId = UUID.randomUUID().toString();
        DirectMessage message = new DirectMessage(messageId, DEFAULT_USER_ID, "message", null, new Date(0L));
        Envelope envelope = new Envelope(UUID.randomUUID().toString(),
                DEFAULT_USER_ID, DEFAULT_USER_ID, messageId, null, 0, null, null, DirectMessagePostingType.POSTING_TWITTER_DIRECT, new Date(0L));
        addQueue(message, envelope);

        new SendEnvelopeTask().run();
        assertThat(countEnvelope(), is(0));

        verify(PartakeApp.getTwitterService(), only()).sendDirectMesage(anyString(), anyString(), anyLong(), anyString());
        verify(PartakeApp.getTwitterService(), never()).updateStatus(anyString(), anyString(), anyString());
    }

    private void addQueue(final DirectMessage message, final Envelope envelope) throws Exception {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                daos.getDirectMessageAccess().put(con, message);
                daos.getEnvelopeAccess().put(con, envelope);
                return null;
            }
        }.execute();
    }

    private int countEnvelope() throws Exception {
        return new DBAccess<Integer>() {
            @Override
            protected Integer doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getEnvelopeAccess().count(con);
            }
        }.execute();
    }
}
