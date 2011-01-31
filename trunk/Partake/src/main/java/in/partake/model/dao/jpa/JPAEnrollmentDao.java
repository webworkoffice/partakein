package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IEnrollmentAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;
import in.partake.model.dto.Participation;
import in.partake.model.dto.User;
import in.partake.model.dto.auxiliary.LastParticipationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;

class JPAEnrollmentDao extends JPADao implements IEnrollmentAccess {

    @Override
    public void enroll(PartakeConnection con, User user, Event event, ParticipationStatus status, String comment, boolean changesOnlyComment,
                    boolean forceChangeModifiedAt) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Participation> getParticipation(PartakeConnection con, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setLastStatus(PartakeConnection con, String eventId, Participation p, LastParticipationStatus lastStatus) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getNumOfParticipants(PartakeConnection con, String eventId, boolean isReservationTimeOver) throws DAOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getOrderOfEnrolledEvent(PartakeConnection con, String eventId, String userId, boolean isReservationTimeOver) throws DAOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public DataIterator<Event> getEnrolledEvents(PartakeConnection connection, String userId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ParticipationStatus getParticipationStatus(PartakeConnection con, Event event, User user) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Enrollments");
        q.executeUpdate();
    }
}
