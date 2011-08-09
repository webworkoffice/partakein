package in.partake.model.dao;

import in.partake.model.dto.Message;

public interface IMessageAccess extends IAccess<Message, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;
    /** eventに紐づいたメッセージを新しい順に返すDataIteratorを作成する。 */
    public DataIterator<Message> findByEventId(PartakeConnection con, String eventId) throws DAOException;
}
