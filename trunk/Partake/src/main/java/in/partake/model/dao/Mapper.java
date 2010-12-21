package in.partake.model.dao;


public abstract class Mapper<S, T> {
    private PartakeModelFactory factory;
    
    protected Mapper(PartakeModelFactory factory) {
        this.factory = factory;
    }
    
    public abstract T map(S s) throws DAOException;
    
    protected PartakeModelFactory getFactory() {
        return factory;
    }
}
