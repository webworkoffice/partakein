package in.partake.service;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.EventNotificationStatus;

public final class MessageService extends PartakeService {
    private static MessageService instance = new MessageService();
    
    private MessageService() {
        // do nothing for now. 
    }
    
    public static MessageService get() {
        return instance;
    }
    
    public DataIterator<EventNotificationStatus> getNotificationStatuses() throws DAOException {
        return getFactory().getMessageAccess().getNotificationStatuses(getFactory());
    }
    
    // TODO: should this be here? EventService?
    public EventNotificationStatus getNotificationStatus(String eventId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = factory.getConnection();
        try {
            return factory.getMessageAccess().getNotificationStatus(con, eventId);
        } finally {
            con.invalidate();
        }
    }
}