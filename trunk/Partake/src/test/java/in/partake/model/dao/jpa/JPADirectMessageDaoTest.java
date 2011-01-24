package in.partake.model.dao.jpa;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import in.partake.model.dao.DirectMessageDaoTestCaseBase;
import in.partake.resource.PartakeProperties;

public class JPADirectMessageDaoTest extends DirectMessageDaoTestCaseBase {
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
