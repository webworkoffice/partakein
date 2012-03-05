package in.partake.service;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.*;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.daofacade.deprecated.DeprecatedMessageDAOFacade;
import in.partake.model.daofacade.deprecated.DeprecatedMessageDAOFacade.TooLongMessageException;

import org.junit.Test;

public class MessageServiceTest extends AbstractServiceTestCaseBase {
	@Test
	public void testBuildMessage() throws NullPointerException, TooLongMessageException {
		DeprecatedMessageDAOFacade service = DeprecatedMessageDAOFacade.get();
		String title = "title";
		String shortenedURL = "http://shorten.ed/url";

		UserEx sender = mock(UserEx.class);
		doReturn("admin").when(sender).getScreenName();

		EventEx event = mock(EventEx.class);
		doReturn(title).when(event).getTitle();
		doReturn(shortenedURL).when(event).getShortenedURL();

		assertEquals(80, service.calcRestCodePoints(sender, event));
		String message = service.buildMessage(sender, shortenedURL, title, "Happy new year!");
		assertEquals("[PARTAKE] 「" + title + "」 " + shortenedURL + " の管理者(@admin)よりメッセージ：Happy new year!", message);
	}

	@Test
	public void testBuildMessageLongWhenEventHasLongTitle() throws NullPointerException, TooLongMessageException {
		DeprecatedMessageDAOFacade service = DeprecatedMessageDAOFacade.get();
		UserEx sender = mock(UserEx.class);
		doReturn("admin").when(sender).getScreenName();
		String message = service.buildMessage(sender, "http://shorten.ed/url", "loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong", "Happy new year!");
		assertEquals("[PARTAKE] 「loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo...」 http://shorten.ed/url の管理者(@admin)よりメッセージ：Happy new year!", message);
	}

	@Test
	public void testBuildMessageLongWhenEventHasLongMessage() throws NullPointerException, TooLongMessageException {
		DeprecatedMessageDAOFacade service = DeprecatedMessageDAOFacade.get();
		UserEx sender = mock(UserEx.class);
		doReturn("admin").when(sender).getScreenName();
		String message = service.buildMessage(sender, "http://shorten.ed/url", "title of event", "loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong");
		assertEquals("[PARTAKE] 「title o...」 http://shorten.ed/url の管理者(@admin)よりメッセージ：loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong", message);
	}

	@Test(expected=TooLongMessageException.class)
	public void testBuildMessageLongWhenEventHasTooLongMessage() throws NullPointerException, TooLongMessageException {
		DeprecatedMessageDAOFacade service = DeprecatedMessageDAOFacade.get();
		UserEx sender = mock(UserEx.class);
		doReturn("admin").when(sender).getScreenName();
		service.buildMessage(sender, "http://shorten.ed/url", "title of event", "looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong");
	}
}
