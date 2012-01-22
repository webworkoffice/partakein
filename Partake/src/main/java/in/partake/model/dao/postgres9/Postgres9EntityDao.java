package in.partake.model.dao.postgres9;

import in.partake.model.dao.DAOException;
import in.partake.util.PDate;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

/**
 * 
 * @author shinyak
 *
 */
public class Postgres9EntityDao extends Postgres9Dao {
    private final String tableName;
    
    public Postgres9EntityDao(String tableName) {
        this.tableName = tableName;
    }
    
    public void initialize(Postgres9Connection con) throws DAOException {
        makeSureExistEntitiesTable(con);
    }
    
    private void makeSureExistEntitiesTable(Postgres9Connection con) throws DAOException {
        try {
            if (existsTable(con, tableName))
                return;

            createEntitiesTable(con.getConnection());
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private void createEntitiesTable(Connection con) throws SQLException {
        // NOTE: Postgres9.1 has 'CREATE TABLE IF NOT EXISTS' though postgres9.0 does not have it.
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(
                    "CREATE TABLE " + tableName + "(" +
                    "    seq        BIGSERIAL   PRIMARY KEY," +
                    "    id         UUID        UNIQUE NOT NULL," +
                    "    version    INTEGER     NOT NULL," +
                    "    body       BYTEA       NOT NULL," +
                    "    opt        BYTEA," +
                    "    createdAt  TIMESTAMP   NOT NULL," +
                    "    modifiedAt TIMESTAMP " +
                    ")");             
             ps.execute();
        } finally {
            close(ps);
        }
    }
    
    public String getFreshId(Postgres9Connection con) throws DAOException {
        for (int i = 0; i < 5; ++i) {
            UUID uuid = UUID.randomUUID();
            if (!exists((Postgres9Connection) con, uuid.toString()))
                return uuid.toString();            
        }
        
        return null;
    }

    
    public void insert(Postgres9Connection pcon, Postgres9Entity entity) throws DAOException {
        Connection con = pcon.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("INSERT INTO " + tableName + "(id, version, body, opt, createdAt) VALUES(?, ?, ?, ?, ?)");            
            ps.setObject(1, entity.getId(), Types.OTHER);
            ps.setInt(2, entity.getVersion());
            ps.setBinaryStream(3, new ByteArrayInputStream(entity.getBody()), entity.getBodyLength());
            if (entity.getOpt() != null)
                ps.setBinaryStream(4, new ByteArrayInputStream(entity.getOpt()), entity.getOptLength());
            else
                ps.setNull(4, Types.NULL);
            ps.setDate(5, new Date(entity.getCreatedAt().getTime()));
            
            ps.execute();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(ps);
        }
    }
    
    public void update(Postgres9Connection pcon, Postgres9Entity entity) throws DAOException {
        Connection con = pcon.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("UPDATE " + tableName + " SET version = ?, body = ?, opt = ?, modifiedAt = ? WHERE id = ?");
            ps.setInt(1, entity.getVersion());
            ps.setBinaryStream(2, new ByteArrayInputStream(entity.getBody()), entity.getBodyLength());
            if (entity.getOpt() != null)
                ps.setBinaryStream(3, new ByteArrayInputStream(entity.getOpt()), entity.getOptLength());
            else
                ps.setNull(3, Types.NULL);
            ps.setDate(4, new Date(PDate.getCurrentTime()));
            ps.setObject(5, entity.getId(), Types.OTHER);
            
            ps.execute();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(ps);
        }
    }    
    
    public boolean exists(Postgres9Connection pcon, String id) throws DAOException {
        Connection con = pcon.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT 1 FROM " + tableName + " WHERE id = ?");
            ps.setObject(1, id, Types.OTHER);

            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(rs);
            close(ps);
        }        
    }
    
    public Postgres9Entity find(Postgres9Connection pcon, String id) throws DAOException {
        Connection con = pcon.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT version, body, opt, createdAt, modifiedAt FROM " + tableName + " WHERE id = ?");
            ps.setObject(1, id, Types.OTHER);

            rs = ps.executeQuery();
            if (rs.next()) {
                int version = rs.getInt(1);
                byte[] body = rs.getBytes(2);
                byte[] opt = rs.getBytes(3);
                Date createdAt = rs.getDate(4);
                Date modifiedAt = rs.getDate(5);
                return new Postgres9Entity(id, version, body, opt, createdAt, modifiedAt);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(rs);
            close(ps);
        }        
    }
    
    /** Removes */
    public void remove(Postgres9Connection pcon, String id) throws DAOException {
        Connection con = pcon.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("DELETE FROM " + tableName + " WHERE id = ?");
            ps.setObject(1, id, Types.OTHER);

            ps.execute();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(ps);
        }                
    }
    
    /** Removes all entities. All data might be lost. You should call this very carefully. */
    public void truncate(Postgres9Connection pcon) throws DAOException {
        Connection con = pcon.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("DELETE FROM " + tableName);
            ps.execute();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(ps);
        }
    }
}
