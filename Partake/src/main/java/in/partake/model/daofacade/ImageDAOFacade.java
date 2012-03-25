package in.partake.model.daofacade;

import org.apache.commons.lang.StringUtils;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.ImageData;
import in.partake.model.dto.User;
import in.partake.service.DBService;

public class ImageDAOFacade {
    public static boolean checkImageOwner(PartakeConnection con, String imageId, User user) throws DAOException {
        // TODO: We don't need to get all image from DB.
        ImageData data = DBService.getFactory().getImageAccess().find(con, imageId);
        if (data == null)
            return false;
        
        return StringUtils.equals(user.getId(), data.getUserId()); 
    }
}
