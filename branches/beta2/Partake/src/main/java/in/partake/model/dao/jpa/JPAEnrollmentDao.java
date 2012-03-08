package in.partake.model.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEnrollmentAccess;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.pk.EnrollmentPK;

class JPAEnrollmentDao extends JPADao<Enrollment> implements IEnrollmentAccess {

    @Override
    public void put(PartakeConnection con, Enrollment enrollment) throws DAOException {
        putImpl(con, enrollment, Enrollment.class);
    }

    @Override
    public void remove(PartakeConnection con, EnrollmentPK pk) throws DAOException {
        removeImpl(con, pk, Enrollment.class);
    }

    @Override
    public Enrollment find(PartakeConnection con, EnrollmentPK pk) throws DAOException {
        return findImpl(con, pk, Enrollment.class);
    }

    @Override
    public List<Enrollment> findByEventId(PartakeConnection con, String eventId) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT en FROM Enrollments en WHERE en.eventId = :eventId");
        q.setParameter("eventId", eventId);
        
        @SuppressWarnings("unchecked")
        List<Enrollment> enrollments = q.getResultList();
        List<Enrollment> result = new ArrayList<Enrollment>();
        for (Enrollment e : enrollments) {
            if (e == null) { continue; }
            result.add(e.freeze());
        }

        return result;
    }

    @Override
    public List<Enrollment> findByUserId(PartakeConnection con, String userId) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT en FROM Enrollments en WHERE en.userId = :userId");
        q.setParameter("userId", userId);
        
        @SuppressWarnings("unchecked")
        List<Enrollment> enrollments = q.getResultList();
        List<Enrollment> result = new ArrayList<Enrollment>();
        for (Enrollment e : enrollments) {
            if (e == null) { continue; }
            result.add(e.freeze());
        }

        return result;
    }
    
    @Override
    public DataIterator<Enrollment> getIterator(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT t FROM Enrollments t");
        
        @SuppressWarnings("unchecked")
        List<Enrollment> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<Enrollment>(em, list, Enrollment.class, false);
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Enrollments");
        q.executeUpdate();
    }
    
    @Override
    public long count(PartakeConnection con) throws DAOException {
        return countImpl(con, "Enrollments");
    }

}
