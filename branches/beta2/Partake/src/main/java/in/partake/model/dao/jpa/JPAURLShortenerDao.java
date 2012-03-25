package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IURLShortenerAccess;
import in.partake.model.dto.ShortenedURLData;
import in.partake.model.dto.pk.ShortenedURLDataPK;

class JPAURLShortenerDao extends JPADao<ShortenedURLData> implements IURLShortenerAccess {

    @Override
    public void put(PartakeConnection con, ShortenedURLData data) throws DAOException {
        putImpl(con, new ShortenedURLData(data), ShortenedURLData.class);
    }

    @Override
    public ShortenedURLData find(PartakeConnection con, ShortenedURLDataPK pk) throws DAOException {
        return findImpl(con, pk, ShortenedURLData.class);
    }
        
    @Override
    public void remove(PartakeConnection con, ShortenedURLDataPK pk) throws DAOException {
        removeImpl(con, pk, ShortenedURLData.class);
    }
    
    @Override
    public DataIterator<ShortenedURLData> getIterator(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT t FROM ShortenedURLData t");
        
        @SuppressWarnings("unchecked")
        List<ShortenedURLData> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<ShortenedURLData>(em, list, ShortenedURLData.class, false);        
    }
    
    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM ShortenedURLData");
        q.executeUpdate();
    }
    

    @Override
    public ShortenedURLData findByURL(PartakeConnection con, String originalURL) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT data FROM ShortenedURLData data WHERE data.originalURL = :ourl");
        q.setParameter("ourl", originalURL);
        
        @SuppressWarnings("unchecked")
        List<ShortenedURLData> objs = q.getResultList();
        if (objs.isEmpty()) { return null; }
        
        return objs.get(0);
    }

    @Override
    public void removeByURL(PartakeConnection con, String originalURL) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT data FROM ShortenedURLData as data WHERE data.originalURL = :ourl");
        q.setParameter("ourl", originalURL);
        @SuppressWarnings("unchecked")
        List<ShortenedURLData> list = (List<ShortenedURLData>) q.getResultList();
        
        // TODO: delete で消すと cache から消えないんだよなあ...
        
        for (ShortenedURLData data : list) {
            em.remove(data);
        }
    }
    
    @Override
    public int count(PartakeConnection con) throws DAOException {
        return countImpl(con, "ShortenedURLData");
    }

}
