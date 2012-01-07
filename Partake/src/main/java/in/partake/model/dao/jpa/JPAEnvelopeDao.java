package in.partake.model.dao.jpa;

import javax.persistence.Query;
import javax.persistence.EntityManager;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEnvelopeAccess;
import in.partake.model.dto.Envelope;

public class JPAEnvelopeDao extends JPADao<Envelope> implements IEnvelopeAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, Envelope.class);
    }
    
    @Override
    public void put(PartakeConnection con, Envelope envelope) throws DAOException {
        putImpl(con, envelope, Envelope.class);
    }
    
    @Override
    public Envelope find(PartakeConnection con, String key) throws DAOException {
        return findImpl(con, key, Envelope.class);
    }
    
    @Override
    public void remove(PartakeConnection con, String key) throws DAOException {
        removeImpl(con, key, Envelope.class);
    }

    @Override
    public DataIterator<Envelope> getIterator(PartakeConnection con) throws DAOException {
        return getIteratorImpl(con, "Envelopes", Envelope.class);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Envelopes");
        q.executeUpdate();
    }

}
