package in.partake.model.dao.cassandra;

import in.partake.model.dao.FeedAccessTestCaseBase;
import in.partake.resource.PartakeProperties;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class CassandraFeedDaoTest extends FeedAccessTestCaseBase {
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
    
    // common test cases are written in the super class. 
}
