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
        // TODO Auto-generated method stub
        
    }

    @Override
    public User getUser(PartakeConnection con, String id) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateLastLogin(PartakeConnection con, User user, Date now) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM Users");
        q.executeUpdate();
    }
}
