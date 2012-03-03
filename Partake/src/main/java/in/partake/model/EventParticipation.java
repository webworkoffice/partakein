package in.partake.model;

import net.sf.json.JSONObject;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.ParticipationStatus;

public class EventParticipation {
    private Event event;
    private int numEnrolledUsers;
    private ParticipationStatus status;
    
    public EventParticipation(Event event, int numEnrolledUsers, ParticipationStatus status) {
        this.event = event;
        this.numEnrolledUsers = numEnrolledUsers;
        this.status = status;
    }
    
    public JSONObject toSafeJSON() {
        JSONObject obj = new JSONObject();
        obj.put("event", event.toSafeJSON());
        obj.put("numEnrolledUsers", numEnrolledUsers);
        obj.put("status", status.toString());
        return obj;
    }
}
