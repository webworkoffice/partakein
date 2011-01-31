package in.partake.model.dao;

import in.partake.model.dto.Event;
import in.partake.model.dto.Participation;
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
	private String eventId;
	private String userId;

	@Before
	public void setup() throws DAOException {
	    super.setup(getFactory().getEnrollmentAccess());
	    
		dao = getFactory().getEnrollmentAccess();
		eventId = name.getMethodName() + System.currentTimeMillis();
		userId = name.getMethodName() + System.currentTimeMillis();
	}

	@Test
	public void testGetEmptyPaticipationList() throws DAOException {
		PartakeConnection con = getPool().getConnection();

		try {
			List<Participation> list = dao.getParticipation(con, eventId);
			Assert.assertTrue(list.isEmpty());
		} finally {
			con.invalidate();
		}
	}

	@Test
	public void testPutAndGetPaticipationList() throws DAOException {
		ParticipationStatus status = ParticipationStatus.ENROLLED;
		User user = createDummyUser();
		Event event = createEvent();

		PartakeConnection con = getPool().getConnection();
		try {
		    event.setId(eventId);
			getFactory().getEventAccess().addEvent(con, event);
			getFactory().getUserAccess().addUser(con, userId, 0);

			dao.enroll(con, user, event, status, "", false, false);
			List<Participation> list = dao.getParticipation(con, eventId);
			Assert.assertEquals(1, list.size());

			Participation storedParticipation = list.get(0);
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
		testPutAndGetPaticipationList();

		PartakeConnection con = getPool().getConnection();
		try {
			List<Participation> storedList = dao.getParticipation(con, eventId);
			Assert.assertEquals(1, storedList.size());

			Participation storedParticipation = storedList.get(0);
			ParticipationStatus status = storedParticipation.getStatus();
			Assert.assertNotNull(storedParticipation);
			LastParticipationStatus newStatus = LastParticipationStatus.NOT_ENROLLED;
			Assert.assertFalse(newStatus.equals(storedParticipation.getLastStatus()));
			dao.setLastStatus(con, eventId, storedParticipation, newStatus);

			List<Participation> updatedList = dao.getParticipation(con, eventId);
			Assert.assertEquals(1, updatedList.size());
			Participation updatedParticipation = updatedList.get(0);
			Assert.assertEquals(userId, updatedParticipation.getUserId());
			Assert.assertEquals(newStatus, updatedParticipation.getLastStatus());
			Assert.assertEquals(status, updatedParticipation.getStatus());
		} finally {
			con.invalidate();
		}
	}

	private Event createEvent() {
		Date beginDate = PDate.getCurrentDate().getDate();
		Date now = PDate.getCurrentDate().getDate();
		String url = "http://localhost:8080/";
		String place = "";
		String address = "";
		String description = "";
		Event event = new Event(eventId, "DUMMY EVENT", "DUMMY EVENT", "DUMMY CATEGORY", null, beginDate , null, 0, url , place , address , description , "#partakein", userId, null, true, "passcode", false, now, now);
		event.setId(eventId);
		return event;
	}

	private User createDummyUser() {
		Date lastLoginAt = PDate.getCurrentDate().getDate();
		int twitterId = 0;
		String calendarId = "";
		User user = new User(userId, lastLoginAt, twitterId, calendarId);
		return user;
	}
}
