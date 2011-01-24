package in.partake.model.dao.jpa;

import in.partake.model.dao.PartakeConnectionTestCaseBase;
import in.partake.resource.PartakeProperties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;


public class JPAConnectionTest extends PartakeConnectionTestCaseBase {
    @BeforeClass
    public static void setUpOnce() {
        PartakeProperties.get().reset("jpa");
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
        Assert.assertTrue(pool instanceof JPAConnectionPool);
    }
    
    @After
    public void tearDown() {
    }
    
    // NOTE: the test cases are implemented in the super class.
}
