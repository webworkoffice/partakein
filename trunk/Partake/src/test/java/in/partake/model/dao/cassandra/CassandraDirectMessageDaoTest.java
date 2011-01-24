package in.partake.model.dao.cassandra;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import in.partake.model.dao.DirectMessageDaoTestCaseBase;
import in.partake.resource.PartakeProperties;

/**
 * @see DirectMessageCassandraDao
 * @author skypencil(@eller86)
 */
public class CassandraDirectMessageDaoTest extends DirectMessageDaoTestCaseBase {
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
