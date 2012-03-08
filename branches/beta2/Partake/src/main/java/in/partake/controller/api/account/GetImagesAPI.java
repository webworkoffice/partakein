package in.partake.controller.api.account;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.base.Transaction;
import in.partake.service.DBService;

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

        GetImagesTransaction transaction = new GetImagesTransaction(user, offset, limit);
        transaction.execute();
        
        JSONArray imageIds = new JSONArray();
        for (String imageId : transaction.getImageIds())
            imageIds.add(imageId);

        JSONObject obj = new JSONObject();
        obj.put("imageIds", imageIds);
        obj.put("count", transaction.getCountImages());
        return renderOK(obj);
    }    
}

class GetImagesTransaction extends Transaction<Void> {
    private UserEx user;
    private int offset;
    private int limit; 

    private List<String> imageIds;
    private int countImages;
    
    public GetImagesTransaction(UserEx user, int offset, int limit) {
        this.user = user;
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    protected Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
        PartakeDAOFactory factory = DBService.getFactory();
        
        this.imageIds = factory.getImageAccess().findIdsByUserId(con, user.getId(), offset, limit);
        this.countImages = factory.getImageAccess().countByUserId(con, user.getId());
        return null;
    }
    
    public List<String> getImageIds() {
        return this.imageIds;
    }
    
    public int getCountImages() {
        return this.countImages;
    }
}