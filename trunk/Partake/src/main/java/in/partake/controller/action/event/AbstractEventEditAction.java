package in.partake.controller.action.event;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.EventEx;

public abstract class AbstractEventEditAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;
    protected EventEx event;
    
    public EventEx getEvent() {
        return event;
    }
}
