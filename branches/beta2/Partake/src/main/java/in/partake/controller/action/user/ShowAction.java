package in.partake.controller.action.user;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.model.daofacade.deprecated.UserService;
import in.partake.model.dto.Event;
import in.partake.model.dto.UserPreference;
import in.partake.resource.Constants;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import com.opensymphony.xwork2.ActionContext;

public class ShowAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------------------------

    public String doExecute() throws DAOException {
        // TODO MypageController#show() のコードをほぼ流用できる
        String userId = getParameter("userId");
        if (StringUtils.isEmpty(userId))
            return renderInvalid(UserErrorCode.MISSING_USER_ID);

        UserEx user = UserService.get().getUserExById(userId);
        if (user == null)
            return renderNotFound();

        UserPreference pref = UserService.get().getUserPreference(userId);
        if (pref == null)
            return renderError(ServerErrorCode.USER_PREFERENCE_NOTFOUND);

        if (!pref.isProfilePublic())
            return render("users/private.jsp");

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

        return render("users/show.jsp");
    }

    private static class IsPublicPredicate implements Predicate {
        @Override
        public boolean evaluate(Object object) {
            Event event = (Event) object;
            return event != null &&! event.isPrivate();
        }
    }
}
