package in.partake.daemon.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.MessageEnvelope;
import in.partake.model.dto.auxiliary.MessageDelivery;
import in.partake.model.fixture.TestDataProviderConstants;
import in.partake.service.ITwitterService;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class SendMessageEnvelopeTaskTest extends AbstractPartakeControllerTest implements TestDataProviderConstants {
    @Before
    public void setUp() throws Exception {
        super.setUp();

        PartakeApp.getTestService().setDefaultFixtures();
        ITwitterService twitterService = mock(ITwitterService.class);
        PartakeTestApp.setTwitterService(twitterService);

        // Removes MessageEnvelope.
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                daos.getMessageEnvelopeAccess().truncate(con);
                return null;
            }
        }.execute();
    }

    @Test
    public void sendEmpty() throws Exception {
        new SendMessageEnvelopeTask().run();
    }

    @Test
    public void sendTwitterMessage() throws Exception {
        UUID uuid = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(uuid.toString(), TWITTER_MESSAGE_INQUEUE_ID, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        List<MessageEnvelope> rest = loadEnvelopes();
        assertThat(rest.isEmpty(), is(true));
        verify(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq("message"));

        assertThat(loadTwitterMessage(TWITTER_MESSAGE_INQUEUE_ID).getDelivery(), is(MessageDelivery.SUCCESS));
    }

    @Test
    public void sendUserNotification() throws Exception {
        UUID uuid = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForUserNotification(uuid.toString(), USER_NOTIFICATION_INQUEUE_ID, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        List<MessageEnvelope> rest = loadEnvelopes();
        assertThat(rest.isEmpty(), is(true));
        verify(PartakeApp.getTwitterService()).sendDirectMesage(anyString(), anyString(), eq(Long.parseLong(DEFAULT_TWITTER_ID)), anyString());

        assertThat(loadUserNotification(USER_NOTIFICATION_INQUEUE_ID).getDelivery(), is(MessageDelivery.SUCCESS));
    }

    @Test
    public void sendUserMessage() throws Exception {
        UUID uuid = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForUserMessage(uuid.toString(), USER_RECEIVED_MESSAGE_INQUEUE_ID.toString(), null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        List<MessageEnvelope> rest = loadEnvelopes();
        assertThat(rest.isEmpty(), is(true));
        verify(PartakeApp.getTwitterService()).sendDirectMesage(anyString(), anyString(), eq(Long.parseLong(DEFAULT_RECEIVER_TWITTER_ID)), anyString());

        assertThat(loadUserReceivedMessage(USER_RECEIVED_MESSAGE_INQUEUE_ID).getDelivery(), is(MessageDelivery.SUCCESS));
    }

    @Test
    public void sendInvalidatedMessage() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        DateTime before = now.nDayBefore(1);

        UUID uuid = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(uuid.toString(), TWITTER_MESSAGE_INQUEUE_ID, before);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        List<MessageEnvelope> rest = loadEnvelopes();
        assertThat(rest.isEmpty(), is(true));
        verify(PartakeApp.getTwitterService(), never()).updateStatus(anyString(), anyString(), anyString());
        assertThat(loadTwitterMessage(TWITTER_MESSAGE_INQUEUE_ID).getDelivery(), is(MessageDelivery.FAIL));
    }

    private void queueEnvelope(final MessageEnvelope envelope) throws Exception {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                daos.getMessageEnvelopeAccess().put(con, envelope);
                return null;
            }
        }.execute();
    }
}
