package in.partake.model.dao;

// TODO: PartakeConnectionFactory と PartakeDAOFactory に分ける予定。
// 任意の DAO はほしいが Connection が取れないようにしたい、というシチュエーションが結構あるので。
public abstract class PartakeModelFactory {
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
