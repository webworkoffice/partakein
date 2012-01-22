package in.partake.model.dao.postgres9.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserPreferenceAccess;

import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dto.UserPreference;

public class Postgres9UserPreferenceDao extends Postgres9Dao implements IUserPreferenceAccess {
    static final String TABLE_NAME = "UserPreferenceEntities";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;

    public Postgres9UserPreferenceDao() {
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
    public void put(PartakeConnection con, UserPreference t) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public UserPreference find(PartakeConnection con, String key) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove(PartakeConnection con, String key) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public DataIterator<UserPreference> getIterator(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    
}
