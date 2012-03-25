package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.ICalendarLinkageAccess;
import in.partake.model.dto.CalendarLinkage;

class JPACalendarLinkageDao extends JPADao<CalendarLinkage> implements ICalendarLinkageAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, CalendarLinkage.class);
    }

    @Override
    public void put(PartakeConnection con, CalendarLinkage embryo) throws DAOException {
        putImpl(con, embryo, CalendarLinkage.class);
    }

    @Override
    public CalendarLinkage find(PartakeConnection con, String id) throws DAOException {
        return findImpl(con, id, CalendarLinkage.class);
    }
    
    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        removeImpl(con, id, CalendarLinkage.class);
    }
    
    @Override
    public CalendarLinkage findByUserId(PartakeConnection con, String userId) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT cl FROM CalendarLinkages cl WHERE cl.userId = :userId");
        q.setParameter("userId", userId);
        @SuppressWarnings("unchecked")
        List<CalendarLinkage> results = q.getResultList();
        if (results.isEmpty()) { return null; }
        else { return freeze(results.get(0)); }
    }

    @Override
    public DataIterator<CalendarLinkage> getIterator(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT t FROM CalendarLinkages t");
        
        @SuppressWarnings("unchecked")
        List<CalendarLinkage> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<CalendarLinkage>(em, list, CalendarLinkage.class, false);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM CalendarLinkages");
        q.executeUpdate();
    }
    
    @Override
    public int count(PartakeConnection con) throws DAOException {
        return countImpl(con, "CalendarLinkages");
    }

}
