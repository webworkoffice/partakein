package in.partake.model.dao;

import in.partake.model.dto.UserPreference;

public interface IUserPreferenceAccess {

    public UserPreference getPreference(PartakeConnection con, String userId) throws DAOException;

    public void setPreference(PartakeConnection con, String userId, UserPreference embryo) throws DAOException;

}