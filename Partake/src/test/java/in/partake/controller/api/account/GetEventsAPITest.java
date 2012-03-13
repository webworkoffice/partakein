package in.partake.controller.api.account;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dto.Event;
import in.partake.model.fixture.TestDataProvider;
import in.partake.service.DBService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.NullValueInNestedPathException;

import com.opensymphony.xwork2.ActionProxy;

public class GetEventsAPITest extends APIControllerTest {
    private static final int N = 20;
    private static List<String> ids = new ArrayList<String>();
    
    @BeforeClass
    public static void setUpOnce() throws Exception {
        APIControllerTest.setUpOnce();
        
        while (ids.size() < N)
            ids.add(UUID.randomUUID().toString());
    }
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        // Create 20 events here.
        PartakeConnection con = DBService.getPool().getConnection();
        try {
            IEventAccess dao = DBService.getFactory().getEventAccess();
            dao.truncate(con);
            
            for (int i = 0; i < N; ++i) {
                dao.put(con, new Event(ids.get(i), "short-id", "title", "summary", "category", 
                        null, new Date(i), null, 0, "url", "place",
                        "address", "description", "#hashTag", TestDataProvider.EVENT_OWNER_ID, TestDataProvider.EVENT_EDITOR_TWITTER_SCREENNAME,
                        null, null, false, null, false, false,
                        new Date(i), new Date(i), -1));
            }
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToGetEventsForOwner() throws Exception {
        ActionProxy proxy = getActionProxy("/api/account/events");
        loginAs(proxy, TestDataProvider.EVENT_OWNER_ID);
        addParameter(proxy, "queryType", "owner");
        
        proxy.execute();
        assertResultOK(proxy);
        
        JSONObject obj = getJSON(proxy);
        assertThat(obj.getInt("numTotalEvents"), is(N));
        assertThat(obj.getJSONArray("eventStatuses"), is(not(nullValue())));
        JSONArray array = obj.getJSONArray("eventStatuses");
        assertThat(array.size(), is(10));
        for (int i = 0; i < array.size(); ++i) {
            JSONObject eventStatus = array.getJSONObject(i);
            JSONObject event = eventStatus.getJSONObject("event");
            assertThat(event.getString("id"), is(ids.get(N - i - 1)));
        }
    }
}
