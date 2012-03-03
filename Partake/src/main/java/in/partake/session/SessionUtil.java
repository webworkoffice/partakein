package in.partake.session;

import java.util.Map;

import org.apache.struts2.ServletActionContext;

import in.partake.resource.Constants;

public class SessionUtil {
    
    /**
     * @return session object を手軽に取得する 
     */
    public static PartakeSession getSession() {
        Map<String, Object> session = ServletActionContext.getContext().getSession();
        if (session == null) { return null; }
        return (PartakeSession) session.get(Constants.ATTR_PARTAKE_SESSION);
    }
}
