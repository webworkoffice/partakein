package in.partake.model.dao;

import in.partake.model.dto.Event;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.User;
import in.partake.model.dto.auxiliary.LastParticipationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.util.PDate;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public abstract class EnrollmentAccessTestCaseBase extends AbstractDaoTestCaseBase {
	@Rule
	public TestName name = new TestName();

	private IEnrollmentAccess dao;

	@Before
	public void setup() throws DAOException {
	    super.setup(getFactory().getEnrollmentAccess());
	    
		dao = getFactory().getEnrollmentAccess();
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
		
		String eventId = "eventId-pagp-" + System.currentTimeMillis();
		String userId  = "userId-pagp-"  + System.currentTimeMillis();
		
		Event event = createEvent(eventId, userId);

		PartakeConnection con = getPool().getConnection();
		try {
		    con.beginTransaction();
		    event.setId(eventId);
			getFactory().getEventAccess().put(con, event);
			getFactory().getUserAccess().put(con, new User(userId, 0, new Date(), null)); 

			dao.put(con, new Enrollment(userId, eventId, "", ParticipationStatus.ENROLLED, false, LastParticipationStatus.CHANGED, new Date()));
			
			List<Enrollment> list = dao.findByEventId(con, eventId);
			con.commit();
			
			Assert.assertEquals(1, list.size());
			Enrollment storedParticipation = list.get(0);
			Assert.assertNotNull(storedParticipation);
			Assert.assertEquals(userId, storedParticipation.getUserId());
			Assert.assertEquals(LastParticipationStatus.CHANGED, storedParticipation.getLastStatus());
			Assert.assertEquals(status, storedParticipation.getStatus());
		} finally {
			con.invalidate();
		}
	}

	@Test
	public void testPutAndUpdatePaticipationList() throws DAOException {
        String eventId = "eventId-paup-" + System.currentTimeMillis();
        String userId  = "userId-paup-"  + System.currentTimeMillis();
        
        Event event = createEvent(eventId, userId);

        PartakeConnection con = getPool().getConnection();
        try {
            
            // create
            {
                con.beginTransaction();
                event.setId(eventId);
                getFactory().getEventAccess().put(con, event);
                getFactory().getUserAccess().put(con, new User(userId, 0, new Date(), null));     
                dao.put(con, new Enrollment(userId, eventId, "", ParticipationStatus.ENROLLED, false, LastParticipationStatus.CHANGED, new Date()));
                con.commit();
            }
            
            // update
            {
                con.beginTransaction();
                List<Enrollment> storedList = dao.findByEventId(con, eventId);
                Enrollment storedParticipation = storedList.get(0);
                Assert.assertNotNull(storedParticipation);
                LastParticipationStatus newStatus = LastParticipationStatus.NOT_ENROLLED;
                Assert.assertFalse(newStatus.equals(storedParticipation.getLastStatus()));
                Enrollment newStoredParticipation = new Enrollment(storedParticipation);
                newStoredParticipation.setLastStatus(LastParticipationStatus.CHANGED);
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
                Assert.assertEquals(LastParticipationStatus.CHANGED, updatedParticipation.getLastStatus());
                Assert.assertEquals(ParticipationStatus.ENROLLED, updatedParticipation.getStatus());
                con.commit();
            }
            
            
        } finally {
            con.invalidate();
        }
	}

	private Event createEvent(String eventId, String userId) {
		Date beginDate = PDate.getCurrentDate().getDate();
		Date now = PDate.getCurrentDate().getDate();
		String url = "http://localhost:8080/";
		String place = "";
		String address = "";
		String description = "";
		Event event = new Event(eventId, "DUMMY EVENT", "DUMMY EVENT", "DUMMY CATEGORY", null, beginDate , null, 0, url , place , address , description , "#partakein", userId, null, true, "passcode", false, false, now, now);
		event.setId(eventId);
		return event;
	}
}
