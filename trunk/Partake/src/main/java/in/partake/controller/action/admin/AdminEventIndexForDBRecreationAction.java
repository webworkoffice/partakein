package in.partake.controller.action.admin;

import org.apache.log4j.Logger;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.base.Util;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.Event;
import in.partake.resource.MessageCode;
import in.partake.service.IEventSearchService;

// TODO: should be APIed.
public class AdminEventIndexForDBRecreationAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    public String doExecute() throws DAOException, PartakeException {
        ensureAdmin();
        // ensureValidSessionToken();

        new EventIndexRecreationForDBTransaction().execute();
        return renderRedirect("/admin/", MessageCode.MESSAGE_EVENT_INDEX_RECREATED);
    }
}

class EventIndexRecreationForDBTransaction extends Transaction<Void> {
    Logger logger = Logger.getLogger("EventIndexRecreationForDBTransaction");

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        DataIterator<Event> it = daos.getEventAccess().getIterator(con);
        while (it.hasNext()) {
            Event event = it.next();
            if (event == null)
                continue;
            if (event.getId() == null)
                continue;
            if (!Util.isUUID(event.getId())) {
                continue;
            }

            daos.getEventAccess().updateIndex(con, event);
        }
        return null;
    }
}
