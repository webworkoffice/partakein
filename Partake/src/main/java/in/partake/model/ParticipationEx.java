package in.partake.model;

import in.partake.model.dto.Participation;

public class ParticipationEx extends Participation {

    private UserEx user; 
    
    public ParticipationEx(Participation p, UserEx user) {
        super(p);
        this.user = user;
    }
    
    public UserEx getUser() {
        return this.user;
    }
}
