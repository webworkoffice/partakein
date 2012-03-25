package in.partake.controller.api.event;

import in.partake.base.MessageUtil;
import in.partake.base.MessageUtil.TooLongMessageException;
import in.partake.base.PartakeException;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.UserPermission;
import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.base.Transaction;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Envelope;
import in.partake.model.dto.Message;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;
import in.partake.resource.UserErrorCode;
import in.partake.service.DBService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class SendMessageAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String eventId = getValidEventIdParameter();

        String message = getParameter("message");
        if (StringUtils.isBlank(message))
            return renderInvalid(UserErrorCode.MISSING_MESSAGE);

        try {
            new SendMessageTransaction(user, eventId, message).execute();
            return renderOK();
        } catch (PartakeException e) {
            return renderException(e);
        }
    }
}

class SendMessageTransaction extends Transaction<Void> {
    private UserEx user;
    private String eventId;
    private String message;
    
    public SendMessageTransaction(UserEx user, String eventId, String message) {
        this.user = user;
        this.eventId = eventId;
        this.message = message;
    }
    
    @Override
    protected Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
        sendMessage(con, user, eventId, message);
        return null;
    }
    
    private void sendMessage(PartakeConnection con, UserEx senderUser, String eventId, String message) throws PartakeException, DAOException {
        assert senderUser != null;
        assert eventId != null;
        assert message != null;
        
        EventEx event = EventDAOFacade.getEventEx(con, eventId); 
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);
    
        if (!event.hasPermission(senderUser, UserPermission.EVENT_SEND_MESSAGE))
            throw new PartakeException(UserErrorCode.INVALID_PROHIBITED);

        // ５つメッセージを取ってきて、制約をみたしているかどうかチェックする。
        List<Message> messages = getRecentUserMessage(con, eventId, 5);
        
        Date currentTime = new Date();

        if (messages.size() >= 3) {
            Message msg = messages.get(2);
            Date msgDate = msg.getCreatedAt();
            Date thresholdDate = new Date(msgDate.getTime() + 1000 * 60 * 60); // one hour later after the message was sent.
            if (currentTime.before(thresholdDate)) // NG
                throw new PartakeException(UserErrorCode.INVALID_MESSAGE_TOOMUCH);
        }
        
        if (messages.size() >= 5) {
            Message msg = messages.get(4);
            Date msgDate = msg.getCreatedAt();
            Date thresholdDate = new Date(msgDate.getTime() + 1000 * 60 * 60 * 24); // one day later after the message was sent.

            if (currentTime.before(thresholdDate)) // NG
                throw new PartakeException(UserErrorCode.INVALID_MESSAGE_TOOMUCH);
        }

        assert (message != null);
        String msg;
        try {
            msg = MessageUtil.buildMessage(senderUser, event.getShortenedURL(), event.getTitle(), message);
        } catch (TooLongMessageException e) {
            throw new PartakeException(UserErrorCode.INVALID_MESSAGE_TOOLONG);
        }
        assert (Util.codePointCount(msg) <= MessageUtil.MESSAGE_MAX_CODEPOINTS);

        String messageId = addMessage(con, senderUser.getId(), msg,event.getId(), true);

        List<Enrollment> participations = DBService.getFactory().getEnrollmentAccess().findByEventId(con, eventId);
                
        for (Enrollment participation : participations) {
            boolean sendsMessage = false;
            switch (participation.getStatus()) {
            case ENROLLED:
                sendsMessage = true; break;
            case RESERVED:
                sendsMessage = true; break;
            default:
                break;
            }

            if (sendsMessage) {
                sendEnvelope(con, messageId, participation.getUserId(), participation.getUserId(), null, DirectMessagePostingType.POSTING_TWITTER_DIRECT);
            }
        }
    }
    
    private void sendEnvelope(PartakeConnection con, String messageId, String senderId, String receiverId, Date deadline, DirectMessagePostingType postingType) throws DAOException {
        PartakeDAOFactory factory = DBService.getFactory();

        String envelopeId = factory.getEnvelopeAccess().getFreshId(con);
        Envelope envelope = new Envelope(envelopeId, senderId, receiverId, messageId, deadline, 0, null, null, postingType, new Date());
        factory.getEnvelopeAccess().put(con, envelope);
    }
    
    private String addMessage(PartakeConnection con, String userId, String message, String eventId, boolean isUserMessage) throws DAOException {
        PartakeDAOFactory factory = DBService.getFactory();

        String id = factory.getDirectMessageAccess().getFreshId(con);
        Message embryo = new Message(id, userId, message, isUserMessage ? eventId : null, new Date());
        factory.getDirectMessageAccess().put(con, embryo);

        return id;
    }

    private List<Message> getRecentUserMessage(PartakeConnection con, String eventId, int maxMessage) throws DAOException {
        PartakeDAOFactory factory = DBService.getFactory();

        List<Message> messages = new ArrayList<Message>();
        DataIterator<Message> it = factory.getDirectMessageAccess().findByEventId(con, eventId);
        try {
            for (int i = 0; i < maxMessage; ++i) {
                if (!it.hasNext()) { break; }
                messages.add(it.next());
            }
        } finally {
            it.close();
        }
        
        return messages;
    }
}