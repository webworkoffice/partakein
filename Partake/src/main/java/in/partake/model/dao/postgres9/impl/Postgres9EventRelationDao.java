package in.partake.model.dao.postgres9.impl;

import java.util.List;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventRelationAccess;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dto.EventRelation;
import in.partake.model.dto.pk.EventRelationPK;

public class Postgres9EventRelationDao extends Postgres9Dao implements IEventRelationAccess {
    static final String TABLE_NAME = "EventRelationEntities";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;

    public Postgres9EventRelationDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void put(PartakeConnection con, EventRelation t) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public EventRelation find(PartakeConnection con, EventRelationPK key) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove(PartakeConnection con, EventRelationPK key) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public DataIterator<EventRelation> getIterator(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeByEventId(PartakeConnection con, String srcEventId) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<EventRelation> findByEventId(PartakeConnection con, String srcEventId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

}
