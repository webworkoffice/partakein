package in.partake.model.daofacade;

import in.partake.model.dao.PartakeDAOFactory;

public abstract class AbstractPartakeDAOFacade {
    protected static PartakeDAOFactory factory;

    public static void setFactory(PartakeDAOFactory factory) {
        AbstractPartakeDAOFacade.factory = factory;
    }
}
