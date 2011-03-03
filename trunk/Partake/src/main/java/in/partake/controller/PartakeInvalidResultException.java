package in.partake.controller;

/**
 * Controller can throw this exception to return a result.
 * @author shinyak
 */
public class PartakeInvalidResultException extends PartakeResultException {
    /** */
    private static final long serialVersionUID = 1L;
    
    private String description;
    
    public PartakeInvalidResultException(String description) {
        super("invalid");
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
