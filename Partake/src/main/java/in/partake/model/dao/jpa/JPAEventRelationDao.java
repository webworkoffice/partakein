package in.partake.model.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IEventRelationAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventRelation;
import in.partake.model.dto.pk.EventRelationPK;

class JPAEventRelationDao extends JPADao<EventRelation> implements IEventRelationAccess {

    @Override
    public void put(PartakeConnection con, EventRelation eventRelation) throws DAOException {
        putImpl(con, eventRelation, EventRelation.class);
    }
    
    @Override
    public EventRelation find(PartakeConnection con, EventRelationPK key) throws DAOException {
        return findImpl(con, key, EventRelation.class);
    }
    
    @Override
    public void remove(PartakeConnection con, EventRelationPK key) throws DAOException {
        removeImpl(con, key, EventRelation.class);        
    }
    
    @Override
    public DataIterator<EventRelation> getIterator(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT t FROM EventRelations t");
        
        @SuppressWarnings("unchecked")
        List<EventRelation> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<EventRelation>(em, list, EventRelation.class, false);

    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM EventRelations");
        q.executeUpdate();
    }

    
    @Override
    public void removeByEventId(PartakeConnection con, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM EventRelations er WHERE er.srcEventId = :eventId");
        q.setParameter("eventId", eventId);
        q.executeUpdate();
    }

    @Override
    public List<EventRelation> findByEventId(PartakeConnection con, String eventId) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT er FROM EventRelations er WHERE er.srcEventId = :eventId");
        q.setParameter("eventId", eventId);
        
        @SuppressWarnings("unchecked")
        List<EventRelation> relations = q.getResultList();
        
        List<EventRelation> eventRelations = new ArrayList<EventRelation>();
        for (EventRelation er : relations) {
            eventRelations.add(er.freeze());
        }
        
        return eventRelations;
    }

}
