package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserOpenIDLink;

import java.util.List;


public interface IUserOpenIDLinkAccess extends IAccess<UserOpenIDLink, String> {
    public abstract List<String> findByUserId(PartakeConnection con, String userId) throws DAOException;
}