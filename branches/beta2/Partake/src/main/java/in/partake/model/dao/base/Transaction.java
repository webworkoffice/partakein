package in.partake.model.dao.base;

import in.partake.model.dao.PartakeConnection;
import in.partake.service.DBService;

public abstract class Transaction<S, T> {
    public final T transaction(S data) throws Exception {
        PartakeConnection con = DBService.getPool().getConnection();
        try {
            con.beginTransaction();
            T result = doTransaction(con, data);
            con.commit();
            
            return result;
        } finally {
            con.invalidate();
        }
    }
    
    protected abstract T doTransaction(PartakeConnection con, S data) throws Exception;
}
