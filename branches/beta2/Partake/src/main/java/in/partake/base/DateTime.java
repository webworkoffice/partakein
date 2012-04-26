package in.partake.base;

import java.util.Date;

// Since java.util.Date is mutable, We prefer to use DateTime instead.
public final class DateTime implements Comparable<DateTime> {
    private long millis;

    public DateTime(long millis) {
        this.millis = millis;
    }

    public long getTime() {
        return millis;
    }

    public Date toDate() {
        return new Date(millis);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DateTime))
            return false;

        DateTime lhs = this;
        DateTime rhs = (DateTime) obj;

        return lhs.millis == rhs.millis;
    }

    @Override
    public int hashCode() {
        return (int) millis;
    }

    @Override
    public int compareTo(DateTime rhs) {
        DateTime lhs = this;
        if (lhs.millis < rhs.millis)
            return -1;
        else if (lhs.millis == rhs.millis)
            return 0;
        else
            return 1;
    }

    public boolean isBefore(DateTime dt) {
        return millis < dt.millis;
    }

    public boolean isAfter(DateTime dt) {
        return dt.millis < millis;
    }

    public DateTime nDayBefore(int n) {
        return new DateTime(getTime() - 1000L * 3600 * 24 * n);
    }

    public DateTime nDayAfter(int n) {
        return new DateTime(getTime() + 1000L * 3600 * 24 * n);
    }

    public DateTime nHourBefore(int n) {
        return new DateTime(getTime() - 1000L * 3600 * n);
    }

    public DateTime nHourAfter(int n) {
        return new DateTime(getTime() + 1000L * 3600 * n);
    }

}
