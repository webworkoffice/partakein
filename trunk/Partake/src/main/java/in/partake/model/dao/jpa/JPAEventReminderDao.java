package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IEventReminderAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventReminder;

public class JPAEventReminderDao extends JPADao<EventReminder> implements IEventReminderAccess {

    @Override
    public void updateEventReminderStatus(PartakeConnection con, EventReminder eventReminder) throws DAOException {
        createOrUpdate(con, eventReminder, EventReminder.class);
    }

    @Override
    public EventReminder getEventReminderStatus(PartakeConnection con, String eventId) throws DAOException {
        return find(con, eventId, EventReminder.class);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM EventReminders");
        q.executeUpdate();
    }

}
