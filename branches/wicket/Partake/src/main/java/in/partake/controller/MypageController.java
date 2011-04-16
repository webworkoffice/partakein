package in.partake.controller;

import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.User;
import in.partake.resource.Constants;
import in.partake.service.EventService;
import in.partake.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;


/**
 * Servlet implementation class UserMypageController
 */
public class MypageController extends PartakeActionSupport implements SessionAware {
	/** */
	private static final long serialVersionUID = 1L;
	// private static final Logger logger = Logger.getLogger(MypageController.class);
	
	public String show() {
		User user = getLoginUser();
		if (user == null) { return LOGIN; }

        try {
            List<Event> ownedEvents = EventService.get().getEventsOwnedBy(user);
            List<Event> enrolledEvents = UserService.get().getEnrolledEvents(user.getId());
            
            List<Event> enrolled = new ArrayList<Event>();
            List<Event> finished = new ArrayList<Event>();
            Date now = new Date();
            for (Event e : enrolledEvents) {
            	if (e == null) { continue; }
            	if (e.getBeginDate().before(now)) {
            		finished.add(e);
            	} else {
            		enrolled.add(e);
            	}
            }
            
            Collections.sort(enrolled, Event.getComparatorBeginDateAsc());
            Collections.sort(finished, Event.getComparatorBeginDateAsc());
            
            ActionContext.getContext().put(Constants.ATTR_OWNED_EVENTSET, ownedEvents);
            ActionContext.getContext().put(Constants.ATTR_ENROLLED_EVENTSET, enrolled);
            ActionContext.getContext().put(Constants.ATTR_FINISHED_EVENTSET, finished);

            return SUCCESS;
        } catch (DAOException e) {
        	e.printStackTrace();
        	return ERROR;
        }
	}
}