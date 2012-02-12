package in.partake.controller;

import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.UserPreference;
import in.partake.resource.Constants;
import in.partake.service.EventService;
import in.partake.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionContext;



public class UsersController extends PartakeActionSupport {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(UsersController.class);

	// ----------------------------------------------------------------------

	public String show() {
		// TODO MypageController#show() のコードをほぼ流用できる
		try {
			String userId = getParameter("userId");
			if (StringUtils.isEmpty(userId)) { return ERROR; }

			UserEx user = UserService.get().getUserExById(userId);
			if (user == null) { return NOT_FOUND; }

			UserPreference pref = UserService.get().getUserPreference(userId);
			if (pref == null) {
				logger.error("UserPreference is null. Something wrong.");
				return NOT_FOUND;
			}

			if (pref.isProfilePublic()) {
				attributes.put(Constants.ATTR_SHOWING_USER, user);
				List<Event> owned = EventService.get().getEventsOwnedBy(user); 
				List<Event> enrolledEvents = UserService.get().getEnrolledEvents(user.getId());

				List<Event> enrolled = new ArrayList<Event>();
				List<Event> finished = new ArrayList<Event>();
				Date now = new Date();

				for (Event e : enrolledEvents) {
					if (e == null) { continue; }
					if (e.getBeginDate().before(now)) {
						finished.add(e);
					} else {
						enrolled.add(e);
					}
				}

				// このページは他者に見せるものなので、見せていいイベントだけを返す
				IsPublicPredicate predicate = new IsPublicPredicate();
				CollectionUtils.filter(owned, predicate);
				CollectionUtils.filter(enrolled, predicate);
				CollectionUtils.filter(finished, predicate);

				Collections.sort(enrolled, Event.getComparatorBeginDateAsc());
				Collections.sort(finished, Event.getComparatorBeginDateAsc());

				ActionContext.getContext().put(Constants.ATTR_OWNED_EVENTSET, owned);
				ActionContext.getContext().put(Constants.ATTR_ENROLLED_EVENTSET, enrolled);
				ActionContext.getContext().put(Constants.ATTR_FINISHED_EVENTSET, finished);

				return SUCCESS;
			} else {
				return PROHIBITED;
			}
		} catch (DAOException e) {
			logger.warn("データベースエラーです。", e);
			addActionError("データベースエラーです。");
			return ERROR;
		}
	}

	private static class IsPublicPredicate implements Predicate {
		@Override
		public boolean evaluate(Object object) {
			Event event = (Event) object;
			return event != null &&! event.isPrivate();
		}
	}
}
