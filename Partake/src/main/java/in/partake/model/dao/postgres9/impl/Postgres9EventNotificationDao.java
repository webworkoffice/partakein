package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventNotificationAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9DataIterator;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dao.postgres9.Postgres9IdMapper;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dao.postgres9.Postgres9StatementAndResultSet;
import in.partake.model.daoutil.DAOUtil;
import in.partake.model.dto.EventNotification;

import java.util.List;

import net.sf.json.JSONObject;

class EntityEventNotificationMapper extends Postgres9EntityDataMapper<EventNotification> {
    public EventNotification map(JSONObject obj) {
        return new EventNotification(obj).freeze();
    }
}

public class Postgres9EventNotificationDao extends Postgres9Dao implements IEventNotificationAccess {
    static final String TABLE_NAME = "EventNotificationEntities";
    static final String INDEX_TABLE_NAME = "EventNotificationIndex";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    private final EntityEventNotificationMapper mapper;

    public Postgres9EventNotificationDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
        this.mapper = new EntityEventNotificationMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);

        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            // event id may be NULL if system message.
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, eventId TEXT NOT NULL, createdAt TIMESTAMP NOT NULL)");
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
    public void put(PartakeConnection con, EventNotification t) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        // TODO: Why User does not have createdAt and modifiedAt?
        Postgres9Entity entity = new Postgres9Entity(t.getId(), CURRENT_VERSION, t.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDate());
        if (entityDao.exists(pcon, t.getId()))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);
        indexDao.put(pcon, new String[] { "id", "eventId", "createdAt" }, new Object[] { t.getId(), t.getEventId(), t.getCreatedAt() } );
    }

    @Override
    public EventNotification find(PartakeConnection con, String id) throws DAOException {
        return mapper.map(entityDao.find((Postgres9Connection) con, id));
    }

    @Override
    public boolean exists(PartakeConnection con, String id) throws DAOException {
        return entityDao.exists((Postgres9Connection) con, id);
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.remove(pcon, id);
        indexDao.remove(pcon, "id", id);
    }

    @Override
    public DataIterator<EventNotification> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, EventNotification>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }

    @Override
    public List<EventNotification> findByEventId(PartakeConnection con, String eventId, int offset, int limit) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE eventId = ? ORDER BY createdAt DESC OFFSET ? LIMIT ?",
                new Object[] { eventId, offset, limit });

        Postgres9IdMapper<EventNotification> idMapper = new Postgres9IdMapper<EventNotification>((Postgres9Connection) con, mapper, entityDao);
        return DAOUtil.convertToList(new Postgres9DataIterator<EventNotification>(idMapper, psars));
    }

    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }
}
