package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.ICacheAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dto.CacheData;
import net.sf.json.JSONObject;

class EntityCacheMapper extends Postgres9EntityDataMapper<CacheData> {   
    public CacheData map(Postgres9Entity entity) throws DAOException {
        if (entity == null)
            return null;

        JSONObject opt = JSONObject.fromObject(new String(entity.getOpt(), UTF8));
        return new CacheData(entity.getId(), entity.getBody(), opt).freeze();
    }
}

public class Postgres9CacheDao extends Postgres9Dao implements ICacheAccess {
    static final String TABLE_NAME = "CacheEntities";
    static final int CURRENT_VERSION = 1;
    
    private final Postgres9EntityDao entityDao;
    private final EntityCacheMapper mapper;

    public Postgres9CacheDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.mapper = new EntityCacheMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        entityDao.initialize((Postgres9Connection) con);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        entityDao.truncate((Postgres9Connection) con);
    }

    @Override
    public void put(PartakeConnection con, CacheData cache) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        byte[] opt = cache.toJSONWithoutData().toString().getBytes(UTF8);
        Postgres9Entity entity = new Postgres9Entity(cache.getId(), CURRENT_VERSION, cache.getData(), opt, TimeUtil.getCurrentDate());
        if (entityDao.exists(pcon, entity.getId()))
            entityDao.update(pcon, entity);            
        else
            entityDao.insert(pcon, entity);
    }

    @Override
    public CacheData find(PartakeConnection con, String id) throws DAOException {
        return mapper.map(entityDao.find((Postgres9Connection) con, id));
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        entityDao.remove((Postgres9Connection) con, id);
    }

    @Override
    public DataIterator<CacheData> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, CacheData>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }
}
