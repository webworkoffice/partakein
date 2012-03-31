package in.partake.daemon.impl;

import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.daemon.IPartakeDaemonTask;
import in.partake.model.EnrollmentEx;
import in.partake.model.EventEx;
import in.partake.model.IPartakeDAOs;
import in.partake.model.ParticipationList;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EnrollmentDAOFacade;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Envelope;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventReminder;
import in.partake.model.dto.Message;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.PartakeProperties;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

class EventReminderTask extends Transaction<Void> implements IPartakeDaemonTask {
    private static final Logger logger = Logger.getLogger(EventReminderTask.class);

    @Override
    public void run() throws Exception {
        this.execute();
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        String topPath = PartakeProperties.get().getTopPath();
        Date now = TimeUtil.getCurrentDate();

        // TODO: 開始時刻が現在時刻より後の event のみを取り出したい、というかリマインダーを送るべりイベントのみを取り出したい
        DataIterator<Event> it = daos.getEventAccess().getIterator(con);
        try {
            while (it.hasNext()) {
                Event e = it.next();
                if (e == null) { continue; }
                String eventId = e.getId();
                if (eventId == null) { continue; }
                EventEx event = EventDAOFacade.getEventEx(con, daos, eventId);
                if (event == null) { continue; }
                if (event.getBeginDate().before(now)) { continue; }

                // NOTE: Since the reminderStatus gotten from getEventReminderStatus is frozen,
                //       the object should be copied to use.
                EventReminder reminderStatus = new EventReminder(getEventReminderImpl(con, daos, eventId));

                boolean changed = sendEventNotification(con, daos, event, reminderStatus, topPath, now);
                if (changed) {
                    reminderStatus.setEventId(eventId);
                    con.beginTransaction();
                    daos.getEventReminderAccess().put(con, reminderStatus);
                    con.commit();
                }
            }
        } finally {
            it.close();
        }

        // TODO Auto-generated method stub
        return null;
    }

    private static boolean needsToSend(Date now, Date targetDate, Date lastSent) {
        if (now.before(targetDate)) { return false; }
        if (lastSent == null) { return true; }
        if (targetDate.before(lastSent)) { return false; }
        if (now.before(new Date(lastSent.getTime() + 1000 * 3600))) { return false; }
        return true;
    }

    /**
     * message の内容で、仮参加者にのみメッセージを送る。
     * @param event
     * @param message
     * @throws DAOException
     */
    private void sendNotificationOnlyForReservedParticipants(PartakeConnection con, IPartakeDAOs daos, Event event, String message) throws DAOException {
        String messageId = daos.getDirectMessageAccess().getFreshId(con);
        Message embryo = new Message(messageId, event.getOwnerId(), message, null, new Date());
        daos.getDirectMessageAccess().put(con, embryo);

        List<Enrollment> participations = daos.getEnrollmentAccess().findByEventId(con, event.getId());
        Date deadline = event.getCalculatedDeadline();
        for (Enrollment participation : participations) {
            if (!ParticipationStatus.RESERVED.equals(participation.getStatus())) { continue; }
            String envelopeId = daos.getEnvelopeAccess().getFreshId(con);
            Envelope envelope = new Envelope(envelopeId, participation.getUserId(), participation.getUserId(),
                    messageId, deadline, 0, null, null, DirectMessagePostingType.POSTING_TWITTER_DIRECT, new Date());
            daos.getEnvelopeAccess().put(con, envelope);
            logger.info("sendEnvelope : " + participation.getUserId() + " : " + embryo.getMessage());
        }
    }
    private boolean sendEventNotification(PartakeConnection con, IPartakeDAOs daos, EventEx event, EventReminder reminderStatus, String topPath, Date now) throws DAOException {
        Date beginDate = event.getBeginDate();
        Date deadline = event.getCalculatedDeadline();

        boolean changed = false;

        String shortenedURL = event.getShortenedURL();

        // TODO: isBeforeDeadline() とかわかりにくいな。
        // 締め切り１日前になっても RESERVED ステータスの人がいればメッセージを送付する。
        // 次の条件でメッセージを送付する
        //  1. 現在時刻が締め切り２４時間前よりも後
        //  2. 次の条件のいずれか満たす
        //    2.1. まだメッセージが送られていない
        //    2.2. 前回送った時刻が締め切り２４時間以上前で、かつ送った時刻より１時間以上経過している。

        if (needsToSend(now, TimeUtil.oneDayBefore(deadline), reminderStatus.getSentDateOfBeforeDeadlineOneday())) {
            String message = "[PARTAKE] 締め切り１日前です。参加・不参加を確定してください。 " + shortenedURL + " " + event.getTitle();
            message = Util.shorten(message, 140);
            sendNotificationOnlyForReservedParticipants(con, daos, event, message);

            reminderStatus.setSentDateOfBeforeDeadlineOneday(now);
            changed = true;
        }

        // 締め切り１２時間前になっても RESERVED な人がいればメッセージを送付する。
        if (needsToSend(now, TimeUtil.halfDayBefore(deadline), reminderStatus.getSentDateOfBeforeDeadlineHalfday())) {
            String message = "[PARTAKE] 締め切り１２時間前です。参加・不参加を確定してください。 ３時間前までに確定されない場合、キャンセル扱いとなります。" + shortenedURL + " " + event.getTitle();
            message = Util.shorten(message, 140);
            sendNotificationOnlyForReservedParticipants(con, daos, event, message);

            reminderStatus.setSentDateOfBeforeDeadlineHalfday(now);
            changed = true;
        }

        // イベント１日前で、参加が確定している人にはメッセージを送付する。
        // 参加が確定していない人には、RESERVED なメッセージが送られている。
        if (needsToSend(now, TimeUtil.oneDayBefore(beginDate), reminderStatus.getSentDateOfBeforeTheDay())) {
            String message = "[PARTAKE] イベントの１日前です。あなたの参加は確定しています。 " + shortenedURL + " " + event.getTitle();
            message = Util.shorten(message, 140);
            sendNotificationOnlyForParticipants(con, daos, event, message);

            reminderStatus.setSentDateOfBeforeTheDay(now);
            changed = true;
        }

        return changed;
    }

    /**
     * message の内容で、参加確定者にのみメッセージを送る
     * @param event
     * @param message
     * @throws DAOException
     */
    private void sendNotificationOnlyForParticipants(PartakeConnection con, IPartakeDAOs daos, EventEx event, String message) throws DAOException {
        String messageId = daos.getDirectMessageAccess().getFreshId(con);
        Message embryo = new Message(messageId, event.getOwnerId(), message, null, new Date());
        daos.getDirectMessageAccess().put(con, embryo);

        Date deadline = event.getCalculatedDeadline();

        List<EnrollmentEx> participations = EnrollmentDAOFacade.getEnrollmentExs(con, daos, event.getId());
        ParticipationList list = event.calculateParticipationList(participations);

        for (EnrollmentEx p : list.getEnrolledParticipations()) {
            if (!ParticipationStatus.ENROLLED.equals(p.getStatus())) { continue; }
            String envelopeId = daos.getEnvelopeAccess().getFreshId(con);
            Envelope envelope = new Envelope(envelopeId, p.getUserId(), p.getUserId(),
                    messageId, deadline, 0, null, null, DirectMessagePostingType.POSTING_TWITTER_DIRECT, new Date());
            daos.getEnvelopeAccess().put(con, envelope);
            logger.info("sendEnvelope : " + p.getUser().getScreenName() + " : " + embryo.getMessage());
        }
    }

    private EventReminder getEventReminderImpl(PartakeConnection con, IPartakeDAOs daos, String eventId) throws DAOException {
        EventReminder reminder = daos.getEventReminderAccess().find(con, eventId);
        if (reminder == null) {
            return new EventReminder(eventId);
        } else {
            return reminder;
        }
    }

}