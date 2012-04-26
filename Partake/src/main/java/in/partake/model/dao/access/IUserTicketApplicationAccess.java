package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserTicketApplication;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import java.util.List;
import java.util.UUID;

public interface IUserTicketApplicationAccess extends IAccess<UserTicketApplication, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;

    public UserTicketApplication findByTicketIdAndUserId(PartakeConnection con, UUID ticketId, String userId) throws DAOException;
    public void removeByEventTicketIdAndUserId(PartakeConnection con, UUID eventTicketId, String userId) throws DAOException;

    public List<UserTicketApplication> findByTicketId(PartakeConnection con, UUID eventTicketId, int offset, int limit) throws DAOException;
    public int countByTicketId(PartakeConnection con, UUID eventTicketId, ParticipationStatus status) throws DAOException;

    public List<UserTicketApplication> findByEventId(PartakeConnection con, String eventId, int offset, int limit) throws DAOException;
    public int countByEventId(PartakeConnection con, String eventId, ParticipationStatus status) throws DAOException;

    public List<UserTicketApplication> findByUserId(PartakeConnection con, String userId, int offset, int limit) throws DAOException;
    public int countByUserId(PartakeConnection con, String userId) throws DAOException;
    public int countByUserId(PartakeConnection con, String userId, ParticipationStatus status) throws DAOException;
}
