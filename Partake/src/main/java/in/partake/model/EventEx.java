package in.partake.model;

import in.partake.model.dto.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
}
