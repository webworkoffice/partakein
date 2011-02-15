package in.partake.model.dao;

import in.partake.model.dto.EventRelation;
import in.partake.model.dto.pk.EventRelationPK;

import org.junit.Before;

public abstract class EventRelationAccessTestCaseBase extends AbstractDaoTestCaseBase<IEventRelationAccess, EventRelation, EventRelationPK> {
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getEventRelationAccess());
    }
    
    @Override
    protected EventRelation create(long pkNumber, String pkSalt, int objNumber) {
        return new EventRelation(pkSalt + pkNumber, pkSalt + pkNumber, (objNumber & 2) > 0, (objNumber & 1) > 0);
    }
}
