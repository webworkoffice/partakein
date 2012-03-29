package in.partake.controller.api.account;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.base.TimeUtil;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class GetEnrollmentsAPITest extends APIControllerTest {

    @Test
    public void testGetEnrollments() throws Exception {
        List<String> eventIds = prepareEvents(20);
        prepareEnrollment(DEFAULT_USER_ID, eventIds);

        for (String eventId : eventIds) {
            System.out.println(eventId);
        }

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
            assertThat(array.getJSONObject(i).getJSONObject("event").getString("id"), is(eventIds.get(i)));
            assertThat(array.getJSONObject(i).getString("status"), is("enrolled"));
        }
    }

    private List<String> prepareEvents(int n) throws Exception {
        List<String> ids = new ArrayList<String>();

        Date now = new Date();
        Date late = new Date(now.getTime() + 1000 * 3600);
        String category = EventCategory.getCategories().get(0).getKey();

        for (int i = 0; i < n; ++i) {
            Event event = new Event(null, "short-id", "title", "summary", category,
                    late, late, late, 0, "url", "place",
                    "address", "description", "#hashTag", EVENT_OWNER_ID, EVENT_EDITOR_TWITTER_SCREENNAME,
                    EVENT_FOREIMAGE_ID, EVENT_BACKIMAGE_ID, false, null, false, false,
                    now, now, -1);

            String id = storeEvent(event);
            ids.add(id);
        }
        return ids;
    }

    private List<String> prepareEnrollment(String userId, List<String> eventIds) throws Exception {
        List<String> ids = new ArrayList<String>();
        for (int i = 0; i < eventIds.size(); ++i) {
            String eventId = eventIds.get(i);
            ParticipationStatus status = ParticipationStatus.ENROLLED;
            boolean vip = false;
            ModificationStatus modificationStatus = ModificationStatus.CHANGED;
            AttendanceStatus attendanceStatus = AttendanceStatus.UNKNOWN;
            Date modifiedAt = new Date(TimeUtil.getCurrentTime() + (eventIds.size() - i) * 1000);
            Enrollment enrollment = new Enrollment(null, userId, eventId, "comment", status, vip, modificationStatus, attendanceStatus, modifiedAt);
            ids.add(storeEnrollment(enrollment));
        }
        return ids;
    }
}
