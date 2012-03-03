package in.partake.controller.action.auth;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.resource.Constants;

import java.util.List;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;

public abstract class AbstractOpenIDAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;
    protected static ConsumerManager consumerManager = null;
    
    static {
        try {
            consumerManager = new ConsumerManager();
        } catch (ConsumerException e) {
            e.printStackTrace();
        }
    }
    
    protected String loginByOpenID(String callbackURL) {
        String userSuppliedString = getParameter("openid_identifier");
        
        try {
            String returnToUrl = callbackURL.toString();
            List<?> discoveries = consumerManager.discover(userSuppliedString);
            DiscoveryInformation discovered = consumerManager.associate(discoveries);
            session.put(Constants.ATTR_OPENID_DISCOVERY_INFORMATION, discovered);
            AuthRequest authReq = consumerManager.authenticate(discovered, returnToUrl);

            return renderRedirect(authReq.getDestinationUrl(true));
        } catch (OpenIDException e) {
            addErrorMessage("OpenID でのログインに失敗しました。");
            return renderRedirect("/");
        }
    }
}
