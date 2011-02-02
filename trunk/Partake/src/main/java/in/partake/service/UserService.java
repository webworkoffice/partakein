package in.partake.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.CalendarLinkage;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Event;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.util.PDate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;

/**
 * User に関連するもの。
 * 
 * 基本的に～～Serviceの関数はそれ１つで transaction が仮想的には確保されていなければならない。
 * 
 * @author shinyak
 *
 */
public final class UserService extends PartakeService {
    private static UserService instance = new UserService();
    
    public static UserService get() {
        return instance;
    }
    
    private UserService() {
        // do nothing for now.
    }

    // ----------------------------------------------------------------------
    // User
    
    public UserEx getUserExById(String userId) throws DAOException {
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
    
    @Deprecated
    public UserEx getUserExByUser(User user) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction(); 
            TwitterLinkage linkage = factory.getTwitterLinkageAccess().getTwitterLinkageById(con, user.getTwitterId());
            con.commit();
            
            return new UserEx(user, linkage);
        } finally {
            con.invalidate();            
        }
    }
    
    // ----------------------------------------------------------------------
    // Authentication
    
    public UserEx loginUserByTwitter(Twitter twitter, AccessToken accessToken) throws DAOException, TwitterException {
        twitter4j.User twitterUser = twitter.showUser(twitter.getId()); 
        TwitterLinkage twitterLinkageEmbryo = new TwitterLinkage(
                twitter.getId(), twitter.getScreenName(), twitterUser.getName(), accessToken.getToken(), accessToken.getTokenSecret(),
                twitter.showUser(twitter.getId()).getProfileImageURL().toString(), null
        );

        // Twitter Linkage から User を引いてくる。
        // 対応する user がいない場合は、user を作成して Twitter Linkage を付与する
        
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction(); 
            
            // 1. まず TwitterLinkage を作成 / アップデート
            TwitterLinkage twitterLinkage = updateTwitterLinkage(con, factory, twitterLinkageEmbryo, twitter); 

            // 2. 対応するユーザーを生成
            UserEx user = getUserFromTwitterLinkage(con, factory, twitterLinkage, twitter, true);
            
            // 3. lastlogin の update
            factory.getUserAccess().updateLastLogin(con, user.getId(), PDate.getCurrentDate().getDate());
            
            con.commit();
            return user;
        } finally {
            con.invalidate();
        }        
    }
    
    private UserEx getUserFromTwitterLinkage(PartakeConnection con, PartakeDAOFactory factory, TwitterLinkage twitterLinkage, Twitter twitter, boolean createsIfAbsent) throws DAOException, TwitterException {
        String userId = twitterLinkage.getUserId();
        UserEx user = null;
        if (userId == null) {
            userId = factory.getUserAccess().getFreshId(con);
        } else {
            user = getUserEx(con, userId); 
        }
        
        if (user == null && createsIfAbsent) {
            factory.getUserAccess().addUser(con, new User(userId, twitter.getId(), new Date(), null));
            user = getUserEx(con, userId);
        }
        
        return user;
    }
    
    private TwitterLinkage updateTwitterLinkage(PartakeConnection con, PartakeDAOFactory factory, TwitterLinkage twitterLinkageEmbryo, Twitter twitter) throws DAOException, TwitterException {
        TwitterLinkage twitterLinkage = factory.getTwitterLinkageAccess().getTwitterLinkageById(con, twitter.getId()); 
        
        if (twitterLinkage == null || twitterLinkage.getUserId() == null) {
            String userId = factory.getUserAccess().getFreshId(con); 
            twitterLinkageEmbryo.setUserId(userId);
        } else {
            twitterLinkageEmbryo.setUserId(twitterLinkage.getUserId());
        }
        
        factory.getTwitterLinkageAccess().addTwitterLinkage(con, twitterLinkageEmbryo);
        return factory.getTwitterLinkageAccess().getTwitterLinkageById(con, twitter.getId());
    }
    
    // ----------------------------------------------------------------------
    // OpenID Authentication
    
    public UserEx loginByOpenID(String identifier) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction(); 
            String userId = factory.getOpenIDLinkageAccess().getUserId(con, identifier); 
            if (userId == null) { return null; }
            
            UserEx user = getUserEx(con, userId);
            con.commit();
            return user;
        } finally {
            con.invalidate();
        }     
    }

    public void addOpenIDLinkage(String userId, String identifier) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction(); 
            factory.getOpenIDLinkageAccess().addOpenID(con, identifier, userId);
            con.commit();
        } finally {
            con.invalidate();
        }        
    }
    
    public List<String> getOpenIDIdentifiers(String userId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction(); 
            List<String> result = new ArrayList<String>();
            DataIterator<String> it = factory.getOpenIDLinkageAccess().getOpenIDIdentifiers(con, userId);
            while (it.hasNext()) {
                String s = it.next();
                if (s == null) { continue; }
                result.add(s);
            }
            con.commit();
            
            return result;
        } finally {
            con.invalidate();
        }
    }
    
    public void removeOpenIDLinkage(String userId, String identifier) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction(); 
            factory.getOpenIDLinkageAccess().removeOpenID(con, identifier);
            con.commit();
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
            List<Enrollment> enrollments = factory.getEnrollmentAccess().getEnrollmentsByUserId(con, userId);
            List<Event> events = new ArrayList<Event>();
            for (Enrollment e : enrollments) {
                if (e.getStatus().isEnrolled()) {
                    Event event = factory.getEventAccess().getEvent(con, e.getEventId());
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
            Enrollment enrollment = factory.getEnrollmentAccess().getEnrollment(con, userId, eventId);
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
            CalendarLinkage calendarLinkage = factory.getCalendarAccess().getCalendarLinkage(con, calendarId);
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
            UserPreference pref = factory.getUserPreferenceAccess().getPreference(con, userId);
            con.commit();
            
            if (pref == null) {
                pref = UserPreference.getDefaultPreference(userId);
            }
            return pref;
        } finally {
            con.invalidate();
        }
    }
    
    public void setUserPreference(UserPreference embryo) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            factory.getUserPreferenceAccess().setPreference(con,  embryo);
            con.commit();
        } finally {
            con.invalidate();
        }
    }

}