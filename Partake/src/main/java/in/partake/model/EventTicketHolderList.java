package in.partake.model;

import java.util.List;

public class EventTicketHolderList {
    private List<EnrollmentEx> enrolledParticipations;
    private List<EnrollmentEx> spareParticipations;
    private List<EnrollmentEx> cancelledParticipations;
    /** 参加者のうち、仮参加者の人数 */
    private int reservedEnrolled;
    /** 補欠のうち、仮参加者の人数 */
    private int reservedSpare;

    public EventTicketHolderList(
            List<EnrollmentEx> enrolledParticipations, List<EnrollmentEx> spareParticipations, List<EnrollmentEx> cancelledParticipations, int reservedEnrolled, int reservedSpare) {
        this.enrolledParticipations = enrolledParticipations;
        this.spareParticipations = spareParticipations;
        this.cancelledParticipations = cancelledParticipations;
        this.reservedEnrolled = reservedEnrolled;
        this.reservedSpare = reservedSpare;
    }

    public List<EnrollmentEx> getEnrolledParticipations() {
        return enrolledParticipations;
    }

    public List<EnrollmentEx> getSpareParticipations() {
        return spareParticipations;
    }

    public List<EnrollmentEx> getCancelledParticipations() {
        return cancelledParticipations;
    }

    public int getReservedEnrolled() {
        return reservedEnrolled;
    }

    public int getReservedSpare() {
        return reservedSpare;
    }
}
