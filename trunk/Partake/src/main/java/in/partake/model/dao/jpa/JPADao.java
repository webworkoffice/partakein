package in.partake.model.dao.jpa;

import java.util.UUID;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.PartakeModel;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

abstract class JPADao {
    private static final Logger logger = Logger.getLogger(JPADao.class);

    protected EntityManager getEntityManager(PartakeConnection con) {
        return ((JPAConnection) con).getEntityManager();
    }
    
    protected <T extends PartakeModel<T>> String getFreshIdImpl(PartakeConnection con, Class<T> clazz) throws DAOException {
        JPAConnection jcon = (JPAConnection) con;

        String key = null;
        T obj = null;
        EntityManager em = jcon.getEntityManager();

        int tryCount = 0;
        do {
            key = UUID.randomUUID().toString();
            obj = em.find(clazz, key);
        } while (obj != null && tryCount++ < 5);

        // if the object is not null, this means that fresh id cannot be taken.
        if (obj != null) {
            logger.error("fresh id cannot be taken.");
            return null;
        } else {
            return key;
        }
    }
    
    protected <T extends PartakeModel<T>> T freeze(T t) {
        if (t == null) { return null; }
        return t.freeze();
    }
}
