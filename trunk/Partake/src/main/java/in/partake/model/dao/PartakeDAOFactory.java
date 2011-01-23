package in.partake.model.dao;

public abstract class PartakeDAOFactory {
    private final ICacheAccess cacheAccess;
    private final ICalendarLinkageAccess calendarLinkageAccess;    
    private final IBinaryAccess binaryAccess;
    private final ICommentAccess commentAccess;
    private final IDirectMessageAccess directMessageAccess;
    private final IEnrollmentAccess enrollmentAccess;
    private final IEventAccess eventAccess;
    private final IEventRelationAccess eventRelationAccess;
    private final IFeedAccess feedAccess;
    private final IOpenIDLinkageAccess openIDLinkageAccess;
    private final ITwitterLinkageAccess twitterLinkageAccess;
    private final IUserAccess userAccess;
    private final IUserPreferenceAccess userPreferenceAccess;
    private final IURLShortenerAccess urlShortenerAccess;
    
    public PartakeDAOFactory() {
        cacheAccess           = createCacheAccess();
        calendarLinkageAccess = createCalendarLinkageAccess();
        binaryAccess          = createBinaryAccess();
        commentAccess         = createCommentAccess();
        directMessageAccess   = createDirectMessageAccess();
        enrollmentAccess      = createEnrollmentAccess();
        eventAccess           = createEventAccess();
        eventRelationAccess   = createEventRelationAccess();
        feedAccess            = createFeedAccess();
        openIDLinkageAccess   = createOpenIDLinkageAccess();
        twitterLinkageAccess  = createTwitterLinkageAccess();
        userAccess            = creataeUserAccess();
        userPreferenceAccess  = createUserPreferenceAccess();
        urlShortenerAccess    = createUrlShortenerAccess();
    }
    
    public final ICacheAccess getCacheAccess() {
        return cacheAccess;
    }
    
    public final ICalendarLinkageAccess getCalendarAccess() {
        return calendarLinkageAccess;
    }
    
    public final IBinaryAccess getBinaryAccess() {
        return binaryAccess;
    }
    
    public final ICommentAccess getCommentAccess() {
        return commentAccess;
    }

    public final IDirectMessageAccess getDirectMessageAccess() {
        return directMessageAccess;
    }
    
    public final IEnrollmentAccess getEnrollmentAccess() {
        return enrollmentAccess;
    }

    public final IEventAccess getEventAccess() {
        return eventAccess;
    }
    
    public final IEventRelationAccess getEventRelationAccess() {
        return eventRelationAccess;
    }

    public final IFeedAccess getFeedAccess() {
        return feedAccess;
    }

    public final IOpenIDLinkageAccess getOpenIDLinkageAccess() {
        return openIDLinkageAccess;
    }

    public final ITwitterLinkageAccess getTwitterLinkageAccess() {
        return twitterLinkageAccess;
    }

    public final IUserAccess getUserAccess() {
        return userAccess;
    }
    
    public final IUserPreferenceAccess getUserPreferenceAccess() {
        return userPreferenceAccess;
    }
    
    public final IURLShortenerAccess getURLShortenerAccess() {
        return urlShortenerAccess;
    }
    
    protected abstract ICacheAccess createCacheAccess();
    protected abstract ICalendarLinkageAccess createCalendarLinkageAccess();
    protected abstract  IBinaryAccess createBinaryAccess();
    protected abstract ICommentAccess createCommentAccess();
    protected abstract IDirectMessageAccess createDirectMessageAccess();
    protected abstract IEnrollmentAccess createEnrollmentAccess();
    protected abstract IEventAccess createEventAccess();
    protected abstract IEventRelationAccess createEventRelationAccess();
    protected abstract IFeedAccess createFeedAccess();
    protected abstract IOpenIDLinkageAccess createOpenIDLinkageAccess();
    protected abstract ITwitterLinkageAccess createTwitterLinkageAccess();
    protected abstract IUserAccess creataeUserAccess();
    protected abstract IUserPreferenceAccess createUserPreferenceAccess();
    protected abstract IURLShortenerAccess createUrlShortenerAccess();
}
