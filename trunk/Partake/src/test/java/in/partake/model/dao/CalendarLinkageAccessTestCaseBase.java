package in.partake.model.dao;

import junit.framework.Assert;
import in.partake.model.dto.CalendarLinkage;

import org.junit.Before;
import org.junit.Test;

public abstract class CalendarLinkageAccessTestCaseBase extends AbstractDaoTestCaseBase {
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getCalendarAccess());
    }
    
    @Test
    public void testToAddGet() throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            con.beginTransaction();
            
            // --- add
            CalendarLinkage original;
            {
                original = new CalendarLinkage("id", "userId");
                factory.getCalendarAccess().put(con, original);
                Assert.assertFalse(original.isFrozen());
            }
            
            // --- get
            CalendarLinkage target;
            {
                target = factory.getCalendarAccess().find(con, "id");
                Assert.assertTrue(target.isFrozen());
            }

            con.commit();

            Assert.assertNotNull(target);
            Assert.assertTrue(target.isFrozen());
            Assert.assertFalse(original.isFrozen());
            Assert.assertEquals(original, target);
            Assert.assertNotSame(original, target);

        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToAddDeleteGet() throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            {
                con.beginTransaction();
                CalendarLinkage original = new CalendarLinkage("id", "userId");
                factory.getCalendarAccess().put(con, original);
                Assert.assertFalse(original.isFrozen());
                con.commit();
            }
            
            {
                con.beginTransaction();
                factory.getCalendarAccess().remove(con, "id");
                con.commit();
            }
            
            {
                con.beginTransaction();
                CalendarLinkage target = factory.getCalendarAccess().find(con, "id");
                Assert.assertNull(target);
                con.commit();
            }
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToAddRemoveAddGet() throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = getPool().getConnection();
        
        try {
            CalendarLinkage original = new CalendarLinkage("id", "userId");
            
            {
                con.beginTransaction();
                factory.getCalendarAccess().put(con, original);
                Assert.assertFalse(original.isFrozen());
                con.commit();
            }
            
            {
                con.beginTransaction();
                factory.getCalendarAccess().remove(con, "id");
                con.commit();
            }

            {
                con.beginTransaction();
                factory.getCalendarAccess().put(con, original);
                Assert.assertFalse(original.isFrozen());
                con.commit();
            }

            {
                con.beginTransaction();
                CalendarLinkage target = factory.getCalendarAccess().find(con, "id");
                Assert.assertNotNull(target);
                Assert.assertEquals(original, target);
                con.commit();
            }
        } finally {
            con.invalidate();
        }
    }
}
