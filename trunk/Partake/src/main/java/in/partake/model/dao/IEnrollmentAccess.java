package in.partake.model.dao;

import in.partake.model.dto.Enrollment;

import java.util.List;

public interface IEnrollmentAccess extends ITruncatable {

    public void addEnrollment(PartakeConnection con, Enrollment enrollment) throws DAOException;
    public void removeEnrollment(PartakeConnection con, String userId, String eventId) throws DAOException;
    public Enrollment getEnrollment(PartakeConnection con, String userId, String eventId) throws DAOException;
    /** enrollment の一覧を取得。順不同。 */
    public List<Enrollment> getEnrollmentsByEventId(PartakeConnection con, String eventId) throws DAOException;
    /** enrollment の一覧を取得。順不同。 */
    public List<Enrollment> getEnrollmentsByUserId(PartakeConnection con, String userId) throws DAOException;
    
//    
//    // TODO: changesOnlyComment がださいのでなんとかする
//    
//    @Deprecated
//    public void enroll(PartakeConnection con, User user, Event event, ParticipationStatus status, String comment, boolean changesOnlyComment, boolean forceChangeModifiedAt) throws DAOException;
//    @Deprecated
//    public List<Enrollment> getParticipation(PartakeConnection con, String eventId) throws DAOException;
//    @Deprecated
//    public void setLastStatus(PartakeConnection con, String eventId, Enrollment p, LastParticipationStatus lastStatus) throws DAOException;
//    @Deprecated
//    public int getNumOfParticipants(PartakeConnection con, String eventId, boolean isReservationTimeOver) throws DAOException;
//    @Deprecated
//    public int getOrderOfEnrolledEvent(PartakeConnection con, String eventId, String userId, boolean isReservationTimeOver) throws DAOException;
//    
//    /**
//     * event の一覧を返す。sort されずに返ってくるので、必要に応じて sort する必要がある。
//     * また、cancel したイベントに関しては null が返るので、適切に continue すること。
//     * TODO: 美しくない！　なんとかならないか。sort はしょうがないが。
//     * 
//     * @param user
//     * @return
//     * @throws Exception
//     */
//    @Deprecated
//    public DataIterator<Event> getEnrolledEvents(PartakeConnection connection, String userId) throws DAOException;
//    @Deprecated
//    public ParticipationStatus getParticipationStatus(PartakeConnection con, Event event, User user) throws DAOException;
}
