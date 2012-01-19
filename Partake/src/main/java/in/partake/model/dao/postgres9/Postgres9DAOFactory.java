package in.partake.model.dao.postgres9;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
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
import in.partake.model.dao.access.IQuestionnaireAccess;
import in.partake.model.dao.access.ITwitterLinkageAccess;
import in.partake.model.dao.access.IURLShortenerAccess;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dao.access.IUserPreferenceAccess;
import in.partake.model.dao.postgres9.impl.Postgres9UserDao;

public class Postgres9DAOFactory extends PartakeDAOFactory {
    private Postgres9EntityDao entityDao;

    public Postgres9DAOFactory() {
        super();
    }
    
    // FIXME: This seems a bad architecture.
    @Override
    protected void willCreateDAOs() {
        entityDao = new Postgres9EntityDao();
    }
    
    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        entityDao.initialize(con);
        super.initialize(con);
    }
    
    @Override
    protected ICacheAccess createCacheAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected ICalendarLinkageAccess createCalendarLinkageAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected IBinaryAccess createBinaryAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected ICommentAccess createCommentAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected IMessageAccess createDirectMessageAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected IEnrollmentAccess createEnrollmentAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected IEnvelopeAccess createEnvelopeAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected IEventAccess createEventAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected IEventRelationAccess createEventRelationAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected IEventReminderAccess createEventReminderAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected IEventFeedAccess createEventFeedAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected IEventActivityAccess createEventActivityAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected IOpenIDLinkageAccess createOpenIDLinkageAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected IQuestionnaireAccess createQuestionnaireAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected ITwitterLinkageAccess createTwitterLinkageAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected IUserAccess creataeUserAccess() {
        return new Postgres9UserDao(entityDao);
    }

    @Override
    protected IUserPreferenceAccess createUserPreferenceAccess() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected IURLShortenerAccess createUrlShortenerAccess() {
        // TODO Auto-generated method stub
        return null;
    }

}
