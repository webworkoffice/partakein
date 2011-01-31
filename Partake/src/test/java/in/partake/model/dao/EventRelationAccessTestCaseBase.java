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
    public void testToSetAndGet() throws DAOException {
        PartakeConnection con = getPool().getConnection();
        try {
            con.beginTransaction();
            
            List<EventRelation> original = new ArrayList<EventRelation>();
            dao.setEventRelations(con, "eventId", original);
            List<EventRelation> target = dao.getEventRelations(con, "invalidId");
            con.commit();
            
            Assert.assertEquals(original, target);
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
