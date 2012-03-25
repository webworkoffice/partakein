package in.partake.model.dao.postgres9.impl;

import java.util.Date;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dto.User;
import net.sf.json.JSONObject;

class EntityUserMapper extends Postgres9EntityDataMapper<User> {   
    public User map(JSONObject obj) {
        return new User(obj).freeze();
    }
}

public class Postgres9UserDao extends Postgres9Dao implements IUserAccess {
    static final String TABLE_NAME = "UserEntities";
    static final String LOGIN_INDEX_TABLE_NAME = "UserLoginIndex";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao loginIndexDao;
    private final EntityUserMapper mapper;

    public Postgres9UserDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.loginIndexDao = new Postgres9IndexDao(LOGIN_INDEX_TABLE_NAME);
        this.mapper = new EntityUserMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);
        
        if (!existsTable(pcon, LOGIN_INDEX_TABLE_NAME)) {
            loginIndexDao.createIndexTable(pcon, "CREATE TABLE " + LOGIN_INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, lastLoginAt TIMESTAMP)");
            loginIndexDao.createIndex(pcon, "CREATE INDEX " + LOGIN_INDEX_TABLE_NAME + "Login" + " ON " + LOGIN_INDEX_TABLE_NAME + "(lastLoginAt)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        entityDao.truncate((Postgres9Connection) con);
        loginIndexDao.truncate((Postgres9Connection) con);
    }

    @Override
    public void put(PartakeConnection con, User user) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        // TODO: Why User does not have createdAt and modifiedAt?
        Postgres9Entity entity = new Postgres9Entity(user.getId(), CURRENT_VERSION, user.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDate());
        if (entityDao.exists(pcon, user.getId()))
            entityDao.update(pcon, entity);            
        else
            entityDao.insert(pcon, entity);
        
        loginIndexDao.put(pcon,
                new String[] { "id", "lastLoginAt" },
                new Object[] { user.getId(), user.getLastLoginAt() });
    }

    @Override
    public User find(PartakeConnection con, String id) throws DAOException {
        return mapper.map(entityDao.find((Postgres9Connection) con, id));
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        entityDao.remove((Postgres9Connection) con, id);
        loginIndexDao.remove((Postgres9Connection) con, "id", id);
    }

    @Override
    public DataIterator<User> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, User>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }

    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }
    
    @Override
    public int countActiveUsers(PartakeConnection con, Date loggedinAfter) throws DAOException {
        return loginIndexDao.count((Postgres9Connection) con, "lastLoginAt >= ?", new Object[] { loggedinAfter });
    }
}
