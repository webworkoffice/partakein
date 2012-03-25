package in.partake.model.dao;

import in.partake.app.PartakeApp;
import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.access.IEnvelopeAccess;
import in.partake.model.dto.Envelope;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;

import java.util.Date;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EnvelopeAccessTest extends AbstractDaoTestCaseBase<IEnvelopeAccess, Envelope, String> {
    private static final Date DEFAULT_CREATED_AT = new Date(1L);	// remove randomness from test code
    private static final String DEFAULT_MESSAGE_ID = "dummyMessageId";
    private static final DirectMessagePostingType DEFAULT_POSTING_TYPE = DirectMessagePostingType.POSTING_TWITTER_DIRECT;

    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getEnvelopeAccess());
    }

    @Override
    protected Envelope create(long pkNumber, String pkSalt, int objNumber) {
        final Envelope envelope = new Envelope();

        UUID uuid = new UUID(pkNumber, ("envelope" + pkSalt).hashCode());
        envelope.setEnvelopeId(uuid.toString());
        envelope.setMessageId(DEFAULT_MESSAGE_ID);
        envelope.setCreatedAt(new Date(objNumber));
        envelope.setPostingType(DEFAULT_POSTING_TYPE);
        return envelope;
    }

    @Test
    public void enqueueWithRequiredProperty() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                con.beginTransaction();
                String dummyEnvelopeId = dao.getFreshId(con);
                Envelope envelope = createEnvelopeByDefaultParams(dummyEnvelopeId);
                dao.put(con, envelope);
                con.commit();

                boolean found = false;
                for (DataIterator<Envelope> iter = dao.getIterator(con); iter.hasNext(); ) {
                    Envelope dequeued = iter.next();
                    if (dequeued == null) { continue; }
                    if (dequeued.getEnvelopeId().equals(dummyEnvelopeId)) {
                        Assert.assertFalse(found);
                        Assert.assertEquals(DEFAULT_MESSAGE_ID, dequeued.getMessageId());
                        Assert.assertEquals(DEFAULT_CREATED_AT, dequeued.getCreatedAt());
                        Assert.assertEquals(DEFAULT_POSTING_TYPE, dequeued.getPostingType());
                        found = true;
                    }
                }
                Assert.assertTrue(found);

                return null;
            }
        }.execute();
    }

    @Test
    public void enqueueWithLastTriedAt() throws Exception {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                con.beginTransaction();

                String dummyEnvelopeId = dao.getFreshId(con);
                Envelope envelope = createEnvelopeByDefaultParams(dummyEnvelopeId);
                // optional property
                Date lastTriedAt = new Date(2L);    // remove randomness from test code
                envelope.setLastTriedAt(lastTriedAt);
                dao.put(con, envelope);
                con.commit();

                boolean found = false;
                for (DataIterator<Envelope> iter = dao.getIterator(con); iter.hasNext(); ) {
                    Envelope dequeued = iter.next();
                    if (dequeued == null) { continue; }

                    if (dequeued.getEnvelopeId().equals(dummyEnvelopeId)) {
                        Assert.assertFalse(found);
                        Assert.assertEquals(lastTriedAt, dequeued.getLastTriedAt());
                        found = true;
                    }
                }
                Assert.assertTrue(found);

                // TODO Auto-generated method stub
                return null;
            }
        }.execute();
    }

    private Envelope createEnvelopeByDefaultParams(String envelopeId) {
        final Envelope envelope = new Envelope();

        envelope.setEnvelopeId(envelopeId);
        envelope.setMessageId(DEFAULT_MESSAGE_ID);
        envelope.setCreatedAt(DEFAULT_CREATED_AT);
        envelope.setPostingType(DEFAULT_POSTING_TYPE);
        return envelope;
    }
}
