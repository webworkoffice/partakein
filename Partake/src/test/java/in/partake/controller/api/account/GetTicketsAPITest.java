package in.partake.controller.api.account;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.UserTicket;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.model.dto.auxiliary.EventRelation;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class GetTicketsAPITest extends APIControllerTest {

    @Test
    public void testGetEnrollments() throws Exception {
        String eventId = prepareEvent();
        List<UUID> ticketIds = prepareEventTickets(20, eventId);
        prepareUserTickets(DEFAULT_USER_ID, ticketIds, eventId);

        ActionProxy proxy = getActionProxy("/api/account/tickets");
        loginAs(proxy, DEFAULT_USER_ID);
        addParameter(proxy, "limit", "10");

        proxy.execute();
        assertResultOK(proxy);

        JSONObject obj = getJSON(proxy);
        assertThat(obj.getInt("totalTicketCount"), is(20));

        JSONArray array = obj.getJSONArray("ticketStatuses");
        assertThat(array.size(), is(10));
        for (int i = 0; i < 10; ++i) {
            assertThat(array.getJSONObject(i).getJSONObject("ticket").getString("id"), is(ticketIds.get(i).toString()));
            assertThat(array.getJSONObject(i).getString("status"), is("enrolled"));
        }
    }

    private String prepareEvent() throws Exception {
        DateTime now = TimeUtil.getCurrentDateTime();
        DateTime late = new DateTime(now.getTime() + 1000 * 3600);
        String category = EventCategory.getCategories().get(0).getKey();

        Event event = new Event(null, "title", "summary", category,
                late, late, "url", "place",
                "address", "description", "#hashTag", EVENT_OWNER_ID, EVENT_EDITOR_TWITTER_SCREENNAME,
                EVENT_FOREIMAGE_ID, EVENT_BACKIMAGE_ID, null, false, new ArrayList<EventRelation>(), null,
                now, now, -1);
        return storeEvent(event);
    }

    private List<UUID> prepareEventTickets(int n, String eventId) throws Exception {
        List<UUID> ids = new ArrayList<UUID>();

        for (int i = 0; i < n; ++i) {
            UUID ticketId = UUID.randomUUID();
            EventTicket ticket = EventTicket.createDefaultTicket(ticketId, eventId);
            storeEventTicket(ticket);

            ids.add(ticketId);
        }
        return ids;
    }

    private List<String> prepareUserTickets(String userId, List<UUID> ticketIds, String eventId) throws Exception {
        List<String> ids = new ArrayList<String>();
        for (int i = 0; i < ticketIds.size(); ++i) {
            UUID ticketId = ticketIds.get(i);
            ParticipationStatus status = ParticipationStatus.ENROLLED;
            boolean vip = false;
            ModificationStatus modificationStatus = ModificationStatus.CHANGED;
            AttendanceStatus attendanceStatus = AttendanceStatus.UNKNOWN;
            DateTime modifiedAt = new DateTime(TimeUtil.getCurrentTime() + (ticketIds.size() - i) * 1000);
            UserTicket enrollment = new UserTicket(null, userId, ticketId, eventId, "comment", status, vip, modificationStatus, attendanceStatus, modifiedAt);
            ids.add(storeEnrollment(enrollment));
        }
        return ids;
    }
}
