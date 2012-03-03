package in.partake.controller.api.image;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;

/**
 * 
 * @author shinyak
 *
 */
public class CreateImagesAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        return renderOK();
    }
}
