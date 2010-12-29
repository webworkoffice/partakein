package in.partake.model.dao.mock;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import in.partake.model.dao.IBinaryAccess;
import in.partake.model.dao.ICalendarLinkageAccess;
import in.partake.model.dao.ICommentAccess;
import in.partake.model.dao.IDirectMessageAccess;
import in.partake.model.dao.IEnrollmentAccess;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.IEventRelationAccess;
import in.partake.model.dao.IFeedAccess;
import in.partake.model.dao.IMessageAccess;
import in.partake.model.dao.IOpenIDLinkageAccess;
import in.partake.model.dao.ITwitterLinkageAccess;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.IUserPreferenceAccess;
import in.partake.model.dao.PartakeDAOFactory;

public class MockDaoFactory extends PartakeDAOFactory {
    // あとでちゃんと書く
    @Mock IEventAccess eventAccess;
    
    public MockDaoFactory() {
        MockitoAnnotations.initMocks(this);       
    }    
    
    @Override
    public IBinaryAccess getBinaryAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ICalendarLinkageAccess getCalendarAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ICommentAccess getCommentAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IDirectMessageAccess getDirectMessageAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IEnrollmentAccess getEnrollmentAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IEventAccess getEventAccess() {
        return eventAccess;
    }

    @Override
    public IEventRelationAccess getEventRelationAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IFeedAccess getFeedAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IMessageAccess getMessageAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IOpenIDLinkageAccess getOpenIDLinkageAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ITwitterLinkageAccess getTwitterLinkageAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IUserAccess getUserAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IUserPreferenceAccess getUserPreferenceAccess() {
        // TODO Auto-generated method stub
        return null;
    }

}
