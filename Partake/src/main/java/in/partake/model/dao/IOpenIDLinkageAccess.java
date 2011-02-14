package in.partake.model.dao;

import in.partake.model.dto.OpenIDLinkage;

import java.util.List;


public interface IOpenIDLinkageAccess extends IAccess<OpenIDLinkage, String> {
    public abstract List<String> findByUserId(PartakeConnection con, String userId) throws DAOException;
}