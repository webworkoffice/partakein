package in.partake.model.dao;

import in.partake.model.dto.Comment;

public interface ICommentAccess extends IAccess<Comment, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;
    public DataIterator<Comment> getCommentsByEvent(PartakeConnection con, String eventId) throws DAOException;
}
