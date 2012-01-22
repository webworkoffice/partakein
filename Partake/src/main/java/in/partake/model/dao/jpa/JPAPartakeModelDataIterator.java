package in.partake.model.dao.jpa;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.persistence.EntityManager;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dto.PartakeModel;

public class JPAPartakeModelDataIterator<T extends PartakeModel<T>> extends DataIterator<T> {
    private EntityManager em;
    private Class<T> clazz;
    private T current;
    private Iterator<T> it;
    private boolean allowsUpdate;

    public JPAPartakeModelDataIterator(EntityManager em, List<T> list, Class<T> clazz, boolean allowsUpdate) {
        this.em = em;
        this.clazz = clazz;
        this.current = null;
        this.it = list.iterator();
        this.allowsUpdate = allowsUpdate;
    }
    
    @Override
    public boolean hasNext() throws DAOException {
        return it.hasNext();
    }

    @Override
    public T next() throws DAOException {
        if (hasNext()) {
            return current = it.next();
        } else {
            current = null;
            throw new NoSuchElementException();
        }
    }
    
    @Override
    public void close() {
    }

    @Override
    public void remove() throws DAOException, UnsupportedOperationException {
        if (!allowsUpdate) { throw new UnsupportedOperationException(); }

        if (current != null) {
            T t = em.find(clazz, current.getPrimaryKey());
            if (t != null) { em.remove(t); }
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void update(T t) throws DAOException, UnsupportedOperationException {
        if (!allowsUpdate) { throw new UnsupportedOperationException(); }

        if (current == null) {
            throw new IllegalStateException();
        } else if (!current.getPrimaryKey().equals(t.getPrimaryKey())) {
            throw new IllegalStateException();
        } else {
            T persisted = em.find(clazz, current.getPrimaryKey());
            if (persisted != null) {
                em.detach(persisted);
                em.merge(t);                
            } else {
                em.merge(t);
            }
            
        }
    }

    
}
