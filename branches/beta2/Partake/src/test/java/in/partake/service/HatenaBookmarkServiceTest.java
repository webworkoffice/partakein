package in.partake.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import in.partake.service.HatenaBookmarkService;

import org.junit.Ignore;
import org.junit.Test;


public class HatenaBookmarkServiceTest {

    @Test
    @Ignore("Too Slow!")
    public void testTwitterIsBookmarkedOver10000() {
        HatenaBookmarkService loader = new HatenaBookmarkService();
        int bookmarkCount = loader.loadCountOfAllPages("http://twitter.com/");
        assertTrue(bookmarkCount > 10000);
    }

    @Test
    public void testUnknownServerIsNotBookmarked() {
    	HatenaBookmarkService loader = new HatenaBookmarkService();
            int bookmarkCount = loader.loadCountOfAllPages("http://unknown.partake.in/");
            assertEquals(0, bookmarkCount);
    }

}
