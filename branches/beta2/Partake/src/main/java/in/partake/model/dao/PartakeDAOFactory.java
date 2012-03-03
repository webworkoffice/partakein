package in.partake.model.dao;

import in.partake.model.dao.access.IAccess;
import in.partake.model.dao.access.IBinaryAccess;
import in.partake.model.dao.access.ICacheAccess;
import in.partake.model.dao.access.ICalendarLinkageAccess;
import in.partake.model.dao.access.ICommentAccess;
import in.partake.model.dao.access.IEnrollmentAccess;
import in.partake.model.dao.access.IEnvelopeAccess;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dao.access.IEventFeedAccess;
import in.partake.model.dao.access.IEventRelationAccess;
import in.partake.model.dao.access.IEventReminderAccess;
import in.partake.model.dao.access.IMessageAccess;
import in.partake.model.dao.access.IOpenIDLinkageAccess;
import in.partake.model.dao.access.ITwitterLinkageAccess;
import in.partake.model.dao.access.IURLShortenerAccess;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dao.access.IUserPreferenceAccess;

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
    
    private final List<IAccess<?, ?>> daos;
    
    protected PartakeDAOFactory() {
        daos = new ArrayList<IAccess<?, ?>>();
        
        addDao(cacheAccess           = createCacheAccess());
        addDao(calendarLinkageAccess = createCalendarLinkageAccess());
        addDao(binaryAccess          = createBinaryAccess());
        addDao(commentAccess         = createCommentAccess());
        addDao(directMessageAccess   = createDirectMessageAccess());
        addDao(envelopeAccess        = createEnvelopeAccess());
        addDao(enrollmentAccess      = createEnrollmentAccess());
        addDao(eventAccess           = createEventAccess());
        addDao(eventRelationAccess   = createEventRelationAccess());
        addDao(eventReminderAccess   = createEventReminderAccess());
        addDao(eventFeedAccess       = createEventFeedAccess());
        addDao(eventActivityAccess   = createEventActivityAccess());
        addDao(openIDLinkageAccess   = createOpenIDLinkageAccess());
        addDao(twitterLinkageAccess  = createTwitterLinkageAccess());
        addDao(userAccess            = creataeUserAccess());
        addDao(userPreferenceAccess  = createUserPreferenceAccess());
        addDao(urlShortenerAccess    = createUrlShortenerAccess());
    }

    public void initialize(PartakeConnection con) throws DAOException {
        for (IAccess<?, ?> dao : daos) {
            dao.initialize(con);
        }
    }
    
    // ----------------------------------------------------------------------
    // 
    
    private void addDao(IAccess<?, ?> t) {
        if (t != null)
            daos.add(t);
    }
    
    public List<IAccess<?, ?>> getDaos() {
        return Collections.unmodifiableList(daos);
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
