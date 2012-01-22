package in.partake.model.dao.postgres9.impl;

import java.util.List;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventActivityAccess;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dto.EventActivity;

public class Postgres9EventActivityDao extends Postgres9Dao implements IEventActivityAccess {
    static final String TABLE_NAME = "EventActivityEntities";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;

    public Postgres9EventActivityDao() {
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
    public void put(PartakeConnection con, EventActivity t) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public EventActivity find(PartakeConnection con, String key) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove(PartakeConnection con, String key) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public DataIterator<EventActivity> getIterator(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EventActivity> findByEventId(PartakeConnection con, String eventId, int length) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

}
