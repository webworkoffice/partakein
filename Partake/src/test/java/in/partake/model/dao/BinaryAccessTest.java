package in.partake.model.dao;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IBinaryAccess;
import in.partake.model.dto.BinaryData;
import in.partake.model.fixture.impl.BinaryTestDataProvider;

import org.junit.Before;

public class BinaryAccessTest extends AbstractDaoTestCaseBase<IBinaryAccess, BinaryData, String> {
    private BinaryTestDataProvider provider = new BinaryTestDataProvider();

    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getBinaryAccess());
    }

    @Override
    protected BinaryData create(long pkNumber, String pkSalt, int objNumber) {
        return provider.create(pkNumber, pkSalt, objNumber);
    }
}
