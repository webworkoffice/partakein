package in.partake.model.dto.auxiliary;

public enum NotificationType {
    UNKNOWN_TYPE,
    EVENT_ONEDAY_BEFORE_REMINDER,
    ONE_DAY_BEFORE_REMINDER_FOR_RESERVATION,
    HALF_DAY_BEFORE_REMINDER_FOR_RESERVATION;

    public static NotificationType safeValueOf(String value) {
        if ("eventOnedayBeforeReminder".equals(value))
            return EVENT_ONEDAY_BEFORE_REMINDER;
        if ("oneDayBeforeReminderForReservation".equals(value))
            return ONE_DAY_BEFORE_REMINDER_FOR_RESERVATION;
        if ("halfDayBeforeReminderForReservation".equals(value))
            return HALF_DAY_BEFORE_REMINDER_FOR_RESERVATION;

        return UNKNOWN_TYPE;
    }
}
