package in.partake.model.dao;

import in.partake.model.dto.FeedLinkage;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public abstract class FeedAccessTestCaseBase extends AbstractDaoTestCaseBase {
    private IFeedAccess dao;
    
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getFeedAccess());
        this.dao = getFactory().getFeedAccess(); 
    }
    
    @Test
    public void testToAddGet() throws Exception {
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            String feedId = dao.getFreshId(con);
            String eventId = "eventId-feedaccess-" + System.currentTimeMillis();
            dao.put(con, new FeedLinkage(feedId, eventId));
            con.commit();
            
            {
                con.beginTransaction();
                String taken = dao.findByEventId(con, eventId);
                con.commit();
                Assert.assertEquals(feedId, taken);
            }
            
            {
                con.beginTransaction();
                FeedLinkage taken =  dao.find(con, feedId); 
                con.commit();
                Assert.assertEquals(eventId, taken.getEventId());
            }
            
        } finally {
            con.invalidate();
        }
    }
}
