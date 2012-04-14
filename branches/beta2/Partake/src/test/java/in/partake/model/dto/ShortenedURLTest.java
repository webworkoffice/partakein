package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.fixture.TestDataProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ShortenedURLTest extends AbstractPartakeModelTest<ShortenedURLData> {
    @Override
    protected ShortenedURLData copy(ShortenedURLData t) {
        return new ShortenedURLData(t);
    }

    @Override
    protected TestDataProvider<ShortenedURLData> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getURLShortenerProvider();
    }

    private ShortenedURLData[] samples;

    @Before
    public void createSampleData() {
        samples = new ShortenedURLData[] {
                new ShortenedURLData(),
                new ShortenedURLData("http://www.example.com/", "bitly", "http://bit.ly/example"),
                new ShortenedURLData("http://www.example.com/", "tco",   "http://t.oo/example"),
        };
    }

    @Test
    public void testCopyConstructor() {
        for (ShortenedURLData source : samples) {
            Assert.assertEquals(source, new ShortenedURLData(source));
        }

        for (ShortenedURLData lhs : samples) {
            for (ShortenedURLData rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }
}
