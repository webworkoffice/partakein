package in.partake.model.dao;

import in.partake.model.dto.Event;

import java.util.List;

public interface IEventAccess extends IAccess<Event, String> {
    // TODO: 他の dao は add と update を区別してないのになんでここは区別しているんだろう
    // ほかも add と update を区別するべき
    public abstract String getFreshId(PartakeConnection con) throws DAOException;

    // ----------------------------------------------------------------------
    // event utilities

    // TODO getEventの結果がremovedフラグを持つ実装にすることも、考察の余地あり（既存コードへの変更が大きいためひとまず見送っている）
    public abstract boolean isRemoved(PartakeConnection con, String eventId) throws DAOException;
    public abstract List<Event> findByOwnerId(PartakeConnection con, String userId) throws DAOException;
}