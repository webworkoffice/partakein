package in.partake.model.dao;

import in.partake.model.dto.DirectMessage;
import in.partake.model.dto.DirectMessageEnvelope;
import in.partake.model.dto.aux.DirectMessagePostingType;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TestName;

public abstract class DirectMessageDaoTestCaseBase extends AbstractDaoTestCaseBase {
	private static final String TWITTER_ID = "eller86";
	private static final String MESSAGE = "message";
	private static final String EVENT_OWNER_ID = "owner_id";

	public TestName name = new TestName();
	private IDirectMessageAccess dao;

	@Before
	public void setup() throws DAOException {
	    super.setup(getFactory().getDirectMessageAccess());
		dao = getFactory().getDirectMessageAccess();
	}

	@Test
	public void testSendAndGet() throws DAOException, InterruptedException {
		Date deadline = new Date();
		String messageId;

		{
			DirectMessage embryo = new DirectMessage(EVENT_OWNER_ID, MESSAGE);
			PartakeConnection sendCon = getPool().getConnection();
			
			try {
			    sendCon.beginTransaction();
			    messageId = dao.getFreshId(sendCon);
				dao.addMessage(sendCon, messageId, embryo);
				dao.sendEnvelope(sendCon, messageId, TWITTER_ID, TWITTER_ID, deadline, DirectMessagePostingType.POSTING_TWITTER_DIRECT);
				sendCon.commit();
			} finally {
				sendCon.invalidate();
			}
		}

		{
		    PartakeConnection getCon = getPool().getConnection();
			boolean found = false;
			try {
			    getCon.beginTransaction();
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
				
				getCon.commit();
			} finally {
				getCon.invalidate();
			}
		}
	}
}
