package in.partake.service;

import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.cassandra.CassandraDAOFactory;
import in.partake.model.dto.Event;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;

public abstract class PartakeService {
    private static PartakeDAOFactory factory = new CassandraDAOFactory();
    
    public static void setDAOFactory(PartakeDAOFactory factory) {
        PartakeService.factory = factory;
    }
    
    protected static PartakeDAOFactory getFactory() {
        return factory;
    }
    
    // ----------------------------------------------------------------------
    // Utility function
    
    protected UserEx getUserEx(PartakeConnection con, String userId) throws DAOException {
        User user = getFactory().getUserAccess().getUserById(con, userId);
        TwitterLinkage linkage = getFactory().getTwitterLinkageAccess().getTwitterLinkageById(con, user.getTwitterId());
        return new UserEx(user, linkage); 
    }
    
    protected EventEx getEventEx(PartakeConnection con, String eventId) throws DAOException {
        Event event = getFactory().getEventAccess().getEventById(con, eventId);
        User user = getFactory().getUserAccess().getUserById(con, event.getOwnerId());
        TwitterLinkage tw = getFactory().getTwitterLinkageAccess().getTwitterLinkageById(con, user.getTwitterId());
        String feedId = getFactory().getFeedAccess().getFeedIdByEventId(con, eventId);
        
        UserEx userEx = new UserEx(user, tw);
        return new EventEx(event, userEx, feedId);
    }
}
