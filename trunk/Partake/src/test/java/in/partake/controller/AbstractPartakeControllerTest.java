package in.partake.controller;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.base.PartakeParamNamesConstants;
import in.partake.base.TimeUtil;
import in.partake.controller.base.AbstractPartakeController;
import in.partake.model.EventEx;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.daoutil.DAOUtil;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.EventTicketNotification;
import in.partake.model.dto.MessageEnvelope;
import in.partake.model.dto.TwitterMessage;
import in.partake.model.dto.UserCalendarLink;
import in.partake.model.dto.UserImage;
import in.partake.model.dto.UserNotification;
import in.partake.model.dto.UserOpenIDLink;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.UserReceivedMessage;
import in.partake.model.dto.UserThumbnail;
import in.partake.model.dto.UserTicket;
import in.partake.model.fixture.TestDataProviderConstants;
import in.partake.resource.Constants;
import in.partake.resource.ServerErrorCode;
import in.partake.session.PartakeSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.struts2.StrutsTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;

// When a test class extends TestCase class, such a test class seems to run on JUnit3.
// To avoid it, @RunWith(JUnit4.class) is set.
@RunWith(JUnit4.class)
public abstract class AbstractPartakeControllerTest extends StrutsTestCase
    implements TestDataProviderConstants, PartakeActionURLConstants, PartakeParamNamesConstants {

    @BeforeClass
    public static void setUpOnce() throws Exception {
        PartakeApp.initialize("unittest");
    }

    // Make setUp called before each test.
    @Before
    public void setUp() throws Exception {
        super.setUp();
        PartakeApp.getTestService().setDefaultFixtures();
        TimeUtil.resetCurrentDate();
    }

    // Make tearDown called after each test.
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * action proxy を取得し、
     *  1) session がなければ付与する。
     *  2) parameters がなければ付与する。
     */
    @Override
    protected ActionProxy getActionProxy(String uri) {
        ActionProxy proxy = super.getActionProxy(uri);
        if (proxy == null) { return null; }

        ActionContext actionContext = proxy.getInvocation().getInvocationContext();

        if (actionContext.getSession() == null) {
            Map<String, Object> session = new HashMap<String, Object>();
            actionContext.setSession(session);

            // Adds Partake session
            session.put(Constants.ATTR_PARTAKE_SESSION, PartakeSession.createInitialPartakeSession());
        }

        if (actionContext.getParameters() == null) {
            Map<String, Object> parameters = new HashMap<String, Object>();
            actionContext.setParameters(parameters);
        }

        // Request has key named "request".
        if (actionContext.get("request") == null)
            actionContext.put("request", new HashMap<String, Object>());

        return proxy;
    }

    protected PartakeSession getPartakeSession(ActionProxy proxy) {
        ActionContext actionContext = proxy.getInvocation().getInvocationContext();

        if (actionContext.getSession() == null)
            return null;

        return (PartakeSession) actionContext.getSession().get(Constants.ATTR_PARTAKE_SESSION);
    }

    /** log in した状態にする */
    protected void loginAs(ActionProxy proxy, String userId) throws DAOException, PartakeException {
        ActionContext actionContext = proxy.getInvocation().getInvocationContext();
        assert actionContext.getSession() != null;

        UserEx user = loadUserEx(userId);
        if (user == null)
            throw new RuntimeException("No such user.");
        actionContext.getSession().put(Constants.ATTR_USER, user);
    }

    /** logout する */
    protected void logout(ActionProxy proxy) throws DAOException {
        ActionContext actionContext = proxy.getInvocation().getInvocationContext();
        assert actionContext.getSession() != null;

        actionContext.getSession().remove(Constants.ATTR_USER);
    }

    protected void addParameter(ActionProxy proxy, String key, Object obj) {
        ActionContext actionContext = proxy.getInvocation().getInvocationContext();
        if (obj == null || obj instanceof String || obj instanceof String[])
            actionContext.getParameters().put(key, obj);
        else
            actionContext.getParameters().put(key, obj.toString());
    }

    protected void addValidSessionTokenToParameter(ActionProxy proxy) {
        ActionContext actionContext = proxy.getInvocation().getInvocationContext();
        assert actionContext.getSession() != null;

        PartakeSession session = (PartakeSession) actionContext.getSession().get(Constants.ATTR_PARTAKE_SESSION);
        actionContext.getParameters().put("sessionToken", session.getCSRFPrevention().getSessionToken());
    }

    protected void addInvalidSessionTokenToParameter(ActionProxy proxy) {
        ActionContext actionContext = proxy.getInvocation().getInvocationContext();
        actionContext.getParameters().put("sessionToken", "INVALID-SESSION-TOKEN");
    }

    // ----------------------------------------------------------------------

    protected void assertLoggedOut(ActionProxy proxy) {
        ActionContext actionContext = proxy.getInvocation().getInvocationContext();
        assert actionContext.getSession() != null;

        Assert.assertTrue(actionContext.getSession() == null || !actionContext.getSession().containsKey(Constants.ATTR_USER));
    }

    protected void assertRedirectedTo(String url) {
        Assert.assertEquals(url, response.getRedirectedUrl());
    }


    protected void assertResultLoginRequired(ActionProxy proxy) throws Exception {
        // Will be redirected to /auth/loginRequired
        // status code should be 401.
        // Assert.assertEquals(401, response.getStatus());
        Assert.assertTrue(response.getRedirectedUrl().startsWith("/loginRequired"));
    }

    protected void assertResultRedirect(ActionProxy proxy, String url) throws Exception {
        Assert.assertEquals(402, response.getStatus());
        if (url != null)
            Assert.assertEquals(url, response.getRedirectedUrl());
    }

    protected void assertResultForbidden(ActionProxy proxy) throws Exception {
        // status code should be 403
        // Assert.assertEquals(403, response.getStatus());
        Assert.assertTrue(response.getRedirectedUrl().startsWith("/prohibited"));
    }

    protected void assertResultNotFound(ActionProxy proxy) throws Exception {
        Assert.assertEquals(404, response.getStatus());
    }

    protected void assertResultError(ActionProxy proxy) throws Exception {
        // Assert.assertEquals(500, response.getStatus());
        Assert.assertTrue(response.getRedirectedUrl().startsWith("/error"));
    }

    protected void assertResultError(ActionProxy proxy, ServerErrorCode errorCode) throws Exception {
        assertResultError(proxy);

        AbstractPartakeController controller = (AbstractPartakeController) proxy.getAction();
        assertThat(controller.getRedirectURL(), is("/error?errorCode=" + errorCode.getErrorCode()));
    }

    // ----------------------------------------------------------------------
    // DB Accessors

    protected List<MessageEnvelope> loadMessageEnvelopes() throws DAOException, PartakeException {
        return new DBAccess<List<MessageEnvelope>>() {
            @Override
            protected List<MessageEnvelope> doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return DAOUtil.convertToList(daos.getMessageEnvelopeAccess().getIterator(con));
            }
        }.execute();
    }

    protected UserEx loadUserEx(final String userId) throws DAOException, PartakeException {
        return new DBAccess<UserEx>() {
            @Override
            protected UserEx doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return UserDAOFacade.getUserEx(con, daos, userId);
            }
        }.execute();
    }

    protected UserPreference loadUserPreference(final String userId) throws DAOException, PartakeException {
        return new DBAccess<UserPreference>() {
            @Override
            protected UserPreference doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getUserPreferenceAccess().find(con, userId);
            }
        }.execute();
    }

    protected Event loadEvent(final String eventId) throws DAOException, PartakeException {
        return new DBAccess<Event>() {
            @Override
            protected Event doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getEventAccess().find(con, eventId);
            }
        }.execute();
    }

    protected EventEx loadEventEx(final String eventId) throws DAOException, PartakeException {
        return new DBAccess<EventEx>() {
            @Override
            protected EventEx doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return EventDAOFacade.getEventEx(con, daos, eventId);
            }
        }.execute();
    }

    protected String storeEvent(final Event event) throws DAOException, PartakeException {
        return new Transaction<String>() {
            protected String doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException ,PartakeException {
                if (event.getId() == null) {
                    String eventId = daos.getEventAccess().getFreshId(con);
                    event.setId(eventId);
                }
                daos.getEventAccess().put(con, event);
                return event.getId();
            };
        }.execute();
    }

    protected List<EventTicket> loadEventTickets(final String eventId) throws DAOException, PartakeException {
        return new Transaction<List<EventTicket>>() {
            protected List<EventTicket> doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException ,PartakeException {
                return daos.getEventTicketAccess().findEventTicketsByEventId(con, eventId);
            };
        }.execute();
    }

    protected void storeEventTicket(final EventTicket ticket) throws DAOException, PartakeException {
        new Transaction<Void>() {
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException ,PartakeException {
                daos.getEventTicketAccess().put(con, ticket);
                return null;
            };
        }.execute();
    }

    protected List<UserOpenIDLink> loadOpenIDIdentifiers(final String userId) throws DAOException, PartakeException {
        return new DBAccess<List<UserOpenIDLink>>() {
            @Override
            protected List<UserOpenIDLink> doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getOpenIDLinkageAccess().findByUserId(con, userId);
            }
        }.execute();
    }

    protected MessageEnvelope loadEnvelope(final String id) throws DAOException, PartakeException {
        return new DBAccess<MessageEnvelope>() {
            @Override
            protected MessageEnvelope doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getMessageEnvelopeAccess().find(con, id);
            }
        }.execute();
    }


    protected List<MessageEnvelope> loadEnvelopes() throws DAOException, PartakeException {
        return new DBAccess<List<MessageEnvelope>>() {
            @Override
            protected List<MessageEnvelope> doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return DAOUtil.convertToList(daos.getMessageEnvelopeAccess().getIterator(con));
            }
        }.execute();
    }

    protected String loadCalendarIdFromUser(final String userId) throws DAOException, PartakeException {
        return new DBAccess<String>() {
            @Override
            protected String doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                UserCalendarLink linkage = daos.getCalendarAccess().findByUserId(con, userId);
                if (linkage == null)
                    return null;
                return linkage.getId();
            }
        }.execute();
    }

    protected UserTicket loadEnrollment(final String enrollmentId) throws DAOException, PartakeException {
        return new DBAccess<UserTicket>() {
            @Override
            protected UserTicket doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getEnrollmentAccess().find(con, enrollmentId);
            }
        }.execute();
    }

    protected UserTicket loadEnrollment(final String userId, final UUID ticketId) throws DAOException, PartakeException {
        return new DBAccess<UserTicket>() {
            @Override
            protected UserTicket doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getEnrollmentAccess().findByTicketIdAndUserId(con, ticketId, userId);
            }
        }.execute();
    }

    protected List<EventTicketNotification> loadEventTicketNotificationsByEventId(final UUID ticketId) throws Exception {
        return new DBAccess<List<EventTicketNotification>>() {
            @Override
            protected List<EventTicketNotification> doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getEventNotificationAccess().findByTicketId(con, ticketId, 0, Integer.MAX_VALUE);
            }
        }.execute();
    }

    protected String storeEnrollment(final UserTicket enrollment) throws DAOException, PartakeException {
        return new Transaction<String>() {
            @Override
            protected String doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                if (enrollment.getId() == null) {
                    String enrollmentId = daos.getEnrollmentAccess().getFreshId(con);
                    enrollment.setId(enrollmentId);
                }
                daos.getEnrollmentAccess().put(con, enrollment);
                return enrollment.getId();
            }
        }.execute();
    }

    protected UserImage loadImage(final String imageId) throws DAOException, PartakeException {
        return new DBAccess<UserImage>() {
            @Override
            protected UserImage doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getImageAccess().find(con, imageId);
            }
        }.execute();
    }

    protected UserThumbnail loadThumbnail(final String imageId) throws DAOException, PartakeException {
        return new DBAccess<UserThumbnail>() {
            @Override
            protected UserThumbnail doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getThumbnailAccess().find(con, imageId);
            }
        }.execute();
    }

    protected TwitterMessage loadTwitterMessage(final String twitterMessageId) throws DAOException, PartakeException {
        return new DBAccess<TwitterMessage>() {
            @Override
            protected TwitterMessage doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getTwitterMessageAccess().find(con, twitterMessageId);
            }
        }.execute();
    }

    protected UserNotification loadUserNotification(final String userNotificationId) throws DAOException, PartakeException {
        return new DBAccess<UserNotification>() {
            @Override
            protected UserNotification doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getUserNotificationAccess().find(con, userNotificationId);
            }
        }.execute();
    }

    protected List<UserNotification> loadUserNotificationsByUserId(final String userId) throws Exception {
        return new DBAccess<List<UserNotification>>() {
            @Override
            protected List<UserNotification> doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getUserNotificationAccess().findByUserId(con, userId, 0, Integer.MAX_VALUE);
            }
        }.execute();
    }

    protected UserReceivedMessage loadUserReceivedMessage(final UUID userReceivedMessageId) throws DAOException, PartakeException {
        return new DBAccess<UserReceivedMessage>() {
            @Override
            protected UserReceivedMessage doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                return daos.getUserReceivedMessageAccess().find(con, userReceivedMessageId);
            }
        }.execute();
    }

}
