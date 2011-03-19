package in.partake.page.error;

import javax.servlet.http.HttpServletResponse;

import in.partake.page.base.PartakeBasePageWithoutMessage;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class InvalidPage extends PartakeBasePageWithoutMessage {
    private static final long serialVersionUID = 1L;
    
    public InvalidPage() {
        add(new Label("reason", "エラーの理由がアプリケーションによって指定されませんでした。そのうち指定されるようになるでしょう。"));
    }
    
    public InvalidPage(PageParameters params) {
        String reason = params.get("reason").toString();
        add(new Label("reason", reason));
    }
    
    @Override
    protected void configureResponse() {
        super.configureResponse();
        
        // この page を返すときは 400 を返していることにする。
        WebResponse webResponse = (WebResponse) getRequestCycle().getResponse();
        webResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
