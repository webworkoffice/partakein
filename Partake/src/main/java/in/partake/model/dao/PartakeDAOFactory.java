package in.partake.model.dao;


public abstract class PartakeDAOFactory {
    public abstract PartakeConnection getConnection() throws DAOException;
    public abstract PartakeConnection getConnection(String name) throws DAOException;
    public abstract void releaseConnection(PartakeConnection connection);
    
    public abstract IBinaryAccess getBinaryAccess();
    public abstract ICalendarLinkageAccess getCalendarAccess();
    public abstract ICommentAccess getCommentAccess();
    public abstract IDirectMessageAccess getDirectMessageAccess();
    public abstract IEnrollmentAccess getEnrollmentAccess();
    public abstract IEventAccess getEventAccess();
    public abstract IEventRelationAccess getEventRelationAccess();
    public abstract IFeedAccess getFeedAccess();
    public abstract IMessageAccess getMessageAccess();
    public abstract IOpenIDLinkageAccess getOpenIDLinkageAccess();
    public abstract ITwitterLinkageAccess getTwitterLinkageAccess();
    public abstract IUserAccess getUserAccess();
    public abstract IUserPreferenceAccess getUserPreferenceAccess();
}
