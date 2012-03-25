package in.partake.model.daofacade;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Envelope;
import in.partake.model.dto.Message;
import in.partake.model.dto.User;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;

import java.util.Date;

public class MessageDAOFacade {

    public static void tweetMessageImpl(PartakeConnection con, IPartakeDAOs daos, User user, String messageStr) throws DAOException {
        String messageId = daos.getDirectMessageAccess().getFreshId(con);
        Message embryo = new Message(messageId, user.getId(), messageStr, null, new Date());

        daos.getDirectMessageAccess().put(con, embryo);

        String envelopeId = daos.getEnvelopeAccess().getFreshId(con);
        Envelope envelope = new Envelope(envelopeId, user.getId(), null, messageId, null, 0, null, null, DirectMessagePostingType.POSTING_TWITTER, new Date());
        daos.getEnvelopeAccess().put(con, envelope);
    }

}
