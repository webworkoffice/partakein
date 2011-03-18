package in.partake.page.error;

import in.partake.page.base.PartakeBasePageWithoutMessage;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.request.http.WebResponse;

public class ProhibitedPage extends PartakeBasePageWithoutMessage {
    private static final long serialVersionUID = 1L;

    public ProhibitedPage() {
    }

    @Override
    protected void configureResponse() {
        super.configureResponse();
        
        // この page を返すときは 403 を返していることにする。
        WebResponse webResponse = (WebResponse) getRequestCycle().getResponse();
        webResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @Override
    public boolean isErrorPage() {
        return true;
    }
}
