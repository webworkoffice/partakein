package in.partake.model.dao;


public interface IOpenIDLinkageAccess {

    public abstract void addOpenID(PartakeConnection con, String identifier, String userId) throws DAOException;
    public abstract String getUserId(PartakeConnection con, String identifier) throws DAOException;
    public abstract void removeOpenID(PartakeConnection con, String identifier, String userId) throws DAOException;
    
    /** Use ONLY in unit tests.*/
    public abstract void truncate(PartakeConnection con) throws DAOException;
}