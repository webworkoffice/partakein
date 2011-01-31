package in.partake.model.dao;

import java.util.Date;

import in.partake.model.dto.DirectMessage;
import in.partake.model.dto.DirectMessageEnvelope;
import in.partake.model.dto.EventReminderStatus;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;

public interface IDirectMessageAccess extends ITruncatable {
    public String getFreshId(PartakeConnection con) throws DAOException;
    
    public DirectMessage getDirectMessageById(PartakeConnection con, String messageId) throws DAOException;
    public void addMessage(PartakeConnection con, String messageId, DirectMessage embryo) throws DAOException;
    public void addUserMessage(PartakeConnection con, String messageId, String eventId) throws DAOException;
    public DataIterator<DirectMessage> getUserMessageIterator(PartakeConnection con, String eventId) throws DAOException;
    
    public void sendEnvelope(PartakeConnection con, String messageId, String senderId, String receiverId, Date deadline, DirectMessagePostingType postingType) throws DAOException;    
    public DataIterator<DirectMessageEnvelope> getEnvelopeIterator(PartakeConnection con) throws DAOException;
    
    // EventReminderStatus
    public void updateEventReminderStatus(PartakeConnection con, String eventId, EventReminderStatus reminderStatus) throws DAOException;
    public EventReminderStatus getEventReminderStatus(PartakeConnection con, String eventId) throws DAOException;
}
