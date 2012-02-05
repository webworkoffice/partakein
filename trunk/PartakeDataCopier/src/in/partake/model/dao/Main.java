package in.partake.model.dao;

import in.partake.model.dao.access.IAccess;
import in.partake.model.dto.PartakeModel;

public class Main {
    private static PartakeDAOFactory casFactory;
    private static PartakeConnectionPool casPool;
    private static PartakeDAOFactory jpaFactory;
    private static PartakeConnectionPool jpaPool;

    public static void main(String[] args) throws Exception {
        casFactory = (PartakeDAOFactory) Class.forName("in.partake.model.dao.cassandra.CassandraDAOFactory").newInstance(); 
        casPool    = (PartakeConnectionPool) Class.forName("in.partake.model.dao.cassandra.CassandraConnectionPool").newInstance();
        jpaFactory = (PartakeDAOFactory) Class.forName("in.partake.model.dao.jpa.JPADAOFactory").newInstance(); 
        jpaPool    = (PartakeConnectionPool) Class.forName("in.partake.model.dao.jpa.JPAConnectionPool").newInstance();
        
        copy(casFactory.getBinaryAccess(),          jpaFactory.getBinaryAccess());
        copy(casFactory.getCacheAccess(),           jpaFactory.getCacheAccess());
        copy(casFactory.getCalendarAccess(),        jpaFactory.getCalendarAccess());
        copy(casFactory.getCommentAccess(),         jpaFactory.getCommentAccess());
        copy(casFactory.getEnrollmentAccess(),      jpaFactory.getEnrollmentAccess());
        copy(casFactory.getEnvelopeAccess(),        jpaFactory.getEnvelopeAccess());
        copy(casFactory.getEventAccess(),           jpaFactory.getEventAccess());
        copy(casFactory.getEventRelationAccess(),   jpaFactory.getEventRelationAccess());
        copy(casFactory.getEventReminderAccess(),   jpaFactory.getEventReminderAccess());
        copy(casFactory.getEventFeedAccess(),       jpaFactory.getEventFeedAccess());
        copy(casFactory.getEventActivityAccess(),   jpaFactory.getEventActivityAccess());
        copy(casFactory.getDirectMessageAccess(),   jpaFactory.getDirectMessageAccess());
        copy(casFactory.getOpenIDLinkageAccess(),   jpaFactory.getOpenIDLinkageAccess());
        copy(casFactory.getURLShortenerAccess(),    jpaFactory.getURLShortenerAccess());
        copy(casFactory.getTwitterLinkageAccess(),  jpaFactory.getTwitterLinkageAccess());
        copy(casFactory.getUserAccess(),            jpaFactory.getUserAccess());
        copy(casFactory.getUserPreferenceAccess(),  jpaFactory.getUserPreferenceAccess());
    }
    
    private static <T extends PartakeModel<T>, PK> void copy(IAccess<T, PK> cas, IAccess<T, PK> jpa) throws Exception {
        PartakeConnection cascon = casPool.getConnection();
        PartakeConnection jpacon = jpaPool.getConnection();

        jpacon.beginTransaction();
        jpa.truncate(jpacon);
        jpacon.commit();
      
        
        int cnt = 0;
        DataIterator<T> it = cas.getIterator(cascon);        
        while (it.hasNext()) {
            T t = it.next();
            if (t == null) { continue; }
            System.out.println(t);
            
            jpacon.beginTransaction();
            jpa.put(jpacon, t);
            jpacon.commit();
            
            if (++cnt % 50 == 0) {
                jpacon.invalidate();
                jpacon = jpaPool.getConnection();
                cascon.invalidate();
                cascon = casPool.getConnection();
            }
            
        }
        
        cascon.invalidate();
        jpacon.invalidate();
        
        System.out.println("Seems OK");
    }    
}
