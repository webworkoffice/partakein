package in.partake.model.dto;

import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.model.dto.auxiliary.EnqueteQuestion;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.model.dto.auxiliary.EventRelation;
import in.partake.resource.Constants;
import in.partake.resource.PartakeProperties;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

public class Event extends PartakeModel<Event> {
    private String id;
    private String title;       // event title
    private String summary;     // event summary
    private String category;    // event category
    private DateTime beginDate;
    private DateTime endDate;
    private String url;         // URL
    private String place;       // event place
    private String address;
    private String description; // event description
    private String hashTag;
    private String ownerId;
    private String managerScreenNames; // TODO: これどうするんだ
    private String foreImageId;
    private String backImageId;

    private String passcode;    // passcode to show (if not public)
    private boolean draft;    // true if the event is still in preview.

    private List<EventRelation> eventRelations;
    private List<EnqueteQuestion> enquetes;

    private DateTime createdAt;     //
    private DateTime modifiedAt;    //
    private int revision;       // used for RSS.

    // begin date 順に並べる comparator
    // TODO: Should be purged! This should be done in DB.
    public static Comparator<Event> getComparatorBeginDateAsc() {
        return new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                if (lhs == rhs) { return 0; }
                if (lhs == null) { return -1; }
                if (rhs == null) { return 1; }
                if (!lhs.getBeginDate().equals(rhs.getBeginDate())) {
                    if (lhs.getBeginDate().isBefore(rhs.getBeginDate())) { return -1; }
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
        this.id = null;
        this.title = "";
        this.summary = "";
        this.category = EventCategory.getCategories().get(0).getKey();
        this.beginDate = TimeUtil.oneDayAfter(TimeUtil.getCurrentDateTime());
        this.endDate = null;
        this.url = "";
        this.place = "";
        this.address = "";
        this.description = "";
        this.hashTag = "";
        this.ownerId = "";
        this.managerScreenNames = "";
        this.foreImageId = null;
        this.backImageId = null;
        this.passcode = null;
        this.draft = true;
        this.eventRelations = new ArrayList<EventRelation>();
        this.enquetes = null;
        this.createdAt = TimeUtil.getCurrentDateTime();
        this.modifiedAt = null;
        this.revision = 1;
    }

    public Event(Event event) {
        this.id = event.id;
        this.title = event.title;
        this.summary = event.summary;
        this.category = event.category;
        this.beginDate = event.beginDate;
        this.endDate = event.endDate;
        this.url = event.url;
        this.place = event.place;
        this.address = event.address;
        this.description = event.description;
        this.hashTag = event.hashTag;
        this.ownerId = event.ownerId;
        this.managerScreenNames = event.managerScreenNames;
        this.foreImageId = event.foreImageId;
        this.backImageId = event.backImageId;
        this.passcode = event.passcode;
        this.draft = event.draft;
        if (event.eventRelations != null)
            this.eventRelations = new ArrayList<EventRelation>(event.eventRelations);
        if (event.enquetes != null)
            this.enquetes = new ArrayList<EnqueteQuestion>(event.enquetes);
        this.createdAt = event.createdAt;
        this.modifiedAt = event.modifiedAt;
        this.revision = event.revision;
    }

    public Event(JSONObject json) {
        this.id = json.getString("id");
        this.title = json.getString("title");
        this.summary = json.getString("summary");
        this.category = json.getString("category");
        if (json.containsKey("beginDate"))
            this.beginDate = new DateTime(json.getLong("beginDate"));
        if (json.containsKey("endDate"))
            this.endDate = new DateTime(json.getLong("endDate"));
        this.url = json.optString("url", null);
        this.place = json.optString("place", null);
        this.address = json.optString("address", null);
        this.description = json.getString("description");
        this.hashTag = json.optString("hashTag", null);
        this.ownerId = json.getString("ownerId");
        this.managerScreenNames = json.optString("managerScreenNames", null);
        this.foreImageId = json.optString("foreImageId", null);
        this.backImageId = json.optString("backImageId", null);
        this.passcode = json.optString("passcode", null);
        this.draft = json.optBoolean("draft", false);
        {
            JSONArray ar = json.optJSONArray("relations");
            if (ar != null) {
                this.eventRelations = new ArrayList<EventRelation>();
                for (int i = 0; i < ar.size(); ++i)
                    eventRelations.add(new EventRelation(ar.getJSONObject(i)));
            }
        }
        {
            JSONArray ar = json.optJSONArray("enquetes");
            if (ar != null) {
                this.enquetes = new ArrayList<EnqueteQuestion>();
                for (int i = 0; i < ar.size(); ++i)
                    enquetes.add(new EnqueteQuestion(ar.getJSONObject(i)));
            }
        }

        if (json.containsKey("createdAt"))
            this.createdAt = new DateTime(json.getLong("createdAt"));
        if (json.containsKey("modifiedAt"))
            this.modifiedAt = new DateTime(json.getLong("modifiedAt"));
        this.revision = json.optInt("revision", 1);
    }

    public Event(String id, String title, String summary, String category, DateTime beginDate, DateTime endDate,
            String url, String place, String address, String description, String hashTag, String ownerId, String managerScreenNames,
            String foreImageId, String backImageId,
            String passcode, boolean draft,
            List<EventRelation> relations, List<EnqueteQuestion> enquetes,
            DateTime createdAt, DateTime modifiedAt, int revision) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.category = category;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.url = url;
        this.place = place;
        this.address = address;
        this.description = description;
        this.hashTag = hashTag;
        this.ownerId = ownerId;
        this.managerScreenNames = managerScreenNames;

        this.foreImageId = foreImageId;
        this.backImageId = backImageId;

        this.passcode = passcode;
        this.draft = draft;
        if (relations != null)
            this.eventRelations = new ArrayList<EventRelation>(relations);
        if (enquetes != null)
            this.enquetes = new ArrayList<EnqueteQuestion>(enquetes);

        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.revision = revision;
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    /** JSON string for external clients.
     * TODO: All Date should be long instead of Formatted date. However, maybe some clients uses this values... What should we do?
     * Maybe we should take a version number in request query. The version 2 format should obey the rule.
     */
    public JSONObject toSafeJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("title", title);
        obj.put("summary", summary);
        obj.put("category", category);
        // TODO Localeは外部ファイルなどで設定可能にする
        // TODO: We don't want to use locale. Instead, we just return date as a long value.
        DateFormat format = new SimpleDateFormat(Constants.JSON_DATE_FORMAT, Locale.getDefault());
        if (beginDate != null) {
            // TODO: beginDate should be deprecated.
            obj.put("beginDate", format.format(beginDate));
            obj.put("beginDateTime", beginDate.getTime());
        }
        if (endDate != null) {
            // TODO: endDate should be deprecated.
            obj.put("endDate", format.format(endDate));
            obj.put("endDateTime", endDate.getTime());
        }
        obj.put("url", url);
        obj.put("place", place);
        obj.put("address", address);
        obj.put("description", description);
        obj.put("hashTag", hashTag);
        obj.put("ownerId", ownerId);
        if (managerScreenNames != null)
            obj.put("managerScreenNames", managerScreenNames);
        obj.put("foreImageId", foreImageId);
        obj.put("backImageId", backImageId);
        obj.put("passcode", passcode);
        obj.put("draft", draft);

        obj.put("relations", Util.toJSONArray(eventRelations));
        obj.put("enquetes", Util.toJSONArray(enquetes));

        if (createdAt != null) {
            obj.put("createdAt", format.format(createdAt));
        }
        if (modifiedAt != null) {
            obj.put("modofiedAt", format.format(modifiedAt));
        }
        obj.put("revision", revision);

        return obj;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("title", title);
        obj.put("summary", summary);
        obj.put("category", category);
        if (beginDate != null)
            obj.put("beginDate", beginDate.getTime());
        if (endDate != null)
            obj.put("endDate", endDate.getTime());
        obj.put("url", url);
        obj.put("place", place);
        obj.put("address", address);
        obj.put("description", description);
        obj.put("hashTag", hashTag);
        obj.put("ownerId", ownerId);
        if (managerScreenNames != null)
            obj.put("managerScreenNames", managerScreenNames);
        obj.put("foreImageId", foreImageId);
        obj.put("backImageId", backImageId);
        obj.put("passcode", passcode);
        obj.put("draft", draft);

        if (eventRelations != null)
            obj.put("relations", Util.toJSONArray(eventRelations));
        if (enquetes != null)
            obj.put("enquetes", Util.toJSONArray(enquetes));

        if (createdAt != null)
            obj.put("createdAt", createdAt.getTime());
        if (modifiedAt != null)
            obj.put("modifiedAt", modifiedAt.getTime());
        obj.put("revision", revision);
        return obj;
    }


    // ----------------------------------------------------------------------
    // equals method

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Event)) { return false; }

        Event lhs = this;
        Event rhs = (Event) obj;

        if (!ObjectUtils.equals(lhs.id, rhs.id)) { return false; }
        if (!ObjectUtils.equals(lhs.title, rhs.title)) { return false; }
        if (!ObjectUtils.equals(lhs.summary, rhs.summary)) { return false; }
        if (!ObjectUtils.equals(lhs.category, rhs.category)) { return false; }
        if (!ObjectUtils.equals(lhs.beginDate, rhs.beginDate)) { return false; }
        if (!ObjectUtils.equals(lhs.endDate, rhs.endDate)) { return false; }
        if (!ObjectUtils.equals(lhs.url, rhs.url)) { return false; }
        if (!ObjectUtils.equals(lhs.place, rhs.place)) { return false; }
        if (!ObjectUtils.equals(lhs.address, rhs.address)) { return false; }
        if (!ObjectUtils.equals(lhs.description, rhs.description)) { return false; }
        if (!ObjectUtils.equals(lhs.hashTag, rhs.hashTag)) { return false; }
        if (!ObjectUtils.equals(lhs.ownerId, rhs.ownerId)) { return false; }
        if (!ObjectUtils.equals(lhs.managerScreenNames, rhs.managerScreenNames)) { return false; }
        if (!ObjectUtils.equals(lhs.foreImageId, rhs.foreImageId)) { return false; }
        if (!ObjectUtils.equals(lhs.backImageId, rhs.backImageId)) { return false; }
        if (!ObjectUtils.equals(lhs.passcode, rhs.passcode)) { return false; }
        if (!ObjectUtils.equals(lhs.draft, rhs.draft)) { return false; }
        if (!ObjectUtils.equals(lhs.eventRelations, rhs.eventRelations)) { return false; }
        if (!ObjectUtils.equals(lhs.enquetes, rhs.enquetes)) { return false; }
        if (!ObjectUtils.equals(lhs.createdAt, rhs.createdAt)) { return false; }
        if (!ObjectUtils.equals(lhs.modifiedAt, rhs.modifiedAt)) { return false; }
        if (!ObjectUtils.equals(lhs.revision, rhs.revision)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;

        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(title);
        code = code * 37 + ObjectUtils.hashCode(summary);
        code = code * 37 + ObjectUtils.hashCode(category);
        code = code * 37 + ObjectUtils.hashCode(beginDate);
        code = code * 37 + ObjectUtils.hashCode(endDate);
        code = code * 37 + ObjectUtils.hashCode(url);
        code = code * 37 + ObjectUtils.hashCode(place);
        code = code * 37 + ObjectUtils.hashCode(address);
        code = code * 37 + ObjectUtils.hashCode(description);
        code = code * 37 + ObjectUtils.hashCode(hashTag);
        code = code * 37 + ObjectUtils.hashCode(ownerId);
        code = code * 37 + ObjectUtils.hashCode(managerScreenNames);
        code = code * 37 + ObjectUtils.hashCode(foreImageId);
        code = code * 37 + ObjectUtils.hashCode(backImageId);
        code = code * 37 + ObjectUtils.hashCode(passcode);
        code = code * 37 + ObjectUtils.hashCode(draft);
        code = code * 37 + ObjectUtils.hashCode(eventRelations);
        code = code * 37 + ObjectUtils.hashCode(enquetes);
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
        checkToUpdateStatus();
        this.foreImageId = foreImageId;
    }

    public void setBackImageId(String backImageId) {
        checkToUpdateStatus();
        this.backImageId = backImageId;
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

    public DateTime getBeginDate() {
        return beginDate;
    }

    public DateTime getEndDate() {
        return endDate;
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

    public String getPasscode() {
        return passcode;
    }

    public boolean isDraft() {
        return draft;
    }

    public List<EventRelation> getRelations() {
        return eventRelations;
    }

    public List<EnqueteQuestion> getEnquetes() {
        return enquetes;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getModifiedAt() {
        return modifiedAt;
    }

    public int getRevision() {
        return revision;
    }

    // ----------------------------------------------------------------------

    public void setId(String id) {
        checkToUpdateStatus();
        this.id = id;
    }

    public void setTitle(String title) {
        checkToUpdateStatus();
        this.title = title;
    }

    public void setSummary(String summary) {
        checkToUpdateStatus();
        this.summary = summary;
    }

    public void setCategory(String category) {
        checkToUpdateStatus();
        this.category = category;
    }

    public void setBeginDate(DateTime beginDate) {
        checkToUpdateStatus();
        this.beginDate = beginDate;
    }

    public void setEndDate(DateTime endDate) {
        checkToUpdateStatus();
        this.endDate = endDate;
    }

    public void setUrl(String url) {
        checkToUpdateStatus();
        this.url = url;
    }

    public void setPlace(String place) {
        checkToUpdateStatus();
        this.place = place;
    }

    public void setAddress(String address) {
        checkToUpdateStatus();
        this.address = address;
    }

    public void setDescription(String description) {
        checkToUpdateStatus();
        this.description = description;
    }

    public void setHashTag(String hashTag) {
        checkToUpdateStatus();
        this.hashTag = hashTag;
    }

    public void setOwnerId(String ownerId) {
        checkToUpdateStatus();
        this.ownerId = ownerId;
    }

    public void setManagerScreenNames(String managerScreenNames) {
        checkToUpdateStatus();
        this.managerScreenNames = managerScreenNames;
    }

    public void setPasscode(String passcode) {
        checkToUpdateStatus();
        this.passcode = passcode;
    }

    public void setDraft(boolean draft) {
        checkToUpdateStatus();
        this.draft = draft;
    }

    public void setRelations(List<EventRelation> relations) {
        checkToUpdateStatus();
        this.eventRelations = relations;
    }

    public void setEnquetes(List<EnqueteQuestion> enquetes) {
        checkToUpdateStatus();
        this.enquetes = enquetes;
    }

    public void setCreatedAt(DateTime createdAt) {
        checkToUpdateStatus();
        this.createdAt = createdAt;
    }

    public void setModifiedAt(DateTime modifiedAt) {
        checkToUpdateStatus();
        this.modifiedAt = modifiedAt;
    }

    public void setRevision(int revision) {
        checkToUpdateStatus();
        this.revision = revision;
    }

    // ----------------------------------------------------------------------

    public boolean isSearchable() {
        if (isDraft())
            return false;
        if (!StringUtils.isBlank(passcode))
            return false;

        return true;
    }

    public DateTime getDeadlineOfAllTickets(List<EventTicket> tickets) {
        DateTime dt = null;
        for (EventTicket ticket : tickets) {
            DateTime t = ticket.acceptsFrom(this);
            if (t == null)
                continue;
            else if (dt == null || t.isBefore(dt))
                dt = t;
        }

        if (dt != null)
            return dt;

        return new DateTime(beginDate.getTime() - 1000 * 3600 * 3);
    }

    public String getEventURL() {
        String topPath = PartakeProperties.get().getTopPath();
        String thispageURL = topPath + "/events/" + getId();
        return thispageURL;
    }

    private void checkToUpdateStatus() {
        checkFrozen();
        ++revision;
    }

    public boolean isPrivate() {
        return !StringUtils.isBlank(getPasscode());
    }

    public boolean isManager(String name) {
        if (StringUtils.isBlank(name)) { return false; }
        if (StringUtils.isBlank(getManagerScreenNames())) { return false; }

        String[] screenNames = getManagerScreenNames().split(",");
        for (String screenName : screenNames) {
            if (name.equals(StringUtils.trim(screenName))) {
                return true;
            }
        }

        return false;
    }

    public boolean hasEndDate() {
        return getEndDate() != null;
    }
}


