package in.partake.controller.action.event;

import in.partake.base.KeyValuePair;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.service.EventSortOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventSearchAction extends AbstractPartakeAction {
	/** */
	private static final long serialVersionUID = 1L;

	private static final List<KeyValuePair> CATEGORIES_FOR_SEARCH;
	static {
		List<KeyValuePair> categories = new ArrayList<KeyValuePair>();
		categories.add(new KeyValuePair(EventCategory.getAllEventCategory(), "全て"));
		categories.addAll(EventCategory.CATEGORIES);
		CATEGORIES_FOR_SEARCH = Collections.unmodifiableList(categories);
	}

	public String doExecute() throws DAOException {
	    return render("events/search.jsp");
	}

	// ----------------------------------------------------------------------

	public List<KeyValuePair> getCategories() {
		return CATEGORIES_FOR_SEARCH;
	}

	public List<KeyValuePair> getSortOrders() {
	    return EventSortOrder.getSortOrders();
	}
}
