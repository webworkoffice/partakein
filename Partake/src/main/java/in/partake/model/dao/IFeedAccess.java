package in.partake.model.dao;

public interface IFeedAccess {
    public String getFreshId(PartakeConnection con) throws DAOException;
	public String getFeedIdByEventId(PartakeConnection con, String eventId) throws DAOException;
	public String getEventIdByFeedId(PartakeConnection con, String feedId) throws DAOException;
	public void addFeedId(PartakeConnection con, String feedId, String eventId) throws DAOException;
	
    /** Use ONLY in unit tests.*/
    public abstract void truncate(PartakeConnection con) throws DAOException;
}