package in.partake.model.dao.jpa;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import in.partake.model.dao.EnrollmentAccessTestCaseBase;
import in.partake.resource.PartakeProperties;

public class JPAEnrollmentAccessTest extends EnrollmentAccessTestCaseBase {
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
