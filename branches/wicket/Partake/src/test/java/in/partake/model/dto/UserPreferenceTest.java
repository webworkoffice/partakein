package in.partake.model.dto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserPreferenceTest extends AbstractPartakeModelTest<UserPreference> {
    private UserPreference[] samples;

    @Before
    public void createSamples() {
        samples = new UserPreference[] {
            new UserPreference("id1", true, true, false),
            new UserPreference("id2", true, true, true),
            new UserPreference("id3", false, false, false),
            new UserPreference("id4", false, true, false),
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

	@Override
	protected UserPreference createModel() {
		return new UserPreference();
	}

}
