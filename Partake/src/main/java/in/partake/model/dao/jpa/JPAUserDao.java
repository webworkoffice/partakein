package in.partake.model.dao.jpa;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.User;

class JPAUserDao extends JPADao implements IUserAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, User.class);
    }

    @Override
    public void addUser(PartakeConnection con, User user) throws DAOException {
        if (user == null) { throw new NullPointerException(); }
        if (user.getId() == null) { throw new NullPointerException(); }
        
        EntityManager em = getEntityManager(con);
        em.persist(new User(user)); // use copy constructor to prevent using the same object in the cache.
    }

    @Override
    public User getUser(PartakeConnection con, String id) throws DAOException {
        EntityManager em = getEntityManager(con);
        return freeze(em.find(User.class, id));
    }

    @Override
    public void updateLastLogin(PartakeConnection con, String userId, Date now) throws DAOException {
        EntityManager em = getEntityManager(con);
        User user = em.find(User.class, userId);
        if (user == null) { throw new DAOException("No such element"); }
        
        user.setLastLoginAt(now);
        em.merge(user);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Users");
        q.executeUpdate();
    }
}
