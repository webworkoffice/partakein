package in.partake.model.dao;

import in.partake.model.dto.Message;

import java.util.Date;
import org.junit.Before;
import org.junit.rules.TestName;

public abstract class MessageDaoTestCaseBase extends AbstractDaoTestCaseBase<IMessageAccess, Message, String> {
	public TestName name = new TestName();

	@Before
	public void setup() throws DAOException {
	    super.setup(getFactory().getDirectMessageAccess());
	}
	
	@Override
	protected Message create(long pkNumber, String pkSalt, int objNumber) {
	    return new Message(pkSalt + pkNumber, "userId" + objNumber, "some message", "eventId" + objNumber, new Date(1L));
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
