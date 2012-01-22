package in.partake.model.dao.postgres9.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventReminderAccess;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dto.EventReminder;

public class Postgres9EventReminderDao extends Postgres9Dao implements IEventReminderAccess {
    static final String TABLE_NAME = "EventReminderEntities";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;

    public Postgres9EventReminderDao() {
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
    public void put(PartakeConnection con, EventReminder t) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public EventReminder find(PartakeConnection con, String key) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove(PartakeConnection con, String key) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public DataIterator<EventReminder> getIterator(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

}
