package in.partake.service;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;

import org.apache.log4j.Logger;

/**
 * test 用の関数が登録された
 * @author shinyak
 *
 */
public final class TestService extends PartakeService {
    private static TestService instance = new TestService();
    private static Logger logger = Logger.getLogger(TestService.class);

    public static TestService get() {
        return instance;
    }
    
    private TestService() {
        // do nothing for now.
    }

    // ----------------------------------------------------------------------
    
    public void createUser(User user, TwitterLinkage linkage) throws DAOException {
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();         
        try {
            con.beginTransaction(); 
            
            factory.getUserAccess().put(con, user);
            factory.getTwitterLinkageAccess().put(con, linkage);

            con.commit();
        } finally {
            con.invalidate();            
        }
    }
}
