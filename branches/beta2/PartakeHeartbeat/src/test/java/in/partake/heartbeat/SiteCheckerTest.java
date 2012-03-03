package in.partake.heartbeat;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class SiteCheckerTest {
	@Test
	public void testToCheckNonexistentSite() throws MalformedURLException {
		Config config = mock(Config.class);
		when(config.getUrl()).thenReturn(new URL("http://NonexistentSite.partake.in/"));
		assertThat(new SiteChecker().execute(config), is(false));
	}

	@Test
	public void testToExistentSite() throws MalformedURLException {
		Config config = mock(Config.class);
		when(config.getUrl()).thenReturn(new URL("http://partake.in/"));
		assertThat(new SiteChecker().execute(config), is(true));
	}
}
