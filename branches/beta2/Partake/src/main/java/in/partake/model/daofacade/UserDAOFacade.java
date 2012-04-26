package in.partake.model.daofacade;

import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserCalendarLinkageAccess;
import in.partake.model.dao.access.IUserTwitterLinkAccess;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dto.UserCalendarLink;
import in.partake.model.dto.UserTwitterLink;
import in.partake.model.dto.User;
import in.partake.model.dto.UserPreference;

public class UserDAOFacade extends AbstractPartakeDAOFacade {

    public static UserPreference getPreference(PartakeConnection con, IPartakeDAOs daos, String userId) throws DAOException {
        UserPreference pref = daos.getUserPreferenceAccess().find(con, userId);
        if (pref == null)
            pref = UserPreference.getDefaultPreference(userId);
        return pref;
    }

    public static UserEx getUserEx(PartakeConnection con, IPartakeDAOs daos, String userId) throws DAOException {
        IUserAccess userAccess = daos.getUserAccess();
        IUserTwitterLinkAccess twitterDAO = daos.getTwitterLinkageAccess();
        IUserCalendarLinkageAccess calendarDAO = daos.getCalendarAccess();

        User user = userAccess.find(con, userId);
        if (user == null) { return null; }

        // TODO: そのうち、user.getCalendarId() を廃止する予定。
        // とりあえずそれまでは user に書いてある calendarId より、こちらに書いてある calendarId を優先しておく。
        {
            UserCalendarLink linkage = calendarDAO.findByUserId(con, userId);
            if (linkage != null) {
                User newUser = new User(user);
                newUser.setCalendarId(linkage.getId());
                newUser.freeze();
                user = newUser;
            }
        }

        UserTwitterLink linkage = twitterDAO.find(con, String.valueOf(user.getTwitterId()));
        return new UserEx(user, linkage);
    }

}
