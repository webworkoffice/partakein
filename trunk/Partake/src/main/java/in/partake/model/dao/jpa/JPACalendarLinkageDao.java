package in.partake.model.dao.jpa;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.ICalendarLinkageAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.CalendarLinkage;

class JPACalendarLinkageDao extends JPADao implements ICalendarLinkageAccess {

    @Override
    public String getFreshCalendarId(PartakeConnection con) throws DAOException {
        JPAConnection jcon = (JPAConnection) con;

        String key = null;
        CalendarLinkage linkage = null;
        do {
            key = UUID.randomUUID().toString();
            EntityManager em = jcon.getEntityManager();
            
            linkage = em.find(CalendarLinkage.class, key);
        } while (linkage != null);
        
        assert(key != null);
        assert(linkage == null);
        
        return key;
    }

    @Override
    public void addCalendarLinkage(PartakeConnection con, CalendarLinkage embryo) throws DAOException {
        if (embryo.getId() == null) { throw new DAOException("id should not be null."); }
        
        EntityManager em = getEntityManager(con);
        em.persist(embryo);
    }

    @Override
    public CalendarLinkage getCalendarLinkage(PartakeConnection con, String id) throws DAOException {
        EntityManager em = getEntityManager(con);
        CalendarLinkage linkage = em.find(CalendarLinkage.class, id);
        
        if (linkage != null) {
            return linkage.freeze();
        } else {
            return null;
        }
    }

    @Override
    public void removeCalendarLinkage(PartakeConnection con, String id) throws DAOException {
        EntityManager em = getEntityManager(con);
        CalendarLinkage linkage = em.find(CalendarLinkage.class, id);
        if (linkage != null) { em.remove(linkage); }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM CalendarLinkage");
        q.executeUpdate();
    }
}
