package in.partake.page.event;

import in.partake.application.PartakeSession;
import in.partake.model.EventEx;
import in.partake.model.EventRelationEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.Enrollment;
import in.partake.model.dto.Event;
import in.partake.model.dto.UserPreference;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.page.base.PartakePanel;
import in.partake.resource.I18n;
import in.partake.service.EventService;
import in.partake.service.MessageService;
import in.partake.service.UserService;
import in.partake.util.Util;
import in.partake.wicket.component.AsIsComponent;
import in.partake.wicket.component.InvisibleComponent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class EventEnrollmentPanel extends PartakePanel {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(EventEnrollmentPanel.class);

    private final EventEx event;
    private final UserEx user;
    
    public EventEnrollmentPanel(String id, EventEx event, UserEx user, ParticipationStatus status) {
        super(id);
        
        assert (event != null);
        // user may be null.
        
        this.event = event;
        this.user = user;
        
        Enrollment enrollment = null;
        try {
            if (user != null) {
                enrollment = EventService.get().findEnrollment(event.getId(), user.getId());
            }
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            renderDBError();
            return;
        }
        
        String currentComment = enrollment != null ? enrollment.getComment() : "";
        createForm("enroll.form", "参加する", "よろしくお願いします", ParticipationStatus.ENROLLED, false);
        createForm("reserve.form", "仮参加する", "よろしくお願いします", ParticipationStatus.RESERVED, false);
        createForm("cancel.form", "キャンセルする", "参加できなくなりました", ParticipationStatus.CANCELLED, false);
        createForm("change.form", "コメントを変更する", currentComment, ParticipationStatus.ENROLLED, true);
        
        Date deadline = event.getDeadline();
        if (deadline == null) {
            deadline = event.getBeginDate();
        }
        boolean deadlineOver = deadline.before(new Date());
        
        if (deadlineOver) {
            renderDeadlineOver();
            return;
        } else if (user == null) {
            renderGuest();
            return;
        } else if (ParticipationStatus.ENROLLED.equals(status)) {
            renderEnrolled();
            return;
        } else if (ParticipationStatus.RESERVED.equals(status) && !event.isReservationTimeOver()) {
            renderReserved();
            return;
        } else {
            renderNotEnrolled(event);
            return;
        }
        
        

    }
    
    private void renderDeadlineOver() {
        add(new AsIsComponent("enrollment.over"));
        add(new InvisibleComponent("enrollment.guest"));
        add(new InvisibleComponent("enrollment.enrolled"));
        add(new InvisibleComponent("enrollment.reserved"));
        add(new InvisibleComponent("enrollment.notenrolled"));
    }
    
    private void renderGuest() {
        add(new InvisibleComponent("enrollment.over"));
        add(new AsIsComponent("enrollment.guest"));
        add(new InvisibleComponent("enrollment.enrolled"));
        add(new InvisibleComponent("enrollment.reserved"));
        add(new InvisibleComponent("enrollment.notenrolled"));
    }
    
    private void renderEnrolled() {
        add(new InvisibleComponent("enrollment.over"));
        add(new InvisibleComponent("enrollment.guest"));
        add(new AsIsComponent("enrollment.enrolled"));
        add(new InvisibleComponent("enrollment.reserved"));
        add(new InvisibleComponent("enrollment.notenrolled"));
    }
    
    private void renderReserved() {
        add(new InvisibleComponent("enrollment.over"));
        add(new InvisibleComponent("enrollment.guest"));
        add(new InvisibleComponent("enrollment.enrolled"));
        add(new AsIsComponent("enrollment.reserved"));
        add(new InvisibleComponent("enrollment.notenrolled"));
    }

    private void renderNotEnrolled(EventEx event) {
        add(new InvisibleComponent("enrollment.over"));
        add(new InvisibleComponent("enrollment.guest"));
        add(new InvisibleComponent("enrollment.enrolled"));
        add(new InvisibleComponent("enrollment.reserved"));
        
        AsIsComponent notEnrolledPanel = new AsIsComponent("enrollment.notenrolled");
        if (CollectionUtils.isEmpty(event.getEventRelations())) {
            notEnrolledPanel.add(new InvisibleComponent("enrollment.notenrolled.relatedevent"));
        } else {
            // TODO: event relation を表示する。
            RepeatingView views = new RepeatingView("enrollment.notenrolled.relatedevent");
//            for (EventRelation er : event.getEventRelations()) {
//                
//            }
            notEnrolledPanel.add(views);
        }
        
        add(notEnrolledPanel);                
    }
    
    private void createForm(String baseId, String submitButtonLabel , String defaultComment, final ParticipationStatus status, final boolean changeOnlyComment) {
        final TextArea<String> commentArea = new TextArea<String>(baseId + ".comment", new Model<String>(defaultComment));
        final Button submitButton = new Button(baseId + ".submit", new Model<String>(submitButtonLabel));

        Form<?> enrollForm = new Form(baseId) {
            protected void onSubmit() {
                String comment = commentArea.getModelObject();
                if (comment == null) { comment = ""; }
                changeParticipationStatus(comment, status, changeOnlyComment);
            };
        };
        enrollForm.add(commentArea, submitButton);        
        add(enrollForm);
    }
    

    
    
    private void changeParticipationStatus(String comment, ParticipationStatus status, boolean changesOnlyComment) {
        if (user == null) {
            renderError("ユーザーが NULL になっています。");
            return;
        }
        if (event == null) {
            renderError("イベントが NULL になっています。");
            return;
        }
        if (comment == null) {
            renderError("コメントが NULL になっています。");
            return;
        }

        PartakeSession session = PartakeSession.get();
        
        if (comment.length() > 1024) {
            session.addWarningMessage("コメントが長すぎます。");
            renderPage(EventShowPage.class, new PageParameters().add("id", event.getId()));
            return;
        }
        
        Date now = new Date();
        try {
            Date deadline = event.getCalculatedDeadline();
            
            // もし、締め切りを過ぎている場合、変更が出来なくなる。
            if (deadline.before(now)) {
                session.addErrorMessage("締め切りを過ぎているため変更できません。");
                renderPage(EventShowPage.class, new PageParameters().add("id", event.getId()));
                return;
            }
            
            // 現在の状況が登録されていない場合、
            List<EventRelationEx> relations = EventService.get().getEventRelationsEx(event.getId());
            ParticipationStatus currentStatus = UserService.get().getParticipationStatus(user.getId(), event.getId());          
            if (!currentStatus.isEnrolled()) {
                List<Event> requiredEvents = getRequiredEventsNotEnrolled(relations); 
                if (requiredEvents != null && !requiredEvents.isEmpty()) {
                    session.addErrorMessage("登録必須のイベントがあるため参加登録が出来ません。");
                    renderPage(EventShowPage.class, new PageParameters().add("id", event.getId()));
                    return;
                }
            }
            
            EventService.get().enroll(user.getId(), event.getId(), status, comment, changesOnlyComment, event.isReservationTimeOver());
            
            // Twitter で参加をつぶやく
            if (!changesOnlyComment) { tweetEnrollment(status); }

            renderPage(EventShowPage.class, new PageParameters().add("id", event.getId()));
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            renderDBError();
        }
    }

    /**
     * user が event に登録するために、登録が必要な event たちを列挙する。
     * @param eventId
     * @param user
     * @return
     * @throws DAOException
     */
    private List<Event> getRequiredEventsNotEnrolled(List<EventRelationEx> relations) throws DAOException {
        List<Event> requiredEvents = new ArrayList<Event>();
        for (EventRelationEx relation : relations) {
            if (!relation.isRequired()) { continue; }
            if (relation.getEvent() == null) { continue; }
            if (user != null) {
                ParticipationStatus status = UserService.get().getParticipationStatus(user.getId(), relation.getEvent().getId());
                if (status.isEnrolled()) { continue; }
            }
            requiredEvents.add(relation.getEvent());
        }
        
        return requiredEvents;
    }

    private void tweetEnrollment(ParticipationStatus status) throws DAOException {
        PartakeSession session = PartakeSession.get();
        
        UserPreference pref = UserService.get().getUserPreference(user.getId());
        if (pref == null || !pref.tweetsAttendanceAutomatically()) { return; }
        
        String left = "[PARTAKE] ";
        String right;
        switch (status) {
        case ENROLLED:
            right = " (" + event.getShortenedURL() + ") へ参加します。";
            break;
        case RESERVED:
            right = " (" + event.getShortenedURL() + ") へ参加予定です。";
            break;
        case CANCELLED:
            right = " (" + event.getShortenedURL() + ") への参加を取りやめました。";
            break;
        default:
            right = null;
        }
        
        if (right == null) {
            session.addWarningMessage("参加予定 tweet に失敗しました。");
            return;
        }
        
        String message = left + Util.shorten(event.getTitle(), 140 - Util.codePointCount(left) - Util.codePointCount(right)) + right;
        MessageService.get().tweetMessage(user, message);
    }

}
