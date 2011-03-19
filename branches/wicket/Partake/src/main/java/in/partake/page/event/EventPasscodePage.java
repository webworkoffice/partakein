package in.partake.page.event;

import in.partake.page.base.PartakeBasePage;

import org.apache.log4j.Logger;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class EventPasscodePage extends PartakeBasePage {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(EventPasscodePage.class);

    public EventPasscodePage(PageParameters params) {
        String eventId = params.get("id").toString();
        
    }
}
