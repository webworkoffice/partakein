package in.partake.model.dto;

import in.partake.model.dto.auxiliary.AttendanceStatus;
import in.partake.model.dto.auxiliary.ModificationStatus;
import in.partake.model.dto.auxiliary.ParticipationStatus;

import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ObjectUtils;

public class Enrollment extends PartakeModel<Enrollment> {
    private String id;
    private String userId;
    private String eventId;
    private String comment;
    private boolean vip;
    private ParticipationStatus status;
    private ModificationStatus modificationStatus;
    private AttendanceStatus attendanceStatus;
    private Date modifiedAt; // TODO: should be renamed to enrolledAt.

    // ----------------------------------------------------------------------
    // constructors

    public Enrollment() {
        this.vip = false;
    }

    public Enrollment(String id, String userId, String eventId, String comment,
            ParticipationStatus status, boolean vip, ModificationStatus modificationStatus,
            AttendanceStatus attendanceStatus, Date modifiedAt) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.comment = comment;
        this.status = status;
        this.vip = vip;
        this.modificationStatus = modificationStatus;
        this.attendanceStatus = attendanceStatus;
        this.modifiedAt = modifiedAt;
    }

    public Enrollment(Enrollment p) {
        this.id = p.id;
        this.userId = p.userId;
        this.eventId = p.eventId;
        this.comment = p.comment;
        this.status = p.status;
        this.vip = p.vip;
        this.modificationStatus = p.modificationStatus;
        this.attendanceStatus = p.attendanceStatus;
        this.modifiedAt = p.modifiedAt == null ? null : (Date) p.modifiedAt.clone();
    }

    public Enrollment(JSONObject obj) {
        this.id = obj.getString("id");
        this.userId = obj.getString("userId");
        this.eventId = obj.getString("eventId");
        this.comment = obj.getString("comment");
        this.vip = obj.getBoolean("vip");
        this.status = ParticipationStatus.safeValueOf(obj.getString("status"));
        this.modificationStatus = ModificationStatus.safeValueOf(obj.getString("modificationStatus"));
        this.attendanceStatus = AttendanceStatus.safeValueOf(obj.getString("attendanceStatus"));
        if (obj.containsKey("modifiedAt"))
            this.modifiedAt = new Date(obj.getLong("modifiedAt"));
    }

    @Override
    public Object getPrimaryKey() {
        return id;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("userId", userId);
        obj.put("eventId", eventId);
        obj.put("comment", comment);
        obj.put("vip", vip);
        obj.put("status", status.toString());
        obj.put("modificationStatus", modificationStatus.toString());
        obj.put("attendanceStatus", attendanceStatus.toString());
        if (modifiedAt != null)
            obj.put("modifiedAt", modifiedAt.getTime());
        return obj;
    }

    // ----------------------------------------------------------------------
    // equals method

    public boolean equals(Object obj) {
        if (!(obj instanceof Enrollment)) { return false; }

        Enrollment lhs = this;
        Enrollment rhs = (Enrollment) obj;

        if (!ObjectUtils.equals(lhs.id,                 rhs.id))                 { return false; }
        if (!ObjectUtils.equals(lhs.userId,             rhs.userId))             { return false; }
        if (!ObjectUtils.equals(lhs.eventId,            rhs.eventId))            { return false; }
        if (!ObjectUtils.equals(lhs.comment,            rhs.comment))            { return false; }
        if (!ObjectUtils.equals(lhs.vip,                rhs.vip))                { return false; }
        if (!ObjectUtils.equals(lhs.status,             rhs.status))             { return false; }
        if (!ObjectUtils.equals(lhs.modificationStatus, rhs.modificationStatus)) { return false; }
        if (!ObjectUtils.equals(lhs.attendanceStatus,   rhs.attendanceStatus))   { return false; }
        if (!ObjectUtils.equals(lhs.modifiedAt,         rhs.modifiedAt))         { return false; }

        return true;
    }

    public int hashCode() {
        int hashCode = 0;

        hashCode = hashCode * 37 + ObjectUtils.hashCode(id);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(userId);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(eventId);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(comment);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(vip);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(status);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(modificationStatus);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(attendanceStatus);
        hashCode = hashCode * 37 + ObjectUtils.hashCode(modifiedAt);

        return hashCode;
    }

    // ----------------------------------------------------------------------
    //

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getComment() {
        return comment;
    }

    public ParticipationStatus getStatus() {
        return status;
    }

    /**
     * 前回チェック時のステータス。ここは、 ENROLLED, NOT_ENROLLED のいずれかでなければならない。
     * 変更時に、この値が ENROLLED -> NOT_ENROLLED もしくあｈ NOT_ENROLLED -> ENROLLED になっていれば、
     * DM によって通知を出す。
     * @return
     */
    public ModificationStatus getModificationStatus() {
        return modificationStatus;
    }

    public AttendanceStatus getAttendanceStatus() {
        return attendanceStatus;
    }

    public boolean isVIP() {
        return vip;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setUserId(String userId) {
        checkFrozen();
        this.userId = userId;
    }

    public void setComment(String comment) {
        checkFrozen();
        this.comment = comment;
    }

    public void setVIP(boolean vip) {
        checkFrozen();
        this.vip = vip;
    }

    public void setStatus(ParticipationStatus status) {
        checkFrozen();
        this.status = status;
    }

    public void setModificationStatus(ModificationStatus lastStatus) {
        checkFrozen();
        this.modificationStatus = lastStatus;
    }

    public void setAttendanceStatus(AttendanceStatus attendanceStatus) {
        checkFrozen();
        this.attendanceStatus = attendanceStatus;
    }

    public void setModifiedAt(Date modifiedAt) {
        checkFrozen();
        this.modifiedAt = modifiedAt;
    }
}
