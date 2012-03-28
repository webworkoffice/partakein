package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import java.util.List;

public interface IEnrollmentAccess extends IAccess<Enrollment, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;

    public Enrollment findByEventIdAndUserId(PartakeConnection con, String eventId, String userId) throws DAOException;
    public void removeByEventIdAndUserId(PartakeConnection con, String eventId, String userId) throws DAOException;

    /** enrollment の一覧を取得。 */
    // TODO: Should return DataIterator.
    @Deprecated
    public List<Enrollment> findByEventId(PartakeConnection con, String eventId) throws DAOException;
    public List<Enrollment> findByEventId(PartakeConnection con, String eventId, int offset, int limit) throws DAOException;
    // Counts the number of participants.
    public int countParticipants(PartakeConnection con, String eventId, ParticipationStatus status) throws DAOException;

    /** enrollment の一覧を取得。 */
    @Deprecated
    public List<Enrollment> findByUserId(PartakeConnection con, String userId) throws DAOException;
    public List<Enrollment> findByUserId(PartakeConnection con, String userId, int offset, int limit) throws DAOException;
    public int countEventsByUserId(PartakeConnection con, String userId) throws DAOException;
    public int countEventsByUserId(PartakeConnection con, String userId, ParticipationStatus status) throws DAOException;
}
