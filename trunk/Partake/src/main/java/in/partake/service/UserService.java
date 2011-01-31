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
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.auxiliary.ParticipationStatus;
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
    
    public User getUserById(String userId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
            User user = factory.getUserAccess().getUserById(con, userId); 
            if (user == null) { return null; }
            
            // TODO: そのうち、user.getCalendarId() を廃止する予定。
            // とりあえずそれまでは user に書いてある calendarId より、こちらに書いてある calendarId を優先しておく。
            CalendarLinkage linkage = factory.getCalendarAccess().getCalendarLinkageByUserId(con, userId);
            if (linkage != null) {
                user = new User(user);
                user.setCalendarId(user.getCalendarId());
                user.freeze();
            }
            return user;
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
            // 1. まず TwitterLinkage を作成 / アップデート
            TwitterLinkage twitterLinkage = updateTwitterLinkage(con, factory, twitterLinkageEmbryo, twitter); 

            // 2. 対応するユーザーを生成
            UserEx user = getUserFromTwitterLinkage(con, factory, twitterLinkage, twitter, true);
            
            // 3. lastlogin の update
            factory.getUserAccess().updateLastLogin(con, user);
            
            return user;
        } finally {
            con.invalidate();
        }        
    }
    
    private UserEx getUserFromTwitterLinkage(PartakeConnection con, PartakeDAOFactory factory, TwitterLinkage twitterLinkage, Twitter twitter, boolean createsIfAbsent) throws DAOException, TwitterException {
        String userId = twitterLinkage.getUserId();
        UserEx user = null;
        if (userId == null) {
            userId = factory.getUserAccess().getFreshUserId(con);
        } else {
            user = getUserEx(con, userId); 
        }
        
        if (user == null && createsIfAbsent) {
            factory.getUserAccess().addUser(con, userId, twitter.getId());
            user = getUserEx(con, userId);
        }
        
        return user;
    }
    
    private TwitterLinkage updateTwitterLinkage(PartakeConnection con, PartakeDAOFactory factory, TwitterLinkage twitterLinkageEmbryo, Twitter twitter) throws DAOException, TwitterException {
        TwitterLinkage twitterLinkage = factory.getTwitterLinkageAccess().getTwitterLinkageById(con, twitter.getId()); 
        
        if (twitterLinkage == null || twitterLinkage.getUserId() == null) {
            String userId = factory.getUserAccess().getFreshUserId(con); 
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
            String userId = factory.getOpenIDLinkageAccess().getUserId(con, identifier); 
            if (userId == null) { return null; }
            
            return getUserEx(con, userId);
        } finally {
            con.invalidate();
        }     
    }

    public void addOpenIDLinkage(String userId, String identifier) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        try {
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
            DataIterator<String> it = factory.getOpenIDLinkageAccess().getOpenIDIdentifiers(con, userId);
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
            factory.getOpenIDLinkageAccess().removeOpenID(con, identifier);
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
            CalendarLinkage calendarLinkage = factory.getCalendarAccess().getCalendarLinkage(con, calendarId); 
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
            factory.getUserPreferenceAccess().setPreference(con,  embryo);
        } finally {
            con.invalidate();
        }
    }

}