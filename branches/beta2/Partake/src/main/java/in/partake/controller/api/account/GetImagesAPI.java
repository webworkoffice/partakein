package in.partake.controller.api.account;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.EventService;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Retrieves images which is uploaded by owners.
 * 
 * @author shinyak
 *
 * Note that this may contain images someone uploaded if an event editor uploaded it.
 * TODO: BinaryData should have userId.
 */
public class GetImagesAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();

        int offset = getIntegerParameter("offset");
        if (offset < 0)
            offset = 0;
        int limit = getIntegerParameter("limit");
        if (limit < 0)
            limit = 0;
        if (100 < limit)
            limit = 100;
        
        List<String> imageIds = EventService.get().getImageIds(user.getId(), offset, limit);
        JSONArray array = new JSONArray();
        for (String imageId : imageIds)
            array.add(imageId);

        JSONObject obj = new JSONObject();
        obj.put("imageIds", array);
        return renderOK(obj);
    }    
}
