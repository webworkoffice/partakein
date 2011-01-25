package in.partake.model.dao;

import org.junit.Before;

public abstract class FeedAccessTestCaseBase extends AbstractDaoTestCaseBase {
    @Before
    public void setup() throws DAOException {
        // --- remove all data before starting test.
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            con.beginTransaction();
            factory.getFeedAccess().truncate(con);
            con.commit();
        } finally {            
            con.invalidate();
        }
    }
}
