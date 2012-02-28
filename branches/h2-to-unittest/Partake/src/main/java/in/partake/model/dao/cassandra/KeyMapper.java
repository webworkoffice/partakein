package in.partake.model.dao.cassandra;

import in.partake.model.dao.DAOException;

public abstract class KeyMapper<T> {
    private CassandraConnection con;
    
    protected KeyMapper(CassandraConnection con) {
        this.con = con;
    }
    
    /**
     * @return
     */
    protected abstract T map(String key) throws DAOException;

    protected CassandraConnection getConnection() {
        return con;
    }
}
