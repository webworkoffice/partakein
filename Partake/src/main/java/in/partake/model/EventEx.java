package in.partake.model;

import in.partake.model.dto.Event;
import in.partake.model.dto.User;
import in.partake.model.dto.auxiliary.UserPermission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

/**
 * event with related data.
 * @author shinyak
 *
 */
public class EventEx extends Event {
    private UserEx owner;
    private String feedId;
    private String cachedShortenedURL;
    private List<EventRelationEx> eventRelations;
    
    public EventEx(Event event, UserEx owner, String feedId, String cachedShortenedURL, List<EventRelationEx> eventRelations) {
        super(event);
        this.owner = owner;
        this.feedId = feedId;
        this.cachedShortenedURL = cachedShortenedURL;
        this.eventRelations = eventRelations;
    }
    
    public UserEx getOwner() {
        return owner;
    }
    
    public String getFeedId() {
        return feedId;
    }
    
    public String getCachedShortenedURL() {
        return cachedShortenedURL;
    }
    
    public String getShortenedURL() {
        if (cachedShortenedURL != null) { return cachedShortenedURL; }
        return getEventURL();
    }
    
    public List<EventRelationEx> getEventRelations() {
        return Collections.unmodifiableList(eventRelations);
    }
    
    public String toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", getId());
        obj.put("shortId", getShortId());
        obj.put("title", getTitle());
        obj.put("summary", getSummary());
        obj.put("category", getCategory());
        obj.put("deadline", getDeadline() != null ? getDeadline().getTime() : null);
        obj.put("beginDate", getBeginDate() != null ? getBeginDate().getTime() : null);
        obj.put("endDate", getEndDate() != null ? getEndDate().getTime() : null);
        obj.put("capacity", getCapacity());
        obj.put("url", getUrl());
        obj.put("place", getPlace());
        obj.put("address", getAddress());
        obj.put("description", getAddress());
        obj.put("hashTag", getAddress());
        obj.put("managerScreenNames", getManagerScreenNames());
        
        // TODO: Hmm...
        
//        @Column
//        private String ownerId;
//
//        @Column
//        private String foreImageId;
//        @Column
//        private String backImageId;
//
//        @Column
//        private boolean isPrivate;  // true if the event is private.
//        @Column
//        private String passcode;    // passcode to show (if not public)
//        
//        @Column
//        private boolean isPreview;    // true if the event is preview.
//        @Column
//        private boolean isRemoved;
//        
//        @Column
//        private Date createdAt;     //
//        @Column
//        private Date modifiedAt;    //
//        @Column
//        private int revision;       // used for RSS.
        
        return obj.toString();
    }
    
    // ----------------------------------------------------------------------
    
    public boolean hasEndDate() {
        return getEndDate() != null;
    }
    
    public String getDefaultTwitterPromotionMessage() {
        String shortenedURL = getShortenedURL(); 
        
        StringBuilder builder = new StringBuilder();
        builder.append(getTitle());
        builder.append(" ").append(shortenedURL).append(" ");
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
    public ParticipationList calculateParticipationList(List<EnrollmentEx> participations) {
        List<EnrollmentEx> enrolledParticipations = new ArrayList<EnrollmentEx>();
        List<EnrollmentEx> spareParticipations = new ArrayList<EnrollmentEx>();
        List<EnrollmentEx> cancelledParticipations = new ArrayList<EnrollmentEx>();
        boolean timeover = isReservationTimeOver();
        
        int reservedEnrolled = 0;
        int reservedSpare = 0;

        for (EnrollmentEx participation : participations) {
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
            case RESERVED:
                if (timeover) {
                    cancelledParticipations.add(participation);
                } else if (getCapacity() == 0 || enrolledParticipations.size() < getCapacity()) {                   
                    enrolledParticipations.add(participation);
                    ++reservedEnrolled;
                } else {
                    spareParticipations.add(participation);
                    ++reservedSpare;
                }
                break;
            case NOT_ENROLLED: // TODO: shouldn't happen.
                cancelledParticipations.add(participation);
                break; 
            }
        }
        
        return new ParticipationList(enrolledParticipations, spareParticipations, cancelledParticipations, reservedEnrolled, reservedSpare);
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
        case EVENT_SEND_MESSAGE:
            return isOwner(user) || isManager(user);
        case EVENT_REMOVE_COMMENT:
        	return isOwner(user) || isManager(user);
        case EVENT_EDIT_PARTICIPANTS:
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
        if (getManagerScreenNames() == null) { return false; }
        
        String[] screenNames = getManagerScreenNames().split(",");
        for (String screenName : screenNames) {
            if (user.getTwitterLinkage().getScreenName().equals(StringUtils.trim(screenName))) {
                return true;
            }
        }
        
        return false;
    }
}
