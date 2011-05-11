package in.partake.controller;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.resource.Constants;
import in.partake.resource.I18n;
import in.partake.service.EventService;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


public class ToppageController extends PartakeActionSupport {
	/** */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ToppageController.class);
	private static final int DISPLAYED_EVENTS = 5;

	public String index() {
		// 最近登録されたイベントを出す。
		// TODO: 毎回表示するの大変なので、cache するべきですよねー。なんとかならないかな。
		try {
			List<Event> events = EventService.get().getRecentEvents(DISPLAYED_EVENTS);
			attributes.put(Constants.ATTR_RECENT_EVENTS, events);
		} catch (DAOException e) {
			logger.warn("Loading recent events failed.", e);
			attributes.put(Constants.ATTR_RECENT_EVENTS, Collections.EMPTY_LIST);
		}

		// もしログインしていれば、最近のイベントを表示する。
		UserEx user = getLoginUser();
		if (user != null) {
		    try {
		        attributes.put(Constants.ATTR_OWNED_EVENTSET, EventService.get().getUnfinishedEventsOwnedBy(user.getId()));
		        attributes.put(Constants.ATTR_ENROLLED_EVENTSET, EventService.get().getUnfinishedEnrolledEvents(user.getId()));
		    } catch (DAOException e) {
		        logger.error(I18n.t(I18n.DATABASE_ERROR), e);
		    }
		}

		return SUCCESS;
	}

	public String alwaysSuccess() {
	    return SUCCESS;
	}
}
