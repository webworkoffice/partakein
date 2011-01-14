package in.partake.model.dao.mock;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import in.partake.model.dao.IBinaryAccess;
import in.partake.model.dao.ICacheAccess;
import in.partake.model.dao.ICalendarLinkageAccess;
import in.partake.model.dao.ICommentAccess;
import in.partake.model.dao.IDirectMessageAccess;
import in.partake.model.dao.IEnrollmentAccess;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.IEventRelationAccess;
import in.partake.model.dao.IFeedAccess;
import in.partake.model.dao.IOpenIDLinkageAccess;
import in.partake.model.dao.ITwitterLinkageAccess;
import in.partake.model.dao.IURLShortenerAccess;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.IUserPreferenceAccess;
import in.partake.model.dao.PartakeDAOFactory;

public class MockDaoFactory extends PartakeDAOFactory {
    @Mock private ICacheAccess cacheAccess;
    @Mock private ICalendarLinkageAccess calendarLinkageAccess;    
    @Mock private IBinaryAccess binaryAccess;
    @Mock private ICommentAccess commentAccess;
    @Mock private IDirectMessageAccess directMessageAccess;
    @Mock private IEnrollmentAccess enrollmentAccess;
    @Mock private IEventAccess eventAccess;
    @Mock private IEventRelationAccess eventRelationAccess;
    @Mock private IFeedAccess feedAccess;
    @Mock private IOpenIDLinkageAccess openIDLinkageAccess;
    @Mock private ITwitterLinkageAccess twitterLinkageAccess;
    @Mock private IUserAccess userAccess;
    @Mock private IUserPreferenceAccess userPreferenceAccess;
    @Mock private IURLShortenerAccess urlShortenerAccess;

    public MockDaoFactory() {
        MockitoAnnotations.initMocks(this);       
    }
    
    public void resetAll() {
        Mockito.reset(cacheAccess, calendarLinkageAccess, binaryAccess, commentAccess, directMessageAccess, enrollmentAccess);
        Mockito.reset(eventAccess, eventRelationAccess, feedAccess, openIDLinkageAccess, twitterLinkageAccess);
        Mockito.reset(userAccess, userPreferenceAccess, urlShortenerAccess);
    }
    
    @Override
    public ICacheAccess getCacheAccess() {
        return cacheAccess;
    }
    
    @Override
    public IBinaryAccess getBinaryAccess() {
        return binaryAccess;
    }

    @Override
    public ICalendarLinkageAccess getCalendarAccess() {
        return calendarLinkageAccess;
    }

    @Override
    public ICommentAccess getCommentAccess() {
        return commentAccess;
    }

    @Override
    public IDirectMessageAccess getDirectMessageAccess() {
        return directMessageAccess;
    }

    @Override
    public IEnrollmentAccess getEnrollmentAccess() {
        return enrollmentAccess;
    }

    @Override
    public IEventAccess getEventAccess() {        
        return eventAccess;
    }

    @Override
    public IEventRelationAccess getEventRelationAccess() {
        return eventRelationAccess;
    }

    @Override
    public IFeedAccess getFeedAccess() {
        return feedAccess;
    }

    @Override
    public IOpenIDLinkageAccess getOpenIDLinkageAccess() {
        return openIDLinkageAccess;
    }

    @Override
    public ITwitterLinkageAccess getTwitterLinkageAccess() {
        return twitterLinkageAccess;
    }

    @Override
    public IUserAccess getUserAccess() {
        return userAccess;
    }

    @Override
    public IUserPreferenceAccess getUserPreferenceAccess() {
        return userPreferenceAccess;
    }

    @Override
    public IURLShortenerAccess getURLShortenerAccess() {
        return urlShortenerAccess;
    }
}
