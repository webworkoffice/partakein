package in.partake.model.dao;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.access.IMessageAccess;
import in.partake.model.dto.Message;

import java.util.Date;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TestName;

public class MessageAccessTest extends AbstractDaoTestCaseBase<IMessageAccess, Message, String> {
    public TestName name = new TestName();

    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getDirectMessageAccess());
    }

    @Override
    protected Message create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, ("message" + pkSalt).hashCode());
        return new Message(uuid.toString(), "userId" + objNumber, "some message", "eventId" + objNumber, new Date(1L));
    }

    @Test
    public void testIterateFromMostRecentToLeastRecent() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                con.beginTransaction();
                String eventId = "eventId" + System.currentTimeMillis();
                dao.put(con, new Message(dao.getFreshId(con), "userId", "message", eventId, new Date(100000L)));
                dao.put(con, new Message(dao.getFreshId(con), "userId", "message", eventId, new Date(0L)));
                dao.put(con, new Message(dao.getFreshId(con), "userId", "message", eventId, new Date(200000L)));
                con.commit();

                Date date = null;
                int count = 0;
                con.beginTransaction();
                DataIterator<Message> iter = dao.findByEventId(con, eventId);
                try {
                    while (iter.hasNext()) {
                        Message m = iter.next();
                        if (m == null)
                            continue;
                        if (date != null) {
                            Assert.assertTrue(date.after(m.getCreatedAt()));
                        }
                        date = m.getCreatedAt();
                        ++count;
                    }
                } finally {
                    iter.close();
                }
                Assert.assertEquals(3, count);

                return null;
            }
        }.execute();
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
