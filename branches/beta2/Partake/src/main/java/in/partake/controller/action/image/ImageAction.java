package in.partake.controller.action.image;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserImage;

import java.io.ByteArrayInputStream;

public class ImageAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;
    static final String IMAGE_ID_PARAM_NAME = "imageId";

    public String doExecute() throws DAOException, PartakeException {
        String imageId = getValidImageIdParameter();

        UserImage image = new ImageTransaction(imageId).execute();
        if (image == null)
            return renderNotFound();

        return renderInlineStream(new ByteArrayInputStream(image.getData()), image.getType());
    }
}

class ImageTransaction extends DBAccess<UserImage> {
    private String imageId;

    public ImageTransaction(String imageId) {
        this.imageId = imageId;
    }

    @Override
    protected UserImage doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        return daos.getImageAccess().find(con, imageId);
    }
}