package in.partake.model.dao.cassandra;

import in.partake.model.dao.PartakeConnectionTestCaseBase;
import in.partake.resource.PartakeProperties;
import in.partake.service.PartakeService;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

public class CassandraConnectionTest extends PartakeConnectionTestCaseBase {
    @BeforeClass
    public static void setUpOnce() {
        PartakeProperties.get().reset("cassandra");
        PartakeService.reset();
    }
    
    @AfterClass
    public static void tearDownOnce() {
        PartakeProperties.get().reset();
        PartakeService.reset();        
    }
    
    @Before
    public void setUp() {
        pool = PartakeService.getPool();
        Assert.assertTrue(pool instanceof CassandraConnectionPool);
    }
    
    @After
    public void tearDown() {
    }
    
    // NOTE: the test cases are implemented in the super class.
}
