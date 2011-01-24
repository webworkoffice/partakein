package in.partake.model.dao.cassandra;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import in.partake.model.dao.AbstractDaoTestCaseBase;
import in.partake.resource.PartakeProperties;

public class CassandraDaoTestBase extends AbstractDaoTestCaseBase {

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
