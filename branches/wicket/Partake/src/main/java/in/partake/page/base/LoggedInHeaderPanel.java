package in.partake.page.base;

import in.partake.application.PartakeSession;
import in.partake.model.UserEx;
import in.partake.resource.PartakeProperties;
import in.partake.wicket.component.InvisibleComponent;

import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;


public class LoggedInHeaderPanel extends Panel {
    private static final long serialVersionUID = 1L;
    
    
    public LoggedInHeaderPanel(String id) {
        super(id);
        
        UserEx user = PartakeSession.get().getCurrentUser();
        assert(user != null);
        
        if (user.isAdministrator()) {
            add(new ExternalLink("user.admin", "/admin", "管理"));
        } else {
            add(new InvisibleComponent("user.admin"));
        }
        add(new ContextImage("user.image", user.getTwitterLinkage().getProfileImageURL()));
        add(new ExternalLink("user.link", PartakeProperties.get().getTopPath() + "/users/" + user.getId(), user.getScreenName()));
    }
}
