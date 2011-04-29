package in.partake.controller;

/**
 * Controller can throw this exception to return a result.
 * @author shinyak
 */
// TODO: this exception will be @deprecated later. 
public class PartakeResultException extends Exception {
    /** */
    private static final long serialVersionUID = 1L;
    
    private String result;
    
    public PartakeResultException(String result) { 
        this.result = result;
    }
    
    public String getResult() {
        return result;
    }    
}
