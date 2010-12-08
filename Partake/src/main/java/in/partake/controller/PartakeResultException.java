package in.partake.controller;

public class PartakeResultException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String result;
    
    public PartakeResultException(String result) { 
        this.result = result;
    }
    
    public String getResult() {
        return result;
    }
    
    public static PartakeResultException throwError() throws PartakeResultException {
        return new PartakeResultException("error"); // TODO: should be constant.
    }
    
    public static PartakeResultException throwLogin() throws PartakeResultException {
        return new PartakeResultException("login"); // TODO: should be constant.
    }
}
