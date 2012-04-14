package in.partake.base;

import java.util.Date;

// Since java.util.Date is mutable, We prefer to use DateTime instead.
public class DateTime {
    private long millis;

    public DateTime(long millis) {
        this.millis = millis;
    }

    public long getTime() {
        return millis;
    }

    public Date getDate() {
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

    public boolean isBefore(DateTime dt) {
        return millis < dt.millis;
    }


}
