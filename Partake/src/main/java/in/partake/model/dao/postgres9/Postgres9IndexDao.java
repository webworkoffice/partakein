package in.partake.model.dao.postgres9;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import in.partake.model.dao.DAOException;

public class Postgres9IndexDao extends Postgres9Dao {
    private String indexTableName;
    
    public Postgres9IndexDao(String indexTableName) {
        this.indexTableName = indexTableName;
    }
    
    public void createIndexTable(Postgres9Connection con, String tableDeclaration) throws DAOException {
        executeSQL(con, tableDeclaration);
    }
    
    public void createIndex(Postgres9Connection con, String indexDeclaration) throws DAOException {
        executeSQL(con, indexDeclaration);        
    }

    /** Be careful about using this. */
    public String find(Postgres9Connection con, String columnName, String value) throws DAOException {
        try {
            return find(con.getConnection(), columnName, value);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }
    
    private String find(Connection con, String columnName, String value) throws SQLException {
        String sql = "SELECT id FROM " + indexTableName + " WHERE " + columnName + " = ?";

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            if (rs.next())
                return (String) rs.getObject(1);
            else
                return null;
        } finally {
            close(rs);
            close(ps);
        }
    }
}
