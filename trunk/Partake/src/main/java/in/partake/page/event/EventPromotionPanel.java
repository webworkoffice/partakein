package in.partake.page.event;

import in.partake.model.dto.Event;

import org.apache.wicket.markup.html.panel.Panel;

public class EventPromotionPanel extends Panel {
    private static final long serialVersionUID = 1L;
    
    public EventPromotionPanel(String id, Event event) {
        super(id);
    }
}
