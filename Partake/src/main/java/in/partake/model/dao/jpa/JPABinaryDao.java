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

        while (true) {
            String key = UUID.randomUUID().toString();
            EntityManager em = jcon.getEntityManager();
            
            CacheData cd = em.find(CacheData.class, key);
            if (cd == null) { return key; }
            // otherwise, generate another key.
        }
    }

    @Override
    public void addBinaryWithId(PartakeConnection con, String id, BinaryData data) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public BinaryData getBinaryById(PartakeConnection con, String id) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeBinary(PartakeConnection con, String id) throws DAOException {
        // TODO Auto-generated method stub
        
    }

}
