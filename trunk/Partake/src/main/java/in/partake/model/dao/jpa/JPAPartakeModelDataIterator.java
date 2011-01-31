package in.partake.model.dao.jpa;

import java.util.List;
import java.util.NoSuchElementException;

import javax.persistence.EntityManager;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dto.PartakeModel;

public class JPAPartakeModelDataIterator<T extends PartakeModel<T>> extends DataIterator<T> {
    private EntityManager em;
    private T current;
    private List<T> list;
    private int pos;

    public JPAPartakeModelDataIterator(EntityManager em, List<T> list) {
        this.em = em;
        this.current = null;
        this.list = list;
        this.pos = 0;
    }
    
    @Override
    public boolean hasNext() throws DAOException {
        return (list != null && pos < list.size());
    }

    @Override
    public T next() throws DAOException {
        if (hasNext()) {
            return current = list.get(pos++);
        } else {
            current = null;
            throw new NoSuchElementException();
        }
    }

    @Override
    public void remove() throws DAOException, UnsupportedOperationException {
        if (current == null) {
            em.remove(current.getPrimaryKey());
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void update(T t) throws DAOException, UnsupportedOperationException {
        if (current == null) {
            throw new IllegalStateException();
        } else if (!current.getPrimaryKey().equals(t.getPrimaryKey())) {
            throw new IllegalStateException();
        } else {
            em.merge(t);
        }
    }

    
}
