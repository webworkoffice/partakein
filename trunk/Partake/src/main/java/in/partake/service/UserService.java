package in.partake.service;

import java.util.ArrayList;
import java.util.List;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.CalendarLinkage;
import in.partake.model.dto.Event;
import in.partake.model.dto.ParticipationStatus;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.model.dto.UserPreference;
import twitter4j.Twitter;
import twitter4j.TwitterException;

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
    
    public User getUserById(String userId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            return factory.getUserAccess().getUserById(con, userId);
        } finally {
            con.invalidate();            
        }
    }
    
    public UserEx getUserExById(String userId) throws DAOException {
        PartakeConnection con = getPool().getConnection();
        try {
            return getUserEx(con, userId);
        } finally {
            con.invalidate();            
        }
    }
    
    @Deprecated
    public UserEx getUserExByUser(User user) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            TwitterLinkage linkage = factory.getTwitterLinkageAccess().getTwitterLinkageById(con, user.getTwitterId());
            return new UserEx(user, linkage);
        } finally {
            con.invalidate();            
        }
    }
    
    @Deprecated
    public UserEx getPartakeUserByUser(User user) throws DAOException {
        return getUserExByUser(user);
    }
    
    // ----------------------------------------------------------------------
    // Authentication
    
    public void updateLastLogin(User user) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            factory.getUserAccess().updateLastLogin(con, user);
        } finally {
            con.invalidate();
        }
    }
    
    // ----------------------------------------------------------------------
    // Twitter Authentication
    
    public User getUserFromTwitterLinkage(TwitterLinkage twitterLinkage, Twitter twitter, boolean createsIfAbsent) throws DAOException, TwitterException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            String userId = twitterLinkage.getUserId();
            User user = null;
            if (userId == null) {
                userId = factory.getUserAccess().getFreshUserId(con);
            } else {
                user = factory.getUserAccess().getUserById(con, userId);
            }
            
            if (user == null && createsIfAbsent) {
                factory.getUserAccess().addUser(con, userId, twitter.getId());
                user = factory.getUserAccess().getUserById(con, userId);
            }
            
            return user;
        } finally {
            con.invalidate();
        }
    }
    
    public TwitterLinkage updateTwitterLinkage(TwitterLinkage twitterLinkageEmbryo, Twitter twitter) throws DAOException, TwitterException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            TwitterLinkage twitterLinkage = factory.getTwitterLinkageAccess().getTwitterLinkageById(con, twitter.getId()); 
            
            if (twitterLinkage == null || twitterLinkage.getUserId() == null) {
                String userId = factory.getUserAccess().getFreshUserId(con); 
                twitterLinkageEmbryo.setUserId(userId);
            } else {
                twitterLinkageEmbryo.setUserId(twitterLinkage.getUserId());
            }
            
            factory.getTwitterLinkageAccess().addTwitterLinkage(con, twitterLinkageEmbryo);
            return factory.getTwitterLinkageAccess().getTwitterLinkageById(con, twitter.getId());
        } finally {
            con.invalidate();
        }
    }
    
    // ----------------------------------------------------------------------
    // OpenID Authentication

    public User getUserFromOpenIDLinkage(String identifier) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            String userId = factory.getOpenIDLinkageAccess().getUserId(con, identifier); 
            if (userId == null) { return null; }
            
            User user = factory.getUserAccess().getUserById(con, userId);
            return user;
        } finally {
            con.invalidate();
        }     
    }

    public void addOpenIDLinkage(String userId, String identifier) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            factory.getUserAccess().addOpenID(con, userId, identifier);
            factory.getOpenIDLinkageAccess().addOpenID(con, identifier, userId);
        } finally {
            con.invalidate();
        }        
    }
    
    public List<String> getOpenIDIdentifiers(String userId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            List<String> result = new ArrayList<String>();
            DataIterator<String> it = factory.getUserAccess().getOpenIDIdentifiers(con, userId);
            while (it.hasNext()) {
                String s = it.next();
                if (s == null) { continue; }
                result.add(s);
            }
            return result;
        } finally {
            con.invalidate();
        }
    }
    
    public void removeOpenIDLinkage(String userId, String identifier) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            factory.getUserAccess().removeOpenID(con, userId, identifier);
            factory.getOpenIDLinkageAccess().removeOpenID(con, identifier, userId);
        } finally {            
            con.invalidate();
        }         
    }
    
    // ----------------------------------------------------------------------
    // Event Participation
    
    // TODO: should this be in UserService or EventService? Hmmm... I think this is suitable to EventService.
    public List<Event> getEnrolledEvents(User user) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            return convertToList(factory.getEnrollmentAccess().getEnrolledEvents(con, user.getId()));
        } finally {
            con.invalidate();
        }
    }
    
    
    
    public ParticipationStatus getParticipationStatus(User user, Event event) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            return factory.getEnrollmentAccess().getParticipationStatus(con, event, user);
        } finally {
            con.invalidate();
        }
    }

    // ----------------------------------------------------------------------
    // Calendar
    
    public User getUserFromCalendarId(String calendarId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            CalendarLinkage calendarLinkage = factory.getCalendarAccess().getCalendarLinkageById(con, calendarId); 
            if (calendarLinkage == null) { return null; }
            
            String userId = calendarLinkage.getUserId();
            if (userId == null) { return null; }
            
            User user = factory.getUserAccess().getUserById(con, userId);
            if (user == null) { return null; }
            
            return user;
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
            UserPreference pref = factory.getUserPreferenceAccess().getPreference(con, userId);
            if (pref == null) {
                pref = UserPreference.getDefaultPreference();
            }
            return pref;
        } finally {
            con.invalidate();
        }
    }
    
    public void setUserPreference(String userId, UserPreference embryo) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            factory.getUserPreferenceAccess().setPreference(con, userId, embryo);
        } finally {
            con.invalidate();
        }
    }

}