package in.partake.controller.action.calendar;

import in.partake.base.PartakeException;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.auxiliary.EventCategory;


public class AllCalendarAction extends AbstractCalendarAction {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        return showByCategory(EventCategory.getAllEventCategory());
    }
}
