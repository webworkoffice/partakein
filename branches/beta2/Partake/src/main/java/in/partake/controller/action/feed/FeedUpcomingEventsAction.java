package in.partake.controller.action.feed;

import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.resource.ServerErrorCode;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;

public class FeedUpcomingEventsAction extends AbstractFeedPageAction {
    private static final long serialVersionUID = 1L;

    @Override
    public String doExecute() throws DAOException {
        // TODO: CACHE!

        String category = getParameter("category");

        // check category is correct.
        if (!EventCategory.isValidCategoryName(category) && !category.equals(EventCategory.getAllEventCategory())) { return NOT_FOUND; }

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setEncoding("utf-8");

        if (category.equals(EventCategory.getAllEventCategory())) {
            feed.setTitle("Upcoming 100 events - [PARTAKE]");
        } else {
            feed.setTitle("Upcoming 100 events - " + EventCategory.getReadableCategoryName(category) + " - [PARTAKE]");
        }
        feed.setLink("http://partake.in/"); // TODO use in.partake.toppath from properties file
        feed.setDescription("近日開催されるイベントを(最大100)フィードします。");

        try {
            List<Event> events = EventService.get().getUpcomingEvents(100, category);
            InputStream is = createFeed(feed, events);
            
            return renderInlineStream(is, "application/rss+xml");
        } catch (IOException e) {
            return renderError(ServerErrorCode.ERROR_IO, e);
        } catch (FeedException e) {
            return renderError(ServerErrorCode.FEED_CREATION, e);
        }
    }
}
