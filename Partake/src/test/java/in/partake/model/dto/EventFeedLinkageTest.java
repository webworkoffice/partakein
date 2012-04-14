package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EventFeedLinkageTest extends AbstractPartakeModelTest<EventFeedLinkage> {
    @Override
    protected EventFeedLinkage copy(EventFeedLinkage t) {
        return new EventFeedLinkage(t);
    }

    @Override
    protected TestDataProvider<EventFeedLinkage> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getEventFeedProvider();
    }

    private EventFeedLinkage[] samples;

    @Before
    public void createSamples() {
        samples = new EventFeedLinkage[] {
            new EventFeedLinkage(),
            new EventFeedLinkage("id1", "hoge"),
            new EventFeedLinkage("id2", "fuga"),
        };
    }

    @Test
    public void testCopyConstructor() {
        for (EventFeedLinkage source : samples) {
            Assert.assertEquals(source, new EventFeedLinkage(source));
        }

        for (EventFeedLinkage lhs : samples) {
            for (EventFeedLinkage rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }
}
