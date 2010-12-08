package in.partake.model.dao;

import in.partake.model.dto.EventNotificationStatus;

public interface IMessageAccess {

    /**
     * 新しく notification を追加。
     * @param event
     * @throws DAOException
     */
    public abstract void addNotification(PartakeConnection con, String eventId) throws DAOException;

    public abstract EventNotificationStatus getNotificationStatus(PartakeConnection con, String eventId) throws DAOException;

    public abstract DataIterator<EventNotificationStatus> getNotificationStatuses(PartakeDAOFactory factory) throws DAOException;

}