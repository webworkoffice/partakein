package in.partake.model.dao.postgres9.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IOpenIDLinkageAccess;
import in.partake.model.dao.postgres9.Postgres9Connection;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9IndexDao;
import in.partake.model.dto.OpenIDLinkage;

import java.util.List;

public class Postgres9OpenIDLinkageDao extends Postgres9Dao implements IOpenIDLinkageAccess {
    static final String TABLE_NAME = "OpenIDLinkageEntities";
    static final int CURRENT_VERSION = 1;

    static final String INDEX_TABLE_NAME = "OpenIdLinkageIndex";

    private final Postgres9IndexDao openIDIndexDao;
    
    public Postgres9OpenIDLinkageDao() {
        this.openIDIndexDao = new Postgres9IndexDao(INDEX_TABLE_NAME);
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        Postgres9Connection pcon = (Postgres9Connection) con;
        
        if (!existsTable(pcon, INDEX_TABLE_NAME)) {
            openIDIndexDao.createIndexTable(pcon, "CREATE TABLE " + INDEX_TABLE_NAME + "(openId TEXT PRIMARY KEY, userId TEXT NOT NULL)");
            openIDIndexDao.createIndex(pcon, "CREATE INDEX " + INDEX_TABLE_NAME + "UserId" + " ON " + INDEX_TABLE_NAME + "(userId)");
        }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        openIDIndexDao.truncate((Postgres9Connection) con);
    }

    @Override
    public void put(PartakeConnection con, OpenIDLinkage linkage) throws DAOException {
        openIDIndexDao.put((Postgres9Connection) con, new String[] { "openId", "userId" }, new String[] { linkage.getId(), linkage.getUserId() });
    }

    @Override
    public OpenIDLinkage find(PartakeConnection con, String id) throws DAOException {
        String userId = openIDIndexDao.find((Postgres9Connection) con, "userId", "openId", id);
        if (userId == null)
            return null;
        
        return new OpenIDLinkage(id, userId).freeze();
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        openIDIndexDao.remove((Postgres9Connection) con, "openId", id);
    }

    @Override
    public DataIterator<OpenIDLinkage> getIterator(PartakeConnection con) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> findByUserId(PartakeConnection con, String userId) throws DAOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
