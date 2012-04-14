package in.partake.daemon.impl;

import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
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
import in.partake.model.dto.Event;
import in.partake.model.dto.EventNotification;
import in.partake.model.dto.EventReminder;
import in.partake.model.dto.MessageEnvelope;
import in.partake.model.dto.UserNotification;
import in.partake.model.dto.auxiliary.NotificationType;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.resource.PartakeProperties;

import java.util.ArrayList;
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
    private void sendNotificationOnlyForReservedParticipants(PartakeConnection con, IPartakeDAOs daos, Event event, NotificationType notificationType) throws DAOException {
        List<Enrollment> participations = daos.getEnrollmentAccess().findByEventId(con, event.getId());

        List<String> userIds = new ArrayList<String>();
        for (Enrollment participation : participations) {
            if (!ParticipationStatus.RESERVED.equals(participation.getStatus())) { continue; }
            userIds.add(participation.getUserId());
        }

        // TODO: ここから下のコードは、参加者のみにおくる場合と仮参加者のみに送る場合で共有するべき
        String eventNotificationId = daos.getEventNotificationAccess().getFreshId(con);
        EventNotification notification = new EventNotification(eventNotificationId, event.getId(), userIds, notificationType, TimeUtil.getCurrentDateTime(), null);
        daos.getEventNotificationAccess().put(con, notification);

        DateTime invalidAfter = new DateTime(event.getCalculatedDeadline().getTime());
        for (String userId : userIds) {
            String notificationId = daos.getUserNotificationAccess().getFreshId(con);
            UserNotification userNotification = new UserNotification(notificationId, event.getId(), userId, notificationType, TimeUtil.getCurrentDateTime(), null);
            daos.getUserNotificationAccess().put(con, userNotification);

            String envelopeId = daos.getMessageEnvelopeAccess().getFreshId(con);
            MessageEnvelope envelope = MessageEnvelope.createForEventNotification(envelopeId, notificationId, invalidAfter);
            daos.getMessageEnvelopeAccess().put(con, envelope);
            logger.info("sendEnvelope : " + userId + " : " + notificationType);
        }
    }
    private boolean sendEventNotification(PartakeConnection con, IPartakeDAOs daos, EventEx event, EventReminder reminderStatus, String topPath, Date now) throws DAOException {
        Date beginDate = event.getBeginDate();
        Date deadline = event.getCalculatedDeadline();
        boolean changed = false;

        // TODO: isBeforeDeadline() とかわかりにくいな。
        // 締め切り１日前になっても RESERVED ステータスの人がいればメッセージを送付する。
        // 次の条件でメッセージを送付する
        //  1. 現在時刻が締め切り２４時間前よりも後
        //  2. 次の条件のいずれか満たす
        //    2.1. まだメッセージが送られていない
        //    2.2. 前回送った時刻が締め切り２４時間以上前で、かつ送った時刻より１時間以上経過している。

        if (needsToSend(now, TimeUtil.oneDayBefore(deadline), reminderStatus.getSentDateOfBeforeDeadlineOneday())) {
            sendNotificationOnlyForReservedParticipants(con, daos, event, NotificationType.ONE_DAY_BEFORE_REMINDER_FOR_RESERVATION);

            reminderStatus.setSentDateOfBeforeDeadlineOneday(now);
            changed = true;
        }

        // 締め切り１２時間前になっても RESERVED な人がいればメッセージを送付する。
        if (needsToSend(now, TimeUtil.halfDayBefore(deadline), reminderStatus.getSentDateOfBeforeDeadlineHalfday())) {
            sendNotificationOnlyForReservedParticipants(con, daos, event, NotificationType.HALF_DAY_BEFORE_REMINDER_FOR_RESERVATION);

            reminderStatus.setSentDateOfBeforeDeadlineHalfday(now);
            changed = true;
        }

        // イベント１日前で、参加が確定している人にはメッセージを送付する。
        // 参加が確定していない人には、RESERVED なメッセージが送られている。
        if (needsToSend(now, TimeUtil.oneDayBefore(beginDate), reminderStatus.getSentDateOfBeforeTheDay())) {
            sendNotificationOnlyForParticipants(con, daos, event, NotificationType.EVENT_ONEDAY_BEFORE_REMINDER);

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
    private void sendNotificationOnlyForParticipants(PartakeConnection con, IPartakeDAOs daos, EventEx event, NotificationType notificationType) throws DAOException {
        List<EnrollmentEx> participations = EnrollmentDAOFacade.getEnrollmentExs(con, daos, event);
        ParticipationList list = event.calculateParticipationList(participations);

        List<String> userIds = new ArrayList<String>();
        for (EnrollmentEx p : list.getEnrolledParticipations()) {
            if (!ParticipationStatus.ENROLLED.equals(p.getStatus()))
                continue;
            userIds.add(p.getUserId());
        }

        String eventNotificationId = daos.getEventNotificationAccess().getFreshId(con);
        EventNotification notification = new EventNotification(eventNotificationId, event.getId(), userIds, notificationType, TimeUtil.getCurrentDateTime(), null);
        daos.getEventNotificationAccess().put(con, notification);

        DateTime invalidAfter = new DateTime(event.getCalculatedDeadline().getTime());
        for (String userId : userIds) {
            String notificationId = daos.getUserNotificationAccess().getFreshId(con);
            UserNotification userNotification = new UserNotification(notificationId, event.getId(), userId, notificationType, TimeUtil.getCurrentDateTime(), null);
            daos.getUserNotificationAccess().put(con, userNotification);

            String envelopeId = daos.getMessageEnvelopeAccess().getFreshId(con);
            MessageEnvelope envelope = MessageEnvelope.createForEventNotification(envelopeId, notificationId, invalidAfter);
            daos.getMessageEnvelopeAccess().put(con, envelope);
            logger.info("sendEnvelope : " + userId + " : " + notificationType);
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
