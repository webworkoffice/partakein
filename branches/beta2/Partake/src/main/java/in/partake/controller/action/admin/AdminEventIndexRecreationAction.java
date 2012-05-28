package in.partake.controller.action.admin;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.resource.MessageCode;
import in.partake.service.IEventSearchService;

public class AdminEventIndexRecreationAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    public String doExecute() throws DAOException, PartakeException {
        ensureAdmin();
        // ensureValidSessionToken();

        new EventIndexRecreationTransaction().execute();
        return renderRedirect("/admin", MessageCode.MESSAGE_EVENT_INDEX_RECREATED);
    }
}

class EventIndexRecreationTransaction extends Transaction<Void> {

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IEventSearchService searchService = PartakeApp.getEventSearchService();
        EventDAOFacade.recreateEventIndex(con, daos, searchService);
        return null;
    }
}
