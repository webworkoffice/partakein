package in.partake.model.dao.jpa;

import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.IBinaryAccess;
import in.partake.model.dao.access.ICalendarLinkageAccess;
import in.partake.model.dao.access.ICommentAccess;
import in.partake.model.dao.access.IEnrollmentAccess;
import in.partake.model.dao.access.IEnvelopeAccess;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dao.access.IEventFeedAccess;
import in.partake.model.dao.access.IEventRelationAccess;
import in.partake.model.dao.access.IEventReminderAccess;
import in.partake.model.dao.access.IImageAccess;
import in.partake.model.dao.access.IMessageAccess;
import in.partake.model.dao.access.IOpenIDLinkageAccess;
import in.partake.model.dao.access.ITwitterLinkageAccess;
import in.partake.model.dao.access.IURLShortenerAccess;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dao.access.IUserPreferenceAccess;

public class JPADAOFactory extends PartakeDAOFactory {

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

    @Override
    protected IImageAccess createImageAccess() {
        return new JPAImageDao();
    }
}
