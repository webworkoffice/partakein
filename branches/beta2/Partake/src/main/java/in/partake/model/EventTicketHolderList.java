package in.partake.model;

import java.util.List;

public class EventTicketHolderList {
    private List<UserTicketApplicationEx> enrolledParticipations;
    private List<UserTicketApplicationEx> spareParticipations;
    private List<UserTicketApplicationEx> cancelledParticipations;
    /** 参加者のうち、仮参加者の人数 */
    private int reservedEnrolled;
    /** 補欠のうち、仮参加者の人数 */
    private int reservedSpare;

    public EventTicketHolderList(
            List<UserTicketApplicationEx> enrolledParticipations, List<UserTicketApplicationEx> spareParticipations, List<UserTicketApplicationEx> cancelledParticipations, int reservedEnrolled, int reservedSpare) {
        this.enrolledParticipations = enrolledParticipations;
        this.spareParticipations = spareParticipations;
        this.cancelledParticipations = cancelledParticipations;
        this.reservedEnrolled = reservedEnrolled;
        this.reservedSpare = reservedSpare;
    }

    public List<UserTicketApplicationEx> getEnrolledParticipations() {
        return enrolledParticipations;
    }

    public List<UserTicketApplicationEx> getSpareParticipations() {
        return spareParticipations;
    }

    public List<UserTicketApplicationEx> getCancelledParticipations() {
        return cancelledParticipations;
    }

    public int getReservedEnrolled() {
        return reservedEnrolled;
    }

    public int getReservedSpare() {
        return reservedSpare;
    }
}
