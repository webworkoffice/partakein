package in.partake.model.dao.postgres9;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Postgres9Dao {

    protected boolean existsTable(Connection con, String tableName) throws SQLException {
        PreparedStatement ps = con.prepareStatement(
                "SELECT table_name FROM information_schema.tables " +
                        "WHERE table_schema = 'public' " +
                        "AND table_name = ?"
        );
        
        ResultSet rs = null;
        try {
            ps.setString(1, tableName);
            rs = ps.executeQuery();
            return rs.next();
        } finally {
            close(rs);
            close(ps);
        }
    }

    
    protected void close(PreparedStatement ps) {
        if (ps == null)
            return;
        
        try {
            ps.close();
        } catch (SQLException e) {
            // squash!
        }
    }

    protected void close(ResultSet rs) {
        if (rs == null)
            return;
        
        try {
            rs.close();
        } catch (SQLException e) {
            // squash!
        }
    }   
}
