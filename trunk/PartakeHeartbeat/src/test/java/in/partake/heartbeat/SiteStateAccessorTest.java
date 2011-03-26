package in.partake.heartbeat;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class SiteStateAccessorTest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Test
	public void testToStoreState() {
		SiteStateAccessor accessor = new SiteStateAccessor();
		accessor.storeSiteState(true);
		accessor.storeSiteState(false);
	}

	@Test
	public void testToLoadState() {
		SiteStateAccessor accessor = new SiteStateAccessor();
		for (boolean b : new boolean[]{ true, false }) {
			accessor.storeSiteState(b);
			assertThat(accessor.loadPrevState(), is(b));
		}
	}
}
