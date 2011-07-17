package in.partake.mashup.hatena;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import in.partake.mashup.hatena.HatenaBookmarkCountLoader;

import org.junit.Test;


public class HatenaBookmarkCountLoaderTest {

    @Test
    public void testTwitterIsBookmarkedOver10000() {
        HatenaBookmarkCountLoader loader = new HatenaBookmarkCountLoader();
        int bookmarkCount = loader.loadCountOfAllPages("http://twitter.com/");
        assertTrue(bookmarkCount > 10000);
    }

    @Test
    public void testUnknownServerIsNotBookmarked() {
    	HatenaBookmarkCountLoader loader = new HatenaBookmarkCountLoader();
            int bookmarkCount = loader.loadCountOfAllPages("http://unknown.partake.in/");
            assertEquals(0, bookmarkCount);
    }

}
