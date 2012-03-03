package in.partake.controller.action.feed;

import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;
import in.partake.model.dto.Event;
import in.partake.resource.ServerErrorCode;

import java.io.IOException;
import java.io.InputStream;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;

public class FeedEventAction extends AbstractFeedPageAction {
    private static final long serialVersionUID = 1L;

    @Override
    public String doExecute() throws DAOException {
        String feedId = getParameter("feedId");
        if (feedId == null) { return NOT_FOUND; }

        try {
            Event event = EventService.get().getEventByFeedId(feedId);
            if (event == null) { return NOT_FOUND; }

            SyndFeed feed = new SyndFeedImpl();
            feed.setFeedType("rss_2.0");
            feed.setEncoding("utf-8");

            feed.setTitle(event.getTitle() + " - [PARTAKE]");
            feed.setLink(event.getEventURL());
            feed.setDescription(event.getSummary());

            InputStream is = createEventFeed(feed, event.getId());
            return renderInlineStream(is, "application/rss+xml");
        } catch (IOException e) {
            return renderError(ServerErrorCode.ERROR_IO, e);
        } catch (FeedException e) {
            return renderError(ServerErrorCode.FEED_CREATION, e);
        }
    }

}
