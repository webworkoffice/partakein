package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.User;

class JPAUserDao extends JPADao implements IUserAccess {

    @Override
    public String getFreshUserId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, User.class);
    }

    @Override
    public void addUser(PartakeConnection con, String usreId, int twitterId) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public User getUserById(PartakeConnection con, String id) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateLastLogin(PartakeConnection con, User user) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<User> getUsersByIds(PartakeConnection con, List<String> ids) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM User");
        q.executeUpdate();
    }
}
