package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventNotification;
import in.partake.model.dto.auxiliary.NotificationType;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class EventNotificationTestDataProvider extends TestDataProvider<EventNotification> {
    @Override
    public EventNotification create(long pkNumber, String pkSalt, int objNumber) {
        String id = new UUID(pkNumber, pkSalt.hashCode()).toString();
        return new EventNotification(id, "eventId", new ArrayList<String>(), NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(0), null);
    }

    @Override
    public List<EventNotification> createGetterSetterSamples() {
        List<EventNotification> array = new ArrayList<EventNotification>();
        array.add(new EventNotification(new UUID(0, 0).toString(), "eventId",Arrays.asList(new String[] { "" }), NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(0), null));
        array.add(new EventNotification(new UUID(0, 1).toString(), "eventId", Arrays.asList(new String[] { "" }), NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(0), null));
        array.add(new EventNotification(new UUID(0, 0).toString(), "eventId1", Arrays.asList(new String[] { "" }), NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(0), null));
        array.add(new EventNotification(new UUID(0, 0).toString(), "eventId", Arrays.asList(new String[] { "1" }), NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(0), null));
        array.add(new EventNotification(new UUID(0, 0).toString(), "eventId", Arrays.asList(new String[] { "" }), NotificationType.BECAME_TO_BE_ENROLLED, new DateTime(0), null));
        array.add(new EventNotification(new UUID(0, 0).toString(), "eventId", Arrays.asList(new String[] { "" }), NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(1), null));
        array.add(new EventNotification(new UUID(0, 0).toString(), "eventId", Arrays.asList(new String[] { "" }), NotificationType.BECAME_TO_BE_CANCELLED, new DateTime(0), new DateTime(1L)));
        return array;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        daos.getEventNotificationAccess().truncate(con);
    }
}
