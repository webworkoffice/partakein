package in.partake.page.feed;

import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventActivity;
import in.partake.page.base.PartakeFeedPage;
import in.partake.resource.I18n;
import in.partake.service.EventService;
import in.partake.view.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;

public class FeedEventPage extends PartakeFeedPage {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(FeedEventPage.class);
    
    // /feed/category/{category}
    public FeedEventPage(PageParameters params) {
        try {
            String id = params.get("id").toOptionalString();
            if (StringUtils.isBlank(id)) {
                renderInvalidRequest("フィード id が適切にしていされていません。");
                return;
            }

            Event event = EventService.get().getEventByFeedId(id);
            if (event == null) { 
                renderNotFound();
                return;
            }
            
            SyndFeed feed = new SyndFeedImpl();
            feed.setFeedType("rss_2.0");
            feed.setEncoding("utf-8");
            
            feed.setTitle(event.getTitle() + " - [PARTAKE]");
            feed.setLink(event.getEventURL());
            feed.setDescription(event.getSummary());
            
            renderEventFeed(feed, event.getId());
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
    
    private void renderEventFeed(SyndFeed feed, String eventId) throws IOException, FeedException, DAOException {
        List<SyndEntry> entries = new ArrayList<SyndEntry>();

        // EventActivity を読んで、そのとおりに出力する。
        //     EventActivity には次のものが登録されているはず
        //     1. Event 更新記録
        //     2. コメント
        //     3. 参加変更
        
        List<EventActivity> activities = EventService.get().getEventActivities(eventId, 100);
        if (activities != null) {
            for (EventActivity activity : activities) {
                SyndContent content = new SyndContentImpl();
                content.setType("text/html");
                content.setValue(Helper.cleanupHTML(activity.getContent()));
                
                SyndEntry entry = new SyndEntryImpl();
                entry.setTitle(Helper.h(activity.getTitle()));
                entry.setDescription(content);
                
                entries.add(entry);
            }
        }
        
        feed.setEntries(entries);
        
        renderSyndFeed(feed);
    }
}
