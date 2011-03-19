package in.partake.page.event;

import in.partake.model.EventEx;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventRelation;
import in.partake.model.dto.User;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.wicket.component.AsIsComponent;
import in.partake.wicket.component.InvisibleComponent;

import java.util.Date;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

public class EventEnrollmentPanel extends Panel {
    private static final long serialVersionUID = 1L;
    
    public EventEnrollmentPanel(String id, EventEx event, User user, ParticipationStatus status) {
        super(id);
        
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
}
