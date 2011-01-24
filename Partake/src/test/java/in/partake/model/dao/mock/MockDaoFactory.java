package in.partake.model.dao.mock;

import org.mockito.Mockito;

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

    public MockDaoFactory() {
    }
    
    @Override
    protected ICacheAccess createCacheAccess() {
        return Mockito.mock(ICacheAccess.class);
    }

    @Override
    protected ICalendarLinkageAccess createCalendarLinkageAccess() {
        return Mockito.mock(ICalendarLinkageAccess.class);
    }

    @Override
    protected IBinaryAccess createBinaryAccess() {
        return Mockito.mock(IBinaryAccess.class);
    }

    @Override
    protected ICommentAccess createCommentAccess() {
        return Mockito.mock(ICommentAccess.class);
    }

    @Override
    protected IDirectMessageAccess createDirectMessageAccess() {
        return Mockito.mock(IDirectMessageAccess.class);
    }

    @Override
    protected IEnrollmentAccess createEnrollmentAccess() {
        return Mockito.mock(IEnrollmentAccess.class);
    }

    @Override
    protected IEventAccess createEventAccess() {
        return Mockito.mock(IEventAccess.class);
    }

    @Override
    protected IEventRelationAccess createEventRelationAccess() {
        return Mockito.mock(IEventRelationAccess.class);
    }

    @Override
    protected IFeedAccess createFeedAccess() {
        return Mockito.mock(IFeedAccess.class);
    }

    @Override
    protected IOpenIDLinkageAccess createOpenIDLinkageAccess() {
        return Mockito.mock(IOpenIDLinkageAccess.class);
    }

    @Override
    protected ITwitterLinkageAccess createTwitterLinkageAccess() {
        return Mockito.mock(ITwitterLinkageAccess.class);
    }

    @Override
    protected IUserAccess creataeUserAccess() {
        return Mockito.mock(IUserAccess.class);
    }

    @Override
    protected IUserPreferenceAccess createUserPreferenceAccess() {
        return Mockito.mock(IUserPreferenceAccess.class);
    }

    @Override
    protected IURLShortenerAccess createUrlShortenerAccess() {
        return Mockito.mock(IURLShortenerAccess.class);
    }

    
}
