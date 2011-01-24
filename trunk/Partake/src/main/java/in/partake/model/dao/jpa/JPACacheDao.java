package in.partake.model.dao.jpa;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.ICacheAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.CacheData;

class JPACacheDao extends JPADao implements ICacheAccess {

    @Override
    public void addCache(PartakeConnection con, CacheData cacheData) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public void removeCache(PartakeConnection con, String cacheId) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public CacheData getCache(PartakeConnection con, String cacheId) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented yet.");
    }
}
