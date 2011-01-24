package in.partake.model.dao.jpa;

import in.partake.model.dao.AbstractDaoTestCaseBase;
import in.partake.resource.PartakeProperties;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class JPADaoTestBase extends AbstractDaoTestCaseBase {
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
}
