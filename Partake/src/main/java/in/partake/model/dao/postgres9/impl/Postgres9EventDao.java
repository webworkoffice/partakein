package in.partake.model.dao.postgres9.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.DataMapper;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9DataIterator;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dao.postgres9.Postgres9StatementAndResultSet;
import in.partake.model.dto.Event;
import in.partake.util.PDate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

public class Postgres9EventDao extends Postgres9Dao implements IEventAccess {
    static final String TABLE_NAME = "EventEntities";
    static final int CURRENT_VERSION = 1;
    static final String OWNER_INDEX_TABLE_NAME = "EventOwnerIndex";   // For owner
    static final String EDITOR_INDEX_TABLE_NAME = "EventEditorIndex"; // For editor 

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao ownerIndexDao;
    private final Postgres9IndexDao editorIndexDao;

    public Postgres9EventDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.ownerIndexDao = new Postgres9IndexDao(OWNER_INDEX_TABLE_NAME);
        this.editorIndexDao = new Postgres9IndexDao(EDITOR_INDEX_TABLE_NAME);
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);
        
        if (!existsTable(pcon, OWNER_INDEX_TABLE_NAME)) {
            ownerIndexDao.createIndexTable(pcon, "CREATE TABLE " + OWNER_INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, ownerId TEXT NOT NULL)");
            ownerIndexDao.createIndex(pcon, "CREATE INDEX " + OWNER_INDEX_TABLE_NAME + "OwnerId" + " ON " + OWNER_INDEX_TABLE_NAME + "(ownerId)");
        }

        if (!existsTable(pcon, EDITOR_INDEX_TABLE_NAME)) {
            editorIndexDao.createIndexTable(pcon, "CREATE TABLE " + EDITOR_INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, editorNames TEXT NOT NULL)");
            editorIndexDao.createIndex(pcon, "CREATE INDEX " + EDITOR_INDEX_TABLE_NAME + "EditorName" + " ON " + EDITOR_INDEX_TABLE_NAME + "(editorNames)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.truncate(pcon);
        ownerIndexDao.truncate(pcon);
        editorIndexDao.truncate(pcon);
    }

    @Override
    public void put(PartakeConnection con, Event event) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        Postgres9Entity entity = new Postgres9Entity(event.getId(), CURRENT_VERSION, event.toJSON().toString().getBytes(UTF8), null, PDate.getCurrentDate().getDate());
        if (entityDao.exists(pcon, event.getId()))
            entityDao.update(pcon, entity);            
        else
            entityDao.insert(pcon, entity);

        ownerIndexDao.put(pcon, new String[] {"id", "ownerId"}, new String[] { event.getId(), event.getOwnerId() });
        if (event.getManagerScreenNames() != null)
            editorIndexDao.put(pcon, new String[] {"id", "editorNames"}, new String[] { event.getId(), event.getManagerScreenNames() });
        else
            editorIndexDao.remove(pcon, "id", event.getId());
    }

    @Override
    public Event find(PartakeConnection con, String id) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;        
        Postgres9Entity entity = entityDao.find(pcon, id);
        if (entity == null)
            return null;
        
        // Checks the entity.
        JSONObject json = JSONObject.fromObject(new String(entity.getBody(), UTF8));
        Event event = new Event(json);

        return event.freeze();
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.remove(pcon, id);
        ownerIndexDao.remove(pcon, "id", id);
        editorIndexDao.remove(pcon, "id", id);
    }

    @Override
    public DataIterator<Event> getIterator(PartakeConnection con) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }

    @Override
    public boolean isRemoved(PartakeConnection con, String eventId) throws DAOException {
        return false;
    }

    // TODO: Why not DataIterator?
    @Override
    public List<Event> findByOwnerId(PartakeConnection con, String userId) throws DAOException {
        Postgres9StatementAndResultSet psars = ownerIndexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + OWNER_INDEX_TABLE_NAME + " WHERE ownerId = ?",
                new Object[] { userId });

        class Mapper implements DataMapper<ResultSet, Event> {
            private Postgres9Connection con;
            
            public Mapper(Postgres9Connection con) {
                this.con = con;
            }
            
            @Override
            public Event map(ResultSet rs) throws DAOException {
                try {
                    String id = rs.getString("id");
                    if (id == null)
                        return null;
                    
                    return Postgres9EventDao.this.find((Postgres9Connection) con, id);
                } catch (SQLException e) {
                    throw new DAOException(e);
                }
            }

            @Override
            public ResultSet unmap(Event t) throws DAOException {
                throw new UnsupportedOperationException();
            }
        }
        
        try {
            ArrayList<Event> events = new ArrayList<Event>();
            DataIterator<Event> it = new Postgres9DataIterator<Event>(new Mapper((Postgres9Connection) con), psars);
            while (it.hasNext()) {
                Event event = it.next();
                if (event == null)
                    continue;
                events.add(event);
            }
            
            return events;
        } finally {
            psars.close();
        }
    }

    // TODO: Why not DataIterator?
    // TODO: This is very slow!
    @Override
    public List<Event> findByScreenName(PartakeConnection con, String screenName) throws DAOException {
        Postgres9StatementAndResultSet psars = editorIndexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + EDITOR_INDEX_TABLE_NAME + " WHERE editorNames LIKE ?",
                new Object[] { "%" + screenName + "%" });

        class Mapper implements DataMapper<ResultSet, Event> {
            private Postgres9Connection con;
            
            public Mapper(Postgres9Connection con) {
                this.con = con;
            }
            
            @Override
            public Event map(ResultSet rs) throws DAOException {
                try {
                    String id = rs.getString("id");
                    if (id == null)
                        return null;
                    
                    return Postgres9EventDao.this.find((Postgres9Connection) con, id);
                } catch (SQLException e) {
                    throw new DAOException(e);
                }
            }

            @Override
            public ResultSet unmap(Event t) throws DAOException {
                throw new UnsupportedOperationException();
            }
        }
        
        try {
            ArrayList<Event> events = new ArrayList<Event>();
            DataIterator<Event> it = new Postgres9DataIterator<Event>(new Mapper((Postgres9Connection) con), psars);
            while (it.hasNext()) {
                Event event = it.next();
                if (event == null)
                    continue;
                if (event.isManager(screenName))
                    events.add(event);
            }
            
            return events;
        } finally {
            psars.close();
        }

    }

}
