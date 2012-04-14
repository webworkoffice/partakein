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
}
