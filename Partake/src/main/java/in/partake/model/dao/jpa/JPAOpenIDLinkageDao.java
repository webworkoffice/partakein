package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.IOpenIDLinkageAccess;
import in.partake.model.dao.PartakeConnection;

class JPAOpenIDLinkageDao extends JPADao implements IOpenIDLinkageAccess {

    @Override
    public void addOpenID(PartakeConnection con, String identifier, String userId) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getUserId(PartakeConnection con, String identifier) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeOpenID(PartakeConnection con, String identifier, String userId) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM OpenIDLinkage");
        q.executeUpdate();   
    }
}
