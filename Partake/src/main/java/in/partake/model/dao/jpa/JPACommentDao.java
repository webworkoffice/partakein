package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.ICommentAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Comment;

class JPACommentDao extends JPADao<Comment> implements ICommentAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, Comment.class);
    }

    @Override
    public void addComment(PartakeConnection con, Comment embryo) throws DAOException {
        createOrUpdate(con, embryo, Comment.class);
    }

    @Override
    public Comment getComment(PartakeConnection con, String commentId) throws DAOException {
        return find(con, commentId, Comment.class);
    }

    @Override
    public void removeComment(PartakeConnection con, String commentId) throws DAOException {
        remove(con, commentId, Comment.class);
    }

    @Override
    public DataIterator<Comment> getCommentsByEvent(PartakeConnection con, String eventId) throws DAOException {
        EntityManager em = getEntityManager(con);
        
        Query q = em.createQuery("SELECT c FROM Comments AS c WHERE c.eventId = :eventId");
        q.setParameter("eventId", eventId);
        
        @SuppressWarnings("unchecked")
        List<Comment> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<Comment>(em, list, Comment.class, false);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Comments");
        q.executeUpdate();
    }
}