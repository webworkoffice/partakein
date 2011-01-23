package in.partake.model.dao.cassandra;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import in.partake.resource.PartakeProperties;
import in.partake.service.PartakeService;

public class CassandraDaoTestBase {

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
}
