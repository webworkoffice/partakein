package in.partake.controller.action.feed;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;

import in.partake.controller.AbstractPartakeControllerTest;

public class FeedListPageActionTest extends AbstractPartakeControllerTest {

    @Test
    public void testCalendar() throws Exception {
        ActionProxy proxy = getActionProxy("/feed/");
        proxy.execute();

        assertResultSuccess(proxy, "feedlist.jsp");
    }
}
