package in.partake.model.dao.postgres9.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventTicketAccess;
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
import in.partake.model.dto.EventTicket;

import java.util.List;
import java.util.UUID;

import net.sf.json.JSONObject;

class EntityEventTicketMapper extends Postgres9EntityDataMapper<EventTicket> {
    public EventTicket map(JSONObject obj) {
        return new EventTicket(obj).freeze();
    }
}

public class Postgres9EventTicketDao extends Postgres9Dao implements IEventTicketAccess {
    static final String ENTITY_TABLE_NAME = "EventTicketEntities";
    static final String INDEX_TABLE_NAME = "EventTicketIndex";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    private final EntityEventTicketMapper mapper;

    public Postgres9EventTicketDao() {
        this.entityDao = new Postgres9EntityDao(ENTITY_TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
        this.mapper = new EntityEventTicketMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);

        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, eventId TEXT NOT NULL, createdAt TIMESTAMP NOT NULL)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "EventId" + " ON " + INDEX_TABLE_NAME + "(eventId, createdAt)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        entityDao.truncate((Postgres9Connection) con);
        indexDao.truncate((Postgres9Connection) con);
    }

    @Override
    public void put(PartakeConnection con, EventTicket comment) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        Postgres9Entity entity = new Postgres9Entity(comment.getId(), CURRENT_VERSION, comment.toJSON().toString().getBytes(UTF8), null, comment.getCreatedAt());
        if (entityDao.exists(pcon, comment.getId()))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);

        indexDao.put(pcon, new String[] { "id", "eventId", "createdAt" }, new Object[] { comment.getId().toString(), comment.getEventId(), comment.getCreatedAt() });
    }

    @Override
    public EventTicket find(PartakeConnection con, UUID id) throws DAOException {
        return mapper.map(entityDao.find((Postgres9Connection) con, id));
    }

    @Override
    public boolean exists(PartakeConnection con, UUID id) throws DAOException {
        return entityDao.exists((Postgres9Connection) con, id);
    }

    @Override
    public void remove(PartakeConnection con, UUID id) throws DAOException {
        entityDao.remove((Postgres9Connection) con, id);
        indexDao.remove((Postgres9Connection) con, "id", id);
    }

    @Override
    public DataIterator<EventTicket> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, EventTicket>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public UUID getFreshId(PartakeConnection con) throws DAOException {
        return UUID.fromString(entityDao.getFreshId((Postgres9Connection) con));
    }

    @Override
    public List<EventTicket> getEventTicketsByEventId(PartakeConnection con, String eventId) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE eventId = ? ORDER BY createdAt ASC",
                new Object[] { eventId });

        Postgres9IdMapper<EventTicket> idMapper = new Postgres9IdMapper<EventTicket>((Postgres9Connection) con, mapper, entityDao);
        return DAOUtil.convertToList(new Postgres9DataIterator<EventTicket>(idMapper, psars));
    }

    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }
}
