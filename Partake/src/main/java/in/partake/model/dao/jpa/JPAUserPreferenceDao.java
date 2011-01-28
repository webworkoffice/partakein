package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IUserPreferenceAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserPreference;

class JPAUserPreferenceDao extends JPADao implements IUserPreferenceAccess {

    @Override
    public UserPreference getPreference(PartakeConnection con, String userId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPreference(PartakeConnection con, UserPreference embryo) throws DAOException {
        if (embryo == null || embryo.getUserId() == null) { throw new IllegalArgumentException(); }
        
        // TODO Auto-generated method stub
        
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM UserPreference");
        q.executeUpdate();
    }
}
