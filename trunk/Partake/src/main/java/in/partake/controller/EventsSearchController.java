package in.partake.controller;

import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventCategory;
import in.partake.resource.Constants;
import in.partake.service.EventService;
import in.partake.util.KeyValuePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;


public class EventsSearchController extends PartakeActionSupport {
	private static final List<KeyValuePair> SORTORDERS = Collections.unmodifiableList(Arrays.asList(
			new KeyValuePair("score", "マッチ度順"),
			new KeyValuePair("createdAt", "新着順"),
			new KeyValuePair("deadline", "締め切りの早い順"),
			new KeyValuePair("deadline-r", "締め切りの遅い順 "),
			new KeyValuePair("beginDate", "開始日時の早い順"),
			new KeyValuePair("beginDate-r", "開始日時の遅い順")
	));
	
	private static final List<KeyValuePair> CATEGORIES_FOR_SEARCH;
	static {
		List<KeyValuePair> categories = new ArrayList<KeyValuePair>();
		categories.add(new KeyValuePair("all", "全て"));
		categories.addAll(EventCategory.CATEGORIES);
		CATEGORIES_FOR_SEARCH = Collections.unmodifiableList(categories);
	}
	
	private String searchTerm;             // search term
	private String category;               // category
	private String sortOrder;              // how to sort
	private boolean beforeDeadlineOnly;    // only events before deadline.
	
	public EventsSearchController() {
		this.searchTerm = null;
		this.category = null;
		this.sortOrder = null;
		this.beforeDeadlineOnly = true;
	}
	
	public String search() {		
		searchTerm = getParameter("searchTerm");
		category = getParameter("category");		
		sortOrder = getParameter("sortOrder");
		if (getParameter("beforeDeadlineOnly") == null) {
		    beforeDeadlineOnly = true;
		} else {
		    beforeDeadlineOnly = "true".equals(getParameter("beforeDeadlineOnly")) ? true : false;
		}
		
		if (searchTerm == null || category == null || sortOrder == null) {
			try {
				List<Event> events = EventService.get().getRecentEvents();
				attributes.put(Constants.ATTR_RECENT_EVENTS, events);
				return INPUT;
			} catch (DAOException e) {
				e.printStackTrace();
				return ERROR;
			}
		}
		
		String trimed = searchTerm.trim();
		if ("".equals(trimed)) {
			addFieldError("searchTerm", "検索条件を入力してください。");
			return INPUT;
		}
		
		try {
			List<Event> events = EventService.get().search(searchTerm, category, sortOrder, beforeDeadlineOnly, 50);
			attributes.put(Constants.ATTR_SEARCH_RESULT, events);

			return SUCCESS;
		} catch (ParseException e) {
			addActionError("検索条件のパーズに失敗しました。");
			return INPUT;
		} catch (DAOException e) {
			e.printStackTrace();
			return ERROR;
		}
	}
	
	// ----------------------------------------------------------------------
	
	public List<KeyValuePair> getCategories() {
		return CATEGORIES_FOR_SEARCH;
	}
	
	public List<KeyValuePair> getSortOrders() {
		return SORTORDERS;
	}
	
	// ----------------------------------------------------------------------
	
	public String getSearchTerm() {
		return searchTerm;
	}
	
	public String getCategory() {
		return category;
	}
	
	public String getSortOrder() {
		return sortOrder;
	}
	
    public boolean isBeforeDeadlineOnly() {
        return beforeDeadlineOnly;
    }

    public boolean getBeforeDeadlineOnly() {
        return beforeDeadlineOnly;
    }

	public void setSearchTerm(String term) {
		this.searchTerm = term;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	public void setBeforeDeadlineOnly(boolean beforeDeadlineOnly) {
		this.beforeDeadlineOnly = beforeDeadlineOnly;
	}
}
