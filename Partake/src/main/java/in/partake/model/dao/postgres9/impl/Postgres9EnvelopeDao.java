package in.partake.model.dao.postgres9.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.DataMapper;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.access.IEnvelopeAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dto.Envelope;
import in.partake.util.PDate;
import net.sf.json.JSONObject;

public class Postgres9EnvelopeDao extends Postgres9Dao implements IEnvelopeAccess {
    static final String TABLE_NAME = "EnvelopeEntities";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;

    public Postgres9EnvelopeDao() {
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
    public void put(PartakeConnection con, Envelope envelope) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        // TODO: Entity should have getId() instead of getEnvelopeId().
        // TODO: Why User does not have createdAt and modifiedAt?
        Postgres9Entity entity = new Postgres9Entity(envelope.getEnvelopeId(), CURRENT_VERSION, envelope.toJSON().toString().getBytes(UTF8), null, PDate.getCurrentDate().getDate());
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
        DataMapper<Postgres9Entity, Envelope> mapper = new DataMapper<Postgres9Entity, Envelope>() {
            @Override
            public Envelope map(Postgres9Entity entity) throws DAOException {
                if (entity == null)
                    return null;

                JSONObject json = JSONObject.fromObject(new String(entity.getBody(), UTF8));
                return new Envelope(json).freeze();
            }

            @Override
            public Postgres9Entity unmap(Envelope t) throws DAOException {
                throw new UnsupportedOperationException();
            }
        };
        DataIterator<Postgres9Entity> iterator = entityDao.getIterator((Postgres9Connection) con); 
        return new MapperDataIterator<Postgres9Entity, Envelope>(mapper, iterator);
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }
}
