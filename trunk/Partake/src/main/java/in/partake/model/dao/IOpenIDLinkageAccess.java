package in.partake.model.dao;


public interface IOpenIDLinkageAccess extends ITruncatable {

    public abstract void addOpenID(PartakeConnection con, String identifier, String userId) throws DAOException;
    public abstract String getUserId(PartakeConnection con, String identifier) throws DAOException;
    public abstract void removeOpenID(PartakeConnection con, String identifier) throws DAOException;

    public abstract DataIterator<String> getOpenIDIdentifiers(PartakeConnection con, String userId) throws DAOException;
}