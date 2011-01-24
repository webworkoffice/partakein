package in.partake.model.dao.cassandra;

import in.partake.model.dao.DAOException;
import in.partake.model.dto.Event;
import in.partake.model.dto.LastParticipationStatus;
import in.partake.model.dto.Participation;
import in.partake.model.dto.ParticipationStatus;
import in.partake.model.dto.User;
import in.partake.resource.PartakeProperties;
import in.partake.util.PDate;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class EnrollmentCassandraDaoTest {
	@Rule
	public TestName name = new TestName();

	private CassandraConnectionPool pool;
	private EnrollmentCassandraDao dao;
	private String eventId;
	private String userId;

	@Before
	public void setup() throws DAOException {
		if (!PartakeProperties.get().getDAOFactoryClassName().endsWith("CassandraDAOFactory")) {
			Assert.fail("This test doesn't run because properties file isn't ready.");
		}

		pool = new CassandraConnectionPool();
		dao = (EnrollmentCassandraDao) new CassandraDAOFactory().getEnrollmentAccess();
		eventId = name.getMethodName() + System.currentTimeMillis();
		userId = name.getMethodName() + System.currentTimeMillis();
	}

	@Test
	public void testGetEmptyPaticipationList() throws DAOException {
		CassandraConnection con = pool.getConnection();

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

		CassandraConnection con = pool.getConnection();
		try {
			new CassandraDAOFactory().getEventAccess().addEvent(con, eventId, event);
			new CassandraDAOFactory().getUserAccess().addUser(con, userId, 0);

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

	Event createEvent() {
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
