package in.partake.controller;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventCategory;
import in.partake.resource.PartakeProperties;
import in.partake.service.EventService;

import java.util.Collections;
import java.util.Date;
import java.util.List;


public class AdministratorController extends PartakeActionSupport {
	/** */
	private static final long serialVersionUID = 1L;	

    public String index() throws PartakeResultException {
        ensureAdmin();
		return SUCCESS;
	}
	
    public String show() throws PartakeResultException {
        ensureAdmin();
        return SUCCESS;
    }
    
    public String debug() throws PartakeResultException {
        ensureAdmin();
        return SUCCESS;
    }
    
    /**
     * create a demo page if absent.
     * @return
     */
    public String createDemoPage() throws PartakeResultException {
        ensureAdmin();
        
        try {
            Event event = EventService.get().getEventById("demo");
            if (event != null) { return SUCCESS; }
            
            // TODO: あとで直す
            Date date = new Date(2011 - 1900, 12, 30, 0, 0, 0);
            Date now = new Date();
            UserEx owner = getLoginUser();
            List<String> managers = Collections.emptyList(); 
            Event embryo = new Event("demo", "demo", "demo", EventCategory.CATEGORIES.get(0).getKey(), date, date, date, 0,
                            "http://partake.in/", "", "", "demo", "#partake", owner.getId(), managers, false, null, now);
            
            EventService.get().createAsDemo(embryo, null, null);
            
            return SUCCESS;
            
        } catch (DAOException e) {
            e.printStackTrace();
            return ERROR;
        }
    }
    
    /**
     * append a feed id to each event if it does not have feed id. 
     */
    public String addFeedIdToAllEvents() throws PartakeResultException {
        ensureAdmin();
//    	try {
//    		KeyIterator it = EventService.get().getAllEventKeysIterator();
//    		while (it.hasNext()) {
//    			Event event = EventService.get().getEventById(it.next());
//    			if (event.getFeedId() == null) {
//    				EventService.get().appendFeed(event.getId());
//    			}
//    		}
//    		
//    		return SUCCESS;
//    	} catch (DAOException e) {
//    		e.printStackTrace();
//    		return ERROR;
//    	}
        return SUCCESS;
    }
    
    // ----------------------------------------------------------------------
    
    private void ensureAdmin() throws PartakeResultException {
        UserEx user = getLoginUser();
        if (user == null) {
            throw new PartakeResultException(PROHIBITED);
        }
        
        if (!PartakeProperties.get().getTwitterAdminName().equals(user.getScreenName())) {
            throw new PartakeResultException(PROHIBITED);
        }     
    }
}
