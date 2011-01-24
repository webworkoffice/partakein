package in.partake.model.dao.jpa;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IURLShortenerAccess;
import in.partake.model.dao.PartakeConnection;

class JPAURLShortenerDao extends JPADao implements IURLShortenerAccess {

    @Override
    public void addShortenedURL(PartakeConnection con, String originalURL, String type, String shortenedURL) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getShortenedURL(PartakeConnection con, String originalURL, String type) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getShortenedURL(PartakeConnection con, String originalURL) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

}
