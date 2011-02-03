package in.partake.model.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IEnrollmentAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.pk.EnrollmentPK;

class JPAEnrollmentDao extends JPADao<Enrollment> implements IEnrollmentAccess {

    @Override
    public void addEnrollment(PartakeConnection con, Enrollment enrollment) throws DAOException {
        createOrUpdate(con, enrollment, Enrollment.class);
    }

    @Override
    public void removeEnrollment(PartakeConnection con, String userId, String eventId) throws DAOException {
        remove(con, new EnrollmentPK(userId, eventId), Enrollment.class);
    }

    @Override
    public Enrollment getEnrollment(PartakeConnection con, String userId, String eventId) throws DAOException {
        return find(con, new EnrollmentPK(userId, eventId), Enrollment.class);
    }

    @Override
    public List<Enrollment> getEnrollmentsByEventId(PartakeConnection con, String eventId) throws DAOException {
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
    public List<Enrollment> getEnrollmentsByUserId(PartakeConnection con, String userId) throws DAOException {
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
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Enrollments");
        q.executeUpdate();
    }
}
