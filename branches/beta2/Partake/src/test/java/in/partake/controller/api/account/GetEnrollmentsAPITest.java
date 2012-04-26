package in.partake.controller.api.account;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.model.dto.auxiliary.EventRelation;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.dto.auxiliary.TicketType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class GetEnrollmentsAPITest extends APIControllerTest {

    @Test
    public void testGetEnrollments() throws Exception {
        String eventId = UUID.randomUUID().toString();
        List<UUID> ticketIds = prepareEvents(20);
        prepareEnrollment(DEFAULT_USER_ID, ticketIds, eventId);

        ActionProxy proxy = getActionProxy("/api/account/enrollments");
        loginAs(proxy, DEFAULT_USER_ID);
        addParameter(proxy, "limit", "10");

        proxy.execute();
        assertResultOK(proxy);

        JSONObject obj = getJSON(proxy);
        assertThat(obj.getInt("numTotalEvents"), is(20));

        JSONArray array = obj.getJSONArray("eventStatuses");
        assertThat(array.size(), is(10));
        for (int i = 0; i < 10; ++i) {
            assertThat(array.getJSONObject(i).getJSONObject("event").getString("id"), is(ticketIds.get(i).toString()));
            assertThat(array.getJSONObject(i).getString("status"), is("enrolled"));
        }
    }

    private List<UUID> prepareEvents(int n) throws Exception {
        List<UUID> ids = new ArrayList<UUID>();

        DateTime now = TimeUtil.getCurrentDateTime();
        DateTime late = new DateTime(now.getTime() + 1000 * 3600);
        String category = EventCategory.getCategories().get(0).getKey();

        for (int i = 0; i < n; ++i) {
            Event event = new Event(null, "title", "summary", category,
                    late, late, "url", "place",
                    "address", "description", "#hashTag", EVENT_OWNER_ID, EVENT_EDITOR_TWITTER_SCREENNAME,
                    EVENT_FOREIMAGE_ID, EVENT_BACKIMAGE_ID, null, false, new ArrayList<EventRelation>(), null,
                    now, now, -1);
            String eventId = storeEvent(event);

            UUID ticketId = UUID.randomUUID();
            EventTicket ticket = new EventTicket(ticketId, eventId, "Free", TicketType.FREE_TICKET, 0, null, null, TimeUtil.getCurrentDateTime(), null);
            storeEventTicket(ticket);

            ids.add(ticketId);
        }
        return ids;
    }

    private List<String> prepareEnrollment(String userId, List<UUID> ticketIds, String eventId) throws Exception {
        List<String> ids = new ArrayList<String>();
        for (int i = 0; i < ticketIds.size(); ++i) {
            UUID ticketId = ticketIds.get(i);
            ParticipationStatus status = ParticipationStatus.ENROLLED;
            boolean vip = false;
            ModificationStatus modificationStatus = ModificationStatus.CHANGED;
            AttendanceStatus attendanceStatus = AttendanceStatus.UNKNOWN;
            DateTime modifiedAt = new DateTime(TimeUtil.getCurrentTime() + (ticketIds.size() - i) * 1000);
            Enrollment enrollment = new Enrollment(null, userId, ticketId, eventId, "comment", status, vip, modificationStatus, attendanceStatus, modifiedAt);
            ids.add(storeEnrollment(enrollment));
        }
        return ids;
    }
}
