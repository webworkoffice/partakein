package in.partake.controller.action.feed;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import in.partake.controller.AbstractPartakeControllerTest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.opensymphony.xwork2.ActionProxy;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

public class FeedRecentEventsActionTest extends AbstractPartakeControllerTest {
    @Test
    public void testFeedRecentEvents() throws Exception {
        ActionProxy proxy = getActionProxy("/feed/all");
        proxy.execute();

        FeedRecentEventsAction action = (FeedRecentEventsAction) proxy.getAction();
        assertThat(action.getContentType(), is("application/rss+xml; charset=utf-8"));
        assertThat(action.getContentDisposition(), is("inline"));

        InputStream is = new ByteArrayInputStream(response.getContentAsByteArray());
        Reader reader = new InputStreamReader(is, Charset.forName("utf-8")); // TODO: Charset should be constant.
        SyndFeed feed = new SyndFeedInput().build(reader);


        List<String> links = new ArrayList<String>();
        @SuppressWarnings("unchecked")
        List<SyndEntry> entries = feed.getEntries();
        for (SyndEntry entry : entries)
            links.add(entry.getLink());

        assertThat(links, hasItem(loadEvent(DEFAULT_EVENT_ID).getEventURL()));
        assertThat(links, not(hasItem(loadEvent(PRIVATE_EVENT_ID).getEventURL())));
    }
}
