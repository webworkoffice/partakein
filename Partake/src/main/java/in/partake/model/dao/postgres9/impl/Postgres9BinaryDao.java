package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IBinaryAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dto.BinaryData;
import net.sf.json.JSONObject;

class EntityBinaryMapper extends Postgres9EntityDataMapper<BinaryData> {   
    public BinaryData map(Postgres9Entity entity) throws DAOException {
        if (entity == null)
            return null;

        JSONObject obj = JSONObject.fromObject(new String(entity.getBody(), UTF8));
        BinaryData binary = new BinaryData(obj);
        binary.setData(entity.getOpt());

        return binary.freeze();
    }
}

public class Postgres9BinaryDao extends Postgres9Dao implements IBinaryAccess {
    static final String TABLE_NAME = "BinaryEntities";
    static final int CURRENT_VERSION = 1;
    static final String USER_INDEX_TABLE_NAME = "BinaryUserIndex";

    private final Postgres9EntityDao entityDao;
    private final EntityBinaryMapper mapper;

    
    public Postgres9BinaryDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.mapper = new EntityBinaryMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        entityDao.truncate((Postgres9Connection) con);
    }

    @Override
    public void put(PartakeConnection con, BinaryData binary) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        
        Postgres9Entity entity = new Postgres9Entity(binary.getId(), CURRENT_VERSION, binary.toJSON().toString().getBytes(UTF8), binary.getData(), TimeUtil.getCurrentDate());
        if (entityDao.exists(pcon, binary.getId()))
            entityDao.update(pcon, entity);            
        else
            entityDao.insert(pcon, entity);
    }

    @Override
    public BinaryData find(PartakeConnection con, String id) throws DAOException {
        return mapper.map(entityDao.find((Postgres9Connection) con, id));
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        entityDao.remove((Postgres9Connection) con, id);
    }

    @Override
    public DataIterator<BinaryData> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, BinaryData>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }
    
    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }
    
    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }
}
