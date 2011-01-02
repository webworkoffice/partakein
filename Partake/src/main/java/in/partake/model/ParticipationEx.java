package in.partake.model;

import in.partake.model.dto.Participation;

/**
 * participation with related data.
 * @author shinyak
 *
 */
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
