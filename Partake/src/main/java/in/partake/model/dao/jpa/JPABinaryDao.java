package in.partake.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IBinaryAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.BinaryData;


public class JPABinaryDao extends JPADao<BinaryData> implements IBinaryAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, BinaryData.class);
    }

    @Override
    public void put(PartakeConnection con, BinaryData data) throws DAOException {
        createOrUpdate(con, data, BinaryData.class);
    }

    @Override
    public BinaryData find(PartakeConnection con, String id) throws DAOException {
        return find(con, id, BinaryData.class);
    }

    @Override
    public void remove(PartakeConnection con, String id) throws DAOException {
        remove(con, id, BinaryData.class);
    }
    
    @Override
    public DataIterator<BinaryData> getIterator(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT t FROM BinaryData t");
        
        @SuppressWarnings("unchecked")
        List<BinaryData> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<BinaryData>(em, list, BinaryData.class, false);
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM BinaryData");
        q.executeUpdate();
    }
}
