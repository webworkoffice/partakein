package in.partake.controller.action.event;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.model.dto.Event;

import org.apache.commons.lang.StringUtils;


public class EventPasscodeAction extends AbstractPartakeAction {
	private static final long serialVersionUID = 1L;
	
	private String eventId;
	
	public String doExecute() throws DAOException, PartakeException {
        eventId = getValidEventIdParameter();

	    String passcode = getParameter("passcode");
	    
	    if (passcode == null)
	        return render("events/passcode.jsp");

	    Event event = DeprecatedEventDAOFacade.get().getEventById(eventId);
	    if (event == null)
	        return renderNotFound();

	    String pass = StringUtils.trim(passcode);

	    if (!pass.equals(event.getPasscode())) {
	        addWarningMessage("passcode が一致しませんでした。");
	        return render("events/passcode.jsp");
	    }

	    session.put("event:" + eventId, passcode);
	    return renderRedirect("/events/" + eventId);
	}
	
	public String getEventId() {
	    return eventId;
	}
}
