package in.partake.model.dao.access;

import java.util.List;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserMessage;

public interface IUserMessageAccess extends IAccess<UserMessage, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;

    int countByReceiverId(PartakeConnection con, String receiverId) throws DAOException;
    List<UserMessage> findByReceiverId(PartakeConnection con, String receiverId, int offset, int limit) throws DAOException;

}
