package in.partake.model.dto;

import in.partake.model.dao.DAOException;
import in.partake.resource.PartakeProperties;
import in.partake.service.EventService;
import in.partake.util.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class Event extends PartakeModel<Event> {
    private String id;
    private String shortId;     // event short id // TODO: short id が使えるように実装する
    private String title;       // event title
    private String summary;     // event summary
    private String category;    // event category
    
    private Date deadline;
    private Date beginDate;
    private Date endDate;
    private int capacity;       // how many people can attend?
    private String url;         // URL
    private String place;       // event place
    private String address;
    private String description; // event description
    private String hashTag;
    private String ownerId;
    private List<String> managerScreenNames; 

    private String foreImageId;
    private String backImageId;

    private boolean isPrivate;  // true if the event is private.
    private String passcode;    // passcode to show (if not public)
    
    private Date createdAt;
    
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

    
    public Event() {        
    }
    
    public Event(Event event) {
        try {
            Field[] fields = Event.class.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) { continue; }
                field.set(this, field.get(event));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    public Event(String shortId, String title, String summary, String category, Date deadline, Date beginDate, Date endDate, int capacity,
            String url, String place, String address, String description, String hashTag, String ownerId, List<String> managerScreenNames,
            boolean isPrivate, String passcode, Date createdAt) {
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
        this.createdAt = createdAt;
    }
    
    public Event(String id, String shortId, String title, String summary, String category, Date deadline, Date beginDate, Date endDate, int capacity,
            String url, String place, String address, String description, String hashTag, String ownerId, List<String> managerScreenNames, 
            String foreImageId, String backImageId,
            boolean isPrivate, String passcode, Date createdAt) {
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
        this.createdAt = createdAt;
    }
    
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
    
    public List<String> getManagerScreenNames() {
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

    public Date getCreatedAt() {
        return createdAt;
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

    public void setManagerScreenNames(List<String> managerScreenNames) {
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

    public void setCreatedAt(Date createdAt) {
        checkFrozen();
        this.createdAt = createdAt;
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


