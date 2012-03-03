package in.partake.controller.action.image;

import in.partake.base.Util;
import in.partake.controller.PartakeActionSupport;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.BinaryData;
import in.partake.resource.UserErrorCode;
import in.partake.service.EventService;

import java.io.ByteArrayInputStream;

public class ImageAction extends PartakeActionSupport {
    private static final long serialVersionUID = 1L;
    // private static final Logger logger = Logger.getLogger(ImageAction.class);
    static final String IMAGE_ID_PARAM_NAME = "imageId";

    public String execute() throws DAOException {
        String imageId = getParameter(IMAGE_ID_PARAM_NAME);

        if (imageId == null)
            return renderInvalid(UserErrorCode.MISSING_IMAGEID); 
        if (!Util.isUUID(imageId))
            return renderInvalid(UserErrorCode.INVALID_IMAGEID);
        
        BinaryData data = EventService.get().getBinaryData(imageId);
        if (data == null)
            return renderNotFound();

        return renderInlineStream(new ByteArrayInputStream(data.getData()), data.getType());
    }
}
