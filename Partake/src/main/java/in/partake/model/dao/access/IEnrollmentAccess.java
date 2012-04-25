package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import java.util.List;
import java.util.UUID;

public interface IEnrollmentAccess extends IAccess<Enrollment, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;

    public Enrollment findByTicketIdAndUserId(PartakeConnection con, UUID ticketId, String userId) throws DAOException;
    public void removeByEventTicketIdAndUserId(PartakeConnection con, UUID eventTicketId, String userId) throws DAOException;

    public List<Enrollment> findByTicketId(PartakeConnection con, UUID eventTicketId, int offset, int limit) throws DAOException;
    public int countByTicketId(PartakeConnection con, UUID eventTicketId, ParticipationStatus status) throws DAOException;

    public List<Enrollment> findByEventId(PartakeConnection con, String eventId, int offset, int limit) throws DAOException;
    public int countByEventId(PartakeConnection con, String eventId, ParticipationStatus status) throws DAOException;

    public List<Enrollment> findByUserId(PartakeConnection con, String userId, int offset, int limit) throws DAOException;
    public int countByUserId(PartakeConnection con, String userId) throws DAOException;
    public int countByUserId(PartakeConnection con, String userId, ParticipationStatus status) throws DAOException;
}
