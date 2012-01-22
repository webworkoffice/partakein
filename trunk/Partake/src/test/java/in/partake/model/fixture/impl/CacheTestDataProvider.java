package in.partake.model.fixture.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.ICacheAccess;
import in.partake.model.dto.CacheData;
import in.partake.model.fixture.TestDataProvider;

import java.util.Date;
import java.util.UUID;

/**
 * Cache のテストデータを作成します。
 * @author shinyak
 *
 */
public class CacheTestDataProvider extends TestDataProvider<CacheData> {

    @Override
    public CacheData create() {
        UUID uuid = new UUID(0, 1);
        return new CacheData(uuid.toString(), new byte[] { 1, 2, 3 }, new Date());
    }
    
    @Override
    public CacheData create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, ("cache" + pkSalt).hashCode());
        if (objNumber == 0) {
            int N = 1024 * 1024;
            byte[] data = new byte[N];
            for (int i = 0; i < N; ++i) {
                data[i] = (byte)(i % N);
            }
            
            return new CacheData(uuid.toString(), data, new Date(Long.MAX_VALUE));
        } else {
            return new CacheData(uuid.toString(), new byte[] { 1, 2, (byte) objNumber }, new Date(Long.MAX_VALUE));
        }
    }
    
    @Override
    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        ICacheAccess dao = factory.getCacheAccess();        
        dao.truncate(con);
        
        // 特に CacheData の fixture は現在用意しない。必要に応じて追加すること。
    }
}
