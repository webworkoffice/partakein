package in.partake.model.daofacade.deprecated;

import in.partake.base.TimeUtil;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.CalendarLinkage;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Event;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.dto.pk.EnrollmentPK;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User に関連するもの。
 *
 * 基本的に～～Serviceの関数はそれ１つで transaction が仮想的には確保されていなければならない。
 *
 * @author shinyak
 *
 */
public final class DeprecatedUserDAOFacade extends DeprecatedPartakeDAOFacade {
    private static DeprecatedUserDAOFacade instance = new DeprecatedUserDAOFacade();
    // private static Logger logger = Logger.getLogger(UserService.class);

    public static DeprecatedUserDAOFacade get() {
        return instance;
    }

    private DeprecatedUserDAOFacade() {
        // do nothing for now.
    }

    // ----------------------------------------------------------------------
    // User

    public UserEx getUserExById(String userId) throws DAOException {
        if (userId == null) {
            throw new NullPointerException();
        }
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            UserEx user = getUserEx(con, userId);
            con.commit();
            return user;
        } finally {
            con.invalidate();
        }
    }

    // ----------------------------------------------------------------------
    // Event Participation

    // TODO: should this be in UserService or EventService? Hmmm... I think this is suitable to EventService.
    public List<Event> getEnrolledEvents(String userId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            List<Enrollment> enrollments = factory.getEnrollmentAccess().findByUserId(con, userId);
            List<Event> events = new ArrayList<Event>();
            for (Enrollment e : enrollments) {
                if (e == null) { continue; }
                if (e.getStatus().isEnrolled()) {
                    Event event = factory.getEventAccess().find(con, e.getEventId());
                    if (event == null) { continue; }
                    events.add(event);
                }
            }
            con.commit();

            return events;
        } finally {
            con.invalidate();
        }
    }

    public ParticipationStatus getParticipationStatus(String userId, String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            Enrollment enrollment = factory.getEnrollmentAccess().find(con, new EnrollmentPK(userId, eventId));
            con.commit();

            if (enrollment == null) { return ParticipationStatus.NOT_ENROLLED; }
            return enrollment.getStatus();

        } finally {
            con.invalidate();
        }
    }

    // ----------------------------------------------------------------------
    // Calendar

    public UserEx getUserFromCalendarId(String calendarId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            CalendarLinkage calendarLinkage = factory.getCalendarAccess().find(con, calendarId);
            con.commit();

            if (calendarLinkage == null) { return null; }

            String userId = calendarLinkage.getUserId();
            if (userId == null) { return null; }

            return getUserEx(con, userId);
        } finally {
            con.invalidate();
        }
    }

    // ----------------------------------------------------------------------
    // User Preference

    public UserPreference getUserPreference(String userId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            UserPreference pref = factory.getUserPreferenceAccess().find(con, userId);
            con.commit();

            if (pref == null) {
                pref = UserPreference.getDefaultPreference(userId);
            }
            return pref;
        } finally {
            con.invalidate();
        }
    }
    
    // ----------------------------------------------------------------------

    /**
     * 
     * @author skypencil (@eller86)
     * @return count of users
     * @throws DAOException
     */
    // 
    public UserCount countUsers() throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        Date oneMonthAgo = new Date(TimeUtil.getCurrentTime() - 30L * 24 * 60 * 60 * 1000);
        UserCount count = new UserCount();

        try {
            con.beginTransaction();
            
            count.user = factory.getUserAccess().count(con);
            count.activeUser = factory.getUserAccess().countActiveUsers(con, oneMonthAgo);
            
            con.commit();
        } finally {
            con.invalidate();
        }

        return count;
    }

    public static final class UserCount {
        /** count of all users. */
        public int user;
        /** count of users who sign in the last 30 days. */
        public int activeUser;
    }
}