package in.partake.model.dao.jpa;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.User;

class JPAUserDao extends JPADao<User> implements IUserAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, User.class);
    }

    @Override
    public void put(PartakeConnection con, User user) throws DAOException {
        putImpl(con, user, User.class);
    }
    
    @Override
    public User find(PartakeConnection con, String id) throws DAOException {
        return findImpl(con, id, User.class);
    }
    
    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        removeImpl(con, id, User.class);
    }
    
    @Override
    public DataIterator<User> getIterator(PartakeConnection con) throws DAOException {
        return getIteratorImpl(con, "Users", User.class);
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Users");
        q.executeUpdate();
    }

    @Override
    public void updateLastLogin(PartakeConnection con, String userId, Date now) throws DAOException {        
        User user = find(con, userId);
        if (user == null) { return; }        
        User newUser = new User(user);
        newUser.setLastLoginAt(now);
        put(con, newUser);
    }

}
