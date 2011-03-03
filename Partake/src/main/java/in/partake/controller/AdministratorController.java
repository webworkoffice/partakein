package in.partake.controller;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.resource.I18n;
import in.partake.resource.PartakeProperties;
import in.partake.service.EventService;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.log4j.Logger;


public class AdministratorController extends PartakeActionSupport {
	/** */
	private static final long serialVersionUID = 1L;	
	private static final Logger logger = Logger.getLogger(AdministratorController.class);
	
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
            
            Date date = createDemoEventDate();
            Date now = new Date();
            UserEx owner = getLoginUser();
            Event embryo = new Event("demo", "demo", "demo", EventCategory.CATEGORIES.get(0).getKey(), date, date, date, 0,
                            "http://partake.in/", "", "", "demo", "#partake", owner.getId(), null, false, null, false, false, now, null);
            
            EventService.get().createAsDemo(embryo, null, null);
            
            return SUCCESS;
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            return ERROR;
        }
    }

	private Date createDemoEventDate() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("JST"), Locale.JAPANESE);

		calendar.clear();
		calendar.set(Calendar.YEAR, 2011);
		calendar.set(Calendar.MONTH, 12 - 1);
		calendar.set(Calendar.DAY_OF_MONTH, 30);

		return calendar.getTime();
	}
    
    /**
     * append a feed id to each event if it does not have feed id. 
     */
    public String addFeedIdToAllEvents() throws PartakeResultException {
    	logger.info("addFeedIdToAllEvents() is called");
    	
        ensureAdmin();
    	try {
    	    EventService.get().addFeedIdToAllEvents();
    		return SUCCESS;
    	} catch (DAOException e) {
    	    logger.error(I18n.t(I18n.DATABASE_ERROR), e);
    		return ERROR;
    	}
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
