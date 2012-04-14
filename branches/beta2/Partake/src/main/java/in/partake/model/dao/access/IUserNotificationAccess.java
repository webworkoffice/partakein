package in.partake.model.dao.access;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserNotification;

public interface IUserNotificationAccess extends IAccess<UserNotification, String> {
    public String getFreshId(PartakeConnection con) throws DAOException;
}
