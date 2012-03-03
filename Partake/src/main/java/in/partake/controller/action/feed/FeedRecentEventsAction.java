package in.partake.controller.action.feed;

import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.model.dto.Event;
import in.partake.resource.ServerErrorCode;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;

public class FeedRecentEventsAction extends AbstractFeedPageAction {
    private static final long serialVersionUID = 1L;
    
    public String doExecute() throws DAOException {
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setEncoding("utf-8");

        // TODO: Use resource
        feed.setTitle("Recent 100 events - [PARTAKE]");
        feed.setLink("http://partake.in/"); // TODO use in.partake.toppath from properties file
        feed.setDescription("最近登録されたイベントを(最大100)フィードします。");

        try {
            List<Event> events = EventService.get().getRecentEvents(100);
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
