package in.partake.model.dao.postgres9.impl;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IURLShortenerAccess;
import in.partake.model.dao.postgres9.Postgres9Dao;
import in.partake.model.dao.postgres9.Postgres9EntityDao;
import in.partake.model.dto.ShortenedURLData;
import in.partake.model.dto.pk.ShortenedURLDataPK;

public class Postgres9UrlShortenerDao extends Postgres9Dao implements IURLShortenerAccess {
    static final String TABLE_NAME = "URLShortenerEntities";
    static final int CURRENT_VERSION = 1;

    private final Postgres9EntityDao entityDao;

    public Postgres9UrlShortenerDao() {
        this.entityDao = new Postgres9EntityDao(TABLE_NAME);
    }

    @Override
    public void initialize(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void put(PartakeConnection con, ShortenedURLData t) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ShortenedURLData find(PartakeConnection con, ShortenedURLDataPK key) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove(PartakeConnection con, ShortenedURLDataPK key) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public DataIterator<ShortenedURLData> getIterator(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ShortenedURLData findByURL(PartakeConnection con, String originalURL) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeByURL(PartakeConnection con, String originalURL) throws DAOException {
        // TODO Auto-generated method stub
        
    }

}
