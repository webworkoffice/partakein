package in.partake.model.dao.postgres9;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.util.PDate;

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

    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;        
        makeSureExistEntitiesTable(pcon);
    }
    
    protected boolean existsTable(PartakeConnection con, String tableName) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        try {
            return existsTable(pcon.getConnection(), tableName);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }
    
    private void makeSureExistEntitiesTable(Postgres9Connection pcon) throws DAOException {
        Connection con = pcon.getConnection();

        try {
            if (existsTable(con, "entities"))
                return;

            createEntitiesTable(con);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private void createEntitiesTable(Connection con) throws SQLException {
        // NOTE: Postgres9.1 has 'CREATE TABLE IF NOT EXISTS' though postgres9.0 does not have it.
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(
                    "CREATE TABLE entities(" +
                    "    seq        SERIAL      PRIMARY KEY," +
                    "    id         UUID        UNIQUE NOT NULL," +
                    "    schema     TEXT        NOT NULL," +
                    "    data       TEXT        NOT NULL," +
                    "    createdAt  TIMESTAMP   NOT NULL,"  +
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
            ps = con.prepareStatement("INSERT INTO entities(id, schema, data, createdAt) VALUES(?, ?, ?, ?)");            
            ps.setObject(1, entity.getId(), Types.OTHER);
            ps.setString(2, entity.getSchema());
            ps.setString(3, entity.getData());
            ps.setDate(4, new Date(entity.getCreatedAt().getTime()));
            
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
            ps = con.prepareStatement("UPDATE entities SET data = ?, modifiedAt = ? WHERE id = ?");
            ps.setString(1, entity.getData());
            ps.setDate(2, new Date(PDate.getCurrentTime()));
            ps.setObject(3, entity.getId(), Types.OTHER);
            
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
            ps = con.prepareStatement("SELECT 1 FROM entities WHERE id = ?");
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
            ps = con.prepareStatement("SELECT schema, data, createdAt, modifiedAt FROM entities WHERE id = ?");
            ps.setObject(1, id, Types.OTHER);

            rs = ps.executeQuery();
            if (rs.next()) {
                String schema = rs.getString(1);
                String data = rs.getString(2);
                Date createdAt = rs.getDate(3);
                Date modifiedAt = rs.getDate(4);
                return new Postgres9Entity(id, schema, data, createdAt, modifiedAt);
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
            ps = con.prepareStatement("DELETE FROM entities WHERE id = ?");
            ps.setObject(1, id, Types.OTHER);

            ps.execute();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(ps);
        }                
    }
    
    /** Removes all entities having <code>schema</code>. All data might be lost. You should call this very carefully. */ 
    public void removeEntitiesHavingSchema(Postgres9Connection pcon, String schema) throws DAOException {
        Connection con = pcon.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("DELETE FROM entities WHERE schema = ?");
            ps.setString(1, schema);
            ps.execute();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(ps);
        }
    }
}
