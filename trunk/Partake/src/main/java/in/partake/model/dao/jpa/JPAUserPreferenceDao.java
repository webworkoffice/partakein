package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IUserPreferenceAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserPreference;

class JPAUserPreferenceDao extends JPADao<UserPreference> implements IUserPreferenceAccess {

    @Override
    public UserPreference getPreference(PartakeConnection con, String userId) throws DAOException {
        return find(con, userId, UserPreference.class);
    }

    @Override
    public void setPreference(PartakeConnection con, UserPreference embryo) throws DAOException {
        createOrUpdate(con, embryo, UserPreference.class);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM UserPreferences");
        q.executeUpdate();
    }
}
