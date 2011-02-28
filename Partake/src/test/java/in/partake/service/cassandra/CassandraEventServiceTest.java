package in.partake.service.cassandra;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import in.partake.resource.PartakeProperties;
import in.partake.service.EventServiceTestCaseBase;

public class CassandraEventServiceTest extends EventServiceTestCaseBase {
    @BeforeClass
    public static void setUpOnce() {
        PartakeProperties.get().reset("cassandra");
        reset();
    }

    @AfterClass
    public static void tearDownOnce() {
        PartakeProperties.get().reset();
        reset();
    }

}
