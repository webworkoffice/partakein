package in.partake.controller.action.calendar;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import in.partake.controller.action.ActionControllerTest;
import in.partake.model.fixture.TestDataProvider;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.VEvent;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class AllCalendarTest extends ActionControllerTest {

    @Test
    public void testCalendar() throws Exception {
        ActionProxy proxy = getActionProxy("/calendars/all");
        proxy.execute();

        AllCalendarAction action = (AllCalendarAction) proxy.getAction();
        assertThat(action.getContentType(), is("text/calendar; charset=utf-8"));
        assertThat(action.getContentDisposition(), is("inline"));

        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(new ByteArrayInputStream(response.getContentAsByteArray()));

        ComponentList list = calendar.getComponents(Component.VEVENT);
        List<String> uids = new ArrayList<String>();
        for (Object obj : list) {
            VEvent vEvent = (VEvent) obj;
            uids.add(vEvent.getUid().getValue());
        }

        assertThat(uids, hasItem(TestDataProvider.DEFAULT_EVENT_ID));
        assertThat(uids, not(hasItem(TestDataProvider.PRIVATE_EVENT_ID)));
   }
}
