package in.partake.model.fixture.impl;

import in.partake.base.TimeUtil;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
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
        return new ImageData(UUID.randomUUID().toString(), DEFAULT_USER_ID, "test", new byte[] { 1, 2, 3 }, TimeUtil.getCurrentDate());
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

            return new ImageData(uuid.toString(), DEFAULT_USER_ID, "data/octet-stream", data, new Date(objNumber));
        } else {
            return new ImageData(uuid.toString(), DEFAULT_USER_ID, "data/octet-stream", new byte[] { 1, 2, (byte) objNumber }, new Date(objNumber));
        }
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IImageAccess dao = daos.getImageAccess();
        dao.truncate(con);

        // IMAGE_OWNED_BY_DEFAULT_USER_ID contains DEFAULT_IMAGE_ID.
        dao.put(con, new ImageData(EVENT_FOREIMAGE_ID, EVENT_OWNER_ID, "byte/octet-stream", BYTE1_CONTENT, TimeUtil.getCurrentDate()));
        dao.put(con, new ImageData(EVENT_BACKIMAGE_ID, EVENT_OWNER_ID, "byte/octet-stream", BYTE1_CONTENT, TimeUtil.getCurrentDate()));
        for (int i = 0; i < IMAGE_OWNED_BY_DEFAULT_USER_ID.length; ++i)
            dao.put(con, new ImageData(IMAGE_OWNED_BY_DEFAULT_USER_ID[i], DEFAULT_USER_ID, "byte/octet-stream", BYTE1_CONTENT, new Date(IMAGE_OWNED_BY_DEFAULT_USER_ID.length - i)));
    }
}
