package in.partake.model.dao.jpa;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.ITwitterLinkageAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.TwitterLinkage;

class JPATwitterLinkageDao extends JPADao implements ITwitterLinkageAccess {

    @Override
    public int addTwitterLinkage(PartakeConnection con, TwitterLinkage embryo) throws DAOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public TwitterLinkage getTwitterLinkageById(PartakeConnection con, int twitterId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

}