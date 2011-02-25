package in.partake.view;

import in.partake.resource.Constants;

import org.apache.struts2.ServletActionContext;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

public class ViewHelper {

    /**
     * HTML の script などを取り除く
     * @param dirtyHTML
     * @return
     */
    public static String cleanupHTML(String dirtyHTML) {
        try {
            String fileName = ServletActionContext.getServletContext().getRealPath(Constants.ANTISAMY_POLICY_FILE_RELATIVE_LOCATION);               
            Policy policy = Policy.getInstance(fileName);
    
            AntiSamy as = new AntiSamy();
            CleanResults cr = as.scan(dirtyHTML, policy);
            
            return cr.getCleanHTML();
        } catch (PolicyException e) {
            e.printStackTrace();
            return "";
        } catch (ScanException e) {
            e.printStackTrace();
            return "";
        }
    }
    
}
