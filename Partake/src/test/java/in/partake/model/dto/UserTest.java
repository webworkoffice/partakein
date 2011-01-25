package in.partake.model.dto;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserTest {
    private User[] samples;

    @Before
    public void createSampleData() {
        Date now = new Date(); 
        samples = new User[] {
                new User(),
                new User("id1", now, 1, "calendarId1"),
                new User("id2", now, 2, "calendarId2")
        };
    }
    
    @Test
    public void testCopyConstructor() {
        for (User source : samples) {
            Assert.assertEquals(source, new User(source));
        }
        
        for (User lhs : samples) {
            for (User rhs : samples) {
                if (lhs == rhs) { continue; }
                Assert.assertFalse(lhs.equals(rhs));
            }
        }
    }
}
