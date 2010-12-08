package in.partake.model.dao;


public abstract class Mapper<S, T> {
    private PartakeDAOFactory factory;
    
    protected Mapper(PartakeDAOFactory factory) {
        this.factory = factory;
    }
    
    public abstract T map(S s) throws DAOException;
    
    protected PartakeDAOFactory getFactory() {
        return factory;
    }
}
