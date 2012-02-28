package in.partake.controller;

import java.io.ByteArrayInputStream;
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
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import in.partake.model.EventEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventActivity;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.resource.PartakeProperties;
import in.partake.service.EventService;
import in.partake.view.util.Helper;

public class EventsFeedController extends PartakeActionSupport {
	private static final Logger LOGGER = Logger.getLogger(EventsFeedController.class);
	/** */
	private static final long serialVersionUID = 1L;
	// private static final Logger logger = Logger.getLogger(EventsFeedController.class);

	private String contentType = null;
	private ByteArrayInputStream inputStream = null;

	public String feedRecentEvents() {
	    // TODO: CACHE!

		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("rss_2.0");
		feed.setEncoding("utf-8");

		feed.setTitle("Recent 100 events - [PARTAKE]");
		feed.setLink("http://partake.in/");	// TODO use in.partake.toppath from properties file
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

	public String feedUpcomingEvents() {
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
		feed.setLink("http://partake.in/");	// TODO use in.partake.toppath from properties file
		feed.setDescription("近日開催されるイベントを(最大100)フィードします。");

		try {
			List<Event> events = EventService.get().getUpcomingEvents(100, category);
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
		feed.setLink(PartakeProperties.get().getTopPath() + "/");
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


	public String feedEvent() {
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

			createEventFeed(feed, event.getId());

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
				LOGGER.warn("Fail to get Author name.", e);
			}
			entry.setDescription(content);

			entries.add(entry);
		}

		feed.setEntries(entries);
		outputSyndFeed(feed);
	}

	private void createEventFeed(SyndFeed feed, String eventId) throws IOException, FeedException, DAOException {
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

        outputSyndFeed(feed);
	}


    private void outputSyndFeed(SyndFeed feed) throws IOException, FeedException, UnsupportedEncodingException {
        SyndFeedOutput output = new SyndFeedOutput();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        output.output(feed, new OutputStreamWriter(baos, "utf-8"));

        baos.flush();

        this.contentType = "application/rss+xml";
        this.inputStream = new ByteArrayInputStream(baos.toByteArray());

        baos.close();
    }
}
