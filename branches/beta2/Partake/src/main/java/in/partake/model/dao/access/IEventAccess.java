package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.aux.EventFilterCondition;
import in.partake.model.dto.Event;

import java.util.List;

public interface IEventAccess extends IAccess<Event, String> {
    public abstract String getFreshId(PartakeConnection con) throws DAOException;

    // TODO: getEventの結果がremovedフラグを持つ実装にすることも、考察の余地あり（既存コードへの変更が大きいためひとまず見送っている）
    public abstract boolean isRemoved(PartakeConnection con, String eventId) throws DAOException;
    
    public abstract int count(PartakeConnection con, EventFilterCondition condition) throws DAOException;
    
    /**
     * fetch events whose owner id is <code>userId</code>. 
     * @param con
     * @param userId
     * @return
     * @throws DAOException
     */
    @Deprecated
    public abstract List<Event> findByOwnerId(PartakeConnection con, String userId) throws DAOException;
    public abstract List<Event> findByOwnerId(PartakeConnection con, String userId, EventFilterCondition condition, int offset, int limit) throws DAOException;
    public abstract int countEventsByOwnerId(PartakeConnection con, String userId, EventFilterCondition condition) throws DAOException;
    
    /**
     * screen name が manager として指定されているような Event を取得する。
     * @param con
     * @param screenName
     * @return
     * @throws DAOException
     */
    @Deprecated
    public abstract List<Event> findByScreenName(PartakeConnection con, String screenName) throws DAOException;
    public abstract List<Event> findByScreenName(PartakeConnection con, String screenName, EventFilterCondition condition, int offset, int limit) throws DAOException;
    public abstract int countEventsByScreenName(PartakeConnection con, String screenName, EventFilterCondition condition) throws DAOException;
}