package in.partake.page.auth;

import in.partake.application.PartakeSession;
import in.partake.page.base.PartakePage;
import in.partake.resource.Constants;
import in.partake.resource.PartakeProperties;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;

public class LoginByOpenIDPage extends PartakePage {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(LoginByOpenIDPage.class);

    public LoginByOpenIDPage(PageParameters params) {
       
        PartakeSession session = PartakeSession.get();
        
        String purpose = params.get("purpose").toOptionalString();
        if ("login".equals(purpose) || "connect".equals(purpose)) {
            session.put(Constants.ATTR_OPENID_PURPOSE, purpose);
        } else {
            renderInvalidRequest("OpenID 利用目的を示すパラメータが適切ではありません。");
            return;
        }
        
        String userSuppliedString = params.get("openid_identifier").toOptionalString();
        String callbackURL = PartakeProperties.get().getTopPath() + "/auth/verifyOpenID";
        
        try {
            ConsumerManager manager = session.getConsumerManager();
            String returnToUrl = callbackURL.toString();
            List<?> discoveries = manager.discover(userSuppliedString);
            DiscoveryInformation discovered = manager.associate(discoveries);
            session.put(Constants.ATTR_OPENID_DISCOVERY_INFORMATION, discovered);
            AuthRequest authReq = manager.authenticate(discovered, returnToUrl);

            renderRedirect(authReq.getDestinationUrl(true));
            return;
        } catch (OpenIDException e) {
            logger.info("OpenID login failed", e);
            session.addErrorMessage("OpenID でのログインに失敗しました。");
            
            renderRedirect(PartakeProperties.get().getTopPath() + "/");
        }
    }
}
