package in.partake.page.error;

import in.partake.page.base.PartakeBasePageWithoutMessage;
import in.partake.util.Util;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * ログインが必要なときに表示する。
 * ステータスコード上は 403 Forbidden が返る。
 * 
 * @author shinyak
 *
 */
public class LoginRequiredPage extends PartakeBasePageWithoutMessage {
    private static final long serialVersionUID = 1L;

    public LoginRequiredPage() {
        String url = "/auth/loginByTwitter";
        add(new ExternalLink("signinwithtwitter", url));
    }
    
    public LoginRequiredPage(PageParameters params) {
        String redirectURL = params.get("redirectURL").toOptionalString();
        
        String url;
        if (redirectURL != null) {
            url = "/auth/loginByTwitter?redirectURL=" + Util.encodeURIComponent(redirectURL);
        } else {
            url = "/auth/loginByTwitter";
        }

        add(new ExternalLink("signinwithtwitter", url));
    }
    
    @Override
    protected void configureResponse() {
        super.configureResponse();
        
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
