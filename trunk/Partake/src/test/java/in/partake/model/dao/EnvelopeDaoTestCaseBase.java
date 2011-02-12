package in.partake.model.dao;

import java.util.Date;

import junit.framework.Assert;

import in.partake.model.dto.Envelope;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class EnvelopeDaoTestCaseBase extends AbstractDaoTestCaseBase {
    private static final Date DEFAULT_CREATED_AT = new Date(1L);	// remove randomness from test code
    private static final String DEFAULT_MESSAGE_ID = "dummyMessageId";
    private static final DirectMessagePostingType DEFAULT_POSTING_TYPE = DirectMessagePostingType.POSTING_TWITTER_DIRECT;
    private IEnvelopeAccess dao;
    private PartakeConnection con;

    @Before
    public void setup() throws Exception {
        super.setup(getFactory().getEnvelopeAccess());
        dao = getFactory().getEnvelopeAccess();
        con = getPool().getConnection();
    }

    @After
    public void teardown() {
        con.invalidate();
    }

    @Test
    public void enqueueWithRequiredProperty() throws DAOException {
        String dummyEnvelopeId = dao.getFreshId(con);
        Envelope envelope = createEnvelopeByDefaultParams(dummyEnvelopeId);
        dao.enqueueEnvelope(con, envelope);

        boolean found = false;
        for (DataIterator<Envelope> iter = dao.getEnvelopeIterator(con); iter.hasNext(); ) {
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
    }

    @Test
    public void enqueueWithLastTriedAt() throws DAOException {
        String dummyEnvelopeId = dao.getFreshId(con);
        Envelope envelope = createEnvelopeByDefaultParams(dummyEnvelopeId);

        // optional property
        Date lastTriedAt = new Date(2L);	// remove randomness from test code
        envelope.setLastTriedAt(lastTriedAt);

        dao.enqueueEnvelope(con, envelope);

        boolean found = false;
        for (DataIterator<Envelope> iter = dao.getEnvelopeIterator(con); iter.hasNext(); ) {
            Envelope dequeued = iter.next();
            if (dequeued.getEnvelopeId().equals(dummyEnvelopeId)) {
                Assert.assertFalse(found);
                Assert.assertEquals(lastTriedAt, dequeued.getLastTriedAt());
                found = true;
            }
        }
        Assert.assertTrue(found);
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
