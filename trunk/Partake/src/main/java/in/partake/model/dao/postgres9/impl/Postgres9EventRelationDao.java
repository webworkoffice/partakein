package in.partake.model.dao.postgres9.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.DataMapper;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventRelationAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9DataIterator;
import in.partake.model.dao.postgres9.Postgres9Entity;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dao.postgres9.Postgres9StatementAndResultSet;
import in.partake.model.dto.EventRelation;
import in.partake.model.dto.pk.EventRelationPK;
import in.partake.util.PDate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

public class Postgres9EventRelationDao extends Postgres9Dao implements IEventRelationAccess {
    static final String TABLE_NAME = "EventRelationEntities";
    static final int CURRENT_VERSION = 1;
    static final String INDEX_TABLE_NAME = "EventRelationIndex";

    private final Postgres9EntityDao entityDao;
    private final Postgres9IndexDao indexDao;
    
    public Postgres9EventRelationDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
        this.indexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.initialize(pcon);
        
        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            indexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(id TEXT PRIMARY KEY, srcEventId TEXT NOT NULL, dstEventId TEXT NOT NULL)");
            indexDao.createIndex(pcon, "CREATE UNIQUE INDEX " + INDEX_TABLE_NAME + "EventId" + " ON " + INDEX_TABLE_NAME + "(srcEventId, dstEventId)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        entityDao.truncate((Postgres9Connection) con);
        indexDao.truncate((Postgres9Connection) con);
    }

    @Override
    public void put(PartakeConnection con, EventRelation relation) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", new String[] { "srcEventId", "dstEventId" }, new Object[] { relation.getSrcEventId(), relation.getDstEventId() }); 
        if (id == null)
            id = entityDao.getFreshId(pcon);
        
        // TODO: EventRelation should be merged into Event.
        Postgres9Entity entity = new Postgres9Entity(id, CURRENT_VERSION, relation.toJSON().toString().getBytes(UTF8), null, PDate.getCurrentDate().getDate());

        if (entityDao.exists(pcon, id))
            entityDao.update(pcon, entity);
        else
            entityDao.insert(pcon, entity);
        indexDao.put(pcon, new String[] { "id", "srcEventId", "dstEventId" } , new Object[] { id, relation.getSrcEventId(), relation.getDstEventId() });
    }

    @Override
    public EventRelation find(PartakeConnection con, EventRelationPK pk) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", new String[] { "srcEventId", "dstEventId" }, new Object[] { pk.getSrcEventId(), pk.getDstEventId() }); 
        if (id == null)
            return null;

        return findById(con, id);
    }

    EventRelation findById(PartakeConnection con, String id) throws DAOException {
        Postgres9Entity entity = entityDao.find((Postgres9Connection) con, id);
        if (entity == null)
            return null;
        
        // TODO: Check the entity is regular.
        JSONObject obj = JSONObject.fromObject(new String(entity.getBody(), UTF8));
        return new EventRelation(obj).freeze();
    }
    
    @Override
    public void remove(PartakeConnection con, EventRelationPK pk) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        String id = indexDao.find(pcon, "id", new String[] { "srcEventId", "dstEventId" }, new Object[] { pk.getSrcEventId(), pk.getDstEventId() }); 
        if (id == null)
            return;

        removeById(con, id);
    }

    void removeById(PartakeConnection con, String id) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        entityDao.remove(pcon, id);
        indexDao.remove(pcon, "id", id);        
    }
    
    @Override
    public DataIterator<EventRelation> getIterator(PartakeConnection con) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeByEventId(PartakeConnection con, String srcEventId) throws DAOException {
        // TODO: When EventRelation is merged into Event, removeByEventId won't be necessary.
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE srcEventId = ?",
                new Object[] { srcEventId });

        try {
            while (psars.getResultSet().next()) {
                String id = psars.getResultSet().getString(1);
                removeById(con, id);
            }
        } catch (SQLException e) {
            throw new DAOException();
        } finally {
            psars.close();
        }
        
        indexDao.remove((Postgres9Connection) con, "srcEventId", srcEventId);
    }

    // TODO: Why not DataIterator?
    @Override
    public List<EventRelation> findByEventId(PartakeConnection con, String srcEventId) throws DAOException {
        Postgres9StatementAndResultSet psars = indexDao.select((Postgres9Connection) con,
                "SELECT id FROM " + INDEX_TABLE_NAME + " WHERE srcEventId = ?",
                new Object[] { srcEventId });

        class Mapper implements DataMapper<ResultSet, EventRelation> {
            private Postgres9Connection con;
            
            public Mapper(Postgres9Connection con) {
                this.con = con;
            }
            
            @Override
            public EventRelation map(ResultSet rs) throws DAOException {
                try {
                    String id = rs.getString("id");
                    if (id == null)
                        return null;
                    
                    return Postgres9EventRelationDao.this.findById((Postgres9Connection) con, id);
                } catch (SQLException e) {
                    throw new DAOException(e);
                }
            }

            @Override
            public ResultSet unmap(EventRelation t) throws DAOException {
                throw new UnsupportedOperationException();
            }
        }
        
        try {
            DataIterator<EventRelation> it = new Postgres9DataIterator<EventRelation>(new Mapper((Postgres9Connection) con), psars);
            
            ArrayList<EventRelation> rels = new ArrayList<EventRelation>();
            while (it.hasNext()) {
                EventRelation rel = it.next();
                if (rel == null)
                    continue;
                rels.add(rel.freeze());
            }
            
            return rels;
        } finally {
            psars.close();
        }
    }
}
