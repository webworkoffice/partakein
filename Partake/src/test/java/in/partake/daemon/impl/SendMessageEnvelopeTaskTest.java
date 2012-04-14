package in.partake.daemon.impl;

import static org.mockito.Mockito.mock;
import in.partake.app.PartakeApp;
import in.partake.app.PartakeTestApp;
import in.partake.controller.AbstractPartakeControllerTest;
import in.partake.model.fixture.TestDataProviderConstants;
import in.partake.service.ITwitterService;

import org.junit.Before;

public class SendMessageEnvelopeTaskTest extends AbstractPartakeControllerTest implements TestDataProviderConstants {
    @Before
    public void setUp() throws Exception {
        super.setUp();

        PartakeApp.getTestService().setDefaultFixtures();
        ITwitterService twitterService = mock(ITwitterService.class);
        PartakeTestApp.setTwitterService(twitterService);
    }
}
