package in.partake.controller.action;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import in.partake.controller.base.AbstractPartakeController;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
import in.partake.session.PartakeSession;

public abstract class AbstractPartakeAction extends AbstractPartakeController {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AbstractPartakeAction.class);

    // Make these methods deprecated. We don't want to use these messages.
    @Override @Deprecated
    public void addActionError(String anErrorMessage) {
        super.addActionError(anErrorMessage);
    }

    // ----------------------------------------------------------------------
    // Renderers

    protected String render(String location) {
        this.location = location;
        return "jsp";
    }

    /**
     * invalid user request.
     */
    protected String renderInvalid(UserErrorCode errorCode) {
        setRedirectURL("/invalid");
        PartakeSession session = getPartakeSession();
        if (session != null)
            session.setLastUserError(errorCode);

        return REDIRECT;
    }

    /**
     * occured an internal server error
     * @return
     */
    protected String redirectError(ServerErrorCode errorCode) {
        return redirectError(errorCode, null);
    }

    @Deprecated
    protected String redirectError(ServerErrorCode ec, Throwable e) {
        if (e != null)
            logger.info("redirectError", e);

        PartakeSession session = getPartakeSession();
        if (session != null)
            session.setLastServerError(ec);

        return ERROR;
    }

    @Override
    protected String renderError(ServerErrorCode ec, Throwable e) {
        return redirectError(ec, e);
    }

    protected String renderError(ServerErrorCode ec) {
        return redirectError(ec);
    }

    /**
     * a utility function to show database error.
     * @return
     */
    protected String redirectDBError() {
        return redirectError(ServerErrorCode.DB_ERROR);
    }

    protected String renderLoginRequired() {
        setRedirectURL(ServletActionContext.getRequest().getRequestURL().toString());
        // Maybe we can specify this status code. I'm not sure.
        // ServletActionContext.getResponse().setStatus(401);
        ServletActionContext.getResponse().setStatus(401);
        return LOGIN;
    }

    /**
     * redirect to the specified URL.
     */
    protected String renderRedirect(String url) {
        ServletActionContext.getResponse().setStatus(402);        
        setRedirectURL(url);
        return REDIRECT;
    }

    /**
     * show the 'forbidden' page when a user did something prohibited.
     * @return 
     */
    protected String renderForbidden() {
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
