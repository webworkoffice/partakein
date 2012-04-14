package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserMessageAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9DataIterator;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dao.postgres9.Postgres9IdMapper;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dao.postgres9.Postgres9StatementAndResultSet;
import in.partake.model.daoutil.DAOUtil;
import in.partake.model.dto.UserMessage;

import java.util.List;

import net.sf.json.JSONObject;

class EntityUserMessageMapper extends Postgres9EntityDataMapper<UserMessage> {
    public UserMessage map(JSONObject obj) {
        return new UserMessage(obj).freeze();
    }
}

public class Postgres9UserMessageDao extends Postgres9Dao implements IUserMessageAccess {
    static final String TABLE_NAME = "UserMessageEntities";
    static final String INDEX_TABLE_NAME = "UserMessageIndex";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    private final EntityUserMessageMapper mapper;

    public Postgres9UserMessageDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
        this.mapper = new EntityUserMessageMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);

        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            // event id may be NULL if system message.
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, senderId TEXT NOT NULL, receiverId TEXT NOT NULL, opened BOOLEAN NOT NULL, createdAt TIMESTAMP NOT NULL)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "ReceiverId" + " ON " + INDEX_TABLE_NAME + "(receiverId, createdAt)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "Opened" + " ON " + INDEX_TABLE_NAME + "(receiverId, opened)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "SenderId" + " ON " + INDEX_TABLE_NAME + "(senderId, createdAt)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.truncate(pcon);
        indexDao.truncate(pcon);
    }

    @Override
    public void put(PartakeConnection con, UserMessage t) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        // TODO: Why User does not have createdAt and modifiedAt?
        Postgres9Entity entity = new Postgres9Entity(t.getId(), CURRENT_VERSION, t.toJSON().toString().getBytes(UTF8), null, TimeUtil.getCurrentDateTime());
        if (entityDao.exists(pcon, t.getId()))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);
        indexDao.put(pcon, new String[] { "id", "senderId", "receiverId", "createdAt" }, new Object[] { t.getId(), t.getSenderId(), t.getReceiverId(), t.getCreatedAt() } );
    }

    @Override
    public UserMessage find(PartakeConnection con, String id) throws DAOException {
        return mapper.map(entityDao.find((Postgres9Connection) con, id));
    }

    @Override
    public boolean exists(PartakeConnection con, String id) throws DAOException {
        return entityDao.exists((Postgres9Connection) con, id);
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.remove(pcon, id);
        indexDao.remove(pcon, "id", id);
    }

    @Override
    public DataIterator<UserMessage> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, UserMessage>(mapper, entityDao.getIterator((Postgres9Connection) con));
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
    public int countByReceiverId(PartakeConnection con, String receiverId) throws DAOException {
        return indexDao.count((Postgres9Connection) con, "receiverId", receiverId);
    }

    @Override
    public List<UserMessage> findByReceiverId(PartakeConnection con, String receiverId, int offset, int limit) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE receiverId = ? ORDER BY createdAt DESC OFFSET ? LIMIT ?",
                new Object[] { receiverId, offset, limit });

        try {
            Postgres9IdMapper<UserMessage> idMapper = new Postgres9IdMapper<UserMessage>((Postgres9Connection) con, mapper, entityDao);
            DataIterator<UserMessage> it = new Postgres9DataIterator<UserMessage>(idMapper, psars);
            return DAOUtil.convertToList(it);
        } finally {
            psars.close();
        }
    }
}
