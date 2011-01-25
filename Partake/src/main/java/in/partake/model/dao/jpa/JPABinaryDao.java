package in.partake.model.dao.jpa;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IBinaryAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.BinaryData;
import in.partake.model.dto.CacheData;

public class JPABinaryDao extends JPADao implements IBinaryAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        JPAConnection jcon = (JPAConnection) con;

        String key = null;
        CacheData cd = null;
        do {
            key = UUID.randomUUID().toString();
            EntityManager em = jcon.getEntityManager();
            
            cd = em.find(CacheData.class, key);
        } while (cd != null);
        
        assert(key != null);
        assert(cd == null);
        
        return key;
    }

    @Override
    public void addBinary(PartakeConnection con, BinaryData data) throws DAOException {
        if (data.getId() == null) { throw new DAOException("id should be specified."); }
        EntityManager em = getEntityManager(con);
        em.persist(data);
    }

    @Override
    public BinaryData getBinary(PartakeConnection con, String id) throws DAOException {
        EntityManager em = getEntityManager(con);   
        BinaryData data = em.find(BinaryData.class, id);
        if (data != null) {
            return data.freeze();
        } else {
            return null;
        }        
    }

    @Override
    public void removeBinary(PartakeConnection con, String id) throws DAOException {
        EntityManager em = getEntityManager(con);
        BinaryData data = em.find(BinaryData.class, id);
        if (data != null) { em.remove(data); }
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createNativeQuery("truncate binarydata");
        q.executeUpdate();
    }
}
