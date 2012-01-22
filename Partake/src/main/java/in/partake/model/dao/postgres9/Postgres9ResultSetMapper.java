package in.partake.model.dao.postgres9;

import in.partake.model.dao.DAOException;

import java.sql.ResultSet;

public abstract class Postgres9ResultSetMapper<T> {
    protected Postgres9Connection con;
    
    protected Postgres9ResultSetMapper(Postgres9Connection con) {
        this.con = con;
    }
    
    public abstract T map(ResultSet resultSet) throws DAOException;
}
