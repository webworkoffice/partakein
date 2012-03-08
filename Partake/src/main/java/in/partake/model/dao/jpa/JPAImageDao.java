package in.partake.model.dao.jpa;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IImageAccess;
import in.partake.model.dto.ImageData;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;


public class JPAImageDao extends JPADao<ImageData> implements IImageAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, ImageData.class);
    }

    @Override
    public void put(PartakeConnection con, ImageData data) throws DAOException {
        putImpl(con, data, ImageData.class);
    }

    @Override
    public ImageData find(PartakeConnection con, String id) throws DAOException {
        return findImpl(con, id, ImageData.class);
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        removeImpl(con, id, ImageData.class);
    }
    
    @Override
    public DataIterator<ImageData> getIterator(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT t FROM ImageData t");
        
        @SuppressWarnings("unchecked")
        List<ImageData> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<ImageData>(em, list, ImageData.class, false);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM ImageData");
        q.executeUpdate();
    }
    
    @Override
    public long count(PartakeConnection con) throws DAOException {
        return countImpl(con, "ImageData");
    }
    
    @Override
    public List<String> findIdsByUserId(PartakeConnection con, String userId, int offset, int limit) throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }
    
    @Override
    public int countByUserId(PartakeConnection con, String userId) throws DAOException {
        throw new RuntimeException("Not implemented yet");
    }
}
