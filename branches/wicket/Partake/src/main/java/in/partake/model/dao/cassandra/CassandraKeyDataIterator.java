package in.partake.model.dao.cassandra;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;

class CassandraKeyDataIterator<T> extends DataIterator<T> {
    private CassandraKeyIterator iterator;
	private KeyMapper<T> mapper;
	
	CassandraKeyDataIterator(CassandraKeyIterator iterator, KeyMapper<T> mapper) {
		this.iterator = iterator;
		this.mapper = mapper;
	}

	public boolean hasNext() throws DAOException {
		return iterator.hasNext();
	}
	
	public T next() throws DAOException {
	    String key = iterator.next();
	    return mapper.map(key);
	}
	
	public void remove() throws DAOException {
	    throw new UnsupportedOperationException();
	}
	
	public void update(T t) throws DAOException {
	    throw new UnsupportedOperationException();
	}
}
