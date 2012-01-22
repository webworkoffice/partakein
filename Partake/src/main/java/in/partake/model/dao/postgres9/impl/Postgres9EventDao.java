package in.partake.model.dao.postgres9.impl;

import java.util.List;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dto.Event;

public class Postgres9EventDao extends Postgres9Dao implements IEventAccess {
    static final String TABLE_NAME = "EventEntities";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;

    public Postgres9EventDao() {
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
    public void put(PartakeConnection con, Event t) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Event find(PartakeConnection con, String key) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove(PartakeConnection con, String key) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public DataIterator<Event> getIterator(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isRemoved(PartakeConnection con, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Event> findByOwnerId(PartakeConnection con, String userId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Event> findByScreenName(PartakeConnection con, String screenName) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

}
