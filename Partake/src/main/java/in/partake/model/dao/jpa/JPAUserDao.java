package in.partake.model.dao.jpa;

import java.util.List;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IUserAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.User;

class JPAUserDao extends JPADao implements IUserAccess {

    @Override
    public String getFreshUserId(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
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
    public void updateCalendarId(PartakeConnection con, User user, String calendarId) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<User> getUsersByIds(PartakeConnection con, List<String> ids) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addOpenID(PartakeConnection con, String userId, String identifier) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeOpenID(PartakeConnection con, String userId, String identifier) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public DataIterator<String> getOpenIDIdentifiers(PartakeConnection con, String userId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        
    }
}
