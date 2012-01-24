package in.partake.model.dao.postgres9.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.ITwitterLinkageAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dto.TwitterLinkage;
import in.partake.util.PDate;
import net.sf.json.JSONObject;

public class Postgres9TwitterLinkageDao extends Postgres9Dao implements ITwitterLinkageAccess {
    static final String TABLE_NAME = "TwitterLinkageEntities";
    static final int CURRENT_VERSION = 1;

    static final String INDEX_TABLE_NAME = "TwitterLinkageIndex";

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    
    public Postgres9TwitterLinkageDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);
        
        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, twitterId TEXT NOT NULL, userId TEXT NOT NULL)");
            indexDao.createIndex(pcon, "CREATE UNIQUE INDEX " + INDEX_TABLE_NAME + "TwitterId" + " ON " + INDEX_TABLE_NAME + "(twitterId)");
            indexDao.createIndex(pcon, "CREATE UNIQUE INDEX " + INDEX_TABLE_NAME + "UserId" + " ON " + INDEX_TABLE_NAME + "(userId)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.truncate(pcon);
        indexDao.truncate(pcon);
    }

    @Override
    public void put(PartakeConnection con, TwitterLinkage linkage) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", "twitterId", linkage.getTwitterId());
        if (id == null)
            id = entityDao.getFreshId(pcon);
        
        Postgres9Entity entity = new Postgres9Entity(id, CURRENT_VERSION, linkage.toJSON().toString().getBytes(UTF8), null, PDate.getCurrentDate().getDate());

        if (entityDao.exists(pcon, id)) {
            entityDao.update(pcon, entity);
        } else {
            entityDao.insert(pcon, entity);
        }
        
        indexDao.put(pcon, new String[] { "id", "twitterId", "userId"}, new String[] { id, linkage.getTwitterId(), linkage.getUserId() });
    }

    @Override
    public TwitterLinkage find(PartakeConnection con, String twitterId) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", "twitterId", twitterId);
        if (id == null)
            return null;
        
        Postgres9Entity entity = entityDao.find(pcon, id);
        if (entity == null)
            return null;
        
        // Checks the entity.
        JSONObject obj = JSONObject.fromObject(new String(entity.getBody(), UTF8));
        TwitterLinkage linkage = new TwitterLinkage(obj);
        
        if (twitterId.equals(linkage.getTwitterId()))
            return linkage.freeze();
        else
            return null;
    }

    @Override
    public void remove(PartakeConnection con, String twitterId) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", "twitterId", twitterId);
        if (id == null)
            return;

        entityDao.remove(pcon, id);
        indexDao.remove(pcon, "id", id);
    }

    @Override
    public DataIterator<TwitterLinkage> getIterator(PartakeConnection con) throws DAOException {
        throw new UnsupportedOperationException();
    }
}
