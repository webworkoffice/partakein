package in.partake.page.base;

import in.partake.model.EventEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.service.EventService;
import in.partake.view.Helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

public abstract class PartakeFeedPage extends PartakeBinaryPage {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(PartakeFeedPage.class);
    
    protected void createFeed(SyndFeed feed, List<Event> events) throws IOException, FeedException {          
        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        
        for (Event event : events) {
            if (event == null) { continue; }
            if (event.isPrivate()) { continue; }

            SyndContent content = new SyndContentImpl();
            content.setType("text/html");
            content.setValue(Helper.cleanupHTML(event.getDescription()));
                
            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(event.getTitle());
            entry.setLink(event.getEventURL());
            entry.setPublishedDate(event.getCreatedAt());
            try {
                // TODO use cache or other ways for performance.
                EventEx ex = EventService.get().getEventExById(event.getId());
                entry.setAuthor(ex.getOwner().getScreenName());
            } catch (DAOException e) {
                logger.warn("Fail to get Author name.", e);
            }           
            entry.setDescription(content);
            
            entries.add(entry);
        }
        
        feed.setEntries(entries);       
        renderSyndFeed(feed);
    }

    protected void renderSyndFeed(SyndFeed feed) throws IOException, FeedException, UnsupportedEncodingException {
        SyndFeedOutput output = new SyndFeedOutput();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        output.output(feed, new OutputStreamWriter(baos, "utf-8"));
        baos.flush();
        renderBinary("application/rss+xml", baos.toByteArray());
        baos.close();
    }
}
