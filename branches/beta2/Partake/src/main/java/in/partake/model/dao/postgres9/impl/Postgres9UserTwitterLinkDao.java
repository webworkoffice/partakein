package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.DataMapper;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserTwitterLinkAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dto.UserTwitterLink;

import java.util.UUID;

import net.sf.json.JSONObject;

public class Postgres9UserTwitterLinkDao extends Postgres9Dao implements IUserTwitterLinkAccess {
    static final String ENTITY_TABLE_NAME = "UserTwitterLinkEntities";
    static final String INDEX_TABLE_NAME = "UserTwitterLinkIndex";
    static final int CURRENT_VERSION = 1;


    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;

    public Postgres9UserTwitterLinkDao() {
        this.entityDao = new Postgres9EntityDao(ENTITY_TABLE_NAME);
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
    public void put(PartakeConnection con, UserTwitterLink linkage) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        Postgres9Entity entity = new Postgres9Entity(linkage.getId(), CURRENT_VERSION, linkage.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDateTime());

        if (entityDao.exists(pcon, linkage.getId())) {
            entityDao.update(pcon, entity);
        } else {
            entityDao.insert(pcon, entity);
        }

        indexDao.put(pcon, new String[] { "id", "twitterId", "userId"}, new Object[] { linkage.getId().toString(), String.valueOf(linkage.getTwitterId()), linkage.getUserId() });
    }

    @Override
    public UserTwitterLink find(PartakeConnection con, UUID id) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        Postgres9Entity entity = entityDao.find(pcon, id);
        if (entity == null)
            return null;

        JSONObject obj = JSONObject.fromObject(new String(entity.getBody(), UTF8));
        UserTwitterLink linkage = new UserTwitterLink(obj);
        return linkage.freeze();
    }

    @Override
    public boolean exists(PartakeConnection con, UUID id) throws DAOException {
        return entityDao.exists((Postgres9Connection) con, id);
    }

    @Override
    public void remove(PartakeConnection con, UUID id) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.remove(pcon, id);
        indexDao.remove(pcon, "id", id);
    }

    @Override
    public DataIterator<UserTwitterLink> getIterator(PartakeConnection con) throws DAOException {
        DataMapper<Postgres9Entity, UserTwitterLink> mapper = new DataMapper<Postgres9Entity, UserTwitterLink>() {
            @Override
            public UserTwitterLink map(Postgres9Entity entity) throws DAOException {
                if (entity == null)
                    return null;

                JSONObject json = JSONObject.fromObject(new String(entity.getBody(), UTF8));
                return new UserTwitterLink(json).freeze();
            }

            @Override
            public Postgres9Entity unmap(UserTwitterLink t) throws DAOException {
                throw new UnsupportedOperationException();
            }
        };
        DataIterator<Postgres9Entity> iterator = entityDao.getIterator((Postgres9Connection) con);
        return new MapperDataIterator<Postgres9Entity, UserTwitterLink>(mapper, iterator);
    }

    @Override
    public int count(PartakeConnection con) throws DAOException {
        return entityDao.count((Postgres9Connection) con);
    }

    @Override
    public UserTwitterLink findByTwitterId(PartakeConnection con, long twitterId) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", "twitterId", String.valueOf(twitterId));
        if (id == null)
            return null;

        return find(pcon, UUID.fromString(id));
    }

    @Override
    public UserTwitterLink findByUserId(PartakeConnection con, String userId) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", "userId", userId);
        if (id == null)
            return null;

        return find(pcon, UUID.fromString(id));
    }

    @Override
    public UUID getFreshId(PartakeConnection con) throws DAOException {
        return UUID.fromString(entityDao.getFreshId((Postgres9Connection) con));
    }
}
