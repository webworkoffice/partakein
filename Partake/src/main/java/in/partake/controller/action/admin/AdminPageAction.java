package in.partake.controller.action.admin;

import java.util.Date;

import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dao.aux.EventFilterCondition;
import in.partake.model.dao.base.Transaction;
import in.partake.service.DBService;

public class AdminPageAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    private int countUser;
    private int countActiveUser;
    private int countEvent;
    private int countPublicEvent;
    private int countPrivateEvent;
    private int countDraftEvent;
    private int countPublishedEvent;
    
    public String doExecute() throws DAOException, PartakeException {
        ensureAdmin();
        
        CountTransaction transaction = new CountTransaction();
        transaction.execute();
        
        countUser = transaction.getCountUser();
        countActiveUser = transaction.getCountActiveUser();
        countEvent = transaction.getCountEvent();
        countPublicEvent = transaction.getCountPublicEvent();
        countPrivateEvent = transaction.getCountPrivateEvent();
        countDraftEvent = transaction.getCountDraftEvent();
        countPublishedEvent = transaction.getCountPublishedEvent();
        
        return render("admin/index.jsp");
    }
    
    public int getCountUser() {
        return countUser;
    }
    
    public int getCountActiveUser() {
        return countActiveUser;
    }

    public int getCountEvent() {
        return countEvent;
    }

    public int getCountPublicEvent() {
        return countPublicEvent;
    }

    public int getCountPrivateEvent() {
        return countPrivateEvent;
    }

    public int getCountDraftEvent() {
        return countDraftEvent;
    }
    
    public int getCountPublishedEvent() {
        return countPublishedEvent;
    }
}

class CountTransaction extends Transaction<Void> {
    private int countUser;
    private int countActiveUser;
    
    private int countEvent;
    private int countPublicEvent;
    private int countPrivateEvent;
    private int countDraftEvent;
    private int countPublishedEvent;

    @Override
    protected Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
        Date oneMonthAgo = new Date(TimeUtil.getCurrentTime() - 30L * 24 * 60 * 60 * 1000);

        IUserAccess userAccess = DBService.getFactory().getUserAccess();
        countUser = userAccess.count(con);
        countActiveUser = userAccess.countActiveUsers(con, oneMonthAgo);

        IEventAccess eventAccess = DBService.getFactory().getEventAccess();
        countEvent = eventAccess.count(con);
        countPublicEvent = eventAccess.count(con, EventFilterCondition.PUBLIC_EVENT_ONLY);
        countPrivateEvent = eventAccess.count(con, EventFilterCondition.PRIVATE_EVENT_ONLY);
        countDraftEvent = eventAccess.count(con, EventFilterCondition.DRAFT_EVENT_ONLY);
        countPublishedEvent = eventAccess.count(con, EventFilterCondition.PUBLISHED_EVENT_ONLY);
        return null;
    }
    
    public int getCountUser() {
        return countUser;
    }
    
    public int getCountActiveUser() {
        return countActiveUser;
    }

    public int getCountEvent() {
        return countEvent;
    }

    public int getCountPublicEvent() {
        return countPublicEvent;
    }

    public int getCountPrivateEvent() {
        return countPrivateEvent;
    }

    public int getCountDraftEvent() {
        return countDraftEvent;
    }
    
    public int getCountPublishedEvent() {
        return countPublishedEvent;
    }
}