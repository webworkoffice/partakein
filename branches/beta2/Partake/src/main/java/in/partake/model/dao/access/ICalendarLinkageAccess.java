package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.CalendarLinkage;


/**
 * @author shinyak
 */
public interface ICalendarLinkageAccess extends IAccess<CalendarLinkage, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;
    public CalendarLinkage findByUserId(PartakeConnection con, String userId) throws DAOException;
}
