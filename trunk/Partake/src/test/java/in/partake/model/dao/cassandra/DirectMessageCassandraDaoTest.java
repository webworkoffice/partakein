package in.partake.model.dao.cassandra;

import java.util.Date;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dto.DirectMessage;
import in.partake.model.dto.DirectMessageEnvelope;
import in.partake.model.dto.DirectMessagePostingType;
import in.partake.resource.PartakeProperties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * @see DirectMessageCassandraDao
 * @author skypencil(@eller86)
 */
public class DirectMessageCassandraDaoTest extends CassandraDaoTestBase {
	private static final String TWITTER_ID = "eller86";
	private static final String MESSAGE = "message";
	private static final String EVENT_OWNER_ID = "owner_id";

	public TestName name = new TestName();
	private CassandraConnectionPool pool;
	private DirectMessageCassandraDao dao;
	private String messageId;

	@Before
	public void setup() throws DAOException {
		if (!PartakeProperties.get().getDAOFactoryClassName().endsWith("CassandraDAOFactory")) {
			Assert.fail("This test doesn't run because properties file isn't ready.");
		}

		CassandraDAOFactory factory = new CassandraDAOFactory();
		pool = new CassandraConnectionPool();
		dao = new DirectMessageCassandraDao(factory);

		CassandraConnection con = (CassandraConnection) pool.getConnection();
		try {
			messageId = dao.getFreshId(con);
		} finally {
			con.invalidate();
		}
	}

	@Test
	public void testSendAndGet() throws DAOException, InterruptedException {
		Date deadline = new Date();

		{
			DirectMessage embryo = new DirectMessage(EVENT_OWNER_ID, MESSAGE);
			CassandraConnection sendCon = (CassandraConnection) pool.getConnection();
			try {
				dao.addMessage(sendCon, messageId, embryo);
				dao.sendEnvelope(sendCon, messageId, TWITTER_ID, TWITTER_ID, deadline, DirectMessagePostingType.POSTING_TWITTER_DIRECT);
			} finally {
				sendCon.invalidate();
			}
		}

		{
		    CassandraConnection getCon = (CassandraConnection) pool.getConnection();
			boolean found = false;
			try {
				DataIterator<DirectMessageEnvelope> iter = dao.getEnvelopeIterator(getCon);

				while (iter.hasNext()) {
					DirectMessageEnvelope envelope = iter.next();
					Assert.assertNotNull(envelope);
					Assert.assertNotNull(envelope.getEnvelopeId());
					Assert.assertNotNull(envelope.getDeadline());
					if (envelope.getMessageId().equals(messageId)) {
						Assert.assertEquals(deadline, envelope.getDeadline());
						found = true;
					}
				}

				Assert.assertTrue(found);
			} finally {
				getCon.invalidate();
			}
		}
	}
}