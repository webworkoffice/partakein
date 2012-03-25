package in.partake.daemon.impl;

import in.partake.base.PartakeException;
import in.partake.daemon.IPartakeDaemonTask;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.ITwitterLinkageAccess;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.dto.Envelope;
import in.partake.model.dto.Message;
import in.partake.model.dto.TwitterLinkage;
import in.partake.model.dto.UserPreference;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

class SendEnvelopeTask extends Transaction<Void> implements IPartakeDaemonTask {
    private static final Logger logger = Logger.getLogger(SendEnvelopeTask.class);

    @Override
    public void run() throws Exception {
        this.execute();
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        DataIterator<Envelope> it = daos.getEnvelopeAccess().getIterator(con);
        try {
            while (it.hasNext()) {
                Envelope envelope = it.next();
                if (envelope == null) { it.remove(); continue; }

                logger.debug("run : Try to send... " + envelope.getEnvelopeId());

                // deadline を超えていれば送らない。
                Date now = new Date();
                if (envelope.getDeadline() != null && envelope.getDeadline().before(now)) {
                    logger.warn("run : envelope id " + envelope.getEnvelopeId() + " could not be sent : Time out.");
                    it.remove();
                    continue;
                }

                // tryAfter 前であれば送らない。
                if (envelope.getTryAfter() != null && !envelope.getTryAfter().before(now)) {
                    logger.debug("run : envelope id " + envelope.getEnvelopeId() + " should be sent after " + envelope.getTryAfter());
                    continue;
                }


                switch (envelope.getPostingType()) {
                case POSTING_TWITTER_DIRECT:
                    if (sendDirectMessage(con, daos, it, envelope)) { it.remove(); }
                    break;
                case POSTING_TWITTER:
                    if (sendTwitterMessage(con, daos, it, envelope)) { it.remove(); }
                    break;
                }
            }
        } finally {
            it.close();
        }

        return null;
    }

    /**
     * Envelope を送信する。true を返すと送ることができた / もうこれ以上送ってはいけないという意味になる。
     * @param envelope
     * @return
     */
    private boolean sendDirectMessage(PartakeConnection con, IPartakeDAOs daos, DataIterator<Envelope> it, Envelope envelope) throws DAOException {
        String receiverId = envelope.getReceiverId();

        // twitter message を受け取らない設定になっていれば送らない。
        UserPreference pref = daos.getUserPreferenceAccess().find(con, receiverId);
        if (pref == null) {
            pref = UserPreference.getDefaultPreference(receiverId);
        }

        if (!pref.isReceivingTwitterMessage()) { return true; }

        UserEx user = UserDAOFacade.getUserEx(con, daos, receiverId);
        if (user == null) { return true; }
        TwitterLinkage twitterLinkage = user.getTwitterLinkage();

        if (twitterLinkage == null || !twitterLinkage.isAuthorized()) {
            logger.warn("sendDirectMessage : envelope id " + envelope.getEnvelopeId() + " could not be sent : No access token");
            return true;
        }

        AccessToken accessToken = new AccessToken(twitterLinkage.getAccessToken(), twitterLinkage.getAccessTokenSecret());
        Twitter twitter = new TwitterFactory().getInstance(accessToken);
        if (twitter == null) { return true; }

        try {
            Message message = daos.getDirectMessageAccess().find(con, envelope.getMessageId());
            int twitterId = Integer.parseInt(user.getTwitterId());
            twitter.sendDirectMessage(twitterId, message.getMessage());

            logger.info("sendDirectMessage : direct message has been sent to " + twitterLinkage.getScreenName());
            return true;
        } catch (NumberFormatException e) {
            logger.error("twitterId has not a number.", e);
            return true;
        } catch (TwitterException e) {
            if (e.isCausedByNetworkIssue()) {
                logger.warn("sendDirectMessage : Twitter Unreachable?", e);
                envelope.updateForSendingFailure();
                it.update(envelope);
                return false;
            } else if (e.exceededRateLimitation()) {
                envelope.updateForSendingFailure();
                int retryAfterInSeconds = e.getRetryAfter();
                envelope.setTryAfter(new Date(new Date().getTime() + retryAfterInSeconds * 1000));
                it.update(envelope);
                return false;
            } else {
                if (e.getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
                    markAsUnauthorizedUser(con, daos, user);
                    logger.info("sendDirectMessage : Unauthorized User : " + envelope.getEnvelopeId() + " was failed to deliver.", e);
                } else {
                    envelope.updateForSendingFailure();
                    logger.warn("sendDirectMessage : Unknown Error : " + envelope.getEnvelopeId() + " was failed to deliver.", e);
                }
                return true;
            }
        }
    }

    private boolean sendTwitterMessage(PartakeConnection con, IPartakeDAOs daos, DataIterator<Envelope> it, Envelope envelope) throws DAOException {
        String senderId = envelope.getSenderId();
        assert (envelope.getReceiverId() == null);
        if (senderId == null) {
            logger.warn("sendTwitterMessage : senderId is null.");
            return true;
        }

        UserEx sender = UserDAOFacade.getUserEx(con, daos, senderId);
        if (sender == null) {
            logger.warn("sendTwitterMessage : sender is null.");
            return true;
        }
        TwitterLinkage twitterLinkage = sender.getTwitterLinkage();
        if (twitterLinkage == null || !twitterLinkage.isAuthorized()) {
            logger.warn("sendDirectMessage : envelope id " + envelope.getEnvelopeId() + " could not be sent : No access token");
            return true;
        }
        AccessToken accessToken = new AccessToken(twitterLinkage.getAccessToken(), twitterLinkage.getAccessTokenSecret());
        Twitter twitter = new TwitterFactory().getInstance(accessToken);

        try {
            Message message = daos.getDirectMessageAccess().find(con, envelope.getMessageId());
            twitter.updateStatus(message.getMessage());
            return true;
        } catch (TwitterException e) {
            if (e.isCausedByNetworkIssue()) {
                logger.warn("Twitter Unreachable?", e);
                envelope.updateForSendingFailure();
                it.update(envelope);
                return false;
            } else if (e.exceededRateLimitation()) {
                envelope.updateForSendingFailure();
                int retryAfterInSeconds = e.getRetryAfter();
                envelope.setTryAfter(new Date(new Date().getTime() + retryAfterInSeconds * 1000));
                it.update(envelope);
                return false;
            } else {
                logger.warn("sendTwitterMessage : Unknown Error : " + envelope.getEnvelopeId() + " was failed to deliver.", e);
                return true;
            }
        }
    }

    private void markAsUnauthorizedUser(PartakeConnection con, IPartakeDAOs daos, UserEx user) {
        ITwitterLinkageAccess access = daos.getTwitterLinkageAccess();
        TwitterLinkage linkage = user.getTwitterLinkage().copy();
        linkage.markAsUnauthorized();

        try {
            // TODO UserExが参照するTwitterLinkageが更新されたため、UserExのキャッシュを破棄あるいは更新する必要がある
            access.put(con, linkage);
        } catch (DAOException ignore) {
            logger.warn("DAOException is thrown but it's ignored.", ignore);
        }
    }
}
