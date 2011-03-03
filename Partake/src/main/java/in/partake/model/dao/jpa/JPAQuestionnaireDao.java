package in.partake.model.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IQuestionnaireAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Questionnaire;

public class JPAQuestionnaireDao extends JPADao<Questionnaire> implements IQuestionnaireAccess {

    @Override
    public void put(PartakeConnection con, Questionnaire t) throws DAOException {
        putImpl(con, t, Questionnaire.class);        
    }

    @Override
    public Questionnaire find(PartakeConnection con, String key) throws DAOException {
        return findImpl(con, key, Questionnaire.class);
    }

    @Override
    public void remove(PartakeConnection con, String key) throws DAOException {
        removeImpl(con, key, Questionnaire.class);
    }

    @Override
    public DataIterator<Questionnaire> getIterator(PartakeConnection con) throws DAOException {
        return getIteratorImpl(con, "Questionnaires", Questionnaire.class);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        truncateImpl(con, "Questionnaires");
    }

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, Questionnaire.class);
    }

    @Override
    public List<Questionnaire> findQuestionnairesByEventId(PartakeConnection con, String eventId) throws DAOException {
        EntityManager em = getEntityManager(con);
        
        Query q = em.createQuery("SELECT t FROM Questionnaires t WHERE t.eventId = :eventId ORDER BY t.questionNo ASC");
        // Query q = em.createQuery("SELECT t FROM Questionnaires t WHERE t.eventId = :eventId");
        q.setParameter("eventId", eventId);
        
        List<Questionnaire> result = new ArrayList<Questionnaire>();
        for (Questionnaire qst : (List<Questionnaire>) q.getResultList()) {
            result.add(qst.freeze());
        }
        
        return result;
    }

    @Override
    public void removeByEventId(PartakeConnection con, String eventId) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Questionnaires t WHERE t.eventId = :eventId");
        q.setParameter("eventId", eventId);
        q.executeUpdate();        
    }

}
