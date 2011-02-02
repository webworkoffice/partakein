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
        EntityManager em = getEntityManager(con);
        return freeze(em.find(UserPreference.class, userId)); 
    }

    @Override
    public void setPreference(PartakeConnection con, UserPreference embryo) throws DAOException {
        if (embryo == null) { throw new NullPointerException(); }
        if (embryo.getUserId() == null) { throw new NullPointerException(); }
        
        EntityManager em = getEntityManager(con);
        if (em.find(UserPreference.class, embryo.getUserId()) != null) {
            em.merge(embryo);
        } else { 
            em.persist(new UserPreference(embryo));
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM UserPreferences");
        q.executeUpdate();
    }
}
