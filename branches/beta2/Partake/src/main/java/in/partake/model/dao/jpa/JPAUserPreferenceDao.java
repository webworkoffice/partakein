package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserPreferenceAccess;
import in.partake.model.dto.UserPreference;

class JPAUserPreferenceDao extends JPADao<UserPreference> implements IUserPreferenceAccess {

    @Override
    public UserPreference find(PartakeConnection con, String userId) throws DAOException {
        return findImpl(con, userId, UserPreference.class);
    }

    @Override
    public void put(PartakeConnection con, UserPreference embryo) throws DAOException {
        putImpl(con, embryo, UserPreference.class);
    }
    
    @Override
    public void remove(PartakeConnection con, String key) throws DAOException {
        removeImpl(con, key, UserPreference.class);
    }

    @Override
    public DataIterator<UserPreference> getIterator(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT t FROM UserPreferences t");
        
        @SuppressWarnings("unchecked")
        List<UserPreference> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<UserPreference>(em, list, UserPreference.class, false);
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM UserPreferences");
        q.executeUpdate();
    }
}
