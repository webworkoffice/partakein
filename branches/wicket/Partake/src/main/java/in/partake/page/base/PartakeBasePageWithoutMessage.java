package in.partake.page.base;

import in.partake.application.PartakeSession;

public class PartakeBasePageWithoutMessage extends PartakePage {
    private static final long serialVersionUID = 1L;

    public PartakeBasePageWithoutMessage() {
        header();
    }
    
    private void header() {
        String url = ""; 

        PartakeSession session = PartakeSession.get();
        if (session.isLoggedIn()) {
            add(new LoggedInHeaderPanel("header"));
        } else {
            add(new NotLoggedInHeaderPanel("header", url));
        }
    }
}
