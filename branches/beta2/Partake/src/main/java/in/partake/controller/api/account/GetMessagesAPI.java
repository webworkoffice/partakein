package in.partake.controller.api.account;

import in.partake.base.PartakeException;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.UserMessageEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.MessageDAOFacade;

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
public class GetMessagesAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();

        int offset = optIntegerParameter("offset", 0);
        offset = Util.ensureRange(offset, 0, Integer.MAX_VALUE);

        int limit = optIntegerParameter("limit", 10);
        limit = Util.ensureRange(limit, 1, 100);

        GetMessagesTransaction transaction = new GetMessagesTransaction(user, offset, limit);
        transaction.execute();

        JSONArray messages = new JSONArray();
        for (UserMessageEx message : transaction.getUserMessageExs())
            messages.add(message.toJSON());

        JSONObject obj = new JSONObject();
        obj.put("messages", messages);
        obj.put("totalMessagesCount", transaction.getCount());
        return renderOK(obj);
    }
}

class GetMessagesTransaction extends DBAccess<Void> {
    private UserEx user;
    private int offset;
    private int limit;

    private List<UserMessageEx> imageIds;
    private int count;

    public GetMessagesTransaction(UserEx user, int offset, int limit) {
        this.user = user;
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        this.imageIds = MessageDAOFacade.findUserMessageExByReceiverId(con, daos, user.getId(), offset, limit);
        this.count = daos.getUserReceivedMessageAccess().countByReceiverId(con, user.getId());
        return null;
    }

    public List<UserMessageEx> getUserMessageExs() {
        return this.imageIds;
    }

    public int getCount() {
        return this.count;
    }
}
