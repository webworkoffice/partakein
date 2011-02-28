package in.partake.model.dao.jpa;

import in.partake.model.dao.IBinaryAccess;
import in.partake.model.dao.ICacheAccess;
import in.partake.model.dao.ICalendarLinkageAccess;
import in.partake.model.dao.ICommentAccess;
import in.partake.model.dao.IEnvelopeAccess;
import in.partake.model.dao.IEventActivityAccess;
import in.partake.model.dao.IEventReminderAccess;
import in.partake.model.dao.IMessageAccess;
import in.partake.model.dao.IEnrollmentAccess;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.IEventRelationAccess;
import in.partake.model.dao.IEventFeedAccess;
import in.partake.model.dao.IOpenIDLinkageAccess;
import in.partake.model.dao.ITwitterLinkageAccess;
import in.partake.model.dao.IURLShortenerAccess;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.IUserPreferenceAccess;
import in.partake.model.dao.PartakeDAOFactory;

public class JPADAOFactory extends PartakeDAOFactory {

    @Override
    protected ICacheAccess createCacheAccess() {
        return new JPACacheDao();
    }

    @Override
    protected ICalendarLinkageAccess createCalendarLinkageAccess() {
        return new JPACalendarLinkageDao();        
    }

    @Override
    protected IBinaryAccess createBinaryAccess() {
        return new JPABinaryDao();
    }

    @Override
    protected ICommentAccess createCommentAccess() {
        return new JPACommentDao();
    }

    @Override
    protected IMessageAccess createDirectMessageAccess() {
        return new JPAMessageDao();
    }

    @Override
    protected IEnrollmentAccess createEnrollmentAccess() {
        return new JPAEnrollmentDao();
    }
    
    @Override
    protected IEnvelopeAccess createEnvelopeAccess() {
        return new JPAEnvelopeDao();
    }

    @Override
    protected IEventAccess createEventAccess() {
        return new JPAEventDao();
    }

    @Override
    protected IEventRelationAccess createEventRelationAccess() {
        return new JPAEventRelationDao();
    }

    @Override
    protected IEventReminderAccess createEventReminderAccess() {
        return new JPAEventReminderDao();
    }
    
    @Override
    protected IEventFeedAccess createEventFeedAccess() {
        return new JPAEventFeedLinkageDao();
    }

    @Override
    protected IEventActivityAccess createEventActivityAccess() {
        return new JPAEventActivityDao();
    }
    
    @Override
    protected IOpenIDLinkageAccess createOpenIDLinkageAccess() {
        return new JPAOpenIDLinkageDao();
    }

    @Override
    protected ITwitterLinkageAccess createTwitterLinkageAccess() {
        return new JPATwitterLinkageDao();
    }

    @Override
    protected IUserAccess creataeUserAccess() {
        return new JPAUserDao();
    }

    @Override
    protected IUserPreferenceAccess createUserPreferenceAccess() {
        return new JPAUserPreferenceDao();
    }

    @Override
    protected IURLShortenerAccess createUrlShortenerAccess() {
        return new JPAURLShortenerDao();
    }

    
}
