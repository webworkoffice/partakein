package in.partake.model.dao;

import in.partake.model.dto.EventRelation;
import in.partake.model.dto.pk.EventRelationPK;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class EventRelationAccessTestCaseBase extends AbstractDaoTestCaseBase<IEventRelationAccess, EventRelation, EventRelationPK> {
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getEventRelationAccess());
    }
    
    @Override
    protected EventRelation create(long pkNumber, String pkSalt, int objNumber) {
        return new EventRelation(pkSalt + pkNumber, pkSalt + pkNumber, (objNumber & 2) == 1, (objNumber & 1) == 1);
    }
    
    @Test
    public void testToAddGet() throws DAOException {
        PartakeConnection con = getPool().getConnection();
        
        String eventId = "eventId-erat-sas-" + System.currentTimeMillis();
        String relatedEventId = "relatedEventId" + System.currentTimeMillis();
        
        try {
            con.beginTransaction();            
            dao.put(con, new EventRelation(eventId, relatedEventId, true, true));
            con.commit();
            
            con.beginTransaction();
            List<EventRelation> target = dao.findByEventId(con, eventId);
            con.commit();
            
            // the order may differ, however the elements set should be equal.
            Assert.assertEquals(1, target.size());
            Assert.assertEquals(eventId, target.get(0).getSrcEventId());
            Assert.assertEquals(relatedEventId, target.get(0).getDstEventId());
            Assert.assertEquals(true, target.get(0).isRequired());
            Assert.assertEquals(true, target.get(0).hasPriority());            
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToAddAddGet() throws DAOException {
        PartakeConnection con = getPool().getConnection();
        
        String eventId = "eventId-erat-sas-" + System.currentTimeMillis();
        String relatedEventId = "relatedEventId" + System.currentTimeMillis();

        EventRelation original = new EventRelation(eventId, relatedEventId, true, true);

        try {
            {
                con.beginTransaction();
                dao.put(con, original);
                con.commit();
            }
            {
                con.beginTransaction();
                dao.put(con, original);
                con.commit();
            }
            {
                con.beginTransaction();
                List<EventRelation> target = dao.findByEventId(con, eventId);
                con.commit();
                
                // the order may differ, however the elements set should be equal.
                Assert.assertEquals(1, target.size());
                Assert.assertEquals(original, target.get(0));
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
            List<EventRelation> relations = dao.findByEventId(con, "invalidId");
            con.commit();
            
            Assert.assertNotNull(relations);
            Assert.assertTrue(relations.isEmpty());
        } finally {
            con.invalidate();
        }
    }
}
