package in.partake.model.dao;


public interface IOpenIDLinkageAccess {

    public abstract void addOpenID(PartakeConnection con, String identity, String userId) throws DAOException;

    public abstract String getUserId(PartakeConnection con, String identity) throws DAOException;

}