package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.base.permission.EventEditPermission;
import in.partake.model.EventEx;
import in.partake.model.EventRelationEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.base.Transaction;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.Event;
import in.partake.resource.UserErrorCode;

import java.util.ArrayList;

public class EventNewAction extends AbstractEventEditAction {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        
        String eventId = optValidIdParameter("eventId", UserErrorCode.INVALID_EVENT_ID, null);
        if (eventId != null) {
            event = new EventNewTransaction(eventId).execute();
            // If the event is not owned (or edit-able) by the user, we don't allow to copy.
            if (!EventEditPermission.check(event, user))
                event = null;
        }

        // We want to polish URL here.
        if (eventId != null && event == null)
            return renderRedirect("/events/new");
        
        if (event == null)
            event = new EventEx(new Event(), user, null, null, new ArrayList<EventRelationEx>());

        return render("events/new.jsp");
    }
}

class EventNewTransaction extends Transaction<EventEx> {
    private String eventId;
    
    EventNewTransaction(String eventId) {
        this.eventId = eventId;
    }
    
    @Override
    protected EventEx doExecute(PartakeConnection con) throws DAOException, PartakeException {
        return EventDAOFacade.getEventEx(con, eventId);
    }
}
