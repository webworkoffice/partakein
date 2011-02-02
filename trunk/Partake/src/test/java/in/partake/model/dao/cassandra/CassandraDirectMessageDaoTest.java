package in.partake.model.dao.cassandra;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import in.partake.model.dao.MessageDaoTestCaseBase;
import in.partake.resource.PartakeProperties;

/**
 * @see MessageCassandraDao
 * @author skypencil(@eller86)
 */
public class CassandraDirectMessageDaoTest extends MessageDaoTestCaseBase {
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
}
