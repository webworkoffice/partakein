package in.partake.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import in.partake.model.EventEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventCategory;
import in.partake.service.EventService;
import in.partake.util.Util;

public class EventsFeedController extends PartakeActionSupport {
	/** */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(EventsFeedController.class);
	
	private String contentType = null;
	private ByteArrayInputStream inputStream = null;

	public String feedRecentEvents() {
	    // TODO: CACHE!
	    
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("rss_2.0");
		feed.setEncoding("utf-8");
		
		feed.setTitle("Recent 100 events - [PARTAKE]");
		feed.setLink("http://partake.in/");
		feed.setDescription("最近登録されたイベントを(最大100)フィードします。");
		
		try {
			List<Event> events = EventService.get().getRecentEvents(100);
			createFeed(feed, events);

			return SUCCESS;
		} catch (DAOException e) {
			e.printStackTrace();
			return ERROR;
		} catch (IOException e) {
			e.printStackTrace();
			return ERROR;
		} catch (FeedException e) {
			e.printStackTrace();
			return ERROR;
		}
	}

	
	public String feedCategory() {
	    // TODO: CACHE!
	    
		String category = getParameter("category");
		
		// check category is correct.
		if (!EventCategory.isValidCategoryName(category)) { return NOT_FOUND; }
		
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("rss_2.0");
		feed.setEncoding("utf-8");
		
		feed.setTitle("Recent 100 events - " + EventCategory.getReadableCategoryName(category) + " - [PARTAKE]");
		feed.setLink("http://partake.in/");
		feed.setDescription("最近登録されたイベントを(最大100)フィードします。");
		
		try {
			List<Event> events = EventService.get().getRecentCategoryEvents(category, 100);
			createFeed(feed, events);
			
			return SUCCESS;
		} catch (DAOException e) {
			e.printStackTrace();
			return ERROR;
		} catch (IOException e) {
			e.printStackTrace();
			return ERROR;
		} catch (FeedException e) {
			e.printStackTrace();
			return ERROR;
		}
	}
	
	
	// feed ごとの event に関してはどうしようか悩み中。
	public String feedEvent() {
		String feedId = getParameter("feedId");
		if (feedId == null) { return NOT_FOUND; }
		
		try {
			Event event = EventService.get().getEventByFeedId(feedId);
			if (event == null) { return NOT_FOUND; }
			
			SyndFeed feed = new SyndFeedImpl();
			feed.setFeedType("rss_2.0");
			
			feed.setTitle(event.getTitle() + " - [PARTAKE]");
			feed.setLink(event.getEventURL());
			feed.setDescription(event.getSummary());
			
			// Comment および参加者リストを RSS でフィードします。
			// createEventFeed(event);
			
			return SUCCESS;
		} catch (DAOException e) {
			e.printStackTrace();
			return ERROR;
		}
	}

	
	
	public String getContentType() {
		return this.contentType;
	}
	
	public ByteArrayInputStream getInputStream() {
        return inputStream;
    }
	
	// ----------------------------------------------------------------------

	private void createFeed(SyndFeed feed, List<Event> events) throws IOException, FeedException {			
		List<SyndEntry> entries = new ArrayList<SyndEntry>();
		
		for (Event event : events) {
			SyndEntry entry = new SyndEntryImpl();
			entry.setTitle(event.getTitle());
			entry.setLink(event.getEventURL());
			entry.setPublishedDate(event.getCreatedAt());
			
			SyndContent content = new SyndContentImpl();
			content.setType("text/html");
			content.setValue(Util.cleanupHTML(event.getDescription()));
			entry.setDescription(content);
			
			entries.add(entry);
		}
		
		feed.setEntries(entries);
		
		SyndFeedOutput output = new SyndFeedOutput();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		output.output(feed, new OutputStreamWriter(baos, "utf-8"));
		
		baos.flush();
		
		this.contentType = "application/rss+xml";
		// this.contentType = "text/xml";
		this.inputStream = new ByteArrayInputStream(baos.toByteArray());

		baos.close();
	}
	
}
