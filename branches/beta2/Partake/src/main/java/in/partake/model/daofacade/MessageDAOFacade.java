package in.partake.model.daofacade;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.Envelope;
import in.partake.model.dto.Message;
import in.partake.model.dto.User;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;
import in.partake.service.DBService;

import java.util.Date;

public class MessageDAOFacade {

    public static void tweetMessageImpl(PartakeConnection con, User user, String messageStr) throws DAOException {
        PartakeDAOFactory factory = DBService.getFactory();
        String messageId = factory.getDirectMessageAccess().getFreshId(con);
        Message embryo = new Message(messageId, user.getId(), messageStr, null, new Date());

        factory.getDirectMessageAccess().put(con, embryo);

        String envelopeId = factory.getEnvelopeAccess().getFreshId(con);
        Envelope envelope = new Envelope(envelopeId, user.getId(), null, messageId, null, 0, null, null, DirectMessagePostingType.POSTING_TWITTER, new Date());
        factory.getEnvelopeAccess().put(con, envelope);
    }

}
