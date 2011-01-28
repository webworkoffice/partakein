package in.partake.model.dao;

import org.junit.Before;

public abstract class EventRelationAccessTestCaseBase extends AbstractDaoTestCaseBase {
    @Before
    public void setup() throws DAOException {
        super.setup();
        
        // --- remove all data before starting test.
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            con.beginTransaction();
            factory.getEventRelationAccess().truncate(con);
            con.commit();
        } finally {            
            con.invalidate();
        }
    }
}
