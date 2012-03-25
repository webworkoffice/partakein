package in.partake.controller.action.auth;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.resource.PartakeProperties;
import in.partake.service.impl.OpenIDService;
import in.partake.session.OpenIDLoginInformation;

import org.openid4java.OpenIDException;
import org.openid4java.discovery.DiscoveryInformation;

public abstract class AbstractOpenIDAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;
    private static final String CALLBACK_URL = PartakeProperties.get().getTopPath() + "/auth/verifyOpenID";

    protected String doAuthenticate(String purpose) throws OpenIDException {
        OpenIDLoginInformation loginInfo = getPartakeSession().ensureOpenIDLoginInformation();
        
        String identifier = getParameter("openidIdentifier");
        DiscoveryInformation discoveryInformation = OpenIDService.discover(identifier);
        loginInfo.setLoginPurpose(purpose);
        
        return renderRedirect(OpenIDService.getURLToAuthenticate(discoveryInformation, CALLBACK_URL));
    }
}
