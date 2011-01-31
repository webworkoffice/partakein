package in.partake.model.dao;

import in.partake.model.dto.TwitterLinkage;

public interface ITwitterLinkageAccess extends ITruncatable {

    public abstract int addTwitterLinkage(PartakeConnection con, TwitterLinkage embryo) throws DAOException;

    public abstract TwitterLinkage getTwitterLinkageById(PartakeConnection con, int twitterId) throws DAOException;

}