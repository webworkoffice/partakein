package in.partake.model.dao.jpa;

import java.util.List;

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
        Query q = em.createQuery("SELECT data FROM ShortenedURLData data WHERE data.originalURL = :ourl");
        q.setParameter("ourl", originalURL);

        List<?> objs = q.getResultList();
        if (objs.isEmpty()) { return null; }
        
        Object obj = objs.get(0);
        if (obj == null) { return null; }
        
        ShortenedURLData data = (ShortenedURLData) obj;
        return data.getShortenedURL();
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM ShortenedURLData");
        q.executeUpdate();
    }
}
