package in.partake.model.dao;

public abstract class PartakeDAOFactory {
    public abstract ICacheAccess getCacheAccess();
    public abstract IBinaryAccess getBinaryAccess();
    public abstract ICalendarLinkageAccess getCalendarAccess();
    public abstract ICommentAccess getCommentAccess();
    public abstract IDirectMessageAccess getDirectMessageAccess();
    public abstract IEnrollmentAccess getEnrollmentAccess();
    public abstract IEventAccess getEventAccess();
    public abstract IEventRelationAccess getEventRelationAccess();
    public abstract IFeedAccess getFeedAccess();
    public abstract IOpenIDLinkageAccess getOpenIDLinkageAccess();
    public abstract ITwitterLinkageAccess getTwitterLinkageAccess();
    public abstract IUserAccess getUserAccess();
    public abstract IUserPreferenceAccess getUserPreferenceAccess();
}
