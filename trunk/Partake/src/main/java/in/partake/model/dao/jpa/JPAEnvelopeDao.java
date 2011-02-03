package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.EntityManager;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IEnvelopeAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Envelope;

public class JPAEnvelopeDao extends JPADao<Envelope> implements IEnvelopeAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, Envelope.class);
    }
    
    @Override
    public void enqueueEnvelope(PartakeConnection con, Envelope envelope) throws DAOException {
        EntityManager em = getEntityManager(con);
        em.persist(envelope);
    }

    @Override
    public DataIterator<Envelope> getEnvelopeIterator(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT envelope FROM Envelopes envelope"); 
        @SuppressWarnings("unchecked")
        List<Envelope> envelopes = q.getResultList();
        
        return new JPAPartakeModelDataIterator<Envelope>(em, envelopes, Envelope.class, true);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Envelopes");
        q.executeUpdate();
    }

}
