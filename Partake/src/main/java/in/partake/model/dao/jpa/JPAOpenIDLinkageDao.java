package in.partake.model.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IOpenIDLinkageAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.OpenIDLinkage;

class JPAOpenIDLinkageDao extends JPADao<OpenIDLinkage> implements IOpenIDLinkageAccess {

    @Override
    public void addOpenID(PartakeConnection con, String identifier, String userId) throws DAOException {
        createOrUpdate(con, new OpenIDLinkage(identifier, userId), OpenIDLinkage.class);
    }

    @Override
    public String getUserId(PartakeConnection con, String identifier) throws DAOException {
        EntityManager em = getEntityManager(con);
        OpenIDLinkage linkage = em.find(OpenIDLinkage.class, identifier);
        if (linkage == null) { return null; }
        return linkage.getUserId();
    }

    @Override
    public void removeOpenID(PartakeConnection con, String identifier) throws DAOException {
        EntityManager em = getEntityManager(con);
        OpenIDLinkage linkage = em.find(OpenIDLinkage.class, identifier);
        if (linkage == null) { return; }
        em.remove(linkage);        
    }
    
    @Override
    public DataIterator<String> getOpenIDIdentifiers(PartakeConnection con, String userId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }    

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM OpenIDLinkages");
        q.executeUpdate();   
    }
}
