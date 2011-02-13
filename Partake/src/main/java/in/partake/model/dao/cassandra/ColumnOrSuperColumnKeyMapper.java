package in.partake.model.dao.cassandra;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeDAOFactory;

import org.apache.cassandra.thrift.ColumnOrSuperColumn;

/**
 * O/R Mapper for CassandraDAO. 
 * @author shinyak
 */
abstract class ColumnOrSuperColumnKeyMapper<T> {
    protected PartakeDAOFactory factory;
    protected CassandraConnection connection;
    
    public ColumnOrSuperColumnKeyMapper(CassandraConnection connection, PartakeDAOFactory factory) {
        this.connection = connection;
        this.factory = factory;
    }
    
	/**
	 * map the ColumnOrSuperColumn to an object.
	 * @param cosc
	 * @return
	 */
	public abstract T map(ColumnOrSuperColumn cosc, String key) throws DAOException;
	
	/**
	 * reversely map the object to a ColumnOrSuperColumn object.
	 * @param t
	 * @return
	 */
	public abstract ColumnOrSuperColumn unmap(T t) throws DAOException;
}
