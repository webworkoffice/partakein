package in.partake.model.dao.jpa;

import java.util.Date;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.IDirectMessageAccess;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.DirectMessage;
import in.partake.model.dto.DirectMessageEnvelope;
import in.partake.model.dto.DirectMessagePostingType;
import in.partake.model.dto.EventReminderStatus;

public class JPADirectMessageDao extends JPADao implements IDirectMessageAccess {

    @Override
    public String getFreshId(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DirectMessage getDirectMessageById(PartakeConnection con, String messageId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addMessage(PartakeConnection con, String messageId, DirectMessage embryo) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addUserMessage(PartakeConnection con, String messageId, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public DataIterator<DirectMessage> getUserMessageIterator(PartakeConnection con, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sendEnvelope(PartakeConnection con, String messageId, String senderId, String receiverId, Date deadline, DirectMessagePostingType postingType)
                    throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public DataIterator<DirectMessageEnvelope> getEnvelopeIterator(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateEventReminderStatus(PartakeConnection con, String eventId, EventReminderStatus reminderStatus) throws DAOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public EventReminderStatus getEventReminderStatus(PartakeConnection con, String eventId) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void truncate(PartakeConnection con) throws DAOException {
        // TODO Auto-generated method stub
        
    }
}
