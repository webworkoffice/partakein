package in.partake.model.fixture.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.IImageAccess;
import in.partake.model.dto.ImageData;
import in.partake.model.fixture.TestDataProvider;

import java.util.Date;
import java.util.UUID;

/**
 * ImageData のテストデータを作成します。
 * @author shinyak
 *
 */
public class ImageTestDataProvider extends TestDataProvider<ImageData> {
    public static final byte[] BYTE1_CONTENT = new byte[] { 1, 2, 3 };  
    
    @Override
    public ImageData create() {
        return new ImageData(UUID.randomUUID().toString(), USER_ID1, "test", new byte[] { 1, 2, 3 }, TimeUtil.getCurrentDate());
    }
    
    @Override
    public ImageData create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, ("image" + pkSalt).toString().hashCode());
        
        if (objNumber == 0) {
            int N = 1024 * 1024;
            byte[] data = new byte[N];
            for (int i = 0; i < N; ++i) {
                data[i] = (byte)(i % N);
            }
            
            return new ImageData(uuid.toString(), USER_ID1, "data/octet-stream", data, TimeUtil.getCurrentDate());
        } else {
            return new ImageData(uuid.toString(), USER_ID1, "data/octet-stream", new byte[] { 1, 2, (byte) objNumber }, TimeUtil.getCurrentDate());
        }
    }
    
    @Override
    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        IImageAccess dao = factory.getImageAccess();
        dao.truncate(con);
        
        dao.put(con, new ImageData(IMAGE_ID1, USER_ID1, "byte/octet-stream", BYTE1_CONTENT, TimeUtil.getCurrentDate()));
        for (int i = 0; i < IMAGE_ID_OWNED_BY_USER2.length; ++i)
            dao.put(con, new ImageData(IMAGE_ID_OWNED_BY_USER2[i], USER_ID2, "byte/octet-stream", BYTE1_CONTENT, new Date(IMAGE_ID_OWNED_BY_USER2.length - i)));
    }
}
