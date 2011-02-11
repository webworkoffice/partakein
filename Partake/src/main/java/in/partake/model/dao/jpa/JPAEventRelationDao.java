package in.partake.model.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IEventRelationAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventRelation;

class JPAEventRelationDao extends JPADao<EventRelation> implements IEventRelationAccess {

    @Override
    public void setEventRelations(PartakeConnection con, String eventId, List<EventRelation> relations) throws DAOException {
        EntityManager em = getEntityManager(con);
        
        // remove event relations first.
        {
            Query q = em.createQuery("DELETE FROM EventRelations er WHERE er.srcEventId = :eventId");
            q.setParameter("eventId", eventId);
            q.executeUpdate();
        }
        
        for (EventRelation er : relations) {
            createWithoutPrimaryKey(con, er, EventRelation.class);
        }
    }

    @Override
    public List<EventRelation> getEventRelations(PartakeConnection con, String eventId) throws DAOException {
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

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM EventRelations");
        q.executeUpdate();
    }
}
