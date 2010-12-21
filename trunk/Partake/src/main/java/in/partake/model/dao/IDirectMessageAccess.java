package in.partake.model.dao;

import java.util.Date;
import java.util.UUID;

import me.prettyprint.cassandra.service.CassandraClient;

import in.partake.model.dto.DirectMessage;
import in.partake.model.dto.DirectMessageEnvelope;
import in.partake.model.dto.DirectMessagePostingType;

public interface IDirectMessageAccess {
    public String getFreshId(PartakeConnection con) throws DAOException;
    public DirectMessage getDirectMessageById(PartakeConnection con, String messageId) throws DAOException;
    
    public void addMessage(PartakeConnection con, String messageId, DirectMessage embryo) throws DAOException;
    
    public void addUserMessage(PartakeConnection con, String messageId, String eventId) throws DAOException;
    
    public DataIterator<DirectMessage> getUserMessageIterator(PartakeModelFactory factory, String eventId) throws DAOException;
    
    public void sendEnvelope(PartakeConnection con, String messageId, String senderId, String receiverId, Date deadline, DirectMessagePostingType postingType) throws DAOException;
    
    public DataIterator<DirectMessageEnvelope> getEnvelopeIterator(PartakeModelFactory factory) throws DAOException;
    
}
