package in.partake.model.fixture.impl;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventFeedLinkage;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventFeedTestDataProvider extends TestDataProvider<EventFeedLinkage> {
    @Override
    public EventFeedLinkage create(long pkNumber, String pkSalt, int objNumber) {
        return new EventFeedLinkage(new UUID(pkNumber, pkSalt.hashCode()).toString(), "eventId" + objNumber);
    }

    @Override
    public List<EventFeedLinkage> createSamples() {
        List<EventFeedLinkage> array = new ArrayList<EventFeedLinkage>();
        array.add(new EventFeedLinkage(new UUID(0, 0).toString(), "eventId"));
        array.add(new EventFeedLinkage(new UUID(0, 1).toString(), "eventId"));
        array.add(new EventFeedLinkage(new UUID(0, 0).toString(), "eventId1"));
        return array;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        daos.getEventFeedAccess().truncate(con);
    }
}
