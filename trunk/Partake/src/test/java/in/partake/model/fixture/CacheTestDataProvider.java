package in.partake.model.fixture;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.ICacheAccess;
import in.partake.model.dto.CacheData;

import java.util.Date;

/**
 * Cache のテストデータを作成します。
 * @author shinyak
 *
 */
public class CacheTestDataProvider {

    public CacheData create() {
        return new CacheData("id", new byte[] { 1, 2, 3 }, new Date());
    }
    
    public CacheData create(long pkNumber, String pkSalt, int objNumber) {
        if (objNumber == 0) {
            int N = 1024 * 1024;
            byte[] data = new byte[N];
            for (int i = 0; i < N; ++i) {
                data[i] = (byte)(i % N);
            }
            return new CacheData(pkSalt + pkNumber, data, new Date(Long.MAX_VALUE));

        } else {
            return new CacheData(pkSalt + pkNumber, new byte[] { 1, 2, (byte)objNumber }, new Date(Long.MAX_VALUE));
        }
    }
    
    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        ICacheAccess dao = factory.getCacheAccess();        
        dao.truncate(con);
        
        // 特に CacheData の fixture は現在用意しない。必要に応じて追加すること。
    }
}
