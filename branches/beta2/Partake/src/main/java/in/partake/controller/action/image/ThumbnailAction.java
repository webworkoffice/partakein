package in.partake.controller.action.image;

import in.partake.base.ImageUtil;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.ImageData;
import in.partake.model.dto.ThumbnailData;
import in.partake.resource.ServerErrorCode;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

public class ThumbnailAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;
    static final String IMAGE_ID_PARAM_NAME = "imageId";

    public String doExecute() throws DAOException, PartakeException {
        String imageId = getValidImageIdParameter();

        ThumbnailData data = new ThumbnailAccess(imageId).execute();
        if (data != null)
            return renderInlineStream(new ByteArrayInputStream(data.getData()), data.getType());

        // If not found, we will generate a thumbnail.
        ThumbnailData created = new ThumbnailTransaction(imageId).execute();
        if (created != null)
            return renderInlineStream(new ByteArrayInputStream(created.getData()), created.getType());

        return renderNotFound();
    }
}

class ThumbnailAccess extends DBAccess<ThumbnailData> {
    private String imageId;

    public ThumbnailAccess(String imageId) {
        this.imageId = imageId;
    }

    @Override
    protected ThumbnailData doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        ThumbnailData thumbnail = daos.getThumbnailAccess().find(con, imageId);
        if (thumbnail != null)
            return thumbnail;

        return null;
    }
}

class ThumbnailTransaction extends Transaction<ThumbnailData> {
    private String imageId;

    public ThumbnailTransaction(String imageId) {
        this.imageId = imageId;
    }

    @Override
    protected ThumbnailData doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        try {
            ImageData image = daos.getImageAccess().find(con, imageId);
            if (image == null)
                return null;

            InputStream is = new ByteArrayInputStream(image.getData());
            BufferedImage converted = ImageUtil.createThumbnail(ImageIO.read(is), 320, 240);
            is.close();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(converted, "png", os);
            os.close();

            ThumbnailData thumbnail = new ThumbnailData(image.getId(), image.getUserId(), "image/png", os.toByteArray(), TimeUtil.getCurrentDateTime());
            daos.getThumbnailAccess().put(con, thumbnail);

            return thumbnail;
        } catch (IOException e) {
            throw new PartakeException(ServerErrorCode.ERROR_IO);
        }
    }
}
