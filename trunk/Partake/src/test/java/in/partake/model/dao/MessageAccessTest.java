package in.partake.model.dao;

import in.partake.model.dao.access.IMessageAccess;
import in.partake.model.dto.Message;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TestName;

public class MessageAccessTest extends AbstractDaoTestCaseBase<IMessageAccess, Message, String> {
	public TestName name = new TestName();

	@Before
	public void setup() throws DAOException {
	    super.setup(getFactory().getDirectMessageAccess());
	}
	
	@Override
	protected Message create(long pkNumber, String pkSalt, int objNumber) {
	    return new Message(pkSalt + pkNumber, "userId" + objNumber, "some message", "eventId" + objNumber, new Date(1L));
	}
	
	@Test
	public void testIterateFromMostRecentToLeastRecent() throws DAOException {
		IMessageAccess access = getFactory().getDirectMessageAccess();
		PartakeConnection con = getPool().getConnection();
		String eventId = "eventId" + System.currentTimeMillis();
		try {
			con.beginTransaction();
			access.put(con, new Message(access.getFreshId(con), "userId", "message", eventId, new Date(100000L)));
			access.put(con, new Message(access.getFreshId(con), "userId", "message", eventId, new Date(0L)));
			access.put(con, new Message(access.getFreshId(con), "userId", "message", eventId, new Date(200000L)));
			con.commit();

			Date date = null;
			int count = 0;
			con.beginTransaction();
			for (DataIterator<Message> iter = access.findByEventId(con, eventId); iter.hasNext();) {
				Message m = iter.next();
				if (date != null) {
					Assert.assertTrue(date.after(m.getCreatedAt()));
				}
				date = m.getCreatedAt();
				++count;
			}
			Assert.assertEquals(3, count);
		} finally {
			con.invalidate();
		}
	}

//	@Test
//	public void testToAddIter() throws DAOException, InterruptedException {
//		PartakeConnection con = getPool().getConnection();
//		
//		Date deadline = new Date();
//
//		Message original;
//        {
//            con.beginTransaction();
//            String userId  = "uesrId-" + System.currentTimeMillis();
//            String eventId = "eventId-" + System.currentTimeMillis();
//            
//            original = new Message(dao.getFreshId(con), userId, "some message", eventId, new Date());
//            
//            dao.addMessage(con, original);
//            dao.sendEnvelope(con, original.getId(), userId, userId, deadline, DirectMessagePostingType.POSTING_TWITTER);
//            
//            con.commit();
//        }
//        
//
//		{
//			boolean found = false;
//			
//		    con.beginTransaction();
//			DataIterator<Envelope> iter = dao.getEnvelopeIterator(con);
//
//			while (iter.hasNext()) {
//				Envelope envelope = iter.next();
//				Assert.assertNotNull(envelope);
//				Assert.assertNotNull(envelope.getEnvelopeId());
//				Assert.assertNotNull(envelope.getDeadline());
//				if (envelope.getMessageId().equals(original.getId())) {
//					Assert.assertEquals(deadline, envelope.getDeadline());
//					found = true;
//				}
//			}
//
//			Assert.assertTrue(found);
//			
//			con.commit();
//		}
//	}
}
