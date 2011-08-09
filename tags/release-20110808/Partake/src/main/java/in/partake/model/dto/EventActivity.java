package in.partake.model.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.jdbc.Index;

@Entity(name = "EventActivities")
public class EventActivity extends PartakeModel<EventActivity> {
    @Id
    private String id;
    @Column @Index
    private String eventId;
    @Column
    private String title;
    @Column @Lob
    private String content;
    @Temporal(TemporalType.TIMESTAMP) @Index
    private Date createdAt;

    public EventActivity() {
    }

    public EventActivity(String id, String eventId, String title, String content, Date createdAt) {
        this.id = id;
        this.eventId = eventId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt == null ? null : new Date(createdAt.getTime());
    }

    public EventActivity(EventActivity eventActivity) {
        this(eventActivity.id, eventActivity.eventId, eventActivity.title, eventActivity.content, eventActivity.createdAt);
    }

    @Override
    public EventActivity copy() {
        return new EventActivity(this);
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    // ----------------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EventActivity)) { return false; }

        EventActivity lhs = this;
        EventActivity rhs = (EventActivity) obj;

        if (!ObjectUtils.equals(lhs.id,        rhs.id))        { return false; }
        if (!ObjectUtils.equals(lhs.eventId,   rhs.eventId))   { return false; }
        if (!ObjectUtils.equals(lhs.title,     rhs.title))     { return false; }
        if (!ObjectUtils.equals(lhs.content,   rhs.content))   { return false; }
        if (!ObjectUtils.equals(lhs.createdAt, rhs.createdAt)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(eventId);
        code = code * 37 + ObjectUtils.hashCode(title);
        code = code * 37 + ObjectUtils.hashCode(content);
        code = code * 37 + ObjectUtils.hashCode(createdAt);

        return code;
    }

    // ----------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Date getCreatedAt() {
        return createdAt == null ? null : new Date(createdAt.getTime());
    }

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }

    public void setTitle(String title) {
        checkFrozen();
        this.title = title;
    }

    public void setContent(String content) {
        checkFrozen();
        this.content = content;
    }

    public void setCreatedAt(Date createdAt) {
        checkFrozen();
        this.createdAt = createdAt == null ? null : new Date(createdAt.getTime());
    }
}
