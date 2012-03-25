package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;
import in.partake.resource.MessageCode;

import org.apache.commons.lang.StringUtils;


public class EventPasscodeAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    private String eventId;

    public String doExecute() throws DAOException, PartakeException {
        eventId = getValidEventIdParameter();

        String passcode = getParameter("passcode");

        if (passcode == null)
            return render("events/passcode.jsp");

        Event event = new EventPasscodeTransaction(eventId).execute();
        if (event == null)
            return renderNotFound();

        String pass = StringUtils.trim(passcode);

        if (!pass.equals(event.getPasscode()))
            return render("events/passcode.jsp", MessageCode.MESSAGE_PASSCODE_INVALID);

        // TODO: Reconsider Session.
        session.put("event:" + eventId, passcode);
        return renderRedirect("/events/" + eventId);
    }

    public String getEventId() {
        return eventId;
    }
}

class EventPasscodeTransaction extends DBAccess<Event> {
    private String eventId;

    public EventPasscodeTransaction(String eventId) {
        this.eventId = eventId;
    }

    @Override
    protected Event doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        return daos.getEventAccess().find(con, eventId);
    }
}
