package in.partake.daemon;

import org.junit.Test;

public class ParticipationStatusChangeTest {

    @Test
    public void testRunTask() {
        ParticipationStatusChangeTask task = new ParticipationStatusChangeTask();
        task.run();
    }
}
