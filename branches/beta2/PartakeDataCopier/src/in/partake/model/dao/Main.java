package in.partake.model.dao;

import in.partake.model.dao.access.IAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dto.PartakeModel;
import in.partake.resource.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Main {
    private static PartakeDAOFactory srcFactory;
    private static PartakeConnectionPool srcPool;
    private static PartakeDAOFactory dstFactory;
    private static PartakeConnectionPool dstPool;

    public static void main(String[] args) throws Exception {
        srcFactory = (PartakeDAOFactory) Class.forName("in.partake.model.dao.jpa.JPADAOFactory").newInstance(); 
        srcPool    = (PartakeConnectionPool) Class.forName("in.partake.model.dao.jpa.JPAConnectionPool").newInstance();
        dstFactory = (PartakeDAOFactory) Class.forName("in.partake.model.dao.postgres9.Postgres9DAOFactory").newInstance(); 
        dstPool    = (PartakeConnectionPool) Class.forName("in.partake.model.dao.postgres9.Postgres9ConnectionPool").newInstance();
        
        prepareCopy();
        performCopy();
    }

    private static void prepareCopy() throws Exception {
        String[] sqls = new String[] {
                // - Binary         [x]
                // - Cache          [x]
                // - Calendar       [x]
                // - Comment        
                "UPDATE Comments         SET eventId     = ? WHERE eventId = 'demo'",
                // - Enrollment
                "UPDATE Enrollments      SET eventId     = ? WHERE eventId = 'demo'",
                // - Envelope       [x]
                // - Event          
                "UPDATE Events           SET id          = ? WHERE id = 'demo'",
                // - EventRelation  
                "UPDATE EventRelations   SET srcEventId  = ? WHERE srcEventId = 'demo'",
                //                  
                "UPDATE EventRelations   SET dstEventId  = ? WHERE dstEventId = 'demo'",
                // - EventReminder  
                "UPDATE EventReminders   SET eventId     = ? WHERE eventId = 'demo'",
                // - EventFeed      
                "UPDATE EventFeeds       SET eventId     = ? WHERE eventId = 'demo'",
                // - EventActivity  
                "UPDATE EventActivityes  SET eventId     = ? WHERE eventId = 'demo'",
                // - Message        
                "UPDATE Messages         SET eventId     = ? WHERE eventId = 'demo'",
                // - OpenID         [x]
                // - URLShortener   [x]
                // - TwitterLinkage [x]
                // - User           [x]
                // - UserPreference [x]
        };
        
        Postgres9Connection pcon = (Postgres9Connection) dstPool.getConnection();
        Connection con = pcon.getConnection();
        try {
            for (String sql : sqls) {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, Constants.DEMO_ID.toString());
                ps.execute();
                ps.close();
            }
        } finally {
            con.close();
        }
    }
    
    private static void performCopy() throws Exception {
        copy(srcFactory.getBinaryAccess(),          dstFactory.getBinaryAccess());
        copy(srcFactory.getCalendarAccess(),        dstFactory.getCalendarAccess());
        copy(srcFactory.getCommentAccess(),         dstFactory.getCommentAccess());
        copy(srcFactory.getEnrollmentAccess(),      dstFactory.getEnrollmentAccess());
        copy(srcFactory.getEnvelopeAccess(),        dstFactory.getEnvelopeAccess());
        copy(srcFactory.getEventAccess(),           dstFactory.getEventAccess());
        copy(srcFactory.getEventRelationAccess(),   dstFactory.getEventRelationAccess());
        copy(srcFactory.getEventReminderAccess(),   dstFactory.getEventReminderAccess());
        copy(srcFactory.getEventFeedAccess(),       dstFactory.getEventFeedAccess());
        copy(srcFactory.getEventActivityAccess(),   dstFactory.getEventActivityAccess());
        copy(srcFactory.getDirectMessageAccess(),   dstFactory.getDirectMessageAccess());
        copy(srcFactory.getOpenIDLinkageAccess(),   dstFactory.getOpenIDLinkageAccess());
        copy(srcFactory.getURLShortenerAccess(),    dstFactory.getURLShortenerAccess());
        copy(srcFactory.getTwitterLinkageAccess(),  dstFactory.getTwitterLinkageAccess());
        copy(srcFactory.getUserAccess(),            dstFactory.getUserAccess());
        copy(srcFactory.getUserPreferenceAccess(),  dstFactory.getUserPreferenceAccess());        
    }
    
    private static <T extends PartakeModel<T>, PK> void copy(IAccess<T, PK> src, IAccess<T, PK> dst) throws Exception {
        PartakeConnection srccon = srcPool.getConnection();
        PartakeConnection dstcon = dstPool.getConnection();

        dstcon.beginTransaction();
        dst.truncate(dstcon);
        dstcon.commit();
        
        // int cnt = 0;
        DataIterator<T> it = src.getIterator(srccon);        
        while (it.hasNext()) {
            T t = it.next();
            if (t == null) { continue; }
            System.out.println(t);
            
            dstcon.beginTransaction();
            dst.put(dstcon, t);
            dstcon.commit();
            
//            if (++cnt % 50 == 0) {
//                dstcon.invalidate();
//                dstcon = dstPool.getConnection();
//                srccon.invalidate();
//                srccon = srcPool.getConnection();
//            }            
        }
        it.close();
        
        srccon.invalidate();
        dstcon.invalidate();
        
        System.out.println("Seems OK");
    }    
}
