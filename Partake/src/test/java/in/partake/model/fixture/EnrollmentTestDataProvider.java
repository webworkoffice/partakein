package in.partake.model.fixture;

import java.util.Date;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.access.IEnrollmentAccess;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;

public class EnrollmentTestDataProvider extends TestDataProvider {

    public void createFixtures(PartakeConnection con, PartakeDAOFactory factory) throws DAOException {
        IEnrollmentAccess dao = factory.getEnrollmentAccess();
        
        dao.truncate(con);
        dao.put(con, new Enrollment(
                USER_ID1, EVENT_ID1, "comment", ParticipationStatus.ENROLLED, false, 
                ModificationStatus.CHANGED, AttendanceStatus.PRESENT, new Date()));
        dao.put(con, new Enrollment(
                USER_ID1, EVENT_ID2, "comment", ParticipationStatus.ENROLLED, false, 
                ModificationStatus.CHANGED, AttendanceStatus.ABSENT, new Date()));
        dao.put(con, new Enrollment(
                USER_ID1, EVENT_ID3, "comment", ParticipationStatus.ENROLLED, false, 
                ModificationStatus.CHANGED, AttendanceStatus.UNKNOWN, new Date()));
    }
}
