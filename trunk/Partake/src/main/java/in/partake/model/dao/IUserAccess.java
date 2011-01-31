package in.partake.model.dao;

import java.util.Date;

import in.partake.model.dto.User;

public interface IUserAccess extends ITruncatable {

    // fresh な user id を１つ作成して返す。
    public abstract String getFreshId(PartakeConnection con) throws DAOException;

    /**
     * add a new user from UserEmbryo.
     */
    public abstract void addUser(PartakeConnection con, User user) throws DAOException;

    /**
     * get user by specifying id.
     * @param id
     * @return
     * @throws DAOException
     */
    public abstract User getUser(PartakeConnection con, String id) throws DAOException;

    public abstract void updateLastLogin(PartakeConnection con, User user, Date now) throws DAOException;
}