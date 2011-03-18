package in.partake.page.auth;

import in.partake.application.PartakeSession;
import in.partake.page.base.PartakePage;

import org.apache.log4j.Logger;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class LogoutPage extends PartakePage {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(LogoutPage.class);
    

    public LogoutPage(PageParameters params) {
        PartakeSession session = PartakeSession.get();
        
        if (session == null) {
            logger.error("Session is null");
            renderError("Session is null");
            return;
        }
        
        session.removeCurrentUser();
        renderRedirect("/");
    }
}
