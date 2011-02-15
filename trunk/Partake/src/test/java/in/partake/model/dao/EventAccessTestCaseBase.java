package in.partake.model.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.partake.model.dao.AbstractDaoTestCaseBase;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.IEventAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;
import in.partake.util.PDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class EventAccessTestCaseBase extends AbstractDaoTestCaseBase<IEventAccess, Event, String> {
	@Before
	public void setup() throws DAOException {
	    super.setup(getFactory().getEventAccess());
	}

	@Override
	protected Event create(long pkNumber, String pkSalt, int objNumber) {
        Date beginDate = PDate.getCurrentDate().getDate();
        Date now = PDate.getCurrentDate().getDate();
        String url = "http://localhost:8080/";
        String place = "";
        String address = "";
        String description = "";
        Event event = new Event("eventId" + pkSalt + pkNumber, "DUMMY EVENT", "DUMMY EVENT", "DUMMY CATEGORY", null, beginDate , null, 0, url , place , address , description , "#partakein", "userId", null, true, "passcode", false, false, now, now);
        event.setId("eventId" + pkSalt + pkNumber);
        return event;	    
	}
	
	@Test
	public void loadAllEvents() throws DAOException {
		PartakeConnection con = getPool().getConnection();

		try {
		    for (DataIterator<Event> it = dao.getIterator(con); it.hasNext(); ) {
		        it.next();
		    }
		} finally {
			con.invalidate();
		}
	}
	
	@Test
	public void testToGetFreshId() throws DAOException {
	    PartakeConnection con = getPool().getConnection();

        try {
            Set<String> visited = new HashSet<String>(); 
            con.beginTransaction();
            for (int i = 0; i < 10; ++i) {
                String id = dao.getFreshId(con);
                Assert.assertFalse(visited.contains(id));
                visited.add(id);
            }
            con.commit();
        } finally {
            con.invalidate();
        }
	}
	
	@Test
	public void testToAddGet() throws DAOException {
	    PartakeConnection con = getPool().getConnection();

        try {
            String userId = "userId" + System.currentTimeMillis();
            String eventId;
            
            Event original = createEvent(null, userId);
            {
                con.beginTransaction();
                eventId = dao.getFreshId(con);
                original.setId(eventId);
                dao.put(con, original);
                con.commit();
            }
            
            Event target;
            {
                con.beginTransaction();
                target = dao.find(con, eventId);
                con.commit();
            }

            Assert.assertNotNull(target);
            Assert.assertTrue(target.isFrozen());
            Assert.assertFalse(original.isFrozen());
            Assert.assertEquals(original, target);
        } finally {
            con.invalidate();
        }
	}
	
    @Test
    public void testToAddRemoveGet() throws DAOException {
        PartakeConnection con = getPool().getConnection();

        try {
            String userId = "userId" + System.currentTimeMillis();
            String eventId;
            
            Event original = createEvent(null, userId);
            {
                con.beginTransaction();
                eventId = dao.getFreshId(con);
                original.setId(eventId);
                dao.put(con, original);
                con.commit();
            }
            
            PDate.waitForTick();
            
            Assert.assertFalse(dao.isRemoved(con, eventId));
            {
                con.beginTransaction();
                dao.remove(con, eventId);
                con.commit();
            }
            
            PDate.waitForTick();
            
            Event target;
            {
                con.beginTransaction();
                target = dao.find(con, eventId);
                con.commit();
            }

            Assert.assertNull(target);
            Assert.assertTrue(dao.isRemoved(con, eventId));
        } finally {
            con.invalidate();
        }        
    }

    @Test
    public void testToAddRemoveAddGet() throws DAOException {
        PartakeConnection con = getPool().getConnection();

        try {
            String userId = "userId" + System.currentTimeMillis();
            String eventId;
            
            Event original = createEvent(null, userId);
            {
                con.beginTransaction();
                eventId = dao.getFreshId(con);
                original.setId(eventId);
                dao.put(con, original);
                con.commit();
            }
            
            {
                con.beginTransaction();
                dao.remove(con, eventId);
                con.commit();
            }
            
            {
                con.beginTransaction();
                dao.put(con, original);
                con.commit();
            }
            
            Event target;
            {
                con.beginTransaction();
                target = dao.find(con, eventId);
                con.commit();
            }

            Assert.assertNotNull(target);
            Assert.assertTrue(target.isFrozen());
            Assert.assertFalse(original.isFrozen());
            Assert.assertEquals(original, target);            
        } finally {
            con.invalidate();
        }
    }

    @Test(expected = NullPointerException.class)
    public void testToAddEventWithoutId() throws DAOException {
        PartakeConnection con = getPool().getConnection();
        
        try {
            String userId = "userId" + System.currentTimeMillis();
            
            Event original = createEvent(null, userId);
            {
                con.beginTransaction();
                dao.put(con, original);
                con.commit();
            }
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToAddUpdateGet() throws DAOException {
        PartakeConnection con = getPool().getConnection();

        try {
            String userId = "userId" + System.currentTimeMillis();
            String eventId;
            
            Event original = createEvent(null, userId);
            {
                con.beginTransaction();
                eventId = dao.getFreshId(con);
                original.setId(eventId);
                dao.put(con, original);
                con.commit();
            }
            
            Event updated = new Event(original);
            {
                updated.setTitle("updated title");
                con.beginTransaction();
                dao.put(con, updated);
                con.commit();
            }
            
            Event target;
            {
                con.beginTransaction();
                target = dao.find(con, eventId);
                con.commit();
            }

            Assert.assertNotNull(target);
            Assert.assertEquals(updated, target);
            Assert.assertFalse(original.equals(target));
        } finally {
            con.invalidate();
        }
    }

    
    @Test
    public void testToGetByInvalidId() throws DAOException {
        PartakeConnection con = getPool().getConnection();

        try {
            String eventId = "eventId-invalid-" + System.currentTimeMillis();
            
            Event target;
            {
                con.beginTransaction();
                target = dao.find(con, eventId);
                con.commit();
            }

            Assert.assertNull(target);
        } finally {
            con.invalidate();
        }   
    }

    @Test
    public void testToGetByOwner() throws DAOException {
        PartakeConnection con = getPool().getConnection();

        try {
            String userId = "userId-getbyowner-" + System.currentTimeMillis();
            Set<String> eventIds = new HashSet<String>();
            
            for (int i = 0; i < 10; ++i) {                
                String eventId;
                
                Event original = createEvent(null, userId);
                {
                    con.beginTransaction();
                    eventId = dao.getFreshId(con);
                    original.setId(eventId);
                    
                    dao.put(con, original);
                    con.commit();
                    
                    eventIds.add(eventId);
                }
            }
            
            List<Event> targetEvents = dao.findByOwnerId(con, userId);
            Set<String> targetEventIds = new HashSet<String>();
            for (Event e : targetEvents) {
                targetEventIds.add(e.getId());
            }
            
            Assert.assertEquals(eventIds, targetEventIds);
        } finally {
            con.invalidate();
        }
    }

    @Test
    public void testToGetByInvalidOwner() throws DAOException {
        PartakeConnection con = getPool().getConnection();

        try {
            String userId = "userId-getbyowner-" + System.currentTimeMillis();
            String invalidUserId = "userId-invalid-" + System.currentTimeMillis();
            
            Set<String> eventIds = new HashSet<String>();
            
            for (int i = 0; i < 10; ++i) {                
                String eventId;
                
                Event original = createEvent(null, userId);
                {
                    con.beginTransaction();
                    eventId = dao.getFreshId(con);
                    original.setId(eventId);
                    
                    dao.put(con, original);
                    con.commit();
                    
                    eventIds.add(eventId);
                }
            }
            
            List<Event> targetEvents = dao.findByOwnerId(con, invalidUserId);
            
            Assert.assertNotNull(targetEvents);
            Assert.assertTrue(targetEvents.isEmpty());
        } finally {
            con.invalidate();
        }
    }
    
    
    private Event createEvent(String eventId, String userId) {
        Date beginDate = PDate.getCurrentDate().getDate();
        Date now = PDate.getCurrentDate().getDate();
        String url = "http://localhost:8080/";
        String place = "";
        String address = "";
        String description = "";
        Event event = new Event(eventId, "DUMMY EVENT", "DUMMY EVENT", "DUMMY CATEGORY", null, beginDate , null, 0, url , place , address , description , "#partakein", userId, null, true, "passcode", false, false, now, now);
        event.setId(eventId);
        return event;
    }
}
