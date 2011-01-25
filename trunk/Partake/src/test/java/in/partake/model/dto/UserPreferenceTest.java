package in.partake.model.dto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserPreferenceTest {
    private UserPreference[] samples;

    @Before
    public void createSamples() {
        samples = new UserPreference[] {
            new UserPreference(true, true, false),
            new UserPreference(true, true, true),
            new UserPreference(false, false, false),
            new UserPreference(false, true, false),
        };
    }
    
    @Test
    public void testCopyConstructor() {
        for (UserPreference source : samples) {
            Assert.assertEquals(source, new UserPreference(source));
        }
        
        for (UserPreference lhs : samples) {
            for (UserPreference rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }

}
