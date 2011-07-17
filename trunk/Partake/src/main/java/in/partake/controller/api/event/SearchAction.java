package in.partake.controller.api.event;

import in.partake.controller.api.PartakeAPIActionSupport;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.resource.UserErrorCode;
import in.partake.service.EventService;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.lucene.queryParser.ParseException;

// search API can take
//  1) query (String)
//  2) category (String)
//  3) beforeDeadlineOnly (Boolean)
//  4) sortOrder (String)
//  5) maxNum (integer)

public class SearchAction extends PartakeAPIActionSupport {
    private static final long serialVersionUID = 1L;
    // private static final Logger logger = Logger.getLogger(SearchAction.class);

    public static final int MAX_NUM = 100;
    
    public String search() throws DAOException {
        String query = getQuery();
        if (query == null) { return renderInvalid(UserErrorCode.MISSING_SEARCH_QUERY); }
        
        String category = getCategory();
        if (category == null) { return renderInvalid(UserErrorCode.MISSING_SEARCH_CATEGORY); }
        
        String beforeDeadlineOnly = getBeforeDeadlineOnly();
        if (beforeDeadlineOnly == null) { return renderInvalid(UserErrorCode.MISSING_SEARCH_DEADLINE); }

        String sortOrder = getSortOrder();
        if (sortOrder == null) { return renderInvalid(UserErrorCode.MISSING_SEARCH_ORDER); }
        
        String maxNumArg = getMaxNum();
        if (maxNumArg == null) { return renderInvalid(UserErrorCode.MISSING_SEARCH_MAXNUM); }
        
        final int maxNum; 
        try {
            maxNum = Integer.parseInt(maxNumArg);
        } catch (NumberFormatException e) {
            return renderInvalid(UserErrorCode.INVALID_SEARCH_MAXNUM);
        }
        // TODO(mayah): 100 is a magic number. This number should be externalized.
        if (maxNum < 0 || 100 < maxNum) { return renderInvalid(UserErrorCode.INVALID_SEARCH_MAXNUM); }
        
        try {
            List<Event> events = EventService.get().search(query, category, sortOrder, Boolean.parseBoolean(beforeDeadlineOnly), maxNum);
             
            JSONArray jsonEventsArray = new JSONArray();
            for (Event event : events) {
                jsonEventsArray.add(event.toJSON());
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
        if (query == null) {
            query = "";
        } else {
            query = query.trim();
        }

        return query;
    }
    
    private String getCategory() {
        String category = getParameter("category");
        if (category == null) { return "all"; }
        
        category = category.trim();
        if ("all".equals(category)) {
            return category;
        } else if (EventCategory.isValidCategoryName(category)) {
            return category;
        } else {
            return null;
        }
    }
    
    private String getBeforeDeadlineOnly() {
        String beforeDeadlineOnly = getParameter("beforeDeadlineOnly");
        if (beforeDeadlineOnly == null) { return "true"; }
        
        if ("true".equalsIgnoreCase(beforeDeadlineOnly)) {
            return "true";
        }
        if ("false".equalsIgnoreCase(beforeDeadlineOnly)) {
            return "false";
        }
        
        return null;
    }
    
    private String getSortOrder() {
        String sortOrder = getParameter("sortOrder");
        if (sortOrder == null) { return "score"; }

        sortOrder = sortOrder.trim();
        if ("score".equalsIgnoreCase(sortOrder))       { return "score"; }
        if ("createdAt".equalsIgnoreCase(sortOrder))   { return "createdAt"; }
        if ("deadline".equalsIgnoreCase(sortOrder))    { return "deadline"; }
        if ("deadline-r".equalsIgnoreCase(sortOrder))  { return "deadline-r"; }
        if ("beginDate".equalsIgnoreCase(sortOrder))   { return "beginDate"; }
        if ("beginDate-r".equalsIgnoreCase(sortOrder)) { return "beginDate-r"; }
        
        return null;
    }
    
    private String getMaxNum() {
        String maxNum = getParameter("maxNum");
        if (maxNum == null) { return "10"; }
        
        maxNum = maxNum.trim();
        if (maxNum.length() > 5) { return null; }
        
        try {
            int v = Integer.parseInt(maxNum);
            if (v <= 0 || MAX_NUM < v) { return null; }
            
            return maxNum;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
