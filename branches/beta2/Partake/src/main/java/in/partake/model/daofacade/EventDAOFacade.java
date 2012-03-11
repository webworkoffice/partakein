package in.partake.model.daofacade;

import in.partake.model.EventEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.deprecated.DeprecatedPartakeDAOFacadeUtils;
import in.partake.service.DBService;

public class EventDAOFacade {    
    public static EventEx getEventEx(PartakeConnection con, String eventId) throws DAOException {
        return DeprecatedPartakeDAOFacadeUtils.getEventEx(con, DBService.getFactory(), eventId);
    }
}
