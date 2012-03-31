package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.User;

import java.util.Date;

public interface IUserAccess extends IAccess<User, String> {
    // fresh な user id を１つ作成して返す。
    public abstract String getFreshId(PartakeConnection con) throws DAOException;
    
    public int countActiveUsers(PartakeConnection con, Date loggedinAfter) throws DAOException;
}