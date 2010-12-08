package in.partake.controller;

import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.resource.Constants;
import in.partake.service.EventService;

import java.util.Collections;
import java.util.List;


public class ToppageController extends PartakeActionSupport {

	public String index() {
		System.out.println("index!");
		
		// 最近登録されたイベントを出す。
		// TODO: 毎回表示するの大変なので、cache するべきですよねー。なんとかならないかな。
		try {
			List<Event> events = EventService.get().getRecentEvents();
			System.out.println("index 2");
			attributes.put(Constants.ATTR_RECENT_EVENTS, events);
			System.out.println("index 3");
		} catch (DAOException e) {
			// TODO: should be logged.
			System.out.println("index 4");
			e.printStackTrace();
			System.out.println("index 5");
			attributes.put(Constants.ATTR_RECENT_EVENTS, Collections.EMPTY_LIST);
			System.out.println("index 6");
		}
		
		System.out.println("SUCCESS!");
		return SUCCESS;
	}
	
	public String alwaysSuccess() {
	    return SUCCESS;
	}
}
