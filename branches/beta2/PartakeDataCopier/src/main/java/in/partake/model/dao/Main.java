package in.partake.model.dao;

import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9ConnectionPool;
import in.partake.model.dao.postgres9.Postgres9DAOFactory;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventActivity;
import in.partake.model.dto.EventComment;
import in.partake.model.dto.EventFeed;
import in.partake.model.dto.EventMessage;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.EventTicketNotification;
import in.partake.model.dto.Message;
import in.partake.model.dto.User;
import in.partake.model.dto.UserCalendarLink;
import in.partake.model.dto.UserImage;
import in.partake.model.dto.UserOpenIDLink;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.UserTwitterLink;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.EnqueteQuestion;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.NotificationType;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.dto.auxiliary.TicketAmountType;
import in.partake.model.dto.auxiliary.TicketApplicationEnd;
import in.partake.model.dto.auxiliary.TicketApplicationStart;
import in.partake.model.dto.auxiliary.TicketPriceType;
import in.partake.model.dto.auxiliary.TicketReservationEnd;
import in.partake.resource.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.dbcp.BasicDataSource;

public class Main {
    private static Postgres9DAOFactory factory;
    private static Postgres9ConnectionPool pool;

    public static void main(String[] args) throws Exception {
        // PartakeApp.initialize("unittest");
        initializeDataSource();

        factory = new Postgres9DAOFactory();
        pool = new Postgres9ConnectionPool();
        doCopy();
    }

    private static void doCopy() throws Exception {
        // --- User
        copyUser();                     // User.java
        System.out.println("copyUser OK");
        copyUserPreference();           // UserPreference.java
        System.out.println("copyUserPreference OK");
        copyUserTwitterLink();          // TwitterLinkage.java
        System.out.println("copyUserTwitterLink OK");
        copyUserOpenIDLink();           // OpenIDLinkage.java
        System.out.println("copyUserOpenIDLink OK");
        copyUserCalendar();             // CalendarLinkage.java
        System.out.println("copyUserCalendar OK");
        copyUserImageData();            // BinaryData.java
        System.out.println("copyUserImageData OK");

        // --- Event
        copyEventActivity();            // EventActivity.java
        System.out.println("copyEventActivity OK");
        copyEventComment();             // Comment.java
        System.out.println("copyEventComment OK");
        copyEventFeed();                // EventFeedLinkage.java
        System.out.println("copyEventFeed OK");
        copyEventMessage();             // Message.java
        System.out.println("copyEventMessage OK");
        copyEvent();                    // Event.java, EventRelation.java, EventReminder.java
        System.out.println("copyEvent OK");

        // --- Ticket
        copyUserTicket();
        System.out.println("copyTicket OK");
    }

    private static void copyUser() throws Exception {
        Postgres9Connection pcon = getCon();
        Connection con = pcon.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT u.id, t.screenName, t.profileImageURL FROM Users as u LEFT OUTER JOIN TwitterLinkages as t ON u.id = t.userId");
        ResultSet rs = ps.executeQuery();

        Set<String> screenNames = new HashSet<String>();
        int i = 0;
        while (rs.next()) {
            String screenName = rs.getString(2);
            while (screenNames.contains(screenName))
                screenName = screenName + "_";

            // I don't know why, but there is a case that userId is not UUID.
            if (!Util.isUUID(rs.getString(1)))
                continue;

            System.out.println(++i + ":" + rs.getString(1));
            User t = new User(rs.getString(1), screenName, rs.getString(3),
                    TimeUtil.getCurrentDateTime(), TimeUtil.getCurrentDateTime());
            factory.getUserAccess().put(pcon, t);

            screenNames.add(screenName);
        }

        ps.close();
        con.close();
    }

    private static void copyUserPreference() throws Exception {
        Postgres9Connection pcon = getCon();
        Connection con = pcon.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM UserPreferences");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            UserPreference t = new UserPreference(
                    rs.getString("userId"), rs.getBoolean("profilePublic"), rs.getBoolean("receivingTwitterMessage"), rs.getBoolean("tweetingAttendanceAutomatically"));
            factory.getUserPreferenceAccess().put(pcon, t);
        }

        ps.close();
        pcon.close();
    }

    private static void copyUserOpenIDLink() throws Exception {
        Postgres9Connection pcon = getCon();
        Connection con = pcon.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM OpenIDLinkages");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            UserOpenIDLink t = new UserOpenIDLink(UUID.randomUUID(), rs.getString("userId"), rs.getString("id"));
            factory.getOpenIDLinkageAccess().put(pcon, t);
        }

        ps.close();
        pcon.close();
    }

    private static void copyUserTwitterLink() throws Exception {
        Postgres9Connection pcon = getCon();
        Connection con = pcon.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM TwitterLinkages");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            UserTwitterLink t = new UserTwitterLink(
                    UUID.randomUUID(),
                    Long.parseLong(rs.getString("twitterId")),
                    rs.getString("userId"),
                    rs.getString("screenName"),
                    rs.getString("name"),
                    rs.getString("accessToken"),
                    rs.getString("accessTokenSecret"),
                    rs.getString("profileImageURL"));
            factory.getTwitterLinkageAccess().put(pcon, t);
        }

        ps.close();
        pcon.close();
    }

    private static void copyUserCalendar() throws Exception {
        Postgres9Connection pcon = getCon();
        Connection con = pcon.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM CalendarLinkages");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            UserCalendarLink t = new UserCalendarLink(rs.getString("id"), rs.getString("userId"));
            factory.getCalendarAccess().put(pcon, t);
        }

        ps.close();
        pcon.close();
    }

    private static void copyUserImageData() throws Exception {
        Postgres9Connection pcon = getCon();
        Connection con = pcon.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM Events");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String userId = rs.getString("ownerId");
            String foreImageId = rs.getString("foreImageId");
            String backImageId = rs.getString("backImageId");
            Timestamp date = rs.getTimestamp("createdAt");

            if (foreImageId != null) {
                PreparedStatement ps2 = con.prepareStatement("SELECT * FROM BinaryData WHERE id = ?");
                ps2.setString(1, foreImageId);
                ResultSet rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    UserImage t = new UserImage(
                            rs2.getString("id"),
                            userId,
                            rs2.getString("type"),
                            rs2.getBytes("data"),
                            new DateTime(date.getTime()));
                    factory.getImageAccess().put(pcon, t);
                }
                rs2.close();
                ps2.close();
            }

            if (backImageId != null) {
                PreparedStatement ps2 = con.prepareStatement("SELECT * FROM BinaryData WHERE id = ?");
                ps2.setString(1, backImageId);
                ResultSet rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    UserImage t = new UserImage(
                            rs2.getString("id"),
                            userId,
                            rs2.getString("type"),
                            rs2.getBytes("data"),
                            new DateTime(date.getTime()));
                    factory.getImageAccess().put(pcon, t);
                }
                rs2.close();
                ps2.close();
            }
        }

        ps.close();
        pcon.close();
    }

    private static void copyEventActivity() throws Exception {
        Postgres9Connection pcon = getCon();
        Connection con = pcon.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM EventActivities");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            EventActivity t = new EventActivity(rs.getString("id"), eventId(rs), rs.getString("title"), rs.getString("content"), createdAt(rs));
            factory.getEventActivityAccess().put(pcon, t);
        }

        ps.close();
        pcon.close();
    }

    private static void copyEventComment() throws Exception {
        Postgres9Connection pcon = getCon();
        Connection con = pcon.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM Comments");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            EventComment t = new EventComment(rs.getString("id"), eventId(rs), rs.getString("userId"), rs.getString("comment"), rs.getBoolean("isHTML"), createdAt(rs));
            factory.getCommentAccess().put(pcon, t);
        }

        ps.close();
        pcon.close();
    }

    private static void copyEventFeed() throws Exception {
        Postgres9Connection pcon = getCon();
        Connection con = pcon.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM EventFeedLinkages");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            EventFeed t = new EventFeed(rs.getString("id"), eventId(rs));
            factory.getEventFeedAccess().put(pcon, t);
        }

        ps.close();
        pcon.close();
    }

    private static void copyEventMessage() throws Exception {
        Postgres9Connection pcon = getCon();
        Connection con = pcon.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM Messages");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            if (eventId(rs) == null)
                continue;
            UUID messageId = UUID.randomUUID();
            Message m = new Message(messageId, "", rs.getString("message"), createdAt(rs), (DateTime) null);
            factory.getMessageAccess().put(pcon, m);

            EventMessage t = new EventMessage(rs.getString("id"), eventId(rs), rs.getString("userId"), messageId.toString(), new DateTime(rs.getTimestamp("createdAt").getTime()), (DateTime) null);
            factory.getEventMessageAccess().put(pcon, t);
        }

        ps.close();
        pcon.close();
    }

    private static void copyEvent() throws Exception {
        Postgres9Connection pcon = getCon();
        Connection con = pcon.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM Events");
        ResultSet rs = ps.executeQuery();

        int i = 0;
        while (rs.next()) {
            System.out.println(++i);
            String eventId = eventId("id", rs);

            List<String> relatedEventIds = new ArrayList<String>();
            {
                PreparedStatement ps2 = con.prepareStatement("SELECT * FROM EventRelations WHERE srcEventId = ?");
                ps2.setString(1, eventId);
                ResultSet rs2 = ps2.executeQuery();
                while (rs2.next())
                    relatedEventIds.add(eventId("dstEventId", rs2));
                rs2.close();
                ps2.close();
            }
            List<String> editors = createEditors(rs.getString("managerScreenNames"));

            String foreImageId = rs.getString("foreImageId");
            if (foreImageId != null && !Util.isUUID(foreImageId))
                foreImageId = null;
            String backImageId = rs.getString("backImageId");
            if (backImageId != null && !Util.isUUID(backImageId))
                backImageId = null;

            Event event = new Event(
                    eventId,
                    rs.getString("title"),
                    rs.getString("summary"),
                    rs.getString("category"),
                    (rs.getTimestamp("beginDate") != null ? new DateTime(rs.getTimestamp("beginDate").getTime()) : null),
                    (rs.getTimestamp("endDate") != null && rs.getTimestamp("endDate").getTime() > 0 ? new DateTime(rs.getTimestamp("endDate").getTime()) : null),
                    rs.getString("url"),
                    rs.getString("place"),
                    rs.getString("address"),
                    rs.getString("description"),
                    rs.getString("hashTag"),
                    rs.getString("ownerId"),
                    foreImageId,
                    backImageId,
                    rs.getBoolean("isPrivate") ? rs.getString("passcode") : (String) null,
                    false, // draft
                    editors,
                    relatedEventIds,
                    (List<EnqueteQuestion>) null, // enquetes
                    createdAt(rs),
                    modifiedAt(rs),
                    rs.getInt("revision"));
            factory.getEventAccess().put(pcon, event);
        }

        ps.close();
        pcon.close();
    }

    private static void copyUserTicket() throws Exception {
        Postgres9Connection pcon = getCon();
        Connection con = pcon.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM Events");
        ResultSet rs = ps.executeQuery();

        int cnt = 0;
        while (rs.next()) {
            System.out.println(++cnt + ": UserTicket");
            List<UUID> ticketIds = new ArrayList<UUID>();

            String rawEventId = rs.getString("id");
            String eventId = eventId("id", rs);

            List<Enrollment> enrollments = new ArrayList<Enrollment>();
            {
                PreparedStatement ps2 = con.prepareStatement("SELECT * FROM Enrollments WHERE eventId = ?");
                ps2.setString(1, rawEventId);
                ResultSet rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    int statusVal = rs2.getInt("status");
                    ParticipationStatus status = ParticipationStatus.values()[statusVal];
                    int modificationStatusVal = rs2.getInt("modificationStatus");
                    ModificationStatus modificationStatus = ModificationStatus.values()[modificationStatusVal];
                    int attendanceStatusVal = rs2.getInt("attendanceStatus");
                    AttendanceStatus attendanceStatus = AttendanceStatus.values()[attendanceStatusVal];

                    Enrollment enrollment = new Enrollment(
                            rs2.getString("userId"),
                            eventId,
                            rs2.getString("comment"),
                            status,
                            rs2.getBoolean("vip"),
                            modificationStatus,
                            attendanceStatus,
                            rs2.getTimestamp("modifiedAt"));
                    enrollments.add(enrollment);
                }
                rs2.close();
                ps2.close();
            }

            List<String> priorityEventIds = new ArrayList<String>();
            {
                PreparedStatement ps2 = con.prepareStatement("SELECT * from EventRelations WHERE srcEventId = ? AND priority = true");
                ps2.setString(1, rawEventId);
                ResultSet rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    priorityEventIds.add(eventId("dstEventId", rs2));
                }
                ps2.close();
            }

            // For each enrollment, calculate priority.
            if (priorityEventIds.size() > 0) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i > priorityEventIds.size(); ++i) {
                    if (i > 0)
                        builder.append(", ");
                    builder.append(priorityEventIds.get(i));
                }

                for (Enrollment enrollment : enrollments) {
                    // If the user enrolled events which has priority, enrollment priority += 1.
                    if ("".equals(builder.toString()))
                        continue;

                    String sql = "SELECT * from Enrollment WHERE userId = ? AND eventId IN [" + builder.toString() + "]";
                    System.out.println(sql);
                    PreparedStatement ps2 = con.prepareStatement(sql);
                    ps2.setString(1, enrollment.getUserId());
                    ResultSet rs2 = ps2.executeQuery();
                    while (rs2.next()) {
                        enrollment.priority += 1;
                    }
                    rs2.close();
                    ps2.close();
                }
            }


            Comparator<Enrollment> comp = new Comparator<Enrollment>() {
                @Override
                public int compare(Enrollment lhs, Enrollment rhs) {
                    if (lhs == rhs) { return 0; }
                    if (lhs == null) { return -1; }
                    if (rhs == null) { return 1; }

                    if ( lhs.isVIP() && !rhs.isVIP()) { return -1; }
                    if (!lhs.isVIP() &&  rhs.isVIP()) { return  1; }

                    if (lhs.getPriority() > rhs.getPriority()) { return -1; }
                    if (lhs.getPriority() < rhs.getPriority()) { return 1; }
                    int x = lhs.getModifiedAt().compareTo(rhs.getModifiedAt());
                    if (x != 0) { return x; }
                    return lhs.getUserId().compareTo(rhs.getUserId());
                }
            };

            Collections.sort(enrollments, comp);
            int countVip = 0;
            int countPri = 0;
            for (Enrollment e: enrollments) {
                if (e.isVIP()) {
                    ++countVip;
                    continue;
                }
                if (e.priority > 0)
                    ++countPri;
            }

            final int capacity = rs.getInt("capacity");
            int remainingSeat = capacity;
            boolean isInfinity = remainingSeat == 0;

            if (countVip > 0) {
                // If there is a vip user, create a vip ticket here.
                UUID ticketId = UUID.randomUUID();
                ticketIds.add(ticketId);
                EventTicket ticket = new EventTicket(ticketId, eventId, 1, "VIP",
                        TicketApplicationStart.ANYTIME, 0, (DateTime) null,
                        rs.getTimestamp("deadline") != null && rs.getTimestamp("deadline").getTime() > 0 ? TicketApplicationEnd.TILL_CUSTOM_DAY : TicketApplicationEnd.TILL_TIME_BEFORE_EVENT,
                        0, rs.getTimestamp("deadline") != null && rs.getTimestamp("deadline").getTime() > 0 ? new DateTime(rs.getTimestamp("deadline").getTime()) : null,
                        TicketReservationEnd.TILL_NHOUR_BEFORE, 3, (DateTime) null,
                        TicketPriceType.FREE, 0, TicketAmountType.LIMITED, countVip, createdAt(rs), modifiedAt(rs));
                factory.getEventTicketAccess().put(pcon, ticket);

                // And creates a user ticket.
                for (Enrollment e : enrollments) {
                    if (!e.isVIP())
                        continue;

                    UserTicket userTicket = new UserTicket(
                            UUID.randomUUID().toString(),
                            e.getUserId(), ticketId, eventId,
                            e.getComment(), e.getStatus(),
                            e.getModificationStatus(), e.getAttendanceStatus(),
                            (Map<UUID, List<String>>) null,
                            new DateTime(e.getModifiedAt().getTime()),
                            new DateTime(e.getModifiedAt().getTime()),
                            new DateTime(e.getModifiedAt().getTime()));
                    factory.getEnrollmentAccess().put(pcon, userTicket);
                }
                remainingSeat -= countVip;
            }

            if (countPri > 0) {
                UUID ticketId = UUID.randomUUID();
                ticketIds.add(ticketId);
                EventTicket ticket = new EventTicket(ticketId, eventId, 2, "優先チケット",
                        TicketApplicationStart.ANYTIME, 0, (DateTime) null,
                        rs.getTimestamp("deadline") != null && rs.getTimestamp("deadline").getTime() > 0 ? TicketApplicationEnd.TILL_CUSTOM_DAY : TicketApplicationEnd.TILL_TIME_BEFORE_EVENT,
                        0, rs.getTimestamp("deadline") != null && rs.getTimestamp("deadline").getTime() > 0 ? new DateTime(rs.getTimestamp("deadline").getTime()) : null,
                                TicketReservationEnd.TILL_NHOUR_BEFORE, 3, (DateTime) null,
                                TicketPriceType.FREE, 0, TicketAmountType.LIMITED, countPri, createdAt(rs), modifiedAt(rs));
                factory.getEventTicketAccess().put(pcon, ticket);

                // And creates a user ticket.
                for (Enrollment e : enrollments) {
                    if (e.isVIP() || e.priority <= 0)
                        continue;

                    UserTicket userTicket = new UserTicket(
                            UUID.randomUUID().toString(),
                            e.getUserId(), ticketId, eventId,
                            e.getComment(), e.getStatus(),
                            e.getModificationStatus(), e.getAttendanceStatus(),
                            (Map<UUID, List<String>>) null,
                            new DateTime(e.getModifiedAt().getTime()),
                            new DateTime(e.getModifiedAt().getTime()),
                            new DateTime(e.getModifiedAt().getTime()));
                    factory.getEnrollmentAccess().put(pcon, userTicket);
                }
                remainingSeat -= countPri;
            }

            // Normal Ticket
            {
                UUID ticketId = UUID.randomUUID();
                ticketIds.add(ticketId);
                EventTicket ticket = new EventTicket(ticketId, eventId, 3, "チケット",
                        TicketApplicationStart.ANYTIME, 0, (DateTime) null,
                        rs.getTimestamp("deadline") != null && rs.getTimestamp("deadline").getTime() > 0 ? TicketApplicationEnd.TILL_CUSTOM_DAY : TicketApplicationEnd.TILL_TIME_BEFORE_EVENT,
                        0,
                        rs.getTimestamp("deadline") != null && rs.getTimestamp("deadline").getTime() > 0 ? new DateTime(rs.getTimestamp("deadline").getTime()) : null,
                                TicketReservationEnd.TILL_NHOUR_BEFORE, 3, (DateTime) null,
                                TicketPriceType.FREE, 0, isInfinity ? TicketAmountType.UNLIMITED : TicketAmountType.LIMITED, Math.max(0, capacity - countVip - countPri), createdAt(rs), modifiedAt(rs));
                factory.getEventTicketAccess().put(pcon, ticket);

                // And creates a user ticket.
                for (Enrollment e : enrollments) {
                    if (e.isVIP() || e.priority > 0)
                        continue;

                    // The other people's ticket.
                    UserTicket userTicket = new UserTicket(
                            UUID.randomUUID().toString(),
                            e.getUserId(), ticketId, eventId,
                            e.getComment(), e.getStatus(),
                            e.getModificationStatus(), e.getAttendanceStatus(),
                            (Map<UUID, List<String>>) null,
                            new DateTime(e.getModifiedAt().getTime()),
                            new DateTime(e.getModifiedAt().getTime()),
                            new DateTime(e.getModifiedAt().getTime()));
                    factory.getEnrollmentAccess().put(pcon, userTicket);
                }
            }

            // Copy EventReminder
            {
                PreparedStatement ps2 = con.prepareStatement("SELECT * from EventReminders WHERE eventId = ?");
                ps2.setString(1, rawEventId);
                ResultSet rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    Timestamp sentDateOfbeforeDeadlineHalfDay = rs2.getTimestamp("sentdateofbeforedeadlinehalfday");
                    if (sentDateOfbeforeDeadlineHalfDay != null) {
                        for (UUID ticketId : ticketIds) {
                            EventTicketNotification t = new EventTicketNotification(
                                    UUID.randomUUID().toString(),
                                    ticketId,
                                    eventId,
                                    (List<String>) (new ArrayList<String>()),
                                    NotificationType.HALF_DAY_BEFORE_REMINDER_FOR_RESERVATION,
                                    new DateTime(sentDateOfbeforeDeadlineHalfDay.getTime()));
                            factory.getEventNotificationAccess().put(pcon, t);
                        }
                    }

                    Timestamp sentdateofbeforedeadlineoneday = rs2.getTimestamp("sentdateofbeforedeadlineoneday");
                    if (sentdateofbeforedeadlineoneday != null) {
                        for (UUID ticketId : ticketIds) {
                            EventTicketNotification t = new EventTicketNotification(
                                    UUID.randomUUID().toString(),
                                    ticketId,
                                    eventId,
                                    (List<String>) (new ArrayList<String>()),
                                    NotificationType.ONE_DAY_BEFORE_REMINDER_FOR_RESERVATION,
                                    new DateTime(sentdateofbeforedeadlineoneday.getTime()));
                            factory.getEventNotificationAccess().put(pcon, t);
                        }
                    }


                    Timestamp sentdateofbeforetheday = rs2.getTimestamp("sentdateofbeforetheday");
                    if (sentdateofbeforetheday != null) {
                        for (UUID ticketId : ticketIds) {
                            EventTicketNotification t = new EventTicketNotification(
                                    UUID.randomUUID().toString(),
                                    ticketId,
                                    eventId,
                                    (List<String>) (new ArrayList<String>()),
                                    NotificationType.EVENT_ONEDAY_BEFORE_REMINDER,
                                    new DateTime(sentdateofbeforetheday.getTime()));
                            factory.getEventNotificationAccess().put(pcon, t);
                        }
                    }

                }
                rs2.close();
                ps2.close();
            }
        }

        ps.close();
        pcon.close();
    }

    private static Postgres9Connection getCon() throws Exception {
        return (Postgres9Connection) pool.getConnection();
    }

    private static String eventId(ResultSet rs) throws Exception {
        return eventId("eventId", rs);
    }

    private static String eventId(String key, ResultSet rs) throws Exception {
        String s = rs.getString(key);
        if ("demo".equals(s))
            return Constants.DEMO_ID.toString();
        else
            return s;
    }

    private static DateTime createdAt(ResultSet rs) throws Exception {
        Timestamp t = rs.getTimestamp("createdAt");
        if (t != null)
            return new DateTime(t.getTime());
        else
            return null;
    }

    private static DateTime modifiedAt(ResultSet rs) throws Exception {
        Timestamp t = rs.getTimestamp("modifiedAt");
        if (t != null)
            return new DateTime(t.getTime());
        else
            return null;
    }

    private static List<String> createEditors(String names) throws Exception {
        List<String> result = new ArrayList<String>();
        if (names == null)
            return result;

        String[] screenNames = names.split(",");
        for (String screenName : screenNames) {
            String n = screenName.trim();

            Postgres9Connection pcon = getCon();
            Connection con = pcon.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM TwitterLinkages where screenName = ?");
            ps.setString(1, n);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.add(rs.getString("userId"));
            }

            rs.close();
            ps.close();
            pcon.close();
        }

        return result;
    }

    private static void initializeDataSource() throws NamingException {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");

        InitialContext ic = new InitialContext();
        ic.createSubcontext("java:");
        ic.createSubcontext("java:/comp");
        ic.createSubcontext("java:/comp/env");
        ic.createSubcontext("java:/comp/env/jdbc");

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql:partake");
        ds.setUsername("partake");
        ds.setPassword("partake");

        ic.bind("java:/comp/env/jdbc/postgres", ds);
    }
}

class Enrollment {
    private String userId;
    private String eventId;
    private String comment;
    private boolean vip;
    private ParticipationStatus status;
    private ModificationStatus modificationStatus;
    private AttendanceStatus attendanceStatus;
    private Date modifiedAt;

    int priority;

    // ----------------------------------------------------------------------
    // constructors

    public Enrollment(String userId, String eventId, String comment,
            ParticipationStatus status, boolean vip, ModificationStatus modificationStatus,
            AttendanceStatus attendanceStatus, Date modifiedAt) {
        this.userId = userId;
        this.eventId = eventId;
        this.comment = comment;
        this.status = status;
        this.vip = vip;
        this.modificationStatus = modificationStatus;
        this.attendanceStatus = attendanceStatus;
        this.modifiedAt = modifiedAt;
        this.priority = 0;
    }

    // ----------------------------------------------------------------------
    //

    public String getUserId() {
        return userId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getComment() {
        return comment;
    }

    public ParticipationStatus getStatus() {
        return status;
    }

    /**
     * 前回チェック時のステータス。ここは、 ENROLLED, NOT_ENROLLED のいずれかでなければならない。
     * 変更時に、この値が ENROLLED -> NOT_ENROLLED もしくあｈ NOT_ENROLLED -> ENROLLED になっていれば、
     * DM によって通知を出す。
     * @return
     */
    public ModificationStatus getModificationStatus() {
        return modificationStatus;
    }

    public AttendanceStatus getAttendanceStatus() {
        return attendanceStatus;
    }

    public boolean isVIP() {
        return vip;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public int getPriority() {
        return priority;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setVIP(boolean vip) {
        this.vip = vip;
    }

    public void setStatus(ParticipationStatus status) {
        this.status = status;
    }

    public void setModificationStatus(ModificationStatus lastStatus) {
        this.modificationStatus = lastStatus;
    }

    public void setAttendanceStatus(AttendanceStatus attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
