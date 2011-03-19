package in.partake.page.event;

import in.partake.page.base.PartakeBasePage;

public class EventNewPage extends PartakeBasePage {
    private static final long serialVersionUID = 1L;
    
    public EventNewPage() {
        super("イベントを作成する - [PARTAKE]");
        add(new EventEditPanel("editpanel"));
    }
}
