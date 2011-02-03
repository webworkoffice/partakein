package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IEventReminderAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventReminder;

public class JPAEventReminderDao extends JPADao implements IEventReminderAccess {

    @Override
    public void updateEventReminderStatus(PartakeConnection con, EventReminder reminderStatus) throws DAOException {
        EntityManager em = getEntityManager(con);
        if (em.contains(reminderStatus)) {
            em.merge(reminderStatus);
        } else {
            em.persist(new EventReminder(reminderStatus));
        }
    }

    @Override
    public EventReminder getEventReminderStatus(PartakeConnection con, String eventId) throws DAOException {
        EntityManager em = getEntityManager(con);
        return freeze(em.find(EventReminder.class, eventId));
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM EventReminders");
        q.executeUpdate();
    }

}
