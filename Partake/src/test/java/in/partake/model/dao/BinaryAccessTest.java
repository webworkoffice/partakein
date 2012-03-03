package in.partake.model.dao;

import in.partake.model.dao.access.IBinaryAccess;
import in.partake.model.dto.BinaryData;
import in.partake.model.fixture.TestDataProvider;
import in.partake.model.fixture.impl.BinaryTestDataProvider;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BinaryAccessTest extends AbstractDaoTestCaseBase<IBinaryAccess, BinaryData, String> {
    private BinaryTestDataProvider provider = new BinaryTestDataProvider();

    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getBinaryAccess());
    }

    @Override
    protected BinaryData create(long pkNumber, String pkSalt, int objNumber) {
        return provider.create(pkNumber, pkSalt, objNumber);
    }
    
    @Test
    public void testToFindIdsByUserId() throws Exception {
        PartakeConnection con = getPool().getConnection();
        
        try {
            // Create test data.
            BinaryData[] data = new BinaryData[10];
            for (int i = 0; i < 10; ++i) {
                data[i] = create(i, "findIds", i);
                data[i].setCreatedAt(new Date(10 - i));
            }

            con.beginTransaction();
            for (int i = 0; i < 10; ++i)
                dao.put(con, data[i]);
            con.commit();

            // Do test
            List<String> result = dao.findIdsByUserId(con, TestDataProvider.USER_ID1, 0, 10);
            Assert.assertEquals(10, result.size());
            for (int i = 0; i < 10; ++i)
                Assert.assertEquals(data[i].getId(), result.get(i));
            
            result = dao.findIdsByUserId(con, TestDataProvider.USER_ID1, 0, 5);
            Assert.assertEquals(5, result.size());
            for (int i = 0; i < 5; ++i)
                Assert.assertEquals(data[i].getId(), result.get(i));
            
            result = dao.findIdsByUserId(con, TestDataProvider.USER_ID1, 5, 10);
            Assert.assertEquals(5, result.size());
            for (int i = 0; i < 5; ++i)
                Assert.assertEquals(data[i + 5].getId(), result.get(i));
        } finally {
            con.invalidate();
        }
    }
}
