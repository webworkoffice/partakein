package in.partake.page.base;

import in.partake.page.error.ErrorPage;
import in.partake.page.error.InvalidPage;
import in.partake.resource.I18n;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class PartakePanel extends Panel {
    private static final long serialVersionUID = 1L;

    public PartakePanel(String id) {
        super(id);
    }
    
    
    // TODO: PartakePage と同じものを提供したい。PartakeRenderer とか作る？ 
    
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
}
