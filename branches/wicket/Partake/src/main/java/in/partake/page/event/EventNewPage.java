package in.partake.page.event;

import in.partake.page.base.PartakeBasePage;

public class EventNewPage extends PartakeBasePage {
    private static final long serialVersionUID = 1L;
    
    public EventNewPage() {
        add(new EventEditPanel("editpanel"));
    }
    
    @Override
    protected String getTitle() {
        return "イベントを作成する - [PARTAKE]";
    }
}
