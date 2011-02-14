package in.partake.model.dao;

import in.partake.model.dto.FeedLinkage;

public interface IFeedAccess extends IAccess<FeedLinkage, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;
	public String findByEventId(PartakeConnection con, String eventId) throws DAOException;
}