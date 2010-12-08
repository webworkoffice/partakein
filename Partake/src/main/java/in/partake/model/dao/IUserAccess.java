package in.partake.model.dao;

import in.partake.model.dto.User;

import java.util.List;

public interface IUserAccess {

    // fresh な user id を１つ作成して返す。
    // TODO: これ public じゃなくて UserService に任せるべきじゃないかね？
    public abstract String getFreshUserId(PartakeConnection con) throws DAOException;

    /**
     * add a new user from UserEmbryo.
     */
    public abstract void addUser(PartakeConnection con, String usreId, int twitterId) throws DAOException;

    /**
     * get user by specifying id.
     * @param id
     * @return
     * @throws DAOException
     */
    public abstract User getUserById(PartakeConnection con, String id) throws DAOException;

    public abstract void updateLastLogin(PartakeConnection con, User user) throws DAOException;

    public abstract void updateCalendarId(PartakeConnection con, User user, String calendarId) throws DAOException;

    /**
     * get users by specifying ids.
     * @param ids
     * @return
     * @throws DAOException
     */
    public abstract List<User> getUsersByIds(PartakeConnection con, List<String> ids) throws DAOException;

    public abstract void addOpenID(PartakeConnection con, String userId, String identity) throws DAOException;

    public abstract DataIterator<String> getOpenIDIdentifiers(PartakeDAOFactory factory, String userId) throws DAOException;

}