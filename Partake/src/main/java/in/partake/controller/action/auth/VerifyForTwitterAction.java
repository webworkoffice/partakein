package in.partake.controller.action.auth;

import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.base.Transaction;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.User;
import in.partake.resource.Constants;
import in.partake.resource.PartakeProperties;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;
import in.partake.service.DBService;
import in.partake.service.impl.TwitterService;
import in.partake.session.TwitterLoginInformation;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import twitter4j.TwitterException;

public class VerifyForTwitterAction extends AbstractPartakeAction {
    private static final long serialVersionUID = 1L;

    public String doExecute() throws DAOException, PartakeException {
        String verifier = getParameter("oauth_verifier");
        if (StringUtils.isBlank(verifier))
            return renderInvalid(UserErrorCode.INVALID_OAUTH_VERIFIER);

        TwitterLoginInformation loginInformation = getPartakeSession().takeTwitterLoginInformation();
        if (loginInformation == null)
            return renderInvalid(UserErrorCode.UNEXPECTED_REQUEST);

        try {
            TwitterLinkage linkage = TwitterService.createTwitterLinkageFromLoginInformation(loginInformation, verifier);
            
            UserEx user = new VerifyForTwitterActionTransaction(linkage).execute();
            session.put(Constants.ATTR_USER, user);

            addActionMessage("ログインしました");
        } catch (TwitterException e) {
            return renderError(ServerErrorCode.TWITTER_OAUTH_ERROR);
        }

        if (StringUtils.isEmpty(redirectURL))
            return renderRedirect("/");

        // If the redirect page is the error page, we do not want to show it. Showing the top page is better.
        String errorPageURL = PartakeProperties.get().getTopPath() + "/error";
        if (errorPageURL.equals(redirectURL))
            return renderRedirect("/");
        
        return renderRedirect(redirectURL);
    }
}

class VerifyForTwitterActionTransaction extends Transaction<UserEx> {
    private TwitterLinkage twitterLinkageEmbryo;
    
    public VerifyForTwitterActionTransaction(TwitterLinkage linkage) {
        this.twitterLinkageEmbryo = linkage;
    }
    
    @Override
    protected UserEx doExecute(PartakeConnection con) throws DAOException, PartakeException {
        // Twitter Linkage から User を引いてくる。
        // 対応する user がいない場合は、user を作成して Twitter Linkage を付与する
        
        try {
            // 1. まず TwitterLinkage をアップデート
            TwitterLinkage twitterLinkage = updateTwitterLinkage(con, twitterLinkageEmbryo);    
            // 2. 対応するユーザーを生成
            UserEx user = getUserFromTwitterLinkage(con, twitterLinkage);
            return user;
        } catch (TwitterException e) {
            throw new PartakeException(ServerErrorCode.TWITTER_OAUTH_ERROR, e);
        }
    }
    
    private TwitterLinkage updateTwitterLinkage(PartakeConnection con, TwitterLinkage twitterLinkageEmbryo) throws DAOException, TwitterException {
        TwitterLinkage twitterLinkage = DBService.getFactory().getTwitterLinkageAccess().find(con, twitterLinkageEmbryo.getTwitterId());

        if (twitterLinkage == null || twitterLinkage.getUserId() == null) {
            String userId = DBService.getFactory().getUserAccess().getFreshId(con);
            twitterLinkageEmbryo.setUserId(userId);
        } else {
            twitterLinkageEmbryo.setUserId(twitterLinkage.getUserId());
        }

        DBService.getFactory().getTwitterLinkageAccess().put(con, twitterLinkageEmbryo);
        return twitterLinkageEmbryo;
    }

    private UserEx getUserFromTwitterLinkage(PartakeConnection con, TwitterLinkage twitterLinkage) throws DAOException, TwitterException {
        String userId = twitterLinkage.getUserId();
        User user = DBService.getFactory().getUserAccess().find(con, userId);

        User newUser;
        if (user == null)
            newUser = new User(twitterLinkage.getUserId(), twitterLinkage.getTwitterId(), new Date(), null);
        else
            newUser = new User(user);

        newUser.setLastLoginAt(TimeUtil.getCurrentDate());
        DBService.getFactory().getUserAccess().put(con, newUser);
        return UserDAOFacade.getUserEx(con, userId);
    }
}
