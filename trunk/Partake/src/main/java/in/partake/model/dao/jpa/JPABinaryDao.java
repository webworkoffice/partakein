package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IBinaryAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.BinaryData;


public class JPABinaryDao extends JPADao implements IBinaryAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        return getFreshIdImpl(con, BinaryData.class);
    }

    @Override
    public void addBinary(PartakeConnection con, BinaryData data) throws DAOException {
        if (data.getId() == null) { throw new DAOException("id should be specified."); }
        EntityManager em = getEntityManager(con);
        em.persist(new BinaryData(data));
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
        Query q = em.createQuery("DELETE FROM BinaryData");
        q.executeUpdate();
    }
}
