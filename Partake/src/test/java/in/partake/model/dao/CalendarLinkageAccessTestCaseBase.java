package in.partake.model.dao;

import junit.framework.Assert;
import in.partake.model.dto.CalendarLinkage;

import org.junit.Before;
import org.junit.Test;

public abstract class CalendarLinkageAccessTestCaseBase extends AbstractDaoTestCaseBase {
    @Before
    public void setup() throws DAOException {
        // --- remove all data before starting test.
        PartakeConnection con = getPool().getConnection();
        PartakeDAOFactory factory = getFactory();
        
        try {
            con.beginTransaction();
            factory.getCalendarAccess().truncate(con);
            con.commit();
        } finally {            
            con.invalidate();
        }
    }
    
    @Test
    public void testToAlwaysSucceed() {
        // do nothing.
    }
    
    @Test
    public void testToAddAndGet() throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();
            
            // --- add
            CalendarLinkage original;
            {
                original = new CalendarLinkage("id", "userId");
                factory.getCalendarAccess().addCalendarLinkageWithId(con, original);
                Assert.assertFalse(original.isFrozen());
            }
            
            // --- get
            CalendarLinkage target;
            {
                target = factory.getCalendarAccess().getCalendarLinkageById(con, "id");
                Assert.assertTrue(target.isFrozen());
            }

            Assert.assertEquals(original, target);            
            con.commit();
        } finally {
            con.invalidate();
        }
    }
}
