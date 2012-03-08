package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEnvelopeAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dto.Envelope;
import net.sf.json.JSONObject;

class EntityEnvelopeMapper extends Postgres9EntityDataMapper<Envelope> {   
    public Envelope map(JSONObject obj) {
        return new Envelope(obj).freeze();
    }
}

public class Postgres9EnvelopeDao extends Postgres9Dao implements IEnvelopeAccess {
    static final String TABLE_NAME = "EnvelopeEntities";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final EntityEnvelopeMapper mapper;

    public Postgres9EnvelopeDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.mapper = new EntityEnvelopeMapper();
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
    public void put(PartakeConnection con, Envelope envelope) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        // TODO: Entity should have getId() instead of getEnvelopeId().
        // TODO: Why User does not have createdAt and modifiedAt?
        Postgres9Entity entity = new Postgres9Entity(envelope.getEnvelopeId(), CURRENT_VERSION, envelope.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDate());
        if (entityDao.exists(pcon, envelope.getEnvelopeId()))
            entityDao.update(pcon, entity);            
        else
            entityDao.insert(pcon, entity);
    }

    @Override
    public Envelope find(PartakeConnection con, String id) throws DAOException {
        Postgres9Entity entity = entityDao.find((Postgres9Connection) con, id);
        if (entity == null)
            return null;

        JSONObject json = JSONObject.fromObject(new String(entity.getBody(), UTF8));
        return new Envelope(json).freeze();
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        entityDao.remove((Postgres9Connection) con, id);
    }

    @Override
    public DataIterator<Envelope> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, Envelope>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }
    
    @Override
    public long count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }
}
