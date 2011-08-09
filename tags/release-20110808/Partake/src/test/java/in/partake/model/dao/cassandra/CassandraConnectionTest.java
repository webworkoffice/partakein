package in.partake.model.dao.cassandra;

import in.partake.model.dao.PartakeConnectionTestCaseBase;
import in.partake.resource.PartakeProperties;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class CassandraConnectionTest extends PartakeConnectionTestCaseBase {
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
    
    // NOTE: the test cases are implemented in the super class.
}
