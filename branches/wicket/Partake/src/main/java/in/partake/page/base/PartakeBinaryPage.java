package in.partake.page.base;

import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;


/**
 * html を使わずに data を返す場合に用いる。
 * @author shinyak
 *
 */
public abstract class PartakeBinaryPage extends PartakePage {
    private static final long serialVersionUID = 1L;

    public PartakeBinaryPage() {
    }

    protected void renderBinary(String contentType, byte[] data) {
        RequestCycle.get().replaceAllRequestHandlers(new RequestHandlerWrapper(contentType, data));
    }
}

class RequestHandlerWrapper implements IRequestHandler {
    private String contentType;
    private byte[] data;
    
    public RequestHandlerWrapper(String contentType, byte[] data) {
        this.contentType = contentType;
        this.data = data;
    }
    
    @Override
    public void respond(IRequestCycle requestCycle) {
        WebResponse webResponse = (WebResponse) requestCycle.getResponse();
        webResponse.setContentType(contentType);
        webResponse.write(data);
    }
    
    @Override
    public void detach(IRequestCycle requestCycle) {
        // do nothing
    }
}