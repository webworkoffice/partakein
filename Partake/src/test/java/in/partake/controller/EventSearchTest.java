package in.partake.controller;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.struts2.StrutsTestCase;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;

public class EventSearchTest extends StrutsTestCase {

	public void setUp() throws Exception {
		super.setUp();
		setDefaultParams();
	}

	private void setDefaultParams() {
		request.setParameter("searchTerm", "term");
		request.setParameter("category", "all");
		request.setParameter("sortOrder", "score");
	}

	public void testLoginIsNotRequired() {
		ActionProxy proxy = getActionProxy("/events/search");
		EventsSearchController controller = (EventsSearchController) proxy.getAction();
		@SuppressWarnings("unchecked")
		Map<String, Object> requestMap = new HashMap<String, Object>(request.getParameterMap());
		controller.setRequest(requestMap);
		Assert.assertEquals(Action.SUCCESS, controller.search());
	}

	public void testToUseUnknownCategory() {
		request.setParameter("category", "unknown");
		ActionProxy proxy = getActionProxy("/events/search");
		EventsSearchController controller = (EventsSearchController) proxy.getAction();
		@SuppressWarnings("unchecked")
		Map<String, Object> requestMap = new HashMap<String, Object>(request.getParameterMap());
		controller.setRequest(requestMap);
		Assert.assertEquals(Action.INPUT, controller.search());
	}

	public void testToUseUnknownSortOrder() {
		request.setParameter("sortOrder", "unknown");
		ActionProxy proxy = getActionProxy("/events/search");
		EventsSearchController controller = (EventsSearchController) proxy.getAction();
		@SuppressWarnings("unchecked")
		Map<String, Object> requestMap = new HashMap<String, Object>(request.getParameterMap());
		controller.setRequest(requestMap);
		Assert.assertEquals(Action.INPUT, controller.search());
	}
}
