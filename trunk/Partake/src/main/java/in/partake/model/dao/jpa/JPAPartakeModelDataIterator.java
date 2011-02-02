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
    private T current;
    private Iterator<T> it;
    private boolean allowsUpdate;

    public JPAPartakeModelDataIterator(EntityManager em, List<T> list, boolean allowsUpdate) {
        this.em = em;
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
    public void remove() throws DAOException, UnsupportedOperationException {
        if (!allowsUpdate) { throw new UnsupportedOperationException(); }

        if (current == null) {
            em.remove(current.getPrimaryKey());
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
            em.merge(t);
        }
    }

    
}
