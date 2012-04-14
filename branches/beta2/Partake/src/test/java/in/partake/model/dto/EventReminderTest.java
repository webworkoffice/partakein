package in.partake.model.dto;

import in.partake.app.PartakeApp;
import in.partake.model.fixture.TestDataProvider;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

public class EventReminderTest extends AbstractPartakeModelTest<EventReminder> {
    @Override
    protected EventReminder copy(EventReminder t) {
        return new EventReminder(t);
    }

    @Override
    protected TestDataProvider<EventReminder> getTestDataProvider() {
        return PartakeApp.getTestService().getTestDataProviderSet().getEventReminderProvider();
    }

    @Test
    public void gettersExecuteDefensiveCopy() {
        Date date = new Date(0L);
        EventReminder reminder = new EventReminder("id", date, date, date);
        Assert.assertNotSame(date, reminder.getSentDateOfBeforeDeadlineHalfday());
        Assert.assertNotSame(date, reminder.getSentDateOfBeforeDeadlineOneday());
        Assert.assertNotSame(date, reminder.getSentDateOfBeforeTheDay());
        reminder.getSentDateOfBeforeDeadlineHalfday().setTime(1L);
        reminder.getSentDateOfBeforeDeadlineOneday().setTime(1L);
        reminder.getSentDateOfBeforeTheDay().setTime(1L);
        Assert.assertEquals(0L, reminder.getSentDateOfBeforeDeadlineHalfday().getTime());
        Assert.assertEquals(0L, reminder.getSentDateOfBeforeDeadlineOneday().getTime());
        Assert.assertEquals(0L, reminder.getSentDateOfBeforeTheDay().getTime());

        // avoid NullPointerException at defensive copy?
        try {
            EventReminder e = new EventReminder("id", null, null, null);
            e.getSentDateOfBeforeDeadlineHalfday();
            e.getSentDateOfBeforeDeadlineOneday();
            e.getSentDateOfBeforeTheDay();
        } catch (NullPointerException e) {
            Assert.fail("should do null check at defensive copy");
        }
    }

    @Test
    public void settersExecuteDefensiveCopy() {
        Date date = new Date(0L);
        EventReminder reminder = new EventReminder("id", date, date, date);
        reminder.setSentDateOfBeforeDeadlineHalfday(date);
        reminder.setSentDateOfBeforeDeadlineOneday(date);
        reminder.setSentDateOfBeforeTheDay(date);
        date.setTime(1L);
        Assert.assertEquals(0L, reminder.getSentDateOfBeforeDeadlineHalfday().getTime());
        Assert.assertEquals(0L, reminder.getSentDateOfBeforeDeadlineOneday().getTime());
        Assert.assertEquals(0L, reminder.getSentDateOfBeforeTheDay().getTime());

        // avoid NullPointerException at defensive copy?
        try {
            reminder.setSentDateOfBeforeDeadlineHalfday(null);
            reminder.setSentDateOfBeforeDeadlineOneday(null);
            reminder.setSentDateOfBeforeTheDay(null);
        } catch (NullPointerException e) {
            Assert.fail("should do null check at defensive copy");
        }
    }

    @Test
    public void constructorExecutesDefensiveCopy() {
        Date date = new Date(0L);
        EventReminder reminder = new EventReminder("id", date, date, date);
        date.setTime(1L);
        Assert.assertEquals(0L, reminder.getSentDateOfBeforeDeadlineHalfday().getTime());
        Assert.assertEquals(0L, reminder.getSentDateOfBeforeDeadlineOneday().getTime());
        Assert.assertEquals(0L, reminder.getSentDateOfBeforeTheDay().getTime());

        // avoid NullPointerException at defensive copy?
        try {
            new EventReminder("id", null, null, null);
        } catch (NullPointerException e) {
            Assert.fail("should do null check at defensive copy");
        }
    }
}
