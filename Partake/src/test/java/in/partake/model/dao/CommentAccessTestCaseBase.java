package in.partake.model.dao;

import org.junit.Before;

public abstract class CommentAccessTestCaseBase extends AbstractDaoTestCaseBase {
    @Before
    public void setup() throws DAOException {
        super.setup();
        
        // --- remove all data before starting test.
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            con.beginTransaction();
            factory.getCommentAccess().truncate(con);
            con.commit();
        } finally {            
            con.invalidate();
        }
    }
}
