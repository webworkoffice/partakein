package in.partake.model.dao.postgres9.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IBinaryAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dto.BinaryData;
import in.partake.util.PDate;

public class Postgres9BinaryDao extends Postgres9Dao implements IBinaryAccess {
    static final String TABLE_NAME = "BinaryEntities";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    
    public Postgres9BinaryDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
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
    public void put(PartakeConnection con, BinaryData binary) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        // TODO: Why BinaryData does not have createdAt and modifiedAt?
        Postgres9Entity entity = new Postgres9Entity(binary.getId(), CURRENT_VERSION, binary.getData(), binary.getType().getBytes(UTF8), PDate.getCurrentDate().getDate());
        if (entityDao.exists(pcon, binary.getId()))
            entityDao.update(pcon, entity);            
        else
            entityDao.insert(pcon, entity);
    }

    @Override
    public BinaryData find(PartakeConnection con, String id) throws DAOException {
        Postgres9Entity entity = entityDao.find((Postgres9Connection) con, id);
        if (entity == null)
            return null;

        BinaryData binary = new BinaryData(entity.getId(), new String(entity.getOpt(), UTF8), entity.getBody());
        return binary.freeze();
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        entityDao.remove((Postgres9Connection) con, id);
    }

    @Override
    public DataIterator<BinaryData> getIterator(PartakeConnection con) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }
}
