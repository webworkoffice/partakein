package in.partake.controller.action.feed;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.EventEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventActivity;
import in.partake.view.util.Helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

public abstract class AbstractFeedPageAction extends AbstractPartakeAction{
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AbstractFeedPageAction.class);

    protected InputStream createFeed(SyndFeed feed, List<Event> events) throws IOException, FeedException {
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
                EventEx ex = DeprecatedEventDAOFacade.get().getEventExById(event.getId());
                entry.setAuthor(ex.getOwner().getScreenName());
            } catch (DAOException e) {
                LOGGER.warn("Fail to get Author name.", e);
            }
            entry.setDescription(content);

            entries.add(entry);
        }

        feed.setEntries(entries);
        return outputSyndFeed(feed);
    }

    protected InputStream createEventFeed(SyndFeed feed, String eventId) throws IOException, FeedException, DAOException {
        List<SyndEntry> entries = new ArrayList<SyndEntry>();

        // EventActivity を読んで、そのとおりに出力する。
        //     EventActivity には次のものが登録されているはず
        //     1. Event 更新記録
        //     2. コメント
        //     3. 参加変更

        List<EventActivity> activities = DeprecatedEventDAOFacade.get().getEventActivities(eventId, 100);
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

        return outputSyndFeed(feed);
    }


    protected InputStream outputSyndFeed(SyndFeed feed) throws IOException, FeedException, UnsupportedEncodingException {
        SyndFeedOutput output = new SyndFeedOutput();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        output.output(feed, new OutputStreamWriter(baos, "utf-8"));
        baos.flush();
        baos.close();

        return new ByteArrayInputStream(baos.toByteArray());
    }
}
