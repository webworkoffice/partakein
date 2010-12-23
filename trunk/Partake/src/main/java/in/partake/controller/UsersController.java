package in.partake.controller;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.UserPreference;
import in.partake.resource.Constants;
import in.partake.service.CalendarService;
import in.partake.service.EventService;
import in.partake.service.UserService;
import in.partake.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionContext;



public class UsersController extends PartakeActionSupport {
	/** */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(UsersController.class);
	
	// ----------------------------------------------------------------------
    
    public String show() {
    	try {
	    	String userId = getParameter("userId");
	    	if (Util.isEmpty(userId)) { return ERROR; }
	    	    	
	    	UserEx user = UserService.get().getUserExById(userId);
	    	if (user == null) { return NOT_FOUND; }
	    	
	    	UserPreference pref = UserService.get().getUserPreference(userId);
	    	if (pref == null) {
	    	    // TODO: should be logged. something wrong.
	    	    return NOT_FOUND;
	    	}
	    	
	    	if (pref.isProfilePublic()) {
		    	attributes.put(Constants.ATTR_SHOWING_USER, user);	
		    	
		    	try {
		            List<Event> owned = EventService.get().getEventsOwnedBy(user); 
		            List<Event> enrolledEvents = UserService.get().getEnrolledEvents(user);
		            
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
		            
		            ActionContext.getContext().put(Constants.ATTR_OWNED_EVENTSET, owned);
		            ActionContext.getContext().put(Constants.ATTR_ENROLLED_EVENTSET, enrolled);
		            ActionContext.getContext().put(Constants.ATTR_FINISHED_EVENTSET, finished);

		            return SUCCESS;
		        } catch (DAOException e) {
		        	e.printStackTrace();
		        	return ERROR;
		        }
		        
	    	} else {
	    		return PROHIBITED;
	    	}
	    	
    	} catch (DAOException e) {
    		e.printStackTrace();
    		addActionError("データベースエラーです。");
    		return ERROR;
    	}
    }

    /**
     * revoke calendar id. A new calendar id is automatically generated.
     * @return
     */
    public String revokeCalendar() {
    	try {
    		UserEx user = getLoginUser();
    		if (user == null) { return ERROR; }
    		
    		CalendarService.get().revokeCalendar(user);
    		
    		// TODO: Unfortunately, the [user] must be updated to reflect this calendar revocation.
    		// For convenient way, we retrieve user again, and set it to the session.    		
            user = UserService.get().getUserExById(user.getId());
            session.put(Constants.ATTR_USER, user);
    		
    		return SUCCESS;    		
    	} catch (DAOException e) {
    	    logger.warn("revokeCalendar() failed.", e);
    		return ERROR;
    	}
    }
}
