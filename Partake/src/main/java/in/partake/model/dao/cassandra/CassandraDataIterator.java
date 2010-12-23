package in.partake.model.dao.cassandra;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;

import org.apache.cassandra.thrift.ColumnOrSuperColumn;

class CassandraDataIterator<T> extends DataIterator<T> {
	private ColumnIterator iterator;
	private ColumnOrSuperColumnMapper<T> mapper;
	
	CassandraDataIterator(ColumnIterator iterator, ColumnOrSuperColumnMapper<T> mapper) {
		this.iterator = iterator;
		this.mapper = mapper;
	}

	public boolean hasNext() throws DAOException {
		return iterator.hasNext();
	}
	
	public T next() throws DAOException { 
		ColumnOrSuperColumn cosc = iterator.next();
		return mapper.map(cosc);
	}
	
	public void remove() throws DAOException {
		iterator.remove();
	}
	
	public void update(T t) throws DAOException {
		iterator.update(mapper.unmap(t));
	}
}
