package in.partake.model.dao;

import in.partake.model.dto.Event;
import in.partake.model.dto.Participation;
import in.partake.model.dto.User;
import in.partake.model.dto.aux.LastParticipationStatus;
import in.partake.model.dto.aux.ParticipationStatus;

import java.util.List;

public interface IEnrollmentAccess extends ITruncatable {

    // TODO: changesOnlyComment がださいのでなんとかする
    public void enroll(PartakeConnection con, User user, Event event, ParticipationStatus status, String comment, boolean changesOnlyComment, boolean forceChangeModifiedAt) throws DAOException;
    
    public List<Participation> getParticipation(PartakeConnection con, String eventId) throws DAOException;
    
    public void setLastStatus(PartakeConnection con, String eventId, Participation p, LastParticipationStatus lastStatus) throws DAOException;
    
    public int getNumOfParticipants(PartakeConnection con, String eventId, boolean isReservationTimeOver) throws DAOException;
    
    public int getOrderOfEnrolledEvent(PartakeConnection con, String eventId, String userId, boolean isReservationTimeOver) throws DAOException;
    
    /**
     * event の一覧を返す。sort されずに返ってくるので、必要に応じて sort する必要がある。
     * また、cancel したイベントに関しては null が返るので、適切に continue すること。
     * TODO: 美しくない！　なんとかならないか。sort はしょうがないが。
     * 
     * @param user
     * @return
     * @throws Exception
     */
    public DataIterator<Event> getEnrolledEvents(PartakeConnection connection, String userId) throws DAOException;
    public ParticipationStatus getParticipationStatus(PartakeConnection con, Event event, User user) throws DAOException;
}
