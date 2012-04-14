package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventNotification;

import java.util.List;

public interface IEventNotificationAccess extends IAccess<EventNotification, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;

    public List<EventNotification> findByEventId(PartakeConnection con, String eventId, int offset, int limit) throws DAOException;

}
