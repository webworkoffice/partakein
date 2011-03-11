package in.partake.model.dao.jpa;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import in.partake.model.dao.MessageDaoTestCaseBase;
import in.partake.resource.PartakeProperties;

public class JPAMessageDaoTest extends MessageDaoTestCaseBase {
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
    
    // common test cases are written in the super class. 
}
