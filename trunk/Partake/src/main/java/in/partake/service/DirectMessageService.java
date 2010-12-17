package in.partake.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.partake.model.DirectMessageEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dto.DirectMessage;
import in.partake.model.dto.DirectMessageEnvelope;
import in.partake.model.dto.DirectMessagePostingType;
import in.partake.model.dto.User;

public class DirectMessageService extends PartakeService {
    private static DirectMessageService instance = new DirectMessageService();
    
    DirectMessageService() {
        // do nothing for now.
    }
    
    public static DirectMessageService get() {
        return instance;
    }
    
    // ----------------------------------------------------------------------

    public DirectMessage getMessageById(String messageId) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = factory.getConnection();
        try {
            return factory.getDirectMessageAccess().getDirectMessageById(con, messageId);
        } finally {
            con.invalidate();
        }
    }
    
    public String addMessage(DirectMessage embryo, boolean isUserMessage) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = factory.getConnection();

        try {
            String messageId = factory.getDirectMessageAccess().getFreshId(con);
            factory.getDirectMessageAccess().addMessage(con, messageId, embryo); 
            if (isUserMessage) {
                factory.getDirectMessageAccess().addUserMessage(con, messageId, embryo.getEventId());
            }

            return messageId;
        } finally {
            con.invalidate();
        }
    }
    
    /**
     * ある event で管理者がユーザーに送ったメッセージを送った順に取得する。
     * @param eventId
     * @return
     * @throws DAOException
     */
    public List<DirectMessageEx> getUserMessagesByEventId(String eventId) throws DAOException {
    	PartakeDAOFactory factory = getFactory();
    	PartakeConnection con = factory.getConnection();
    	
    	try {
	        List<DirectMessageEx> messages = new ArrayList<DirectMessageEx>();
	        DataIterator<DirectMessage> it = factory.getDirectMessageAccess().getUserMessageIterator(factory, eventId);
	
	        
	        while (it.hasNext()) {
	        	DirectMessage message = it.next();
	        	messages.add(new DirectMessageEx(message, getUserEx(con, message.getUserId())));
	        }
	                
	        return messages;
    	} finally {
    		con.invalidate();
    	}
    }
    
    /**
     * 管理者が送ったユーザーに送ったメッセージを最大 maxMessage 個取得する。
     * @param eventId
     * @param maxMessage
     * @return
     * @throws DAOException
     */
    public List<DirectMessage> getRecentUserMessage(String eventId, int maxMessage) throws DAOException {
        PartakeDAOFactory factory = getFactory();

        List<DirectMessage> messages = new ArrayList<DirectMessage>();
        DataIterator<DirectMessage> it = factory.getDirectMessageAccess().getUserMessageIterator(factory, eventId);
        
        for (int i = 0; i < maxMessage; ++i) {
            if (!it.hasNext()) { break; }
            messages.add(it.next());
        }
                
        return messages;
    }
    
    public void tweetMessage(User user, String messageStr) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = factory.getConnection();

        try {
            DirectMessage embryo = new DirectMessage(user.getId(), messageStr);
            String messageId = factory.getDirectMessageAccess().getFreshId(con);
            factory.getDirectMessageAccess().addMessage(con, messageId, embryo);
            factory.getDirectMessageAccess().sendEnvelope(con, messageId, user.getId(), null, null, DirectMessagePostingType.POSTING_TWITTER);
        } finally {
            con.invalidate();
        }
    }

    public void sendEnvelope(String messageId, String senderId, String receiverId, Date deadline, DirectMessagePostingType postingType) throws DAOException {
        PartakeDAOFactory factory = getFactory();
        PartakeConnection con = factory.getConnection();

        try {
            factory.getDirectMessageAccess().sendEnvelope(con, messageId, senderId, receiverId, deadline, postingType);
        } finally {
            con.invalidate();
        }
    }

    public DataIterator<DirectMessageEnvelope> getEnvelopeIterator() throws DAOException {
        PartakeDAOFactory factory = getFactory();
        return factory.getDirectMessageAccess().getEnvelopeIterator(factory);            
    }
}
