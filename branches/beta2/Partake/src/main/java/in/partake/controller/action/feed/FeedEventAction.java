package in.partake.controller.action.feed;

import in.partake.base.PartakeException;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventFeedAccess;
import in.partake.model.dao.base.Transaction;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventActivity;
import in.partake.model.dto.EventFeedLinkage;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
import in.partake.service.DBService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;

public class FeedEventAction extends AbstractFeedPageAction {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        String feedId = getValidIdParameter("feedId", UserErrorCode.INVALID_NOTFOUND, UserErrorCode.INVALID_NOTFOUND);

        FeedEventTransaction transaction = new FeedEventTransaction(feedId);
        transaction.execute();

        try {
            Event event = transaction.getEvent();
            if (event == null)
                return renderNotFound();

            SyndFeed feed = new SyndFeedImpl();
            feed.setFeedType("rss_2.0");
            feed.setEncoding("utf-8");

            feed.setTitle(event.getTitle() + " - [PARTAKE]");
            feed.setLink(event.getEventURL());
            feed.setDescription(event.getSummary());

            InputStream is = createEventFeed(feed, transaction.getActivities());
            if (is == null)
                return renderNotFound();
            return renderInlineStream(is, "application/rss+xml");
        } catch (IOException e) {
            throw new PartakeException(ServerErrorCode.ERROR_IO, e);
        } catch (FeedException e) {
            throw new PartakeException(ServerErrorCode.FEED_CREATION, e);
        }
    }
}

class FeedEventTransaction extends Transaction<InputStream> {
    private String feedId;
    private Event event;
    private List<EventActivity> eventActivities;

    public FeedEventTransaction(String feedId) {
        this.feedId = feedId;
    }

    @Override
    protected InputStream doExecute(PartakeConnection con) throws DAOException, PartakeException {
        IEventFeedAccess feedAccess = DBService.getFactory().getEventFeedAccess();
        EventFeedLinkage linkage = feedAccess.find(con, feedId);
        if (linkage == null)
            return null;

        event = EventDAOFacade.getEventEx(con, linkage.getEventId());
        if (event == null)
            return null;
        
        eventActivities = DBService.getFactory().getEventActivityAccess().findByEventId(con, event.getId(), 100);
        return null;
    }

    public Event getEvent() {
        return event;
    }

    public List<EventActivity> getActivities() {
        return eventActivities;
    }
}
