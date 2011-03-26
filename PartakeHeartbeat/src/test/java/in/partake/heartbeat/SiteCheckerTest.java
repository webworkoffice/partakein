package in.partake.heartbeat;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Test;

public class SiteCheckerTest {
	@Test
	public void testToCheckNonexistentSite() {
		Config config = mock(Config.class);
		when(config.getUrl()).thenReturn("http://NonexistentSite.partake.in/");
		assertThat(new SiteChecker().execute(config), is(false));
	}

	@Test
	public void testToExistentSite() {
		Config config = mock(Config.class);
		when(config.getUrl()).thenReturn("http://partake.in/");
		assertThat(new SiteChecker().execute(config), is(true));
	}
}
