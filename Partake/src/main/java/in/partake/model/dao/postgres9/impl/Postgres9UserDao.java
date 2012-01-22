package in.partake.model.dao.postgres9.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dto.User;
import in.partake.util.PDate;
import net.sf.json.JSONObject;

public class Postgres9UserDao extends Postgres9Dao implements IUserAccess {
    static final String SCHEMA = "user1";
    static final String[] ALL_SCHEMA = new String[] {
        "user1"
    };

    private Postgres9EntityDao entityDao;

    public Postgres9UserDao(Postgres9EntityDao entityDao) {
        this.entityDao = entityDao;
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        for (String schema : ALL_SCHEMA)
            entityDao.removeEntitiesHavingSchema((Postgres9Connection) con, schema);
    }

    @Override
    public void put(PartakeConnection con, User user) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        // TODO: Why User does not have createdAt and modifiedAt?
        Postgres9Entity entity = new Postgres9Entity(user.getId(), SCHEMA, user.toJSON().toString().getBytes(UTF8), null, PDate.getCurrentDate().getDate());
        if (entityDao.exists(pcon, user.getId()))
            entityDao.update(pcon, entity);            
        else
            entityDao.insert(pcon, entity);
    }

    @Override
    public User find(PartakeConnection con, String id) throws DAOException {
        Postgres9Entity entity = entityDao.find((Postgres9Connection) con, id);
        if (entity == null)
            return null;

        User user = User.fromJSON(JSONObject.fromObject(new String(entity.getBody(), UTF8)));
        if (user != null)
            return user.freeze();
        return null;
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        entityDao.remove((Postgres9Connection) con, id);
    }

    @Override
    public DataIterator<User> getIterator(PartakeConnection con) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }

}
