package in.partake.page.event;

import in.partake.model.EnrollmentEx;
import in.partake.model.EventEx;
import in.partake.model.ParticipationList;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.auxiliary.ParticipationStatus;
import in.partake.page.base.PartakePanel;
import in.partake.resource.I18n;
import in.partake.service.EventService;
import in.partake.wicket.component.InvisibleComponent;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

public class EventParticipantsPanel extends PartakePanel {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(EventParticipantsPanel.class);
    
    public EventParticipantsPanel(String id, EventEx event) {
        super(id);
        
        try {
            List<EnrollmentEx> participations = EventService.get().getEnrollmentEx(event.getId());
            if (participations == null) { 
                logger.error("Getting participation failed.");
                renderError("Getting participation failed.");
                return;
            }
            
            ParticipationList participationList = event.calculateParticipationList(participations);
            List<EnrollmentEx> enrolledParticipations = participationList.getEnrolledParticipations();
            List<EnrollmentEx> spareParticipations = participationList.getSpareParticipations();
            List<EnrollmentEx> cancelledParticipations = participationList.getCancelledParticipations();
            
            // summary
            add(new Label("enrolled.count",       String.valueOf(enrolledParticipations.size())));
            add(new Label("enrolled.spare.count", String.valueOf(spareParticipations.size())));
            add(new Label("reserved.count",       String.valueOf(participationList.getReservedEnrolled())));
            add(new Label("reserved.spare.count", String.valueOf(participationList.getReservedSpare())));

            // 参加者
            add(new Label("list.enrolled.count", String.valueOf(enrolledParticipations.size())));
            if (enrolledParticipations.size() > 0) {
                add(new ParticipationListView("list.enrolled.user", enrolledParticipations));                   
                add(new InvisibleComponent("list.enrolled.nouser"));
            } else {
                add(new InvisibleComponent("list.enrolled.user"));
                add(new Label("list.enrolled.nouser", "現在参加者はいません。"));
            }
            
            // 補欠者
            if (spareParticipations.size() > 0) {
                add(new Label("list.spare.count", String.valueOf(spareParticipations.size())));
                add(new ParticipationListView("list.spare.user", spareParticipations));                   
            } else {
                add(new InvisibleComponent("list.spare.count"));
                add(new InvisibleComponent("list.spare.user"));
            }

            // キャンセル
            if (cancelledParticipations.size() > 0) {
                add(new Label("list.cancelled.count", String.valueOf(cancelledParticipations.size())));
                add(new ParticipationListView("list.cancelled.user", cancelledParticipations));                   
            } else {
                add(new InvisibleComponent("list.cancelled.count"));
                add(new InvisibleComponent("list.cancelled.user"));
            }
        } catch (DAOException e) {
            logger.error(I18n.t(I18n.DATABASE_ERROR), e);
            renderDBError();
        }
    }
}

class ParticipationListView extends ListView<EnrollmentEx> {
    private static final long serialVersionUID = 1L;
    
    public ParticipationListView(String id, List<EnrollmentEx> enrollments) {
        super(id, enrollments);
    }

    @Override
    protected void populateItem(ListItem<EnrollmentEx> item) {
        EnrollmentEx enrollment = item.getModelObject();
        String id = getId();
        
        item.add(new ContextImage(id + ".photo", enrollment.getUser().getProfileImageURL())); 
        item.add(new ExternalLink(id + ".name", "users/" + enrollment.getUserId(), enrollment.getUser().getScreenName()));

        if (ParticipationStatus.RESERVED.equals(enrollment.getStatus())) {
            item.add(new ContextImage(id + ".reserved", "/images/reserved1.png"));
        } else {
            item.add(new InvisibleComponent(id + ".reserved"));
        }
        
        if (enrollment.isVIP()) {
            item.add(new ContextImage(id + ".vip", "/images/crown.png"));
        } else {
            item.add(new InvisibleComponent(id + ".vip"));
        }
        
        if (enrollment.getPriority() > 0) {
            item.add(new ContextImage(id + ".priority", "/images/star.png"));
        } else {
            item.add(new InvisibleComponent(id + ".priority"));
        }
        
        item.add(new Label(id + ".comment", enrollment.getComment()));
    }
    
}
