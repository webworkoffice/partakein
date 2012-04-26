package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventTicketAccess;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.auxiliary.TicketApplicationEnd;
import in.partake.model.dto.auxiliary.TicketApplicationStart;
import in.partake.model.dto.auxiliary.TicketPriceType;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventTicketTestDataProvider extends TestDataProvider<EventTicket> {

    @Override
    public EventTicket create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, ("eventticket" + pkSalt).hashCode());
        return new EventTicket(uuid, DEFAULT_EVENT_ID, "name" + objNumber,
                TicketApplicationStart.ANYTIME, 0, null,
                TicketApplicationEnd.TILL_TIME_BEFORE_EVENT, 0, null,
                TicketPriceType.FREE, 0, false, 0, new DateTime(0), null);
    }

    @Override
    public List<EventTicket> createSamples() {
        List<EventTicket> list = new ArrayList<EventTicket>();

        list.add(new EventTicket(new UUID(0, 0), "eventId", "name", TicketApplicationStart.ANYTIME, 0, null, TicketApplicationEnd.TILL_TIME_BEFORE_EVENT, 0, null, TicketPriceType.FREE, 0, false, 0, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 1), "eventId", "name", TicketApplicationStart.ANYTIME, 0, null, TicketApplicationEnd.TILL_TIME_BEFORE_EVENT, 0, null, TicketPriceType.FREE, 0, false, 0, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId1", "name", TicketApplicationStart.ANYTIME, 0, null, TicketApplicationEnd.TILL_TIME_BEFORE_EVENT, 0, null, TicketPriceType.FREE, 0, false, 0, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId", "name1", TicketApplicationStart.ANYTIME, 0, null, TicketApplicationEnd.TILL_TIME_BEFORE_EVENT, 0, null, TicketPriceType.FREE, 0, false, 0, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId", "name", TicketApplicationStart.FROM_CUSTOM_DAY, 0, null, TicketApplicationEnd.TILL_TIME_BEFORE_EVENT, 0, null, TicketPriceType.FREE, 0, false, 0, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId", "name", TicketApplicationStart.ANYTIME, 1, null, TicketApplicationEnd.TILL_TIME_BEFORE_EVENT, 0, null, TicketPriceType.FREE, 0, false, 0, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId", "name", TicketApplicationStart.ANYTIME, 0, new DateTime(0L), TicketApplicationEnd.TILL_TIME_BEFORE_EVENT, 0, null, TicketPriceType.FREE, 0, false, 0, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId", "name", TicketApplicationStart.ANYTIME, 0, null, TicketApplicationEnd.TILL_TIME_AFTER_EVENT, 0, null, TicketPriceType.FREE, 0, false, 0, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId", "name", TicketApplicationStart.ANYTIME, 0, null, TicketApplicationEnd.TILL_TIME_BEFORE_EVENT, 1, null, TicketPriceType.FREE, 0, false, 0, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId", "name", TicketApplicationStart.ANYTIME, 0, null, TicketApplicationEnd.TILL_TIME_BEFORE_EVENT, 0, new DateTime(0L), TicketPriceType.FREE, 0, false, 0, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId", "name", TicketApplicationStart.ANYTIME, 0, null, TicketApplicationEnd.TILL_TIME_BEFORE_EVENT, 0, null, TicketPriceType.NONFREE, 0, false, 0, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId", "name", TicketApplicationStart.ANYTIME, 0, null, TicketApplicationEnd.TILL_TIME_BEFORE_EVENT, 0, null, TicketPriceType.FREE, 1, false, 0, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId", "name", TicketApplicationStart.ANYTIME, 0, null, TicketApplicationEnd.TILL_TIME_BEFORE_EVENT, 0, null, TicketPriceType.FREE, 0, true, 0, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId", "name", TicketApplicationStart.ANYTIME, 0, null, TicketApplicationEnd.TILL_TIME_BEFORE_EVENT, 0, null, TicketPriceType.FREE, 0, false, 1, new DateTime(0), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId", "name", TicketApplicationStart.ANYTIME, 0, null, TicketApplicationEnd.TILL_TIME_BEFORE_EVENT, 0, null, TicketPriceType.FREE, 0, false, 0, new DateTime(1), null));
        list.add(new EventTicket(new UUID(0, 0), "eventId", "name", TicketApplicationStart.ANYTIME, 0, null, TicketApplicationEnd.TILL_TIME_BEFORE_EVENT, 0, null, TicketPriceType.FREE, 0, false, 0, new DateTime(0), new DateTime(0L)));

        return list;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IEventTicketAccess dao = daos.getEventTicketAccess();
        dao.truncate(con);

        dao.put(con, EventTicket.createDefaultTicket(DEFAULT_EVENT_TICKET_ID, DEFAULT_EVENT_ID));
        dao.put(con, EventTicket.createDefaultTicket(PRIVATE_EVENT_TICKET_ID, PRIVATE_EVENT_ID));
        dao.put(con, EventTicket.createDefaultTicket(JAPANESE_EVENT_TICKET_ID, JAPANESE_EVENT_ID));
        dao.put(con, EventTicket.createDefaultTicket(UNIQUEIDENTIFIER_EVENT_TICKET_ID, UNIQUEIDENTIFIER_EVENT_ID));
        dao.put(con, EventTicket.createDefaultTicket(UNPUBLISHED_EVENT_TICKET_ID, UNPUBLISHED_EVENT_ID));
    }
}
