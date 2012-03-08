package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IOpenIDLinkageAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9DataIterator;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dao.postgres9.Postgres9IdMapper;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dao.postgres9.Postgres9StatementAndResultSet;
import in.partake.model.dto.OpenIDLinkage;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

class EntityOpenIDLinkageMapper extends Postgres9EntityDataMapper<OpenIDLinkage> {
    @Override
    public OpenIDLinkage map(JSONObject obj) {
        return new OpenIDLinkage(obj).freeze();
    }
}

public class Postgres9OpenIDLinkageDao extends Postgres9Dao implements IOpenIDLinkageAccess {
    static final String TABLE_NAME = "OpenIDLinkageEntities";
    static final int CURRENT_VERSION = 1;

    static final String INDEX_TABLE_NAME = "OpenIdLinkageIndex";

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    private final EntityOpenIDLinkageMapper mapper;
    
    public Postgres9OpenIDLinkageDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
        this.mapper = new EntityOpenIDLinkageMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        
        entityDao.initialize((Postgres9Connection) con);
        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, openId TEXT NOT NULL, userId TEXT NOT NULL)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "UserId" + " ON " + INDEX_TABLE_NAME + "(userId)");
            indexDao.createIndex(pcon, "CREATE UNIQUE INDEX " + INDEX_TABLE_NAME + "OpenId" + " ON " + INDEX_TABLE_NAME + "(openId)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        entityDao.truncate((Postgres9Connection) con);
        indexDao.truncate((Postgres9Connection) con);
    }

    @Override
    public void put(PartakeConnection con, OpenIDLinkage linkage) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", "openId", linkage.getId());
        if (id == null)
            id = entityDao.getFreshId(pcon);
        
        Postgres9Entity entity = new Postgres9Entity(id, CURRENT_VERSION, linkage.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDate());

        if (entityDao.exists(pcon, id)) {
            entityDao.update(pcon, entity);
        } else {
            entityDao.insert(pcon, entity);
        }
        indexDao.put((Postgres9Connection) con, new String[] { "id", "openId", "userId" }, new String[] { id, linkage.getId(), linkage.getUserId() });
    }

    @Override
    public OpenIDLinkage find(PartakeConnection con, String openId) throws DAOException {
        String id = indexDao.find((Postgres9Connection) con, "id", "openId", openId);
        if (id == null)
            return null;
        
        return findById(con, id);
    }
    
    public OpenIDLinkage findById(PartakeConnection con, String id) throws DAOException {
        return mapper.map(entityDao.find((Postgres9Connection) con, id));
    }

    @Override
    public void remove(PartakeConnection con, String openId) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", "openId", openId);

        entityDao.remove(pcon, id);
        indexDao.remove((Postgres9Connection) con, "id", id);
    }

    @Override
    public DataIterator<OpenIDLinkage> getIterator(PartakeConnection con) throws DAOException {
        DataIterator<Postgres9Entity> iterator = entityDao.getIterator((Postgres9Connection) con); 
        return new MapperDataIterator<Postgres9Entity, OpenIDLinkage>(mapper, iterator);
    }

    // TODO: Why not DataIterator?
    // TODO: Why not List<OpenIdLinkage>?
    @Override
    public List<String> findByUserId(PartakeConnection con, String userId) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE userId = ?",
                new Object[] { userId });

        Postgres9IdMapper<OpenIDLinkage> idMapper = new Postgres9IdMapper<OpenIDLinkage>((Postgres9Connection) con, mapper, entityDao);

        DataIterator<OpenIDLinkage> it = new Postgres9DataIterator<OpenIDLinkage>(idMapper, psars);
        try {
            ArrayList<String> results = new ArrayList<String>();
            while (it.hasNext()) {
                OpenIDLinkage t = it.next();
                if (t == null)
                    continue;
                
                results.add(t.getId());
            }

            return results;
        } finally {
            it.close();
        }

    }

    @Override
    public long count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }
}
