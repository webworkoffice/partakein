package in.partake.model.dao;

import in.partake.model.dto.BinaryData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class BinaryAccessTestCaseBase extends AbstractDaoTestCaseBase<IBinaryAccess, BinaryData, String> {
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getBinaryAccess());
    }

    @Override
    protected BinaryData create(long pkNumber, String pkSalt, int objNumber) {
        if (objNumber == 0) {
            int N = 1024 * 1024;
            byte[] data = new byte[N];
            for (int i = 0; i < N; ++i) {
                data[i] = (byte)(i % N);
            }
            return new BinaryData(pkSalt + "-" + pkNumber, "test-type", data);
        } else {
            return new BinaryData(pkSalt + "-" + pkNumber, "test-type", new byte[] {1, 2, (byte) objNumber});
        }            
    }
    
    @Test
    public void testToRemoveBinaryData() throws Exception {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            {
                con.beginTransaction();
                BinaryData data = new BinaryData("test", "test-type", new byte[] {1, 2, 3});
                factory.getBinaryAccess().put(con, data);
                con.commit();
            }
            {
                con.beginTransaction();
                factory.getBinaryAccess().remove(con, "test");
                con.commit();
            }
            {
                con.beginTransaction();
                BinaryData data = factory.getBinaryAccess().find(con, "test");
                Assert.assertNull(data);
                con.commit();
            }
            
        } finally {
            con.invalidate();
        }        
    }    
}
