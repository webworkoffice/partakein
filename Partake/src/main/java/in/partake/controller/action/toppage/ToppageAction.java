package in.partake.controller.action.toppage;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.model.dto.Event;

import java.util.List;

public class ToppageAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;
	//private static final Logger logger = Logger.getLogger(ToppageAction.class);
	
    private static final int NUM_EVENTS_TO_DISPLAY = 5;

	private List<Event> recentEvents;
	private List<Event> ownedEvents;
	private List<Event> enrolledEvents;
	
	public String doExecute() throws DAOException {
	    recentEvents = DeprecatedEventDAOFacade.get().getRecentEvents(NUM_EVENTS_TO_DISPLAY);
		
		// もしログインしていれば、最近のイベントを表示する。
		UserEx user = getLoginUser();
		if (user != null) {
		    ownedEvents = DeprecatedEventDAOFacade.get().getUnfinishedEventsOwnedBy(user.getId());
	        enrolledEvents = DeprecatedEventDAOFacade.get().getUnfinishedEnrolledEvents(user.getId());
		}

		return render("index.jsp");
	}

	public List<Event> getRecentEvents() {
	    return this.recentEvents;
	}

	public List<Event> getOwnedEvents() {
	    return this.ownedEvents;
	}
	
	public List<Event> getEnrolledEvents() {
	    return this.enrolledEvents;
	}
}
