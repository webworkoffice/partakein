package in.partake.model.dao.cassandra;

import in.partake.model.dao.DAOException;

import org.apache.cassandra.thrift.ColumnOrSuperColumn;

/**
 * O/R Mapper for CassandraDAO. 
 * @author shinyak
 */
// TODO: temporarily public. should be package local.
abstract class ColumnOrSuperColumnMapper<T> {
    private CassandraDAOFactory factory;
    
    public ColumnOrSuperColumnMapper(CassandraDAOFactory factory) {
        this.factory = factory;
    }
    
	/**
	 * map the ColumnOrSuperColumn to an object.
	 * @param cosc
	 * @return
	 */
	public abstract T map(ColumnOrSuperColumn cosc) throws DAOException;
	
	/**
	 * reversely map the object to a ColumnOrSuperColumn object.
	 * @param t
	 * @return
	 */
	public abstract ColumnOrSuperColumn unmap(T t) throws DAOException;

	/**
	 * get a factory.
	 * @return
	 */
	protected CassandraDAOFactory getFactory() {
	    return factory;
	}
}
