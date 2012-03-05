package in.partake.model.dao.base;

import in.partake.base.PartakeException;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.service.DBService;

public abstract class Transaction<T> {
    public final T transaction() throws DAOException, PartakeException {
        PartakeConnection con = DBService.getPool().getConnection();
        try {
            con.beginTransaction();
            T result = doTransaction(con);
            
            if (con.isInTransaction())
                con.commit();
            
            return result;
        } finally {
            con.invalidate();
        }
    }
    
    protected abstract T doTransaction(PartakeConnection con) throws DAOException, PartakeException;
}
