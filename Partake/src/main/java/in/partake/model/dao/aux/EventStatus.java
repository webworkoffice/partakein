package in.partake.model.dao.aux;

import net.sf.json.JSONObject;
import in.partake.base.TimeUtil;
import in.partake.model.dto.Event;

public class EventStatus {
    public Event event;
    public int numEnrolledUsers;
    public int numReservedUsers;
    public int numCancelledUsers;
    
    public EventStatus(Event event, int numEnrolledUsers, int numReservedUsers, int numCancelledUsers) {
        this.event = event;
        this.numEnrolledUsers = numEnrolledUsers;
        this.numReservedUsers = numReservedUsers;
        this.numCancelledUsers = numCancelledUsers;
    }

    public JSONObject toSafeJSON() {
        JSONObject obj = new JSONObject();
        obj.put("event", event.toSafeJSON());
        obj.put("isBeforeDeadline", event.getCalculatedDeadline().before(TimeUtil.getCurrentDate()));
        obj.put("numEnrolledUsers", numEnrolledUsers);
        obj.put("numReservedUsers", numReservedUsers);
        obj.put("numCancelledUsers", numCancelledUsers);
        return obj;
    }
}