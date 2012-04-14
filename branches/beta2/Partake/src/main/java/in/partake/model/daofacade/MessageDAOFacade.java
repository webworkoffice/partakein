package in.partake.model.daofacade;

import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.UserMessageEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.DirectMessage;
import in.partake.model.dto.Envelope;
import in.partake.model.dto.Message;
import in.partake.model.dto.User;
import in.partake.model.dto.UserMessage;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageDAOFacade {

    public static List<UserMessageEx> findUserMessageExByReceiverId(PartakeConnection con, IPartakeDAOs daos, String userId, int offset, int limit) throws DAOException {
        List<UserMessage> userMessages = daos.getUserMessageAccess().findByReceiverId(con, userId, offset, limit);
        List<UserMessageEx> userMessageExs = new ArrayList<UserMessageEx>();
        for (UserMessage userMessage : userMessages) {
            if (userMessage == null)
                continue;

            UserEx sender = UserDAOFacade.getUserEx(con, daos, userMessage.getSenderId());
            if (sender == null)
                continue;
            Message message = daos.getMessageAccess().find(con, userMessage.getMessageId());
            if (message == null)
                continue;
            UserMessageEx messageEx = new UserMessageEx(userMessage, sender, message);
            userMessageExs.add(messageEx);
        }

        return userMessageExs;
    }

    public static void tweetMessageImpl(PartakeConnection con, IPartakeDAOs daos, User user, String messageStr) throws DAOException {
        String messageId = daos.getDirectMessageAccess().getFreshId(con);
        DirectMessage embryo = new DirectMessage(messageId, user.getId(), messageStr, null, new Date());

        daos.getDirectMessageAccess().put(con, embryo);

        String envelopeId = daos.getEnvelopeAccess().getFreshId(con);
        Envelope envelope = new Envelope(envelopeId, user.getId(), null, messageId, null, 0, null, null, DirectMessagePostingType.POSTING_TWITTER, new Date());
        daos.getEnvelopeAccess().put(con, envelope);
    }

}
