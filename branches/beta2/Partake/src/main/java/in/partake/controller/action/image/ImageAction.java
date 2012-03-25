package in.partake.controller.action.image;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.base.Transaction;
import in.partake.model.dto.BinaryData;
import in.partake.model.dto.ImageData;
import in.partake.resource.ServerErrorCode;
import in.partake.service.DBService;

import java.io.ByteArrayInputStream;

public class ImageAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;
    static final String IMAGE_ID_PARAM_NAME = "imageId";

    public String doExecute() throws DAOException, PartakeException {
        String imageId = getValidImageIdParameter();
        
        Object data = new ImageTransaction(imageId).execute();
        if (data == null)
            return renderNotFound();

        if (data instanceof ImageData) {
            ImageData image = (ImageData) data;
            return renderInlineStream(new ByteArrayInputStream(image.getData()), image.getType());
        }
        
        if (data instanceof BinaryData) {
            BinaryData image = (BinaryData) data;
            return renderInlineStream(new ByteArrayInputStream(image.getData()), image.getType());            
        }
        
        return renderError(ServerErrorCode.LOGIC_ERROR);
    }
}

class ImageTransaction extends Transaction<Object> {
    private String imageId;
    
    public ImageTransaction(String imageId) {
        this.imageId = imageId;
    }
    
    @Override
    protected Object doExecute(PartakeConnection con) throws DAOException, PartakeException {
        ImageData image = DBService.getFactory().getImageAccess().find(con, imageId);
        if (image != null)
            return image;
        
        // TODO: Some old images may be stored as BinaryData. We should convert them later.
        BinaryData binary = DBService.getFactory().getBinaryAccess().find(con, imageId);
        return binary;
    }
}