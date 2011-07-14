package in.partake.model.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HatenaDaoTest {
	@Test
	public void testTwitterIsBookmarkedOver10000() {
		HatenaDao dao = new HatenaDao();
		int bookmarkCount = dao.getTotalBookmarkCount("http://twitter.com/");
		assertTrue(bookmarkCount > 10000);
	}

	@Test
	public void testUnknownServerIsNotBookmarked() {
		HatenaDao dao = new HatenaDao();
		int bookmarkCount = dao.getTotalBookmarkCount("http://unknown.partake.in/");
		assertEquals(0, bookmarkCount);
	}
}
