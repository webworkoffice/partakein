package in.partake.controller.api.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.fixture.impl.EventTestDataProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;

import net.sf.json.JSONObject;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;


public class SearchAPITest extends APIControllerTest {
    private static final String SEARCH_QUERY = "あ";

    @Test
    public void testSearchEventAllCategory() throws Exception {
        storeEventBeforeDeadline();
        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        assertThat(proxy.execute(), equalTo("json"));

        assertResultOK(proxy);
        JSONObject json = getJSON(proxy);
        assertPublicEventsAreFound(json);
        assertThat(json.containsKey("reason"), equalTo(false));
    }

    @Test
    public void testSearchJapaneseQuery() throws Exception {
        // http://code.google.com/p/partakein/issues/detail?id=233
        ActionProxy proxy = getActionProxy("/api/event/search");
        addQueryParameter(proxy, EventTestDataProvider.JAPANESE_IDENTIFIER);

        assertThat(proxy.execute(), equalTo("json"));

        assertResultOK(proxy);
        JSONObject json = getJSON(proxy);
        assertPublicEventsAreFound(json);
        assertThat(json.containsKey("reason"), equalTo(false));
    }

    @Test
    public void testSearchJapaneseQueryWithBeforeDeadlineOnly() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addQueryParameter(proxy, EventTestDataProvider.JAPANESE_IDENTIFIER, "all", "false", "score", "10");

        assertThat(proxy.execute(), equalTo("json"));

        assertResultOK(proxy);
        JSONObject json = getJSON(proxy);
        assertPublicEventsAreFound(json);
        assertThat(json.containsKey("reason"), equalTo(false));

    }

    @Test
    public void testSearchEventWithoutQuery() throws Exception {
        storeEventBeforeDeadline();
        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "query", null);
        assertThat(proxy.execute(), equalTo("json"));

        assertResultOK(proxy);
        JSONObject json = getJSON(proxy);
        assertThat(json.containsKey("reason"), equalTo(false));
        assertPublicEventsAreFound(json);
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
        assertOnlyBeforeDeadlineAreFound(json);
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
        assertOnlyBeforeDeadlineAreFound(json);
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

        assertResultSuccess(proxy);
    }

    private Event createEvent() {
        Event event = PartakeApp.getTestService().getTestDataProviderSet().getEventProvider().create();
        event.setTitle(SEARCH_QUERY);
        event.setSummary(SEARCH_QUERY);
        event.setDescription(SEARCH_QUERY);
        event.setBeginDate(new Date(0L));
        event.setCategory("neta");
        event.setCreatedAt(new Date(0L));
        event.setPrivate(false);	// privateイベントは検索の対象にならないので公開イベントとして作成
        return event;
    }

    private Event storeEventAfterDeadline() throws DAOException, PartakeException {
        final Event event = createEvent();
        event.setDeadline(new Date(0L));

        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                EventDAOFacade.create(con, daos, event);
                return null;
            }
        }.execute();

        PartakeApp.getEventSearchService().create(event);
        return event;
    }

    private Event storeEventBeforeDeadline() throws DAOException, PartakeException {
        final Event event = createEvent();
        Date tomorrow = new Date(System.currentTimeMillis() + 24L * 60L * 60L * 1000L);
        event.setDeadline(tomorrow);

        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                EventDAOFacade.create(con, daos, event);
                return null;
            }
        }.execute();

        PartakeApp.getEventSearchService().create(event);
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

        assertResultSuccess(proxy);
    }

    /**
     * maxNum省略時はデフォルト（10）と解釈する
     * @throws Exception
     */
    @Test
    public void testSearchEventMissingMaxNum() throws Exception {
        for (int i = 1; i <= 11; ++i) {
            storeEventBeforeDeadline();
        }
        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "maxNum", null);
        assertThat(proxy.execute(), equalTo("json"));

        assertResultOK(proxy);
        JSONObject json = getJSON(proxy);
        assertThat(json.containsKey("reason"), equalTo(false));
        assertThat(json.getJSONArray("events").size(), equalTo(10));
    }

    @Test
    public void testSearchEventEmptyMaxNum() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "maxNum", "");
        assertThat(proxy.execute(), equalTo("json"));

        assertResultSuccess(proxy);
    }

    @Test
    public void testSearchEventNotNumberMaxNum() throws Exception {
        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "maxNum", "m(_ _)m");
        assertThat(proxy.execute(), equalTo("json"));

        assertResultSuccess(proxy);
    }

    /**
     * 大文字小文字間違えて指定した場合はデフォルト値が有効になる
     */
    @Test
    public void testSearchEventNotLowerCamelMaxNum() throws Exception {
        for (int i = 1; i <= 11; ++i) {
            storeEventBeforeDeadline();
        }

        ActionProxy proxy = getActionProxy("/api/event/search");
        addBasicParameter(proxy);
        addParameter(proxy, "maxNum", null);
        addParameter(proxy, "maxnum", "10");
        assertThat(proxy.execute(), equalTo("json"));

        assertResultOK(proxy);
        JSONObject json = getJSON(proxy);
        assertThat(json.containsKey("reason"), equalTo(false));
        assertThat(json.getJSONArray("events").size(), equalTo(10));
    }

    // =========================================================================
    // utility

    /**
     * queryString を入力としてセットする。
     * @param proxy
     * @param queryString
     */
    // TODO: なんで DAOException が必要？
    private void addQueryParameter(ActionProxy proxy, String queryString) throws DAOException {
        addParameter(proxy, "query", queryString);
        addParameter(proxy, "category", "all");
        addParameter(proxy, "beforeDeadlineOnly", "true");
        addParameter(proxy, "sortOrder", "score");
        addParameter(proxy, "maxNum", "10");
    }

    private void addQueryParameter(ActionProxy proxy, String queryString, String category, String beforeDeadlineOnly, String sortOrder, String maxNum) throws DAOException {
        addParameter(proxy, "query", queryString);
        addParameter(proxy, "category", category);
        addParameter(proxy, "beforeDeadlineOnly", beforeDeadlineOnly);
        addParameter(proxy, "sortOrder", sortOrder);
        addParameter(proxy, "maxNum", maxNum);
    }

    private void addBasicParameter(ActionProxy proxy) throws DAOException {
        addParameter(proxy, "query", SEARCH_QUERY);
        addParameter(proxy, "category", "all");
        addParameter(proxy, "beforeDeadlineOnly", "true");
        addParameter(proxy, "sortOrder", "score");
        addParameter(proxy, "maxNum", "10");
    }

    private void assertPublicEventsAreFound(JSONObject json) {
        boolean findEvents = false;
        for (@SuppressWarnings("unchecked") Iterator<JSONObject> iter = json.getJSONArray("events").iterator(); iter.hasNext();) {
            iter.next();
            findEvents = true;
        }
        assertTrue(findEvents);
    }

    private void assertOnlyBeforeDeadlineAreFound(JSONObject json) throws ParseException {
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
}
