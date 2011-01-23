package in.partake.model.dao.jpa;

import in.partake.resource.PartakeProperties;
import in.partake.service.PartakeService;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class JPADaoTestBase extends JPADao {
    @BeforeClass
    public static void setUpOnce() {
        PartakeProperties.get().reset("jpa");
        PartakeService.reset();
    }
    
    @AfterClass
    public static void tearDownOnce() {
        PartakeProperties.get().reset();
        PartakeService.reset();        
    }
}
