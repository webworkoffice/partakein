package in.partake.model.dao.jpa;

import java.util.UUID;

import javax.persistence.EntityManager;

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
    public void addBinaryWithId(PartakeConnection con, String id, BinaryData data) throws DAOException {
        EntityManager em = getEntityManager(con);
        
        data.setId(id);
        em.persist(data);
    }

    @Override
    public BinaryData getBinaryById(PartakeConnection con, String id) throws DAOException {
        EntityManager em = getEntityManager(con);        
        return em.find(BinaryData.class, id).freeze();
    }

    @Override
    public void removeBinary(PartakeConnection con, String id) throws DAOException {
        EntityManager em = getEntityManager(con);
        em.remove(em.find(BinaryData.class, id));        
    }

}
