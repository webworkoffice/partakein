package in.partake.model.dao;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IThumbnailAccess;
import in.partake.model.dto.ThumbnailData;

import org.junit.Before;

public class ThumbnailAccessTest extends AbstractDaoTestCaseBase<IThumbnailAccess, ThumbnailData, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getThumbnailAccess());
    }

    @Override
    protected ThumbnailData create(long pkNumber, String pkSalt, int objNumber) {
        return PartakeApp.getTestService().getTestDataProviderSet().getThumbnailProvider().create(pkNumber, pkSalt, objNumber);
    }
}
