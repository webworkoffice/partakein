package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserNotification;
import in.partake.model.dto.auxiliary.NotificationType;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserNotificationTestDataProvider extends TestDataProvider<UserNotification> {
    @Override
    public UserNotification create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        return new UserNotification(uuid.toString(), "eventId", "userId", NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(0), null);
    }

    @Override
    public List<UserNotification> createGetterSetterSamples() {
        List<UserNotification> array = new ArrayList<UserNotification>();
        array.add(new UserNotification(new UUID(0, 0).toString(), "eventId", "userId", NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(0), null));
        array.add(new UserNotification(new UUID(0, 1).toString(), "eventId", "userId", NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(0), null));
        array.add(new UserNotification(new UUID(0, 0).toString(), "eventId1", "userId", NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(0), null));
        array.add(new UserNotification(new UUID(0, 0).toString(), "eventId", "userId1", NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(0), null));
        array.add(new UserNotification(new UUID(0, 0).toString(), "eventId", "userId", NotificationType.BECAME_TO_BE_ENROLLED, new DateTime(0), null));
        array.add(new UserNotification(new UUID(0, 0).toString(), "eventId", "userId", NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(1), null));
        array.add(new UserNotification(new UUID(0, 0).toString(), "eventId", "userId", NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(0), new DateTime(1)));
        return array;
    }

    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        daos.getUserNotificationAccess().truncate(con);
    }
}
