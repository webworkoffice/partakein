package in.partake.model.dto;

import in.partake.model.dao.DAOException;
import in.partake.resource.PartakeProperties;
import in.partake.service.EventService;
import in.partake.util.Util;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;

@Entity(name = "Events")
public class Event extends PartakeModel<Event> {
    @Id
    private String id;
    @Column
    private String shortId;     // event short id // TODO: short id が使えるように実装する
    @Column
    private String title;       // event title
    @Column
    private String summary;     // event summary
    @Column
    private String category;    // event category
    
    @Column
    private Date deadline;
    @Column
    private Date beginDate;
    @Column
    private Date endDate;
    @Column
    private int capacity;       // how many people can attend?
    @Column(length = 10000) 
    private String url;         // URL
    @Column
    private String place;       // event place
    @Column
    private String address;
    @Column(length = 1024 * 1024)
    private String description; // event description
    @Column
    private String hashTag;
    @Column
    private String ownerId;
    @Column // TODO: これどうするんだ
    private String managerScreenNames;

    @Column
    private String foreImageId;
    @Column
    private String backImageId;

    @Column
    private boolean isPrivate;  // true if the event is private.
    @Column
    private String passcode;    // passcode to show (if not public)
    
    @Column
    private boolean isPreview;    // true if the event is preview.
   
    @Column
    private Date createdAt;     //
    @Column
    private Date modifiedAt;    //
    @Column
    private int revision;       // used for RSS.
    
    // begin date 順に並べる comparator 
    public static Comparator<Event> getComparatorBeginDateAsc() {
        return new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                if (lhs == rhs) { return 0; }
                if (lhs == null) { return -1; }
                if (rhs == null) { return 1; }
                if (!lhs.getBeginDate().equals(rhs.getBeginDate())) {
                    if (lhs.getBeginDate().before(rhs.getBeginDate())) { return -1; }
                    else { return 1; }
                } else { 
                    return lhs.getId().compareTo(rhs.getId());
                }
            }
        };
    }

    // ----------------------------------------------------------------------
    // ctors
    
    public Event() {        
    }
    
    public Event(Event event) {
    	this.id = event.id;
    	this.shortId = event.shortId;
    	this.title = event.title;
    	this.summary = event.summary;
    	this.category = event.category;
    	this.deadline = event.deadline == null ? null : (Date) event.deadline.clone();
    	this.beginDate = event.beginDate == null ? null : (Date) event.beginDate.clone();
    	this.endDate = event.endDate == null ? null : (Date) event.endDate.clone();
    	this.capacity = event.capacity;
    	this.url = event.url;
    	this.place = event.place;
    	this.address = event.address;
    	this.description = event.description;
    	this.hashTag = event.hashTag;
    	this.ownerId = event.ownerId;
    	this.managerScreenNames = event.managerScreenNames;
    	this.foreImageId = event.foreImageId;
    	this.backImageId = event.backImageId;
    	this.isPrivate = event.isPrivate;
    	this.passcode = event.passcode;
    	this.isPreview = event.isPreview;
    	this.createdAt = event.createdAt == null ? null : (Date) event.createdAt.clone();
    	this.modifiedAt = event.modifiedAt == null ? null : (Date) event.modifiedAt.clone();
    	this.revision = event.revision;
    	
    }
    
    public Event(String shortId, String title, String summary, String category, Date deadline, Date beginDate, Date endDate, int capacity,
            String url, String place, String address, String description, String hashTag, String ownerId, String managerScreenNames,
            boolean isPrivate, String passcode, boolean isPreview, Date createdAt, Date modifiedAt) {
        this.id = null;
        this.shortId = shortId;
        this.title = title;
        this.summary = summary;
        this.category = category;
        this.deadline = deadline;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.capacity = capacity;
        this.url = url;
        this.place = place;
        this.address = address;
        this.description = description;
        this.hashTag = hashTag;
        this.ownerId = ownerId;
        this.managerScreenNames = managerScreenNames;
        
        this.foreImageId = null;
        this.backImageId = null;
        
        this.isPrivate = isPrivate;
        this.passcode = passcode;
        
        this.isPreview = isPreview;
        
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.revision = 0;
    }
    
    public Event(String id, String shortId, String title, String summary, String category, Date deadline, Date beginDate, Date endDate, int capacity,
            String url, String place, String address, String description, String hashTag, String ownerId, String managerScreenNames, 
            String foreImageId, String backImageId,
            boolean isPrivate, String passcode, boolean isPreview, Date createdAt, Date modifiedAt, int revision) {
        this.id = id;
        this.shortId = shortId;
        this.title = title;
        this.summary = summary;
        this.category = category;
        this.deadline = deadline;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.capacity = capacity;
        this.url = url;
        this.place = place;
        this.address = address;
        this.description = description;
        this.hashTag = hashTag;
        this.ownerId = ownerId;
        this.managerScreenNames = managerScreenNames;
        
        this.foreImageId = foreImageId;
        this.backImageId = backImageId;
        
        this.isPrivate = isPrivate;
        this.passcode = passcode;
        this.isPreview = isPreview;
        
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.revision = revision;
    }
    
    @Override
    public Object getPrimaryKey() {
        return id;
    }

    @Override
    public Event copy() {
        return new Event(this);
    }
    
    // ----------------------------------------------------------------------
    // equals method 
    
    // TODO: should be implemented.
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Event)) { return false; }
        
        Event lhs = this;
        Event rhs = (Event) obj;
        
        if (!ObjectUtils.equals(lhs.id, rhs.id)) { return false; }
        if (!ObjectUtils.equals(lhs.shortId, rhs.shortId)) { return false; }
        if (!ObjectUtils.equals(lhs.title, rhs.title)) { return false; }
        if (!ObjectUtils.equals(lhs.summary, rhs.summary)) { return false; }
        if (!ObjectUtils.equals(lhs.category, rhs.category)) { return false; }
        if (!ObjectUtils.equals(lhs.deadline, rhs.deadline)) { return false; }
        if (!ObjectUtils.equals(lhs.beginDate, rhs.beginDate)) { return false; }
        if (!ObjectUtils.equals(lhs.endDate, rhs.endDate)) { return false; }
        if (!ObjectUtils.equals(lhs.capacity, rhs.capacity)) { return false; }
        if (!ObjectUtils.equals(lhs.url, rhs.url)) { return false; }
        if (!ObjectUtils.equals(lhs.place, rhs.place)) { return false; }
        if (!ObjectUtils.equals(lhs.address, rhs.address)) { return false; }
        if (!ObjectUtils.equals(lhs.description, rhs.description)) { return false; }
        if (!ObjectUtils.equals(lhs.hashTag, rhs.hashTag)) { return false; }
        if (!ObjectUtils.equals(lhs.ownerId, rhs.ownerId)) { return false; }
        if (!ObjectUtils.equals(lhs.managerScreenNames, rhs.managerScreenNames)) { return false; }
        if (!ObjectUtils.equals(lhs.foreImageId, rhs.foreImageId)) { return false; }
        if (!ObjectUtils.equals(lhs.backImageId, rhs.backImageId)) { return false; }
        if (!ObjectUtils.equals(lhs.isPrivate, rhs.isPrivate)) { return false; }
        if (!ObjectUtils.equals(lhs.passcode, rhs.passcode)) { return false; }
        if (!ObjectUtils.equals(lhs.isPreview, rhs.isPreview)) { return false; }
        if (!ObjectUtils.equals(lhs.createdAt, rhs.createdAt)) { return false; }
        if (!ObjectUtils.equals(lhs.modifiedAt, rhs.modifiedAt)) { return false; }
        if (!ObjectUtils.equals(lhs.revision, rhs.revision)) { return false; }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        int code = 0;        
        
        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(shortId);
        code = code * 37 + ObjectUtils.hashCode(title);
        code = code * 37 + ObjectUtils.hashCode(summary);
        code = code * 37 + ObjectUtils.hashCode(category);
        code = code * 37 + ObjectUtils.hashCode(deadline);
        code = code * 37 + ObjectUtils.hashCode(beginDate);
        code = code * 37 + ObjectUtils.hashCode(endDate);
        code = code * 37 + ObjectUtils.hashCode(capacity);
        code = code * 37 + ObjectUtils.hashCode(url);
        code = code * 37 + ObjectUtils.hashCode(place);
        code = code * 37 + ObjectUtils.hashCode(address);
        code = code * 37 + ObjectUtils.hashCode(description);
        code = code * 37 + ObjectUtils.hashCode(hashTag);
        code = code * 37 + ObjectUtils.hashCode(ownerId);
        code = code * 37 + ObjectUtils.hashCode(managerScreenNames);
        code = code * 37 + ObjectUtils.hashCode(foreImageId);
        code = code * 37 + ObjectUtils.hashCode(backImageId);
        code = code * 37 + ObjectUtils.hashCode(isPrivate);
        code = code * 37 + ObjectUtils.hashCode(passcode);
        code = code * 37 + ObjectUtils.hashCode(isPreview);
        code = code * 37 + ObjectUtils.hashCode(createdAt);
        code = code * 37 + ObjectUtils.hashCode(modifiedAt);
        code = code * 37 + ObjectUtils.hashCode(revision);
        
        return code;
    }
    
    // ----------------------------------------------------------------------
    // 
    
    public String getId() {
        return this.id;
    }
    
    public void setForeImageId(String foreImageId) {
        this.foreImageId = foreImageId;
    }
    
    public void setBackImageId(String backImageId) {
        this.backImageId = backImageId;
    }
    
    public String getShortId() {
        return shortId;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }
    
    public String getCategory() {
        return category;
    }

    public Date getDeadline() {
        return deadline;
    }
    
    public Date getBeginDate() {
        return beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getUrl() {
        return url;
    }

    public String getPlace() {
        return place;
    }

    public String getAddress() {
        return address;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getHashTag() {
        return hashTag;
    }

    public String getOwnerId() {
        return ownerId;
    }
    
    public String getManagerScreenNames() {
        return managerScreenNames;
    }
    
    public String getForeImageId() {
        return foreImageId;
    }
    
    public String getBackImageId() {
        return backImageId;
    }
    
    public boolean isPrivate() {
        return isPrivate;
    }

    public String getPasscode() {
        return passcode;
    }
    
    public boolean isPreview() {
        return isPreview;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
    
    public Date getModifiedAt() {
    	return modifiedAt;
    }
    
    public int getRevision() {
        return revision;
    }
    
    // ----------------------------------------------------------------------
    
    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setShortId(String shortId) {
        checkFrozen();
        this.shortId = shortId;
    }

    public void setTitle(String title) {
        checkFrozen();
        this.title = title;
    }

    public void setSummary(String summary) {
        checkFrozen();
        this.summary = summary;
    }

    public void setCategory(String category) {
        checkFrozen();
        this.category = category;
    }

    public void setDeadline(Date deadline) {
        checkFrozen();
        this.deadline = deadline;
    }

    public void setBeginDate(Date beginDate) {
        checkFrozen();
        this.beginDate = beginDate;
    }

    public void setEndDate(Date endDate) {
        checkFrozen();
        this.endDate = endDate;
    }

    public void setCapacity(int capacity) {
        checkFrozen();
        this.capacity = capacity;
    }

    public void setUrl(String url) {
        checkFrozen();
        this.url = url;
    }

    public void setPlace(String place) {
        checkFrozen();
        this.place = place;
    }

    public void setAddress(String address) {
        checkFrozen();
        this.address = address;
    }

    public void setDescription(String description) {
        checkFrozen();
        this.description = description;
    }

    public void setHashTag(String hashTag) {
        checkFrozen();
        this.hashTag = hashTag;
    }

    public void setOwnerId(String ownerId) {
        checkFrozen();
        this.ownerId = ownerId;
    }

    public void setManagerScreenNames(String managerScreenNames) {
        checkFrozen();
        this.managerScreenNames = managerScreenNames;
    }
    
    public void setPrivate(boolean isPrivate) {
        checkFrozen();
        this.isPrivate = isPrivate;
    }

    public void setPasscode(String passcode) {
        checkFrozen();
        this.passcode = passcode;
    }

    public void setPreview(boolean isPreview) {
        checkFrozen();
        this.isPreview = isPreview;
    }

    public void setCreatedAt(Date createdAt) {
        checkFrozen();
        this.createdAt = createdAt;
    }
    
    public void setModifiedAt(Date modifiedAt) {
    	checkFrozen();
    	this.modifiedAt = modifiedAt;
    }
    
    public void setRevision(int revision) {
        checkFrozen();
        this.revision = revision;
    }
    
    // ----------------------------------------------------------------------
    
    public String getEventURL() {
        String topPath = PartakeProperties.get().getTopPath();
        String thispageURL = topPath + "/events/" + getId();

        return thispageURL;
    }

    /**
     * true if reservation is acceptable now.
     * @return
     */
    public boolean canReserve() {
        Date now = new Date();
        Date deadline = getDeadline();
        if (deadline == null) { deadline = getBeginDate(); }
                
        return now.before(Util.halfDayBefore(deadline));
    }
    
    /**
     * get a calculated deadline. If deadline is set, it is returned. Otherwise, beginDate is deadline.
     * @return
     */
    public Date getCalculatedDeadline() {
        if (getDeadline() != null) { return getDeadline(); }
        return new Date(getBeginDate().getTime());
    }
    
    public Date getCalculatedReservationDeadline() {
        if (getDeadline() != null) { return getDeadline(); }
        return new Date(getBeginDate().getTime() - 1000 * 3600 * 3);
    }
    
    /**
     * true if all reservations are cancelled.
     * @return
     */
    public boolean isReservationTimeOver() {        
        Date now = new Date();
        Date deadline = getCalculatedReservationDeadline();
        
        return deadline.before(now);
    }
    
    // XXX: this methods will access database.
    // XXX: Hmm...
    @Deprecated
    public int fetchNumOfEnrolledUsers() {
        try {
            // TODO: something wrong... 
            return EventService.get().getNumOfEnrolledUsers(getId());
        } catch (DAOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}


