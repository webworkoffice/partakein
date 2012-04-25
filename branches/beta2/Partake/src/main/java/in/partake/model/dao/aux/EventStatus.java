package in.partake.model.dao.aux;

import in.partake.model.dto.Event;
import net.sf.json.JSONObject;

public class EventStatus {
	public Event event;
	public boolean isBeforeDeadline;
    public int numEnrolledUsers;
    public int numReservedUsers;
    public int numCancelledUsers;

    public EventStatus(Event event, boolean isBeforeDeadline, int numEnrolledUsers, int numReservedUsers, int numCancelledUsers) {
        this.event = event;
        this.isBeforeDeadline = isBeforeDeadline;
        this.numEnrolledUsers = numEnrolledUsers;
        this.numReservedUsers = numReservedUsers;
        this.numCancelledUsers = numCancelledUsers;
    }

    public JSONObject toSafeJSON() {
        JSONObject obj = new JSONObject();
        obj.put("isBeforeDeadline", isBeforeDeadline);
        obj.put("numEnrolledUsers", numEnrolledUsers);
        obj.put("numReservedUsers", numReservedUsers);
        obj.put("numCancelledUsers", numCancelledUsers);
        return obj;
    }
}