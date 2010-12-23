package in.partake.model;

import java.util.List;

public class ParticipationList {
    private List<ParticipationEx> enrolledParticipations;
    private List<ParticipationEx> spareParticipations;
    private List<ParticipationEx> cancelledParticipations;
    /** 参加者のうち、仮参加者の人数 */
    private int reservedEnrolled;
    /** 補欠のうち、仮参加者の人数 */
    private int reservedSpare;

    public ParticipationList(List<ParticipationEx> enrolledParticipations, List<ParticipationEx> spareParticipations, List<ParticipationEx> cancelledParticipations, int reservedEnrolled, int reservedSpare) {
        this.enrolledParticipations = enrolledParticipations;
        this.spareParticipations = spareParticipations;
        this.cancelledParticipations = cancelledParticipations;
        this.reservedEnrolled = reservedEnrolled;
        this.reservedSpare = reservedSpare;
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

    public int getReservedEnrolled() {
        return reservedEnrolled;
    }

    public int getReservedSpare() {
        return reservedSpare;
    }
}
