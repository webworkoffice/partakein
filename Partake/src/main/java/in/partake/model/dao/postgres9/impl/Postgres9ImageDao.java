package in.partake.model.dao.postgres9.impl;

import in.partake.base.TimeUtil;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.MapperDataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IImageAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9EntityDataMapper;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dao.postgres9.Postgres9StatementAndResultSet;
import in.partake.model.dto.ImageData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

class EntityImageMapper extends Postgres9EntityDataMapper<ImageData> {
    public ImageData map(Postgres9Entity entity) throws DAOException {
        if (entity == null)
            return null;

        JSONObject obj = JSONObject.fromObject(new String(entity.getBody(), UTF8));
        ImageData imageData = new ImageData(obj);
        imageData.setData(entity.getOpt());

        return imageData.freeze();
    }
}

public class Postgres9ImageDao extends Postgres9Dao implements IImageAccess {
    static final String TABLE_NAME = "ImageEntities";
    static final int CURRENT_VERSION = 1;
    static final String USER_INDEX_TABLE_NAME = "ImageUserIndex";

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    private final EntityImageMapper mapper;


    public Postgres9ImageDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(USER_INDEX_TABLE_NAME);
        this.mapper = new EntityImageMapper();
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);

        if (!existsTable(pcon, USER_INDEX_TABLE_NAME)) {
            indexDao.createIndexTable(pcon, "CREATE TABLE " + USER_INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, userId TEXT NOT NULL, createdAt TIMESTAMP)");
            indexDao.createIndex(pcon, "CREATE INDEX " + USER_INDEX_TABLE_NAME + "UserId" + " ON " + USER_INDEX_TABLE_NAME + "(userId)");
            indexDao.createIndex(pcon, "CREATE INDEX " + USER_INDEX_TABLE_NAME + "CreatedAt" + " ON " + USER_INDEX_TABLE_NAME + "(createdAt)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        entityDao.truncate((Postgres9Connection) con);
        indexDao.truncate((Postgres9Connection) con);
    }

    @Override
    public void put(PartakeConnection con, ImageData imageData) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;

        Postgres9Entity entity = new Postgres9Entity(imageData.getId(), CURRENT_VERSION, imageData.toJSON().toString().getBytes(UTF8), imageData.getData(), TimeUtil.getCurrentDate());
        if (entityDao.exists(pcon, imageData.getId()))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);

        indexDao.put(pcon, new String[] {"id", "userId", "createdAt"}, new Object[] { imageData.getId(), imageData.getUserId(), imageData.getCreatedAt() });
    }

    @Override
    public ImageData find(PartakeConnection con, String id) throws DAOException {
        return mapper.map(entityDao.find((Postgres9Connection) con, id));
    }

    @Override
    public boolean exists(PartakeConnection con, String id) throws DAOException {
        return entityDao.exists((Postgres9Connection) con, id);
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        entityDao.remove((Postgres9Connection) con, id);
        indexDao.remove((Postgres9Connection) con, "id", id);
    }

    @Override
    public DataIterator<ImageData> getIterator(PartakeConnection con) throws DAOException {
        return new MapperDataIterator<Postgres9Entity, ImageData>(mapper, entityDao.getIterator((Postgres9Connection) con));
    }

    @Override
    public List<String> findIdsByUserId(PartakeConnection con, String userId, int offset, int limit) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + USER_INDEX_TABLE_NAME + " WHERE userId = ?  ORDER BY createdAt DESC OFFSET ? LIMIT ?",
                new Object[] { userId, offset, limit });

        ArrayList<String> result = new ArrayList<String>();
        try {
            ResultSet rs = psars.getResultSet();
            while (rs.next())
                result.add(rs.getString(1));
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            psars.close();
        }

        return result;
    }

    @Override
    public int countByUserId(PartakeConnection con, String userId) throws DAOException {
        return indexDao.count((Postgres9Connection) con, "userId", userId);
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
