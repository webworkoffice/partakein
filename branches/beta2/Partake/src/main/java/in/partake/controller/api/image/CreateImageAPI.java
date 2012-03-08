package in.partake.controller.api.image;

import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IImageAccess;
import in.partake.model.dao.base.Transaction;
import in.partake.model.dto.ImageData;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
import in.partake.service.DBService;

import java.io.File;
import java.io.IOException;

import net.sf.json.JSONObject;

/**
 * 
 * @author shinyak
 *
 */
public class CreateImageAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    private File file;
    private String contentType;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        if (!checkCSRFToken())
            return renderInvalid(UserErrorCode.INVALID_SECURITY_CSRF);

        if (file == null || contentType == null)
            return renderInvalid(UserErrorCode.INVALID_NOIMAGE);

        // IE sends jpeg file using contentType = "image/pjpeg". We should handle this here.
        if ("image/pjpeg".equals(contentType))
            contentType = "image/jpeg";

        if (!Util.isImageContentType(contentType))
            return renderInvalid(UserErrorCode.INVALID_IMAGE_CONTENTTYPE);

        String imageId = new CreateImageAPITransaction(user, file, contentType).transaction();

        JSONObject obj = new JSONObject();
        obj.put("imageId", imageId);
        return renderOK(obj);
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setFileContentType(String contentType) {
        this.contentType = contentType;
    }
}

class CreateImageAPITransaction extends Transaction<String> {
    private UserEx user;
    private File file;
    private String contentType;


    CreateImageAPITransaction(UserEx user, File file, String contentType) {
        this.user = user;
        this.file = file;
        this.contentType = contentType;
    }

    // TODO: We should not load image in memory here. However, sending image from DB directly will cause
    // another problem, e.g. DDOS.
    public String doTransaction(PartakeConnection con) throws DAOException, PartakeException {
        IImageAccess dao = DBService.getFactory().getImageAccess();

        byte[] foreImageByteArray;
        try {
            foreImageByteArray = Util.getContentOfFile(file);
        } catch (IOException e) {
            throw new PartakeException(ServerErrorCode.ERROR_IO);
        }

        String imageId = dao.getFreshId(con); 
        ImageData imageEmbryo = new ImageData(imageId, user.getId(), contentType, foreImageByteArray, TimeUtil.getCurrentDate());
        dao.put(con, imageEmbryo);

        return imageId;
    }
}
