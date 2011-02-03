package in.partake.model.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
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
        OpenIDLinkage linkage = find(con, identifier, OpenIDLinkage.class);
        if (linkage == null) { return null; }
        return linkage.getUserId();
    }

    @Override
    public void removeOpenID(PartakeConnection con, String identifier) throws DAOException {
        remove(con, identifier, OpenIDLinkage.class);
    }
    
    @Override
    public List<String> getOpenIDIdentifiers(PartakeConnection con, String userId) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT oil FROM OpenIDLinkages oil WHERE oil.userId = :userId");
        q.setParameter("userId", userId);
        
        @SuppressWarnings("unchecked")
        List<OpenIDLinkage> linkages = q.getResultList();
        
        List<String> results = new ArrayList<String>();
        for (OpenIDLinkage linkage : linkages) {
            results.add(linkage.getId());
        }
        
        return results;
    }    

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("DELETE FROM OpenIDLinkages");
        q.executeUpdate();   
    }
}
