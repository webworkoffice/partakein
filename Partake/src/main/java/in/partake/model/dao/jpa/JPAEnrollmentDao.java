package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IEnrollmentAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Enrollment;

class JPAEnrollmentDao extends JPADao implements IEnrollmentAccess {

    @Override
    public void addEnrollment(PartakeConnection con, Enrollment participation) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public void removeEnrollment(PartakeConnection con, String userId, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public Enrollment getEnrollment(PartakeConnection con, String userId, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public List<Enrollment> getEnrollmentsByEventId(PartakeConnection con, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public List<Enrollment> getEnrollmentsByUserId(PartakeConnection con, String userId) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Enrollments");
        q.executeUpdate();
    }
}
