package in.partake.model.dao.postgres9.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventFeedAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dto.EventFeedLinkage;
import in.partake.util.PDate;
import net.sf.json.JSONObject;

import org.apache.openjpa.util.UnsupportedException;

public class Postgres9EventFeedDao extends Postgres9Dao implements IEventFeedAccess {
    static final String TABLE_NAME = "EventFeedEntities";
    static final int CURRENT_VERSION = 1;
    static final String INDEX_TABLE_NAME = "EventFeedIndex";

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;

    public Postgres9EventFeedDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con; 
        entityDao.initialize(pcon);
        
        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, eventId TEXT NOT NULL)");
            indexDao.createIndex(pcon, "CREATE UNIQUE INDEX " + INDEX_TABLE_NAME + "EventId" + " ON " + INDEX_TABLE_NAME + "(eventId)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        entityDao.truncate((Postgres9Connection) con);
        indexDao.truncate((Postgres9Connection) con);
    }

    @Override
    public void put(PartakeConnection con, EventFeedLinkage linkage) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        Postgres9Entity entity = new Postgres9Entity(linkage.getId(), CURRENT_VERSION, linkage.toJSON().toString().getBytes(UTF8), null, PDate.getCurrentDate().getDate());

        if (entityDao.exists(pcon, linkage.getId()))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);
        indexDao.put(pcon, new String[] { "id", "eventId" } , new Object[] { linkage.getId(), linkage.getEventId() });
    }

    @Override
    public EventFeedLinkage find(PartakeConnection con, String id) throws DAOException {
        Postgres9Entity entity = entityDao.find((Postgres9Connection) con, id);
        if (entity == null)
            return null;
        
        JSONObject obj = JSONObject.fromObject(new String(entity.getBody(), UTF8));
        return new EventFeedLinkage(obj).freeze();
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        entityDao.remove(pcon, id);
        indexDao.remove(pcon, "id", id);
    }

    @Override
    public DataIterator<EventFeedLinkage> getIterator(PartakeConnection con) throws DAOException {
        throw new UnsupportedException();
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }

    @Override
    public String findByEventId(PartakeConnection con, String eventId) throws DAOException {
        return indexDao.find((Postgres9Connection) con, "id", "eventId", eventId);
    }

}
