package in.partake.model.dao;

import in.partake.model.dto.CacheData;

public interface ICacheAccess {
    /**
     * create or update cache data. 
     * @param con
     * @param cacheData
     * @throws DAOException
     */
    public void addCache(PartakeConnection con, CacheData cacheData) throws DAOException;
    
    /**
     * remove the specified cache data.
     * @param con
     * @param cacheId
     * @throws DAOException
     */
    public void removeCache(PartakeConnection con, String cacheId) throws DAOException;
    
    /**
     * take the specified cache data.
     * @param con
     * @param cacheId
     * @return
     * @throws DAOException
     */
    public CacheData getCache(PartakeConnection con, String cacheId) throws DAOException;
    
    /**
     * remove all data in the cache access. 
     * Use ONLY in unit tests.
     */
    public void truncate(PartakeConnection con) throws DAOException;
}
