package in.partake.model.dao;

import in.partake.base.TimeUtil;
import in.partake.model.dao.access.IEnrollmentAccess;
import in.partake.model.dto.Event;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.User;
import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.model.dto.pk.EnrollmentPK;
import in.partake.service.DBService;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class EnrollmentAccessTest extends AbstractDaoTestCaseBase<IEnrollmentAccess, Enrollment, EnrollmentPK> {
	@Rule
	public TestName name = new TestName();

	@Before
	public void setup() throws DAOException {
	    super.setup(DBService.getFactory().getEnrollmentAccess());
	}
	
	@Override
	protected Enrollment create(long pkNumber, String pkSalt, int objNumber) {
	    return new Enrollment("userId" + pkSalt + pkNumber,
	                    "eventId" + pkSalt + pkNumber,
	                    "comment" + objNumber,
	                    ParticipationStatus.ENROLLED,
	                    false,
	                    ModificationStatus.CHANGED,
	                    AttendanceStatus.UNKNOWN,
	                    new Date(1L));
	}

	@Test
	public void testGetEmptyPaticipationList() throws DAOException {
		PartakeConnection con = getPool().getConnection();

		try {
		    String eventId = "eventId" + System.currentTimeMillis();
		    List<Enrollment> list = dao.findByEventId(con, eventId);
			Assert.assertTrue(list.isEmpty());
		} finally {
			con.invalidate();
		}
	}

	@Test
	public void testPutAndGetPaticipationList() throws DAOException {
		ParticipationStatus status = ParticipationStatus.ENROLLED;
		
		String eventId = UUID.randomUUID().toString();
		String userId = UUID.randomUUID().toString();
		
		Event event = createEvent(eventId, userId);

		PartakeConnection con = getPool().getConnection();
		try {
		    con.beginTransaction();
		    event.setId(eventId);
			getFactory().getEventAccess().put(con, event);
			getFactory().getUserAccess().put(con, new User(userId, 0, new Date(), null)); 

			dao.put(con, new Enrollment(userId, eventId, "", ParticipationStatus.ENROLLED, false, ModificationStatus.CHANGED, AttendanceStatus.UNKNOWN, new Date()));
			
			List<Enrollment> list = dao.findByEventId(con, eventId);
			con.commit();
			
			Assert.assertEquals(1, list.size());
			Enrollment storedParticipation = list.get(0);
			Assert.assertNotNull(storedParticipation);
			Assert.assertEquals(userId, storedParticipation.getUserId());
			Assert.assertEquals(ModificationStatus.CHANGED, storedParticipation.getModificationStatus());
			Assert.assertEquals(status, storedParticipation.getStatus());
		} finally {
			con.invalidate();
		}
	}

	@Test
	public void testPutAndUpdatePaticipationList() throws DAOException {
        String eventId = UUID.randomUUID().toString();
        String userId  = UUID.randomUUID().toString();
        
        Event event = createEvent(eventId, userId);

        PartakeConnection con = getPool().getConnection();
        try {
            
            // create
            {
                con.beginTransaction();
                event.setId(eventId);
                getFactory().getEventAccess().put(con, event);
                getFactory().getUserAccess().put(con, new User(userId, 0, new Date(), null));     
                dao.put(con, new Enrollment(userId, eventId, "", ParticipationStatus.ENROLLED, false, ModificationStatus.CHANGED, AttendanceStatus.UNKNOWN, new Date()));
                con.commit();
            }
            
            // update
            {
                con.beginTransaction();
                List<Enrollment> storedList = dao.findByEventId(con, eventId);
                Enrollment storedParticipation = storedList.get(0);
                Assert.assertNotNull(storedParticipation);
                ModificationStatus newStatus = ModificationStatus.NOT_ENROLLED;
                Assert.assertFalse(newStatus.equals(storedParticipation.getModificationStatus()));
                Enrollment newStoredParticipation = new Enrollment(storedParticipation);
                newStoredParticipation.setModificationStatus(ModificationStatus.CHANGED);
                dao.put(con, newStoredParticipation);
                con.commit();
            }
            
            // get
            {
                con.beginTransaction();
                List<Enrollment> updatedList = dao.findByEventId(con, eventId);
                Assert.assertEquals(1, updatedList.size());
                Enrollment updatedParticipation = updatedList.get(0);
                Assert.assertEquals(userId, updatedParticipation.getUserId());
                Assert.assertEquals(ModificationStatus.CHANGED, updatedParticipation.getModificationStatus());
                Assert.assertEquals(ParticipationStatus.ENROLLED, updatedParticipation.getStatus());
                con.commit();
            }
            
            
        } finally {
            con.invalidate();
        }
	}

	private Event createEvent(String eventId, String userId) {
		Date beginDate = TimeUtil.getCurrentDate();
		Date now = TimeUtil.getCurrentDate();
		String url = "http://localhost:8080/";
		String place = "";
		String address = "";
		String description = "";
		Event event = new Event(eventId, "DUMMY EVENT", "DUMMY EVENT", "DUMMY CATEGORY", null, beginDate , null, 0, url , place , address , description , "#partakein", userId, null, true, "passcode", false, false, now, now);
		event.setId(eventId);
		return event;
	}
}
