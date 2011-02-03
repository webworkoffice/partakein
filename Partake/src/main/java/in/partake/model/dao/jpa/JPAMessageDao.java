package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IMessageAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Message;

public class JPAMessageDao extends JPADao implements IMessageAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, Message.class);
    }

    @Override
    public Message getMessage(PartakeConnection con, String messageId) throws DAOException {
        EntityManager em = getEntityManager(con);
        return freeze(em.find(Message.class, messageId));
    }

    @Override
    public void addMessage(PartakeConnection con, Message embryo) throws DAOException {
        EntityManager em = getEntityManager(con);
        em.persist(new Message(embryo));
    }

    @Override
    public DataIterator<Message> getMessagesByEventId(PartakeConnection con, String eventId) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT m FROM Messages m WHERE m.eventId = :eventId");
        q.setParameter("eventId", eventId);
        
        @SuppressWarnings("unchecked")
        List<Message> messages = q.getResultList();
        return new JPAPartakeModelDataIterator<Message>(em, messages, false);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Messages");
        q.executeUpdate();
    }
}
