package in.partake.page.event;

import in.partake.model.dto.Event;
import in.partake.wicket.component.InvisibleComponent;

import org.apache.wicket.markup.html.panel.Panel;

public class EventCommentPanel extends Panel {
    private static final long serialVersionUID = 1L;
    
    public EventCommentPanel(String id, Event event) {
        super(id);
        
        add(new InvisibleComponent("comment"));
        add(new InvisibleComponent("commentForm"));
        add(new InvisibleComponent("noCommentForm"));
    }
}
