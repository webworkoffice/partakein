package in.partake.model.dao.postgres9.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.ICommentAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9DataIterator;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dao.postgres9.Postgres9ResultSetMapper;
import in.partake.model.dao.postgres9.Postgres9StatementAndResultSet;
import in.partake.model.dto.Comment;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.json.JSONObject;

public class Postgres9CommentDao extends Postgres9Dao implements ICommentAccess {
    static final String TABLE_NAME = "CommentEntities";
    static final int CURRENT_VERSION = 1;
    static final String INDEX_TABLE_NAME = "CommentIndex";
    
    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    
    public Postgres9CommentDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);
        
        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, eventId TEXT NOT NULL, createdAt TIMESTAMP NOT NULL)");
            indexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "EventId" + " ON " + INDEX_TABLE_NAME + "(eventId, createdAt)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        entityDao.truncate((Postgres9Connection) con);
        indexDao.truncate((Postgres9Connection) con);
    }

    @Override
    public void put(PartakeConnection con, Comment comment) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        
        Postgres9Entity entity = new Postgres9Entity(comment.getId(), CURRENT_VERSION, comment.toJSON().toString().getBytes(UTF8), null, comment.getCreatedAt());
        if (entityDao.exists(pcon, comment.getId()))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);

        indexDao.put(pcon, new String[] { "id", "eventId", "createdAt" }, new Object[] { comment.getId(), comment.getEventId(), comment.getCreatedAt() });
    }

    @Override
    public Comment find(PartakeConnection con, String id) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        
        Postgres9Entity entity = entityDao.find(pcon, id);
        if (entity == null)
            return null;
        
        // Checks the entity.
        JSONObject obj = JSONObject.fromObject(new String(entity.getBody(), UTF8));
        return new Comment(obj).freeze();
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        entityDao.remove((Postgres9Connection) con, id);
        indexDao.remove((Postgres9Connection) con, "id", id);
    }

    @Override
    public DataIterator<Comment> getIterator(PartakeConnection con) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return entityDao.getFreshId((Postgres9Connection) con);
    }

    @Override
    public DataIterator<Comment> getCommentsByEvent(PartakeConnection con, String eventId) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE eventId = ? ORDER BY createdAt ASC",
                new Object[] { eventId });

        return new Postgres9DataIterator<Comment>(new Postgres9ResultSetMapper<Comment>((Postgres9Connection) con) {
            @Override
            public Comment map(ResultSet resultSet) throws DAOException {
                try {
                    String id = resultSet.getString("id");
                    if (id == null)
                        return null;
                    
                    return Postgres9CommentDao.this.find((Postgres9Connection) con, id);
                } catch (SQLException e) {
                    throw new DAOException(e);
                }
            }
        }, psars);
    }

}
