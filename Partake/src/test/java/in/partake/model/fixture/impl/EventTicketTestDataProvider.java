package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventTicketAccess;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.auxiliary.TicketType;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventTicketTestDataProvider extends TestDataProvider<EventTicket> {

    @Override
    public EventTicket create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, ("eventticket" + pkSalt).hashCode());
        return new EventTicket(uuid, DEFAULT_EVENT_ID, TicketType.FREE_TICKET, 0, new DateTime(objNumber), null);
    }

    @Override
    public List<EventTicket> createSamples() {
        List<EventTicket> list = new ArrayList<EventTicket>();
        list.add(new EventTicket(new UUID(0, 0), "eventId", TicketType.FREE_TICKET, 0, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 1), "eventId", TicketType.FREE_TICKET, 0, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId1", TicketType.FREE_TICKET, 0, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId", TicketType.UNKNOWN_TICKET, 0, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId", TicketType.FREE_TICKET, 1, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId", TicketType.FREE_TICKET, 0, new DateTime(1), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId", TicketType.FREE_TICKET, 0, new DateTime(0), new DateTime(1)));

        return list;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IEventTicketAccess dao = daos.getEventTicketAccess();
        dao.truncate(con);

        dao.put(con, new EventTicket(DEFAULT_EVENT_TICKET_ID, DEFAULT_EVENT_ID, TicketType.FREE_TICKET, 0, new DateTime(0), null));
        dao.put(con, new EventTicket(PRIVATE_EVENT_TICKET_ID, PRIVATE_EVENT_ID, TicketType.FREE_TICKET, 0, new DateTime(0), null));
        dao.put(con, new EventTicket(JAPANESE_EVENT_TICKET_ID, JAPANESE_EVENT_ID, TicketType.FREE_TICKET, 0, new DateTime(0), null));
        dao.put(con, new EventTicket(UNIQUEIDENTIFIER_EVENT_TICKET_ID, UNIQUEIDENTIFIER_EVENT_ID, TicketType.FREE_TICKET, 0, new DateTime(0), null));
        dao.put(con, new EventTicket(UNPUBLISHED_EVENT_TICKET_ID, UNPUBLISHED_EVENT_ID, TicketType.FREE_TICKET, 0, new DateTime(0), null));
    }
}
