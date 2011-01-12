package in.partake.model.dao.cassandra;

import junit.framework.Assert;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dto.EventNotificationStatus;
import in.partake.resource.PartakeProperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * @see MessageCassandraDao
 * @author skypencil(@eller86)
 */
//FIXME in.partake.mode=test で実行できない。というかMockのテストをやりたいわけではないしなぁ。
public class EventNotificationStatusTestWithMessageCassandraDao {
	public TestName name = new TestName();
	private MessageCassandraDao dao;
	private CassandraConnectionPool pool;
	private String eventId;

	@Before
	public void setup() throws DAOException {
		if (!PartakeProperties.get().getDAOFactoryClassName().endsWith("CassandraDAOFactory")) {
			Assert.fail("This test doesn't run because properties file isn't ready.");
		}

		CassandraDAOFactory factory = new CassandraDAOFactory();
		pool = new CassandraConnectionPool();
		dao = new MessageCassandraDao(factory);
		eventId = name.getMethodName() + System.currentTimeMillis();
	}

	@Test(expected = DAOException.class)
	public void getNullEventIdStatus() throws DAOException {
		createNotification(null);
	}

	@Test
	public void getNotExistStatus() throws DAOException {
		CassandraConnection con = pool.getConnection();
		try {
			EventNotificationStatus status = dao.getNotificationStatus(con, eventId);
	
			Assert.assertNotNull(status);
			Assert.assertEquals(status.getEventId(), eventId);
			Assert.assertTrue(status.isBeforeDeadlineOneday());
			Assert.assertTrue(status.isBeforeDeadlineHalfday());
			Assert.assertTrue(status.isBeforeTheDay());
		} finally {
			con.invalidate();
		}
	}

	@Test
	public void createNewStatus() throws DAOException {
		createNotification(eventId);

		CassandraConnection con = pool.getConnection();
		try {
			EventNotificationStatus status = dao.getNotificationStatus(con, eventId);
			Assert.assertNotNull(status);
			Assert.assertEquals(eventId, status.getEventId());
			Assert.assertFalse(status.isBeforeDeadlineOneday());
			Assert.assertFalse(status.isBeforeDeadlineHalfday());
			Assert.assertFalse(status.isBeforeTheDay());
		} finally {
			con.invalidate();
		}
	}

	@Test
	public void updateStatus() throws DAOException {
		createNotification(eventId);

		CassandraConnection conUpdate = pool.getConnection();
		boolean found = false;
		try {
			DataIterator<EventNotificationStatus> iterator = dao.getNotificationStatuses(conUpdate);
			while (iterator.hasNext()) {
				EventNotificationStatus status = iterator.next();
				Assert.assertNotNull(status);
	
				if (eventId.equals(status.getEventId())) {
					Assert.assertFalse(found);
					found = true;

					Assert.assertFalse(status.isBeforeDeadlineOneday());
					Assert.assertFalse(status.isBeforeDeadlineHalfday());
					Assert.assertFalse(status.isBeforeTheDay());
	
					status.setBeforeDeadlineOneday(true);
					status.setBeforeTheDay(true);
					iterator.update(status);
				}
			}
			Assert.assertTrue(found);
		} finally {
			conUpdate.invalidate();
		}

		CassandraConnection conGet = pool.getConnection();
		try {
			EventNotificationStatus updatedStatus = dao.getNotificationStatus(conGet, eventId);
			Assert.assertNotNull(updatedStatus);
			Assert.assertEquals(eventId, updatedStatus.getEventId());
			Assert.assertTrue(updatedStatus.isBeforeDeadlineOneday());
			Assert.assertFalse(updatedStatus.isBeforeDeadlineHalfday());
			Assert.assertTrue(updatedStatus.isBeforeTheDay());
		} finally {
			conGet.invalidate();
		}
	}

	@Test
	public void updateStatusTwice() throws DAOException {
		createNotification(eventId);

		{
			CassandraConnection conUpdate1 = pool.getConnection();
			boolean found = false;
			try {
				for (DataIterator<EventNotificationStatus> iterator = dao.getNotificationStatuses(conUpdate1); iterator.hasNext();) {
					EventNotificationStatus status = iterator.next();
					Assert.assertNotNull(status);

					if (eventId.equals(status.getEventId())) {
						Assert.assertFalse(found);
						found = true;

						Assert.assertFalse(status.isBeforeDeadlineOneday());
						Assert.assertFalse(status.isBeforeDeadlineHalfday());
						Assert.assertFalse(status.isBeforeTheDay());

						status.setBeforeDeadlineOneday(true);
						status.setBeforeTheDay(true);
						iterator.update(status);
					}
				}
				Assert.assertTrue(found);
			} finally {
				conUpdate1.invalidate();
			}
		}

		{
			CassandraConnection conUpdate2 = pool.getConnection();
			boolean found = false;
			try {
				for (DataIterator<EventNotificationStatus> iterator = dao.getNotificationStatuses(conUpdate2); iterator.hasNext();) {
					EventNotificationStatus status = iterator.next();
					Assert.assertNotNull(status);
		
					if (eventId.equals(status.getEventId())) {
						Assert.assertFalse(found);
						found = true;

						Assert.assertTrue(status.isBeforeDeadlineOneday());
						Assert.assertFalse(status.isBeforeDeadlineHalfday());
						Assert.assertTrue(status.isBeforeTheDay());
		
						status.setBeforeDeadlineOneday(false);
						status.setBeforeDeadlineHalfday(true);
						iterator.update(status);
					}
				}
				Assert.assertTrue(found);
			} finally {
				conUpdate2.invalidate();
			}
		}

		{
			CassandraConnection conGet = pool.getConnection();
			try {
				EventNotificationStatus updatedStatus = dao.getNotificationStatus(conGet, eventId);
				Assert.assertNotNull(updatedStatus);
				Assert.assertEquals(eventId, updatedStatus.getEventId());
				Assert.assertFalse(updatedStatus.isBeforeDeadlineOneday());	// TODO たまにここで失敗する→整合性が取れないため?=仕様？
				Assert.assertTrue(updatedStatus.isBeforeDeadlineHalfday());
				Assert.assertTrue(updatedStatus.isBeforeTheDay());
			} finally {
				conGet.invalidate();
			}
		}
	}

	@Test
	public void removeStatus() throws DAOException {
		createNotification(eventId);

		{
			CassandraConnection conRemove = pool.getConnection();
			try {
				boolean found = false;
				for (DataIterator<EventNotificationStatus> iterator = dao.getNotificationStatuses(conRemove); iterator.hasNext();) {
					EventNotificationStatus status = iterator.next();
					Assert.assertNotNull(status);
		
					if (eventId.equals(status.getEventId())) {
						Assert.assertFalse(found);
						found = true;

						status.setBeforeDeadlineOneday(true);
						status.setBeforeTheDay(true);
						iterator.update(status);	
						iterator.remove();
					}
				}
				Assert.assertTrue(found);
			} finally {
				conRemove.invalidate();
			}
		}

		{
			CassandraConnection conGet = pool.getConnection();
			try {
				for (DataIterator<EventNotificationStatus> iterator = dao.getNotificationStatuses(conGet); iterator.hasNext();) {
					EventNotificationStatus status = iterator.next();
					Assert.assertNotNull(status);
		
					if (eventId.equals(status.getEventId())) {
						Assert.fail();
					}
				}
		
				EventNotificationStatus removedStatus = dao.getNotificationStatus(conGet, eventId);
				Assert.assertNotNull(removedStatus);
				Assert.assertEquals(eventId, removedStatus.getEventId());
				Assert.assertTrue(removedStatus.isBeforeDeadlineHalfday());
				Assert.assertTrue(removedStatus.isBeforeDeadlineOneday());
				Assert.assertTrue(removedStatus.isBeforeTheDay());
			} finally {
				conGet.invalidate();
			}
		}
	}

	private void createNotification(String eventId) throws DAOException {
		CassandraConnection con = pool.getConnection();
		try {
			dao.addNotification(con, eventId);
		} finally {
			con.invalidate();
		}
	}
}