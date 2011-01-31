package in.partake.model.dao;

import in.partake.model.dto.Comment;

public interface ICommentAccess extends ITruncatable {
    public String getFreshId(PartakeConnection con) throws DAOException;
    
    public void addComment(PartakeConnection con, Comment embryo) throws DAOException;
    public Comment getComment(PartakeConnection con, String commentId) throws DAOException;
    public void removeComment(PartakeConnection con, String commentId) throws DAOException;
    
    public DataIterator<Comment> getCommentsByEvent(PartakeConnection con, String eventId) throws DAOException;
}
