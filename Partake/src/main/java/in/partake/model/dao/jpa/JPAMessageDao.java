package in.partake.model.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IMessageAccess;
import in.partake.model.dto.DirectMessage;

public class JPAMessageDao extends JPADao<DirectMessage> implements IMessageAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, DirectMessage.class);
    }

    @Override
    public void put(PartakeConnection con, DirectMessage embryo) throws DAOException {
        putImpl(con, embryo, DirectMessage.class);
    }

    @Override
    public DirectMessage find(PartakeConnection con, String messageId) throws DAOException {
        return findImpl(con, messageId, DirectMessage.class);
    }    
    
    @Override
    public void remove(PartakeConnection con, String key) throws DAOException {
        removeImpl(con, key, DirectMessage.class);
    }

    @Override
    public DataIterator<DirectMessage> getIterator(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT t FROM Messages t");
        
        @SuppressWarnings("unchecked")
        List<DirectMessage> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<DirectMessage>(em, list, DirectMessage.class, false);
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Messages");
        q.executeUpdate();
    }

    
    @Override
    public DataIterator<DirectMessage> findByEventId(PartakeConnection con, String eventId) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT m FROM Messages m WHERE m.eventId = :eventId ORDER BY m.createdAt desc");
        q.setParameter("eventId", eventId);
        
        @SuppressWarnings("unchecked")
        List<DirectMessage> messages = q.getResultList();
        List<DirectMessage> result = new ArrayList<DirectMessage>();
        for (DirectMessage m : messages) {
            result.add(new DirectMessage(m));
        }
        
        return new JPAPartakeModelDataIterator<DirectMessage>(em, result, DirectMessage.class, false);
    }

}
