package in.partake.model.dao;

import in.partake.model.dto.Message;

public interface IMessageAccess extends ITruncatable {
    public String getFreshId(PartakeConnection con) throws DAOException;
    
    public void addMessage(PartakeConnection con, Message embryo) throws DAOException;
    public Message getMessage(PartakeConnection con, String messageId) throws DAOException;
    public DataIterator<Message> getMessagesByEventId(PartakeConnection con, String eventId) throws DAOException;
    
}
