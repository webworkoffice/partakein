package in.partake.daemon.impl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.MessageEnvelope;
import in.partake.model.dto.TwitterMessage;
import in.partake.model.dto.auxiliary.MessageDelivery;
import in.partake.model.fixture.TestDataProviderConstants;
import in.partake.service.ITwitterService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import twitter4j.TwitterException;
import twitter4j.internal.http.HttpResponse;

public class SendMessageEnvelopeTaskTest extends AbstractPartakeControllerTest implements TestDataProviderConstants {
    private static final String TWITTER_MESSAGE_WILLFAIL_MESSAGE = "Sending this message should fail";

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
    public void sendTwitterMessageBeforeTryAfter() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        String twitterMessageId = UUID.randomUUID().toString();
        TwitterMessage message = new TwitterMessage(twitterMessageId, DEFAULT_USER_ID, TWITTER_MESSAGE_WILLFAIL_MESSAGE, MessageDelivery.INQUEUE, new DateTime(0), null);
        storeTwitterMessage(message);

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = new MessageEnvelope(envelopeId.toString(), null, twitterMessageId, null, 0, null, null, now.nHourAfter(1), now, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        // |modified| should not be changed.
        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified.getNumTried(), is(0));
        assertThat(modified.getLastTriedAt(), is(nullValue()));
    }

    @Test
    public void sendTwitterMessageWithTwitterException() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        HttpResponse res = mock(HttpResponse.class);
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Limit"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Remaining"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Reset"));
        doThrow(new TwitterException("message", res)).when(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq(TWITTER_MESSAGE_WILLFAIL_MESSAGE));

        String twitterMessageId = UUID.randomUUID().toString();
        TwitterMessage message = new TwitterMessage(twitterMessageId, DEFAULT_USER_ID, TWITTER_MESSAGE_WILLFAIL_MESSAGE, MessageDelivery.INQUEUE, new DateTime(0), null);
        storeTwitterMessage(message);

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId.toString(), twitterMessageId, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified.getNumTried(), is(1));
        assertThat(modified.getLastTriedAt(), is(now));
        assertThat(modified.getTryAfter(), is(now.nSecAfter(600)));
    }

    @Test
    public void sendTwitterMessageWithTwitterExceptionCausedByNetworkError() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        // When |cause| is IOException, TwitterException thinks it's a network error.
        doThrow(new TwitterException("message", new IOException(""))).when(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq(TWITTER_MESSAGE_WILLFAIL_MESSAGE));

        String twitterMessageId = UUID.randomUUID().toString();
        TwitterMessage message = new TwitterMessage(twitterMessageId, DEFAULT_USER_ID, TWITTER_MESSAGE_WILLFAIL_MESSAGE, MessageDelivery.INQUEUE, new DateTime(0), null);
        storeTwitterMessage(message);

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId.toString(), twitterMessageId, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified.getNumTried(), is(1));
        assertThat(modified.getLastTriedAt(), is(now));
        assertThat(modified.getTryAfter(), is(now.nSecAfter(600))); // 5 min later
    }

    @Test
    public void sendTwitterMessageWithTwitterExceptionExceededLimit() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        HttpResponse res = mock(HttpResponse.class);
        doReturn(400).when(res).getStatusCode();
        doReturn("100").when(res).getResponseHeader(eq("X-RateLimit-Limit"));
        doReturn("100").when(res).getResponseHeader(eq("X-RateLimit-Remaining"));
        doReturn("1").when(res).getResponseHeader(eq("X-RateLimit-Reset"));
        doThrow(new TwitterException("message", res)).when(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq(TWITTER_MESSAGE_WILLFAIL_MESSAGE));

        String twitterMessageId = UUID.randomUUID().toString();
        TwitterMessage message = new TwitterMessage(twitterMessageId, DEFAULT_USER_ID, TWITTER_MESSAGE_WILLFAIL_MESSAGE, MessageDelivery.INQUEUE, new DateTime(0), null);
        storeTwitterMessage(message);

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId.toString(), twitterMessageId, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified.getNumTried(), is(1));
        assertThat(modified.getLastTriedAt(), is(now));
        // TODO: We should think how to test this.
        // assertThat(modified.getTryAfter(), is(now.nSecAfter(1))); // 1 sec after.
    }

    @Test
    public void sendTwitterMessageWithTwitterExceptionCausedByUnauthorized() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        HttpResponse res = mock(HttpResponse.class);
        doReturn(401).when(res).getStatusCode();
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Limit"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Remaining"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Reset"));
        doThrow(new TwitterException("message", res)).when(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq(TWITTER_MESSAGE_WILLFAIL_MESSAGE));

        String twitterMessageId = UUID.randomUUID().toString();
        TwitterMessage message = new TwitterMessage(twitterMessageId, DEFAULT_USER_ID, TWITTER_MESSAGE_WILLFAIL_MESSAGE, MessageDelivery.INQUEUE, new DateTime(0), null);
        storeTwitterMessage(message);

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId.toString(), twitterMessageId, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        // Message Envelop should be removed.
        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified, is(nullValue()));

        // User should be unauthorized.
        UserEx user = loadUserEx(DEFAULT_USER_ID);
        assertThat(user.getTwitterLinkage().getAccessToken(), is(nullValue()));
        assertThat(user.getTwitterLinkage().getAccessTokenSecret(), is(nullValue()));
    }

    @Test
    public void sendTwitterMessageWithInvalidId() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        HttpResponse res = mock(HttpResponse.class);
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Limit"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Remaining"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Reset"));
        doThrow(new TwitterException("message", res)).when(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq(TWITTER_MESSAGE_WILLFAIL_MESSAGE));

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId.toString(), UUID.randomUUID().toString(), null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        // The message should be removed from the queue.
        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified, is(nullValue()));
    }

    @Test
    public void sendTwitterMessageWithInvalidUserId() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        HttpResponse res = mock(HttpResponse.class);
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Limit"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Remaining"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Reset"));
        doThrow(new TwitterException("message", res)).when(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq(TWITTER_MESSAGE_WILLFAIL_MESSAGE));

        String twitterMessageId = UUID.randomUUID().toString();
        TwitterMessage message = new TwitterMessage(twitterMessageId, INVALID_USER_ID, TWITTER_MESSAGE_WILLFAIL_MESSAGE, MessageDelivery.INQUEUE, new DateTime(0), null);
        storeTwitterMessage(message);

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId.toString(), twitterMessageId, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        // The message should be removed from the queue.
        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified, is(nullValue()));
    }

    @Test
    public void sendTwitterMessageWithNoTwitterLinkUser() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        HttpResponse res = mock(HttpResponse.class);
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Limit"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Remaining"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Reset"));
        doThrow(new TwitterException("message", res)).when(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq(TWITTER_MESSAGE_WILLFAIL_MESSAGE));

        String twitterMessageId = UUID.randomUUID().toString();
        TwitterMessage message = new TwitterMessage(twitterMessageId, USER_NO_TWITTER_LINK_ID, TWITTER_MESSAGE_WILLFAIL_MESSAGE, MessageDelivery.INQUEUE, new DateTime(0), null);
        storeTwitterMessage(message);

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId.toString(), twitterMessageId, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        // The message should be removed from the queue.
        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified, is(nullValue()));
    }

    @Test
    public void sendTwitterMessageWithNoAuthorizedTwitterLinkUser() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        TimeUtil.setCurrentDateTime(now);

        HttpResponse res = mock(HttpResponse.class);
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Limit"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Remaining"));
        doReturn(null).when(res).getResponseHeader(eq("X-RateLimit-Reset"));
        doThrow(new TwitterException("message", res)).when(PartakeApp.getTwitterService()).updateStatus(anyString(), anyString(), eq(TWITTER_MESSAGE_WILLFAIL_MESSAGE));

        String twitterMessageId = UUID.randomUUID().toString();
        TwitterMessage message = new TwitterMessage(twitterMessageId, USER_TWITTER_NOAUTH_ID, TWITTER_MESSAGE_WILLFAIL_MESSAGE, MessageDelivery.INQUEUE, new DateTime(0), null);
        storeTwitterMessage(message);

        UUID envelopeId = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForTwitterMessage(envelopeId.toString(), twitterMessageId, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        // The message should be removed from the queue.
        MessageEnvelope modified = loadEnvelope(envelopeId.toString());
        assertThat(modified, is(nullValue()));
    }

    // ----------------------------------------------------------------------

    @Test
    public void sendUserNotification() throws Exception {
        UUID uuid = UUID.randomUUID();
        MessageEnvelope envelope = MessageEnvelope.createForUserNotification(uuid.toString(), USER_NOTIFICATION_INQUEUE_ID, null);
        queueEnvelope(envelope);

        new SendMessageEnvelopeTask().run();

        List<MessageEnvelope> rest = loadEnvelopes();
        assertThat(rest.isEmpty(), is(true));
        verify(PartakeApp.getTwitterService()).sendDirectMesage(anyString(), anyString(), eq(DEFAULT_TWITTER_ID), anyString());

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
        verify(PartakeApp.getTwitterService()).sendDirectMesage(anyString(), anyString(), eq(DEFAULT_RECEIVER_TWITTER_ID), anyString());

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

    // -----

    private void storeTwitterMessage(final TwitterMessage message) throws Exception {
        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                daos.getTwitterMessageAccess().put(con, message);
                return null;
            }
        }.execute();
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
