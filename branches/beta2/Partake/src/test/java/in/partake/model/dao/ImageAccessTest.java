package in.partake.model.dao;

import in.partake.model.dao.access.IImageAccess;
import in.partake.model.dto.ImageData;
import in.partake.model.fixture.TestDataProvider;
import in.partake.model.fixture.impl.ImageTestDataProvider;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ImageAccessTest extends AbstractDaoTestCaseBase<IImageAccess, ImageData, String> {
    private ImageTestDataProvider provider = new ImageTestDataProvider();

    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getImageAccess());
    }

    @Override
    protected ImageData create(long pkNumber, String pkSalt, int objNumber) {
        return provider.create(pkNumber, pkSalt, objNumber);
    }
    
    @Test
    public void testToFindIdsByUserId() throws Exception {
        PartakeConnection con = getPool().getConnection();
        
        try {
            // Create test data.
            ImageData[] data = new ImageData[10];
            for (int i = 0; i < 10; ++i) {
                data[i] = create(i, "findIds", i);
                data[i].setCreatedAt(new Date(10 - i));
            }

            con.beginTransaction();
            for (int i = 0; i < 10; ++i)
                dao.put(con, data[i]);
            con.commit();

            // Do test
            List<String> result = dao.findIdsByUserId(con, TestDataProvider.DEFAULT_USER_ID, 0, 10);
            Assert.assertEquals(10, result.size());
            for (int i = 0; i < 10; ++i)
                Assert.assertEquals(data[i].getId(), result.get(i));
            
            result = dao.findIdsByUserId(con, TestDataProvider.DEFAULT_USER_ID, 0, 5);
            Assert.assertEquals(5, result.size());
            for (int i = 0; i < 5; ++i)
                Assert.assertEquals(data[i].getId(), result.get(i));
            
            result = dao.findIdsByUserId(con, TestDataProvider.DEFAULT_USER_ID, 5, 10);
            Assert.assertEquals(5, result.size());
            for (int i = 0; i < 5; ++i)
                Assert.assertEquals(data[i + 5].getId(), result.get(i));
        } finally {
            con.invalidate();
        }
    }
}
