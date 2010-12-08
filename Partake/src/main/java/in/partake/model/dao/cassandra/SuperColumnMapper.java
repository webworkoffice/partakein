package in.partake.model.dao.cassandra;

import org.apache.cassandra.thrift.SuperColumn;

// temporarily public.
@Deprecated
public interface SuperColumnMapper<T> {
	public T map(SuperColumn superColumn);
	public SuperColumn unmap(T t);
}

