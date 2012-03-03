package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventReminderAccess;
import in.partake.model.dto.EventReminder;

public class JPAEventReminderDao extends JPADao<EventReminder> implements IEventReminderAccess {

    @Override
    public void put(PartakeConnection con, EventReminder eventReminder) throws DAOException {
        putImpl(con, eventReminder, EventReminder.class);
    }

    @Override
    public EventReminder find(PartakeConnection con, String eventId) throws DAOException {
        return findImpl(con, eventId, EventReminder.class);
    }
    
    @Override
    public void remove(PartakeConnection con, String eventId) throws DAOException {
        removeImpl(con, eventId, EventReminder.class);        
    }
    
    @Override
    public DataIterator<EventReminder> getIterator(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT t FROM EventReminders t");
        
        @SuppressWarnings("unchecked")
        List<EventReminder> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<EventReminder>(em, list, EventReminder.class, false);
        
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM EventReminders");
        q.executeUpdate();
    }

}
