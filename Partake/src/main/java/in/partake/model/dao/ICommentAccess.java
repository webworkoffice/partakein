package in.partake.model.dao;

import in.partake.model.dto.Comment;

public interface ICommentAccess {
    public String getFreshId(PartakeConnection con) throws DAOException;
    
    public void addCommentWithId(PartakeConnection con, String commentId, Comment embryo) throws DAOException;
    public Comment getCommentById(PartakeConnection con, String commentId) throws DAOException;
    public void removeComment(PartakeConnection con, String commentId) throws DAOException;
    
    public void addCommentToEvent(PartakeConnection con, String commentId, String eventId) throws DAOException;
    public DataIterator<Comment> getCommentsByEvent(PartakeModelFactory factory, String eventId) throws DAOException;
}
