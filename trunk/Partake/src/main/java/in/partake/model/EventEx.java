package in.partake.model;

import java.util.ArrayList;
import java.util.List;

import in.partake.model.dto.Event;
import in.partake.model.dto.User;
import in.partake.model.dto.UserPermission;
import in.partake.util.Util;

public class EventEx extends Event {
    
    private UserEx owner;
    
    public EventEx(Event event, UserEx owner) {
        super(event);
        this.owner = owner;
    }
    
    public UserEx getOwner() {
        return owner;
    }
    
    // ----------------------------------------------------------------------
    
    public boolean hasEndDate() {
        return getEndDate() != null;
    }
    
    public String getDefaultTwitterPromotionMessage() {
        String shortenURL = Util.bitlyShortURL(getEventURL());
        
        StringBuilder builder = new StringBuilder();
        builder.append(getTitle());
        builder.append(" ").append(shortenURL).append(" ");
        if (getHashTag() != null && !"".equals(getHashTag())) {
            builder.append(" ").append(getHashTag());
        }
        
        return builder.toString(); 
    }
    
    /**
     * From participations, distribute participation to enrolled, spare, or cancelled. 
     * @param participations
     * @return
     */
    public ParticipationList calculateParticipationList(List<ParticipationEx> participations) {
        List<ParticipationEx> enrolledParticipations = new ArrayList<ParticipationEx>();
        List<ParticipationEx> spareParticipations = new ArrayList<ParticipationEx>();
        List<ParticipationEx> cancelledParticipations = new ArrayList<ParticipationEx>();
        boolean timeover = isReservationTimeOver();

        for (ParticipationEx participation : participations) {
            switch (participation.getStatus()) {
            case CANCELLED:
                cancelledParticipations.add(participation);
                break;
            case ENROLLED:
                if (getCapacity() == 0 || enrolledParticipations.size() < getCapacity()) {
                    enrolledParticipations.add(participation);
                } else {
                    spareParticipations.add(participation);
                }
                break;
            case NOT_ENROLLED: // TODO: shouldn't happen.
                cancelledParticipations.add(participation);
                break; 
            case RESERVED:
                if (timeover) {
                    cancelledParticipations.add(participation);
                } else if (getCapacity() == 0 || enrolledParticipations.size() < getCapacity()) {                   
                    enrolledParticipations.add(participation);
                } else {
                    spareParticipations.add(participation);
                }
                break;
            }
        }
        
        return new ParticipationList(enrolledParticipations, spareParticipations, cancelledParticipations);
    }

    public boolean hasPermission(UserEx user, UserPermission permission) {
        if (user == null || permission == null) { return false; }
        
        // TODO: Hmm... UserPermission should have a check method. This should be polymorphic.
        switch (permission) {
        case EVENT_EDIT:
            return isOwner(user) || isManager(user);
        case EVENT_REMOVE:
            return isOwner(user);
        case EVENT_PARTICIPATION_LIST:
            return isOwner(user) || isManager(user);
        case EVENT_PRIVATE_EVENT:
            return isOwner(user) || isManager(user);
        case EVENT_PROMOTE:
            return isOwner(user) || isManager(user);
        case EVENT_SEND_MESSAGE:
            return isOwner(user) || isManager(user);
        case EVENT_MAKE_SUBEVENT:
            return isOwner(user);
        case EVENT_EDIT_SUBEVENT:
            return isOwner(user) || isManager(user);
        }
        
        throw new RuntimeException("Unknown permission is being required... This must be a bug.");
    }
    
    /** return true if [user] is the owner of the event. */
    private boolean isOwner(User user) {
        if (user == null || user.getId() == null) { return false; }
        if (getOwnerId() == null) { return false; }
        return getOwnerId().equals(user.getId());
    }
    
    private boolean isManager(UserEx user) {
        if (user == null || user.getId() == null) { return false; }
        if (user.getTwitterLinkage().getScreenName() == null) { return false; }
        
        List<String> screenNames = getManagerScreenNames();
        if (screenNames == null) { return false; }
        for (String screenName : screenNames) {
            if (user.getTwitterLinkage().getScreenName().equals(screenName)) {
                return true;
            }
        }
        
        return false;
    }
}
