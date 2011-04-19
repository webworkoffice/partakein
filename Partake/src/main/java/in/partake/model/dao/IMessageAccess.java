package in.partake.model.dao;

import in.partake.model.dto.Message;

public interface IMessageAccess extends IAccess<Message, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;
    // FIXME このメソッドが返すDataIteratorの要素が、何順に並んでいるのかが不明。MessageServiceは新しい順に並んでいることを期待している。
    public DataIterator<Message> findByEventId(PartakeConnection con, String eventId) throws DAOException;
}
