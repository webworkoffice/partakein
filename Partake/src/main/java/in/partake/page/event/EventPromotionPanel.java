package in.partake.page.event;

import in.partake.model.dto.Event;
import in.partake.util.Util;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

public class EventPromotionPanel extends Panel {
    private static final long serialVersionUID = 1L;
    
    public EventPromotionPanel(String id, Event event) {
        super(id);
        
        {
            WebMarkupContainer twitter = new WebMarkupContainer("promotion.twitter");
            String twitterComment = event.getTitle() + " - [PARTAKE] " + event.getHashTag();
            twitter.add(new SimpleAttributeModifier("data-text", twitterComment));
            add(twitter);
        }
        
        {
            WebMarkupContainer faceBook = new WebMarkupContainer("promotion.facebook");
            String faceBookSrc = String.format(
                    "http://www.facebook.com/plugins/like.php?href=%s&layout=button_count&show_faces=true&width=450&action=like&colorscheme=light&height=21",
                    Util.encodeURIComponent(event.getEventURL()));
            faceBook.add(new SimpleAttributeModifier("src", faceBookSrc));
            add(faceBook);
        }
    }
}
