package in.partake.page.event;

import in.partake.model.dto.Event;
import in.partake.view.Helper;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * TopPage や、検索結果ページで使われるイベント表示を提供します。
 * @author shinyak
 */
public class EventMiniPanel extends Panel {
    private static final long serialVersionUID = 1L;
    
    public EventMiniPanel(String id, Event event) {
        super(id);
        
        add(new ExternalLink("event.title.a", event.getEventURL(), event.getTitle()));
        ExternalLink eventImage = new ExternalLink("event.image.link", event.getEventURL());
        if (StringUtils.isEmpty(event.getForeImageId())) {
            eventImage.add(new ContextImage("event.image.image", "/images/no-image.png"));
        } else {
            eventImage.add(new ContextImage("event.image.image", "/events/images/" + event.getForeImageId()));
        }
        add(eventImage);
        add(new Label("event.summary", event.getSummary()));
        add(new Label("event.place", event.getPlace()));
        add(new Label("event.date", Helper.readableDate(event.getBeginDate())));
    }
}
