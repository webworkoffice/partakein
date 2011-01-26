package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IURLShortenerAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.ShortenedURLData;
import in.partake.model.dto.pk.ShortenedURLDataPK;

class JPAURLShortenerDao extends JPADao implements IURLShortenerAccess {

    @Override
    public void addShortenedURL(PartakeConnection con, String originalURL, String serviceType, String shortenedURL) throws DAOException {
        ShortenedURLData data = new ShortenedURLData(originalURL, serviceType, shortenedURL);
        
        EntityManager em = getEntityManager(con);
        em.persist(data);
    }

    @Override
    public String getShortenedURL(PartakeConnection con, String originalURL, String serviceType) throws DAOException {
        EntityManager em = getEntityManager(con);
        ShortenedURLData data = em.find(ShortenedURLData.class, new ShortenedURLDataPK(originalURL, serviceType));
        
        if (data != null) {
            return data.getShortenedURL();
        } else {
            return null;
        }
    }

    @Override
    public String getShortenedURL(PartakeConnection con, String originalURL) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT surl FROM shortenedurldata surl where originalurl := ?");
        q.setParameter(1, originalURL);
        
        throw new RuntimeException("not implemented yet.");
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        throw new RuntimeException("not implemented yet.");
    }
}
