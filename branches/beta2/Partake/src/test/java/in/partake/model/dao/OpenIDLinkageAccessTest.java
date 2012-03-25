package in.partake.model.dao;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IOpenIDLinkageAccess;
import in.partake.model.dto.OpenIDLinkage;

import org.junit.Before;

public class OpenIDLinkageAccessTest extends AbstractDaoTestCaseBase<IOpenIDLinkageAccess, OpenIDLinkage, String> {
    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getOpenIDLinkageAccess());
    }

    @Override
    protected OpenIDLinkage create(long pkNumber, String pkSalt, int objNumber) {
        return new OpenIDLinkage("id" + pkSalt + pkNumber, "userId" + objNumber);
    }
}
