package in.partake.model.dao;

import in.partake.model.dto.OpenIDLinkage;

import org.junit.Before;

public class OpenIDLinkageAccessTest extends AbstractDaoTestCaseBase<IOpenIDLinkageAccess, OpenIDLinkage, String> {
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getOpenIDLinkageAccess());
    }
    
    @Override
    protected OpenIDLinkage create(long pkNumber, String pkSalt, int objNumber) {
        return new OpenIDLinkage("id" + pkSalt + pkNumber, "userId" + objNumber);
    }
}
