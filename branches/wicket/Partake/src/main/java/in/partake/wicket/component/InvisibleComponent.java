package in.partake.wicket.component;

import org.apache.wicket.markup.html.WebComponent;

public class InvisibleComponent extends WebComponent {
    private static final long serialVersionUID = 1L;

    public InvisibleComponent(String id) {
        super(id);
    }
    
    @Override
    public boolean isVisible() {
        return false;
    }
}
