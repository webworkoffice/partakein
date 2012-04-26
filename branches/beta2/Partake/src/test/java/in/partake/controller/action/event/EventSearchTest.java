package in.partake.controller.action.event;

import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.controller.action.event.EventSearchAction;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

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
    public void testLoginIsNotRequired() throws Exception {
        ActionProxy proxy = getActionProxy("/events/search");
        EventSearchAction controller = (EventSearchAction) proxy.getAction();
        Map<String, Object> requestMap = new HashMap<String, Object>(request.getParameterMap());
        controller.setRequest(requestMap);
        
        proxy.execute();
        assertResultSuccess(proxy);        
    }

    @Test
    public void testToUseUnknownCategory() throws Exception {
        request.setParameter("category", "unknown");
        ActionProxy proxy = getActionProxy("/events/search");
        EventSearchAction controller = (EventSearchAction) proxy.getAction();
        Map<String, Object> requestMap = new HashMap<String, Object>(request.getParameterMap());
        controller.setRequest(requestMap);

        proxy.execute();
        assertResultSuccess(proxy);
    }

    /**
     * 存在しないソート順を指定して検索した場合、スコア順にソートされて返却される
     */
    @Test	
    public void testToUseUnknownSortOrder() throws Exception {
        request.setParameter("sortOrder", "unknown");
        ActionProxy proxy = getActionProxy("/events/search");
        EventSearchAction controller = (EventSearchAction) proxy.getAction();
        Map<String, Object> requestMap = new HashMap<String, Object>(request.getParameterMap());
        controller.setRequest(requestMap);
        
        proxy.execute();
        assertResultSuccess(proxy);
        
        // TODO スコア順にソートされていることを確認        
    }
}
