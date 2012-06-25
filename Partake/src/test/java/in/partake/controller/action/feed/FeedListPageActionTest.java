package in.partake.controller.action.feed;

import in.partake.controller.action.ActionControllerTest;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

public class FeedListPageActionTest extends ActionControllerTest {

    @Test
    public void testCalendar() throws Exception {
        ActionProxy proxy = getActionProxy("/feed/");
        proxy.execute();

        assertResultSuccess(proxy, "feedlist.jsp");
    }
}
