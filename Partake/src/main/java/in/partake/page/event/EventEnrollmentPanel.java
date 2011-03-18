package in.partake.page.event;

import in.partake.model.dto.Event;
import in.partake.model.dto.User;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import org.apache.wicket.markup.html.panel.Panel;

public class EventEnrollmentPanel extends Panel {
    private static final long serialVersionUID = 1L;
    
    public EventEnrollmentPanel(String id, Event event, User user, ParticipationStatus status) {
        super(id);
    }
}
