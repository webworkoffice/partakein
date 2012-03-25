package in.partake.daemon;

import java.util.Date;
import java.util.List;

import in.partake.base.PartakeException;
import in.partake.base.Util;
import in.partake.model.EnrollmentEx;
import in.partake.model.EventEx;
import in.partake.model.ParticipationList;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.PartakeDAOFactory;
import in.partake.model.dao.base.Transaction;
import in.partake.model.daofacade.EnrollmentDAOFacade;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Envelope;
import in.partake.model.dto.Event;
import in.partake.model.dto.Message;
import in.partake.model.dto.auxiliary.DirectMessagePostingType;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.service.DBService;

public class SendParticipationStatusChangeNotificationsTask extends Transaction<Void> {

    @Override
    protected Void doExecute(PartakeConnection con) throws DAOException, PartakeException {
        sendParticipationStatusChangeNotifications(con);
        return null;
    }
    
    // TODO: 開催前のイベントだけiterateすれば充分かも
    public void sendParticipationStatusChangeNotifications(PartakeConnection con) throws DAOException {
        Date now = new Date();

        PartakeDAOFactory factory = DBService.getFactory();
        try {
            con.beginTransaction();
            DataIterator<Event> it = factory.getEventAccess().getIterator(con);
            try {
                while (it.hasNext()) {
                    Event e = it.next();
                    if (e == null) { continue; }
                    String eventId = e.getId();
                    if (eventId == null) { continue; }
                    EventEx event = EventDAOFacade.getEventEx(con, eventId);
                    if (event == null) { continue; }

                    if (!now.before(event.getBeginDate())) { continue; }

                    List<EnrollmentEx> participations = EnrollmentDAOFacade.getEnrollmentExs(con, eventId);
                    ParticipationList list = event.calculateParticipationList(participations);

                    String enrollingMessage = "[PARTAKE] 補欠から参加者へ繰り上がりました。 " + event.getShortenedURL() + " " + event.getTitle();
                    String cancellingMessage = "[PARTAKE] 参加者から補欠扱い(あるいはキャンセル扱い)に変更になりました。 " + event.getShortenedURL() + " " + event.getTitle();
                    enrollingMessage = Util.shorten(enrollingMessage, 140);
                    cancellingMessage = Util.shorten(cancellingMessage, 140);

                    String okMessageId = null;
                    String ngMessageId = null;

                    // TODO: ここのソース汚い。同一化できる。とくに、あとの２つは一緒。
                    for (Enrollment p : list.getEnrolledParticipations()) {
                        // -- 参加者向

                        ModificationStatus status = p.getModificationStatus();
                        if (status == null) { continue; }

                        switch (status) {
                        case CHANGED: { // 自分自身の力で変化させていた場合は status を enrolled にのみ変更して対応
                            updateLastStatus(con, eventId, p, ModificationStatus.ENROLLED);
                            break;
                        }
                        case NOT_ENROLLED: {
                            if (okMessageId == null) {
                                okMessageId = factory.getDirectMessageAccess().getFreshId(con);
                                Message okEmbryo = new Message(okMessageId, event.getOwnerId(), enrollingMessage, null, new Date());
                                factory.getDirectMessageAccess().put(con, okEmbryo);
                            }

                            updateLastStatus(con, eventId, p, ModificationStatus.ENROLLED);
                            String envelopeId = DBService.getFactory().getEnvelopeAccess().getFreshId(con);
                            Envelope envelope = new Envelope(envelopeId, p.getUserId(), p.getUserId(),
                                    okMessageId, event.getBeginDate(), 0, null, null, DirectMessagePostingType.POSTING_TWITTER_DIRECT, new Date());
                            DBService.getFactory().getEnvelopeAccess().put(con, envelope);

                            break;
                        }
                        case ENROLLED:
                            break;
                        }
                    }

                    for (Enrollment p : list.getSpareParticipations()) {
                        ModificationStatus status = p.getModificationStatus();
                        if (status == null) { continue; }

                        switch (status) {
                        case CHANGED: // 自分自身の力で変化させていた場合は status を not_enrolled にのみ変更して対応
                            updateLastStatus(con, eventId, p, ModificationStatus.NOT_ENROLLED);
                            break;
                        case NOT_ENROLLED:
                            break;
                        case ENROLLED:
                            if (ngMessageId == null) {
                                ngMessageId = factory.getDirectMessageAccess().getFreshId(con);
                                Message ngEmbryo = new Message(ngMessageId, event.getOwnerId(), cancellingMessage, null, new Date());
                                factory.getDirectMessageAccess().put(con, ngEmbryo);
                            }

                            updateLastStatus(con, eventId, p, ModificationStatus.NOT_ENROLLED);

                            String envelopeId = DBService.getFactory().getEnvelopeAccess().getFreshId(con);
                            Envelope envelope = new Envelope(envelopeId, p.getUserId(), p.getUserId(),
                                    ngMessageId, event.getBeginDate(), 0, null, null, DirectMessagePostingType.POSTING_TWITTER_DIRECT, new Date());
                            DBService.getFactory().getEnvelopeAccess().put(con, envelope);
                            break;
                        }
                    }

                    for (Enrollment p : list.getCancelledParticipations()) {
                        ModificationStatus status = p.getModificationStatus();
                        if (status == null) { continue; }

                        switch (status) {
                        case CHANGED: // 自分自身の力で変化させていた場合は status を not_enrolled にのみ変更して対応
                            updateLastStatus(con, eventId, p, ModificationStatus.NOT_ENROLLED);
                            break;
                        case NOT_ENROLLED:
                            break;
                        case ENROLLED:
                            if (ngMessageId == null) {
                                ngMessageId = factory.getDirectMessageAccess().getFreshId(con);
                                Message ngEmbryo = new Message(ngMessageId, event.getOwnerId(), cancellingMessage, null, new Date());
                                factory.getDirectMessageAccess().put(con, ngEmbryo);
                            }

                            updateLastStatus(con, eventId, p, ModificationStatus.NOT_ENROLLED);

                            String envelopeId = DBService.getFactory().getEnvelopeAccess().getFreshId(con);
                            Envelope envelope = new Envelope(envelopeId, p.getUserId(), p.getUserId(),
                                    ngMessageId, event.getBeginDate(), 0, null, null, DirectMessagePostingType.POSTING_TWITTER_DIRECT, new Date());
                            DBService.getFactory().getEnvelopeAccess().put(con, envelope);
                            break;
                        }
                    }
                }
            } finally {
                it.close();
            }
            con.commit();
        } finally {
            con.invalidate();
        }
    }

    private void updateLastStatus(PartakeConnection con, String eventId, Enrollment enrollment, ModificationStatus status) throws DAOException {
        Enrollment newEnrollment = new Enrollment(enrollment);
        newEnrollment.setModificationStatus(status);
        DBService.getFactory().getEnrollmentAccess().put(con, newEnrollment);
    }

}
