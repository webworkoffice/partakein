package in.partake.model.dao.postgres9;

import in.partake.model.dao.DAOException;

import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Base class of Postgres9Dao.
 * @author shinyak
 *
 */
public abstract class Postgres9Dao {
    static protected Charset UTF8 = Charset.forName("utf-8");

    /** Checks the existence of table. 
     * @return true if the specified table exists. 
     */
    protected boolean existsTable(Postgres9Connection pcon, String tableName) throws DAOException {
        try {
            return existsTable(pcon.getConnection(), tableName);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    /**
     * Executes SQL.
     */
    protected void executeSQL(Postgres9Connection pcon, String sql) throws DAOException {
        try {
            executeSQL(pcon.getConnection(), sql);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    /** Closes PreparedStatement silently. */
    protected void close(PreparedStatement ps) {
        if (ps == null)
            return;

        try {
            ps.close();
        } catch (SQLException e) {
            // squash!
        }
    }

    /** Closes ResultSet silently. */
    protected void close(ResultSet rs) {
        if (rs == null)
            return;

        try {
            rs.close();
        } catch (SQLException e) {
            // squash!
        }
    }

    private void executeSQL(Connection pcon, String sql) throws SQLException {
        PreparedStatement ps = null;

        try {
            ps = pcon.prepareStatement(sql);
            ps.execute();
        } finally {
            close(ps);
        }
    }

    private boolean existsTable(Connection con, String tableName) throws SQLException {
        PreparedStatement ps = null;        
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT table_name FROM information_schema.tables " +
                    "WHERE table_schema = 'public' " +
                    "AND lower(table_name) = lower(?)");

            ps.setString(1, tableName);
            rs = ps.executeQuery();
            return rs.next();
        } finally {
            close(rs);
            close(ps);
        }
    }
}
