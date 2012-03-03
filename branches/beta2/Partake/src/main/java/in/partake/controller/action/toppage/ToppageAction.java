package in.partake.controller.action.toppage;

import in.partake.controller.PartakeActionSupport;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.service.EventService;

import java.util.List;


public class ToppageAction extends PartakeActionSupport {
    private static final long serialVersionUID = 1L;
	//private static final Logger logger = Logger.getLogger(ToppageAction.class);
	
    private static final int NUM_EVENTS_TO_DISPLAY = 5;

	private List<Event> recentEvents;
	private List<Event> ownedEvents;
	private List<Event> enrolledEvents;
	
	public String execute() throws DAOException {
	    recentEvents = EventService.get().getRecentEvents(NUM_EVENTS_TO_DISPLAY);
		
		// もしログインしていれば、最近のイベントを表示する。
		UserEx user = getLoginUser();
		if (user != null) {
		    ownedEvents = EventService.get().getUnfinishedEventsOwnedBy(user.getId());
	        enrolledEvents = EventService.get().getUnfinishedEnrolledEvents(user.getId());
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
