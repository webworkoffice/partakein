package in.partake.model.fixture.impl;

import in.partake.base.Util;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IThumbnailAccess;
import in.partake.model.dto.ThumbnailData;
import in.partake.model.fixture.TestDataProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ThumbnailTestDataProvider extends TestDataProvider<ThumbnailData> {
    private final byte[] defaultImageContent;

    public ThumbnailTestDataProvider() {
        try {
            defaultImageContent = Util.getContentOfFile(new File("src/test/resources/images/null.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ThumbnailData create(long pkNumber, String pkSalt, int objNumber) {
        UUID id = new UUID(pkNumber, pkSalt.hashCode());
        return new ThumbnailData(id.toString(), "userId", "image/png", new byte[] { 0, 1, (byte) objNumber } , new Date(0L));
    }

    @Override
    public List<ThumbnailData> createSamples() {
        List<ThumbnailData> array = new ArrayList<ThumbnailData>();
        array.add(new ThumbnailData("id", "userId", "image/png", new byte[] { 0, 1, 2 }, new Date(0L)));
        array.add(new ThumbnailData("id1", "userId", "image/png", new byte[] { 0, 1, 2 }, new Date(0L)));
        array.add(new ThumbnailData("id", "userId1", "image/png", new byte[] { 0, 1, 2 }, new Date(0L)));
        array.add(new ThumbnailData("id", "userId", "image/jpeg", new byte[] { 0, 1, 2 }, new Date(0L)));
        array.add(new ThumbnailData("id", "userId", "image/png", new byte[] { 0, 1, 3 }, new Date(0L)));
        array.add(new ThumbnailData("id", "userId", "image/png", new byte[] { 0, 1, 2 }, new Date(1L)));
        return array;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IThumbnailAccess dao = daos.getThumbnailAccess();
        dao.truncate(con);

        dao.put(con, new ThumbnailData(DEFAULT_IMAGE_ID, DEFAULT_USER_ID, "byte/octet-stream", defaultImageContent, new Date(0L)));
    }
}
