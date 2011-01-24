package in.partake.model.dao.cassandra;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import in.partake.model.dao.BinaryAccessTestCaseBase;
import in.partake.resource.PartakeProperties;

public class CassandraCacheDaoTest extends BinaryAccessTestCaseBase {
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
