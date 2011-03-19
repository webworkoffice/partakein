package in.partake.model.dao;

public interface ITruncatable {
    /** Use ONLY in unit tests.*/
    public abstract void truncate(PartakeConnection con) throws DAOException;
}
