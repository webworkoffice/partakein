package in.partake.model.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IMessageAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Message;

public class JPAMessageDao extends JPADao<Message> implements IMessageAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, Message.class);
    }

    @Override
    public void put(PartakeConnection con, Message embryo) throws DAOException {
        putImpl(con, embryo, Message.class);
    }

    @Override
    public Message find(PartakeConnection con, String messageId) throws DAOException {
        return findImpl(con, messageId, Message.class);
    }    
    
    @Override
    public void remove(PartakeConnection con, String key) throws DAOException {
        removeImpl(con, key, Message.class);
    }

    @Override
    public DataIterator<Message> getIterator(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT t FROM Messages t");
        
        @SuppressWarnings("unchecked")
        List<Message> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<Message>(em, list, Message.class, false);
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Messages");
        q.executeUpdate();
    }

    
    @Override
    public DataIterator<Message> findByEventId(PartakeConnection con, String eventId) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT m FROM Messages m WHERE m.eventId = :eventId ORDER BY m.createdAt desc");
        q.setParameter("eventId", eventId);
        
        @SuppressWarnings("unchecked")
        List<Message> messages = q.getResultList();
        List<Message> result = new ArrayList<Message>();
        for (Message m : messages) {
            result.add(new Message(m));
        }
        
        return new JPAPartakeModelDataIterator<Message>(em, result, Message.class, false);
    }

}
