package in.partake.model.dao;

import in.partake.model.dto.User;

import java.util.List;

public interface IUserAccess extends ITruncatable {

    // fresh な user id を１つ作成して返す。
    public abstract String getFreshUserId(PartakeConnection con) throws DAOException;

    /**
     * add a new user from UserEmbryo.
     */
    public abstract void addUser(PartakeConnection con, String userId, int twitterId) throws DAOException;

    /**
     * get user by specifying id.
     * @param id
     * @return
     * @throws DAOException
     */
    public abstract User getUserById(PartakeConnection con, String id) throws DAOException;

    public abstract void updateLastLogin(PartakeConnection con, User user) throws DAOException;

    /**
     * get users by specifying ids.
     * @param ids
     * @return
     * @throws DAOException
     */
    public abstract List<User> getUsersByIds(PartakeConnection con, List<String> ids) throws DAOException;


    
}