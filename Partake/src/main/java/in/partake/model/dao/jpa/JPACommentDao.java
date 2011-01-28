package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.ICommentAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Comment;

class JPACommentDao extends JPADao implements ICommentAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public void addCommentWithId(PartakeConnection con, String commentId, Comment embryo) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");        
    }

    @Override
    public Comment getCommentById(PartakeConnection con, String commentId) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");        
    }

    @Override
    public void removeComment(PartakeConnection con, String commentId) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");        
    }

    @Override
    public void addCommentToEvent(PartakeConnection con, String commentId, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public DataIterator<String> getCommentIdsByEvent(PartakeConnection con, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public DataIterator<Comment> getCommentsByEvent(PartakeConnection con, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Comment");
        q.executeUpdate();
    }
}
