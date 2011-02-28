package in.partake.model.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PartakeDAOFactory {
    private final ICacheAccess cacheAccess;
    private final ICalendarLinkageAccess calendarLinkageAccess;    
    private final IBinaryAccess binaryAccess;
    private final ICommentAccess commentAccess;
    private final IMessageAccess directMessageAccess;
    private final IEnrollmentAccess enrollmentAccess;
    private final IEnvelopeAccess envelopeAccess;
    private final IEventAccess eventAccess;
    private final IEventRelationAccess eventRelationAccess;
    private final IEventReminderAccess eventReminderAccess;
    private final IEventFeedAccess eventFeedAccess;
    private final IEventActivityAccess eventActivityAccess;
    private final IOpenIDLinkageAccess openIDLinkageAccess;
    private final ITwitterLinkageAccess twitterLinkageAccess;
    private final IUserAccess userAccess;
    private final IUserPreferenceAccess userPreferenceAccess;
    private final IURLShortenerAccess urlShortenerAccess;
    
    private final List<ITruncatable> truncatables;
    
    public PartakeDAOFactory() {
        truncatables = new ArrayList<ITruncatable>();
        
        addTruncatable(cacheAccess           = createCacheAccess());
        addTruncatable(calendarLinkageAccess = createCalendarLinkageAccess());
        addTruncatable(binaryAccess          = createBinaryAccess());
        addTruncatable(commentAccess         = createCommentAccess());
        addTruncatable(directMessageAccess   = createDirectMessageAccess());
        addTruncatable(envelopeAccess        = createEnvelopeAccess());
        addTruncatable(enrollmentAccess      = createEnrollmentAccess());
        addTruncatable(eventAccess           = createEventAccess());
        addTruncatable(eventRelationAccess   = createEventRelationAccess());
        addTruncatable(eventReminderAccess   = createEventReminderAccess());
        addTruncatable(eventFeedAccess       = createEventFeedAccess());
        addTruncatable(eventActivityAccess   = createEventActivityAccess());
        addTruncatable(openIDLinkageAccess   = createOpenIDLinkageAccess());
        addTruncatable(twitterLinkageAccess  = createTwitterLinkageAccess());
        addTruncatable(userAccess            = creataeUserAccess());
        addTruncatable(userPreferenceAccess  = createUserPreferenceAccess());
        addTruncatable(urlShortenerAccess    = createUrlShortenerAccess());
    }
    
    // ----------------------------------------------------------------------
    // 
    
    private void addTruncatable(ITruncatable t) {
        truncatables.add(t);
    }
    
    public List<ITruncatable> getTruncatables() {
        return Collections.unmodifiableList(truncatables);
    }
    
    
    // ----------------------------------------------------------------------
    // accessors
    
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

    public final IMessageAccess getDirectMessageAccess() {
        return directMessageAccess;
    }
    
    public final IEventReminderAccess getEventReminderAccess() {
        return eventReminderAccess;
    }
    
    public final IEnvelopeAccess getEnvelopeAccess() {
        return envelopeAccess;
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

    public final IEventFeedAccess getEventFeedAccess() {
        return eventFeedAccess;
    }
    
    public final IEventActivityAccess getEventActivityAccess() {
        return eventActivityAccess;
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
    
    // ----------------------------------------------------------------------
    // abstract factory
    
    protected abstract ICacheAccess createCacheAccess();
    protected abstract ICalendarLinkageAccess createCalendarLinkageAccess();
    protected abstract IBinaryAccess createBinaryAccess();
    protected abstract ICommentAccess createCommentAccess();
    protected abstract IMessageAccess createDirectMessageAccess();
    protected abstract IEnrollmentAccess createEnrollmentAccess();
    protected abstract IEnvelopeAccess createEnvelopeAccess();
    protected abstract IEventAccess createEventAccess();
    protected abstract IEventRelationAccess createEventRelationAccess();
    protected abstract IEventReminderAccess createEventReminderAccess();
    protected abstract IEventFeedAccess createEventFeedAccess();
    protected abstract IEventActivityAccess createEventActivityAccess();
    protected abstract IOpenIDLinkageAccess createOpenIDLinkageAccess();
    protected abstract ITwitterLinkageAccess createTwitterLinkageAccess();
    protected abstract IUserAccess creataeUserAccess();
    protected abstract IUserPreferenceAccess createUserPreferenceAccess();
    protected abstract IURLShortenerAccess createUrlShortenerAccess();
}
