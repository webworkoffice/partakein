package in.partake.controller.action;

import in.partake.controller.base.AbstractPartakeController;
import in.partake.resource.MessageCode;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.io.InputStream;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

public abstract class AbstractPartakeAction extends AbstractPartakeController {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AbstractPartakeAction.class);

    // ----------------------------------------------------------------------
    // Renderers

    protected String render(String location) {
        return render(location, null);
    }

    protected String render(String location, MessageCode messageCode) {
        this.location = location;
        getPartakeSession().setMessageCode(messageCode);
        return "jsp";
    }

    @Override
    protected String renderInvalid(UserErrorCode ec, Map<String, String> additionalInfo, Throwable e) {
        if (e != null)
            logger.info("renderInvalid", e);

        if (ec != null)
            setRedirectURL("/invalid?errorCode=" + ec.getErrorCode());
        else
            setRedirectURL("/invalid");
        return REDIRECT;
    }

    @Override
    protected String renderError(ServerErrorCode ec, Map<String, String> additionalInfo, Throwable e) {
        if (e != null)
            logger.info("redirectError", e);

        if (ec != null)
            setRedirectURL("/error?errorCode=" + ec.getErrorCode());
        else
            setRedirectURL("/error");

        return REDIRECT;
    }

    protected String renderLoginRequired() {
        setRedirectURL(ServletActionContext.getRequest().getRequestURL().toString());
        ServletActionContext.getResponse().setStatus(401);
        return "login";
    }

    // TODO: renderRedirect はなにか引数を１つ取って、それを表示できるようにするべきだなあ……
    // addActionMessage, addWarningMessage, addErrorMessage などは全部廃止。
    // 表示用の文字列は Session に入れておくしかないのかな……。
    /**
     * redirect to the specified URL.
     */
    protected String renderRedirect(String url, MessageCode messageCode) {
        ServletActionContext.getResponse().setStatus(402);
        setRedirectURL(url);
        if (messageCode != null)
            ensurePartakeSession().setMessageCode(messageCode);

        return REDIRECT;
    }

    protected String renderRedirect(String url) {
        return renderRedirect(url, null);
    }

    /**
     * show the 'forbidden' page when a user did something prohibited.
     * @return
     */
    protected String renderForbidden() {
        return renderForbidden(null);
    }

    protected String renderForbidden(UserErrorCode ec) {
        if (ec != null)
            logger.info(ec.getReasonString());

        ServletActionContext.getResponse().setStatus(403);
        return PROHIBITED;
    }


    /**
     * show the 'not found' page.
     * @return
     */
    protected String renderNotFound() {
        ServletActionContext.getResponse().setStatus(404);
        return NOT_FOUND;
    }

    protected String renderStream(InputStream stream, String contentType, String contentDisposition) {
        this.stream = stream;
        this.contentType = contentType;
        this.contentDisposition = contentDisposition;
        return "stream";
    }

    protected String renderAttachmentStream(InputStream stream, String contentType) {
        return renderStream(stream, contentType, "attachment");
    }

    protected String renderInlineStream(InputStream stream, String contentType) {
        return renderStream(stream, contentType, "inline");
    }

    protected String renderInlineStream(InputStream stream, String contentType, String filename) {
        String contentDisposition = String.format("inline; filename=\"%s\"", filename);
        return renderStream(stream, contentType, contentDisposition);
    }

    /** return contentType. This function is only valid when renderStream() has been called. */
    public String getContentType() {
        return this.contentType;
    }

    /** return input stream. This function is only valid when renderStream() has been called. */
    public InputStream getInputStream() {
        return stream;
    }

    public String getContentDisposition() {
        return this.contentDisposition;
    }

}
