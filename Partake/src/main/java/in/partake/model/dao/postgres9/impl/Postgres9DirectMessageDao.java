package in.partake.model.dao.postgres9.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.DataMapper;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IMessageAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9DataIterator;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dao.postgres9.Postgres9StatementAndResultSet;
import in.partake.model.dto.Message;
import in.partake.util.PDate;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.json.JSONObject;

// TODO: Should be renamed to Postgres9MessageDao.
public class Postgres9DirectMessageDao extends Postgres9Dao implements IMessageAccess {
    static final String TABLE_NAME = "MessageEntities";
    static final String INDEX_TABLE_NAME = "MessageIndex";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;

    public Postgres9DirectMessageDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);
        
        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            // event id may be NULL if system message.
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, eventId TEXT, createdAt TIMESTAMP NOT NULL)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "EventId" + " ON " + INDEX_TABLE_NAME + "(eventId, createdAt)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.truncate(pcon);
        indexDao.truncate(pcon);
    }

    @Override
    public void put(PartakeConnection con, Message t) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        // TODO: Why User does not have createdAt and modifiedAt?
        Postgres9Entity entity = new Postgres9Entity(t.getId(), CURRENT_VERSION, t.toJSON().toString().getBytes(UTF8), null, PDate.getCurrentDate().getDate());
        if (entityDao.exists(pcon, t.getId()))
            entityDao.update(pcon, entity);            
        else
            entityDao.insert(pcon, entity);
        indexDao.put(pcon, new String[] { "id", "eventId", "createdAt" }, new Object[] { t.getId(), t.getEventId(), t.getCreatedAt() } );
    }

    @Override
    public Message find(PartakeConnection con, String id) throws DAOException {
        Postgres9Entity entity = entityDao.find((Postgres9Connection) con, id);
        if (entity == null)
            return null;

        JSONObject json = JSONObject.fromObject(new String(entity.getBody(), UTF8));
        return new Message(json).freeze();
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.remove(pcon, id);
        indexDao.remove(pcon, "id", id);
    }

    @Override
    public DataIterator<Message> getIterator(PartakeConnection con) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }

    @Override
    public DataIterator<Message> findByEventId(PartakeConnection con, String eventId) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE eventId = ? ORDER BY createdAt DESC",
                new Object[] { eventId });

        class Mapper implements DataMapper<ResultSet, Message> {
            private Postgres9Connection con;
            
            public Mapper(Postgres9Connection con) {
                this.con = con;
            }
            
            @Override
            public Message map(ResultSet rs) throws DAOException {
                try {
                    String id = rs.getString("id");
                    if (id == null)
                        return null;
                    
                    return Postgres9DirectMessageDao.this.find((Postgres9Connection) con, id);
                } catch (SQLException e) {
                    throw new DAOException(e);
                }
            }

            @Override
            public ResultSet unmap(Message t) throws DAOException {
                throw new UnsupportedOperationException();
            }
        }
        
        return new Postgres9DataIterator<Message>(new Mapper((Postgres9Connection) con), psars);
    }

}
