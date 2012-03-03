package in.partake.controller.api.event;

import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.resource.UserErrorCode;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.ParseException;

// search API can take
//  1) query (String)
//  2) category (String)
//  3) beforeDeadlineOnly (Boolean)
//  4) sortOrder (String)
//  5) maxNum (integer)

public class SearchAPI extends AbstractPartakeAPI {
	private static final long serialVersionUID = 1L;
    // private static final Logger logger = Logger.getLogger(SearchAction.class);
	private static final String DEFAULT_CATEGORY = EventCategory.getAllEventCategory();
	private static final String DEFAULT_BEFORE_DEADLINE_ONLY = "true";
    private static final String DEFAULT_SORT_ORDER = "score";
    private static final int DEFAULT_MAX_NUM = 10;

    public static final int MAX_NUM = 100;

    
    public String doExecute() throws DAOException {
        String query = getQuery();

        String category = getCategory();
        if (category == null) { return renderInvalid(UserErrorCode.MISSING_SEARCH_CATEGORY); }

        String sortOrder = getSortOrder();
        if (sortOrder == null) { return renderInvalid(UserErrorCode.MISSING_SEARCH_ORDER); }

        final String beforeDeadlineOnly;
        final int maxNum;
        try {
            beforeDeadlineOnly = getBeforeDeadlineOnly();
            maxNum = getMaxNum();
        } catch (IllegalRequestException e) {
            return renderInvalid(e.getErrorCode());
        }

        try {
            List<Event> events = EventService.get().search(query, category, sortOrder, Boolean.parseBoolean(beforeDeadlineOnly), maxNum);

            JSONArray jsonEventsArray = new JSONArray();
            for (Event event : events) {
                jsonEventsArray.add(event.toSafeJSON());
            }
            JSONObject obj = new JSONObject();
            obj.put("events", jsonEventsArray);
            return renderOK(obj);
        } catch (IllegalArgumentException e) {
            return renderInvalid(UserErrorCode.INVALID_SEARCH_QUERY);
        } catch (ParseException e) {
            return renderInvalid(UserErrorCode.INVALID_SEARCH_QUERY);
        }


    }

    private String getQuery() {
        String query = getParameter("query");
        return StringUtils.trimToEmpty(query);
    }

    private String getCategory() {
        String category = getParameter("category");
        if (category == null) { return DEFAULT_CATEGORY; }

        category = category.trim();
        if (EventCategory.getAllEventCategory().equals(category) || EventCategory.isValidCategoryName(category)) {
            return category;
        } else {
            return null;
        }
    }

    private String getBeforeDeadlineOnly() throws IllegalRequestException {
        String beforeDeadlineOnly = getParameter("beforeDeadlineOnly");
        if (beforeDeadlineOnly == null) { return DEFAULT_BEFORE_DEADLINE_ONLY; }

        if ("true".equalsIgnoreCase(beforeDeadlineOnly)) {
            return "true";
        }
        if ("false".equalsIgnoreCase(beforeDeadlineOnly)) {
            return "false";
        }

        throw new IllegalRequestException(UserErrorCode.INVALID_SEARCH_DEADLINE);
    }

    private String getSortOrder() {
        String sortOrder = getParameter("sortOrder");
        if (sortOrder == null) { return DEFAULT_SORT_ORDER; }

        sortOrder = sortOrder.trim();
        if ("score".equalsIgnoreCase(sortOrder))       { return "score"; }
        if ("createdAt".equalsIgnoreCase(sortOrder))   { return "createdAt"; }
        if ("deadline".equalsIgnoreCase(sortOrder))    { return "deadline"; }
        if ("deadline-r".equalsIgnoreCase(sortOrder))  { return "deadline-r"; }
        if ("beginDate".equalsIgnoreCase(sortOrder))   { return "beginDate"; }
        if ("beginDate-r".equalsIgnoreCase(sortOrder)) { return "beginDate-r"; }

        return null;
    }

    private int getMaxNum() throws IllegalRequestException {
        String maxNum = getParameter("maxNum");
        if (maxNum == null) {
            return DEFAULT_MAX_NUM;
        }

        try {
            int v = Integer.parseInt(StringUtils.trim(maxNum));
            if (v <= 0 || MAX_NUM < v) {
                throw new IllegalRequestException(UserErrorCode.INVALID_SEARCH_MAXNUM);
            }

            return v;
        } catch (NumberFormatException e) {
            throw new IllegalRequestException(UserErrorCode.INVALID_SEARCH_MAXNUM);
        }
    }

    // 他のActionクラスでも使うようならcontroller.apiパッケージに移動することも検討
    private static class IllegalRequestException extends Exception {
        private static final long serialVersionUID = -2150899144288175828L;
        private final UserErrorCode errorCode;
        IllegalRequestException(UserErrorCode errorCode) {
            if (errorCode == null) {
                throw new IllegalArgumentException();
            }
            this.errorCode = errorCode;
        }
        UserErrorCode getErrorCode() {
            return this.errorCode;
        }
    }
}
