package in.partake.model.dao.postgres9.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserPreferenceAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dto.UserPreference;
import in.partake.util.PDate;
import net.sf.json.JSONObject;

class EntityUserPreferenceMapper extends Postgres9EntityDataMapper<UserPreference> {   
    public UserPreference map(JSONObject obj) {
        return new UserPreference(obj).freeze();
    }
}

//TODO: UserPreference should be merged into User.
public class Postgres9UserPreferenceDao extends Postgres9Dao implements IUserPreferenceAccess {
    static final String TABLE_NAME = "UserPreferenceEntities";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final EntityUserPreferenceMapper mapper;

    public Postgres9UserPreferenceDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.mapper = new EntityUserPreferenceMapper();
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
    public void put(PartakeConnection con, UserPreference pref) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        // TODO: Why User does not have createdAt and modifiedAt?
        Postgres9Entity entity = new Postgres9Entity(pref.getUserId(), CURRENT_VERSION, pref.toJSON().toString().getBytes(UTF8), null, PDate.getCurrentDate().getDate());
        if (entityDao.exists(pcon, pref.getUserId()))
            entityDao.update(pcon, entity);            
        else
            entityDao.insert(pcon, entity);
    }

    @Override
    public UserPreference find(PartakeConnection con, String id) throws DAOException {
        return mapper.map(entityDao.find((Postgres9Connection) con, id));
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        entityDao.remove((Postgres9Connection) con, id);
    }

    @Override
    public DataIterator<UserPreference> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, UserPreference>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }
}
