package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.ICalendarLinkageAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.CalendarLinkage;

class JPACalendarLinkageDao extends JPADao implements ICalendarLinkageAccess {

    @Override
    public String getFreshCalendarId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, CalendarLinkage.class);
    }

    @Override
    public void addCalendarLinkage(PartakeConnection con, CalendarLinkage embryo) throws DAOException {
        if (embryo.getId() == null) { throw new DAOException("id should not be null."); }
        
        EntityManager em = getEntityManager(con);
        em.persist(new CalendarLinkage(embryo));
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
    public CalendarLinkage getCalendarLinkageByUserId(PartakeConnection con, String userId) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT cl FROM CalendarLinkage WHERE userId = :userId");
        return (CalendarLinkage) q.getSingleResult();
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
        Query q = em.createQuery("DELETE FROM CalendarLinkages");
        q.executeUpdate();
    }
}
