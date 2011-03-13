package in.partake.page.event;

import in.partake.model.dto.Event;

import org.apache.wicket.markup.html.panel.Panel;

public class EventManagementPanel extends Panel {
    private static final long serialVersionUID = 1L;
    
    public EventManagementPanel(String id, Event event) {
        super(id);
    }
}
