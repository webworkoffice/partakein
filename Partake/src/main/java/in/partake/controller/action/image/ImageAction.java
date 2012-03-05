package in.partake.controller.action.image;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.model.dto.BinaryData;

import java.io.ByteArrayInputStream;

public class ImageAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;
    static final String IMAGE_ID_PARAM_NAME = "imageId";

    public String doExecute() throws DAOException, PartakeException {
        String imageId = getValidImageIdParameter();
        
        BinaryData data = DeprecatedEventDAOFacade.get().getBinaryData(imageId);
        if (data == null)
            return renderNotFound();

        return renderInlineStream(new ByteArrayInputStream(data.getData()), data.getType());
    }
}
