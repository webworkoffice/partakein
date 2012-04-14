package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CalendarLinkageTest extends AbstractPartakeModelTest<CalendarLinkage> {
    private CalendarLinkage[] samples;

    @Override
    protected CalendarLinkage copy(CalendarLinkage t) {
        return new CalendarLinkage(t);
    }

    @Override
    protected TestDataProvider<CalendarLinkage> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getCalendarTestDataProvider();
    }

    @Before
    public void createSampleData() {
        samples = new CalendarLinkage[] {
                new CalendarLinkage(),
                new CalendarLinkage("id1", "userId1"),
                new CalendarLinkage("id2", "userId2"),
        };
    }

    @Test
    public void testCopyConstructor() {
        for (CalendarLinkage source : samples) {
            Assert.assertEquals(source, new CalendarLinkage(source));
        }

        for (CalendarLinkage lhs : samples) {
            for (CalendarLinkage rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }

    @Test
    public void testToJSONFromJSON() {
        CalendarLinkage linkage = new CalendarLinkage("id", "userId");
        Assert.assertEquals(linkage, new CalendarLinkage(linkage.toJSON()));
    }
}
