package in.partake.model.dao.jpa;

import java.util.List;

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
        return getFreshIdImpl(con, Comment.class);
    }

    @Override
    public void addComment(PartakeConnection con, Comment embryo) throws DAOException {
        if (embryo == null) { throw new IllegalStateException(); }
        if (embryo.getId() == null) { throw new IllegalStateException(); }

        EntityManager em = getEntityManager(con);        
        em.persist(embryo);
    }

    @Override
    public Comment getComment(PartakeConnection con, String commentId) throws DAOException {
        EntityManager em = getEntityManager(con);        
        return em.find(Comment.class, commentId);
    }

    @Override
    public void removeComment(PartakeConnection con, String commentId) throws DAOException {
        EntityManager em = getEntityManager(con);
        
        Query q = em.createQuery("DELETE FROM Comment c WHERE c.id = :id");
        q.setParameter("id", commentId);
        q.executeUpdate();
    }

    @Override
    public DataIterator<Comment> getCommentsByEvent(PartakeConnection con, String eventId) throws DAOException {
        EntityManager em = getEntityManager(con);
        
        Query q = em.createQuery("SELECT FROM Comment c WHERE c.eventId = :eventId");
        q.setParameter("eventId", eventId);
        
        @SuppressWarnings("unchecked")
        List<Comment> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<Comment>(em, list);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Comment");
        q.executeUpdate();
    }
}
