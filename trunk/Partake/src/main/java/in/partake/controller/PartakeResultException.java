package in.partake.controller;

/**
 * Controller can throw this exception to return a result.
 * API cannot.
 * 
 * @author shinyak
 */
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
