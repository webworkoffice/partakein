package in.partake.model.dao.postgres9.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.ICalendarLinkageAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dto.CalendarLinkage;
import in.partake.util.PDate;
import net.sf.json.JSONObject;

public class Postgres9CalendarLinkageDao extends Postgres9Dao implements ICalendarLinkageAccess {
    static final String SCHEMA = "calendarlinkage1";
    static final String[] ALL_SCHEMA = new String[] {
        "calendar-linkage1"
    };
    
    static final String INDEX_TABLE_NAME = "CalendarLinkageIndex1";

    private Postgres9EntityDao entityDao;
    private Postgres9IndexDao userIndexDao;
    
    public Postgres9CalendarLinkageDao(Postgres9EntityDao entityDao) {
        this.entityDao = entityDao;
        this.userIndexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        if (existsTable(pcon, INDEX_TABLE_NAME))
            return;
        
        userIndexDao.createIndexTable(pcon, "CREATE TABLE CalendarLinkageIndex1(id UUID PRIMARY KEY, userId TEXT NOT NULL)");
        userIndexDao.createIndex(pcon, "CREATE INDEX CalendarLinkageIndex1UserId ON CalendarLinkageIndex1(userId)");
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        entityDao.removeEntitiesHavingSchema((Postgres9Connection) con, SCHEMA);
    }

    @Override
    public void put(PartakeConnection con, CalendarLinkage t) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        Postgres9Entity entity = new Postgres9Entity(t.getId(), SCHEMA, t.toJSON().toString().getBytes(UTF8), PDate.getCurrentDate().getDate());
        if (entityDao.exists(pcon, t.getId()))
            entityDao.update(pcon, entity);            
        else
            entityDao.insert(pcon, entity);
    }

    @Override
    public CalendarLinkage find(PartakeConnection con, String id) throws DAOException {
        Postgres9Entity entity = entityDao.find((Postgres9Connection) con, id);
        if (entity == null)
            return null;

        CalendarLinkage t = CalendarLinkage.fromJSON(JSONObject.fromObject(entity.getBody()));
        if (t != null)
            return t.freeze();
        return null;
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        entityDao.remove((Postgres9Connection) con, id);
    }

    @Override
    public DataIterator<CalendarLinkage> getIterator(PartakeConnection con) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }

    @Override
    public CalendarLinkage findByUserId(PartakeConnection con, String userId) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = userIndexDao.find(pcon, "userId", userId);
        if (id == null)
            return null;
        
        Postgres9Entity entity = entityDao.find(pcon, id);
        if (entity == null)
            return null;
        
        return CalendarLinkage.fromJSON(JSONObject.fromObject(new String(entity.getBody(), UTF8)));
    }
}
