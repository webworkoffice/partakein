package in.partake.controller;

import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.resource.Constants;
import in.partake.service.EventService;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


public class ToppageController extends PartakeActionSupport {
	/** */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ToppageController.class);
	
	public String index() {
		// 最近登録されたイベントを出す。
		// TODO: 毎回表示するの大変なので、cache するべきですよねー。なんとかならないかな。
		try {
			List<Event> events = EventService.get().getRecentEvents();
			attributes.put(Constants.ATTR_RECENT_EVENTS, events);
		} catch (DAOException e) {
			// TODO: should be logged.
			e.printStackTrace();
			attributes.put(Constants.ATTR_RECENT_EVENTS, Collections.EMPTY_LIST);
		}
		
		return SUCCESS;
	}
	
	public String alwaysSuccess() {
	    return SUCCESS;
	}
}
