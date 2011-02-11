package in.partake.model.dao;

import in.partake.model.dto.EventRelation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class EventRelationAccessTestCaseBase extends AbstractDaoTestCaseBase {
    private IEventRelationAccess dao;
    
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getEventRelationAccess());
        
        dao = getFactory().getEventRelationAccess();
    }
    
    @Test
    public void testToAddGet() throws DAOException {
        PartakeConnection con = getPool().getConnection();
        
        String eventId = "eventId-erat-sas-" + System.currentTimeMillis();
        String relatedEventId = "relatedEventId" + System.currentTimeMillis();
        
        try {
            con.beginTransaction();
            
            List<EventRelation> original = new ArrayList<EventRelation>();
            original.add(new EventRelation(eventId, relatedEventId, true, true));
            
            dao.setEventRelations(con, eventId, original);
            List<EventRelation> target = dao.getEventRelations(con, eventId);
            con.commit();
            
            // the order may differ, however the elements set should be equal.
            Assert.assertEquals(original.size(), target.size());
            for (EventRelation lhs : original) {
                boolean found = false;
                for (EventRelation rhs : target) {
                    found |= lhs.equals(rhs);
                }
                
                Assert.assertTrue(found);
            }
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToAddAddGet() throws DAOException {
        PartakeConnection con = getPool().getConnection();
        
        String eventId = "eventId-erat-sas-" + System.currentTimeMillis();
        String relatedEventId = "relatedEventId" + System.currentTimeMillis();

        List<EventRelation> original = new ArrayList<EventRelation>();
        original.add(new EventRelation(eventId, relatedEventId, true, true));

        try {
            {
                con.beginTransaction();
                dao.setEventRelations(con, eventId, original);
                con.commit();
            }
            {
                con.beginTransaction();
                dao.setEventRelations(con, eventId, original);
                con.commit();
            }
            {
                con.beginTransaction();
                List<EventRelation> target = dao.getEventRelations(con, eventId);
                con.commit();
                
                // the order may differ, however the elements set should be equal.
                Assert.assertEquals(original.size(), target.size());
                for (EventRelation lhs : original) {
                    boolean found = false;
                    for (EventRelation rhs : target) {
                        found |= lhs.equals(rhs);
                    }
                    
                    Assert.assertTrue(found);
                }

            }
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToGetInvalidData() throws DAOException {
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            List<EventRelation> relations = dao.getEventRelations(con, "invalidId");
            con.commit();
            
            Assert.assertNotNull(relations);
            Assert.assertTrue(relations.isEmpty());
        } finally {
            con.invalidate();
        }
    }
}
