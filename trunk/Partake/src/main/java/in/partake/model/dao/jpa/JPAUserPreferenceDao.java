package in.partake.model.dao.jpa;

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
    public void setPreference(PartakeConnection con, String userId, UserPreference embryo) throws DAOException {
        // TODO Auto-generated method stub
        
    }

}
