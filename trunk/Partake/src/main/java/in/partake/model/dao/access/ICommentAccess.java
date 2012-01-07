package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Comment;

public interface ICommentAccess extends IAccess<Comment, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;
    // TODO tell about order of the DataIterator's value.
    public DataIterator<Comment> getCommentsByEvent(PartakeConnection con, String eventId) throws DAOException;
}
