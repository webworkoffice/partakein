package in.partake.model.dao;

import in.partake.model.dto.Comment;

public interface ICommentAccess extends IAccess<Comment, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;
    // TODO tell about order of the DataIterator's value.
    public DataIterator<Comment> getCommentsByEvent(PartakeConnection con, String eventId) throws DAOException;
}
