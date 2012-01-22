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
    static final String TABLE_NAME = "CalendarLinkageEntities";
    static final int CURRENT_VERSION = 1;
    
    static final String INDEX_TABLE_NAME = "CalendarLinkageIndex";

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao userIndexDao;
    
    public Postgres9CalendarLinkageDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.userIndexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        entityDao.initialize((Postgres9Connection) con);

        Postgres9Connection pcon = (Postgres9Connection) con;
        if (existsTable(pcon, INDEX_TABLE_NAME))
            return;
        
        userIndexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, userId TEXT NOT NULL)");
        userIndexDao.createIndex(pcon, "CREATE INDEX "+ INDEX_TABLE_NAME + "UserId ON " + INDEX_TABLE_NAME + "(userId)");
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        entityDao.truncate((Postgres9Connection) con);
        userIndexDao.truncate((Postgres9Connection) con);
    }

    @Override
    public void put(PartakeConnection con, CalendarLinkage linkage) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        Postgres9Entity entity = new Postgres9Entity(linkage.getId(), CURRENT_VERSION, linkage.toJSON().toString().getBytes(UTF8), null, PDate.getCurrentDate().getDate());
        if (entityDao.exists(pcon, linkage.getId()))
            entityDao.update(pcon, entity);            
        else
            entityDao.insert(pcon, entity);
        userIndexDao.put(pcon, new String[] { "id", "userId" }, new String[] { linkage.getId(), linkage.getUserId() });
    }

    @Override
    public CalendarLinkage find(PartakeConnection con, String id) throws DAOException {
        Postgres9Entity entity = entityDao.find((Postgres9Connection) con, id);
        if (entity == null)
            return null;

        CalendarLinkage t = CalendarLinkage.fromJSON(JSONObject.fromObject(new String(entity.getBody(), UTF8)));
        if (t != null)
            return t.freeze();
        return null;
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        entityDao.remove((Postgres9Connection) con, id);
        userIndexDao.remove((Postgres9Connection) con, "id", id);
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
        if (userId == null)
            return null;
        
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = userIndexDao.find(pcon, "id", "userId", userId);
        if (id == null)
            return null;
        
        return new CalendarLinkage(id, userId);
    }
}
