package in.partake.controller.api.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.resource.UserErrorCode;
import in.partake.service.EventService;

import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;

import net.sf.json.JSONObject;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;


public class SearchActionTest extends APIControllerTest {
    private static final String SEARCH_QUERY = "あ";
    @Test
    public void testSearchEventAllCategory() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        assertThat(proxy.execute(), equalTo("json"));

        assertResultOK(proxy);
        JSONObject json = getJSON(proxy);
        assertThat(json.containsKey("reason"), equalTo(false));
    }

    // =========================================================================
    // beforeDeadlineOnly
    @Test
    public void testSearchEventBeforeDeadlineOnly() throws Exception {
        storeEventAfterDeadline();
        storeEventBeforeDeadline();

        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "beforeDeadlineOnly", null);
        assertThat(proxy.execute(), equalTo("json"));

        assertResultOK(proxy);
        JSONObject json = getJSON(proxy);
        DateFormat format = createDateFormat();
        Date now = new Date();
        boolean findEvents = false;
        for (@SuppressWarnings("unchecked") Iterator<JSONObject> iter = json.getJSONArray("events").iterator(); iter.hasNext();) {
            JSONObject event = iter.next();
            String deadlineAsString = event.getString("deadline");
            assertThat("締め切り後のイベントが見つかってしまいました", format.parse(deadlineAsString), is(greaterThan(now)));
            findEvents = true;
        }
        assertTrue("見つかるはずのイベントが見つかりませんでした", findEvents);
    }

    @Test
    public void testSearchEventIncludeAfterDeadline() throws Exception {
        storeEventAfterDeadline();
        storeEventBeforeDeadline();

        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "beforeDeadlineOnly", "false");
        assertThat(proxy.execute(), equalTo("json"));

        assertResultOK(proxy);
        JSONObject json = getJSON(proxy);
        DateFormat format = createDateFormat();
        Date now = new Date();
        boolean findEventWhichIsAfterDeadline = false;
        boolean findEventWhichIsBeforeDeadline = false;
        for (@SuppressWarnings("unchecked")Iterator<JSONObject> iter = json.getJSONArray("events").iterator(); iter.hasNext();) {
            JSONObject event = iter.next();
            String deadlineAsString = event.getString("deadline");
            if(format.parse(deadlineAsString).before(now)) {
                findEventWhichIsAfterDeadline = true;
            } else {
                findEventWhichIsBeforeDeadline = true;
            }
        }
        assertTrue("見つかるはずのイベントが見つかりませんでした", findEventWhichIsAfterDeadline);
        assertTrue("見つかるはずのイベントが見つかりませんでした", findEventWhichIsBeforeDeadline);
    }

    /**
     * beforeDeadlineOnlyを省略した場合はtrueであるとみなす
     */
    @Test
    public void testSearchEventBeforeDeadlineOnlyEmpty() throws Exception {
        storeEventAfterDeadline();
        storeEventBeforeDeadline();

        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "beforeDeadlineOnly", null);
        assertThat(proxy.execute(), equalTo("json"));

        assertResultOK(proxy);
        JSONObject json = getJSON(proxy);
        DateFormat format = createDateFormat();
        Date now = new Date();
        boolean findEvents = false;
        for (@SuppressWarnings("unchecked")Iterator<JSONObject> iter = json.getJSONArray("events").iterator(); iter.hasNext();) {
            JSONObject event = iter.next();
            String deadlineAsString = event.getString("deadline");
            assertThat("締め切り後のイベントが見つかってしまいました", format.parse(deadlineAsString), is(greaterThan(now)));
            findEvents = true;
        }
        assertTrue("見つかるはずのイベントが見つかりませんでした", findEvents);
    }
    /**
     * beforeDeadlineOnlyにtrueでもfalseでもない値が渡されたら引数が異常としてエラー
     */
    @Test
    public void testSearchEventIllegalBeforeDeadlineOnly() throws Exception {
        storeEventAfterDeadline();
        storeEventBeforeDeadline();

        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "beforeDeadlineOnly", "(´・ω・`)");
        assertThat(proxy.execute(), equalTo("json"));

        assertResultInvalid(proxy);
        JSONObject json = getJSON(proxy);
        assertThat(json.getString("reason"), equalTo(UserErrorCode.INVALID_SEARCH_DEADLINE.getReasonString()));
    }

    private Event createEvent() {
        Event event = new Event();
        event.setTitle(SEARCH_QUERY);
        event.setSummary(SEARCH_QUERY);
        event.setDescription(SEARCH_QUERY);
        event.setBeginDate(new Date(0L));
        event.setCategory("neta");
        event.setCreatedAt(new Date(0L));
        event.setPrivate(false);	// privateイベントは検索の対象にならないので公開イベントとして作成
        return event;
    }

    private Event storeEventAfterDeadline() throws DAOException {
        Event event = createEvent();
        event.setDeadline(new Date(0L));
        EventService.get().create(event, null, null);
        return event;
    }

    private Event storeEventBeforeDeadline() throws DAOException {
        Event event = createEvent();
        Date tomorrow = new Date(System.currentTimeMillis() + 24L * 60L * 60L * 1000L);
        event.setDeadline(tomorrow);
        EventService.get().create(event, null, null);
        return event;
    }

    // =========================================================================
    // maxNum
    @Test
    public void testSearchEventTooLargeMaxNum() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "maxNum", "10000");
        assertThat(proxy.execute(), equalTo("json"));

        assertResultInvalid(proxy);
        JSONObject json = getJSON(proxy);
        assertThat(json.getString("reason"), equalTo(UserErrorCode.INVALID_SEARCH_MAXNUM.getReasonString()));
    }

    @Test
    public void testSearchEventMissingMaxNum() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "maxNum", null);
        assertThat(proxy.execute(), equalTo("json"));

        assertResultInvalid(proxy);
        JSONObject json = getJSON(proxy);
        assertThat(json.getString("reason"), equalTo(UserErrorCode.MISSING_SEARCH_MAXNUM.getReasonString()));
    }

    @Test
    public void testSearchEventEmptyMaxNum() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "maxNum", "");
        assertThat(proxy.execute(), equalTo("json"));

        assertResultInvalid(proxy);
        JSONObject json = getJSON(proxy);
        assertThat(json.getString("reason"), equalTo(UserErrorCode.INVALID_SEARCH_MAXNUM.getReasonString()));
    }

    @Test
    public void testSearchEventNotNumberMaxNum() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "maxNum", "m(_ _)m");
        assertThat(proxy.execute(), equalTo("json"));

        assertResultInvalid(proxy);
        JSONObject json = getJSON(proxy);
        assertThat(json.getString("reason"), equalTo(UserErrorCode.INVALID_SEARCH_MAXNUM.getReasonString()));
    }

    @Test
    public void testSearchEventNotLowerCamelMaxNum() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "maxNum", null);
        addParameter(proxy, "maxnum", "10");
        assertThat(proxy.execute(), equalTo("json"));

        assertResultInvalid(proxy);
        JSONObject json = getJSON(proxy);
        assertThat(json.getString("reason"), equalTo(UserErrorCode.MISSING_SEARCH_MAXNUM.getReasonString()));
    }

    // =========================================================================
    // utility
    private void addBasicParameter(ActionProxy proxy) throws DAOException {
        addParameter(proxy, "query", SEARCH_QUERY);
        addParameter(proxy, "category", "all");
        addParameter(proxy, "beforeDeadlineOnly", "true");
        addParameter(proxy, "sortOrder", "score");
        addParameter(proxy, "maxNum", "10");
	}
}
