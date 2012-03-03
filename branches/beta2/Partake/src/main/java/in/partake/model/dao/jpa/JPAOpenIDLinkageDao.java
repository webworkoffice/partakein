package in.partake.model.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IOpenIDLinkageAccess;
import in.partake.model.dto.OpenIDLinkage;

class JPAOpenIDLinkageDao extends JPADao<OpenIDLinkage> implements IOpenIDLinkageAccess {

    @Override
    public void put(PartakeConnection con, OpenIDLinkage linkage) throws DAOException {
        putImpl(con, linkage, OpenIDLinkage.class);
    }

    @Override
    public OpenIDLinkage find(PartakeConnection con, String identifier) throws DAOException {
        return findImpl(con, identifier, OpenIDLinkage.class);
    }

    @Override
    public void remove(PartakeConnection con, String identifier) throws DAOException {
        removeImpl(con, identifier, OpenIDLinkage.class);
    }
    
    @Override
    public DataIterator<OpenIDLinkage> getIterator(PartakeConnection con) throws DAOException {
        EntityManager em = getEntityManager(con);
        Query q = em.createQuery("SELECT t FROM OpenIDLinkages t");
        
        @SuppressWarnings("unchecked")
        List<OpenIDLinkage> list = q.getResultList();
        
        return new JPAPartakeModelDataIterator<OpenIDLinkage>(em, list, OpenIDLinkage.class, false);
    }
    
    @Override
    public List<String> findByUserId(PartakeConnection con, String userId) throws DAOException {
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
