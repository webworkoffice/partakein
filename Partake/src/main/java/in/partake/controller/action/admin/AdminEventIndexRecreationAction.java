package in.partake.controller.action.admin;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.base.Transaction;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.service.IEventSearchService;
import in.partake.service.PartakeService;

public class AdminEventIndexRecreationAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    public String doExecute() throws DAOException, PartakeException {
        ensureAdmin(); 
        ensureValidSessionToken();
        
        new EventIndexRecreationTransaction().execute();
        
        addActionMessage("Event Index has been recreated.");
        return renderRedirect("/admin");
    }
}

class EventIndexRecreationTransaction extends Transaction<Void> {
    @Override
    protected Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
        IEventSearchService searchService = PartakeService.get().getEventSearchService();
        EventDAOFacade.recreateEventIndex(con, searchService);
        return null;
    }
}
