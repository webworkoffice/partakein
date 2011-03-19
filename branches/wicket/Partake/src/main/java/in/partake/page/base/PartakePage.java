package in.partake.page.base;


import in.partake.page.error.ErrorPage;
import in.partake.page.error.InvalidPage;
import in.partake.page.error.LoginRequiredPage;
import in.partake.page.error.NotFoundPage;
import in.partake.resource.I18n;

import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public abstract class PartakePage extends WebPage {
    private static final long serialVersionUID = 1L;

    public PartakePage() {
    }
    
    protected void configureResponse() {
        super.configureResponse();

//        final String encoding = "text/" + getMarkupType() + "; charset="
//                + getApplication().getRequestCycleSettings().getResponseRequestEncoding(); 
//        ((WebResponse) getResponse()).setContentType(encoding);
    }

    // ----------------------------------------------------------------------
    // Page rendering
    
    protected void renderPage(Class<? extends Page> pageClass) {
        throw new RestartResponseException(pageClass);
    }

    protected void renderPage(Class<? extends Page> pageClass, PageParameters params) {
        throw new RestartResponseException(pageClass, params);
    }

    protected void renderNotFound() {
        throw new RestartResponseException(NotFoundPage.class);
    }

    protected void renderInvalidRequest(String reason) {
        // redirect じゃなくて forward したいんだよね
        throw new RestartResponseException(InvalidPage.class, new PageParameters().add("reason", reason));
    }
    
    protected void renderError(String reason) {
        throw new RestartResponseException(ErrorPage.class, new PageParameters().add("reason", reason));
    }
    
    protected void renderDBError() {
        renderError(I18n.t(I18n.DATABASE_ERROR));
    }

    protected void renderLoginRequired() {
        throw new RestartResponseException(LoginRequiredPage.class);        
    }
    
    protected void renderRedirect(String url) throws RedirectToUrlException {
        throw new RedirectToUrlException(url);
    }
    
}
