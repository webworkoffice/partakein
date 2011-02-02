package in.partake.model.dao;

import java.util.Date;

import in.partake.model.dto.Message;
import in.partake.model.dto.Envelope;
import in.partake.model.dto.EventReminderStatus;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;

public interface IMessageAccess extends ITruncatable {
    public String getFreshId(PartakeConnection con) throws DAOException;
    
    public void addMessage(PartakeConnection con, Message embryo) throws DAOException;
    public Message getMessage(PartakeConnection con, String messageId) throws DAOException;
    public DataIterator<Message> getMessagesByEventId(PartakeConnection con, String eventId) throws DAOException;
    
    public void sendEnvelope(PartakeConnection con, String messageId, String senderId, String receiverId, Date deadline, DirectMessagePostingType postingType) throws DAOException;    
    public DataIterator<Envelope> getEnvelopeIterator(PartakeConnection con) throws DAOException;
    
    // EventReminderStatus
    public void updateEventReminderStatus(PartakeConnection con, String eventId, EventReminderStatus reminderStatus) throws DAOException;
    public EventReminderStatus getEventReminderStatus(PartakeConnection con, String eventId) throws DAOException;
}
