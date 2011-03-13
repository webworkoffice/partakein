package in.partake.page.feed;

import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.page.base.PartakeFeedPage;
import in.partake.resource.I18n;
import in.partake.resource.PartakeProperties;
import in.partake.service.EventService;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;

public class FeedEventPage extends PartakeFeedPage {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(FeedEventPage.class);
    
    // /feed/all
    public FeedEventPage() {
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setEncoding("utf-8");
        
        feed.setTitle("Recent 100 events - [PARTAKE]");
        feed.setLink(PartakeProperties.get().getTopPath() + "/");
        feed.setDescription("最近登録されたイベントを(最大100)フィードします。");
        
        try {
            List<Event> events = EventService.get().getRecentEvents(100);
            createFeed(feed, events);
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            renderDBError();
        } catch (IOException e) {
            logger.error("IOException", e);
            renderError("IOException ?");
        } catch (FeedException e) {
            logger.error("FeedException", e);
            renderError("FeedException");
        }
    }
    
    // /feed/category/{category}
    public FeedEventPage(PageParameters params) {
        String category = params.get("category").toOptionalString();
        
        // check category is correct.
        if (!EventCategory.isValidCategoryName(category)) {
            renderNotFound();
            return;
        }
        
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setEncoding("utf-8");
        
        feed.setTitle("Recent 100 events - " + EventCategory.getReadableCategoryName(category) + " - [PARTAKE]");
        feed.setLink(PartakeProperties.get().getTopPath() + "/");
        feed.setDescription("最近登録されたイベントを(最大100)フィードします。");
        
        try {
            List<Event> events = EventService.get().getRecentCategoryEvents(category, 100);
            createFeed(feed, events);
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            renderDBError();
        } catch (IOException e) {
            logger.error("IOException", e);
            renderError("IOException ?");
        } catch (FeedException e) {
            logger.error("FeedException", e);
            renderError("FeedException");
        }
    }
}
