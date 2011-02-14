package in.partake.model.dao;

import java.util.Date;

import in.partake.model.dto.User;

public interface IUserAccess extends IAccess<User, String> {
    // fresh な user id を１つ作成して返す。
    public abstract String getFreshId(PartakeConnection con) throws DAOException;
    
    @Deprecated
    public abstract void updateLastLogin(PartakeConnection con, String userId, Date now) throws DAOException;
}