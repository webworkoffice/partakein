package in.partake.model.dao;

import in.partake.model.dto.PartakeModel;

/**
 * 全ての DAO が兼ね備えるべき、put, find, remove を提供する層。
 * TODO: 全ての Access Interface が IAccess を継承するようにする。
 * 
 * @author shinyak
 *
 * @param <T> Data type
 * @param <PK> primary key type. usually String, but it may differ.
 */
public interface IAccess<T extends PartakeModel<T>, PK> extends ITruncatable {
    public abstract void put(PartakeConnection con, T t) throws DAOException;
    public abstract T find(PartakeConnection con, PK key) throws DAOException;
    public abstract void remove(PartakeConnection con, PK key) throws DAOException;
    public abstract DataIterator<T> getIterator(PartakeConnection con) throws DAOException;
}
