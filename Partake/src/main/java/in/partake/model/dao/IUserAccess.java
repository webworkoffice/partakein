package in.partake.model.dao;

import in.partake.model.dto.User;

public interface IUserAccess extends IAccess<User, String> {
    // fresh な user id を１つ作成して返す。
    public abstract String getFreshId(PartakeConnection con) throws DAOException;
    
    
}