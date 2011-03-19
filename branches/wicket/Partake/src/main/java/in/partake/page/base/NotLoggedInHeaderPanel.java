package in.partake.page.base;

import in.partake.resource.PartakeProperties;

import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;


public class NotLoggedInHeaderPanel extends Panel {
    private static final long serialVersionUID = 1L;
    
    
    public NotLoggedInHeaderPanel(String id, final String redirectURL) {
        super(id);
        
        String currentURL = PartakeProperties.get().getTopPath() + "/" + getRequest().getClientUrl().toString();
        
        HiddenField<String> redirectURLField = new HiddenField<String>("redirectURL", new Model<String>(currentURL)) {
            private static final long serialVersionUID = 1L;

            @Override
            public String getInputName() {
                return "redirectURL";
            }
        };
        
        add(redirectURLField);
    }
}
