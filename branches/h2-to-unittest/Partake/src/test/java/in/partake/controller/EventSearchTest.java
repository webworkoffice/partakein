package in.partake.controller;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;

public class EventSearchTest extends AbstractPartakeControllerTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        setDefaultParams();
    }

    private void setDefaultParams() {
        request.setParameter("searchTerm", "term");
        request.setParameter("category", "all");
        request.setParameter("sortOrder", "score");
    }

    @Test
    public void testLoginIsNotRequired() {
        ActionProxy proxy = getActionProxy("/events/search");
        EventsSearchController controller = (EventsSearchController) proxy.getAction();
        Map<String, Object> requestMap = new HashMap<String, Object>(request.getParameterMap());
        controller.setRequest(requestMap);
        Assert.assertEquals(Action.SUCCESS, controller.search());
    }

    @Test
    public void testToUseUnknownCategory() {
        request.setParameter("category", "unknown");
        ActionProxy proxy = getActionProxy("/events/search");
        EventsSearchController controller = (EventsSearchController) proxy.getAction();
        Map<String, Object> requestMap = new HashMap<String, Object>(request.getParameterMap());
        controller.setRequest(requestMap);
        Assert.assertEquals(Action.INPUT, controller.search());
    }

    /**
     * 存在しないソート順を指定して検索した場合、スコア順にソートされて返却される
     */
    @Test	
    public void testToUseUnknownSortOrder() {
        request.setParameter("sortOrder", "unknown");
        ActionProxy proxy = getActionProxy("/events/search");
        EventsSearchController controller = (EventsSearchController) proxy.getAction();
        Map<String, Object> requestMap = new HashMap<String, Object>(request.getParameterMap());
        controller.setRequest(requestMap);
        Assert.assertEquals(Action.SUCCESS, controller.search());
        // TODO スコア順にソートされていることを確認
    }
}
