package in.partake.page.event;

import org.apache.wicket.markup.html.form.TextField;

import in.partake.page.base.PartakeBasePage;

public class EventNewPage extends PartakeBasePage {
    private static final long serialVersionUID = 1L;

    public EventNewPage() {
        TextField<String> title = new TextField<String>("title");
        
        
        
        add(title);
    }
}
