package in.partake.model.dao.postgres9.impl;

import in.partake.base.PartakeRuntimeException;
import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9DataIterator;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dao.postgres9.Postgres9IdMapper;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dao.postgres9.Postgres9StatementAndResultSet;
import in.partake.model.dto.Event;
import in.partake.resource.ServerErrorCode;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

class EntityEventMapper extends Postgres9EntityDataMapper<Event> {   
    public Event map(JSONObject obj) {
        return new Event(obj).freeze();
    }
}

public class Postgres9EventDao extends Postgres9Dao implements IEventAccess {
    static final String TABLE_NAME = "EventEntities";
    static final int CURRENT_VERSION = 1;
    static final String OWNER_INDEX_TABLE_NAME = "EventOwnerIndex";   // For owner
    static final String EDITOR_INDEX_TABLE_NAME = "EventEditorIndex"; // For editor 

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao ownerIndexDao;
    private final Postgres9IndexDao editorIndexDao;
    private final EntityEventMapper mapper;

    public Postgres9EventDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.ownerIndexDao = new Postgres9IndexDao(OWNER_INDEX_TABLE_NAME);
        this.editorIndexDao = new Postgres9IndexDao(EDITOR_INDEX_TABLE_NAME);
        this.mapper = new EntityEventMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);
        
        if (!existsTable(pcon, OWNER_INDEX_TABLE_NAME)) {
            ownerIndexDao.createIndexTable(pcon, "CREATE TABLE " + OWNER_INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, ownerId TEXT NOT NULL, draft BOOL NOT NULL, beginDate TIMESTAMP NOT NULL)");
            ownerIndexDao.createIndex(pcon, "CREATE INDEX " + OWNER_INDEX_TABLE_NAME + "OwnerId" + " ON " + OWNER_INDEX_TABLE_NAME + "(ownerId, draft, beginDate)");
        }

        if (!existsTable(pcon, EDITOR_INDEX_TABLE_NAME)) {
            editorIndexDao.createIndexTable(pcon, "CREATE TABLE " + EDITOR_INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, editorNames TEXT NOT NULL, draft BOOL NOT NULL, beginDate TIMESTAMP NOT NULL)");
            editorIndexDao.createIndex(pcon, "CREATE INDEX " + EDITOR_INDEX_TABLE_NAME + "EditorName" + " ON " + EDITOR_INDEX_TABLE_NAME + "(editorNames, draft, beginDate)");
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

        Postgres9Entity entity = new Postgres9Entity(event.getId(), CURRENT_VERSION, event.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDate());
        if (entityDao.exists(pcon, event.getId()))
            entityDao.update(pcon, entity);            
        else
            entityDao.insert(pcon, entity);

        ownerIndexDao.put(pcon, 
                new String[] {"id", "ownerId", "draft", "beginDate" }, 
                new Object[] { event.getId(), event.getOwnerId(), event.isPreview(), event.getBeginDate() });
        
        if (event.getManagerScreenNames() != null) {
            editorIndexDao.put(pcon,
                    new String[] {"id", "editorNames", "draft", "beginDate" }, 
                    new Object[] { event.getId(), event.getManagerScreenNames(), event.isPreview(), event.getBeginDate() });
        } else {
            editorIndexDao.remove(pcon, "id", event.getId());
        }
    }

    @Override
    public Event find(PartakeConnection con, String id) throws DAOException {
        return mapper.map(entityDao.find((Postgres9Connection) con, id));
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
        return new MapperDataIterator<Postgres9Entity, Event>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }

    @Override
    public boolean isRemoved(PartakeConnection con, String eventId) throws DAOException {
        // TODO: should be implemented.
        return false;
    }
    
    // TODO: Why not DataIterator?
    @Override
    public List<Event> findByOwnerId(PartakeConnection con, String userId) throws DAOException {
        Postgres9StatementAndResultSet psars = ownerIndexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + OWNER_INDEX_TABLE_NAME + " WHERE ownerId = ?",
                new Object[] { userId });

        Postgres9IdMapper<Event> idMapper = new Postgres9IdMapper<Event>((Postgres9Connection) con, mapper, entityDao);
        
        try {
            ArrayList<Event> events = new ArrayList<Event>();
            DataIterator<Event> it = new Postgres9DataIterator<Event>(idMapper, psars);
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

    @Override
    public List<Event> findByOwnerId(PartakeConnection con, String userId, EventFindCriteria criteria, int offset, int limit) throws DAOException {
        String draftSql = conditionClauseForCriteria(criteria);
        
        Postgres9StatementAndResultSet psars = ownerIndexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + OWNER_INDEX_TABLE_NAME + " WHERE ownerId = ? " + draftSql + " ORDER BY beginDate DESC OFFSET ? LIMIT ?",
                new Object[] { userId, offset, limit });

        Postgres9IdMapper<Event> idMapper = new Postgres9IdMapper<Event>((Postgres9Connection) con, mapper, entityDao);
        
        try {
            ArrayList<Event> events = new ArrayList<Event>();
            DataIterator<Event> it = new Postgres9DataIterator<Event>(idMapper, psars);
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

        Postgres9IdMapper<Event> idMapper = new Postgres9IdMapper<Event>((Postgres9Connection) con, mapper, entityDao);
        
        try {
            ArrayList<Event> events = new ArrayList<Event>();
            DataIterator<Event> it = new Postgres9DataIterator<Event>(idMapper, psars);
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

    @Override
    public List<Event> findByScreenName(PartakeConnection con, String screenName, EventFindCriteria criteria, int offset, int limit) throws DAOException {
        String condition = conditionClauseForCriteria(criteria);

        Postgres9StatementAndResultSet psars = editorIndexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + EDITOR_INDEX_TABLE_NAME + " WHERE editorNames LIKE ? " + condition + " ORDER BY beginDate DESC OFFSET ? LIMIT ?",
                new Object[] { "%" + screenName + "%", offset, limit });

        Postgres9IdMapper<Event> idMapper = new Postgres9IdMapper<Event>((Postgres9Connection) con, mapper, entityDao);
        
        try {
            ArrayList<Event> events = new ArrayList<Event>();
            DataIterator<Event> it = new Postgres9DataIterator<Event>(idMapper, psars);
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
    
    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }
    
    @Override
    public int countEventsByOwnerId(PartakeConnection con, String userId, EventFindCriteria criteria) throws DAOException {
        switch (criteria) {
        case ALL_EVENTS:
            return ownerIndexDao.count((Postgres9Connection) con, "ownerId", userId);
        case DRAFT_EVENT_ONLY:
            return ownerIndexDao.count((Postgres9Connection) con, new String[] { "ownerId", "draft" }, new Object[] { userId, true });
        case PUBLISHED_EVENT_ONLY:
            return ownerIndexDao.count((Postgres9Connection) con, new String[] { "ownerId", "draft" }, new Object[] { userId, false });
        }
        
        assert false;
        throw new PartakeRuntimeException(ServerErrorCode.LOGIC_ERROR);
    }
    
    @Override
    public int countEventsByScreenName(PartakeConnection con, String screenName, EventFindCriteria criteria) throws DAOException {
        String condition = conditionClauseForCriteria(criteria);
        Postgres9StatementAndResultSet psars = editorIndexDao.select((Postgres9Connection) con,
                "SELECT count(1) FROM " + EDITOR_INDEX_TABLE_NAME + " WHERE editorNames LIKE ? " + condition,
                new Object[] { "%" + screenName + "%" });
        
        try {
            ResultSet rs = psars.getResultSet();
            if (rs.next())
                return rs.getInt(1);
            else
                return 0;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            psars.close();
        }
    }
    
    private String conditionClauseForCriteria(EventFindCriteria criteria) {
        switch (criteria) {
        case ALL_EVENTS:
            return "";
        case DRAFT_EVENT_ONLY:
            return " AND draft = true";
        case PUBLISHED_EVENT_ONLY:
            return " AND draft = false";
        }
        
        return "";
    }
}
