package in.partake.model.dao.jpa;

import java.util.List;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IEventRelationAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventRelation;

class JPAEventRelationDao extends JPADao implements IEventRelationAccess {

    @Override
    public void setEventRelations(PartakeConnection con, String eventId, List<EventRelation> relations) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<EventRelation> getEventRelations(PartakeConnection con, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

}
