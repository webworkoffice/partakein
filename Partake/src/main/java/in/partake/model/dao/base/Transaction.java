package in.partake.model.dao.base;

import in.partake.model.dao.PartakeConnection;
import in.partake.service.DBService;

public abstract class Transaction<T> {
    public final T transaction() throws Exception {
        PartakeConnection con = DBService.getPool().getConnection();
        try {
            con.beginTransaction();
            T result = doTransaction(con);
            con.commit();
            
            return result;
        } finally {
            con.invalidate();
        }
    }
    
    protected abstract T doTransaction(PartakeConnection con) throws Exception;
}
