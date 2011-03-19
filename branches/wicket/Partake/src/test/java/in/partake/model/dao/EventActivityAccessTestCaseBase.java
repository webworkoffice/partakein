package in.partake.model.dao;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import in.partake.model.dto.EventActivity;

public abstract class EventActivityAccessTestCaseBase extends AbstractDaoTestCaseBase<IEventActivityAccess, EventActivity, String> {
    
    @Before
    public void setup() throws DAOException {
        super.setup(getFactory().getEventActivityAccess());
    }
    
    @Override
    public EventActivity create(long pkNumber, String pkSalt, int objNumber) {
        return new EventActivity(pkSalt + pkNumber, "eventId", "title" + objNumber, "content", new Date(objNumber % 10));
    }
    
    @Test
    public void testToIterator1() throws Exception {
        PartakeConnection con = getPool().getConnection();
        String eventId = "eventId-findByEventId-0-" + System.currentTimeMillis();
        
        try {
            con.beginTransaction();
            for (int i = 0; i < 10; ++i) {
                EventActivity activity = new EventActivity(dao.getFreshId(con), eventId, "title-" + i, "content", new Date(i));
                dao.put(con, activity);
            }
            con.commit();
            
            List<EventActivity> activities = dao.findByEventId(con, eventId, 10);
            
            Assert.assertEquals(10, activities.size());
            for (int i = 0; i < 10; ++i) {
                Assert.assertEquals(9 - i, activities.get(i).getCreatedAt().getTime());
            }
        } finally {
            con.invalidate();
        }
    }
    
    @Test
    public void testToIterator2() throws Exception {
        PartakeConnection con = getPool().getConnection();
        String eventId1 = "eventId-findByEventId-1-" + System.currentTimeMillis();
        String eventId2 = "eventId-findByEventId-2-" + System.currentTimeMillis();
        
        
        try {
            con.beginTransaction();
            for (int i = 0; i < 10; ++i) {
                EventActivity activity = new EventActivity(dao.getFreshId(con), eventId1, "title-" + i, "content", new Date(i));
                dao.put(con, activity);
            }
            for (int i = 0; i < 10; ++i) {
                EventActivity activity = new EventActivity(dao.getFreshId(con), eventId2, "title-" + i, "content", new Date(i));
                dao.put(con, activity);                
            }
            con.commit();
            
            List<EventActivity> activities1 = dao.findByEventId(con, eventId1, 10);
            List<EventActivity> activities2 = dao.findByEventId(con, eventId2, 10);
            
            Assert.assertEquals(10, activities1.size());
            Assert.assertEquals(10, activities2.size());
            
            for (int i = 0; i < 10; ++i) {
                Assert.assertEquals(9 - i, activities1.get(i).getCreatedAt().getTime());
                Assert.assertEquals(9 - i, activities2.get(i).getCreatedAt().getTime());                
            }
        } finally {
            con.invalidate();
        }
    }
    
    
    @Test
    public void testToIterator3() throws Exception {
        PartakeConnection con = getPool().getConnection();
        String eventId1 = "eventId-findByEventId-1-" + System.currentTimeMillis();
        String eventId2 = "eventId-findByEventId-2-" + System.currentTimeMillis();
        
        try {
            con.beginTransaction();
            for (int i = 0; i < 100; ++i) {
                EventActivity activity = new EventActivity(dao.getFreshId(con), eventId1, "title-" + i, "content", new Date(i));
                dao.put(con, activity);
            }
            con.commit();
            con.beginTransaction();
            for (int i = 0; i < 100; ++i) {
                EventActivity activity = new EventActivity(dao.getFreshId(con), eventId2, "title-" + i, "content", new Date(i));
                dao.put(con, activity);                
            }
            con.commit();
            
            
            List<EventActivity> activities1 = dao.findByEventId(con, eventId1, 100);
            List<EventActivity> activities2 = dao.findByEventId(con, eventId2, 100);
            
            Assert.assertEquals(100, activities1.size());
            Assert.assertEquals(100, activities2.size());
            
            // TODO: JPA ではミリ秒が考慮されてないっぽい。(10 ミリ秒単位で四捨五入された文字列が生成されている)
            // FIXME: これキャストとかしないとだめなんか？
            for (int i = 0; i < 100; ++i) {
                Assert.assertEquals(99 - i, activities1.get(i).getCreatedAt().getTime());
                Assert.assertEquals(99 - i, activities2.get(i).getCreatedAt().getTime());                
            }
        } finally {
            con.invalidate();
        }
    }
}
