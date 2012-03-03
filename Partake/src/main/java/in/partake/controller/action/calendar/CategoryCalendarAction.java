package in.partake.controller.action.calendar;

import in.partake.model.dao.DAOException;
import in.partake.model.dto.auxiliary.EventCategory;

import org.apache.commons.lang.StringUtils;


public class CategoryCalendarAction extends AbstractCalendarAction {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException {
        String categoryName = getParameter("category");
        if (StringUtils.isEmpty(categoryName))
            return renderNotFound();

        if (!EventCategory.isValidCategoryName(categoryName))
            return renderNotFound();

        return showByCategory(categoryName);
    }
    

}
