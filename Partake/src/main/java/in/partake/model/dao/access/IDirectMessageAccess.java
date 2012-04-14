package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.DirectMessage;

@Deprecated
public interface IDirectMessageAccess extends IAccess<DirectMessage, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;
    /** eventに紐づいたメッセージを新しい順に返すDataIteratorを作成する。 */
    public DataIterator<DirectMessage> findByEventId(PartakeConnection con, String eventId) throws DAOException;
}
