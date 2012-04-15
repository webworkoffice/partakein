package in.partake.model.dao;

import in.partake.app.PartakeApp;
import in.partake.model.dao.access.IEventTicketAccess;
import in.partake.model.dto.EventTicket;
import in.partake.model.fixture.impl.EventTicketTestDataProvider;

import java.util.UUID;

import org.junit.Before;

public class EventTicketAccessTest extends AbstractDaoTestCaseBase<IEventTicketAccess, EventTicket, UUID> {
    private EventTicketTestDataProvider provider;

    @Before
    public void setup() throws Exception {
        super.setup(PartakeApp.getDBService().getDAOs().getEventTicketAccess());
        provider = PartakeApp.getTestService().getTestDataProviderSet().getEventTicketProvider();
    }

    @Override
    protected EventTicket create(long pkNumber, String pkSalt, int objNumber) {
        return provider.create(pkNumber, pkSalt, objNumber);
    }
}
