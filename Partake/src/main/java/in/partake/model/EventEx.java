package in.partake.model;

import in.partake.model.dto.Event;

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
    private List<EventRelationEx> eventRelations;

    public EventEx(Event event, UserEx owner, String feedId, List<EventRelationEx> eventRelations) {
        super(event);
        this.owner = owner;
        this.feedId = feedId;
        this.eventRelations = eventRelations;
    }

    public UserEx getOwner() {
        return owner;
    }

    public String getFeedId() {
        return feedId;
    }

    public List<EventRelationEx> getEventRelations() {
        return Collections.unmodifiableList(eventRelations);
    }

    public String getDefaultTwitterPromotionMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(getTitle());
        builder.append(" ").append(getEventURL()).append(" ");
        if (getHashTag() != null && !"".equals(getHashTag())) {
            builder.append(" ").append(getHashTag());
        }

        return builder.toString();
    }
}
