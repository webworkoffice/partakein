package in.partake.service;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeModelFactory;
import in.partake.model.dto.EventNotificationStatus;

public final class MessageService extends PartakeService {
    private static MessageService instance = new MessageService();
    
    private MessageService() {
        // do nothing for now. 
    }
    
    public static MessageService get() {
        return instance;
    }
    
    /**
     * 「通知（リマインダ）を送るべきイベント全て」の通知の送信状況を取得するイテレータを作成する。
     * 
     * @return 現存するすべてのEventNotificationStatusに対するイテレータ
     * @throws DAOException
     */
    public DataIterator<EventNotificationStatus> getNotificationStatuses() throws DAOException {
        return getFactory().getMessageAccess().getNotificationStatuses(getFactory());
    }
    
    /**
     * イベントIDから通知（リマインダ）の送信状況を取得する。
     * @param eventId イベントのID
     * @return 指定したイベントに関する通知の状況
     * @throws DAOException
     */
    // TODO: should this be here? EventService?
    public EventNotificationStatus getNotificationStatus(String eventId) throws DAOException {
        PartakeModelFactory factory = getFactory();
        PartakeConnection con = factory.getConnection();
        try {
            return factory.getMessageAccess().getNotificationStatus(con, eventId);
        } finally {
            con.invalidate();
        }
    }
}
