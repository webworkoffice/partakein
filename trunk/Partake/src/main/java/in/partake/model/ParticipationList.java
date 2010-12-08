package in.partake.model;

import in.partake.model.dto.Participation;

import java.util.List;

public class ParticipationList {
    private List<ParticipationEx> enrolledParticipations;
    private List<ParticipationEx> spareParticipations;
    private List<ParticipationEx> cancelledParticipations;
    
    public ParticipationList(List<ParticipationEx> enrolledParticipations, List<ParticipationEx> spareParticipations, List<ParticipationEx> cancelledParticipations) {
        this.enrolledParticipations = enrolledParticipations;
        this.spareParticipations = spareParticipations;
        this.cancelledParticipations = cancelledParticipations;
    }

    public List<ParticipationEx> getEnrolledParticipations() {
        return enrolledParticipations;
    }

    public List<ParticipationEx> getSpareParticipations() {
        return spareParticipations;
    }

    public List<ParticipationEx> getCancelledParticipations() {
        return cancelledParticipations;
    }

    
}
