package in.partake.model.dao;

import java.util.Date;

import in.partake.model.dto.Envelope;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class EnvelopeDaoTestCaseBase extends AbstractDaoTestCaseBase<IEnvelopeAccess, Envelope, String> {
    private static final Date DEFAULT_CREATED_AT = new Date(1L);	// remove randomness from test code
    private static final String DEFAULT_MESSAGE_ID = "dummyMessageId";
    private static final DirectMessagePostingType DEFAULT_POSTING_TYPE = DirectMessagePostingType.POSTING_TWITTER_DIRECT;

    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getEnvelopeAccess());
    }
    
    @Override
    protected Envelope create(long pkNumber, String pkSalt, int objNumber) {
        final Envelope envelope = new Envelope();

        envelope.setEnvelopeId(pkSalt + pkNumber);
        envelope.setMessageId(DEFAULT_MESSAGE_ID);
        envelope.setCreatedAt(new Date(objNumber));
        envelope.setPostingType(DEFAULT_POSTING_TYPE);
        return envelope;
    }

    @Test
    public void enqueueWithRequiredProperty() throws DAOException {
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            String dummyEnvelopeId = dao.getFreshId(con);
            Envelope envelope = createEnvelopeByDefaultParams(dummyEnvelopeId);
            dao.put(con, envelope);
    
            boolean found = false;
            for (DataIterator<Envelope> iter = dao.getIterator(con); iter.hasNext(); ) {
                Envelope dequeued = iter.next();
                if (dequeued.getEnvelopeId().equals(dummyEnvelopeId)) {
                    Assert.assertFalse(found);
                    Assert.assertEquals(DEFAULT_MESSAGE_ID, dequeued.getMessageId());
                    Assert.assertEquals(DEFAULT_CREATED_AT, dequeued.getCreatedAt());
                    Assert.assertEquals(DEFAULT_POSTING_TYPE, dequeued.getPostingType());
                    found = true;
                }
            }            
            Assert.assertTrue(found);
            
            con.commit();
        } finally {
            con.invalidate();
        }
    }

    @Test
    public void enqueueWithLastTriedAt() throws DAOException {
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            
            String dummyEnvelopeId = dao.getFreshId(con);
            Envelope envelope = createEnvelopeByDefaultParams(dummyEnvelopeId);
    
            // optional property
            Date lastTriedAt = new Date(2L);	// remove randomness from test code
            envelope.setLastTriedAt(lastTriedAt);
            dao.put(con, envelope);
    
            boolean found = false;
            for (DataIterator<Envelope> iter = dao.getIterator(con); iter.hasNext(); ) {
                Envelope dequeued = iter.next();
                if (dequeued.getEnvelopeId().equals(dummyEnvelopeId)) {
                    Assert.assertFalse(found);
                    Assert.assertEquals(lastTriedAt, dequeued.getLastTriedAt());
                    found = true;
                }
            }
            Assert.assertTrue(found);
            
            con.commit();
        } finally {
            con.invalidate();
        }

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
