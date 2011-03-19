package in.partake.model.dao.jpa;

import in.partake.model.dao.EnvelopeDaoTestCaseBase;
import in.partake.resource.PartakeProperties;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class JPAEnvelopeDaoTest extends EnvelopeDaoTestCaseBase {
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
