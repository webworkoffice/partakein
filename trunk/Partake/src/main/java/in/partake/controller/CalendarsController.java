package in.partake.controller;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dto.Event;
import in.partake.model.dto.User;
import in.partake.service.UserService;
import in.partake.util.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.Version;


public class CalendarsController extends PartakeActionSupport {
	/** */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(CalendarsController.class);
    private ByteArrayInputStream inputStream = null;
	
	public ByteArrayInputStream getInputStream() {
        return inputStream;
    }
	
    // カレンダーの表示
    public String show() {
    	String calendarId = getParameter("calendarId");
    	if (Util.isEmpty(calendarId)) { return ERROR; }
    	
    	try {
    	    // TODO: これは CalendarService.get().getEnrolledEventsByCalendarId 的ななにかにしなければならない。
    	    User user = UserService.get().getUserFromCalendarId(calendarId);
    	    if (user == null) { return NOT_FOUND; }
    		
    		// user に関連する ics を生成して返す。
    		// TODO: why not cache?
    		Calendar calendar = new Calendar();
    		calendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
    		calendar.getProperties().add(Version.VERSION_2_0);
    		calendar.getProperties().add(CalScale.GREGORIAN);
    		
    		// set timezone
    		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
    		TimeZone timezone = registry.getTimeZone("Asia/Tokyo");
    		if (timezone == null) {
    			logger.warn("timezone is null.");
    		} else {
	    		VTimeZone tz = timezone.getVTimeZone();
	    		calendar.getComponents().add(tz);    			
    		}
    		
    		// for all events the user will participate ...
    		DataIterator<Event> it = UserService.get().getEnrolledEvents(user);
    		while (it.hasNext()) {
    			Event event = it.next();
    			if (event == null) { continue; }
    			
				DateTime beginDate = new DateTime(event.getBeginDate().getTime());

				VEvent vEvent;
    			if (event.getEndDate() != null) {
        			DateTime endDate = new DateTime(event.getEndDate().getTime());
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
    			
    			calendar.getComponents().add(vEvent);
    		}
    		
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		new CalendarOutputter().output(calendar, baos);
    		byte[] data = baos.toByteArray();
    		
    		// TODO: input stream の部分はもっときれいにならないかなー
    		inputStream = new ByteArrayInputStream(data);
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
}
