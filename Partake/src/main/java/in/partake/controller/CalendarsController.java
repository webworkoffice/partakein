package in.partake.controller;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.KeyIterator;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventCategory;
import in.partake.model.dto.User;
import in.partake.resource.I18n;
import in.partake.service.EventService;
import in.partake.service.UserService;
import in.partake.util.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.Version;


public class CalendarsController extends PartakeActionSupport {
	/** */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(CalendarsController.class);
        
    private static final TimeZone JST_TIMEZONE;
    static {
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        JST_TIMEZONE = registry.getTimeZone("Asia/Tokyo");    	
    }
    
    private ByteArrayInputStream inputStream = null;
    
	public ByteArrayInputStream getInputStream() {
        return inputStream;
    }
	
	// 全てのイベントのカレンダーの表示
	// TODO: cache!
	public String all() {
	    return showByCategory("all");
	}
	
	// TODO: cache!
	public String showCategory() {
	    String categoryName = getParameter("category");
	    if (StringUtils.isEmpty(categoryName)) { return NOT_FOUND; }	    
	    if (!EventCategory.isValidCategoryName(categoryName)) { return NOT_FOUND; }
	    
	    return showByCategory(categoryName);
	}
	
	private String showByCategory(String categoryName) {
	    assert(!StringUtils.isEmpty(categoryName));
	    
        try {
            Calendar calendar = createCalendarSkeleton();
            
            KeyIterator it = EventService.get().getAllEventKeysIterator();
            while (it.hasNext()) {
                String eventId = it.next();
                Event event = EventService.get().getEventById(eventId);
                if (event == null) { continue; }
                if (event.isPrivate()) { continue; } // private calendar should not be displayed.
                if (!"all".equals(categoryName) && !categoryName.equals(event.getCategory())) { continue; }
                addToCalendar(calendar, event);
            }
            
            outputCalendar(calendar);
            return SUCCESS;
            
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            addActionError(I18n.t(I18n.DATABASE_ERROR));
        } catch (IOException e) {
            logger.error("IOException occured.", e);
        } catch (ValidationException e) {
            logger.error("Calendar Validation Exception occured.", e);
        }
        
        return ERROR;
	}
	
    // カレンダーの表示
	// user に関連する ics を生成して返す。
	// TODO: why not cache?
    public String show() {
    	String calendarId = getParameter("calendarId");
    	if (Util.isEmpty(calendarId)) { return ERROR; }
    	
    	try {
    	    // TODO: これは CalendarService.get().getEnrolledEventsByCalendarId 的ななにかにしなければならない。
    	    User user = UserService.get().getUserFromCalendarId(calendarId);
    	    if (user == null) { return NOT_FOUND; }
    		
    		Calendar calendar = createCalendarSkeleton();
    		
    		// for all events the user will participate ...
    		List<Event> enrolledEvents = UserService.get().getEnrolledEvents(user);
    		for (Event event : enrolledEvents) {
    			if (event == null) { continue; }
    			addToCalendar(calendar, event);
    		}
    		
    		outputCalendar(calendar);
    		return SUCCESS;
    		
    	} catch (DAOException e) {
    		e.printStackTrace();
    		return ERROR;
    	} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return ERROR;
		} catch (IOException e) {
			e.printStackTrace();
			return ERROR;
		} catch (ValidationException e) {
			e.printStackTrace();
			return ERROR;
		}
    }

    private Calendar createCalendarSkeleton() {
        Calendar calendar = new Calendar();
        
        calendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        calendar.getComponents().add(JST_TIMEZONE.getVTimeZone());
        
        return calendar;
    }
    
    private void addToCalendar(Calendar calendar, Event event) {
        DateTime beginDate = new DateTime(event.getBeginDate().getTime());
        beginDate.setTimeZone(JST_TIMEZONE);

        VEvent vEvent;
        if (event.getEndDate() != null) {
            DateTime endDate = new DateTime(event.getEndDate().getTime());
            endDate.setTimeZone(JST_TIMEZONE);
            vEvent = new VEvent(beginDate, endDate, event.getTitle());
        } else {
            vEvent = new VEvent(beginDate, event.getTitle());
        }

        // set unique identifier
        vEvent.getProperties().add(new Uid(event.getId()));

        // Description
        vEvent.getProperties().add(new Description(event.getEventURL()));
        
        // URL
        if (event.getUrl() != null && !event.getUrl().isEmpty()) {
            try {
                vEvent.getProperties().add(new Url(new URI(event.getUrl())));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        
        // modified
        DateTime modifiedAt = null;
        if (event.getModifiedAt() != null) {
        	modifiedAt = new DateTime(event.getModifiedAt());
        } else if (event.getCreatedAt() != null){
        	modifiedAt = new DateTime(event.getCreatedAt());
        }
        if (modifiedAt != null) {
	        modifiedAt.setTimeZone(JST_TIMEZONE);
	        vEvent.getProperties().add(new LastModified(modifiedAt));
        }        
        
        // sequence
       	vEvent.getProperties().add(new Sequence(event.getRevision()));
        
        calendar.getComponents().add(vEvent);
    }
    
    private void outputCalendar(Calendar calendar) throws IOException, ValidationException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new CalendarOutputter().output(calendar, baos);
        byte[] data = baos.toByteArray();
        
        // TODO: input stream の部分はもっときれいにならないかなー
        inputStream = new ByteArrayInputStream(data);
    }
}
