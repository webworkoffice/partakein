package in.partake.page.feed;

import in.partake.controller.CalendarsController;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.page.base.PartakeCalendarPage;
import in.partake.resource.I18n;
import in.partake.service.EventService;
import in.partake.util.functional.Function;

import java.io.IOException;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

import org.apache.log4j.Logger;

public class CalendarAllEventPage extends PartakeCalendarPage {
    /** */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(CalendarsController.class);
        
    public CalendarAllEventPage() {
        try {
            Calendar calendar = createCalendarSkeleton();
            
            class F implements Function<Event, Void> {
                private Calendar calendar;
                
                public F(Calendar calendar) {
                    this.calendar = calendar;
                }
                
                public Void apply(Event event) {
                    if (event == null) { return null; }
                    if (event.isPrivate()) { return null; } // private calendar should not be displayed.
                    addToCalendar(calendar, event);
                    return null;
                }
            }
            
            EventService.get().applyForAllEvents(new F(calendar));
            renderCalendar(calendar);
            
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            renderDBError();
        } catch (IOException e) {
            logger.error("IOException occured.", e);
            renderError("IOException occured.");
        } catch (ValidationException e) {
            logger.error("Calendar Validation Exception occured.", e);
            renderError("Calendar Validation Exception occured.");
        }
    }
}
