package in.partake.controller.base;

import in.partake.model.UserEx;

public abstract class AbstractPartakeUserAwareAction extends AbstractPartakeController {
    public String execute() {
        UserEx user = getLoginUser();
        if (user == null)
            return renderLoginRequired();
        
        return doExecute(user);
    }
    
    protected abstract String doExecute(UserEx user);
}
