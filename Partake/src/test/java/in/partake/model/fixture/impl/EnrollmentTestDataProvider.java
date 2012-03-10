package in.partake.model.fixture.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.IEnrollmentAccess;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.fixture.TestDataProvider;

import java.util.Date;

public class EnrollmentTestDataProvider extends TestDataProvider<Enrollment> {

    @Override
    public Enrollment create() {
        throw new RuntimeException("Not implemented yet");
    }
    
    @Override
    public Enrollment create(long pkNumber, String pkSalt, int objNumber) {
        throw new RuntimeException("Not implemented yet");
    }    
    
    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        IEnrollmentAccess dao = factory.getEnrollmentAccess();
        
        dao.truncate(con);
        dao.put(con, new Enrollment(
                EVENT_ENROLLED_USER_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.ENROLLED, false, 
                ModificationStatus.CHANGED, AttendanceStatus.PRESENT, new Date()));
        dao.put(con, new Enrollment(
                EVENT_RESERVED_USER_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.RESERVED, false, 
                ModificationStatus.CHANGED, AttendanceStatus.PRESENT, new Date()));
        dao.put(con, new Enrollment(
                EVENT_CANCELLED_USER_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.CANCELLED, false, 
                ModificationStatus.CHANGED, AttendanceStatus.PRESENT, new Date()));
        
        dao.put(con, new Enrollment(
                ATTENDANCE_PRESENT_USER_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.ENROLLED, false, 
                ModificationStatus.CHANGED, AttendanceStatus.PRESENT, new Date()));
        dao.put(con, new Enrollment(
                ATTENDANCE_ABSENT_USER_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.ENROLLED, false, 
                ModificationStatus.CHANGED, AttendanceStatus.ABSENT, new Date()));
        dao.put(con, new Enrollment(
                ATTENDANCE_UNKNOWN_USER_ID, DEFAULT_EVENT_ID, "comment", ParticipationStatus.ENROLLED, false, 
                ModificationStatus.CHANGED, AttendanceStatus.UNKNOWN, new Date()));
    }
}
