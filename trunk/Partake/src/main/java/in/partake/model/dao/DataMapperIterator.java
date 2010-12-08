package in.partake.model.dao;


public class DataMapperIterator<S, T> extends DataIterator<T> {
    private DataIterator<S> iterator;
    private Mapper<S, T> mapper;
    
    public DataMapperIterator(DataIterator<S> iterator, Mapper<S, T> mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }
    
    @Override
    public boolean hasNext() throws DAOException {
        return iterator.hasNext();
    }

    @Override
    public T next() throws DAOException {
        return mapper.map(iterator.next());
    }

    @Override
    public void remove() throws DAOException {
        iterator.remove();
    }

    @Override
    public void update(T t) throws DAOException {
        throw new UnsupportedOperationException();
    }

}
