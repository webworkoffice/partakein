package in.partake.model.dao;

import in.partake.model.dto.UserPreference;

public interface IUserPreferenceAccess {

    public void setPreference(PartakeConnection con, UserPreference embryo) throws DAOException;
    public UserPreference getPreference(PartakeConnection con, String userId) throws DAOException;

    /** Use ONLY in unit tests.*/
    public abstract void truncate(PartakeConnection con) throws DAOException;

}