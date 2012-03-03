package in.partake.model.fixture.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.IBinaryAccess;
import in.partake.model.dto.BinaryData;
import in.partake.model.fixture.TestDataProvider;

import java.util.Date;
import java.util.UUID;

/**
 * BinaryData のテストデータを作成します。
 * @author shinyak
 *
 */
public class BinaryTestDataProvider extends TestDataProvider<BinaryData> {
    public static final byte[] BYTE1_CONTENT = new byte[] { 1, 2, 3 };  
    
    @Override
    public BinaryData create() {
        return new BinaryData(UUID.randomUUID().toString(), USER_ID1, "test", new byte[] { 1, 2, 3 }, new Date(0));
    }
    
    @Override
    public BinaryData create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, ("binary" + pkSalt).toString().hashCode());
        
        if (objNumber == 0) {
            int N = 1024 * 1024;
            byte[] data = new byte[N];
            for (int i = 0; i < N; ++i) {
                data[i] = (byte)(i % N);
            }
            
            return new BinaryData(uuid.toString(), USER_ID1, "data/octet-stream", data, new Date(0));
        } else {
            return new BinaryData(uuid.toString(), USER_ID1, "data/octet-stream", new byte[] { 1, 2, (byte) objNumber }, new Date(0));
        }
    }
    
    @Override
    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        IBinaryAccess dao = factory.getBinaryAccess();
        dao.truncate(con);
        
        dao.put(con, new BinaryData(IMAGE_ID1, USER_ID1, "byte/octet-stream", BYTE1_CONTENT, new Date(0)));
    }
}
