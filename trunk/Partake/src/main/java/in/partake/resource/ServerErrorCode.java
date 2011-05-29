package in.partake.resource;

/**
 * ServerErrorCode describes the reason why the server returned an error. 
 * @author shinyak
 */
public enum ServerErrorCode {
    DB_ERROR("in.partake.database_error");
    // TODO: あといろいろ追加する

    
    // ----------------------------------------------------------------------
    private String errorDescriptionId;
    
    private ServerErrorCode(String errorReason) {
        this.errorDescriptionId = errorReason;
    }
    
    public String getReasonString() {
        return I18n.t(errorDescriptionId);
    }
    
}
