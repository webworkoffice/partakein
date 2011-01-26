package in.partake.model.dao.jpa;

import in.partake.model.dao.PartakeConnectionTestCaseBase;
import in.partake.resource.PartakeProperties;

import org.junit.AfterClass;
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
    
    // NOTE: the test cases are implemented in the super class.
}
