package in.partake.controller.action.feed;

import in.partake.base.PartakeException;
import in.partake.model.EventEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.base.Transaction;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.resource.ServerErrorCode;
import in.partake.service.IEventSearchService;
import in.partake.service.PartakeService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;

public class FeedRecentEventsAction extends AbstractFeedPageAction {
    private static final long serialVersionUID = 1L;
    
    public String doExecute() throws DAOException, PartakeException {
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setEncoding("utf-8");

        // TODO: Use resource
        feed.setTitle("Recent 100 events - [PARTAKE]");
        feed.setLink("http://partake.in/"); // TODO use in.partake.toppath from properties file
        feed.setDescription("最近登録されたイベントを(最大100)フィードします。");

        try {
            IEventSearchService searchService = PartakeService.get().getEventSearchService();        
            List<String> eventIds = searchService.getRecent(100);

            List<EventEx> events = new GetEventsTransaction(eventIds).execute(); 
            InputStream is = createFeed(feed, events);

            return renderInlineStream(is, "application/rss+xml");
        } catch (DAOException e) {
            return renderError(ServerErrorCode.DB_ERROR, e);
        } catch (IOException e) {
            return renderError(ServerErrorCode.ERROR_IO, e);
        } catch (FeedException e) {
            return renderError(ServerErrorCode.FEED_CREATION, e);
        }
    }    
}

class GetEventsTransaction extends Transaction<List<EventEx>> {
    private List<String> eventIds;
    
    public GetEventsTransaction(List<String> eventIds) {
        this.eventIds = eventIds;
    }
    
    @Override
    protected List<EventEx> doExecute(PartakeConnection con) throws DAOException, PartakeException {        
        List<EventEx> events = new ArrayList<EventEx>();
        for (String eventId : eventIds) {
            EventEx event = EventDAOFacade.getEventEx(con, eventId);
            if (event != null)
                events.add(event);
        }
        
        return events;
    }
}

