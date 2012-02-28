package in.partake.all;

import org.junit.Ignore;
import org.junit.Test;

public class EventGeneralTest {
    
    @Test @Ignore("Not implemented yet.")
    public void testToCreateAndGetAnEvent() throws Exception {
        // TODO not implemented yet.
        // 1. create an event.
        // 2. retrieve the created event.
        // 3. the event should be searchable. (This needs lucene...)
        // 4. the RSS should be published.
        
        throw new RuntimeException("Not implemented yet.");
    }
    
    @Test @Ignore("Not implemented yet.")
    public void testToSendReminder() throws Exception {
        // TODO not implemented yet.
        // 1. create an event, whose begin time is a few days after.
        // 2. run the reminder task. The reminder should NOT be sent.
        // 3. change the current time to just before the event.
        // 4. run the reminder task again. The reminder should be sent this time.
        
        throw new RuntimeException("Not implemented yet.");
    }
}
