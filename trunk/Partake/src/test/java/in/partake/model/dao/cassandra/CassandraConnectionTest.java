package in.partake.model.dao.cassandra;

import in.partake.model.dao.PartakeConnectionTestCaseBase;
import in.partake.resource.PartakeProperties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
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
    
    @Before
    public void setUp() {
        pool = getPool();
        Assert.assertTrue(pool instanceof CassandraConnectionPool);
    }
    
    @After
    public void tearDown() {
    }
    
    // NOTE: the test cases are implemented in the super class.
}
